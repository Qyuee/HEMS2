package com.example.development.hems2;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

// 현재의 온도 값을 바탕으로 실내 적정 온도를 위한 행동을 추천하는 팝업 및 알림을 제공.

public class Inside_Appropriate_temp_Service extends Service {
    float temp=0;
    float humi=0;
    int alarm_point=0;
    Current_HomeTemp_Thread_for_Service t=null;

    public Inside_Appropriate_temp_Service() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate(){
        t=new Current_HomeTemp_Thread_for_Service();
        t.setDaemon(true);
        t.start();

        super.onCreate();
    }


    //***********************************************************************************************************************
    //***********************************************************************************************************************

    class Current_HomeTemp_Thread_for_Service extends Thread {
        private String TAG = " 실내 온,습도 정보 백 스레드 : ";

        private Handler handler;

        ArrayList<String> temp_data=new ArrayList<>();
        ArrayList<String> humi_data=new ArrayList<>();

        InputStream inputStream;

        boolean option=true;
        int temp_option=0;
        boolean temp_alarm=true;

        Current_HomeTemp_Thread_for_Service() {
        }

        @Override
        public void run() {
            while (!isInterrupted()) {
                try {
                    Log.v("실시간 온습도 알림 서비스","나 살아 있어!!");

                    SharedPreferences alarm= PreferenceManager.getDefaultSharedPreferences(Inside_Appropriate_temp_Service.this);
                    option = alarm.getBoolean("alarmsetting",true);                   // 전역 알람 변수
                    temp_option = Integer.parseInt(alarm.getString("temp","360"));    // 알람 주기 변수
                    temp_alarm= alarm.getBoolean("temp_setting", true);               // 알람 선택 여부 변수

                    Network(); // temp와 humi 값을 서버로 부터 받아온다. 또한 내부적으로 MainActivity에 핸들러를 통해 값을 전달한다.

                    // 만약 알람이 설정되어 있다면 불쾌지수를 구하고 팝업을 출력할 준비를 한다.
                    if(temp_alarm==true && option==true){

                        alarm_point++;     // 알람 포인트를 1 증가. -> 10초에 1증가.

                        Log.e("실시간 온습도 알림 서비스", "알람 설정 여부 = "+temp_alarm+", 알람 주기 = "+temp_option+", alarm_point = "+alarm_point);
                        Log.v("실시간 온습도 알림 서비스","경과 시간 = "+alarm_point*10+"[초]");

                        int level=Discomport_Level(temp, humi);                         // 구한 temp와 humi를 바탕으로 불쾌지수를 계산.

                        if(level==4 && (alarm_point==temp_option)){
                            // 실내 온도가 매우 불안정하여 불쾌지수 상승.
                            String status="[불쾌 지수] - 매우 높음";
                            String coments1="1. 어린이, 노약자 등 충분한 수분 섭취. 야외 활동을 자제."+"\n"+"2. 에어컨, 제습기 등을 이용해 실내 온습도를 조절 필요."+"\n"+"3. 에어컨 등이 없을 경우 무더위 쉼터로 이동";
                            Notificattion_temp(status, temp, humi, coments1);
                            alarm_point=0;
                        }else if(level==3 && (alarm_point==temp_option)){
                            // 50%정도가 불쾌감을 느낌.
                            String status="[불쾌 지수] - 높음";
                            String coments1="1. 어린이, 노약자 등 12~17시 야외 활동을 자제 및 가벼운 옷 착용 요망."+"\n"+" 2. 에어컨, 제습기를 통해 실내 온습도 조절";
                            Notificattion_temp(status, temp, humi, coments1);
                            alarm_point=0;
                        }else if(level==2 && (alarm_point==temp_option)){
                            // 불쾌감이 서서히 나타나고 느껴짐.
                            String status="[불쾌 지수] - 보통";
                            String coments1="1. 노약자, 어린이 등 더위에 취약한 사람들은 가벼운 옷 차림을 요망";
                            Notificattion_temp(status, temp, humi, coments1);
                            alarm_point=0;
                        }else if(level==1 && (alarm_point==temp_option)){
                            // 전원 쾌적함을 느낀다. 에어컨 및 제습기 등의 사용을 자제해도 된다.
                            String status="[불쾌 지수] - 낮음";
                            String coments1="1. 실내 쾌적. 에어컨 및 제습기 등의 사용을 자제";
                            Notificattion_temp(status, temp, humi, coments1);
                            alarm_point=0;
                        }

                    }else if(temp_alarm==false || option==false){
                        // 서비스 중지. temp_alarm이 true이 되기 전까지 죽는다.
                        // 서비스의 활동 유무는 MainActivity에서 판단.
                        break;
                    }

                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.e(TAG, "스레드 중지!!");
                }
            }
        }

