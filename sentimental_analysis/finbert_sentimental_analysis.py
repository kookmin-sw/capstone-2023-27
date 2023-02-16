# -*- coding: utf-8 -*-
"""FinBERT_감성분석.ipynb
입력: news.csv
출력: news_with_sent.csv (부정, 중립, 긍정 지수가 포함됨)
"""### Loading FinBERT model"""

from transformers import AutoTokenizer, AutoModelForSequenceClassification, pipeline
from tqdm import tqdm
import torch
import torch.nn as nn
import pandas as pd

# FinBERT 모델 로드 - huggingface 사용
tokenizer = AutoTokenizer.from_pretrained("snunlp/KR-FinBert-SC")
model = AutoModelForSequenceClassification.from_pretrained("snunlp/KR-FinBert-SC").cuda()

device = "cuda:0" if torch.cuda.is_available() else "cpu"

# 뉴스 csv 데이터 로드
news = pd.read_csv('drive/MyDrive/Capstone/data/news.csv')

# 뉴스 제목 추출
titles = list(news['title'])
outputs_list = []

# 뉴스 제목 loop돌며 감성분석 수행
for title in tqdm(titles):
    inputs = tokenizer(title, return_tensors='pt').to(device)
    output = model(**inputs)
    print(output)
    output = output.logits.tolist()[0]
    outputs_list.append(output)

# 소프트맥스 함수로 예측 값 생성
outputs = torch.tensor(outputs_list)
predictions = nn.functional.softmax(outputs, dim=-1)

# 데이터프레임에 column 추가. 각 column은 해당되는 감정의 지수를 나타냄
df_sc = pd.DataFrame(predictions.detach().numpy())
df_sc.columns = ['Negative', 'Neutral', 'Positive']
news = pd.concat([news, df_sc], axis=1)

# csv 파일로 저장
news.to_csv('news_with_sent.csv', index=False)

