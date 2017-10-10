package com.example.development.hems2;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "로그: ";
    private String status="측정중..";

    public Current_Elec_Usage_thread thread1;
    public Day_Elec_value_thread Day_elec_thread;
    public Current_home_temp_thread thread2;
    public Led_control_thread stat_Led_thread;
    public Led_control_thread Led_thread;

    TextView Current_elec_value;
    TextView Day_elec_value;
    TextView weather_info_home_temp;
    TextView weather_info_home_humi;
    TextView inside_status;

    TextView setting_info;

    ImageView led_image;

    private int alarm_point=0;
    private int discomport_level=0;
    private int ex_index=0;

    private float temp=0;
    private float humi=0;

    TextView Current_date_text;
    java.util.Calendar cal;

    // Led 조작 텍스트 변수
    private TextView stat;

    // 설정 - 팝업 설정 유무
    boolean option;
    boolean temp_Service_flag;

    Intent service=null;
    Intent temp_service=null;

    SharedPreferences alarm=null;

    int temp_option=0;
    boolean temp_alarm=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e("onCreate() 호출","onCreate() 호출.");

        setContentView(R.layout.activity_main);

        Current_elec_value=(TextView) findViewById(R.id.Current_elec_value); // 현재 사용량 텍스트 연결
        Day_elec_value=(TextView) findViewById(R.id.Day_elec_value);        // 하루 사용량 텍스트 연결

        led_image=(ImageView) findViewById(R.id.led_image);

        weather_info_home_temp=(TextView) findViewById(R.id.weather_info_temp);
        weather_info_home_humi=(TextView) findViewById(R.id.weather_info_humi);
        Current_date_text=(TextView) findViewById(R.id.Current_date);
        inside_status=(TextView) findViewById(R.id.Inside_status);

        setting_info=(TextView) findViewById(R.id.setting_value_info);

        cal = java.util.Calendar.getInstance();

        Current_date_text.setText(cal.get(Calendar.YEAR)+"년 "+(cal.get(Calendar.MONTH)+1)+"월 "+cal.get(Calendar.DAY_OF_MONTH)+"일");

        // 현재 사용량 스레드 실행
        thread1 =new Current_Elec_Usage_thread(Crruent_elec_handler);
        thread1.setDaemon(true);
        thread1.start();

        // 하루 사용량 스레드 실행
        Day_elec_thread =new Day_Elec_value_thread(Day_ele_value_handler);
        Day_elec_thread.setDaemon(true);
        Day_elec_thread.start();

        // 실시간 온습도 스레드 실행
        thread2 = new Current_home_temp_thread(handler2);
        thread2.setDaemon(true);
        thread2.start();

        // Led 컨트롤 스레드 준비
        stat_Led_thread=new Led_control_thread(Led_handler, 0);
        stat_Led_thread.setDaemon(true);
        stat_Led_thread.start();

        alarm = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        option = alarm.getBoolean("alarmsetting",true);

        service = new Intent(this, MainService.class);
        temp_service=new Intent(this, Inside_Appropriate_temp_Service.class);

        if(option){
            startService(service);
            setting_info.setText("알람 설정됨");
        }else{
            setting_info.setText("알람 해제.");
        }

        // LED On/Off 텍스트 연결
        stat=(TextView) findViewById(R.id.stat);
    }

    //다른 액티비티에 이동했다가 홈화면으로 돌아오면 onResume 실행
    //alarmsettig(프리퍼런스 key value) 체크 해제 유무에 따라 알람서비스 온오프
    @Override
    public void onResume(){
        super.onResume();

        if(option){
            startService(service);
            setting_info.setText("알람 설정됨");
        }else{
            setting_info.setText("알람 해제.");
        }

        Log.e("OnResume() 호츌", "onResume() 호출 됨. option ="+option);

        Thread t=new Thread(){
            @Override
            public void run(){
                while (!isInterrupted()) {
                    Log.e("서비스 실행 여부", ""+isServiceRunningCheck());
                    try {
                        SharedPreferences alarm = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                        option = alarm.getBoolean("alarmsetting",true);
                        //option은 알람 체크 value 값

                        if(option) {
                            //service가 실행되고 있지않은지 확인후 다시 서비스 실행
                            if(!isServiceRunningCheck()) {
                                startService(service);
                                Log.e(TAG, "서비스 시작!!");
                                Thread.sleep(8000);
                            }
                            else if(isServiceRunningCheck()) {
                                Log.e(TAG, "서비스 이미 실행중, 중복실행 방지!!");
                                break;
                            }

                        }
                        else if(!option) {
                            if(!isServiceRunningCheck()) {
                                Log.e(TAG, "서비스 죽어있음, 서비스종료 중복명령 방지!!");
                                break;
                            }
                            // option 값이 false고 isServiceRunningCheck()가 true일 때
                            else if(isServiceRunningCheck()) {
                                stopService(service);
                                Log.e(TAG, "알람 꺼짐, 서비스종료!!");
                                break;
                            }
                        }

                    } catch(InterruptedException e){
                        e.printStackTrace();
                        Log.e("test", "알람 스레드 에러");

                    }
                }
            }
        };
        t.start();

//        Thread temp_service_check=new Thread(){
//            @Override
//            public void run(){
//                String TAG="실시간 온습도 서비스";
//                while (!isInterrupted()) {
//                    Log.e(TAG, ""+tempServiceRunningCheck());
//                    try {
//                        SharedPreferences alarm = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
//                        temp_Service_flag = alarm.getBoolean("temp_setting",true);
//
//                        //service가 실행되고 있지않은지 확인후 다시 서비스 실행
//                        if(temp_Service_flag) {
//                            if(!tempServiceRunningCheck()) {
//                                startService(temp_service);
//                                Log.e(TAG, "서비스 시작!!");
//                                Thread.sleep(8000);
//                            }
//                            else if(tempServiceRunningCheck()) {
//                                Log.e(TAG, "서비스 이미 실행중, 중복실행 방지!!");
//                                break;
//                            }
//
//                        }
//                        else if(!temp_Service_flag) {
//                            if(!tempServiceRunningCheck()) {
//                                Log.e(TAG, "서비스 죽어있음, 서비스종료 중복명령 방지!!");
//                                break;
//                            }
//                            // temp_Service_flag 값이 false고 isServiceRunningCheck()가 true일 때
//                            else if(tempServiceRunningCheck()) {
//                                stopService(temp_service);
//                                Log.e(TAG, "알람 꺼짐, 서비스종료!!");
//                                break;
//                            }
//                        }
//                    } catch(InterruptedException e){
//                        e.printStackTrace();
//                        Log.e("test", "알람 스레드 에러");
//
//                    }
//                }
//            }
//        };
//        temp_service_check.start();
    }

    //서비스 중복실행 확인 메소드
    public boolean isServiceRunningCheck() {
        ActivityManager manager = (ActivityManager) this.getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.example.development.hems2.MainService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    // 온습도 서비스 중복 실행 검사 메소드
    public boolean tempServiceRunningCheck() {
        ActivityManager manager = (ActivityManager) this.getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.example.development.hems2.Inside_Appropriate_temp_Service".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onStart(){
        super.onStart();

    }

    @Override
    public void onStop(){
        super.onStop();

        Log.e("onStop() 호출","onStop() 호출. option ="+option);

    }

    @Override
    public void onPause(){
        super.onPause();

        Log.e("onPause() 호출","onPause() 호출. option ="+option);

    }

    @Override
    public void onRestart(){
        super.onRestart();

        Log.e("onRestart() 호출","onRestart() 호출. option ="+option);

    }

    // 접속 시작
    public void btnConnect(View v){
        Led_thread=new Led_control_thread(Led_handler, 1);
        Led_thread.setDaemon(true);
        Led_thread.start();
        Led_thread.interrupt();
    }

    // "상세 조회" 엑티비티 전환 버튼.
    public void focus_btn(View v){
        Intent intent=new Intent(getApplicationContext(), Focus_search_activity.class);
        startActivity(intent);
    }

    // "날씨 정보" 엑티비티 전환 버튼.
    public void btn_weather(View v){
        Intent intent=new Intent(getApplicationContext(), WeatherInfo_activity.class);
        intent.putExtra("온도",temp);
        intent.putExtra("습도",humi);
        startActivity(intent);
    }

    //"설정" 전환버튼
    public void setting(View v){
        Intent intent=new Intent(getApplicationContext(), PreferenceSetting.class);
        startActivity(intent);
    }

    // "어떻게 절약 할까요?" 버튼 클릭 시 -> 화면 이동.
    public void saveEnergyInfo_btn(View v){
        Intent intent=new Intent(getApplicationContext(), SaveEnergyInfo.class);
        startActivity(intent);
    }

    // 뒤로가기 눌렀을 때.
    @Override
    public void onBackPressed(){

        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("HEMS System")
                .setMessage("앱을 종료합니까?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        thread1.interrupt();
                        thread2.interrupt();
                        Day_elec_thread.interrupt();
                        stat_Led_thread.interrupt();
                        //Led_thread.interrupt();
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    // 현재 사용량 스레드 핸들러
    Handler Crruent_elec_handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            //Log.e(TAG, "현재 사용량 관련 데이터 수신 스레드 실행.");

            Bundle bd=msg.getData();
            String data=bd.getString("data");
            Current_elec_value.setText("현재 사용량 : "+data+" [W]");           // 일 평균 텍스트를 변경한다.
        }
    };

    // 하루 사용량 스레드 핸들러
    Handler Day_ele_value_handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            //Log.e(TAG, "하루 사용량 관련 데이터 수신 스레드 실행.");
            Bundle bd=msg.getData();
            String data=bd.getString("data");
            Day_elec_value.setText("하루 사용량 : "+data+" [KWH]");           // 일 평균 텍스트를 변경한다.
        }
    };

    // LED 조작 관련 스레드 핸들러
    Handler Led_handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            Bundle bd=msg.getData();
            int status=bd.getInt("data");

            Log.e(TAG, "LED 조작 스레드 동작. status : "+status);
            if(status==1){
                stat.setText("LED 켜짐");
                led_image.setImageResource(R.drawable.led_on);
            }else{
                stat.setText("LED 꺼짐");
                led_image.setImageResource(R.drawable.led_off);
            }
        }
    };

    // 실내 온습도 정보를 받는 핸들러.
    // 여기서 "Inside_Appropriate_temp_Service" 를 실행 시키고, temp, humi 를 넘긴다.
    Handler handler2 = new Handler(){
        @Override
        public void handleMessage(Message msg){
            //Log.e(TAG, "온습도 스레드 실행! 호출횟수: "+checkpoint);
            Bundle bd=msg.getData();
            String temp=bd.getString("temp");
            String humi=bd.getString("humi");

            weather_info_home_temp.setText("온도: "+temp+"도");
            weather_info_home_humi.setText("습도: "+humi+"%");

            temp_option = Integer.parseInt(alarm.getString("temp","1"));
            temp_alarm= alarm.getBoolean("temp_setting", true);

            Log.e(TAG, "alarm_point - "+alarm_point+", temp_option - "+temp_option+" ,temp_alarm - "+temp_alarm);

            // 여기서 형변환.
            float temp_f = Float.parseFloat(temp);
            float humi_f= Float.parseFloat(humi);

            //불쾌 지수 공식 : (9/5)T-0.55(1-RH)((9/5)T-26)+32 (T:온도, RH:상대습도)
            // ex) 30도, 67% -> 불쾌지수 : 80.1

            double Discomfort_index=((1.8*temp_f)-(0.55*(1-(humi_f/100))*((1.8*temp_f)-26)))+32;

            if(Discomfort_index>=80){
                // 매우 높음 단계
                discomport_level=4;
                status="매우불쾌";
            }else if(Discomfort_index>=75 && Discomfort_index<80){
                // 높음
                discomport_level=3;
                status="불쾌";
            }else if(Discomfort_index>=68 && Discomfort_index<75){
                // 보통
                discomport_level=2;
                status="보통";
            }else if(Discomfort_index<68){
                // 낮음
                discomport_level=1;
                status="쾌적";
            }

            if(temp_alarm==true){
                // 불쾌 지수가 변동이 있을 경우.
                if(ex_index!=discomport_level){
                    alarm_point++;
                    if(alarm_point==10){
                        // 불쾌 지수가 변동되었을 때, 높아지거나 낮아지거나.
                        // 알림 팝업을 띄운다. + 현재의 불쾌지수 단계에 따른 추천 행동 제시. references 기상청.
                        Intent temp_service=new Intent(MainActivity.this, Inside_Appropriate_temp_Service.class);
                        temp_service.putExtra("temp", temp);
                        temp_service.putExtra("humi", humi);
                        temp_service.putExtra("level", Integer.toString(discomport_level));
                        startService(temp_service);
                    }
                }else{
                    // 1시간 정도를 체크하기 위한 카운터 변수 필요.
                    alarm_point++;
                    if(alarm_point==6){  // 360-> 1시간
                        //알람 발생.
                        Intent temp_service=new Intent(MainActivity.this, Inside_Appropriate_temp_Service.class);
                        temp_service.putExtra("temp", temp);
                        temp_service.putExtra("humi", humi);
                        temp_service.putExtra("level", Integer.toString(discomport_level));
                        startService(temp_service);
                    }
                    // 변동이 없을 때. 즉, 같은 상황 일 때.
                    // 이 상황이 1시간 정도 지속되면 그에 맞는 추전 행동을 제시하는 팝업 띄움.
                }
            }

            inside_status.setText("상태 : "+status);
            ex_index=discomport_level;
        }
    };

}