package com.example.development.hems2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
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
import java.util.Calendar;

/**
 * Created by Development on 2017-10-14.
 */

public class Estimated_charge_thread extends Thread {
    private static String TAG = "오늘까지 예상 요금";
    private Handler handler;
    private Context context;

    int total=0;
    int charge=0;

    private ArrayList id=new ArrayList();
    private ArrayList date=new ArrayList();
    private ArrayList avgs=new ArrayList();

    Calendar c = Calendar.getInstance();
    int year=c.get(Calendar.YEAR);
    int month=c.get(Calendar.MONTH)+1;
    int today=c.get(Calendar.DATE);     // 오늘 날짜
    int estimated_day=0;

    Estimated_charge_thread(Handler handler, int estimated_day, Context context){
        this.handler=handler;
        this.context=context;
        this.estimated_day=estimated_day;
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

    public void Network(){
        String ddd="";

        String serverURL = "http://211.178.109.157/Haniem/Estimated_charge.php";
        String postParameters = "first_Year=" + year + "&first_Month=" + month+"&first_Date="+estimated_day;
        postParameters+="&second_Year="+year+"&second_Month="+month+"&second_Date="+today;

        try{
            URL url=new URL(serverURL);
            HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();

            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setReadTimeout(5000);
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);

            OutputStream outputStream=httpURLConnection.getOutputStream();
            outputStream.write(postParameters.getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();

            Log.d(TAG, " 전송한 데이터(파라미터) - " + postParameters);

            InputStream inputStream;
            int responseStatusCode = httpURLConnection.getResponseCode();
            if(responseStatusCode==httpURLConnection.HTTP_OK){
                inputStream = httpURLConnection.getInputStream();
            }else{
                inputStream = httpURLConnection.getErrorStream();
            }

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder sb = new StringBuilder();

            String line =null;
            while((line = bufferedReader.readLine()) != null){
                sb.append(line+"\n");
            }
            bufferedReader.close();

            JSONArray json=new JSONArray(sb.toString());
            for(int i=0; i<json.length(); i++) {
                JSONObject jOb=json.getJSONObject(i);
                ddd += jOb.getString("total");
            }

            total=Math.round(Float.parseFloat(ddd));

            Log.d(TAG, " 전송 받은 데이터 - " + ddd);
            Log.e(TAG, " 전송 받은 데이터 변환 - " + total);

            charge=calc(total);

            Bundle data = new Bundle();
            data.putInt("charge", charge);
            data.putInt("total",total);

            Message msg = handler.obtainMessage();
            msg.setData(data);
            handler.sendMessage(msg);

        }catch (IOException e){
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public int calc(int total){
        int usage=total;              // 사용량
        int electricity_rate=0; // 전력량 요금
        int elec_base=0;
        int charge_sum=0;     // 사용총 요금
        int vat=0;            // 부가가치세
        int Base_rate=910;       // 기본료
        int Industry_Based_Fund=0;  // 전력 산업 기반기금
        int Guarantee_deduction=0;


        // ex) 430 Kwh
        // 0~200Kwh 일때.   - 1단계
        // 200Kwh ~ 400Kwh  - 2단계
        // 400Kwh 초과      - 3단계

        if(usage<=200){  // 1단계
            if(usage<=43){
                elec_base=1000;
            }else{
                Base_rate=910;
                Guarantee_deduction=4000;
                electricity_rate=(int)(usage*93.3);
                elec_base=Base_rate+electricity_rate-Guarantee_deduction;
            }
        }else if(usage>200 && usage<=400){
            Base_rate=1600;
            electricity_rate=(int)(200*93.3+((usage-200)*187.9));
            elec_base=Base_rate+electricity_rate;

        }else if(usage>400){
            Base_rate=7300;
            electricity_rate=(int)((200*93.3)+(200*187.9)+((usage-400)*280.6));
            elec_base=Base_rate+electricity_rate;
        }

        vat=(int)(elec_base*0.1);
        Industry_Based_Fund=(int)(elec_base*0.037);

        charge_sum=vat+elec_base+Industry_Based_Fund;

        charge_sum=(charge_sum/10)*10;

        return charge_sum;
    }
}