import csv
import json
import read_db as db
import pymysql
import random
db = pymysql.connect('localhost','root','justbemyself1998','time')

cursor = db.cursor()
cursor.execute("select  * from appinfo")

appinfo = cursor.fetchall()
cursor.execute("delete from rank_install")
cursor.execute("delete from rank_minutes")
apps=[]
l = len(appinfo)
for i in range(l):
    tmp = appinfo[i]
    apps.append([tmp[0],tmp[1],tmp[2],tmp[3],tmp[4],tmp[5],tmp[6]])

def update_rank():
    apps.sort(key = lambda x:x[3],reverse=True)
    i= 0
    while(i < 50):
        tmp = apps[i]
        packagename = tmp[0]
        appname = tmp[1]
        if(appname[0] != '?'):
            weight = tmp[2]
            installnum = tmp[3]
            category = tmp[4]
            minutes = tmp[5]
            image = tmp[6]
            sql = "insert into rank_install(packagename,appname,weight,installnum,category,image,minutes) values('%s','%s',%i,%i,%i,'%s',%i)" % (packagename,appname,weight,installnum,category,image,minutes)
            try:
                cursor.execute(sql)
                db.commit()
            except:
                print(sql)
                db.rollback()
    apps.sort(key = lambda x:x[5],reverse=True)
    for i in range(50):
        tmp = apps[i]
        packagename = tmp[0]
        appname = tmp[1]
        if(appname[0] != '?'):
            weight = tmp[2]
            installnum = tmp[3]
            category = tmp[4]
            minutes = tmp[5]
            image = tmp[6]
            sql = "insert into rank_minutes(packagename,appname,weight,installnum,category,image,minutes) values('%s','%s',%i,%i,%i,'%s',%i)" % (packagename,appname,weight,installnum,category,image,minutes)
            try:
                cursor.execute(sql)
                db.commit()
            except:
                print(sql)
                db.rollback()

update_rank()