from konlpy.tag import Mecab
import pandas as pd
from tqdm import tqdm
from pykrx import stock
import matplotlib.pyplot as plt
import re
import numpy as np
pd.options.display.max_rows = 100
pd.options.display.max_columns = 20


# 제목과 본문에서 명사만 뽑아서 명사 : ticker 쌍 저장
def title_description_change_noun():
    # 읽을때 ticker을 int로 읽으면 맨앞 0이 제거 되서 나온다
    df = pd.read_csv("csvFile/news_description.csv", index_col=0, dtype=str)
    mecab = Mecab()
    ticker_noun_list = []

    # 명사 추출
    def noun_extraction(title):
        noun_list = mecab.nouns(title)
        # 추출한 명사중 한글자인 것들은 제외 - 제대로된 명사인 경우가 적다
        noun_list = [noun for noun in noun_list if len(noun) != 1]
        return noun_list

    for row in tqdm(df.itertuples(), total=df.shape[0]):
        text = getattr(row, 'title') + " " + getattr(row, 'description')
        # 이걸 list로 할까 set으로 해서 중복을 없앨까 고민 한 뉴스당 여러번 포함된 키워드 제외할까 고민
        noun_list = list(set(noun_extraction(text)))
        # 영어,숫자 및 공백 제거.
        eng_noun_list = re.sub('[^a-zA-Z]', ' ', text).strip()
        # 다중공백 치환
        eng_noun_list = re.sub(' +', ' ', eng_noun_list).split(" ")
        eng_noun_list = [noun.lower() for noun in eng_noun_list if len(noun) != 1 and len(noun) < 15]
        noun_list = noun_list + list(set(eng_noun_list))
        for none in noun_list:
            ticker_noun_list.append([getattr(row, 'ticker'), none])

    noun_df = pd.DataFrame(ticker_noun_list, columns=["ticker", "noun"])
    noun_df["count"] = 1
    # 중복된 ticker : noun 쌍을 카운트해줌
    noun_df = noun_df.groupby(["ticker", "noun"])["count"].sum().reset_index().sort_values("count", ascending=False)
    rm_id = noun_df[noun_df["count"] == 1].index
    noun_df.drop(rm_id, inplace=True)

    li = [[ticker[0], ticker[1]] for ticker in ticker_noun_list]
    df2 = pd.DataFrame(li, columns=["ticker", "company_name"])
    df = pd.merge(left=df, right=df2, how="inner", on="company_name")
    df = df[["company_name", "keyword", "ticker"]]

    noun_df.to_csv("csvFile/news_noun.csv", index=False, mode="w")


# 뉴스 본문 명사 추출 및 전처리
def news_description():
    df = pd.read_csv("csvFile/news_description.csv", index_col=0, dtype=str)
    mecab = Mecab()
    ticker_noun_list = []

    # 명사 추출
    def noun_extraction(title):
        noun_list = mecab.nouns(title)
        # 추출한 명사중 한글자인 것들은 제외 - y
        # 제대로된 명사인 경우가 적다
        noun_list = [noun for noun in noun_list if len(noun) != 1]
        return noun_list

    for row in tqdm(df.itertuples()):
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
    df.to_csv("csvFile/news_description2.csv", index=False, mode="w")


