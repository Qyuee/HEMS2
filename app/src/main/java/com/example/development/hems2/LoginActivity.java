package com.example.development.hems2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {
    EditText et_id, et_pw;
    String sId, sPw;
    double initTime;

    LinearLayout linearLayout=null;
    LinearLayout.LayoutParams params=null;

    final Context context = this;//이거 onPostExecute부분에서 필요한 것이었음

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        et_id = (EditText) findViewById(R.id.IdText);
        et_pw = (EditText) findViewById(R.id.PasswordText);

        LayoutInflater inflater=(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        linearLayout=(LinearLayout)inflater.inflate(R.layout.progress, null);

        params=new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );

        getWindow().addContentView(linearLayout,params);
        linearLayout.setVisibility(View.GONE);

    }

    public void LoginButton(View view){
        linearLayout.setVisibility(View.VISIBLE);

        try{
            sId = et_id.getText().toString();
            sPw = et_pw.getText().toString();
        }catch (NullPointerException e) {
            Log.e("err",e.getMessage());
        }

        loginDB lDB = new loginDB();
        lDB.execute();

    }

    public class loginDB extends AsyncTask<Void, Integer, String> {

        String data = "";//이 값이 지금 class 내 전역변수로 작용하기 때문에 문제가 발생하는건가?

        @Override
        protected String doInBackground(Void... unused) {

            /* 인풋 파라메터값 생성 */
            String param = "u_id=" + sId + "&u_pw=" + sPw + "";
            Log.e("POST",param);//현재 적은 id, pw 값을 log에 띄우는 것 뿐
            try {
            /* 서버연결 */
//                URL url = new URL("http://172.20.10.2/Haniem/Login.php");
                URL url = new URL("http://211.178.109.157/Haniem/Login.php");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setConnectTimeout(10000);
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

                Log.e("POST","받아온거 : "+data);//현재 적은 id, pw 값을 log에 띄우는 것 뿐

                return data;

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "error";
            } catch (IOException e) {
                e.printStackTrace();
                return "error";
            }

        }

        @Override
        protected void onPostExecute(String data) {
            super.onPostExecute(data);

            if(data.equals("error")){
                linearLayout.setVisibility(View.GONE);
                Log.v("로그인 에러" , "네트워크 확인 바람");

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);//이거 넣어줘야 builder을 사용가능
                if(data.equals("error"))
                {
                    alertBuilder
                            .setTitle("네트워크 에러")
                            .setIcon(R.drawable.smart_home_icon)
                            .setMessage("네트워크 연결을 확인해주세요.")
                            .setCancelable(true)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    AlertDialog dialog = alertBuilder.create();
                    dialog.show();
                }
            }else{
                linearLayout.setVisibility(View.GONE);
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);//이거 넣어줘야 builder을 사용가능
                if(data.equals("1"))
                {
                    Log.e("RESULT","성공적으로 처리되었습니다!");
                    alertBuilder
                            .setTitle("로그인")
                            .setIcon(R.drawable.smart_home_icon)
                            .setMessage("로그인 성공!")
                            .setCancelable(true)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                    AlertDialog dialog = alertBuilder.create();
                    dialog.show();
                }
                else if(data.equals("0"))
                {
                    linearLayout.setVisibility(View.GONE);
                    Log.e("RESULT","비밀번호가 일치하지 않습니다.");
                    alertBuilder
                            .setTitle("로그인")
                            .setIcon(R.drawable.smart_home_icon)
                            .setMessage("비밀번호가 일치하지 않습니다.")
                            .setCancelable(true)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //finish();
                                }
                            });
                    AlertDialog dialog = alertBuilder.create();
                    dialog.show();
                }
                else { //1도 0도 아닌 값이 반환되면 에러발생한다. 아이디 다를 때
                    linearLayout.setVisibility(View.GONE);
                    Log.e("RESULT","에러 발생! ERRCODE = " + data);
                    alertBuilder
                            .setTitle("로그인")
                            .setIcon(R.drawable.smart_home_icon)
                            .setMessage(data)
                            .setCancelable(true)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //finish();
                                }
                            });
                    AlertDialog dialog = alertBuilder.create();
                    dialog.show();
                }
            }
        }


    }


    public void JoinButton(View view){//회원가입으로 넘어감
        Intent joinIntent = new Intent(LoginActivity.this, JoinActivity.class);//레지스터액티비티로 넘어갈 수 있도록
        LoginActivity.this.startActivity(joinIntent);
    }
    @Override//뒤로가기 키 누를시 토스트발생
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - initTime > 2000) {
                //Toast t = Toast.makeText(this, R.string.main_bakc_end, Toast.LENGTH_SHORT);
                //t.show();
            } else {
                finish();
            }
            initTime = System.currentTimeMillis();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

}
