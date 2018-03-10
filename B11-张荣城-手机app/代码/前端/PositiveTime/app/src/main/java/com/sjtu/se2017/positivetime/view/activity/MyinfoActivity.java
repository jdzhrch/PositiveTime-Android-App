package com.sjtu.se2017.positivetime.view.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sjtu.se2017.positivetime.R;
import com.sjtu.se2017.positivetime.model.application.ATapplicaion;
import com.sjtu.se2017.positivetime.service.util.BarUtils;
import com.sjtu.se2017.positivetime.view.BaseActivity;

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
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.sjtu.se2017.positivetime.R.id.toolbar;

/**
 * Created by bonjour on 17-9-5.
 */

public class MyinfoActivity extends BaseActivity {
    @BindView(R.id.head)
    CircleImageView mHead;
    @BindView(R.id.main_fl_title)
    RelativeLayout mMainFlTitle;
    @BindView(R.id.main_tv_toolbar_title)
    TextView mMainTvToolbarTitle;
    @BindView(toolbar)
    Toolbar mToolbar;
    @BindView(R.id.main_abl_app_bar)
    AppBarLayout mMainAblAppBar;
    //private Button star;
    private ImageView follows,bubble;
    private TextView totalAT,averageATbyday,averageUTimebyday;
    private String viewemail,myownemail;
    double total,averageAT = 0,averageUtime = 0;
    boolean isfollow = false;

    @Override
    protected int bindLayout() {
        return R.layout.activity_myinfo;
    }

    @Override
    protected void initView() {
        BarUtils.setColor(this, Color.parseColor("#5DC9D3"), 0);
        ButterKnife.bind(this);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);

        ATapplicaion aTapplicaion = ATapplicaion.getInstance();

        //viewemail = getIntent().getStringExtra("email");//email是从相似用户列表传输过来的 用户点击了哪个就传输哪个
        myownemail = aTapplicaion.getEmail(); //myownemail represent who is the user

        MyinfoActivity.DownloadTask t = new MyinfoActivity.DownloadTask();
        t.execute(myownemail);
        //MyinfoActivity.DownloadTask3 t3 = new MyinfoActivity.DownloadTask3();
        //t3.execute(myownemail,viewemail);
        try{
            TimeUnit.MILLISECONDS.sleep(300);
        }catch(InterruptedException e){
            System.out.print(e.getStackTrace());
        }
        averageATbyday = (TextView) findViewById(R.id.averageATbyday);
        averageUTimebyday = (TextView) findViewById(R.id.averageUTimebyday);

        ((TextView)findViewById(R.id.email)).setText(myownemail);
        averageATbyday.setText(""+averageAT);
        averageUTimebyday.setText(""+averageUtime);

        //star = (Button) findViewById(R.id.button);
        //check if myownemail follows viewemail

        /*if (isfollow){
            star.setText("已关注");
        }
        else{
            star.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    MyinfoActivity.DownloadTask2 t2 = new MyinfoActivity.DownloadTask2();
                    t2.execute(myownemail,viewemail);
                    star.setText("已关注");


                }

            });
        }*/


