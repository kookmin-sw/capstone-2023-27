# Package 설치!
# pip install dtw-python
# pip install tslearn
'''
1. 8% 이상 상승 종목 추출
2. 주가와 날짜를 기준으로 TimeSeriesKMeans를 사용해 클러스트링하여 부분집합별로 나눠줌
3. 라벨별 평균 차트와 각 종목 차트 간의 dtw 점수 계산
'''
from time import strptime

'''
output:
- trend_df: 추세 (label별)
- latest_df: 추세와 비슷한 종목 정보
'''

import warnings

warnings.filterwarnings('ignore')
import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
import dtw
from tslearn.clustering import TimeSeriesKMeans, KShape
from tslearn.barycenters import softdtw_barycenter
from collections import defaultdict
from datetime import datetime
from dateutil.relativedelta import relativedelta
pd.options.display.max_rows = 100
pd.options.display.max_columns = 10

# 최근 N일 간의 Stock 중 상승률 5% 이상인 데이터를 추출, end_price 정규화도 수행
def data_preprocessing(data, recent_days):
    date = (datetime.today() - relativedelta(years=1)).strftime("%Y-%m-%d")
    up_rate_df = data[(data["date"]>=date) & (data["rate"]>=5)][["ticker","date"]]
    print(up_rate_df)
    # print(up_rate_df.values.tolist())

    # days = recent_days
    #
    # df = data[(data["date"]>=date) & (data["rate"]>=8)]
    #
    # days = recent_days
    # # ticker로 그룹화한 후 최근 days 만큼의 데이터 추출
    # latest_data = data.groupby(['ticker']).tail(days + 1)
    # print(latest_data)
    # # 오늘 상승률이 5% 이상인 종목의 ticker 추출
    # current_rate_data = latest_data.groupby(['ticker']).last()
    # rise_ticker_list = current_rate_data[current_rate_data['rate'] >= 5].index.tolist()
    # # 종목별로 오늘 row를 제거
    # latest_data.drop(latest_data.groupby('ticker').tail(1).index, axis=0, inplace=True)
    # # 고 상승률 종목만 추출
    # latest_data = latest_data[latest_data['ticker'].isin(rise_ticker_list)]
    # # end_price 정규화
    # normalized = latest_data.groupby('ticker').transform(lambda x: (x / x.max()))
    # normalized.columns = ['end_price_n', 'rate_n']
    #
    # latest_data = pd.concat([latest_data, normalized], axis=1)
    # print(latest_data)
    # return latest_data


# [[ticker, dates, prices], ...] 형태의 리스트 생성
def get_prices_list(latest_data):
    prices_list = []
    rise_ticker_list = latest_data['ticker'].unique()

    for ticker in rise_ticker_list:
        dates = latest_data[latest_data['ticker'] == ticker]['date'].tolist()
        prices = latest_data[latest_data['ticker'] == ticker]['end_price_n'].tolist()
        prices_list.append([ticker, dates, prices])

    return prices_list


# 클러스터링 수행
def clustering(prices_list, metric, num_labels):
    model = TimeSeriesKMeans(n_clusters=num_labels, metric=metric,
                             max_iter=1000)

    X_train = [row[2] for row in prices_list]
    pred = model.fit_predict(X_train)

    return pred


# 라벨별로 시각화
def visualize_by_label(prices_list, num_labels):
    figsize = (3, 1)
    X_train = [row[2] for row in prices_list]
    for i in range(num_labels):
        print('Label: ', i)
        prices_sum = [0] * len(X_train[0])
        X = []
        for j in range(len(X_train)):
            if pred[j] == i:
                plt.figure(figsize=figsize)
                plt.plot(X_train[j])
                plt.show()
                prices_sum = [x + y for x, y in zip(prices_sum, X_train[j])]
                X.append(X_train[j])
        print("AVERAGE")
        plt.figure(figsize=figsize)
        plt.plot([x / len(prices_sum) for x in prices_sum], 'r')
        plt.show()
        plt.figure(figsize=figsize)
        plt.plot(softdtw_barycenter(X), 'r')
        plt.show()


