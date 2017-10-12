package com.example.development.hems2;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

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

// 외부 디바이스 컨트롤 스레드
/*
    run() 메소드 - 스레드 동작과 관련.
    network() 메소드 - http 통신 관련.
 */

public class Led_control_thread extends Thread {
    private static String TAG = "백 스레드 1 : ";
    private Handler handler;

    int check;
    InputStream inputStream;

    Led_control_thread(Handler handler, int check) {
        this.handler = handler;
        this.check=check;
    }

    Led_control_thread(int check){
        this.check=check;
    }

    @Override
    public void run() {
        if(check==0) {
            while (!isInterrupted()) {
                try {
                    Network();
                    Thread.sleep(1000);
                } catch (Exception e) {
                    Log.e(TAG, "스레드 중지!");
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        }else{
            Network();
        }
    }

    public void Network() {
        String ddd = "";
//        String serverURL = "http://172.20.10.2/Haniem/Led_control.php";
        String serverURL = "http://211.178.109.157/Haniem/Led_control.php";
        try {
            URL url = new URL(serverURL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            String postParameters = "check="+check;

            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setConnectTimeout(10000);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);

            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(postParameters.getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();

//            Log.d(TAG, "POST Transfer Data - " + postParameters);
            int responseStatusCode = httpURLConnection.getResponseCode();

            if (responseStatusCode == httpURLConnection.HTTP_OK) {
                inputStream = httpURLConnection.getInputStream();
            } else {
                inputStream = httpURLConnection.getErrorStream();
            }

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder sb = new StringBuilder();

            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line + "\n");
            }
            bufferedReader.close();

            // 받아온 Json array를 매개변수로 json 객체 생성.
            JSONArray json = new JSONArray(sb.toString());
            for (int i = 0; i < json.length(); i++) {
                JSONObject jOb = json.getJSONObject(i);        // 각각의 json 오브젝트의 키워드 값을 가지고 데이터를 문자열로 변경.
                ddd += jOb.getString("status");
            }

//            Log.d(TAG, "POST response  - ddd" + ddd+", check="+check);

            if(check==1 || check==0){
                Bundle data = new Bundle();
                data.putInt("data", Integer.parseInt(ddd));

                Message msg = handler.obtainMessage();
                msg.setData(data);
                handler.sendMessage(msg);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