# 가장 평균 상승률이 높은 키워드 10개 도출
def best_rise_rate_keyword(day):
    df = stock.get_market_ohlcv(day, market="KOSPI")
    df2 = stock.get_market_ohlcv(day, market="KOSDAQ")
    df = pd.concat([df, df2])["등락률"].reset_index()
    df.columns = ["ticker", "change_rate"]
    dfn = pd.read_csv("csvFile/news_noun.csv", dtype=str)
    dfn = dfn.astype({"count": int})
    # 카운트 횟수가 20개 이상인 것만 포함
    dfn = dfn[dfn["count"] >= 20]
    tmp_df = dfn.groupby("noun")["ticker"].count().sort_values(ascending=False)
    dfn = pd.merge(left=dfn, right=df, how="inner", on="ticker")
    # 연관된 종목이 5개 이상인것만 선택
    tmp = dfn.groupby("noun")["ticker"].count().reset_index()
    li = tmp[tmp["ticker"] >= 5]["noun"].values.tolist()
    df = dfn[dfn["noun"].isin(li)]

    # noun이 이름인 경우를 제거
    rm_li = list(set(df["noun"].tolist()))
    first_name_li = ["김", "이", "박", "최", "정", "강", "조", "윤", "장", "임", "한", "오", "서", "신", "권", "황", "안", "송", "전", "횽",
                     "유"]
    rm_li = [i for i in rm_li if len(i) == 3 and i[0] in first_name_li]
    rm_id = df[df["noun"].isin(rm_li)].index
    df.drop(rm_id, inplace=True)
    df = df.groupby("noun")["change_rate"].mean().sort_values(ascending=False)[0:10].reset_index()
    df["change_rate"] = df["change_rate"].round(3)
    df.to_csv("csvFile/best_rise_rate_keyword.csv", index=False, mode="w")

# 추세 분석 함수, HH, LL, HL, LH 판별 및 상승/하락 판별별
def analyze_trend(tmp_df, ticker):
    tmp_df['변화량'] = tmp_df['end_price'].diff()
    growth = tmp_df['변화량'] > 0
    tmp_df['marker'] = growth.diff().shift(-1)
    # 고점, 저점, 유지 부분을 각각 'H', 'L', 'S' 로 구분
    tmp_df['high_low'] = np.where(tmp_df['marker'] == False, 'S', (np.where(tmp_df['변화량'] > 0, 'H', 'L')))
    tmp_df['trend'] = '유지'
    tmp_df.reset_index(inplace=True)
    tmp_df.drop('index', axis=1, inplace=True)

    prev_low, prev_high = -1, -1
    sequence = []
    rise_seq_list = [['LL', 'LH', 'HL', 'HH'], ['LL', 'HH', 'HL', 'HH']]
    dec_seq_list = [['HH', 'HL', 'LH', 'LL'], ['HH', 'LL', 'LH', 'LL']]
    prev_idx = []

    for idx, row in enumerate(tmp_df.itertuples()):
        # print(row)
        label = ''
        trend = ''
        if row[-2] == 'L':
            current_low = row[5]
            # print(f'Current low: {current_low}, Previous low: {prev_low}')
            # 첫 Low 일 경우, LL로 판정
            if prev_low == -1:
                label = 'LL'
            # 현재 저점이 이전 저저점보다 높을 경우 -> HL
            elif prev_low < current_low:
                label = 'HL'
            # 현재 저점이 이전 저점보다 낮을 경우 -> LL
            elif prev_low >= current_low:
                label = 'LL'
            tmp_df.at[idx, 'high_low'] = label
            prev_low = current_low
            sequence.append(label)
            prev_idx.append(idx)
            # print(f'--> {label}')

        if row[-2] == 'H':
            current_high = row[5]
            # print(f'Current high: {current_high}, Previous high: {prev_high}')
            # 첫 High 일 경우, HH로 판정
            if prev_high == -1:
                label = 'HH'
            # 현재 고점이 이전 고점보다 낮을 경우 -> LH
            elif prev_high > current_high:
                label = 'LH'
            # 현재 고점이 이전 고점보다 높을 경우 -> HH
            elif prev_high <= current_high:
                label = 'HH'
            tmp_df.at[idx, 'high_low'] = label
            prev_high = current_high
            sequence.append(label)
            prev_idx.append(idx)
            # print(f'--> {label}')

        if len(sequence) >= 4 and row[-3] == True:
            # 최근 4개의 상태를 체크
            recent_seq = sequence[-4:]
            if recent_seq in rise_seq_list:
                tmp_df.at[prev_idx[-4], 'trend'] = '상승'
                # print('!!! RISE starts')
                # print(recent_seq)
            elif recent_seq in dec_seq_list:
                tmp_df.at[prev_idx[-4], 'trend'] = '하락'
                # print('!!! DEC starts')
                # print(recent_seq)
    tmp_df.drop('변화량', axis=1,inplace = True)
    return tmp_df,prev_low, prev_high

