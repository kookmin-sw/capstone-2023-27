from datetime import datetime, timedelta

import pymysql
import sqlalchemy.dialects.mysql
from sqlalchemy import create_engine
import pandas as pd
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
        df.to_sql(name='company_info_table', con=self.engine, if_exists='append', index=False)


    def tmp_fuc(self):
        # self.engine.execute("update time_table set day_date = '{now}' limit 1".format(now=datetime.now()- timedelta(days=3)))

        df = pd.read_sql("select * from stock_price", self.engine)
        df.to_csv("csvFile/test.csv")
        # def tmp_fuc():
        #     print(level_df["date"])


if __name__ == "__main__":
    a= UploadMysql()
    a.tmp_fuc()
    # a.upload_news()
    # a.upload_search_noun()
    # a.upload_news()
    # a.upload_price()
    # a.upload_sector()
    # a.upload_thema()
    # a.upload_date()
    # a.upload_company_info()