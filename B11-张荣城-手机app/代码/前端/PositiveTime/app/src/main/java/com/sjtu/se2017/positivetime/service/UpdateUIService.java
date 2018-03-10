package com.sjtu.se2017.positivetime.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;
import com.sjtu.se2017.positivetime.R;
import com.sjtu.se2017.positivetime.dao.ATDao;
import com.sjtu.se2017.positivetime.dao.AppInfoDao;
import com.sjtu.se2017.positivetime.model.Statistics.AppInformation;
import com.sjtu.se2017.positivetime.model.Statistics.StatisticsInfo;
import com.sjtu.se2017.positivetime.model.Uploadinfo;
import com.sjtu.se2017.positivetime.model.application.ATapplicaion;
import com.sjtu.se2017.positivetime.model.application.Constants;
import com.sjtu.se2017.positivetime.view.activity.MainActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by bonjour on 17-7-5.
 * 此service实现了broadcastreceiver，用于保存at
 */

public class UpdateUIService extends Service implements Constants {

    private Handler handler = new Handler();
    private Timer timer;
    public ArrayList<Uploadinfo> Uploadlist;
    public long AT;
    ATDao atDao;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (timer == null) {
            timer = new Timer();
            timer.scheduleAtFixedRate(new RefreshTask(), 0L, (long)TIME_SPAN);
        }
        initTimePrompt();
        //int result = super.onStartCommand(intent, flags, startId);
        //return result;
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Service被终止的同时也停止定时器继续运行
        timer.cancel();
        timer = null;;
    }


    class RefreshTask extends TimerTask {


        @Override
        public void run() {

            handler.post(new Runnable() {
                @Override
                public void run() {

                    if(MainActivity.getInstance()!=null){MainActivity.getInstance().Update();}
                }
            });
            Intent intent = new Intent(UpdateUIService.this, WatchDogService.class);
            if(ATapplicaion.getInstance().getAT() < 0){
                startService(intent);
            }else{
                stopService(intent);
            }
        }
    }

    private void initTimePrompt() {//用于注册broadcast receiver
        IntentFilter timeFilter = new IntentFilter();
        timeFilter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(mTimeReceiver, timeFilter);
    }
    public void Upload(){

        int style;
        ArrayList<AppInformation> Tmplist;
        String email,label;
        ArrayList<Uploadinfo> list;
        AppInfoDao appInfoDao = new AppInfoDao(this);
        ATDao atDao = new ATDao(this);

        /*Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, -1 * 24);
        SimpleDateFormat yes = new SimpleDateFormat("yyyy-MM-dd");
        String yesterday = yes.format(calendar.getTime());*/
        //System.out.println(yesterday+"");
        android.icu.util.Calendar cal = android.icu.util.Calendar.getInstance();
        int day = cal.get(android.icu.util.Calendar.DAY_OF_YEAR) -1;
        int hour = 23;

        AT = atDao.checkAT(day,hour)/60000;

        StatisticsInfo statisticsInfo = new StatisticsInfo(getApplicationContext(),StatisticsInfo.YESTERDAY);
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

        email = aTapplicaion.getEmail();

        PackageManager pm = getPackageManager();

        Uploadlist = new ArrayList<>();
        for(int i=0;i<size;i++) {
            Uploadinfo uploadlist = new Uploadinfo();
            uploadlist.setEmail(email);
            String p = Tmplist.get(i).getPackageName();
            uploadlist.setPackageName(Tmplist.get(i).getPackageName());
            uploadlist.setDay(dateNowStr);
            try {
                ApplicationInfo applicationInfo = pm.getApplicationInfo(Tmplist.get(i).getPackageName(), 0);
                label = (String)pm.getApplicationLabel(applicationInfo);
                uploadlist.setAppname(label);
                uploadlist.setWeight(appInfoDao.checkweight(label));

            } catch (PackageManager.NameNotFoundException  e) {
                e.printStackTrace();
            }
            uploadlist.setFrequency(Tmplist.get(i).getTimes());
            uploadlist.setDuration((int) Tmplist.get(i).getUsedTimebyDay());


            Uploadlist.add(uploadlist);
            String dedug = dateNowStr + "";
            Log.e("ryze", dedug);
        }
        DownloadTask t= new DownloadTask();
        t.execute();

    }

    private BroadcastReceiver mTimeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Calendar cal = Calendar.getInstance();
            int day = cal.get(Calendar.DAY_OF_YEAR);
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int min = cal.get(Calendar.MINUTE);
            if (min == 0) {
                ATDao atDao = new ATDao(UpdateUIService.this);
                atDao.insertOrUpdate(day,hour,ATapplicaion.getInstance().getAT()/1000);
                Upload();
            }
            if(hour == 23){
                Upload();
            }
        }
    };

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
                jsonstr = jsonstr + "at_yesterday:"+AT;
                //------------字符流写入数据------------
                OutputStream out = urlConnection.getOutputStream();//输出流，用来发送请求，http请求实际上直到这个函数里面才正式发送出去
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));//创建字符流对象并用高效缓冲流包装它，便获得最高的效率,发送的是字符串推荐用字符流，其它数据就用字节流
                bw.write(jsonstr);//把json字符串写入缓冲区中
                bw.flush();//刷新缓冲区，把数据发送出去，这步很重要
                out.close();
                bw.close();//使用完关闭

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
                } else {
                    // connection failure
                    returnStr = "connection failure";
                }
            } catch (Exception e) {
                // exception
            } finally {
                urlConnection.disconnect();//使用完关闭TCP连接，释放资源
            }
            return returnStr;
        }
    }
}