# 라벨별 평균 차트 생성
def get_average_chart_by_label(prices_list, pred, num_labels):
    prices_avg_list = []
    prices_softdtw_list = []

    for i in range(num_labels):
        prices_sum = [0] * len(prices_list[0][2])
        prices = []
        for j in range(len(prices_list)):
            if pred[j] == i:
                prices_sum = [x + y for x, y in zip(prices_sum, prices_list[j][2])]
                prices.append(prices_list[j][2])
        prices_avg = [x / len(prices_sum) for x in prices_sum]
        prices_avg_list.append(prices_avg)
        if len(prices) > 0:
            prices_softdtw_list.append(softdtw_barycenter(prices).flatten())

    return prices_avg_list, prices_softdtw_list


# 라벨별 평균 차트와 각 종목 차트 간의 dtw 점수 계산
def calculate_similarity_by_label(prices_list, prices_softdtw_list, pred, num_labels):
    similarity_dic = defaultdict(list)

    for i in range(num_labels):
        prices_avg = prices_softdtw_list[i]
        for j in range(len(prices_list)):
            ticker, dates, prices = prices_list[j]
            if pred[j] != i:
                continue
            similarity = dtw.dtw(prices_avg, prices, keep_internals=True).distance
            similarity_dic[i].append([ticker, similarity])

    return similarity_dic


if __name__ == "__main__":
    data = pd.read_csv('stockPrice.csv', index_col=0)

    recent_days = 30
    num_labels = 7
    metric = 'dtw'

    latest_data = data_preprocessing(data, recent_days)

    # prices_list = get_prices_list(latest_data)
    # dates = [row[1] for row in prices_list][0]
    # pred = clustering(prices_list, metric, num_labels)
    # print(pred)
    # prices_avg_list, prices_softdtw_list = get_average_chart_by_label(prices_list, pred, num_labels)
    # similarity_dic = calculate_similarity_by_label(prices_list, prices_softdtw_list, pred, num_labels)
    # # visualize_by_label(prices_list, num_labels)
    # '''
    # label별 추세 데이터프레임 생성
    # trend_df --> [end_price, date, period, label]
    # - end_price: 정규화된 추세의 종가
    # - date: 날짜
    # - period: 최근 몇일의 데이터를 가져왔는지
    # - label: 분류 라벨 (몇 번째 그룹에 속하는가)
    # '''
    # trend_df = pd.DataFrame({'end_price': np.array(prices_softdtw_list).flatten(),
    #                          'date': dates * num_labels,
    #                          'period': [recent_days] * recent_days * num_labels,
    #                          'label': np.array([[x] * recent_days for x in range(num_labels)]).flatten()})
    # '''
    # 추세 비슷한 종목 데이터프레임 생성
    # latest_df --> [ticker, date, end_price, rate, company_name, end_price_n, rate_n, label, similarity]
    # '''
    # similarity_list = []
    # for label, items in similarity_dic.items():
    #     for ticker, similarity in items:
    #         similarity_list.append([label, ticker, similarity])
    # similarity_df = pd.DataFrame(similarity_list, columns=['label', 'ticker', 'similarity'])
    # similarity_df["ranking"] = similarity_df.groupby("label")["similarity"].rank(method="min",ascending=True)
    # print(similarity_df.sort_values(["label","ranking"],ascending=True))
    # latest_df = pd.merge(latest_data, similarity_df, on='ticker')
    # latest_df['ticker'] = latest_df['ticker'].apply(lambda x: '0' + str(x) if len(str(x)) == 5 else str(x))

    # trend_df.to_csv('trend_df.csv', index=False)
    # latest_df.to_csv('latest_df.csv', index=False)