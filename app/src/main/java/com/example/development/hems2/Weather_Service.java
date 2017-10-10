package com.example.development.hems2;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;


// 다음날 12~18시 사이의 기상청 온도를 바탕으로 전력 소비 주의를 사용자에게 알려주는 팝업을 띄운다.
public class Weather_Service extends Service {
/*
*   서비스의 생명 주기
*   UnBind 서비스 : onCreate() -> onStartCommand() -> [Service 수행] -> onDestroy()
*   Bind 서비스 : onCreate() -> onBind() -> onUnbind() -> onReBind() -> onDestroy()
*                                          |                  |
*                                          |---<----<----<----|
*/

    @Override
    public IBinder onBind(Intent intent) {
        // Service 객체와 (화면단 Activity 사이에서)
        // 통신 할 때 사용하는 메소드.
        // 데이터를 전달 할 필요 없으며 return null;
        Toast.makeText(getApplicationContext(), "onBind() 메소드 호출", Toast.LENGTH_SHORT).show();
        return null;
    }

    // 서비스에서 가장 먼저 호출됨. ( 최초에 한번만 실행 )
    @Override
    public void onCreate(){
        super.onCreate();
        Toast.makeText(getApplicationContext(), "onCreate() 메소드 호출", Toast.LENGTH_SHORT).show();
    }

    // 서비스가 호출 될 때 실행. --> 실질적인 동작하는 내용의 위치.
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        float temp=Float.parseFloat(intent.getStringExtra("온도값"));
        float humi=Float.parseFloat(intent.getStringExtra("습도값"));
        String hour=intent.getStringExtra("시간값");
        String day=null;
        if(intent.getStringExtra("날짜").equals("0")){
            day="오늘";
        }else if(intent.getStringExtra("날짜").equals("1")){
            day="내일";
        }else if(intent.getStringExtra("날짜").equals("2")){
            day="내일 모레";
        }
        String[] coments=new String[5];
        coments[0]="폭염이 예상됩니다. 전력 사용에 주의하세요.";
        coments[1]="열대야가 예상 됩니다. 전력 사용에 관심을 가져주세요.";
        coments[2]="서늘한 날씨 입니다. 에어컨이 필요없겠어요!";
        coments[3]="많이 건조하겠네요. 가습기가 필요하겠는데요?";
        coments[4]="외출 하실때 콘센트는 모두 빼셨나요?";

        Toast.makeText(getApplicationContext(), "시간: "+hour+" temp="+temp+" humi="+humi, Toast.LENGTH_SHORT).show();

        Notificattion_weather(day,hour, temp, humi);
        stopSelf(startId);          // -> 스스로 startId 값을 가지고 종료를 한다.
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Toast.makeText(getApplicationContext(), "onDestroy() 메소드 호출", Toast.LENGTH_SHORT).show();
    }

    public void Notificattion_weather(String day, String when, float temp, float humi){
        Resources res=getResources();

        Intent notificationIntent=new Intent(this, WeatherInfo_activity.class);
        PendingIntent conPendingIntent=PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder=new NotificationCompat.Builder(this);
        builder.setContentTitle(day+" "+when+ " 폭염이 예상 됩니다.")
                .setContentText("온도 : "+temp+" 습도 : "+humi+"\n"+"열대야와 폭염을 염두하여 전기사용량을 확인하세요.")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setDefaults(android.app.Notification.DEFAULT_ALL);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            builder.setCategory(android.app.Notification.CATEGORY_MESSAGE)
                    .setPriority(android.app.Notification.PRIORITY_HIGH)
                    .setVisibility(android.app.Notification.VISIBILITY_PUBLIC);
        }

        NotificationManager nm=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(1234,builder.build());
    }
}
