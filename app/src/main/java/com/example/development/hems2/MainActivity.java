package com.example.development.hems2;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "로그: ";
    private String status="측정중..";

    DecimalFormat form_current_data;
    DecimalFormat current_charge;

    public Current_Elec_Usage_thread thread1;
    public Day_Elec_value_thread Day_elec_thread;
    public Current_home_temp_thread thread2;
    public Led_control_thread stat_Led_thread;
    public Led_control_thread Led_thread;
    public Estimated_charge_thread estimated_charge_thread;

    TextView Current_elec_value;
    TextView Day_elec_value;
    TextView weather_info_home_temp;
    TextView weather_info_home_humi;
    TextView inside_status;

    TextView estimated_charge_text;
    TextView standard_date_today_total;

    private Button btnConnect;

    private int discomport_level=0;

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

    SharedPreferences pref=null;
    SharedPreferences.Editor editor=null;

    DatePickerDialog dpd=null;
    int selected_day=0;
    private Button setting_day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e("onCreate() 호출","onCreate() 호출.");

        setContentView(R.layout.activity_main);

        form_current_data=new DecimalFormat("###.##");

        current_charge=new DecimalFormat("#,###[원]");


        estimated_charge_text=(TextView) findViewById(R.id.estimated_charge_text);
        standard_date_today_total=(TextView) findViewById(R.id.standard_date_today_total);

        Current_elec_value=(TextView) findViewById(R.id.Current_elec_value); // 현재 사용량 텍스트 연결
        Day_elec_value=(TextView) findViewById(R.id.Day_elec_value);        // 하루 사용량 텍스트 연결

        //led_image=(ImageView) findViewById(R.id.led_image);

        weather_info_home_temp=(TextView) findViewById(R.id.weather_info_temp);
        weather_info_home_humi=(TextView) findViewById(R.id.weather_info_humi);
        Current_date_text=(TextView) findViewById(R.id.Current_date);
        inside_status=(TextView) findViewById(R.id.Inside_status);

        //setting_info=(TextView) findViewById(R.id.setting_value_info);

        btnConnect=(Button) findViewById(R.id.btnConnect);

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


        // 예상 요금 계산 기준일을 default 1일로 한다. key 값은 'day'
        pref=getSharedPreferences("Based_day", Activity.MODE_PRIVATE);
        editor=pref.edit();
        selected_day=pref.getInt("day",1);

        // 기준일 버튼
        setting_day=(Button) findViewById(R.id.estimated_charge_day_setting);
        setting_day.setText("기준일 설정 - "+selected_day+"일");

        // 현재까지의 요금 계산하는 스레드 준비
        estimated_charge_thread=new Estimated_charge_thread(Estimated_handler, selected_day, MainActivity.this);
        estimated_charge_thread.setDaemon(true);
        estimated_charge_thread.start();
    }

    //다른 액티비티에 이동했다가 홈화면으로 돌아오면 onResume 실행
    //alarmsettig(프리퍼런스 key value) 체크 해제 유무에 따라 알람서비스 온오프
    @Override
    public void onResume(){
        super.onResume();

        Log.e("OnResume() 호츌", "onResume() 호출 됨. option ="+option);

        Thread t=new Thread(){
            @Override
            public void run(){
                while (!isInterrupted()) {
                    Log.e("메인 서비스 실행 여부", "★"+isServiceRunningCheck());
                    try {
                        SharedPreferences alarm = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                        option = alarm.getBoolean("alarmsetting",true);
                        //option은 알람 체크 value 값

                        if(option) {
                            //service가 실행되고 있지않은지 확인후 다시 서비스 실행
                            if(!isServiceRunningCheck()) {
                                startService(service);
                                Log.e("메인 서비스", "★서비스 시작!!");
                                Thread.sleep(8000);
                            }
                            else if(isServiceRunningCheck()) {
                                Log.e("메인 서비스", "★메인 서비스 이미 실행중, 중복실행 방지!!");
                                break;
                            }

                        }
                        else if(!option) {
                            if(!isServiceRunningCheck()) {
                                Log.e("메인 서비스", "★메인 서비스 죽어있음, 서비스종료 중복명령 방지!!");
                                break;
                            }
                            // option 값이 false고 isServiceRunningCheck()가 true일 때
                            else if(isServiceRunningCheck()) {
                                stopService(service);
                                Log.e("메인 서비스", "★메인 서비스 알람 꺼짐, 서비스종료!!");
                                break;
                            }
                        }

                    } catch(InterruptedException e){
                        e.printStackTrace();
                        Log.e("메인 서비스", "★메인 서비스 알람 스레드 에러");

                    }
                }
            }
        };
        t.start();


        Thread temp_service_check=new Thread(){
            @Override
            public void run(){
                String TAG="실시간 온습도 알람 서비스";
                while (!isInterrupted()) {
                    Log.e(TAG, "◆"+tempServiceRunningCheck());

                    try {
                        SharedPreferences alarm = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                        temp_Service_flag = alarm.getBoolean("temp_setting",true);

                        //service가 실행되고 있지않은지 확인후 다시 서비스 실행
                        if(temp_Service_flag && (option==true)) {
                            if(!tempServiceRunningCheck()) {
                                startService(temp_service);
                                Log.e(TAG, "◆실시간 온습도 알람 서비스 시작!!");
                                Thread.sleep(8000);

                            } else if(tempServiceRunningCheck()) {
                                Log.e(TAG, "◆서비스 이미 실행중, 중복실행 방지!!");
                                break;
                            }

                        }
                        else if(!temp_Service_flag || (option==false)) {
                            if(!tempServiceRunningCheck()) {
                                Log.e(TAG, "◆서비스 죽어있음, 서비스종료 중복명령 방지!!");
                                break;

                            }else if(tempServiceRunningCheck()) {
                                // temp_Service_flag 값이 false고 isServiceRunningCheck()가 true일 때
                                stopService(temp_service);
                                Log.e(TAG, "◆알람 꺼짐, 서비스종료!!");
                                break;
                            }
                        }

                    } catch(InterruptedException e){
                        e.printStackTrace();
                        Log.e(TAG, "◆알람 스레드 에러");

                    }
                }
            }
        };
        temp_service_check.start();
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
    // hello!
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

    // 예상 요금 계산의 기준일 설정 버튼
    public void estimated_charge_day_setting(View v){
        Calendar c = Calendar.getInstance();
        int cyear = c.get(Calendar.YEAR);
        int cmonth = c.get(Calendar.MONTH);
        int today=c.get(Calendar.DATE);
        int cday = selected_day;
        DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                //날짜가 선택 된 이후 할일
                //ex.텍스트뷰에 날짜를 표시등등

                // 선택한 날짜를 저장
                editor.putInt("day",dayOfMonth);
                editor.commit();

                selected_day=pref.getInt("day",1);

                Toast.makeText(getApplicationContext(), "선택한 day 확인 : "+pref.getInt("day",100), Toast.LENGTH_SHORT).show();
                setting_day.setText("기준일 설정 - "+selected_day+"일");
                estimated_charge_thread.interrupt();

                estimated_charge_thread=new Estimated_charge_thread(Estimated_handler, selected_day, MainActivity.this);
                estimated_charge_thread.setDaemon(true);
                estimated_charge_thread.start();

            }
        };
        dpd = new DatePickerDialog(this, mDateSetListener, cyear,
                cmonth, cday-1);

        dpd.getDatePicker().init(cyear, cmonth, cday, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                dpd.setTitle("기준일 선택");
            }
        });

        Calendar calendar = Calendar.getInstance();
