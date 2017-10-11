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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class WeatherInfo_activity extends AppCompatActivity {
    private String Tag="날씨 정보 스레드 : ";

    ArrayAdapter<CharSequence> adapter1, adapter2, adapter3;
    TextView today;
    TextView tomorrow;
    TextView day_after_tomorrow;
    Weather_thread weather_thread;

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

    String zoneCode="11110";   //디폴트 - 종로구

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_info);
        today=(TextView) findViewById(R.id.today);
        //tomorrow=(TextView) findViewById(R.id.tomorrow);
        day_after_tomorrow=(TextView) findViewById(R.id.day_after_tomorrow);

        weather_thread=new Weather_thread(handler, zoneCode);
        weather_thread.setDaemon(true);
        weather_thread.start();

        final Spinner s1=(Spinner) findViewById(R.id.select_area1);
        final Spinner s2=(Spinner) findViewById(R.id.select_area2);
        final Spinner s3=(Spinner) findViewById(R.id.select_area3);

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
        Toast.makeText(getApplicationContext(), zoneCode, Toast.LENGTH_SHORT).show();
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


            today.setText("[오늘 예상 날씨]"+area+"\n"+today_info);
           // tomorrow.setText("[내일 예상 날씨]"+"\n"+tomorrow_info);

            String[] tomorrow_value=new String[8];
            String tomorrow_temp_max="";
            String tomorrow_temp_min="";
            int tomorrow_index=0;

//            for(int i=0; i<day.length; i++){
//                if(day[i].equals("1")){
//                    tomorrow_temp_max=temp_max[i];
//                    tomorrow_temp_min=temp_min[i];
//                    tomorrow_area_info.setText("내일 날씨 - 최고:"+tomorrow_temp_max+"℃"+" 최저:"+tomorrow_temp_min+"℃");
//                    break;
//                }
//            }

            for(int i=0; i<day.length; i++){
                if(day[i].equals("1")){
                    tomorrow_value[tomorrow_index]=www[i][1] + "  온도 : "+www[i][2]+"℃" + "  습도 : "+www[i][4]+"%";
                    tomorrow_index++;
               }
            }

            for(int i=0; i<8; i++){
                te[i].setText(tomorrow_value[i]);

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
            }

            day_after_tomorrow.setText("[내일 모레 예상 날씨]"+"\n"+day_after_tomorrow_info);

            for(int i=0; i<hour.length; i++){
                if(Float.parseFloat(www[i][2])>=30){
                    Intent intent=new Intent(getApplicationContext(), Weather_Service.class);
                    intent.putExtra("날짜", www[i][0]);
                    intent.putExtra("온도값", www[i][2]);
                    intent.putExtra("습도값", www[i][4]);
                    intent.putExtra("시간값", www[i][1]);
                    startService(intent);
                    break;
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
