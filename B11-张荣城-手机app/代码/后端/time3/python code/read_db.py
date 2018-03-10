import pymysql

db = pymysql.connect('localhost','root','justbemyself1998','time')

cursor = db.cursor()

cursor.execute("select * from users")

users = cursor.fetchall()

cursor.execute("select  * from appinfo")

appinfo = cursor.fetchall()

cursor.execute("select * from record")

record = cursor.fetchall()

cursor.execute("select * from weight")

weight = cursor.fetchall()

cursor.execute("select * from feature")

feature = cursor.fetchall()

cursor.execute("select * from at_record")

at = cursor.fetchall()

cursor.execute("select * from time_record")

min = cursor.fetchall()

cursor.execute("select * from stat")

stat = cursor.fetchall()

