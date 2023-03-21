# Package 설치!
# pip install dtw-python
# pip install tslearn

'''
output:
- trend_df: 추세 (label별)
- similarity_df: 추세와 비슷한 종목 정보
'''
import pandas as pd
import warnings

from sqlalchemy import create_engine
warnings.filterwarnings('ignore')
import matplotlib.pyplot as plt
import numpy as np
import datetime
from datetime import date, timedelta
from dateutil.relativedelta import relativedelta
from tqdm import tqdm
import dtw
from tslearn.clustering import TimeSeriesKMeans, KShape
from tslearn.barycenters import softdtw_barycenter
from collections import defaultdict
import config
# from datetime import datetime



def get_trend_data(data, recent_days):
    # 1년전 데이터에서 상승률 8% 이상이었던 종목 추출
    rise_data = data.loc[(data['rate'] >= 8)]
    day_before_year = datetime.date.today() - timedelta(days=365)
    rise_data = rise_data[rise_data['date'].dt.date >= day_before_year]

    # 추출한 [종목, 날짜] 데이터의 날짜별로 N일 전부터 날짜까지의 정보 추출
    trend_data = pd.DataFrame(columns=['ticker', 'date', 'end_price', 'rate', 'company_name', 'idx'])
    idx = 0
    for row in tqdm(rise_data.itertuples(), total=rise_data.shape[0]):
        ticker, date = row[1], row[2]
        temp_df = data.loc[(data['ticker'] == ticker)]
        temp_df.set_index('date', inplace=True)
        temp_df = temp_df.iloc[:temp_df.index.get_loc(date)].tail(recent_days)
        temp_df.reset_index(inplace=True)
        temp_df['idx'] = [idx] * len(temp_df)
        if len(temp_df) == recent_days:
            trend_data = trend_data.append(temp_df)
            idx += 1

    # end_price, rate 정규화
    normalized = trend_data.groupby('idx').transform(lambda x: (x / x.max()))
    normalized.columns = ['end_price_n', 'rate_n']
    trend_data = pd.concat([trend_data, normalized], axis=1)

    return trend_data


# 위 함수 속도 개선
def get_trend_data2(data, recent_days):
    data['date'] = pd.to_datetime(data['date'])
    # 1년전 데이터에서 상승률 8% 이상이었던 종목 추출
    rise_data = data.loc[(data['rate'] >= 8)]
    day_before_year = datetime.date.today() - timedelta(days=365)
    rise_data = rise_data[rise_data['date'].dt.date >= day_before_year]
    rise_data_idx = rise_data.index.tolist()
    end_id_list = []
    for idx in tqdm(rise_data_idx):
        ticker = data.iloc[idx, 0]
        # 급등날과 급등날 - recent_days의 ticker가 같으면 급등날을 end_id_list 추가
        if data.iloc[idx - recent_days, 0] == ticker:
            end_id_list.append(idx)
    id_list = [[i for i in range(id - recent_days, id)] for id in end_id_list]
    id_list = sum(id_list, [])
    trend_data = data.iloc[id_list]
    tmp_li = [[i] * recent_days for i in range(int(trend_data.shape[0] / recent_days))]
    trend_data['idx'] = sum(tmp_li, [])
    normalized = trend_data.groupby('idx').transform(lambda x: (x / x.max()))
    normalized.columns = ['end_price_n', 'rate_n']
    trend_data = pd.concat([trend_data, normalized], axis=1)
    return trend_data


# [[index, ticker, dates, prices, company_name], ...] 형태의 리스트 생성
def get_trend_prices_list(latest_data):
    trend_prices_list = []

    for i in range(max(latest_data['idx'] + 1)):
        ticker = latest_data[latest_data['idx'] == i]['ticker'].unique().tolist()
        if not ticker:
            continue
        ticker = ticker[0]
        dates = latest_data[latest_data['idx'] == i]['date'].tolist()
        prices = latest_data[latest_data['idx'] == i]['end_price_n'].tolist()
        company_name = latest_data[latest_data['idx'] == i]['company_name'].unique().tolist()[0]
        trend_prices_list.append([i, ticker, dates, prices, company_name])

    return trend_prices_list


def get_recent_data(data, recent_days):
    recent_data = data.groupby(['ticker']).tail(recent_days)
    # end_price, rate 정규화
    normalized = recent_data.groupby('ticker').transform(lambda x: (x / x.max()))
    normalized.columns = ['end_price_n', 'rate_n']
    recent_data = pd.concat([recent_data, normalized], axis=1)

    return recent_data


def get_recent_prices_list(recent_data):
    recent_prices_list = []
    ticker_list = recent_data['ticker'].unique().tolist()
    idx = 0

    for ticker in ticker_list:
        ticker_data = recent_data[recent_data['ticker'] == ticker]
        dates = ticker_data['date'].tolist()
        prices = ticker_data['end_price_n'].tolist()
        company_name = ticker_data['company_name'].unique()[0]
        rate = ticker_data['rate'].tolist()

        recent_prices_list.append([idx, ticker, dates, prices, company_name,rate])
        idx += 1

    return recent_prices_list


