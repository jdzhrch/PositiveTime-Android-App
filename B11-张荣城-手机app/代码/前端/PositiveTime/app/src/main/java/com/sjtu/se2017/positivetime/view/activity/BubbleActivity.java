package com.sjtu.se2017.positivetime.view.activity;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.gson.Gson;
import com.sjtu.se2017.positivetime.R;
import com.sjtu.se2017.positivetime.model.Appsta;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class BubbleActivity extends AppCompatActivity {

    //private Drawable Icon;
    private String label,packageName,viewemail;
    private ArrayList<Appsta> ShowList;
    private int style;
    private long totalTime;
    private int totalTimes;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_featureapp);
        //email = aTapplicaion.getEmail();
        viewemail = getIntent().getStringExtra("email");//email是从相似用户列表传输过来的 用户点击了哪个就传输哪个
        DownloadTask t = new DownloadTask();
        t.execute(viewemail);
        try{
            TimeUnit.MILLISECONDS.sleep(500);
        }catch(InterruptedException e){
            System.out.print(e.getStackTrace());
        }
        onResume();



    }



    //每次重新进入界面的时候加载listView
    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();

        List<Map<String,Object>> datalist = null;



        datalist = getDataList(ShowList);

        ListView listView = (ListView)findViewById(R.id.AppStatisticsList);
        SimpleAdapter adapter = new SimpleAdapter(this,datalist,R.layout.inner_list,
                new String[]{"label","info"},
                new int[]{R.id.label,R.id.info});
        listView.setAdapter(adapter);

        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object o, String s) {
                if(view instanceof ImageView && o instanceof Drawable){

                    ImageView iv=(ImageView)view;
                    iv.setImageDrawable((Drawable)o);
                    return true;
                }
                else return false;
            }
        });

//        TextView textView = (TextView)findViewById(R.id.text1);
//        textView.setText("运行总时间: " + DateUtils.formatElapsedTime(totalTime / 1000));
    }

    private List<Map<String,Object>> getDataList(ArrayList<Appsta> ShowList) {
        List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();

        Map<String,Object> map = new HashMap<String,Object>();


        dataList.add(map);
        int size = ShowList.size();

        for(int i=0;i<size;i++) {
            map = new HashMap<String,Object>();
            map.put("label",ShowList.get(i).getAppName());
            map.put("info","运行时间: " + ShowList.get(i).getTime());

            dataList.add(map);
        }

        /*for(int i=0; i<size; i++) {
            try {
            PackageManager pm = getPackageManager();
            map = new HashMap<String,Object>();
            packageName = ShowList.get(i).getAppName();
            ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);
            label = (String)pm.getApplicationLabel(applicationInfo);
            map.put("label",label);

            map.put("info","运行时间: " + ShowList.get(i).getTime());
            dataList.add(map);

            } catch (PackageManager.NameNotFoundException  e) {
                e.printStackTrace();
            }

        }*/

        return dataList;
    }

    private class DownloadTask extends AsyncTask<String, Object, String> {

        @Override
        protected String doInBackground(String... params) {

            ShowList = new ArrayList<Appsta>();
            String returnStr = "";
            String urlStr = getResources().getString(R.string.ipAddress)+"/appinfo/userApp";
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
                JSONObject json = new JSONObject();//创建json对象


                json.put("email",  URLEncoder.encode(params[0], "UTF-8"));//使用URLEncoder.encode对特殊和不可见字符进行编码
                String jsonstr = json.toString();//把JSON对象按JSON的编码格式转换为字符串
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
                    JSONArray array = JSONArray.fromObject(t);
                    int size = array.size();
                    Gson gson = new Gson();
                    if(size != 0) {
                        for (int k = 0; k < size; k++) {
                            JSONObject tmp = JSONObject.fromObject(array.get(k));
                            Appsta m = new Appsta();
                            m.setAppName(tmp.getString("appname"));
                            m.setTime(tmp.getInt("duration"));
                            ShowList.add(m);
                            //Log.v("done",m.getAppName());
                        }
                        Log.v("done", "end");
                        returnStr = "success";
                    } else {
                        returnStr = "no result";
                    }
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
