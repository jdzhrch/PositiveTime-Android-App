package com.sjtu.se2017.positivetime.view.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

/**
 * Created by Administrator on 2017/8/29.
 */

public class Fragment_shichang extends Fragment {

    List<AppSearchInfo> adapterDatas;
    ListView listView;
    private Fragment_shichang.AppAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment, container, false);
        adapterDatas = new ArrayList<AppSearchInfo>();
        listView = (ListView) view.findViewById(R.id.appList);
        adapter = new Fragment_shichang.AppAdapter(adapterDatas,getActivity());
        listView.setAdapter(adapter);

        initAppList();
        return view;
    }

    public void initAppList(){/*
        AppSearchInfo m = new AppSearchInfo();
        m.setAppName("appname");
        m.setPackageName("packagename");
        m.setCategory("category");
        m.setInstallNum(1);
        m.setWeight(2);
        m.setImage(getResources().getDrawable(R.drawable.account));
        adapterDatas.add(m);
        adapter.notifyDataSetChanged();*/
        DownloadTask t = new DownloadTask();
        t.execute();
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

        public AppAdapter(List<AppSearchInfo> appSearchInfos, Context context) {
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
            convertView = inflater.inflate(R.layout.apprankinfo_listitem, null);
            app_icon = (ImageView) convertView.findViewById(R.id.app_icon);
            app_name = (TextView) convertView.findViewById(R.id.app_name);
            installNum = (TextView) convertView.findViewById(R.id.installNum);
            category = (TextView) convertView.findViewById(R.id.category);
            minutes = (TextView) convertView.findViewById(R.id.minutes);
            materialRatingBar = (MaterialRatingBar) convertView.findViewById(R.id.materialRatingBar);
            TextView rank = (TextView) convertView.findViewById(R.id.rank);

            app_icon.setImageDrawable(appSearchInfo.getImage());
            app_name.setText(appSearchInfo.getAppName());
            installNum.setText("("+appSearchInfo.getInstallNum()+")");
            category.setText(appSearchInfo.getCategory());
            materialRatingBar.setRating((float)appSearchInfo.getWeight()/20);
            minutes.setText(appSearchInfo.getMinutes()+"分钟");
            rank.setText((position+1)+"");


            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.v("aaa","aaa");
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("market://details?id=" + appSearchInfo.getPackageName())); //跳转到应用市场，非Google Play市场一般情况也实现了这个接口
                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) { //可以接收
                        startActivity(intent);
                    } else { //没有应用市场，我们通过浏览器跳转到Google Play
                        intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + appSearchInfo.getPackageName()));
                        if (intent.resolveActivity(getActivity().getPackageManager()) != null) { //有浏览器
                            startActivity(intent);
                        } else { //天哪，这还是智能手机吗？
                            Toast.makeText(getActivity(), "您没安装应用市场，连浏览器也没有", Toast.LENGTH_SHORT).show();
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

            urlStr = getResources().getString(R.string.ipAddress) + "/appinfo/rankByMinutes";

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
            } else {
                Toast.makeText(getActivity().getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