        public void Network() {
            String ddd = "";

//            String serverURL = "http://172.20.10.2/Haniem/Current_search_temp.php";
            String serverURL = "http://211.178.109.157/Haniem/Current_search_temp.php";
            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                String postParameters = "check="+1;

                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setReadTimeout(15000);
                httpURLConnection.setConnectTimeout(15000);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                //Log.e(TAG, "POST Transfer Data - " + postParameters);
                int responseStatusCode = httpURLConnection.getResponseCode();

                if (responseStatusCode == httpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder sb2 = new StringBuilder();

                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    sb2.append(line + "\n");
                }

                bufferedReader.close();

                // 받아온 Json array를 매개변수로 json 객체 생성.
                JSONArray json2=new JSONArray(sb2.toString());
                for(int i=0; i<json2.length(); i++) {
                    JSONObject jOb=json2.getJSONObject(i);
                    ddd +=jOb.getString("temp")+ "," +jOb.getString("humi");
                }

                String[] parse_data = ddd.split(",");   //문자열을 \n을 기준으로 문자열 배열로 나눈다.

                for(int i=0; i<parse_data.length; i++){
                    temp=Float.parseFloat(parse_data[0]);
                    humi=Float.parseFloat(parse_data[1]);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    // 불쾌 지수 계산 메소드.
    public int Discomport_Level(float temp, float humi){
        int discomport_level=1;

        float temp_f=temp;
        float humi_f=humi;

        //불쾌 지수 공식 : (9/5)T-0.55(1-RH)((9/5)T-26)+32 (T:온도, RH:상대습도)
        // ex) 30도, 67% -> 불쾌지수 : 80.1

        double Discomfort_index=((1.8*temp_f)-(0.55*(1-(humi_f/100))*((1.8*temp_f)-26)))+32;

        if(Discomfort_index>=80){
            // 매우 높음 단계
            discomport_level=4;
        }else if(Discomfort_index>=75 && Discomfort_index<80){
            // 높음
            discomport_level=3;
        }else if(Discomfort_index>=68 && Discomfort_index<75){
            // 보통
            discomport_level=2;
        }else if(Discomfort_index<68){
            // 낮음
            discomport_level=1;
        }

        return discomport_level;
    }

    //***********************************************************************************************************************
    //***********************************************************************************************************************

    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {

        return super.onStartCommand(intent, flags, startid);
    }

    // 서비스 종료시 호출.
    @Override
    public void onDestroy(){
        super.onDestroy();
        t.interrupt();
        Log.e("실시간 온습도 알림 서비스", "onDestroy 동작 - 서비스 종료");
    }

    public void Notificattion_temp(String status, float temp, float humi, String coments1){
        Resources res=getResources();

        Intent notificationIntent=new Intent(this, MainActivity.class);
        PendingIntent conPendingIntent=PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this);
        builder.setContentTitle("[실내 온도 알리미]")
                .setContentText("실내 온도 : "+temp+" 습도 : "+humi +"\n 더 보기")
                .setTicker("[실내 온도 알리미]")
                .setSmallIcon(R.drawable.smart_home_icon)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.smart_home_icon))
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setDefaults(android.app.Notification.DEFAULT_ALL);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            builder.setCategory(android.app.Notification.CATEGORY_MESSAGE)
                    .setPriority(android.app.Notification.PRIORITY_HIGH)
                    .setVisibility(android.app.Notification.VISIBILITY_PUBLIC);
        }

        NotificationCompat.BigTextStyle bigTextStyle=new NotificationCompat.BigTextStyle(builder);
        bigTextStyle.setBigContentTitle("[실내 온도 알리미]");
        bigTextStyle.bigText("실내 온도 : "+temp+", 습도 : "+humi+"\n"+status+"\n"+coments1);
        builder.setStyle(bigTextStyle);

        NotificationManager nm=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(0,builder.build());
    }
}
