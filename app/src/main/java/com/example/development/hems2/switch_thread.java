package com.example.development.hems2;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Development on 2017-10-06.
 */


public class switch_thread extends Thread {
    String TAG="소켓 스레드 : ";
    private Handler handler=null;
    Socket mSock = null;
    BufferedReader mReader;
    BufferedWriter mWriter;
    String mRecvData = "";

    switch_thread(Handler handler){
        this.handler=handler;
    }

    @Override
    public void run(){

        while(true){

            try{
                mSock = new Socket("211.178.109.157", 84);
                mWriter = new BufferedWriter(new OutputStreamWriter(mSock.getOutputStream()));
                mReader = new BufferedReader(new InputStreamReader(mSock.getInputStream()));
                Log.e(TAG, "제발...");

                PrintWriter out = new PrintWriter(mWriter, true);

                out.println("OK!");

                mRecvData = mReader.readLine();

                Bundle data = new Bundle();
                data.putString("msg", mRecvData);

                Message msg = handler.obtainMessage();
                msg.setData(data);
                handler.sendMessage(msg);

                if( mSock != null ) {
                    // 소켓 접속 종료
                    mSock.close();
                    mSock = null;
                }

                Thread.sleep(2000);

            }catch (Exception e){
                Log.e(TAG, "switch 스레드 중지!!");
                Thread.currentThread().interrupt();
            }
        }
    }
}