//        현재 년도 표현을 max로 설정하여 현재 년도 이후 년도는 나오지 않도록 설정
        dpd.getDatePicker().setMaxDate(calendar.getTimeInMillis()); // 날짜의 최대 = 오늘
        Calendar cal = Calendar.getInstance();
//        cal.set(Calendar.YEAR, 2000);
        cal.set(Calendar.DATE, 2);
//        2000년를 min으로 설정하여 2000년 이전 년도는 나오지 않도록 설정
        dpd.getDatePicker().setMinDate(cal.getTimeInMillis());  // 날짜의 최소 값
        dpd.setTitle("기준일 선택");

        try {
            java.lang.reflect.Field[] datePickerDialogFields = dpd.getClass().getDeclaredFields();
            for (java.lang.reflect.Field datePickerDialogField : datePickerDialogFields) {
//                Log.e("date",""+datePickerDialogFields[0].getName());
//                Log.e("date",""+datePickerDialogFields[1].getName());
//                Log.e("date",""+datePickerDialogFields[2].getName());
//                Log.e("date",""+datePickerDialogFields[3].getName());
//                Log.e("date",""+datePickerDialogFields[4].getName());
//                Log.e("date",""+datePickerDialogFields[5].getName());

                if (datePickerDialogField.getName().equals("mDatePicker")) {
                    datePickerDialogField.setAccessible(true);

                    DatePicker datePicker = (DatePicker) datePickerDialogField.get(dpd);
                    java.lang.reflect.Field[] datePickerFields = datePickerDialogField.getType().getDeclaredFields();
                    for (java.lang.reflect.Field datePickerField : datePickerFields) {
                        // Log.i("test", datePickerField.getName());
                        if (android.os.Build.VERSION.SDK_INT >= 21) {
                            int yearSpinnerId = Resources.getSystem().getIdentifier("year", "id", "android");
                            if (yearSpinnerId != 0) {
                                View yearSpinner = datePicker.findViewById(yearSpinnerId);
                                if (yearSpinner != null) {
                                    yearSpinner.setVisibility(View.GONE);
                                }
                            }
                            int MonthSpinnerId = Resources.getSystem().getIdentifier("month", "id", "android");
                            if (MonthSpinnerId != 0) {
                                View monthSpinner = datePicker.findViewById(MonthSpinnerId);
                                if (monthSpinner != null) {
                                    monthSpinner.setVisibility(View.GONE);
                                }
                            }
                        } else {
                            if ("mYearSpinner".equals(datePickerField.getName()) || "mYearSpinner".equals(datePickerField.getName()) || "mMonthPicker".equals(datePickerField.getName())
                                    || "mMonthSpinner".equals(datePickerField
                                    .getName())) {
                                datePickerField.setAccessible(true);
                                Object dayPicker = new Object();
                                dayPicker = datePickerField.get(datePicker);
                                ((View) dayPicker).setVisibility(View.GONE);
                            }
                        }
                    }
                }
            }
        }catch (Exception ex){

        }

        dpd.show();
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
                        estimated_charge_thread.interrupt();
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
            Log.v("[현재 사용량 스레드 동작]", "현재 사용량 관련 데이터 수신 스레드 실행.");

            Bundle bd=msg.getData();
            String data=bd.getString("data");

            Current_elec_value.setText(data+"[W]");           // 일 평균 텍스트를 변경한다.
        }
    };

    // 현재 요금 계산 스레드 핸들러
    Handler Estimated_handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            Log.v("[오늘 까지 요금 스레드]", "요금 계산 스레드 실행.");

            Bundle bd=msg.getData();
            int charge=bd.getInt("charge");
            int total=bd.getInt("total");

            estimated_charge_text.setText("실시간 요금 : "+current_charge.format(charge));
            standard_date_today_total.setText(selected_day+"일~"+cal.get(Calendar.DATE)+"일 사용량 : "+total+"[Kwh]");
        }
    };

    // 하루 사용량 스레드 핸들러
    Handler Day_ele_value_handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            Log.v("[하루 사용량 스레드 동작]", "하루 사용량 관련 데이터 수신 스레드 실행.");
            Bundle bd=msg.getData();
            String data=bd.getString("data");

            float value=Float.parseFloat(data);

//            Log.v(TAG, ""+value);

            String final_value=form_current_data.format(value);

//            Log.v(TAG, final_value);

            Day_elec_value.setText(final_value+" [KWH]");           // 일 평균 텍스트를 변경한다.
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
                btnConnect.setBackgroundResource(R.drawable.ledon);
            }else{
                btnConnect.setBackgroundResource(R.drawable.ledoff);
            }
        }
    };

    // 실내 온습도 정보를 받는 핸들러.
    // 여기서 "Inside_Appropriate_temp_Service" 를 실행 시키고, temp, humi 를 넘긴다.
    Handler handler2 = new Handler(){
        @Override
        public void handleMessage(Message msg){
            Log.e("[온습도 스레드]", "온습도 스레드 실행!");
            Bundle bd=msg.getData();
            String temp=bd.getString("temp");
            String humi=bd.getString("humi");

            weather_info_home_temp.setText(temp+"℃");
            weather_info_home_humi.setText(humi+"%");

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

            inside_status.setText(status);
        }
    };

}