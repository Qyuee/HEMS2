package com.example.development.hems2;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class WeatherInfo_activity extends AppCompatActivity {
    private String Tag="날씨 정보 스레드 : ";

    ArrayAdapter<CharSequence> adapter1, adapter2, adapter3;
    TextView today;
    Weather_thread weather_thread;

    // 오늘 날씨 관련 TableRow, TextView, ImageView 변수들
    private TextView today_area_info;

    private TableRow today_first;
    private TableRow today_second;
    private TableRow today_third;
    private TableRow today_forth;
    private TableRow today_fifth;
    private TableRow today_sixth;
    private TableRow today_seven;
    private TableRow today_eight;

    private TextView today_first_text;
    private TextView today_second_text;
    private TextView today_third_text;
    private TextView today_forth_text;
    private TextView today_fifth_text;
    private TextView today_sixth_text;
    private TextView today_seven_text;
    private TextView today_eight_text;

    private ImageView today_first_image;
    private ImageView today_second_image;
    private ImageView today_third_image;
    private ImageView today_forth_image;
    private ImageView today_fifth_image;
    private ImageView today_sixth_image;
    private ImageView today_seven_image;
    private ImageView today_eight_image;

    TableRow[] today_row=null;
    TextView[] today_te=null;
    ImageView[] today_ti=null;

    // 내일 날씨 관련 TableRow, TextView, ImageView 변수들
    private TextView tomorrow_area_info;

    private TableRow tomorrow_first;
    private TableRow tomorrow_second;
    private TableRow tomorrow_thrid;
    private TableRow tomorrow_forth;
    private TableRow tomorrow_fifth;
    private TableRow tomorrow_sixth;
    private TableRow tomorrow_seven;
    private TableRow tomorrow_eight;

    private TextView tomorrow_first_text;
    private TextView tomorrow_second_text;
    private TextView tomorrow_third_text;
    private TextView tomorrow_forth_text;
    private TextView tomorrow_fifth_text;
    private TextView tomorrow_sixth_text;
    private TextView tomorrow_seven_text;
    private TextView tomorrow_eight_text;

    private ImageView tomorrow_first_image;
    private ImageView tomorrow_second_image;
    private ImageView tomorrow_third_image;
    private ImageView tomorrow_forth_image;
    private ImageView tomorrow_fifth_image;
    private ImageView tomorrow_sixth_image;
    private ImageView tomorrow_seven_image;
    private ImageView tomorrow_eight_image;

    TextView[] te=null;
    ImageView[] ti=null;
    //==============================================

    // 모레 날씨 TableRow, TextView, ImageView 변수들
    private LinearLayout after_tomorrow_layout;

    private TextView after_tomorrow_area_info;

    private TableRow after_tomorrow_first;
    private TableRow after_tomorrow_second;
    private TableRow after_tomorrow_thrid;
    private TableRow after_tomorrow_forth;
    private TableRow after_tomorrow_fifth;
    private TableRow after_tomorrow_sixth;
    private TableRow after_tomorrow_seven;
    private TableRow after_tomorrow_eight;

    private TextView after_tomorrow_first_text;
    private TextView after_tomorrow_second_text;
    private TextView after_tomorrow_third_text;
    private TextView after_tomorrow_forth_text;
    private TextView after_tomorrow_fifth_text;
    private TextView after_tomorrow_sixth_text;
    private TextView after_tomorrow_seven_text;
    private TextView after_tomorrow_eight_text;

    private ImageView after_tomorrow_first_image;
    private ImageView after_tomorrow_second_image;
    private ImageView after_tomorrow_third_image;
    private ImageView after_tomorrow_forth_image;
    private ImageView after_tomorrow_fifth_image;
    private ImageView after_tomorrow_sixth_image;
    private ImageView after_tomorrow_seven_image;
    private ImageView after_tomorrow_eight_image;

    TextView[] after_te=null;
    ImageView[] after_ti=null;
    //=============================================

    // 프로그래스바 레이아웃 정의 변수
    LinearLayout linearLayout=null;

    // 프로그래스바 레이아웃 옵션 변수
    LinearLayout.LayoutParams params=null;

    String zoneCode="11110";   //디폴트 - 종로구

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_info);

        final Spinner s1=(Spinner) findViewById(R.id.select_area1);
        final Spinner s2=(Spinner) findViewById(R.id.select_area2);
        final Spinner s3=(Spinner) findViewById(R.id.select_area3);

        //================================오늘 날씨 변수 연결================================
        today_area_info=(TextView) findViewById(R.id.today_area_info);

        today_row=new TableRow[8];
        today_first=(TableRow) findViewById(R.id.today_first);
        today_second=(TableRow) findViewById(R.id.today_second);
        today_third=(TableRow) findViewById(R.id.today_third);
        today_forth=(TableRow) findViewById(R.id.today_forth);
        today_fifth=(TableRow) findViewById(R.id.today_fifth);
        today_sixth=(TableRow) findViewById(R.id.today_sixth);
        today_seven=(TableRow) findViewById(R.id.today_seven);
        today_eight=(TableRow) findViewById(R.id.today_eight);

        today_row[0]=today_first;
        today_row[1]=today_second;
        today_row[2]=today_third;
        today_row[3]=today_forth;
        today_row[4]=today_fifth;
        today_row[5]=today_sixth;
        today_row[6]=today_seven;
        today_row[7]=today_eight;


        today_te=new TextView[8];
        today_first_text=(TextView) findViewById(R.id.today_first_text);
        today_second_text=(TextView) findViewById(R.id.today_second_text);
        today_third_text=(TextView) findViewById(R.id.today_third_text);
        today_forth_text=(TextView) findViewById(R.id.today_forth_text);
        today_fifth_text=(TextView) findViewById(R.id.today_fifth_text);
        today_sixth_text=(TextView) findViewById(R.id.today_sixth_text);
        today_seven_text=(TextView) findViewById(R.id.today_seven_text);
        today_eight_text=(TextView) findViewById(R.id.today_eight_text);

        today_te[0]=today_first_text;
        today_te[1]=today_second_text;
        today_te[2]=today_third_text;
        today_te[3]=today_forth_text;
        today_te[4]=today_fifth_text;
        today_te[5]=today_sixth_text;
        today_te[6]=today_seven_text;
        today_te[7]=today_eight_text;

        today_ti=new ImageView[8];
        today_first_image=(ImageView) findViewById(R.id.today_first_image);
        today_second_image=(ImageView) findViewById(R.id.today_second_image);
        today_third_image=(ImageView) findViewById(R.id.today_third_image);
        today_forth_image=(ImageView) findViewById(R.id.today_forth_image);
        today_fifth_image=(ImageView) findViewById(R.id.today_fifth_image);
        today_sixth_image=(ImageView) findViewById(R.id.today_sixth_image);
        today_seven_image=(ImageView) findViewById(R.id.today_seven_image);
        today_eight_image=(ImageView) findViewById(R.id.today_eight_image);

        today_ti[0]=today_first_image;
        today_ti[1]=today_second_image;
        today_ti[2]=today_third_image;
        today_ti[3]=today_forth_image;
        today_ti[4]=today_fifth_image;
        today_ti[5]=today_sixth_image;
        today_ti[6]=today_seven_image;
        today_ti[7]=today_eight_image;

        //================================내일 날씨 변수 연결================================
        tomorrow_area_info=(TextView) findViewById(R.id.tomorrow_area_info);

        tomorrow_first=(TableRow) findViewById(R.id.tomorrow_first);
        tomorrow_second=(TableRow) findViewById(R.id.tomorrow_second);
        tomorrow_thrid=(TableRow) findViewById(R.id.tomorrow_third);
        tomorrow_forth=(TableRow) findViewById(R.id.tomorrow_forth);
        tomorrow_fifth=(TableRow) findViewById(R.id.tomorrow_fifth);
        tomorrow_sixth=(TableRow) findViewById(R.id.tomorrow_sixth);
        tomorrow_seven=(TableRow) findViewById(R.id.tomorrow_seven);
        tomorrow_eight=(TableRow) findViewById(R.id.tomorrow_eight);

        te=new TextView[8];
        tomorrow_first_text=(TextView) findViewById(R.id.tomorrow_first_text);
        tomorrow_second_text=(TextView) findViewById(R.id.tomorrow_second_text);
        tomorrow_third_text=(TextView) findViewById(R.id.tomorrow_third_text);
        tomorrow_forth_text=(TextView) findViewById(R.id.tomorrow_forth_text);
        tomorrow_fifth_text=(TextView) findViewById(R.id.tomorrow_fifth_text);
        tomorrow_sixth_text=(TextView) findViewById(R.id.tomorrow_sixth_text);
        tomorrow_seven_text=(TextView) findViewById(R.id.tomorrow_seven_text);
        tomorrow_eight_text=(TextView) findViewById(R.id.tomorrow_eight_text);

        te[0]=tomorrow_first_text;
        te[1]=tomorrow_second_text;
        te[2]=tomorrow_third_text;
        te[3]=tomorrow_forth_text;
        te[4]=tomorrow_fifth_text;
        te[5]=tomorrow_sixth_text;
        te[6]=tomorrow_seven_text;
        te[7]=tomorrow_eight_text;

        ti=new ImageView[8];

        tomorrow_first_image=(ImageView) findViewById(R.id.tomorrow_first_image);
        tomorrow_second_image=(ImageView) findViewById(R.id.tomorrow_second_image);
        tomorrow_third_image=(ImageView) findViewById(R.id.tomorrow_third_image);
        tomorrow_forth_image=(ImageView) findViewById(R.id.tomorrow_forth_image);
        tomorrow_fifth_image=(ImageView) findViewById(R.id.tomorrow_fifth_image);
        tomorrow_sixth_image=(ImageView) findViewById(R.id.tomorrow_sixth_image);
        tomorrow_seven_image=(ImageView) findViewById(R.id.tomorrow_seven_image);
        tomorrow_eight_image=(ImageView) findViewById(R.id.tomorrow_eight_image);

        ti[0]=tomorrow_first_image;
        ti[1]=tomorrow_second_image;
        ti[2]=tomorrow_third_image;
        ti[3]=tomorrow_forth_image;
        ti[4]=tomorrow_fifth_image;
        ti[5]=tomorrow_sixth_image;
        ti[6]=tomorrow_seven_image;
        ti[7]=tomorrow_eight_image;

        //====================================모레 날씨 변수 연결====================================
        after_tomorrow_layout=(LinearLayout) findViewById(R.id.after_tomorrow_layout);

        after_tomorrow_area_info=(TextView) findViewById(R.id.after_tomorrow_area_info);

        after_tomorrow_first=(TableRow) findViewById(R.id.after_tomorrow_first);
        after_tomorrow_second=(TableRow) findViewById(R.id.after_tomorrow_second);
        after_tomorrow_thrid=(TableRow) findViewById(R.id.after_tomorrow_third);
        after_tomorrow_forth=(TableRow) findViewById(R.id.after_tomorrow_forth);
        after_tomorrow_fifth=(TableRow) findViewById(R.id.after_tomorrow_fifth);
        after_tomorrow_sixth=(TableRow) findViewById(R.id.after_tomorrow_sixth);
        after_tomorrow_seven=(TableRow) findViewById(R.id.after_tomorrow_seven);
        after_tomorrow_eight=(TableRow) findViewById(R.id.after_tomorrow_eight);

        after_te=new TextView[8];

        after_tomorrow_first_text=(TextView) findViewById(R.id.after_tomorrow_first_text);
        after_tomorrow_second_text=(TextView) findViewById(R.id.after_tomorrow_second_text);
        after_tomorrow_third_text=(TextView) findViewById(R.id.after_tomorrow_third_text);
        after_tomorrow_forth_text=(TextView) findViewById(R.id.after_tomorrow_forth_text);
        after_tomorrow_fifth_text=(TextView) findViewById(R.id.after_tomorrow_fifth_text);
        after_tomorrow_sixth_text=(TextView) findViewById(R.id.after_tomorrow_sixth_text);
        after_tomorrow_seven_text=(TextView) findViewById(R.id.after_tomorrow_seven_text);
        after_tomorrow_eight_text=(TextView) findViewById(R.id.after_tomorrow_eight_text);

        after_te[0]=after_tomorrow_first_text;
        after_te[1]=after_tomorrow_second_text;
        after_te[2]=after_tomorrow_third_text;
        after_te[3]=after_tomorrow_forth_text;
        after_te[4]=after_tomorrow_fifth_text;
        after_te[5]=after_tomorrow_sixth_text;
        after_te[6]=after_tomorrow_seven_text;
        after_te[7]=after_tomorrow_eight_text;

        after_ti=new ImageView[8];

        after_tomorrow_first_image=(ImageView) findViewById(R.id.after_tomorrow_first_image);
        after_tomorrow_second_image=(ImageView) findViewById(R.id.after_tomorrow_second_image);
        after_tomorrow_third_image=(ImageView) findViewById(R.id.after_tomorrow_third_image);
        after_tomorrow_forth_image=(ImageView) findViewById(R.id.after_tomorrow_forth_image);
        after_tomorrow_fifth_image=(ImageView) findViewById(R.id.after_tomorrow_fifth_image);
        after_tomorrow_sixth_image=(ImageView) findViewById(R.id.after_tomorrow_sixth_image);
        after_tomorrow_seven_image=(ImageView) findViewById(R.id.after_tomorrow_seven_image);
        after_tomorrow_eight_image=(ImageView) findViewById(R.id.after_tomorrow_eight_image);

        after_ti[0]=after_tomorrow_first_image;
        after_ti[1]=after_tomorrow_second_image;
        after_ti[2]=after_tomorrow_third_image;
        after_ti[3]=after_tomorrow_forth_image;
        after_ti[4]=after_tomorrow_fifth_image;
        after_ti[5]=after_tomorrow_sixth_image;
        after_ti[6]=after_tomorrow_seven_image;
        after_ti[7]=after_tomorrow_eight_image;

        //=================================================================================


        // 프로그래스바 레이아웃 연결
        LayoutInflater inflater=(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        linearLayout=(LinearLayout)inflater.inflate(R.layout.progress, null);


        // 프로그래스바 레이아웃 옵션 설정
        params=new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );

        // 프로그래스바 레이아웃 가져오기
        getWindow().addContentView(linearLayout, params);

        // 프로그래스바 레이아웃 띄움(중첩 됨)
        linearLayout.setVisibility(View.VISIBLE);

        weather_thread=new Weather_thread(handler, zoneCode);
        weather_thread.setDaemon(true);
        weather_thread.start();

        adapter1=ArrayAdapter.createFromResource(this, R.array.first_area, android.R.layout.simple_spinner_dropdown_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s1.setAdapter(adapter1);

        s1.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        if(adapter1.getItem(i).equals("서울")){
                            adapter2=ArrayAdapter.createFromResource(WeatherInfo_activity.this, R.array.seoul, android.R.layout.simple_spinner_dropdown_item);
                            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            s2.setAdapter(adapter2);

                            s2.setOnItemSelectedListener(
                                    new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                            if(adapter2.getItem(i).equals("동대문구")){
                                                adapter3=ArrayAdapter.createFromResource(WeatherInfo_activity.this, R.array.동대문구, android.R.layout.simple_spinner_dropdown_item);
                                                adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                s3.setAdapter(adapter3);

                                                s3.setOnItemSelectedListener(
                                                        new AdapterView.OnItemSelectedListener() {
                                                            @Override
                                                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                                if(adapter3.getItem(i).equals("장안동")){
                                                                    zoneCode="1123065000";
                                                                } else if(adapter3.getItem(i).equals("답십리동")){
                                                                    zoneCode="1123060000";
                                                                }else if(adapter3.getItem(i).equals("용신동")){
                                                                    zoneCode="1123053600";
                                                                }else if(adapter3.getItem(i).equals("이문동")){
                                                                    zoneCode="1123074000";
                                                                }else if(adapter3.getItem(i).equals("전농동")){
                                                                    zoneCode="1123056000";
                                                                }else if(adapter3.getItem(i).equals("제기동")){
                                                                    zoneCode="1123054500";
                                                                }else if(adapter3.getItem(i).equals("청량리동")){
                                                                    zoneCode="1123070500";
                                                                }else if(adapter3.getItem(i).equals("회기동")){
                                                                    zoneCode="1123071000";
                                                                }else if(adapter3.getItem(i).equals("휘경동")){
                                                                    zoneCode="1123072000";
                                                                }
                                                            }

//                                                            else if(adapter3.getItem(i).equals("")){
//                                                                zoneCode="";
//                                                            }

                                                            @Override
                                                            public void onNothingSelected(AdapterView<?> adapterView) {

                                                            }
                                                        }
                                                );
                                                //zoneCode="11230";
                                            }else if(adapter2.getItem(i).equals("동작구")){
                                                adapter3=ArrayAdapter.createFromResource(WeatherInfo_activity.this, R.array.동작구, android.R.layout.simple_spinner_dropdown_item);
                                                adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                s3.setAdapter(adapter3);

                                                s3.setOnItemSelectedListener(
                                                        new AdapterView.OnItemSelectedListener() {
                                                            @Override
                                                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                                if(adapter3.getItem(i).equals("노량진동")){
                                                                    zoneCode="1159051000";
                                                                }else if(adapter3.getItem(i).equals("대방동")){
                                                                    zoneCode="1159066000";
                                                                }else if(adapter3.getItem(i).equals("사당동")){
                                                                    zoneCode="1159062000";
                                                                }else if(adapter3.getItem(i).equals("상도동")){
                                                                    zoneCode="1159053000";
                                                                }else if(adapter3.getItem(i).equals("신대방동")){
                                                                    zoneCode="1159067000";
                                                                }else if(adapter3.getItem(i).equals("흑석동")){
                                                                    zoneCode="1159060500";
                                                                }
                                                            }

                                                            @Override
                                                            public void onNothingSelected(AdapterView<?> adapterView) {

                                                            }
                                                        }
                                                );
                                            }else if(adapter2.getItem(i).equals("중구")){
                                                adapter3=ArrayAdapter.createFromResource(WeatherInfo_activity.this, R.array.중구, android.R.layout.simple_spinner_dropdown_item);
                                                adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                s3.setAdapter(adapter3);

                                                s3.setOnItemSelectedListener(
                                                        new AdapterView.OnItemSelectedListener() {
                                                            @Override
                                                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                                if(adapter3.getItem(i).equals("광희동")){
                                                                    zoneCode="1114059000";
                                                                }else if(adapter3.getItem(i).equals("다산동")){
                                                                    zoneCode="1114062500";
                                                                }else if(adapter3.getItem(i).equals("동화동")){
                                                                    zoneCode="1114066500";
                                                                }else if(adapter3.getItem(i).equals("명동")){
                                                                    zoneCode="1114055000";
                                                                }else if(adapter3.getItem(i).equals("소공동")){
                                                                    zoneCode="1114052000";
                                                                }else if(adapter3.getItem(i).equals("신당동")){
                                                                    zoneCode="1114061500";
                                                                }else if(adapter3.getItem(i).equals("약수동")){
                                                                    zoneCode="1114063500";
                                                                }else if(adapter3.getItem(i).equals("을지로동")){
                                                                    zoneCode="1114060500";
                                                                }else if(adapter3.getItem(i).equals("장충동")){
                                                                    zoneCode="1114058000";
                                                                }else if(adapter3.getItem(i).equals("중림동")){
                                                                    zoneCode="1114068000";
                                                                }else if(adapter3.getItem(i).equals("청구동")){
                                                                    zoneCode="1114064500";
                                                                }else if(adapter3.getItem(i).equals("필동")){
                                                                    zoneCode="1114057000";
                                                                }else if(adapter3.getItem(i).equals("황학동")){
                                                                    zoneCode="1114067000";
                                                                }else if(adapter3.getItem(i).equals("회현동")){
                                                                    zoneCode="1114054000";
                                                                }
                                                            }

                                                            @Override
                                                            public void onNothingSelected(AdapterView<?> adapterView) {

                                                            }
                                                        }
                                                );
                                            }
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> adapterView) {

                                        }
                                    }
                            );
                        }else if(adapter1.getItem(i).equals("인천")){
                            adapter2=ArrayAdapter.createFromResource(WeatherInfo_activity.this, R.array.inchon, android.R.layout.simple_spinner_dropdown_item);
                            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            s2.setAdapter(adapter2);

                            s2.setOnItemSelectedListener(
                                    new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                            if(adapter2.getItem(i).equals("중구")){
                                                zoneCode="28110";
                                            }else if(adapter2.getItem(i).equals("동구")){
                                                zoneCode="28140";
                                            }
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> adapterView) {

                                        }
                                    }
                            );
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                }
        );
    }

    // "조회" 버튼 눌렀을 때.
    public void search(View v){
        weather_thread.interrupt();
        weather_thread=new Weather_thread(handler, zoneCode);
        weather_thread.setDaemon(true);
        weather_thread.start();
//        Toast.makeText(getApplicationContext(), zoneCode, Toast.LENGTH_SHORT).show();

        linearLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Toast.makeText(getApplicationContext(), "뒤로 가기 버튼 누름!!", Toast.LENGTH_SHORT).show();
        weather_thread.interrupt();
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            linearLayout.setVisibility(View.GONE);

            Log.e(Tag, " 실행!!! ");
            Bundle bd=msg.getData();
            //String data=bd.getString("총결과");
            String today_info=bd.getString("오늘");
            String tomorrow_info=bd.getString("내일");
            String day_after_tomorrow_info=bd.getString("내일모레");
            String area=bd.getString("지역");

            String[] hour=bd.getStringArray("시간");
            String[] day=bd.getStringArray("날짜");
            String[] temp=bd.getStringArray("온도");
            String[] status=bd.getStringArray("날씨상태");
            String[] humi=bd.getStringArray("습도");
            String[] temp_max=bd.getStringArray("최고온도");
            String[] temp_min=bd.getStringArray("최저온도");

            String[][] www=new String[day.length][5];

            for(int i=0; i<hour.length; i++){
                www[i][0]=day[i];
                www[i][1]=hour[i];
                www[i][2]=temp[i];
                www[i][3]=status[i];
                www[i][4]=humi[i];
            }

            // www[i][j] --> j가 0인 공간은 날짜 구분 숫자. 1-> 시간, 2->온도, 3->날씨상태, 4->습도.
            for(int i=0; i<hour.length; i++){
                Log.e(Tag, "날짜 : "+www[i][0] +"  시간 : "+www[i][1] + "  온도 : "+www[i][2] + "  날씨 : "+www[i][3] + "  습도 : "+www[i][4]);
            }

            String[] today_value=new String[8];
            String[] tomorrow_value=new String[8];
            String[] after_tomorrow_value=new String[8];

            String tomorrow_temp_max="";
            String tomorrow_temp_min="";

            int today_index=0;
            int tomorrow_index=0;
            int after_tomorrow_index=0;

//            for(int i=0; i<day.length; i++){
//                if(day[i].equals("1")){
//                    tomorrow_temp_max=temp_max[i];
//                    tomorrow_temp_min=temp_min[i];
//                    tomorrow_area_info.setText("내일 날씨 - 최고:"+tomorrow_temp_max+"℃"+" 최저:"+tomorrow_temp_min+"℃");
//                    break;
//                }
//            }

            for(int i=0; i<day.length; i++){

                if(day[i].equals("0")){
                    today_value[today_index]=www[i][1] + "  온도 : "+www[i][2]+"℃" + "  습도 : "+www[i][4]+"%";
                    today_index++;
                }else if(day[i].equals("1")){
                    tomorrow_value[tomorrow_index]=www[i][1] + "  온도 : "+www[i][2]+"℃" + "  습도 : "+www[i][4]+"%";
                    tomorrow_index++;
                }else if(day[i].equals("2")){
                    after_tomorrow_value[after_tomorrow_index]=www[i][1] + "  온도 : "+www[i][2]+"℃" + "  습도 : "+www[i][4]+"%";
                    after_tomorrow_index++;
                }
            }

            for(int i=0; i<today_index; i++){
                if(today_value[i]==null){
                    today_area_info.setText("오늘 날씨 - 데이터를 준비 중 입니다.");
                    today_area_info.setWidth(today_area_info.getMaxWidth());
                    today_area_info.setGravity(Gravity.CENTER);

                    today_ti[i].setVisibility(View.GONE);
                    today_te[i].setVisibility(View.GONE);
                }else if(today_value[i]!=null){
                    if(!today_value[i].equals("No Data")){

                        today_row[i].setVisibility(View.VISIBLE);
                        today_te[i].setText(today_value[i]);

                        if(status[i].equals("구름 많음") && i<4){
                            today_ti[i].setImageResource(R.drawable.over_cloudy_day);
                        }else if(status[i].equals("구름 조금") && i<4){
                            today_ti[i].setImageResource(R.drawable.cloudy_day);
                        }else if(status[i].equals("맑음") && i<4){
                            today_ti[i].setImageResource(R.drawable.sunny);
                        }else if(status[i].equals("흐림")){
                            today_ti[i].setImageResource(R.drawable.cloudy);
                        }else if(status[i].equals("비")){
                            today_ti[i].setImageResource(R.drawable.rainy);
                        }else if(status[i].equals("맑음") && i>=4){
                            today_ti[i].setImageResource(R.drawable.sunny_night);
                        }else if(status[i].equals("구름 조금") && i>=4){
                            today_ti[i].setImageResource(R.drawable.cloudy_night);
                        }else if(status[i].equals("구름 많음") && i>=4){
                            today_ti[i].setImageResource(R.drawable.over_cloudy_night);
                        }
                    }else if(today_value[i].equals("")){
                        today_te[i].setVisibility(View.GONE);
                    }
                }
            }

            for(int i=0; i<8; i++){
                te[i].setText(tomorrow_value[i]);

                // 내일 예상 날씨 정보 출력

                if(status[i].equals("구름 많음") && i<4){
                    ti[i].setImageResource(R.drawable.over_cloudy_day);
                }else if(status[i].equals("구름 조금") && i<4){
                    ti[i].setImageResource(R.drawable.cloudy_day);
                }else if(status[i].equals("맑음") && i<4){
                    ti[i].setImageResource(R.drawable.sunny);
                }else if(status[i].equals("흐림")){
                    ti[i].setImageResource(R.drawable.cloudy);
                }else if(status[i].equals("비")){
                    ti[i].setImageResource(R.drawable.rainy);
                }else if(status[i].equals("맑음") && i>=4){
                    ti[i].setImageResource(R.drawable.sunny_night);
                }else if(status[i].equals("구름 조금") && i>=4){
                    ti[i].setImageResource(R.drawable.cloudy_night);
                }else if(status[i].equals("구름 많음") && i>=4){
                    ti[i].setImageResource(R.drawable.over_cloudy_night);
                }

                if(after_tomorrow_value[i]==null){
//                    after_tomorrow_layout.setMinimumHeight(after_tomorrow_area_info.getHeight());
                    after_tomorrow_area_info.setText("모레 날씨 - 데이터를 준비 중 입니다.");
                    after_tomorrow_area_info.setWidth(after_tomorrow_area_info.getMaxWidth());
                    after_tomorrow_area_info.setGravity(Gravity.CENTER);

                    after_ti[i].setVisibility(View.GONE);
                    after_te[i].setVisibility(View.GONE);
                }else{
                    if(!after_tomorrow_value[i].equals("No Data")){
                        after_te[i].setText(after_tomorrow_value[i]);

                        if(status[i].equals("구름 많음") && i<4){
                            after_ti[i].setImageResource(R.drawable.over_cloudy_day);
                        }else if(status[i].equals("구름 조금") && i<4){
                            after_ti[i].setImageResource(R.drawable.cloudy_day);
                        }else if(status[i].equals("맑음") && i<4){
                            after_ti[i].setImageResource(R.drawable.sunny);
                        }else if(status[i].equals("흐림")){
                            after_ti[i].setImageResource(R.drawable.cloudy);
                        }else if(status[i].equals("비")){
                            after_ti[i].setImageResource(R.drawable.rainy);
                        }else if(status[i].equals("맑음") && i>=4){
                            after_ti[i].setImageResource(R.drawable.sunny_night);
                        }else if(status[i].equals("구름 조금") && i>=4){
                            after_ti[i].setImageResource(R.drawable.cloudy_night);
                        }else if(status[i].equals("구름 많음") && i>=4){
                            after_ti[i].setImageResource(R.drawable.over_cloudy_night);
                        }
                    }else if(after_tomorrow_value[i].equals("")){
                        after_te[i].setVisibility(View.GONE);
                    }
                }
            }


//            if(day_after_tomorrow_info!=""){
//                day_after_tomorrow.setVisibility(View.VISIBLE);
//                day_after_tomorrow.setText("[내일 모레 예상 날씨]"+"\n"+day_after_tomorrow_info);
//            }

//            if(true){
//                for(int i=0; i<day.length; i++){
//                    if(Float.parseFloat(temp[i])>30){
//                        Intent intent=new Intent(getApplicationContext(), Weather_Service.class);
//                        startService(intent);
//                    }
//                }
//            }
        }
    };
}
