import re
from datetime import datetime, timedelta

import pymysql
import sqlalchemy.dialects.mysql
from konlpy.tag import Mecab
from sqlalchemy import create_engine
import pandas as pd
from tqdm import tqdm

import config
from time import time


pd.options.display.max_rows = 100
pd.options.display.max_columns = 20


class UploadMysql():

    def __init__(self):
       self.engine = create_engine("mysql+pymysql://admin:" + config.db_config["password"]  + config.db_config["location"] , encoding='utf-8')

    def upload_terms(self):
        df = pd.read_csv("csvFile/terms.csv",dtype=str,usecols=[1,2])
        df.to_sql(name='terms_table', con=self.engine, if_exists='replace', index=False, index_label="terms")

    def upload_search_noun(self):
        df = pd.read_csv("csvFile/news_noun.csv",dtype=str)
        df = df.drop_duplicates(subset = ["ticker","noun"])
        df2 =pd.read_csv("csvFile/company.csv", dtype=str)
        df = pd.merge(left=df, right=df2, how="inner", on="ticker")
        df.to_sql(name='search_noun', con=self.engine, if_exists='append', index=False, index_label="noun")

    def upload_news(self):
        df = pd.read_csv("csvFile/news_description7.csv", dtype=str)
        # df['title'] = df['title'].str.replace('…', '...')
        df = df.drop_duplicates(subset = ["ticker","date"])
        df.to_sql(name='news', con=self.engine, if_exists='append', index=False)

    def upload_price(self):
        df = pd.read_csv("csvFile/result_df.csv",index_col=0,dtype={"ticker":str})
        df2 = pd.read_csv("csvFile/level_df.csv",index_col=0,dtype={"ticker":str})
        df = df.drop_duplicates(subset = ["ticker","date"])
        df2 = df2.drop_duplicates(subset = ["ticker"])

        df.to_sql(name='stock_price', con=self.engine, if_exists='append', index=False)
        df2.to_sql(name='dow_table', con=self.engine, if_exists='append', index=False)

    def upload_sector(self):
        df = pd.read_csv("csvFile/sector.csv", dtype=str,index_col=0)
        df2 =pd.read_csv("csvFile/company.csv", dtype=str)
        df = pd.merge(left=df, right=df2, how="inner", on="company_name")
        df.drop_duplicates(subset=['keyword', 'ticker'], keep='last')

        df.to_sql(name='sector', con=self.engine, if_exists='append', index=False)

    def upload_thema(self):
        df = pd.read_csv("csvFile/thema.csv", dtype=str,index_col=0)
        df2 = pd.read_csv("csvFile/company.csv", dtype=str)
        df = pd.merge(left=df, right=df2, how="inner", on="company_name")
        df = df.drop_duplicates(subset=['keyword', 'ticker'], keep='last')
        df.to_sql(name='thema', con=self.engine, if_exists='append', index=False)

    def upload_date(self):
        query = "insert into time_table values ('2023-01-02 22:38:00','2023-01-11 00:00:00','2023-01-11 00:00:00');"
        self.engine.execute(query)

    def upload_company_info(self):
        df = pd.read_csv("csvFile/company_info.csv", dtype=str, index_col=0)
        df = df.drop_duplicates(subset=['ticker'], keep='last')
        p
        df.to_sql(name='company_info_table', con=self.engine, if_exists='append', index=False)


    def news_change_search_noun(self):
        df = pd.read_csv("csvFile/tttts.csv",dtype=str,index_col=0)
        df["noun"]= df["noun"].str.lower()
        df = df.drop_duplicates(subset=['ticker', 'noun'], keep='last')
        # print(df[(df["ticker"]=="005380") & (df["noun"]=="SK")])
        df.to_sql(name='search_noun', con=self.engine, if_exists='append', index=False)

        # self.engine.execute("update time_table set day_date = '{now}' limit 1".format(now=datetime.now()- timedelta(days=3)))
        # df = pd.read_sql("select * from news", self.engine)
        # company_df = pd.read_sql("select distinct ticker,company_name from stock_price", self.engine)
        #
        # mecab = Mecab()
        # ticker_noun_list = []
        # # 명사 추출
        # def noun_extraction(title):
        #     noun_list = mecab.nouns(title)
        #     # 추출한 명사중 한글자인 것들은 제외 - 제대로된 명사인 경우가 적다
        #     noun_list = [noun for noun in noun_list if len(noun) != 1]
        #     return noun_list
        #
        # for row in tqdm(df.itertuples(), total=df.shape[0]):
        #
        #     text = getattr(row, 'title') + " " + getattr(row, 'description')
        #     # 이걸 list로 할까 set으로 해서 중복을 없앨까 고민 한 뉴스당 여러번 포함된 키워드 제외할까 고민
        #     noun_list = list(set(noun_extraction(text)))
        #     # 영어,숫자 및 공백 제거.
        #     eng_noun_list = re.sub('[^a-zA-Z]', ' ', text).strip()
        #     # 다중공백 치환
        #     eng_noun_list = re.sub(' +', ' ', eng_noun_list).split(" ")
        #     eng_noun_list = [noun.lower() for noun in eng_noun_list if len(noun) != 1 and len(noun) < 15]
        #     noun_list = noun_list + list(set(eng_noun_list))
        #     for none in noun_list:
        #         ticker_noun_list.append([getattr(row, 'ticker'), none])
        #
        # noun_df = pd.DataFrame(ticker_noun_list, columns=["ticker", "noun"])
        # noun_df["count"] = 1
        # # 중복된 ticker : noun 쌍을 카운트해줌
        # noun_df = noun_df.groupby(["ticker", "noun"])["count"].sum().reset_index().sort_values("count", ascending=False)
        # rm_id = noun_df[noun_df["count"] == 1].index
        # noun_df.drop(rm_id, inplace=True)
        # noun_df = pd.merge(left=noun_df, right=company_df, how="inner", on="ticker")
        # noun_df.dropna(axis=0,inplace=True)
        # noun_df = noun_df.drop_duplicates(subset=["noun", "ticker"])
        # noun_df.to_csv("csvFile/tttts.csv")
        # noun_df.to_sql(name='search_noun', con=self.engine, if_exists='append', index=False)


if __name__ == "__main__":
    a= UploadMysql()
    # a.news_change_search_noun()

    a.upload_news()
    a.upload_search_noun()
    a.upload_news()
    a.upload_price()
    a.upload_sector()
    a.upload_thema()
    a.upload_date()
    a.upload_company_info()