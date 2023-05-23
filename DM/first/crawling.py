from tqdm import tqdm
from pykrx import stock
import pandas as pd
import requests
from bs4 import BeautifulSoup
import aiohttp
import asyncio

"""
크롤링을해서 csvFile/ 에 각 데이터들을 저장 
"""


# 국내 주식 모든 종목들의 날짜별 네이버 뉴스 개수 크롤링하기
def crawling_thema():
    # 테마명들 구하기
    urls = []
    headers = {"User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/100.0.48496.75"}
    for pagenum in range(1, 8):
        url = "https://finance.naver.com/sise/theme.nhn?field=name&ordering=asc&page={pagenum}".format(pagenum=pagenum)
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
    df.to_csv("csvFile/thema.csv", mode="w")

#섹터 크롤링하기
def crawling_sectors():

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
        sectors_pages = soup.select("#contentarea_left > table > tr >td ")
        # contentarea_left > table > tbody > tr:nth-child(4) > td:nth-child(1)
        company_pages = soup.select("#contentarea > div > table > tbody > tr > td.name > div.name_area > a")
        for company in company_pages:
            sectors_company_list.append([company.text, sectors_pages[1].text])

    df = pd.DataFrame(sectors_company_list, columns=["company_name", "keyword"])
    df['keyword'] = df['keyword'].str.strip()
    df.to_csv("csvFile/sector.csv", mode="w")


# 기업정보 크롤링하기
def crawling_company_info():
    tickersD = stock.get_market_ticker_list("20221229", market="KOSDAQ")
    tickersP = stock.get_market_ticker_list("20221229", market="KOSPI")
    tickers = list(map(str, tickersD + tickersP))  # [0:10]
    headers = {"User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/100.0.48496.75"}
    company_info_list = []
    for ticker in tqdm(tickers):
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
    df.to_csv("csvFile/company_info.csv", mode="w")
    # 뉴스가 1페이지만 있는 경우 2페이지로 중복된 뉴스들이 나옴 중복 제거 필요
    # 1페이지 1뉴스만 있는 경우 9999페이지로 검색하면 아무 페이지도 나오지 않음


# 티커, 주식명 크롤링
def crawling_ticker_name():
    tickersD = stock.get_market_ticker_list("20221229", market="KOSDAQ")
    tickersP = stock.get_market_ticker_list("20221229", market="KOSPI")
    tickers = list(map(str, tickersD + tickersP))  # [0:30]
    tmp_list = []
    for ticker in tqdm(tickers):
        name = stock.get_market_ticker_name(ticker)
        tmp_list.append([ticker, name])
    df = pd.DataFrame(tmp_list, columns=["ticker", "company_name"])
    df.to_csv("csvFile/company.csv", mode='w', index=False)


# 주가 크롤링
def crawling_stock_price():
    df = pd.DataFrame(columns=["티커", "시가", "고가", "저가", "종가", "거래량", "거래대금", "등락률", "날짜"])
    # 1년 이상을 못가져옴
    today = "20230514"
    d_li = ["20200101","20210101","20220101","20230101",today]
    tmpDays = []

    for i in range(len(d_li)-1):
        ddf = stock.get_market_ohlcv(d_li[i], d_li[i+1], "005930")
        tmpDays.extend(ddf.index.to_list())
    tmpDays = list(set(tmpDays))
    def str_day(d):
        return d.strftime('%Y%m%d')

    days = list(map(str_day, tmpDays))

    for day in tqdm(days):
        tmp_df = stock.get_market_ohlcv(day, market="KOSPI")
        tmp_df2 = stock.get_market_ohlcv(day, market="KOSDAQ")
        tmp_df = pd.concat([tmp_df, tmp_df2]).reset_index()
        tmp_df["날짜"] = day
        df = pd.concat([df, tmp_df])
    df["등락률"] = round(df["등락률"].astype('float'), 3)
    tickers = set(df["티커"].to_list())
    name_li = [[ticker,stock.get_market_ticker_name(ticker)] for ticker in tickers]
    df2 = pd.DataFrame(name_li,columns=["티커","종목명"])
    df = pd.merge(left=df,right=df2,how="inner",on="티커")
    df = df[["티커","날짜", "시가", "고가", "저가", "종가", "거래량", "거래대금", "등락률","종목명"]]
    df.columns = ["ticker", "date","start_price","high_price","low_price", "end_price","share_volume","trade_volume", "rate", "company_name"]
    df.to_csv("csvFile/stock.csv", mode='w', index=False)