# 유지 제거 -> '변화'칼럼의 모든 값들을 '유지'대신 '상승' 또는 '하락'으로 변경
def remove_yuji(tmp_df):
    tmp_df.loc[tmp_df["trend"] == "유지","trend"] = None
    tmp_df.fillna(method='ffill',inplace= True)
    # 변화 칼럼이 모두 '유지'인 경우
    if tmp_df['trend'].isnull().sum() == tmp_df.shape[0]:
        return tmp_df
    # 변화 칼럼 중 None이 존재하는 경우
    if tmp_df['trend'].isnull().sum():
        first_not_none_id = tmp_df[tmp_df["trend"].isnull()].index[-1]+1
        if tmp_df.iloc[first_not_none_id,-1] == "상승":
            tmp_df.fillna("하락",inplace= True)
        else:
            tmp_df.fillna("상승",inplace= True)
    tmp_df.iloc[-1,-3] = None
    return tmp_df


# 각 ticker별로 전날까지의 level을 측정정
def get_level_and_trend(tmp_df):
    ticker = tmp_df.iloc[-1, 0]
    end_price1 = tmp_df.iloc[-1, 5]
    end_price2 = tmp_df.iloc[-2, 5]
    li = tmp_df.loc[tmp_df["marker"] == True, "high_low"][-3:].values.tolist()
    ud = tmp_df.iloc[-1, -1]
    date_li = tmp_df.loc[tmp_df["marker"] == True, "date"][-3:].values.tolist()
    date = None
    # Level과 상승/하락 판정
    n = 0
    while len(li) != 0:
        tmp = li.pop(0)
        dat = date_li.pop(0)

        if n== 0:
            if tmp == "LL" or tmp == "HH":
                n += 1
                date = dat
            else:
                n = 0
        elif n== 1:
            n += 1
        elif n== 2:
            if ud == "상승":
                if tmp == "LH":
                    n += 1
                else:
                    date = None
                    n = 0
            else:
                if tmp == "HL":
                    n += 1
                else:
                    n = 0
                    date = None
        elif n== 3:
            if ud == "상승":
                if tmp == "LL":
                    n = 1
                    ud = "하락"
                    date = dat
                else:
                    n = 0
                    date = None
            else:
                if tmp == "HH":
                    n = 1
                    ud = "상승"
                    date = dat
                else:
                    n = 0
                    date = None

    return ticker, end_price1, end_price2, n, ud, date


def first_dow():
    df = pd.read_csv("csvFile/stock.csv", index_col=0)
    columns_li = df.columns.tolist()
    columns_li.extend(["marker","high_low","trend"])
    # Columns: [ticker, date, start_price, high_price, low_price, end_price, share_volume, trade_volume, rate, company_name, marker, high_low, trend]
    result_df = pd.DataFrame(columns=columns_li)
    ticker_list = df['ticker'].unique().tolist()
    level_list = []
    for ticker in tqdm(ticker_list):
        tdf = df[df["ticker"] == ticker].copy()
        tm_df,prev_low, prev_high = analyze_trend(tdf, ticker)
        tm_df = remove_yuji(tm_df.copy())
        result_df = pd.concat([result_df, tm_df])
        try:
            ticker, end_price1, end_price2, n, ud, date = get_level_and_trend(tm_df)
            level_list.append([ticker, end_price1, end_price2, n, ud, date,prev_low, prev_high])
        except:
            continue

    level_df = pd.DataFrame(level_list, columns=['ticker', 'm1_end_price', 'm2_end_price', 'lv', 'trend',"date","low","high"])
    result_df.drop('marker', axis=1,inplace = True)
    result_df.to_csv("csvFile/result_df.csv")
    level_df.to_csv("csvFile/level_df.csv")

if __name__ == "__main__":

    first_dow()

    # title_description_change_noun()
    # news_description()
