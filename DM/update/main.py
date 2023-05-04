import pymysql
from sqlalchemy import create_engine
import pandas as pd
import requests
from bs4 import BeautifulSoup
from tqdm import tqdm
from pykrx import stock
from datetime import datetime, timezone, timedelta
from konlpy.tag import Mecab
import re
import time
import config
from transformers import AutoTokenizer, AutoModelForSequenceClassification, pipeline
import torch
import torch.nn as nn

class Update():

    def __init__(self):
        self.engine = create_engine(
            "mysql+pymysql://admin:" + config.db_config["password"] + config.db_config["location"], encoding='utf-8')


    def update_now_price(self):
        def str_day(d):
            return d.strftime('%Y%m%d')

        #  현재 주가 크롤링
        date = str_day(datetime.now())
        df = pd.concat(
            [stock.get_market_ohlcv(date, market="KOSPI"), stock.get_market_ohlcv(date, market="KOSDAQ")])
        df = df.reset_index()[["티커", "시가", "고가", "저가", "종가","거래량","등락률"]]
        df.columns = ["ticker", "start_price", "high_price", "low_price", "end_price","volume", "rate"]
        df.to_sql(name='now_stock_price', con=self.engine, if_exists='replace', index=False)

        # 코스피 코스닥 지수 크롤링
        market_index_list = []
        headers = {"User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/100.0.48496.75"}
        url = 'https://finance.naver.com/sise/sise_index.naver?code=KOSPI'
        html = requests.get(url, headers=headers)
        page = BeautifulSoup(html.text, "html.parser")
        now_value = page.select("#now_value")[0].text
        now_value = re.sub('&nbsp;| |\t|\r|\n', '', now_value).replace(",", "")
        values_rate = page.select("#change_value_and_rate")[0].text
        values_rate = re.sub('%상승', '', values_rate).replace(",", "").split(" ")
        change_value = values_rate[0]
        change_rate = values_rate[1]
        market_index_list.append(["코스피", float(now_value), float(change_value), float(change_rate)])
        url = 'https://finance.naver.com/sise/sise_index.naver?code=KOSDAQ'
        html = requests.get(url, headers=headers)
        page = BeautifulSoup(html.text, "html.parser")
        now_value = page.select("#now_value")[0].text
        now_value = re.sub('&nbsp;| |\t|\r|\n', '', now_value).replace(",", "")
        values_rate = page.select("#change_value_and_rate")[0].text
        values_rate = re.sub('%상승', '', values_rate).replace(",", "").split(" ")
        change_value = values_rate[0]
        change_rate = values_rate[1]
        market_index_list.append(["코스닥", float(now_value), float(change_value), float(change_rate)])
        df = pd.DataFrame(market_index_list, columns=["market", "now_value", "change_value", "change_rate"])
        df.to_sql(name='market_index', con=self.engine, if_exists='replace', index=False)

        self.engine.execute("update time_table set day_minute1= '{now}' limit 1".format(now=datetime.now()))
        print("end now_stock_price")

    # 하루에 한번 장 마감 후 종가,등락률 등등 크롤링해서 전처리
    def update_price(self):

        a = self.engine.execute("select day_date from time_table")
        for i in a:
            start_datetime = i.day_date

        def str_day(d):
            return d.strftime('%Y%m%d')

        def datetime_date(d):
            return datetime.strptime(d, '%Y%m%d')
        # start_date = str_day(start_datetime - timedelta(days=1100))
        start_date = str_day(start_datetime - timedelta(days=1))
        print(start_date)
        end_date = str_day(datetime.now())

        day_list = stock.get_market_ohlcv(start_date, end_date, "005930").index.to_list()
        day_list = list(map(str_day, day_list))
        df = pd.DataFrame(columns=["티커", "시가", "고가", "저가", "종가", "거래량", "거래대금", "등락률", "날짜"])

        for day in (day_list):
            tmp_df = stock.get_market_ohlcv(day, market="KOSPI")
            tmp_df2 = stock.get_market_ohlcv(day, market="KOSDAQ")
            tmp_df = pd.concat([tmp_df, tmp_df2]).reset_index()
            tmp_df["날짜"] = day
            df = pd.concat([df, tmp_df])
        df["등락률"] = round(df["등락률"].astype('float'), 3)
        df = df[["티커", "날짜", "시가", "고가", "저가", "종가", "거래량", "거래대금", "등락률"]]
        tickers = df["티커"].to_list()
        tmp_li = []
        for ticker in (tickers):
            name = stock.get_market_ticker_name(ticker)
            tmp_li.append(name)
        df["company_name"] = tmp_li
        df.columns = ["ticker", "date","start_price","high_price","low_price", "end_price","share_volume","trade_volume", "rate", "company_name"]
        # print(df)
        for day in day_list:
            d_df = df[df["date"]==day]
            dtday= datetime.strptime(day, '%Y%m%d')
            stock_li = []
            lev_li =[]
            level_df = pd.read_sql("select * from dow_table", self.engine)
            for ti in tqdm(d_df['ticker'].unique().tolist()):

                # high_low(S,HH,LH), trend(상승,하락)를 추가해야됨
                add_row = d_df[d_df['ticker'] == ti ].iloc[0].tolist()
                end_price0 = add_row[5]
                try:
                    ticker, end_price1, end_price2, n, ud, date, low, high = level_df[level_df['ticker'] == ti].iloc[0].tolist()
                except:
                    # 해당 티커 level_table에 없을때
                    ticker, end_price1, end_price2, n, ud, date, low, high  = [ti,end_price0,end_price0,0,"상승",None,end_price0,end_price0]


                condition = (end_price0 < end_price1 and end_price1 > end_price2) or (end_price0 > end_price1 and end_price1 < end_price2)


                # high_low 구하기 전날보다 오늘 종가가 높으면 H 낮으면 L
                label = "S"
                trend = ud
                l_date =date
                change_p = end_price0 - end_price1
                if condition:
                    if change_p <= 0 :
                            # 현재 저점이 이전 저저점보다 높을 경우 -> HL
                        if low < end_price0:
                            label = 'HL'
                            # 현재 저점이 이전 저점보다 낮을 경우 -> LL
                        elif low >= end_price0:
                            label = 'LL'

                    if change_p > 0 :
                            # 현재 저점이 이전 저저점보다 높을 경우 -> HL
                        if high < end_price0:
                            label = 'LH'
                            # 현재 저점이 이전 저점보다 낮을 경우 -> LL
                        elif high >= end_price0:
                            label = 'HH'


                    if n== 0:
                        if label == "LL" or label == "HH":
                            n += 1
                            l_date = dtday
                        else:
                            n = 0
                    elif n== 1:
                        n += 1
                    elif n== 2:
                        if ud == "상승":
                            if label == "LH":
                                n += 1
                            else:
                                l_date = None
                                n = 0
                        else:
                            if label == "HL":
                                n += 1
                            else:
                                n = 0
                                l_date = None
                    # 추세 전환시 lv 1의 날짜부터 오늘까지의 추세를 변경하고 lv은 1로 지정
                    elif n == 3:
                        # 상승 -> 하락 추세로 전환
                        if label == 'LL' and ud == '상승':
                            # date ~ 전날까지의 trend를 하락으로 변경
                            self.engine.execute(
                               "update stock_price set trend = '하락' where ticker ='{tik}' & date >='{d}'".format(d=date,tik=ticker))

                            trend = "하락"
                            l_date = dtday
                            n = 1
                        # 하락 -> 상승 추세로 전환
                        elif label == 'HH' and ud == '하락':
                            # date ~ 전날까지의 trend를 상승으로 변경
                            self.engine.execute(
                               "update stock_price set trend = '상승' where ticker ='{tik}' &  date >='{d}'".format(d=date,tik=ticker))
                            trend = "상승"
                            l_date = dtday
                            n = 1
                        else:
                            l_date = None
                            n = 0

                    if condition:
                        if change_p > 0 :
                            high = end_price0
                        else:
                            low = end_price0


                add_row.extend([label,trend])
                level_row = [ticker,end_price0,end_price1,n,trend,l_date,low,high]
                stock_li.append(add_row)
                lev_li.append(level_row)

            stock_df = pd.DataFrame(stock_li,columns=["ticker", "date","start_price","high_price","low_price", "end_price","share_volume","trade_volume", "rate", "company_name","high_low","trend"])
            lev_df = pd.DataFrame(lev_li,columns=['ticker', 'm1_end_price', 'm2_end_price', 'lv', 'trend', "date", "low", "high"])
            stock_df.to_sql(name='stock_price', con=self.engine, if_exists='append', index=False)
            lev_df.to_sql(name='dow_table', con=self.engine, if_exists='replace', index=False)
            self.engine.execute("update time_table set day_date = '{now}' limit 1".format(now=dtday))
        print("end stock_price")
    #끝
    # 뉴스 크롤링
    def update_news(self):

        query1 = self.engine.execute("select day_minute10 from time_table")
        dd = query1.first()[0]
        tic = self.engine.execute("select distinct(ticker) from stock_price;")
        tickers = [ticker[0] for ticker in tic]
        tmp_li = []
        # 이전 업데이트~ 현재까지 새로운 뉴스를 크롤링
        for ticker in (tickers):
            p = 1
            fday = datetime.now()
            end_point = False
            while end_point == False:
                headers = {"User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/100.0.48496.75"}
                url = 'https://finance.naver.com/item/news_news.nhn?code={ticker}&page={page}'.format(ticker=ticker,
                                                                                                      page=p)
                html = requests.get(url, headers=headers)
                page = BeautifulSoup(html.text, "html.parser")
                try:
                    titles = page.select("body > div > table.type5 > tbody > tr > td.title > a")
                    infos = page.select("body > div > table.type5 > tbody > tr > td.info")
                    dates = page.select("body > div > table.type5 > tbody > tr > td.date")
                    result = []
                    for i in range(len(titles)):
                        title = titles[i].text
                        info = infos[i].text
                        date = dates[i].text
                        date2 = datetime.strptime(date, ' %Y.%m.%d %H:%M')

                        if dd >= date2:
                            end_point = True
                            break
                        if fday > date2:
                            fday = date2
                        else:
                            end_point = True
                            break
                        rink = "https://finance.naver.com" + titles[i]['href']
                        result.append([ticker, info, date, rink, title])

                    if result:
                        tmp_li.append(result)
                        p += 1
                    else:
                        end_point = True

                except:
                    print("err")
        res = sum(tmp_li, [])
        df = pd.DataFrame(res, columns=["ticker", "provider", "date", "rink", "title"])

        #새로운 뉴스가 없는 경우 종료
        if df.empty==True:
            print("empty")
            self.engine.execute("update time_table set day_minute10 = '{now}' limit 1".format(now=datetime.now()))
            return

        # 크롤링 해온 뉴스들의 본문 가져오기
        urls = df["rink"].values.tolist()
        t_li = []
        for url in (urls):
            headers = {"User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/100.0.48496.75"}
            html = requests.get(url, headers=headers)
            soup = BeautifulSoup(html.text, "html.parser")
            try:
                # description = soup.select_one('meta[property="og:description"]')['content']
                description = soup.find('div', {'id': 'news_read'})
                try:
                    description.find('div', {'class': 'link_news'}).decompose()
                    description = description.text
                except:
                    description = description.text
            except Exception as e:
                description = ""
                print(1, e, url)
            t_li.append(description)
        df["description"] = t_li

        # 제목과 본문에서 명사를 추출해서 description에 저장, 그 명사들로 새로운 ticker:명사 쌍 테이블 생성
        mecab = Mecab()
        ticker_noun_list = []

        # 명사 추출
        def noun_extraction(title):
            noun_list = mecab.nouns(title)
            # 추출한 명사중 한글자인 것들은 제외 - 제대로된 명사인 경우가 적다
            noun_list = [noun for noun in noun_list if len(noun) != 1]
            return noun_list

        for row in (df.itertuples()):
            text = getattr(row, 'title') + " " + getattr(row, 'description')
            # 이걸 list로 할까 set으로 해서 중복을 없앨까 고민 한 뉴스당 여러번 포함된 키워드 제외할까 고민
            noun_list = list(set(noun_extraction(text)))
            # 영어,숫자 및 공백 제거.
            eng_noun_list = re.sub('[^a-zA-Z]', ' ', text).strip()
            # 다중공백 치환
            eng_noun_list = re.sub(' +', ' ', eng_noun_list).split(" ")
            eng_noun_list = [noun.lower() for noun in eng_noun_list if len(noun) != 1 and len(noun) < 15]
            noun_list = noun_list + list(set(eng_noun_list))
            noun = " ".join(noun_list)
            df.loc[row.Index, "description"] = noun
            # ticker: 명사쌍 추출
            for none in noun_list:
                ticker_noun_list.append([getattr(row, 'ticker'), none])

        noun_df = pd.DataFrame(ticker_noun_list, columns=["ticker", "noun"])
        noun_df["count"] = 1
        # 중복된 ticker : noun 쌍을 카운트해줌
        noun_df = noun_df.groupby(["ticker", "noun"])["count"].sum().reset_index().sort_values("count", ascending=False)
        rm_id = noun_df[noun_df["count"] == 1].index
        noun_df.drop(rm_id, inplace=True)
        # 종목명 추가
        tickers = set(df["ticker"].to_list())
        tmp_list = []
        for ticker in (tickers):
            name = stock.get_market_ticker_name(ticker)
            tmp_list.append([ticker, name])
        df2 = pd.DataFrame(tmp_list, columns=["ticker", "company_name"])
        noun_df = pd.merge(left=noun_df, right=df2, how="inner", on="ticker")

        df = df.drop_duplicates(subset=["ticker", "date"])

        #감정분석
        device = torch.device('cpu')
        tokenizer = AutoTokenizer.from_pretrained("snunlp/KR-FinBert-SC")
        model = AutoModelForSequenceClassification.from_pretrained("snunlp/KR-FinBert-SC")
        # 뉴스 csv 데이터 로드
        titles = list(df['title'])
        outputs_list = []

        # 뉴스 제목 loop돌며 감성분석 수행
        for title in (titles):
            inputs = tokenizer(title, return_tensors='pt').to(device)
            output = model(**inputs)
            output = output.logits.tolist()[0]
            outputs_list.append(output)

        # 소프트맥스 함수로 예측 값 생성
        outputs = torch.tensor(outputs_list)
        predictions = nn.functional.softmax(outputs, dim=-1)

        # 데이터프레임에 column 추가. 각 column은 해당되는 감정의 지수를 나타냄
        df_sc = pd.DataFrame(predictions.detach().numpy())
        df_sc.columns = ['Negative', 'Neutral', 'Positive']
        df = pd.concat([df, df_sc], axis=1)
        df["sentiment"] = ""
        for row in (df.itertuples()):
            li = [getattr(row, 'Negative'), getattr(row, 'Neutral'), getattr(row, 'Positive')]
            if li.index(max(li)) == 0:
                df.loc[row.Index, "sentiment"] = "악재"
            elif li.index(max(li)) == 1:
                df.loc[row.Index, "sentiment"] = "중립"
            else:
                df.loc[row.Index, "sentiment"] = "호재"
        df = df[['ticker', 'provider', 'date', 'rink', 'title','description', 'sentiment']]

        for row in (df.itertuples()):
            title1 = getattr(row, "title")
            title1 = '\\"'.join(title1.split('"'))
            title1 = "\\'".join(title1.split("'"))
            title1 = "%%".join(title1.split("%"))
            title1= str(title1)

            query = "INSERT IGNORE INTO news values(lpad({ticker},'6','0'),'{provider}','{date}','{rink}','{title}','{description}','{sentiment}')".format(
                ticker=getattr(row, "ticker"),provider=getattr(row, "provider"), date=getattr(row, "date"), rink=getattr(row, "rink"), title=title1, description=getattr(row, "description"), sentiment=getattr(row, "sentiment"))
            self.engine.execute(query)

        for row in (noun_df.itertuples()):

            query = "INSERT INTO search_noun VALUES (lpad({ticker},'6','0'), '{noun}', {count}, '{company_name}') ON DUPLICATE KEY UPDATE count = count+{count}".format(
                ticker=(getattr(row, 'ticker')), noun=(getattr(row, 'noun')), count=int(getattr(row, 'count')),
                company_name=(getattr(row, 'company_name')))
            self.engine.execute(query)

        self.engine.execute("update time_table set day_minute10 = '{now}' limit 1".format(now=datetime.now()))
        print("end news")

    # 테마 업데이트
    def update_thema(self):
        # 테마명들 구하기
        urls = []
        headers = {"User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/100.0.48496.75"}
        for pagenum in range(1, 8):
            url = "https://finance.naver.com/sise/theme.nhn?field=name&ordering=asc&page={pagenum}".format(
                pagenum=pagenum)
            resp = requests.get(url, headers=headers)
            soup = BeautifulSoup(resp.content, "html.parser")
            pages = soup.select("#contentarea_left > table.type_1.theme > tr > td > a")
            for p in pages:
                urls.append('https://finance.naver.com' + p['href'])
        thema_company_list = []
        for url in urls:
            resp = requests.get(url, headers=headers)
            # 인코딩을 통해 한글 꺠짐 문제 해결
            soup = BeautifulSoup(resp.content, "html.parser", from_encoding='cp949')
            # 테마명 뽑아오기
            thema_pages = soup.select("#contentarea_left > table > tr > td > div > div > strong.info_title")
            company_pages = soup.select("#contentarea > div > table > tbody > tr > td.name > div.name_area > a")
            for company in company_pages:
                thema_company_list.append([company.text, thema_pages[0].text])

        df = pd.DataFrame(thema_company_list, columns=["company_name", "keyword"])
        df['keyword'] = df['keyword'].str.rstrip(" 관련주")

        tic = self.engine.execute("select distinct(ticker),company_name from stock_price;")
        li = [[ticker[0], ticker[1]] for ticker in tic]
        df2 = pd.DataFrame(li, columns=["ticker", "company_name"])
        df = pd.merge(left=df, right=df2, how="inner", on="company_name")
        df = df[["company_name", "keyword", "ticker"]]
        for row in (df.itertuples()):
            query = "INSERT IGNORE INTO thema values('{company_name}','{keyword}','{ticker}')".format(
                company_name=getattr(row, "company_name"),
                keyword=getattr(row, "keyword"), ticker=getattr(row, "ticker"))
            self.engine.execute(query)
        self.engine.execute("update time_table set day_week = '{now}' limit 1".format(now=datetime.now()))

        print("end thema")

    def update_sectors(self):
        urls = []
        headers = {"User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/100.0.48496.75"}
        url = "https://finance.naver.com/sise/sise_group.naver?type=upjong"
        resp = requests.get(url, headers=headers)
        soup = BeautifulSoup(resp.content, "html.parser")
        pages = soup.select("#contentarea_left > table.type_1 > tr > td > a")
        for p in pages:
            urls.append('https://finance.naver.com' + p['href'])

        sectors_company_list = []
        for url in urls:
            resp = requests.get(url, headers=headers)
            # 인코딩을 통해 한글 꺠짐 문제 해결
            soup = BeautifulSoup(resp.content, "html.parser", from_encoding='cp949')
            # contentarea > div:nth-child(5) > table > tbody > tr:nth-child(1) > td.name > div > a
            # 섹터명 뽑아오기
            sectors_pages = soup.select("#contentarea_left > table > tr >td")
            if sectors_pages[1].text.strip() =="기타":
                continue
            # contentarea_left > table > tbody > tr:nth-child(4) > td:nth-child(1)
            company_pages = soup.select("#contentarea > div > table > tbody > tr > td.name > div.name_area > a")
            for company in company_pages:
                sectors_company_list.append([company.text, sectors_pages[1].text])

        df = pd.DataFrame(sectors_company_list, columns=["company_name", "keyword"])
        df['keyword'] = df['keyword'].str.strip()

        tic = self.engine.execute("select distinct(ticker),company_name from stock_price;")
        li = [[ticker[0], ticker[1]] for ticker in tic]
        df2 = pd.DataFrame(li, columns=["ticker", "company_name"])
        df = pd.merge(left=df, right=df2, how="inner", on="company_name")
        df = df[["company_name", "keyword", "ticker"]]
        for row in (df.itertuples()):
            query = "INSERT IGNORE INTO sector values('{company_name}','{keyword}','{ticker}')".format(
                company_name=getattr(row, "company_name"),
                keyword=getattr(row, "keyword"), ticker=getattr(row, "ticker"))
            self.engine.execute(query)
        self.engine.execute("update time_table set day_week = '{now}' limit 1".format(now=datetime.now()))
        print("end sector")

    # 기업정보 업데이트
    def update_company_info(self):
        tmp_df = pd.read_sql("select distinct(ticker),company_name from stock_price;", self.engine)
        tickers = tmp_df["ticker"].tolist()
        info_list=[]
        market_cap_list=[]
        per_list=[]
        eps_list=[]
        est_per_list=[]
        est_eps_list=[]
        pbr_list=[]
        dvr_list=[]

        headers = {"User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/100.0.48496.75"}
        for ticker in (tickers):
            url = 'https://finance.naver.com/item/main.naver?code={ticker}'.format(ticker=ticker)
            html = requests.get(url, headers=headers)
            page = BeautifulSoup(html.text, "html.parser")
            tmpInfo = ""
            info = page.select('#summary_info > p')
            for i in info:
                tmpInfo = tmpInfo + i.text + " "
            info_list.append(tmpInfo)

            marketCap = page.select("#_market_sum")
            if len(marketCap)!=0:
                marketCap= marketCap[0].text
                marketCap = re.sub('&nbsp;| |\t|\r|\n', '', marketCap).replace(",", "").split("조")
                if len(marketCap)==1:
                    marketCap = marketCap[0]
                else:
                    marketCap = int(marketCap[0]) * 10000 + int(marketCap[1])
                market_cap_list.append(marketCap)
            else:
                market_cap_list.append(0)

            per = page.select("#_per")
            if len(per)!=0:
                per = per[0].text
            else:
                per=0
            per_list.append(per)

            eps = page.select("#_eps")
            if len(eps)!=0:
                eps = eps[0].text.replace(",", "")
            else:
                eps = 0
            eps_list.append(eps)

            estimatePer = page.select("#_cns_per")
            if len(estimatePer)!=0:
                estimatePer=estimatePer[0].text
            else:
                estimatePer=0
            est_per_list.append(estimatePer)

            estimateEps = page.select("#_cns_eps")
            if len(estimateEps) != 0:
                estimateEps = estimateEps[0].text.replace(",", "")
            else:
                estimateEps = 0
            est_eps_list.append(estimateEps)

            pbr = page.select("#_pbr")
            if len(pbr) !=0:
                pbr = pbr[0].text.replace(",", "")
            else:
                pbr = 0
            pbr_list.append(pbr)
            # 배당수익률

            dvr = page.select("#_dvr")
            if len(dvr)!=0:
                dvr = dvr[0].text
            else:
                dvr=0
            dvr_list.append(dvr)

        tmp_df["company_info"] = info_list
        tmp_df["market_cap"] = market_cap_list
        tmp_df["per"] = per_list
        tmp_df["eps"] = eps_list
        tmp_df["est_per"] = est_per_list
        tmp_df["est_eps"] = est_eps_list
        tmp_df["pbr"] = pbr_list
        tmp_df["dvr"] = dvr_list
        tmp_df.to_sql(name='company_info_table', con=self.engine, if_exists='replace', index=False, index_label="ticker")

    # 기업 설명 추가
    def update_company_info2(self):
        tic = self.engine.execute("select distinct(ticker) from stock_price;")
        tickers = [ticker[0] for ticker in tic]
        headers = {"User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/100.0.48496.75"}

        company_info_list = []
        for ticker in (tickers):
            url = 'https://finance.naver.com/item/main.naver?code={ticker}'.format(ticker=ticker)
            html = requests.get(url, headers=headers)
            page = BeautifulSoup(html.text, "html.parser")
            try:
                # 마지막 페이지 구하기
                tmp = ""
                info = page.select('#summary_info > p')
                for i in info:
                    tmp = tmp + i.text + " "
                company_info_list.append([ticker, tmp])
            except:
                continue
        df = pd.DataFrame(company_info_list, columns=["ticker", "company_info"])
        tmp_df = pd.read_sql("select distinct(ticker),company_name from stock_price;", self.engine)
        df = pd.merge(left=df, right=tmp_df, how="inner", on="ticker")

        for row in (df.itertuples()):
            # 따옴표 처리
            info = getattr(row, "company_info")
            info = '\\"'.join(info.split('"'))
            info = "\\'".join(info.split("'"))
            info = "%%".join(info.split("%"))

            info = str(info)

            query = "INSERT IGNORE INTO company_info_table values('{ticker}','{company_name}','{company_info}')".format(
                company_info=info
                , ticker=getattr(row, "ticker"), company_name=getattr(row, "company_name"))
            self.engine.execute(query)
        self.engine.execute("update time_table set day_week = '{now}' limit 1".format(now=datetime.now()))
        print("end sector")



# crontab을 이용해서 주기별로 코드를 실행
# a = Update()
# a.update_now_price()
# 1일 주기
# a.update_price()
# 1시간 주기
# a.update_news()
# 1주일 주기
# a.update_sectors()
# a.update_thema()
# a.update_company_info()
# a.update_company_info2()