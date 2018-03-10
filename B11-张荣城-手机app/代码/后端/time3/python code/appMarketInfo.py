import csv

filename = ['wandoujia_0.tsv','wandoujia_1.tsv','wandoujia_2.tsv','wandoujia_3.tsv','wandoujia_4.tsv','wandoujia_5.tsv']

dic = {}
name=[]
for i in range(6):
    with open(filename[i],encoding="utf-8") as tsvin:
        t = csv.reader(tsvin, delimiter='\t')
        for row in t:
            if(len(row)>6):
                pack = row[2][30:]
                cate = row[6]

                n = row[3]
                dic[pack] = cate
                name.append([pack,n])


result = sorted(dic.items(),key = lambda item:item[1])
data = {}

for i in result:
    data[i[0]] = i[1]

for i in data:
   if(data[i][0] == "N" or data[i][0] == "其"):
       data[i] = 0
   elif(data[i][0] == "体" or data[i][0] == "全" or data[i][0] == "动" or data[i][0] == "塔" or data[i][0] == "宝" or data[i][0] == "扑" or data[i][0] == "经" or data[i][0] == "网" or data[i][0] == "角" or data[i][0] == "跑"):
       data[i] = 1
   elif(data[i][0] == "交" or data[i][0] == "地" or data[i][0] == "旅"):
       data[i] = 2
   elif(data[i][0] == "商" or data[i][0] == "购"):
       data[i] = 3
   elif(data[i][0] == "儿" or data[i][0] == "教" or data[i][0] == "语"):
       data[i] = 4
   elif(data[i][0] == "丽" or data[i][0] == "休"):
       data[i] = 5
   elif(data[i][0] == "新" or data[i][0] == "视" or data[i][0] == "图"):
       data[i] = 6
   elif(data[i][0] == "安" or data[i][0] == "效" or data[i][0] == "生" or  data[i][0] == "电" or data[i][0] == "系" or data[i][0] == "优" or data[i][0] == "美"):
       data[i] = 7
   elif(data[i][0] == "聊"):
       data[i] = 8
   elif(data[i][0] == "运"):
       data[i] = 9
   else:
       data[i] = 10

d = []
for i in data:
    d.append((i,data[i]))
with open('data.csv','w') as f:
   f_csv = csv.writer(f)
   f_csv.writerows(d)
f.close()

with open('name.csv','w',encoding='utf-8') as ff:
    ff_csv = csv.writer(ff)
    ff_csv.writerows(name)