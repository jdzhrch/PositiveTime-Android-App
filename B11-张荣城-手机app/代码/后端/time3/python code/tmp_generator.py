import random
import pymysql
import csv
db = pymysql.connect('localhost','root','justbemyself1998','time')
cursor = db.cursor()
cursor.execute("select * from users")
users = cursor.fetchall()
cursor.execute("select * from appinfo")
appinfo = cursor.fetchall()
name_dic={}
apps = []
emails = []
for i in range(500):
    emails.append(users[i][0])
for j in range(100):
    apps.append(appinfo[j][0])

i=0
with open('name.csv',encoding="utf-8") as f:
    f_csv = csv.reader(f)
    for row in f_csv:
        i = i + 1
        if(i%2 == 1):
            name_dic[row[0]] = row[1]

def insert_time_record():
    app_num = len(apps)
    email_num = len(emails)
    for i in range(email_num):
        for j in range(app_num):
            sql = "insert into time_record(email,packagename,appname,minutes) values(\'{}\',\'{}\',\'{}\',{})".format(emails[i],apps[j],name_dic[apps[j]],random.randint(1,100))
            try:
                cursor.execute(sql)
                db.commit()
            except:
                db.rollback()

insert_time_record()

# def insert_min_and_at():
#     email_num = len(emails)
#     for i in range(email_num):
#         sql = "insert into stat(email,avg_at,avg_min) values(\'{}\',{},{})".format(emails[i],random.randint(1,10),random.randint(1,100))
#         try:
#             cursor.execute(sql)
#             db.commit()
#         except:
#             print(sql)
#             db.rollback()
#
# insert_min_and_at()