# 클러스터링 수행
def clustering(prices_list, metric, num_labels):
    print('Running Clustering...')
    model = TimeSeriesKMeans(n_clusters=num_labels, metric=metric,
                             max_iter=10)

    X_train = [row[3] for row in prices_list]
    pred = model.fit_predict(X_train)

    return pred


# 라벨별로 시각화
def visualize_by_label(prices_list, num_labels):
    figsize = (2, 1)
    X_train = [row[3] for row in prices_list]
    for i in range(num_labels):
        print('Label: ', i)
        prices_sum = [0] * len(X_train[0])
        X = []
        for j in range(len(X_train)):
            if pred[j] == i:
                # plt.figure(figsize=figsize)
                # plt.plot(X_train[j])
                # plt.show()
                prices_sum = [x + y for x, y in zip(prices_sum, X_train[j])]
                X.append(X_train[j])
        print("AVERAGE")
        plt.figure(figsize=figsize)
        plt.plot([x / len(prices_sum) for x in prices_sum], 'r')
        plt.show()
        plt.figure(figsize=figsize)
        plt.plot(softdtw_barycenter(X), 'r')
        plt.show()


# 라벨별 추세 차트 생성
def get_average_chart_by_label(prices_list, pred, num_labels):
    prices_avg_list = []
    prices_softdtw_list = []

    for i in range(num_labels):
        prices_sum = [0] * len(prices_list[0][3])
        prices = []
        for j in range(len(prices_list)):
            if pred[j] == i:
                prices_sum = [x + y for x, y in zip(prices_sum, prices_list[j][3])]
                prices.append(prices_list[j][3])
        prices_avg = [x / len(prices_sum) for x in prices_sum]
        prices_avg_list.append(prices_avg)
        if len(prices) > 0:
            prices_softdtw_list.append(softdtw_barycenter(prices).flatten())

    return prices_avg_list, prices_softdtw_list


# 라벨별 평균 차트와 각 종목 차트 간의 dtw 점수를 구해서 가장 유사한 label 찾기
def calculate_similarity_by_label(prices_list, prices_softdtw_list, num_labels):
    similarity_list = []
    for j in range(len(prices_list)):
        idx, ticker, dates, prices, company_name,rate_list = prices_list[j]  # data,price는 리스트
        rate = rate_list[-1]
        sim = 100
        lab = 0
        for i in range(num_labels):
            prices_avg = prices_softdtw_list[i]
            similarity = dtw.dtw(prices_avg, prices, keep_internals=True).distance
            if similarity < sim:
                lab = i
                sim = similarity
        similarity_list.append([ticker, max(dates), sim, company_name,rate, lab])

    return similarity_list


# CLUSTERS 수 ELBOW_METHOD를 통해 구하기
# 5: 40 ,30: 60이 적당
def elbow_method(trend_prices_list):
  recent_day_list= [5,30,90]
  for recent_days in recent_day_list:
    trend_data = get_trend_data(data, recent_days)
    trend_prices_list = get_trend_prices_list(trend_data)
    elbow_data = []
    X_train = [row[3] for row in trend_prices_list]
    for n_clusters in range (10,500,10):
        km = TimeSeriesKMeans(n_clusters=n_clusters, verbose=False, random_state=42,n_jobs=-1)
        y_pred = km.fit_predict(X_train)
        # km.inertia_ 군집의 CENTER에 얼마나 잘 뭉쳤는지 알 수 있다
        elbow_data.append((n_clusters, km.inertia_))
    pd.DataFrame(elbow_data,columns=['clusters', 'distance']).plot(x='clusters',y='distance')


# period별로 어떤 label이 가장 많이 올랐는지 구하기
def find_best_label(data,df):
    best_label_df = pd.DataFrame(columns=["period","label","rate_mean","count"])
    num_labels = 50

    period_list = [5, 15, 30]

    date_list=  pd.read_csv("csvFile/stockPrice.csv", index_col=0).astype("str").values.tolist()
    date_list = sum(date_list,[])
    # 하나의 pred마다 5일 정도
    test_day = [i for i in range(2,7)]

    for period in period_list:
        tr_df = df[df["period"] == period]
        prices_softdtw_list2 = tr_df["end_price"].values.reshape(num_labels, period)
        test_data = data[data["date"]>=date_list[-(test_day[-1]+period)]]

        test_prices_list = []

        for d in test_day:
            start_day = date_list[-(d + period)]
            end_day = date_list[-d]
            tmp_df = test_data[test_data["date"].between(start_day, end_day)]
            normalized = tmp_df.groupby('ticker').transform(lambda x: (x / x.max()))
            normalized.columns = ['end_price_n', 'rate_n']
            tmp_df = pd.concat([tmp_df, normalized], axis=1)
            ticker_list = tmp_df['ticker'].unique().tolist()
            idx = 0

            for ticker in ticker_list:
                ticker_data = tmp_df[tmp_df['ticker'] == ticker]
                dates = ticker_data['date'].tolist()
                prices = ticker_data['end_price_n'].tolist()
                company_name = ticker_data['company_name'].unique()[0]
                rate = ticker_data['rate'].tolist()
                test_prices_list.append([idx, ticker, dates, prices, company_name,rate])
                idx += 1

        similarity_list = calculate_similarity_by_label(test_prices_list, prices_softdtw_list2, num_labels)
        similarity_df = pd.DataFrame(similarity_list, columns=['ticker', 'date', 'similarity', 'company_name', "rate",'label'])
        similarity_df = similarity_df.groupby("label")["rate"].agg(['mean', 'count'])
        similarity_df["period"] = period
        similarity_df.reset_index(inplace=True)
        similarity_df = similarity_df[["period", "label", "mean", "count"]]
        similarity_df = similarity_df.rename(columns={"mean": "rate_mean"})
        best_label_df = pd.concat([best_label_df, similarity_df], axis=0)
    return  best_label_df
    # best_label_df.to_csv("best_label_df.csv",mode="w")

