package com.sjtu.se2017.positivetime.view.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.DatePicker;

import com.jn.chart.charts.LineChart;
import com.jn.chart.data.Entry;
import com.jn.chart.manager.LineChartManager;
import com.sjtu.se2017.positivetime.R;
import com.sjtu.se2017.positivetime.dao.ATDao;

import java.util.ArrayList;
import java.util.Calendar;

public class LineChartActivity extends Activity {
    private LineChart mLineChart;
    private Context context;
    private int todayOfYear;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linechart);

        mLineChart = (LineChart) findViewById(R.id.lineChart);
        //设置图表的描述
        mLineChart.setDescription("AT变化图");

        //设置x轴的数据
        final ArrayList<String> xValues = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            xValues.add(i+"点");
        }

        //设置y轴的数据
        //ArrayList<Entry> yValue = new ArrayList<>();

        ATDao atDao = new ATDao(this);
        ArrayList<Entry> yValue = atDao.checkATofToday(Calendar.getInstance().get(Calendar.DAY_OF_YEAR));
        if(yValue.size()==0){
            yValue.add(new Entry(0, 1));
            yValue.add(new Entry(0, 2));
            yValue.add(new Entry(0, 3));
            yValue.add(new Entry(0, 4));
            yValue.add(new Entry(0, 5));
            yValue.add(new Entry(0, 6));
            yValue.add(new Entry(0, 7));
        }
        //设置折线的名称
        LineChartManager.setLineName("AT");
        //创建一条折线的图表
        LineChartManager.initSingleLineChart(context,mLineChart,xValues,yValue);

        //日期选择器
        DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker);
        Calendar calNow = Calendar.getInstance();
        int year = calNow.get(Calendar.YEAR);
        int month = calNow.get(Calendar.MONTH);
        int day = calNow.get(Calendar.DAY_OF_MONTH);
        long  time_e=calNow.getTimeInMillis();
        datePicker.setMaxDate(time_e);
        calNow.set(year, 1, 1);
        time_e=calNow.getTimeInMillis();
        datePicker.setMinDate(time_e);
        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth) {
                // 获取一个日历对象，并初始化为当前选中的时间
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfMonth);
                todayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
                ATDao atDao = new ATDao(LineChartActivity.this);
                ArrayList<Entry> yValue = atDao.checkATofToday(todayOfYear);
                //设置折线的名称
                LineChartManager.setLineName("AT");
                //创建一条折线的图表
                LineChartManager.initSingleLineChart(context,mLineChart,xValues,yValue);
            }
        });
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

}
