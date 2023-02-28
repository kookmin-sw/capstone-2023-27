from konlpy.tag import Mecab
import pandas as pd
from tqdm import tqdm
from pykrx import stock
import matplotlib.pyplot as plt
import re
pd.options.display.max_rows = 100
pd.options.display.max_columns = 20

def title_description_change_noun():
    #읽을때 ticker을 int로 읽으면 맨앞 0이 제거 되서 나온다
    df = pd.read_csv("csvFile/news_description.csv", index_col=0,dtype=str)
    mecab = Mecab()
    ticker_noun_list = []
    #명사 추출
    def noun_extraction(title):
        noun_list = mecab.nouns(title)
        #추출한 명사중 한글자인 것들은 제외 - 제대로된 명사인 경우가 적다
        noun_list = [noun for noun in noun_list if len(noun)!=1]
        return noun_list

    for row in tqdm(df.itertuples(),total=df.shape[0]):
        text = getattr(row, 'title') +" " + getattr(row, 'description')
        #이걸 list로 할까 set으로 해서 중복을 없앨까 고민 한 뉴스당 여러번 포함된 키워드 제외할까 고민
        noun_list = list(set(noun_extraction(text)))
        # 영어,숫자 및 공백 제거.
        eng_noun_list = re.sub('[^a-zA-Z]',' ', text).strip()
        # 다중공백 치환
        eng_noun_list= re.sub(' +', ' ', eng_noun_list).split(" ")
        eng_noun_list = [noun.lower() for noun in eng_noun_list if len(noun)!=1 and len(noun)<15]
        noun_list = noun_list+list(set(eng_noun_list))
        for none in noun_list:
            ticker_noun_list.append([getattr(row, 'ticker'),none])
        #
        # #이걸 list로 할까 set으로 해서 중복을 없앨까 고민 한 뉴스당 여러번 포함된 키워드 제외할까 고민
        # none_list = list(set(noun_extraction(getattr(row, 'title')) + noun_extraction(getattr(row, 'description'))))
        # for none in none_list:
        #     ticker_noun_list.append([getattr(row, 'ticker'),none])


    noun_df = pd.DataFrame(ticker_noun_list,columns=["ticker","noun"])
    noun_df["count"] = 1
    #중복된 ticker : noun 쌍을 카운트해줌
    noun_df = noun_df.groupby(["ticker", "noun"])["count"].sum().reset_index().sort_values("count",ascending=False)
    rm_id = noun_df[noun_df["count"] ==1].index
    noun_df.drop(rm_id, inplace=True)

    li = [[ticker[0], ticker[1]] for ticker in tic]
    df2 = pd.DataFrame(li, columns=["ticker", "company_name"])
    df = pd.merge(left=df, right=df2, how="inner", on="company_name")
    df = df[["company_name", "keyword", "ticker"]]
    
    
    noun_df.to_csv("csvFile/news_noun.csv",index=False, mode="w")


    # #각각의 명사가 몇개의 종목과 연결되있는지 확인후 과하게 연결되있는건 제거 적정값 찾는게 중요
    # tmp_df = noun_df.groupby("noun")["ticker"].count().sort_values(ascending=False)
    # rm_noun_list = tmp_df[tmp_df > 40].index.tolist()
    # rm_index = noun_df[noun_df["noun"].isin(rm_noun_list)].index
    # noun_df.drop(rm_index, inplace=True)

    #명사와 회사명이 완전이 일치하는 경우를 제거
    # ticker_name_df = pd.read_csv("csvFile/company.csv", dtype=str)
    # noun_df = pd.merge(left=noun_df, right=ticker_name_df, how="inner", on="ticker")
    # rm_index2 = noun_df[noun_df["noun"] == noun_df["company_name"]].index
    # noun_df.drop(rm_index2,inplace=True)
    #명사별로 그룹핑한뒤 count 상위 10개만 남겨둠
    # noun_df = noun_df.astype({'count': 'int'}).sort_values(by="count", ascending=False).groupby("noun").head(10)
    #총35만행 키워드 8만개
    #저장

