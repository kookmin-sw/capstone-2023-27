
from konlpy.tag import Mecab
mecab = Mecab()
title = "한글과컴퓨터"
noun_list = mecab.nouns(title)
print(noun_list)