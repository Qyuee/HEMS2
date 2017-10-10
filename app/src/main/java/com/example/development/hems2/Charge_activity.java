package com.example.development.hems2;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TabHost;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Charge_activity extends AppCompatActivity {

    Date currentDate;
    SimpleDateFormat sdf_year, sdf_month, sdf_day;              // 데이터 포멧을 설정하기 위해.
    private long now=System.currentTimeMillis();                // 시스템의 현재 시간.
    private int first_Year, first_Month, first_Day;             // 첫번째 데이터 피커 다이어로그에서 선택한 날짜를 입력 받는다.
    private int second_Year, second_Month, second_Day;          // 두번째 데이터 피커 다이어로그에서 선택한 날짜.

    BarChart barChart=null;
    BarDataSet barDataSet=null;
    BarData barData=null;

    ArrayList<String> labels=new ArrayList<>();
    ArrayList<BarEntry> entries=new ArrayList<>();

    // "일간 조회"의 "날짜 선택" 버튼 연결 변수
    private Button select_day=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge);

        //"일간 조회"의 버튼 연결 부분
        select_day=(Button) findViewById(R.id.select_day);

        // 초기 날짜 값을 설정하기 위한 부분.
        currentDate=new Date(now);
        sdf_year=new SimpleDateFormat("yyyy");
        first_Year=Integer.parseInt(sdf_year.format(currentDate));
        second_Year=Integer.parseInt(sdf_year.format(currentDate));

        sdf_month=new SimpleDateFormat("MM");
        first_Month=Integer.parseInt(sdf_month.format(currentDate))+1;
        second_Month=Integer.parseInt(sdf_month.format(currentDate))+1;

        sdf_day=new SimpleDateFormat("dd");
        first_Day=Integer.parseInt(sdf_day.format(currentDate));
        second_Day=Integer.parseInt(sdf_day.format(currentDate));

        // "탭 레이아웃"을 만드는 부분.
        TabHost tabHost1=(TabHost) findViewById(R.id.tabHost1);
        tabHost1.setup();

        TabHost.TabSpec ts1=tabHost1.newTabSpec("Tab Spec 1");
        ts1.setContent(R.id.content1);
        ts1.setIndicator("일간");
        tabHost1.addTab(ts1);

        TabHost.TabSpec ts2=tabHost1.newTabSpec("Tab Spec 2");
        ts2.setContent(R.id.content2);
        ts2.setIndicator("주간");
        tabHost1.addTab(ts2);

        TabHost.TabSpec ts3=tabHost1.newTabSpec("Tab Spec 3");
        ts3.setContent(R.id.content3);
        ts3.setIndicator("월간");
        tabHost1.addTab(ts3);
    }

    /*
        1. 일간 요금 조회 -> 하루 시간별에 대한 데이터 필요함.
        2. 주간 요금 조회 -> 선택 범위에 따른 데이터 필요함.
        3. 월간 요금 조회 -> 1일~30,31일 에 따른 데이터 필요함.
     */

    // "날짜 선택"버튼 눌렀을 때 동작 -> 데이트 피커 동작과 버튼의 텍스트 수정 부분.
    public void select_day(View v){
        java.util.Calendar cal= java.util.Calendar.getInstance();
        int dyear=cal.get(cal.YEAR);
        int dmonth=cal.get(cal.MONTH);
        int dday=cal.get(cal.DAY_OF_MONTH);

        DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                first_Year=year;
                first_Month=month+1;
                first_Day=day;
                Toast.makeText(getApplicationContext(), first_Year+". "+first_Month+". "+first_Day, Toast.LENGTH_SHORT).show();
                select_day.setText(first_Year+"년 "+first_Month+"월 "+first_Day+"일");
            }
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, dateSetListener, dyear, dmonth, dday-7);
        datePickerDialog.show();
    }

    // 일간 요금조회 그래프 그리는 메소드.
    public void drawGraph_day(){
        entries.add(new BarEntry(10, 0));
        entries.add(new BarEntry(20, 1));
        entries.add(new BarEntry(30, 2));
        entries.add(new BarEntry(40, 3));
        entries.add(new BarEntry(50, 4));

        labels.add("1");
        labels.add("2");
        labels.add("3");
        labels.add("4");
        labels.add("5");

        barDataSet=new BarDataSet(entries,"");
        barDataSet.setColor(Color.BLACK);
        barDataSet.setDrawValues(true);

        barData=new BarData(labels, barDataSet);
        barData.setDrawValues(true);
        barData.setValueTextSize(10);
        barData.setHighlightEnabled(true);

        barChart=(BarChart) findViewById(R.id.Current_charge_chart);
        barChart.setData(barData);
        barChart.getAxisRight().setEnabled(false);
        barChart.getAxisLeft();

        barChart.setBorderColor(Color.WHITE);

        YAxis y= barChart.getAxisRight();
        y.setDrawAxisLine(true);
        y.setTextColor(Color.BLACK);

        XAxis x=barChart.getXAxis();
        x.setAvoidFirstLastClipping(true);
        x.setTextColor(Color.BLACK);
        x.setDrawAxisLine(false);
        x.setDrawGridLines(true);
        x.setDrawLabels(true);
        x.setEnabled(true);

        Legend legend=barChart.getLegend();
        legend.setTextColor(Color.BLACK);
        legend.setEnabled(true);

        barChart.animateXY(1000,1000);
        barChart.invalidate();
    }
}
