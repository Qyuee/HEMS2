package com.example.development.hems2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class JoinActivity extends AppCompatActivity {
    final Context context = this;//이거 onPostExecute부분에서 필요한 것이었음

    EditText et_id, et_pw, et_pw_chk;
    String sId, sPw, sPw_chk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        et_id = (EditText) findViewById(R.id.IdText);
        et_pw = (EditText) findViewById(R.id.PasswordText);
        et_pw_chk = (EditText) findViewById(R.id.PasswordCheckText);
    }
    public void JoinButton(View view)
    {
        sId = et_id.getText().toString();
        sPw = et_pw.getText().toString();
        sPw_chk = et_pw_chk.getText().toString();

        if(sPw.equals(sPw_chk))   //근데 비밀번호를 다르게 하면 애초부터 else에 걸림
        {
            /* 패스워드 확인이 정상적으로 됨 */
            registDB rdb = new registDB();
            rdb.execute();
        }
        else
        {
            /* 패스워드 확인이 불일치 함 */

        }

    }


    public class registDB extends AsyncTask<Void, Integer, Void>  {

        String data = "";
        @Override
        public Void doInBackground(Void... unused) {

            /* 인풋 파라메터값 생성 */
            String param = "u_id=" + sId + "&u_pw=" + sPw + "";
            try {
                /* 서버연결 */
                URL url = new URL("http://211.178.109.157/Haniem/Register.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();

                /* 안드로이드 -> 서버 파라메터값 전달 */
                OutputStream outs = conn.getOutputStream();
                outs.write(param.getBytes("UTF-8"));
                outs.flush();
                outs.close();

                /* 서버 -> 안드로이드 파라메터값 전달 */
                InputStream is = null;
                BufferedReader in = null;


                is = conn.getInputStream();
                in = new BufferedReader(new InputStreamReader(is), 8 * 1024);
                String line = null;
                StringBuffer buff = new StringBuffer();
                while ( ( line = in.readLine() ) != null )
                {
                    buff.append(line + "\n");
                }
                data = buff.toString().trim();
                Log.e("RECV DATA", data);//data는 php상 echo값임


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override// doInBackground종료 뒤 호출된다. 아무튼 바깥으로 빼야됐으
        protected void onPostExecute(Void aVoid)   {
            super.onPostExecute(aVoid);
        /* 서버에서 응답 */
            Log.e("RECV DATA",data);

            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);//이거 넣어줘야 builder을 사용가능
            if(data.equals("0"))//올바르게 값이 들어갔으면 0을 반환함ㅌ
            {
                Log.e("RESULT","성공적으로 처리되었습니다!");
                alertBuilder
                        .setTitle("회원가입");
                alertBuilder
                        .setMessage("회원가입 성공! 이메일 인증 후 로그인 가능")
                        .setCancelable(true)
                        .setPositiveButton("로그인 화면으로", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                AlertDialog dialog = alertBuilder.create();
                dialog.show();

            }
            else//지금 무슨이유에선지 모르겠는데 그냥 계쏙 여기로 들어옴 값이
            {
                Log.e("RESULT","에러 발생! ERRCODE = " + data);
                alertBuilder
                        .setTitle("알림")
                        .setMessage("이미 존재하는 아이디입니다. ! errcode : "+ data)
                        .setCancelable(true)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                AlertDialog dialog = alertBuilder.create();
                dialog.show();
            }
        }




    }


}

