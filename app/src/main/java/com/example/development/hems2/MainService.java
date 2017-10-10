package com.example.development.hems2;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainService extends Service {
    private static String TAG = "phptest_MainActivity";

    Integer electNum;
    String Data;
    Integer DBnum;
    int i=0;

    int alarm_point=0;
    boolean push;

    thread t;
    boolean option;

    Led_control_thread led_thread;

    public MainService() {

    }

    @Override
    public void onCreate() {
        Log.v("MainService ", "onCreate() 호출");

        t=new thread();
        t.start();
        //t.interrupt();

        super.onCreate();
    }

    class thread extends Thread{
        @Override
        public void run(){
            while (!isInterrupted()) {
                try {
                    SharedPreferences alarm = PreferenceManager.getDefaultSharedPreferences(MainService.this);
                    option = alarm.getBoolean("alarmsetting",true);

                    SharedPreferences prefer = PreferenceManager.getDefaultSharedPreferences(MainService.this);
                    String elecsetting = prefer.getString("edittextvalue", "1");
                    electNum=Integer.parseInt(elecsetting);

                    GetData task = new GetData();
                    task.execute("http://211.178.109.157/Haniem/setting.php");
                    i++;
                    Log.v("MainService ", "data 받기" +i);

                    //true 이면 DB값이 설정한 값을 넘었다는 뜻
                    if(push==true && alarm_point==0){
                        alarm_point=1;
                        getMassage();
                        Log.v("MainService ", "노티피케이션 발생");

                        Thread.sleep(5000);
                        //최초 알림 발생후 5초간 sleep
                        // (주의, 정확히 1시간인지 테스트 안해봄)
                    }
                    else if(push==false) {
                        Log.v("MainService ", "DB 아직 안넘음 or 알림 이미 발생함.");
                    }

                    if(option==false){
                        break;
                    }

                    Thread.sleep(5000);
                } catch(InterruptedException e){
                    e.printStackTrace();
                    Log.v("MainService ", "예외");
                }
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("MainService ", "onStartCommand() 호출");
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        t.interrupt();

        Log.e("onDestroy", "서비스 종료");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private class GetData extends AsyncTask<String,Void,String> {

        String errorString = null;

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Data=result;
            DBnum=Integer.parseInt(Data);

            Log.v("MainService","받은 수치 : "+Data);
            Log.v("MainService","설정된 수치 : "+electNum);

            // 알람이 이미 생성된 상태고 DB값이 설정 값보다 작다는 것은 재설정 되었다는 뜻.
            if(alarm_point==1 && DBnum<electNum){
                alarm_point=0;
            }

            // 문자열 elecsetting -> 정수형으로 변환.
            // Integer.parseInt(String s) -> 변환 메소드 입니다.

            // if문으로 DB에 저장된 값과 사용자가 설정한 전기사용량보다 큰지 확인
            // electNum - 사용자 설정 값.
            // DBnum - 데이터 베이스 값.
            if(electNum<DBnum) { //여기 조건문에 비교대상으로 넣어주면 되요!
                push = true;
            }
            else if(electNum>DBnum){
                push= false;
                Log.v("MainService ", "아직 안넘음");
            }
            else
                Log.v("MainService ","잠시만 기다려주세요");
        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];

            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.connect();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }

                bufferedReader.close();


                return sb.toString().trim();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }
    private void getMassage(){

        NotificationCompat.Builder mBuilder = createNotification();
        mBuilder.setContentIntent(createPendingIntent());
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }


    /**
     * 노티피케이션 빌드
     * @return
     */

    //activity  중첩!! 앱 실행중이다가 팝업 누르면 또 같은 앱 페이지 발생
    // manifest에 액티비티랑 서비스마다    android:launchMode="singleTask"   추가해주면 해결됨

    private PendingIntent createPendingIntent(){
        Intent resultIntent = new Intent(this, PreferenceSetting.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(PreferenceSetting.class);
        stackBuilder.addNextIntent(resultIntent);

        return stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
    }


    private NotificationCompat.Builder createNotification(){

        int check=2;
        led_thread=new Led_control_thread(check);
        led_thread.setDaemon(true);
        led_thread.start();

        led_thread.interrupt();

        //alarm 소리 진동 무음 설정 value는 "alram"
        SharedPreferences alarmsound = PreferenceManager.getDefaultSharedPreferences(MainService.this);
        String soundsetting = alarmsound.getString("alarm", "");

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.smart_home_icon);
        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.smart_home)
                .setLargeIcon(icon)
                .setContentTitle("전기 사용량이 설정한 수치를 넘었습니다.")
                .setContentText("전기 사용량 목표치를 재설정 해주세요")
                .setSmallIcon(R.drawable.smart_home)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis());

        //res 폴더->raw 폴더  mp3 추가, values -> array 폴더 0,1,2 일치하는지 확인
        //  핸드폰 기본 설정 무음 소리 진동에 따라서 어플 설정 무시될수도 있음

        Uri alarmSound = Uri.parse("android.resource://com.example.development.hems2/"+ R.raw.circles);

        // alarm 값 0이면 무음
        if(soundsetting.equals("0")){
            builder.setVibrate(new long[]{0, 0});
        }

        // alarm 값 1이면 소리
        if(soundsetting.equals("1")) {
            builder.setSound(alarmSound);
        }

        // alarm 값 2이면 진동
        if(soundsetting.equals("2")) {
            builder.setVibrate(new long[]{0, 500});
        }
        // 여기까지 추가됨

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            builder.setCategory(Notification.CATEGORY_MESSAGE)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setVisibility(Notification.VISIBILITY_PUBLIC);
        }return builder;
    }
}
