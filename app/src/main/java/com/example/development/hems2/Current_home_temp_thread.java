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
import java.util.ArrayList;

/**
 * Created by Development on 2017-08-03.
 */

public class Current_home_temp_thread extends Thread {
    private static String TAG = " 실내 온,습도 정보 백 스레드 : ";
    private Handler handler;

    ArrayList<String> temp_data=new ArrayList<>();
    ArrayList<String> humi_data=new ArrayList<>();

    InputStream inputStream;


    Current_home_temp_thread(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        while (isInterrupted() == false) {
            try {
                Network();
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                //e.printStackTrace();
                Log.e(TAG, "스레드 중지!!");
                Thread.currentThread().interrupt();
            }
        }
    }

    public void Network() {
        String ddd = "";
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
                temp_data.add(parse_data[0]);
                humi_data.add(parse_data[1]);
            }


            //Log.e(TAG, "POST response  - " + ddd);

            Bundle data = new Bundle();
            data.putString("temp", parse_data[0]);
            data.putString("humi", parse_data[1]);

            Message msg = handler.obtainMessage();
            msg.setData(data);
            handler.sendMessage(msg);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