# 추세그래프 저장
def upload_trend_chart():
    data = pd.read_csv("csvFile/stockPrice.csv", index_col=0)
    num_labels = 50
    metric = 'dtw'
    recent_days_list = [5, 15, 30]
    df = pd.DataFrame(columns=["end_price", "period", "label"])
    for recent_days in (recent_days_list):
        # (1)추세 추출을 위한 데이터
        trend_data = get_trend_data2(data, recent_days)
        trend_prices_list = get_trend_prices_list(trend_data)
        pred = clustering(trend_prices_list, metric, num_labels)  # 22분

        prices_avg_list, prices_softdtw_list = get_average_chart_by_label(trend_prices_list, pred, num_labels)

        trend_df = pd.DataFrame({'end_price': np.array(prices_softdtw_list).flatten(),
                                 'period': [recent_days] * recent_days * num_labels,
                                 'label': np.array([[x] * recent_days for x in range(num_labels)]).flatten()})
        df = pd.concat([df, trend_df])
    df["end_price"] = round(df["end_price"],3)
    df.to_csv("csvFile/trend_chart.csv",mode="w")

# period 별로 rate 평균이 높은 label 저장
def upload_period_best_label():
    data = pd.read_csv("csvFile/stockPrice.csv", index_col=0)
    df = pd.read_csv("csvFile/trend_chart.csv", index_col=0)
    best_label_df = find_best_label(data,df)
    best_label_df = best_label_df[best_label_df["count"] > 10]
    best_label_df["ranking"] = best_label_df.groupby("period")["rate_mean"].rank(method="max", ascending=False)
    best_label_df = best_label_df.sort_values(by=['period', 'ranking']).groupby('period').head(3)

    best_label_df.to_csv("csvFile/period_best_label.csv",mode="w")


#  위에서 뽑은 period:label 쌍의 포함되는 오늘 종목 중 가장 유사도가 높은 종목 저장
def upload_trend_stock():

    best_label_df = pd.read_csv("csvFile/period_best_label.csv", index_col=0)
    df = pd.read_csv("csvFile/trend_chart.csv", index_col=0)
    data = pd.read_csv("csvFile/stockPrice.csv", index_col=0)
    num_labels =50

    period_list = [5, 15, 30]
    period_best_label_dic = {}
    for period  in period_list:
        period_best_label_dic[period]= best_label_df[best_label_df["period"]==period]["label"].values.tolist()

    recent_df = pd.DataFrame(columns=["ticker","date","similarity","company_name","label","ranking","period"])
    for period in period_list:
        recent_data =get_recent_data(data, period)
        recent_prices_list = get_recent_prices_list(recent_data)
        p_df = df[df["period"] == period]
        prices_softdtw_list2 = p_df["end_price"].values.reshape(num_labels, period)
        similarity_list = calculate_similarity_by_label(recent_prices_list, prices_softdtw_list2, num_labels)
        similarity_df = pd.DataFrame(similarity_list,
                                     columns=['ticker', 'date', 'similarity', 'company_name', "rate", 'label'])

        similarity_df = similarity_df[similarity_df["label"].isin(period_best_label_dic[period])]
        similarity_df["ranking"] = similarity_df.groupby("label")["similarity"].rank(method="min", ascending=True)
        similarity_df["period"] = period
        similarity_df = similarity_df.sort_values(by=['label', 'ranking']).groupby('label').head(5)
        similarity_df.drop('similarity', axis=1, inplace=True)
        similarity_df.drop('rate', axis=1, inplace=True)
        recent_df = pd.concat([recent_df,similarity_df])

    recent_df.to_csv("csvFile/trend_stock.csv",mode="w")

if __name__ == "__main__":
    upload_trend_chart()
    upload_period_best_label()
    upload_trend_stock()