package com.sjtu.se2017.positivetime.view.activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sjtu.se2017.positivetime.R;
import com.sjtu.se2017.positivetime.model.AppSearchInfo;
import com.sjtu.se2017.positivetime.model.UserSearchInfo;
import com.sjtu.se2017.positivetime.model.application.ATapplicaion;
import com.sjtu.se2017.positivetime.service.UpdateUIService;
import com.sjtu.se2017.positivetime.service.WatchDogService;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;


/**
 * Created by Administrator on 2017/7/7.
 */

public class UserActivity extends Activity {
    Context context;
    List<UserSearchInfo> adapterDatas;
    ListView listView;
    private AppAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        context = this;

        adapterDatas = new ArrayList<UserSearchInfo>();


        //findSimilarusers("222"+"@"+"qq.com");
        findSimilarusers(ATapplicaion.getInstance().getEmail());

        listView = (ListView) findViewById(R.id.UserSearchInfoList);
        adapter = new UserActivity.AppAdapter(adapterDatas,context);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    public void findSimilarusers(String email){
        DownloadTasks t = new DownloadTasks();
        t.execute(email);
    }

    private class AppAdapter extends BaseAdapter {
        private List<UserSearchInfo> userSearchInfos;
        private LayoutInflater inflater;
        ImageView avatar;
        TextView username;
        TextView achievements;

        public AppAdapter() {}

        public AppAdapter(List<UserSearchInfo> UserSearchInfos,Context context) {
            this.userSearchInfos = userSearchInfos;
            this.inflater=LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return adapterDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return adapterDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final UserSearchInfo userSearchInfo = adapterDatas.get(position);
            convertView = inflater.inflate(R.layout.usersearchinfo_listitem, null);
            avatar = (ImageView) convertView.findViewById(R.id.avatar);
            username = (TextView) convertView.findViewById(R.id.username);
            achievements = (TextView) convertView.findViewById(R.id.achievements);

            avatar.setImageDrawable(userSearchInfo.getAvatar());
            username.setText(userSearchInfo.getUsername());
            //achievements.setText(userSearchInfos.achievementsToString());
            achievements.setText("achievements");
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(UserActivity.this,UserDetailActivity.class);
                    intent.putExtra("email",userSearchInfo.getEmail());
                    startActivity(intent);
                }
            });
            return convertView;
        }
    }

    private class DownloadTasks extends AsyncTask<String, Object, String> {

        @Override
        protected String doInBackground(String... params) {
            String returnStr = "";
            String urlStr = getResources().getString(R.string.ipAddress)+"/user/similarUser";
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
                json.put("email", URLEncoder.encode(params[0], "UTF-8"));//使用URLEncoder.encode对特殊和不可见字符进行编码
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
                            UserSearchInfo m = new UserSearchInfo();
                            m.setAvatar(null);
                            m.setEmail(tmp.getString("email"));
                            m.setUsername(tmp.getString("username"));
                            m.setAchievements(null);
                            adapterDatas.add(m);
                            Log.v("done", m.getUsername());
                        }
                        Log.v("done", "end");
                        returnStr = "success";
                    } else{
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

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equals("success")) {
                Log.v("search", adapterDatas.size() + "");
                for (int i = 0; i < adapterDatas.size(); i++) {
                    Log.v("search", adapterDatas.get(i).getEmail());
                }
                adapter.notifyDataSetInvalidated();
            } else {
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
