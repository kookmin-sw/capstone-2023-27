import pandas as pd
from tqdm import tqdm
from tqdm import tqdm
from transformers import AutoTokenizer, AutoModelForSequenceClassification, pipeline
import torch
import torch.nn as nn
import pandas as pd
from sqlalchemy import create_engine
import config

#mac m1칩인경우 gpu 사용시 mps 써야함
device = torch.device('mps')
tokenizer = AutoTokenizer.from_pretrained("snunlp/KR-FinBert-SC")
model = AutoModelForSequenceClassification.from_pretrained("snunlp/KR-FinBert-SC").to(device)

#기존 뉴스 df가져와서 sentimental 열 추가하기
engine = create_engine("mysql+pymysql://admin:" + config.db_config["password"]  + config.db_config["location"] , encoding='utf-8')
df = pd.read_sql("select * from news;", engine)
# 뉴스 csv 데이터 로드
titles = list(df['title'])
outputs_list = []

# 뉴스 제목 loop돌며 감성분석 수행
for title in tqdm(titles):
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
for row in tqdm(df.itertuples()):
    li = [getattr(row, 'Negative'),getattr(row, 'Neutral'),getattr(row, 'Positive')]
    if li.index(max(li)) ==0:
        df.loc[row.Index, "sentiment"] = "악재"
    elif li.index(max(li)) ==1:
        df.loc[row.Index, "sentiment"] = "중립"
    else:
        df.loc[row.Index, "sentiment"] = "호재"
df = df[['ticker', 'provider', 'date', 'rink', 'title', 'sentiment']]
# csv 파일로 저장
df.to_csv('csvFile/news_description7.csv', index=False)