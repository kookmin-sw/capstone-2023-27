# Package 설치!
# pip install dtw-python
# pip install tslearn

'''
output:
- trend_df: 추세 (label별)
- similarity_df: 추세와 비슷한 종목 정보
'''

import warnings
warnings.filterwarnings('ignore')
import pandas as pd
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


def data_preprocessing(data, recent_days):
    # 1년전 데이터에서 상승률 8% 이상이었던 종목 추출
    rise_data = data.loc[(data['rate']>=8)]
    day_before_year = datetime.date.today() - timedelta(days=365)
    rise_data = rise_data[rise_data['date'].dt.date>=day_before_year]

    # 추출한 [종목, 날짜] 데이터의 날짜별로 N일 전부터 날짜까지의 정보 추출
    latest_data = pd.DataFrame(columns=['ticker', 'date', 'end_price', 'rate', 'company_name', 'idx'])
    idx = 0
    for row in tqdm(rise_data.itertuples()):
        ticker, date = row[1], row[2]
        temp_df = data.loc[(data['ticker']==ticker)] 
        temp_df.set_index('date', inplace=True)
        temp_df = temp_df.iloc[:temp_df.index.get_loc(date)].tail(recent_days)
        temp_df.reset_index(inplace=True)
        temp_df['idx'] = [idx]*len(temp_df)
        if len(temp_df) == recent_days:
            latest_data = latest_data.append(temp_df)
            idx += 1

    # end_price, rate 정규화
    normalized = latest_data.groupby('idx').transform(lambda x: (x / x.max()))
    normalized.columns = ['end_price_n', 'rate_n']
    latest_data = pd.concat([latest_data, normalized], axis=1)

    return latest_data


# [[index, ticker, dates, prices, company_name], ...] 형태의 리스트 생성
def get_prices_list(latest_data):
    prices_list = []
    
    for i in range(max(latest_data['idx']+1)):
        ticker = latest_data[latest_data['idx']==i]['ticker'].unique().tolist()
        if not ticker:
            continue
        ticker = ticker[0]
        dates = latest_data[latest_data['idx']==i]['date'].tolist()
        prices = latest_data[latest_data['idx']==i]['end_price_n'].tolist()
        company_name = latest_data[latest_data['idx']==i]['company_name'].unique().tolist()[0]
        prices_list.append([i, ticker, dates, prices, company_name])
        
    return prices_list


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
        prices_sum = [0]*len(X_train[0])
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
        prices_sum = [0]*len(prices_list[0][3])
        prices = []
        for j in range(len(prices_list)):
            if pred[j] == i:
                prices_sum = [x+y for x, y in zip(prices_sum, prices_list[j][3])]
                prices.append(prices_list[j][3])
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
            idx, ticker, dates, prices, company_name = prices_list[j]
            if pred[j] != i:
                continue
            similarity = dtw.dtw(prices_avg, prices, keep_internals=True).distance
            similarity_dic[i].append([ticker, max(dates), similarity, company_name])
            
    return similarity_dic


if __name__=="__main__":
    data = pd.read_csv('stockPrice.csv', index_col=0)
    data['date'] = pd.to_datetime(data['date'])
    
    recent_days = 30
    num_labels = 7
    metric = 'dtw'
    
    latest_data = data_preprocessing(data, recent_days)
    prices_list = get_prices_list(latest_data)
    pred = clustering(prices_list, metric, num_labels)
    prices_avg_list, prices_softdtw_list = get_average_chart_by_label(prices_list, pred, num_labels)
    similarity_dic = calculate_similarity_by_label(prices_list, prices_softdtw_list, pred, num_labels)
    
     '''
    label별 추세 데이터프레임 생성
    trend_df --> [end_price_n, date, period, label]
    - end_price_n: 정규화된 추세의 종가
    - period: 최근 몇일의 데이터를 가져왔는지
    - label: 분류 라벨 (몇 번째 그룹에 속하는가)
    '''
    trend_df = pd.DataFrame({'end_price_n': np.array(prices_softdtw_list).flatten(),
                         'period': [recent_days]*recent_days*num_labels,
                         'label': np.array([[x]*recent_days for x in range(num_labels)]).flatten()})
    
    '''
    추세 비슷한 종목 데이터프레임 생성
    similarity_df --> [label, ticker, date, similarity, company_name, ranking, period]
    '''
    similarity_list = []
    for label, items in similarity_dic.items():
        print(items)
        for ticker, date, similarity, company_name in items:
            similarity_list.append([label, ticker, date, similarity, company_name])
    similarity_df = pd.DataFrame(similarity_list, columns=['label', 'ticker', 'date', 'similarity', 'company_name'])
    similarity_df["ranking"] = similarity_df.groupby("label")["similarity"].rank(method="min",ascending=True)
    similarity_df['period'] = [recent_days]*len(similarity_df)
    similarity_df = similarity_df.sort_values(by=['label', 'ranking']).groupby('label').head(5)
    similarity_df['ticker'] = similarity_df['ticker'].apply(lambda x: '0'+str(x) if len(str(x)) == 5 else str(x))
    
    trend_df.to_csv('trend_df.csv', index=False)
    similarity_df.to_csv('similarity_df.csv', index=False)

