package com.sjtu.se2017.positivetime.view.activity;


import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sjtu.se2017.positivetime.R;
import com.sjtu.se2017.positivetime.dao.ATDao;
import com.sjtu.se2017.positivetime.dao.AppInfoDao;
import com.sjtu.se2017.positivetime.model.ContentAdapter;
import com.sjtu.se2017.positivetime.model.ContentModel;
import com.sjtu.se2017.positivetime.model.Statistics.AppInformation;
import com.sjtu.se2017.positivetime.model.Statistics.StatisticsInfo;
import com.sjtu.se2017.positivetime.model.Uploadinfo;
import com.sjtu.se2017.positivetime.model.application.ATapplicaion;
import com.sjtu.se2017.positivetime.service.FloatWindowService;
import com.sjtu.se2017.positivetime.service.UpdateUIService;

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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends FragmentActivity {
    Context context;
    private DrawerLayout drawerLayout;
    private RelativeLayout rightLayout;
    private List<ContentModel> list;
    private ContentAdapter adapter;
    private ListView listView;
    private FragmentManager fm;
    private TextView PView,NView;
    private ImageView user_pic;
    private RelativeLayout Playout,Nlayout;
    private Button PButton,NButton;
    private long ptime,ntime;
    private AppInfoDao appInfoDao = new AppInfoDao(this);
    private static MainActivity instance;
    public ArrayList<Uploadinfo> Uploadlist;
    public long AT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //init parameters
        initData();
        //getActionBar().hide();


        // open floatwindowservice  get floatwindow permission
        startService(new Intent(this, FloatWindowService.class));
        //startService(new Intent(this, WatchDogControlService.class));
        //get usage_stats permission
        try {
            if(!isStatAccessPermissionSet(this)) {
                startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));   //查看是否为应用设置了权限
                Toast toast=Toast.makeText(getApplicationContext(), "此权限用于计算各app使用时间", Toast.LENGTH_SHORT);    //显示toast信息
                toast.show();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        adapter = new ContentAdapter(this, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                switch ((int) id) {
                    case 1:
                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this, MenuActivity.class);
                        intent.putExtra("MenuName","statistics");
                        ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this
                                , Pair.create(view.findViewById(R.id.item_imageview), "share"));
                        startActivity(intent, transitionActivityOptions.toBundle());
                        break;
                    case 2:
                        intent = new Intent(MainActivity.this, MenuActivity.class);
                        intent.putExtra("MenuName","account");
                        transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this
                                , Pair.create(view.findViewById(R.id.item_imageview), "share"));
                        startActivity(intent, transitionActivityOptions.toBundle());
                        break;
                    case 3:
                        intent = new Intent(MainActivity.this, MenuActivity.class);
                        intent.putExtra("MenuName","social");
                        transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this
                                , Pair.create(view.findViewById(R.id.item_imageview), "share"));
                        startActivity(intent, transitionActivityOptions.toBundle());
                        break;
                    case 4:
                        intent = new Intent(MainActivity.this, MenuActivity.class);
                        intent.putExtra("MenuName","settings");
                        transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this
                                , Pair.create(view.findViewById(R.id.item_imageview), "share"));
                        startActivity(intent, transitionActivityOptions.toBundle());
                        break;/*
                    case 1:
                        Intent intent = new Intent(MainActivity.this, AppStatisticsList.class);
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(MainActivity.this, SetWeightActivity.class);
                        startActivity(intent);
                        break;
                    case 3:
                        intent = new Intent(MainActivity.this, FloatingWindow.class);
                        startActivity(intent);
                        break;
                    case 4:
                        intent = new Intent(MainActivity.this, PlanActivity.class);
                        startActivity(intent);
                        break;
                    case 5:
                        intent = new Intent(MainActivity.this, AppActivity.class);
                        startActivity(intent);
                        break;
                    case 6:
                        intent = new Intent(MainActivity.this, UserActivity.class);
                        startActivity(intent);
                        break;
                    case 7:
                        intent = new Intent(MainActivity.this, LineChartActivity.class);
                        startActivity(intent);
                        break;
                    case 8:
                        intent = new Intent(MainActivity.this, PrintActivity.class);
                        startActivity(intent);
                        break;*/
                    default:
                        break;
                }

                drawerLayout.closeDrawer(Gravity.LEFT);
            }
        });
        /*user_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
        rightLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(Gravity.RIGHT);
            }
        });*/
        PButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WorkActivity.class);
                startActivity(intent);
            }
        });
        NButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,RelaxActivity.class);
                startActivity(intent);
            }
        });
        startService(new Intent(this, UpdateUIService.class));
        //Upload();
        Log.v("test","1");

    }

    public void Upload(){

        int style;
        ArrayList<AppInformation> Tmplist;
        String email,label;
        ArrayList<Uploadinfo> list;
        AppInfoDao appInfoDao = new AppInfoDao(this);
        ATDao atDao = new ATDao(this);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, -1 * 24);
        SimpleDateFormat yes = new SimpleDateFormat("yyyy-MM-dd");
        String yesterday = yes.format(calendar.getTime());
        //System.out.println(yesterday+"");

        android.icu.util.Calendar cal = android.icu.util.Calendar.getInstance();
        int day = cal.get(android.icu.util.Calendar.DAY_OF_YEAR) -1;
        int hour = 23;

        AT = atDao.checkAT(day,hour);

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
            uploadlist.setEmail("100@qq.com");
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


    private void initData() {
        context = this;
        list = new ArrayList<ContentModel>();
        list.add(new ContentModel(R.drawable.chart, "", 1));
        list.add(new ContentModel(R.drawable.accounts, "", 2));
        list.add(new ContentModel(R.drawable.socials, "", 3));
        list.add(new ContentModel(R.drawable.setting, "", 4));/*
        list.add(new ContentModel(R.mipmap.doctoradvice2, "查看数据", 1));
        list.add(new ContentModel(R.mipmap.infusion_selected, "设置权重", 2));
        list.add(new ContentModel(R.mipmap.doctoradvice2, "悬浮窗", 3));
        list.add(new ContentModel(R.mipmap.mypatient_selected, "计划", 4));
        list.add(new ContentModel(R.mipmap.mypatient_selected, "app搜索", 5));
        list.add(new ContentModel(R.mipmap.mypatient_selected, "相似用户", 6));
        list.add(new ContentModel(R.mipmap.mypatient_selected, "AT折线图", 7));
        list.add(new ContentModel(R.mipmap.mypatient_selected, "分享", 8));*/

        instance = this;
        PView = (TextView)findViewById(R.id.PView);
        NView = (TextView)findViewById(R.id.NView);
        //user_pic = (ImageView)findViewById(R.id.user_pic);
        PButton = (Button)findViewById(R.id.PButton);
        NButton = (Button)findViewById(R.id.NButton);
        Playout = (RelativeLayout)findViewById(R.id.Playout);
        Nlayout = (RelativeLayout)findViewById(R.id.Nlayout);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        //rightLayout = (RelativeLayout) findViewById(R.id.right);
        listView = (ListView) findViewById(R.id.left_listview);
        fm = getSupportFragmentManager();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private boolean isStatAccessPermissionSet(Context c) throws PackageManager.NameNotFoundException {
        PackageManager pm = c.getPackageManager();
        ApplicationInfo info = pm.getApplicationInfo(c.getPackageName(),0);
        AppOpsManager aom = (AppOpsManager) c.getSystemService(Context.APP_OPS_SERVICE);
        aom.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,info.uid,info.packageName);
        return aom.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,info.uid,info.packageName)
                == AppOpsManager.MODE_ALLOWED;
    }

    public void Update() {
        ATapplicaion aTapplicaion = ATapplicaion.getInstance();
        ptime = aTapplicaion.getPTime();
        ntime = aTapplicaion.getNTime();

        //创建一个线程
        new Thread(new Runnable() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        float pweight = ATapplicaion.getInstance().getPWeight();
                        float nweight = ATapplicaion.getInstance().getNWeight();
                        PView.setText( (float)(Math.round(pweight*100))/100+"");
                        NView.setText((float)(Math.round(nweight*100))/100+"");
                        if(pweight>0.8){
                            pweight = (float)0.8;
                            nweight = 1-pweight;
                        }
                        if(nweight>0.8){
                            nweight = (float)0.8;
                            pweight = 1-nweight;
                        }
                        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, nweight );
                        Playout.setLayoutParams(param);
                        param = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, pweight );
                        Nlayout.setLayoutParams(param);
                    }
                });
            }
        }).start();
    }

    public static MainActivity getInstance(){
        return instance;
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