        bubble = (ImageView) findViewById(R.id.bubble);
        bubble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyinfoActivity.this,BubbleActivity.class);
                intent.putExtra("email",myownemail);
                startActivity(intent);
            }
        });
        follows = (ImageView) findViewById(R.id.follows);
        follows.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyinfoActivity.this,FollowActivity.class);
                intent.putExtra("email",myownemail);
                startActivity(intent);
            }
        });



    }

    @Override
    protected void initListener() {
        super.initListener();
        mMainAblAppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int halfScroll = appBarLayout.getTotalScrollRange() / 2;
                int offSetAbs = Math.abs(verticalOffset);
                float percentage;
                if (offSetAbs < halfScroll) {
                    mMainTvToolbarTitle.setText("正时间");
                    percentage = 1 - (float) offSetAbs / (float) halfScroll;
                } else {
                    mMainTvToolbarTitle.setText("个人中心");
                    percentage = (float) (offSetAbs - halfScroll) / (float) halfScroll;
                }
                mToolbar.setAlpha(percentage);
            }
        });
    }

    private class DownloadTask extends AsyncTask<String, Object, String> {

        @Override
        protected String doInBackground(String... params) {

            Log.v("email",params[0]);
            String returnStr = "";
            String urlStr = getResources().getString(R.string.ipAddress)+"/appinfo/userInfo" ;
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
                JSONObject json = new JSONObject();//创建json对象
                json.put("email",  URLEncoder.encode(params[0], "UTF-8"));//使用URLEncoder.encode对特殊和不可见字符进行编码

                jsonstr = json.toString();//把JSON对象按JSON的编码格式转换为字符串
                System.out.print(jsonstr);
                //------------字符流写入数据------------
                OutputStream out = urlConnection.getOutputStream();//输出流，用来发送请求，http请求实际上直到这个函数里面才正式发送出去
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));//创建字符流对象并用高效缓冲流包装它，便获得最高的效率,发送的是字符串推荐用字符流，其它数据就用字节流
                bw.write(jsonstr);//把json字符串写入缓冲区中
                bw.flush();//刷新缓冲区，把数据发送出去，这步很重要
                out.close();
                bw.close();//使用完关闭
                Log.v("email1",jsonstr);
                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {//得到服务端的返回码是否连接成功
                    InputStream in = urlConnection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    String str = null;
                    StringBuffer buffer = new StringBuffer();
                    Log.v("email2",params[0]);
                    while ((str = br.readLine()) != null) {//BufferedReader特有功能，一次读取一行数据
                        buffer.append(str);
                    }
                    in.close();
                    br.close();
                    String t = buffer.toString();
                    JSONObject tmp = JSONObject.fromObject(t);
                    Log.v("email3",t);
                    averageAT = tmp.getDouble("avg_at");
                    averageUtime =tmp.getDouble("avg_min");

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

    private class DownloadTask2 extends AsyncTask<String, Object, String> {

        @Override
        protected String doInBackground(String... params) {


            String returnStr = "";
            String urlStr = getResources().getString(R.string.ipAddress)+"/follow/insert_follow" ;
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
                JSONObject json = new JSONObject();//创建json对象
                json.put("email",  URLEncoder.encode(params[0], "UTF-8"));//使用URLEncoder.encode对特殊和不可见字符进行编码
                json.put("following",URLEncoder.encode(params[1], "UTF-8"));
                jsonstr = json.toString();//把JSON对象按JSON的编码格式转换为字符串
                System.out.print(jsonstr);
                //------------字符流写入数据------------
                OutputStream out = urlConnection.getOutputStream();//输出流，用来发送请求，http请求实际上直到这个函数里面才正式发送出去
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));//创建字符流对象并用高效缓冲流包装它，便获得最高的效率,发送的是字符串推荐用字符流，其它数据就用字节流
                bw.write(jsonstr);//把json字符串写入缓冲区中
                bw.flush();//刷新缓冲区，把数据发送出去，这步很重要
                out.close();
                bw.close();//使用完关闭
                Log.v("email1",jsonstr);
                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {//得到服务端的返回码是否连接成功
                    InputStream in = urlConnection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    String str = null;
                    StringBuffer buffer = new StringBuffer();
                    Log.v("email2",params[0]);
                    while ((str = br.readLine()) != null) {//BufferedReader特有功能，一次读取一行数据
                        buffer.append(str);
                    }
                    in.close();
                    br.close();
                    String t = buffer.toString();
                    JSONObject tmp = JSONObject.fromObject(t);
                    Log.v("email3",t);


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

    private class DownloadTask3 extends AsyncTask<String, Object, String> {

        @Override
        protected String doInBackground(String... params) {


            String returnStr = "";
            String urlStr = getResources().getString(R.string.ipAddress)+"/follow/is_follow" ;
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
                JSONObject json = new JSONObject();//创建json对象
                json.put("email",  URLEncoder.encode(params[0], "UTF-8"));//使用URLEncoder.encode对特殊和不可见字符进行编码
                json.put("following",URLEncoder.encode(params[1], "UTF-8"));
                jsonstr = json.toString();//把JSON对象按JSON的编码格式转换为字符串
                System.out.print(jsonstr);
                //------------字符流写入数据------------
                OutputStream out = urlConnection.getOutputStream();//输出流，用来发送请求，http请求实际上直到这个函数里面才正式发送出去
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));//创建字符流对象并用高效缓冲流包装它，便获得最高的效率,发送的是字符串推荐用字符流，其它数据就用字节流
                bw.write(jsonstr);//把json字符串写入缓冲区中
                bw.flush();//刷新缓冲区，把数据发送出去，这步很重要
                out.close();
                bw.close();//使用完关闭
                Log.v("email1",jsonstr);
                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {//得到服务端的返回码是否连接成功
                    InputStream in = urlConnection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    String str = null;
                    StringBuffer buffer = new StringBuffer();
                    Log.v("email2",params[0]);
                    while ((str = br.readLine()) != null) {//BufferedReader特有功能，一次读取一行数据
                        buffer.append(str);
                    }
                    in.close();
                    br.close();
                    String t = buffer.toString();
                    if(t.equals("1")){
                        isfollow = true;
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
