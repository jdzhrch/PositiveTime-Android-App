package com.sjtu.se2017.positivetime.controller;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.Gson;
import com.sjtu.se2017.positivetime.R;
import com.sjtu.se2017.positivetime.dao.ATDao;
import com.sjtu.se2017.positivetime.dao.AppInfoDao;
import com.sjtu.se2017.positivetime.model.Statistics.AppInformation;
import com.sjtu.se2017.positivetime.model.Statistics.StatisticsInfo;
import com.sjtu.se2017.positivetime.model.Uploadinfo;
import com.sjtu.se2017.positivetime.model.application.ATapplicaion;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by bonjour on 17-7-14.
 */

public class Upload extends AppCompatActivity {
    private ArrayList<Uploadinfo> Uploadlist;
    private int style;
    private ArrayList<AppInformation> Tmplist;
    private String email,label;
    private ArrayList<Uploadinfo> list;
    private AppInfoDao appInfoDao = new AppInfoDao(this);
    private static Upload instance;
    private ATDao atDao = new ATDao(this);

    private long AT;
    ATapplicaion aTapplicaion = ATapplicaion.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        doit();
    }

    public static Upload getInstance() {
        if (instance == null) {
            instance = new Upload();
        }
        return instance;
    }

    public void doit(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, -1 * 24);
        SimpleDateFormat yes = new SimpleDateFormat("yyyy-MM-dd");
        String yesterday = yes.format(calendar.getTime());
        System.out.println(yesterday+"");

        android.icu.util.Calendar cal = android.icu.util.Calendar.getInstance();
        int day = cal.get(android.icu.util.Calendar.DAY_OF_YEAR) -1;
        int hour = 23;

        AT = atDao.checkAT(day,hour);

        this.style = StatisticsInfo.YESTERDAY;
        StatisticsInfo statisticsInfo = new StatisticsInfo(getApplicationContext(),this.style);
        Tmplist = statisticsInfo.getShowList();
        int size = Tmplist.size();
        //Calendar calendar = Calendar.getInstance();
        //calendar.
        Date d = new Date();
        //System.out.println(d);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateNowStr = sdf.format(d);
        //System.out.println(dateNowStr);

        ATapplicaion aTapplicaion = ATapplicaion.getInstance();

        //email = aTapplicaion.getEmail();
        Uploadinfo list = new Uploadinfo();

        PackageManager pm = getPackageManager();

        this.Uploadlist = new ArrayList<>();
        for(int i=0;i<size;i++) {
            list.setEmail("100@qq.com");
            String p = Tmplist.get(i).getPackageName();
            System.out.print(p);
            list.setPackageName(Tmplist.get(i).getPackageName());
            list.setDay(dateNowStr);
            try {
                ApplicationInfo applicationInfo = pm.getApplicationInfo(Tmplist.get(i).getPackageName(), 0);
                label = (String)pm.getApplicationLabel(applicationInfo);
                list.setAppname(label);
                list.setWeight(appInfoDao.checkweight(label));

            } catch (PackageManager.NameNotFoundException  e) {
                e.printStackTrace();
            }
            list.setFrequency(Tmplist.get(i).getTimes());
            list.setDuration((int) Tmplist.get(i).getUsedTimebyDay());

            Log.v("test2","2");
            Uploadlist.add(list);
            String dedug = dateNowStr + "";
            Log.e("ryze", dedug);
        }
        DownloadTask t= new DownloadTask();
        t.execute();

    }

    private class DownloadTask extends AsyncTask<String, Object, String> {

        @Override
        protected String doInBackground(String... params) {



            String returnStr = "";
            String urlStr = getResources().getString(R.string.ipAddress)+"/appinfo/insert" ;
            HttpURLConnection urlConnection = null;
            URL url = null;
            try {
                url = new URL(urlStr);
                urlConnection = (HttpURLConnection) url.openConnection();//打开http连接
                urlConnection.setConnectTimeout(3000);//连接的超时时间
                urlConnection.setUseCaches(false);//不使用缓存
                urlConnection.setInstanceFollowRedirects(true);//是成员函数，仅作用于当前函数,设置这个连接是否可以被重定向
                urlConnection.setReadTimeout(3000);//响应的超时时间
                urlConnection.setDoInput(true);//设置这个连接是否可以写入数据
                urlConnection.setDoOutput(true);//设置这个连接是否可以输出数据
                urlConnection.setRequestMethod("POST");//设置请求的方式
                urlConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");//设置消息的类型
                urlConnection.connect();// 连接，从上述至此的配置必须要在connect之前完成，实际上它只是建立了一个与服务器的TCP连接
                String jsonstr;
                Gson gson = new Gson();
                jsonstr = gson.toJson(Uploadlist);

                //------------字符流写入数据------------
                OutputStream out = urlConnection.getOutputStream();//输出流，用来发送请求，http请求实际上直到这个函数里面才正式发送出去
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));//创建字符流对象并用高效缓冲流包装它，便获得最高的效率,发送的是字符串推荐用字符流，其它数据就用字节流
                bw.write(jsonstr);//把json字符串写入缓冲区中
                bw.flush();//刷新缓冲区，把数据发送出去，这步很重要
                out.close();
                bw.close();//使用完关闭
                /*
                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {//得到服务端的返回码是否连接成功
                    InputStream in = urlConnection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    String str = null;
                    StringBuffer buffer = new StringBuffer();
                    while ((str = br.readLine()) != null) {//BufferedReader特有功能，一次读取一行数据
                        buffer.append(str);
                    }
                    in.close();
                    br.close();
                    String t = buffer.toString();
                    JSONArray array = JSONArray.fromObject(t);
                    int size = array.size();
                    Gson gson = new Gson();

                } else {
                    // connection failure
                    returnStr = "connection failure";
                }*/
            } catch (Exception e) {
                // exception
            } finally {
                urlConnection.disconnect();//使用完关闭TCP连接，释放资源
            }
            return returnStr;
        }
    }

}
