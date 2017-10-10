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
import android.widget.Spinner;
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

    String zoneCode="11110";   //디폴트 - 종로구

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_info);
        today=(TextView) findViewById(R.id.today);
        tomorrow=(TextView) findViewById(R.id.tomorrow);
        day_after_tomorrow=(TextView) findViewById(R.id.day_after_tomorrow);

        weather_thread=new Weather_thread(handler, zoneCode);
        weather_thread.setDaemon(true);
        weather_thread.start();

        final Spinner s1=(Spinner) findViewById(R.id.select_area1);
        final Spinner s2=(Spinner) findViewById(R.id.select_area2);
        final Spinner s3=(Spinner) findViewById(R.id.select_area3);


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
            String data=bd.getString("총결과");
            String today_info=bd.getString("오늘");
            String tomorrow_info=bd.getString("내일");
            String day_after_tomorrow_info=bd.getString("내일모레");
            String area=bd.getString("지역");

            String[] hour=bd.getStringArray("시간");
            String[] day=bd.getStringArray("날짜");
            String[] temp=bd.getStringArray("온도");
            String[] status=bd.getStringArray("날씨상태");
            String[] humi=bd.getStringArray("습도");

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
            tomorrow.setText("[내일 예상 날씨]"+"\n"+tomorrow_info);
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