# 티커로 검색한 네이버 증권 뉴스 크롤링해오기
def crawling_news_keyword2():
    tickersD = stock.get_market_ticker_list("20221229", market="KOSDAQ")
    tickersP = stock.get_market_ticker_list("20221229", market="KOSPI")
    tickers = list(map(str, tickersD + tickersP))
    end_page_list = []

    for ticker in tqdm(tickers):
        headers = {"User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/100.0.48496.75"}
        url = 'https://finance.naver.com/item/news_news.nhn?code={ticker}&page=9999'.format(ticker=ticker)
        html = requests.get(url, headers=headers)
        page = BeautifulSoup(html.text, "html.parser")
        try:
            # 마지막 페이지 구하기
            end_page = page.select('body > div > table.Nnavi > tr > td.on > a')[0].text
            for page in range(1, int(end_page) + 1):
                end_page_list.append([ticker, page])
        except:
            end_page_list.append([ticker, 1])

    async def main(end_page_list):
        # 한번에 너무 많은 데이터를 전달해 await가 과다하게 걸려 파일 디스크럽트을 전부 사용해 오류가 생겨서 나눠서 asyc
        res = []
        print("start get_news_url")
        # 뉴스들의 url 가져오기
        session_timeout = aiohttp.ClientTimeout(total=None)

        connector = aiohttp.TCPConnector(limit=50, force_close=True)
        async with aiohttp.ClientSession(connector=connector, timeout=session_timeout) as session:
            input_coroutines = [get_news_url(session, page_list) for page_list in end_page_list]
            res = await asyncio.gather(*input_coroutines)
        res = sum(res, [])
        df = pd.DataFrame(res, columns=["ticker", "provider", "date", "rink", "title"])
        df.to_csv("csvFile/news.csv", mode="w")

    async def get_news_url(session, page_list):
        ticker = page_list[0]
        page = page_list[1]
        url = 'https://finance.naver.com/item/news_news.nhn?code={ticker}&page={page}'.format(ticker=ticker, page=page)
        """ 에러 
        aiohttpHTTP에서는 동일한 tcp에서 여러 요청을 처리하는데 서버에서 일정 시간 지나면 서버가 연결을 닫음 
        이게 문제 그래서 연결 유지를 비활성화 해결 x오류 해결
        첫번째 오류 too open many file은 한번에 너무 많은 파일을 전달하여 async 사용하려해서임 그래서 나눠서 async
        두번째 오류 server_disconnect는 tcp 커넥터 제한이 있는거 같아서 50개로 제한걸어서 실행 해결 x
        """
        async with session.get(url, ssl=False, async_timeout=None) as response:
            # 보안 관련 ssl오류가 떠서 체크 안하게 끔함 좋지 못한 방법
            try:
                html = await response.text()
                soup = BeautifulSoup(html, "html.parser")
                # articles = soup.select("div.group_news > ul.list_news > li div.news_area > a")  #
                titles = soup.select("body > div > table.type5 > tbody > tr > td.title > a")
                infos = soup.select("body > div > table.type5 > tbody > tr > td.info")
                dates = soup.select("body > div > table.type5 > tbody > tr > td.date")
                result = []
                for i in range(len(titles)):
                    title = titles[i].text
                    info = infos[i].text
                    date = dates[i].text
                    rink = "https://finance.naver.com" + titles[i]['href']
                    result.append([ticker, info, date, rink, title])
                return result
            except:
                print(url)
                return []

    result = asyncio.run(main(end_page_list))

# 뉴스의 링크로 들어가 본문 가져오기
def crawling_news_main():
    async def main():
        print("start get_news_main")
        df = pd.read_csv("csvFile/news.csv", index_col=0, dtype=str)  # .loc[1000:1100]
        urls = df["rink"].values.tolist()
        # 뉴스들의 url에서 title과 description 크롤링
        # timeout error 해결 위해서 연결 시간 제한 x
        session_timeout = aiohttp.ClientTimeout(total=None)
        connector = aiohttp.TCPConnector(limit=100)
        async with aiohttp.ClientSession(connector=connector, timeout=session_timeout) as session:
            futures = [get_news_title_description(session, url) for url in urls]
            res2 = await asyncio.gather(*futures)
        df["description"] = res2
        df.to_csv("csvFile/news_description.csv", mode="w")

    async def get_news_title_description(session, url):
        try:
            async with session.get(url) as response:
                html = await response.text()
                soup = BeautifulSoup(html, "html.parser")
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
                return [description]
        except:
            return [""]

    result = asyncio.run(main())


# 네이버 뉴스에서 과도한 요청으로 값을 못가져온것들 처리 ['']값 들어간거 다시 크롤링
def err_crawling_news_main():
    async def main():
        print("start get_news_main")
        df = pd.read_csv("csvFile/news_description.csv", dtype=str, index_col=0)
        df2 = df[df["description"] == "['']"]
        urls = df2["rink"].values.tolist()
        # 뉴스들의 url에서 title과 description 크롤링
        # timeout error 해결 위해서 연결 시간 제한 x
        session_timeout = aiohttp.ClientTimeout(total=None)
        connector = aiohttp.TCPConnector(limit=100)
        async with aiohttp.ClientSession(connector=connector, timeout=session_timeout) as session:
            futures = [get_news_title_description(session, url) for url in urls]
            res2 = await asyncio.gather(*futures)
        df.loc[df2.index, "description"] = res2
        df.to_csv("csvFile/news_description.csv", mode="w")

    async def get_news_title_description(session, url):
        try:
            async with session.get(url) as response:
                html = await response.text()
                soup = BeautifulSoup(html, "html.parser")
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
                return [description]
        except:
            print(2)
            return [""]

    result = asyncio.run(main())

if __name__ == "__main__":
    crawling_thema()
    crawling_sectors()
    crawling_company_info()
    crawling_ticker_name()
    crawling_stock_price()
    crawling_news_keyword2()
    crawling_news_main()
    err_crawling_news_main()