def news_description():
    df = pd.read_csv("csvFile/news_description.csv", index_col=0,dtype=str)
    mecab = Mecab()
    ticker_noun_list = []
    #명사 추출
    def noun_extraction(title):
        noun_list = mecab.nouns(title)
        #추출한 명사중 한글자인 것들은 제외 - y
        # 제대로된 명사인 경우가 적다
        noun_list = [noun for noun in noun_list if len(noun)!=1]
        return noun_list
    for row in tqdm(df.itertuples()):
        text = getattr(row, 'title') +" " + getattr(row, 'description')
        #이걸 list로 할까 set으로 해서 중복을 없앨까 고민 한 뉴스당 여러번 포함된 키워드 제외할까 고민
        noun_list = list(set(noun_extraction(text)))
        # 영어,숫자 및 공백 제거.
        eng_noun_list = re.sub('[^a-zA-Z]', ' ', text).strip()
        # 다중공백 치환
        eng_noun_list= re.sub(' +', ' ', eng_noun_list).split(" ")
        eng_noun_list = [noun.lower() for noun in eng_noun_list if len(noun)!=1 and len(noun)<15 ]
        noun_list = noun_list+list(set(eng_noun_list))
        noun = " ".join(noun_list)
        df.loc[row.Index,"description"] = noun
    df.to_csv("csvFile/news_description2.csv",index=False, mode="w")

#가장 평균 상승률이 높은 키워드 10개 도출
def best_rise_rate_keyword(day):
    df = stock.get_market_ohlcv(day, market="KOSPI")
    df2 = stock.get_market_ohlcv(day, market="KOSDAQ")
    df = pd.concat([df, df2])["등락률"].reset_index()
    df.columns = ["ticker", "change_rate"]
    dfn = pd.read_csv("csvFile/news_noun.csv", dtype=str)
    dfn = dfn.astype({"count":int})
    # 카운트 횟수가 20개 이상인 것만 포함
    dfn = dfn[dfn["count"] >= 20]
    tmp_df = dfn.groupby("noun")["ticker"].count().sort_values(ascending=False)
    dfn = pd.merge(left=dfn, right=df, how="inner", on="ticker")
    #연관된 종목이 5개 이상인것만 선택
    tmp = dfn.groupby("noun")["ticker"].count().reset_index()
    li = tmp[tmp["ticker"] >= 5 ]["noun"].values.tolist()
    df = dfn[dfn["noun"].isin(li)]

    #noun이 이름인 경우를 제거
    rm_li = list(set(df["noun"].tolist()))
    first_name_li = ["김","이","박","최","정","강","조","윤","장","임","한","오","서","신","권","황","안","송","전","횽","유"]
    rm_li = [i for i in rm_li if len(i) == 3 and i[0] in first_name_li]
    rm_id = df[df["noun"].isin(rm_li)].index
    df.drop(rm_id, inplace=True)
    df = df.groupby("noun")["change_rate"].mean().sort_values(ascending=False)[0:10].reset_index()
    df["change_rate"]= df["change_rate"].round(3)
    df.to_csv("csvFile/best_rise_rate_keyword.csv",index=False, mode="w")
title_description_change_noun()
news_description()
# best_rise_rate_keyword("20220102")

# df = pd.read_csv("csvFile/news_description2.csv", index_col=0,dtype=str)
# # print(df.columns)
# print(max([len(i) for i in df["description"].values.tolist()]))


# print(df.groupby("noun")["ticker"].count().sort_values())
# df = pd.read_csv("csvFile/news_description.csv", dtype=str, index_col=0)
# df2 = df[df["description"] == "['']"]

# print(max([len(i) for i in df["count"].values.tolist()]))
# df = pd.read_csv("csvFile/news_noun.csv", dtype=str, index_col=0)

# search test
# ticker = "005380"
# print(df[df["ticker"] == ticker])
# name= "SK"
# print(df[df["company_name"] == name])
# noun = "이디야"
# print(df[df["noun"] == noun])

#명사당 몇개 중복 테스트
# df = df.groupby("noun")["ticker"].count().sort_values(ascending=False)
# print(df)
# print(df[df < 30])
