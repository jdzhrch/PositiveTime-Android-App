package com.sjtu.se2017.positivetime.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.sjtu.se2017.positivetime.R;
import com.sjtu.se2017.positivetime.model.AppSearchInfo;
import com.sjtu.se2017.positivetime.model.application.ATapplicaion;

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
import java.util.List;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;



public class AppActivity extends Activity{
    Context context;
    MaterialSearchBar materialSearchBar;
    List<AppSearchInfo> adapterDatas;
    ListView listView;
    private AppAdapter adapter;
    List<String> CATEGORY_DETAIL = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);
        context = this;
        adapterDatas = new ArrayList<AppSearchInfo>();
        CATEGORY_DETAIL.add("其他类");
        CATEGORY_DETAIL.add("游戏类");
        CATEGORY_DETAIL.add("交通类");
        CATEGORY_DETAIL.add("购物类");
        CATEGORY_DETAIL.add("学习类");
        CATEGORY_DETAIL.add("生活类");
        CATEGORY_DETAIL.add("阅读/视频类");
        CATEGORY_DETAIL.add("工具类");
        CATEGORY_DETAIL.add("社交类");
        CATEGORY_DETAIL.add("运动类");
        CATEGORY_DETAIL.add("金融类");



        listView = (ListView) findViewById(R.id.AppSearchInfoList);
        adapter = new AppActivity.AppAdapter(adapterDatas,context);
        listView.setAdapter(adapter);

        materialSearchBar = (MaterialSearchBar)findViewById(R.id.materialSearchBar);
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener(){
            @Override
            public void onButtonClicked(int buttonCode){
                //Log.v("test",buttonCode+"");
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                search(text.toString());
                //startSearch(text.toString(), true, null, true);
            }

            @Override
            public void onSearchStateChanged(boolean enabled) {
            }
        });
        initAppList(ATapplicaion.getInstance().getEmail());
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

    public void search(String text){
        //adapterDatas
        DownloadTask t = new DownloadTask();
        t.execute(text,"s");
        //这里把“查询”那个按钮设为不能点击的状态，下面是我新创一个线程接收数据
    }

    public void initAppList(String email){
        DownloadTask t = new DownloadTask();
        t.execute(email,"r");
        ((TextView) findViewById(R.id.hint)).setText("以下是根据您的手机使用情况为您推荐的app");
    }//每次查询或者推荐应该把list清空掉再重新填装吧？

    private class AppAdapter extends BaseAdapter {
        private List<AppSearchInfo> appSearchInfos;
        private LayoutInflater inflater;
        ImageView app_icon;
        TextView app_name;
        TextView installNum;
        TextView category;
        TextView minutes;
        MaterialRatingBar materialRatingBar;

        public AppAdapter() {}

        public AppAdapter(List<AppSearchInfo> appSearchInfos,Context context) {
            this.appSearchInfos = appSearchInfos;
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
            final AppSearchInfo appSearchInfo = adapterDatas.get(position);
            convertView = inflater.inflate(R.layout.appsearchinfo_listitem, null);
            app_icon = (ImageView) convertView.findViewById(R.id.app_icon);
            app_name = (TextView) convertView.findViewById(R.id.app_name);
            installNum = (TextView) convertView.findViewById(R.id.installNum);
            category = (TextView) convertView.findViewById(R.id.category);
            minutes = (TextView) convertView.findViewById(R.id.minutes);
            materialRatingBar = (MaterialRatingBar) convertView.findViewById(R.id.materialRatingBar);

            app_icon.setImageDrawable(appSearchInfo.getImage());
            app_name.setText(appSearchInfo.getAppName());
            installNum.setText("("+appSearchInfo.getInstallNum()+")");
            category.setText(CATEGORY_DETAIL.get(Integer.parseInt(appSearchInfo.getCategory())));
            minutes.setText(appSearchInfo.getMinutes()+"分钟");
            materialRatingBar.setRating((float)appSearchInfo.getWeight()/20);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.v("aaa","aaa");
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("market://details?id=" + appSearchInfo.getPackageName())); //跳转到应用市场，非Google Play市场一般情况也实现了这个接口
                    if (intent.resolveActivity(getPackageManager()) != null) { //可以接收
                        startActivity(intent);
                    } else { //没有应用市场，我们通过浏览器跳转到Google Play
                        intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + appSearchInfo.getPackageName()));
                        if (intent.resolveActivity(getPackageManager()) != null) { //有浏览器
                            startActivity(intent);
                        } else { //天哪，这还是智能手机吗？
                            Toast.makeText(AppActivity.this, "您没安装应用市场，连浏览器也没有", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
            return convertView;
        }
    }

    private class DownloadTask extends AsyncTask<String, Object, String> {

        @Override
        protected String doInBackground(String... params) {

            adapterDatas = new ArrayList<AppSearchInfo>();
            String returnStr = "";
            String urlStr;
            if(params[1].equals("s")) {
                urlStr = getResources().getString(R.string.ipAddress) + "/appinfo/search";
            }else{
                urlStr = getResources().getString(R.string.ipAddress) + "/appinfo/recommand";
            }
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

                if(params[1].equals("s")) {
                    json.put("appname", URLEncoder.encode(params[0], "UTF-8"));//使用URLEncoder.encode对特殊和不可见字符进行编码
                }else{
                    json.put("email",URLEncoder.encode(params[0],"UTF-8"));//URLEcoder.encode 会把@等符号encode后再decode后得不到原来的符号
                }
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
                            AppSearchInfo m = new AppSearchInfo();
                            m.setAppName(tmp.getString("appName"));
                            m.setPackageName(tmp.getString("packageName"));
                            m.setCategory(tmp.getString("category"));
                            m.setInstallNum(tmp.getInt("installNum"));
                            m.setWeight(tmp.getInt("weight"));
                            m.setMinutes(tmp.getInt("minutes"));
                            m.setImage(null);
                            adapterDatas.add(m);
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


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equals("success")) {
                Log.v("search", adapterDatas.size() + "");
                for (int i = 0; i < adapterDatas.size(); i++) {
                    Log.v("search", adapterDatas.get(i).getAppName());
                }
                adapter.notifyDataSetChanged();
                ((TextView) findViewById(R.id.hint)).setText("以下是查询结果");
            } else {
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            }
        }
    }

}
