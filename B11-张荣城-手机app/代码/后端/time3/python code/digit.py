
from sklearn.cluster import k_means
import numpy as np
import read_db as db
import pymysql
digit_db = pymysql.connect('localhost','root','justbemyself1998','time')
cursor = digit_db.cursor()
cursor.execute("select * from users")
users = cursor.fetchall()
cursor.execute("select * from appinfo")
appinfo = cursor.fetchall()
apps = []
emails = []
for i in range(500):
    emails.append(users[i][0])
for j in range(50000):
    apps.append(appinfo[j][0])
category = {}
for i in range(len(db.appinfo)):
    category[db.appinfo[i][0]] = db.appinfo[i][4]
r = []
for i in range(len(db.record)):
    tmp = db.record[i]
    r.append([tmp[0],tmp[1],tmp[4]])

r_by_category = {}

for i in range(len(emails)):#改到服务器上时，应该改成len(emails)
    for j in range(11):
        r_by_category[emails[i],j] = 0

for i in range(len(db.record)):
    tmp = r[i]
    r_by_category[tmp[0],category[tmp[1]]] += tmp[2]#导入record的信息格式为 (email,category)->time

r_final = {}

for i in range(len(emails)):
    r_final[emails[i]] = [0,0,0,0,0,0,0,0,0,0,0]

for email,c in r_by_category.keys():
    r_final[email][c] += r_by_category[email,c]#格式换成 email->(11 categories 各自的 time)

r_sorted = {}
for i in r_final.keys():
    r_sorted[i] =r_final[i]

matrix = [r_sorted[k] for k in r_sorted]

X = np.array(matrix)
#到时候np.array里就是每个用户一个特征向量，现在具体定为用户每个类别app平均每天使用时长，每天的使用频率用来对这个使用时长做一些调整
#这是最naive的用户feature，我觉得到时候还是把用户详细到每次解锁手机使用app的情况都送到服务器拿来分析

k = k_means(X,n_clusters=10)
c = k[1]
sorted_keys = [i for i in r_sorted.keys()]
classification = {}
for i in range(500):
    classification[sorted_keys[i]] = c[i]
print(classification)

cursor = digit_db.cursor()

for i in classification.keys():
    sql = "update users set classification = %d where email = '%s'" % (classification[i],i)
    try:
        cursor.execute(sql)
        digit_db.commit()
    except:
        print("error")
        digit_db.rollback()

