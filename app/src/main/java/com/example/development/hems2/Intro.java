package com.example.development.hems2;



import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

public class Intro extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences alarm = PreferenceManager.getDefaultSharedPreferences(Intro.this);
                boolean option = alarm.getBoolean("logout",true);

                if(option==true){
                    Intent intent = new Intent(Intro.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }else{
                    Intent intent = new Intent(Intro.this, LoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }

                //뒤로가기 했을 때 다시 안나오게 >>finish!!
                finish();
            }
        },2000);
    }
}

