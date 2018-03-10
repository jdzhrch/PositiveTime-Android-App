from random import Random
import random
import pymysql
import csv
db = pymysql.connect('localhost','root','justbemyself1998','time')
cursor = db.cursor()
cursor.execute("select * from users")
users = cursor.fetchall()
cursor.execute("select * from appinfo")
appinfo = cursor.fetchall()

apps = []
emails = []
category_dic={}
name_dic={}
for i in range(500):
    emails.append(users[i][0])
for j in range(50000):
    apps.append(appinfo[j][0])
i=0
with open('data.csv') as f:
    f_csv = csv.reader(f)
    for row in f_csv:
        i = i + 1
        if(i%2 == 1):
            category_dic[row[0]] = row[1]
i=0
with open('name.csv',encoding="utf-8") as f:
    f_csv = csv.reader(f)
    for row in f_csv:
        i = i + 1
        if(i%2 == 1):
            name_dic[row[0]] = row[1]

pack_array = category_dic.keys()

def random_str(randomlength=8):
    str = ''
    chars = 'AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz0123456789'
    length = len(chars) - 1
    random = Random()
    for i in range(randomlength):
        str+=chars[random.randint(0, length)]
    return str

def insert_user(i):
    username = random_str()
    password = random_str()
    email = str(i) + "@qq.com"
    sql = "insert into users(email,username,password,status,classification) values('%s','%s','%s','%d','%d')" % (email,username,password,0,-1)
    try:
        cursor.execute(sql)
        db.commit()
    except:
        db.rollback()

def insert_appinfo():
    for i in category_dic.keys():
        packagename = i
        appname = name_dic[i]
        category = int(category_dic[i])
        weight = random.randint(0,100)
        installnum = random.randint(10,1000)
        image = ""
        sql = "insert into appinfo(packagename,appname,weight,installnum,category,image,minutes) values('%s','%s',%i,%i,%i,'%s',%i)" % (packagename,appname,weight,installnum,category,image,random.randint(10,50))
        try:
            cursor.execute(sql)
            db.commit()
        except:
            db.rollback()
def insert_rank_install():
    for i in range(500):
        packagename = apps[i]
        appname = name_dic[packagename]
        category = int(category_dic[packagename])
        weight = random.randint(0,100)
        installnum = random.randint(10,1000)
        image = ""
        sql = "insert into rank_install(packagename,appname,weight,installnum,category,image,minutes) values('%s','%s',%i,%i,%i,'%s',%i)" % (packagename,appname,weight,installnum,category,image,random.randint(10,50))
        try:
            cursor.execute(sql)
            db.commit()
        except:
            db.rollback()

def insert_rank_minutes():
    for i in range(500):
        packagename = apps[i]
        appname = name_dic[packagename]
        category = int(category_dic[packagename])
        weight = random.randint(0,100)
        installnum = random.randint(10,1000)
        image = ""
        sql = "insert into rank_minutes(packagename,appname,weight,installnum,category,image,minutes) values('%s','%s',%i,%i,%i,'%s',%i)" % (packagename,appname,weight,installnum,category,image,random.randint(10,50))
        try:
            cursor.execute(sql)
            db.commit()
        except:
            db.rollback()

def insert_weight():
    user_start = 0
    while(user_start < 500):
        app_start = random.randint(1, 5)
        while(app_start<50000):
            w = random.randint(0,100)
            sql = "insert into weight(email,packagename,weight) values('%s','%s','%i')" % (emails[user_start],apps[app_start],w)
            app_start += random.randint(300,1000)
            try:
                cursor.execute(sql)
                db.commit()
            except:
                print(sql)
                db.rollback()
        user_start += 1



def insert_record():
    email_start = 0
    day = '2017-7-14'
    while(email_start < 500):
        app_start = random.randint(0,100)
        while(app_start < 50000):
            f = random.randint(0,100)
            d = random.randint(0,50)
            sql = "insert into record(email,packagename,day,frequency,duration) values('%s','%s','%s','%i','%i')" %(emails[email_start],apps[app_start],day,f,d)
            app_start += random.randint(300,1000)
            try:
                cursor.execute(sql)
                db.commit()
            except:
                print(sql)
                db.rollback()
        email_start += 1

insert_rank_install()
insert_rank_minutes()
#insert_appinfo()

#insert_weight()

#insert_record()

db.close()