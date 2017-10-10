package com.example.development.hems2;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class Focus_search_activity extends AppCompatActivity {

    DecimalFormat decimalFormat=new DecimalFormat("###,###원");

    // 아래의 3가지 ArrayList는 tsak1(특정일 조회 통신 스레드)에서 받은 데이터를 id, date, avgs 에따라 각각으로 나누기 위해 사용한다.
    private ArrayList id=new ArrayList();
    private ArrayList date=new ArrayList();
    private ArrayList avgs=new ArrayList();

    // 아래의 2가지 ArrayList는 tsak3(월간 조회 통신 스레드)에서 받은 데이터를 date, avgs 에따라 각각으로 나누기 위해 사용한다.
    private ArrayList date_month=new ArrayList();
    private ArrayList avgs_month=new ArrayList();

    // "기간별 조회"의 세부사항 텍스트 뷰 변수.
    private TextView Detail_Max_Value=null;                     // 선택한 기간중 가장 높은 사용량을 표시할 텍스트.
    private TextView Deatil_Min_Value=null;                     // 선택한 기간중 가장 낮은 사용량을 표시할 텍스트.
    private TextView Detail_Avg_Value=null;                     // 선택한 기간의 평균 사용량.
    private TextView Detail_charge_range=null;
    private TextView Detail_charge_value=null;

    // "시간별 조회"의 세부사항 텍스트 뷰 변수.
    private TextView Detail_Max_Value_time=null;
    private TextView Detail_Min_Value_time=null;
    private TextView Detail_Avg_Value_time=null;


    // "월별 조회"의 요금조회 텍스트 뷰 변수.
    private TextView Detail_charge_range_month=null;
    private TextView Detail_charge_value_month=null;
    private TextView Detail_Max_Value_month=null;
    private TextView Detail_Min_Value_month=null;
    private TextView Detail_Avg_Value_month=null;


    private Button Select_first_date=null;                      // "기간별 조회"의 "시작 날짜" 버튼
    private Button Select_second_date=null;                     // "기간별 조회"의 "종료 날짜" 버튼

    private Button Select_time_date=null;                       // 시간별 조회의 "선택날짜" 버튼

    private Button Select_first_month=null;                     // 월별 조회의 "시작 월" 버튼
    private Button Select_second_month=null;                    // 월별 조회의 "종료 월" 버튼

    String ddd="";
    String ddd2="";

    selected_data task;         // 기간별 그래프를 그릴 데이터를 가져오는 백 스레드.
    search_based_time task2;    // 시간별 그래프를 그릴 데이터를 가져오는 백 스레드.
    search_data_month task3;

    private int first_Year, first_Month, first_Day;             // 첫번째 데이터 피커 다이어로그에서 선택한 날짜를 입력 받는다.
    private int second_Year, second_Month, second_Day;          // 두번째 데이터 피커 다이어로그에서 선택한 날짜.
    private String TAG1="조회 데이터 수신 스레드(주간) : ";                 // 상태 로그를 보기 위한 커스텀 로그 아이디.
    private String Month_Tag="월간 조회 데이터 수신 스레드 : ";

    // 특정 일 조회 날짜 변수
    private int first_Year_day, first_Month_day, first_Day_day;             // 첫번째 데이터 피커 다이어로그에서 선택한 날짜를 입력 받는다.

    // 월간 조회 날짜 변수
    private int first_Year_month, first_Month_month;             // 첫번째 데이터 피커 다이어로그에서 선택한 날짜를 입력 받는다.
    private int second_Year_month, second_Month_month;          // 두번째 데이터 피커 다이어로그에서 선택한 날짜.

    private int graph_toggle=0;                                 // "기간별 조회"의 그래프가 이미 그려져 있는지 확인.
    private int time_graph_toggle=0;                            // "시간별 조회"의 그래프가 이미 그려져 있는지 확인.
    private int graph_toggle_month=0;                            // "시간별 조회"의 그래프가 이미 그려져 있는지 확인.

    // 기간별 조회 레이아웃
    LinearLayout Detail_layout=null;
    LinearLayout Based_date_graph_layout=null;
    LinearLayout Detail_layout_headline=null;
    LinearLayout Select_date_layout=null;
    LinearLayout Detail_charge_layout=null;

    //시간별 조회 레이아웃
    LinearLayout Select_time_layout=null;
    LinearLayout Based_Time_graph_layout=null;
    LinearLayout Detail_time_layout_headline=null;
    LinearLayout Detail_time_layout=null;

    //월별 조회 레이아웃
    LinearLayout Based_month_graph_layout=null;
    LinearLayout Detail_charge_layout_month=null;
    LinearLayout Detail_layout_month=null;

    // "기간별 조회"의 그래프 관련 변수 선언.
    LineChart lineChart=null;
    LineDataSet lineDataSet=null;
    LineData lineData=null;
    ArrayList<String> labels=new ArrayList<>();
    ArrayList<Entry> entries=new ArrayList<>();

    // "시간별 조회"의 그래프 관련 변수 선언.
    LineChart lineChart_time=null;
    LineDataSet lineDataSet_time=null;
    LineData lineData_time=null;
    ArrayList<String> labels_time=new ArrayList<>();
    ArrayList<Entry> entries_time=new ArrayList<>();

    // "월별 조회"의 그래프 관련 변수 선언.
    LineChart lineChart_month=null;
    LineDataSet lineDataSet_month=null;
    LineData lineData_month=null;
    ArrayList<String> labels_month=new ArrayList<>();
    ArrayList<Entry> entries_month=new ArrayList<>();

   @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_focus_search);

       // 기간별 조회의 "시작 날짜", "종료 날짜" 버튼 연결.
       Select_first_date=(Button) findViewById(R.id.select_first_date);
       Select_second_date=(Button) findViewById(R.id.select_second_date);

       // 시간별 조회의 "날짜 선택" 버튼 연결.
       Select_time_date=(Button) findViewById(R.id.select_time_date);

       // 월별 조회의 "날짜 선택" 버튼 연결
       Select_first_month=(Button) findViewById(R.id.select_first_month);
       Select_second_month=(Button) findViewById(R.id.select_second_month);

       // 기간별 레이아웃에 대한 연결.
       Select_date_layout=(LinearLayout) findViewById(R.id.select_date_layout);
       Based_date_graph_layout=(LinearLayout) findViewById(R.id.Based_Date_graph_layout);
       Detail_layout_headline=(LinearLayout) findViewById(R.id.Detail_layout_headline);
       Detail_layout=(LinearLayout) findViewById(R.id.Detail_layout);
       Detail_charge_layout=(LinearLayout) findViewById(R.id.Detail_charge_layout);

       // 시간별 레이아웃에 대한 연결.
       Select_time_layout=(LinearLayout) findViewById(R.id.select_time_layout);
       Based_Time_graph_layout=(LinearLayout) findViewById(R.id.Based_Time_graph_layout);
       Detail_time_layout_headline=(LinearLayout) findViewById(R.id.Detail_time_layout_headline);
       Detail_time_layout=(LinearLayout) findViewById(R.id.Detail_time_layout);

       // 월별 레이아웃에 대한 연결
       Based_month_graph_layout=(LinearLayout) findViewById(R.id.Based_month_graph_layout);
       Detail_charge_layout_month=(LinearLayout) findViewById(R.id.Detail_charge_layout_month);
       Detail_layout_month=(LinearLayout) findViewById(R.id.Detail_layout_month);

       // "기간별 조회"의 "조회에 따른 세부내용"의 텍스트를 변경하기 위해 연결.
       Detail_Max_Value=(TextView)findViewById(R.id.Detail_Max_Value);
       Deatil_Min_Value=(TextView)findViewById(R.id.Detail_Min_Value);
       Detail_Avg_Value=(TextView)findViewById(R.id.Detail_Avg_Value);

        // "기간별 조회"의 요금 정보의 텍스트를 변경하기 위한 연결 부분
       Detail_charge_range=(TextView) findViewById(R.id.Detail_charge_range);
       Detail_charge_value=(TextView) findViewById(R.id.Detail_charge_value);

       // "기간별 조회"의 "조회에 따른 세부내용"의 텍스트를 변경하기 위해 연결.
       Detail_Max_Value_time=(TextView)findViewById(R.id.Detail_Max_Value_time);
       Detail_Min_Value_time=(TextView)findViewById(R.id.Detail_Min_Value_time);
       Detail_Avg_Value_time=(TextView)findViewById(R.id.Detail_Avg_Value_time);


       // "월별 조회"의 요금정보 의 텍스트를 변경하기 위해 연결.
       Detail_charge_range_month=(TextView) findViewById(R.id.Detail_charge_range_month);
       Detail_charge_value_month=(TextView) findViewById(R.id.Detail_charge_value_month);
       Detail_Max_Value_month=(TextView) findViewById(R.id.Detail_Max_Value_month);
       Detail_Min_Value_month=(TextView) findViewById(R.id.Detail_Min_Value_month);
       Detail_Avg_Value_month=(TextView) findViewById(R.id.Detail_Avg_Value_month);

       TabHost tabHost1=(TabHost) findViewById(R.id.tabHost1);
       tabHost1.setup();

       TabHost.TabSpec ts1=tabHost1.newTabSpec("Tab Spec 1");
       ts1.setContent(R.id.content1);
       ts1.setIndicator("주간");
       tabHost1.addTab(ts1);

       TabHost.TabSpec ts2=tabHost1.newTabSpec("Tab Spec 2");
       ts2.setContent(R.id.content2);
       ts2.setIndicator("일간");
       tabHost1.addTab(ts2);

       TabHost.TabSpec ts3=tabHost1.newTabSpec("Tab Spec 3");
       ts3.setContent(R.id.content3);
       ts3.setIndicator("월간");
       tabHost1.addTab(ts3);

       tabHost1.setCurrentTab(0);

   }

//+++++++++++++++++++++++++[기간별 조회 관련 메소드]+++++++++++++++++++++++++++++++++++
//   선택한 날짜에 맞는 데이터가 존재하지 않을 경우에 대한 예외 처리 필요함.

    // "시작날짜" 눌렀을 때 동작.  --> 시작일 선택.
    public void btn1(View v){

        Calendar cal = Calendar.getInstance();
        int dyear=cal.get(cal.YEAR);
        int dmonth=cal.get(cal.MONTH);
        int dday=cal.get(cal.DAY_OF_MONTH);

        DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                first_Year=year;
                first_Month=month+1;
                first_Day=day;
                //Toast.makeText(getApplicationContext(), first_Year+". "+first_Month+". "+first_Day, Toast.LENGTH_SHORT).show();
                Select_first_date.setText(first_Year+"년 "+first_Month+"월 "+first_Day+"일");
            }
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, dateSetListener, dyear, dmonth-1, dday);
        datePickerDialog.show();
    }

    // "종료날짜" 눌렀을 때 동작.  --> 종료일 선택.
    public void btn2(View v){

        Calendar cal = Calendar.getInstance();

        int dyear=cal.get(cal.YEAR);
        int dmonth=cal.get(cal.MONTH);
        int dday=cal.get(cal.DAY_OF_MONTH);
        DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                second_Year=year;             // 선택한 년도
                second_Month=month+1;           // 선택한 월.
                second_Day=day;               // 선택한 일.
                //Toast.makeText(getApplicationContext(), second_Year+". "+second_Month+". "+second_Day, Toast.LENGTH_SHORT).show();
                Select_second_date.setText(second_Year+"년 "+second_Month+"월 "+second_Day+"일");
            }
        };

        new DatePickerDialog(this, dateSetListener, dyear, dmonth, dday).show();

    }

    // "기간별 조회"의 "조회"버튼 눌렀을 때.        --> 백그라운드 호출.
    public void btn_search_date(View v){
        //Toast.makeText(getApplicationContext(), "기간별 조회 합니다.", Toast.LENGTH_SHORT).show();
        task=new selected_data();

        ddd="";

        if(graph_toggle==1){
            id.clear();
            date.clear();
            avgs.clear();

            labels.clear();
            entries.clear();
            lineData.clearValues();
            lineDataSet.clear();
            lineChart.clear();

            task.execute(Integer.toString(first_Year), Integer.toString(first_Month), Integer.toString(first_Day), Integer.toString(second_Year), Integer.toString(second_Month), Integer.toString(second_Day));    // int형 이기에 String으로 변환 필요.
        }else{
            task.execute(Integer.toString(first_Year), Integer.toString(first_Month), Integer.toString(first_Day), Integer.toString(second_Year), Integer.toString(second_Month), Integer.toString(second_Day));    // int형 이기에 String으로 변환 필요.
        }
    }

    // (기간별 조회) 백 그라운드에서 네트워크 통신하는 스레드.     --> DB로 부터 선택된 범위의 데이터를 가져옴.
    public class selected_data extends AsyncTask<String, Void, String> {

        String first_Year = null;    // 첫번째 선택한 년.
        String first_Month= null;    // 첫번째 선택한 월.
        String first_Date = null;    // 첫번째 선택한 일.
        String second_Year= null;    // 두 번째 선택한 년.
        String second_Month= null;   // 두 번째 선택한 월.
        String second_Date= null;    // 두 번째 선택한 일.

        // BackGround 스레드를 실행하기 전에 할 행동들을 정의한다. 변수 초기화, 네트워크 연결 등.
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        // doInBackground 메소드에의 결과를 가지고 UI작업 수행.
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            //Toast.makeText(getApplicationContext(), first_Year+"."+first_Month+"."+first_Day, Toast.LENGTH_SHORT).show();
            Log.d(TAG1, "POST response  - " + result);

            String[] id_array=new String[id.size()];
            String[] date_array=new String[date.size()];
            String[] avgs_array=new String[avgs.size()];

            if(result.equals("Error Code 1")){
                ddd="종료월이 시작월을 앞설 수 없습니다. \n 다시 선택하세요.";
                Toast.makeText(getApplicationContext(), ddd, Toast.LENGTH_SHORT).show();
            }else if(result.equals("Error Code 2")){
                ddd="종료일이 시작일를 앞설 수 없습니다. \n 다시 선택하세요.";
                Toast.makeText(getApplicationContext(), ddd, Toast.LENGTH_SHORT).show();
            }else if(result.equals("Error Code 3")){
                ddd="범위를 선택하세요.";
                Toast.makeText(getApplicationContext(), ddd, Toast.LENGTH_SHORT).show();
            }
            else{
                if(id_array.length>0){
                    // 그래프 그리는 메소드.
                    drawGraph(id_array, date_array, avgs_array);

                    // 선택된 기간에 대한 요금 계산 메소드. -> 계산 후 텍스트 창에 출력해야 함.
                    // CalcCharge(date_array, avgs_array);

                    // 선택된 기간에 대한 상세치 표시 메소드
                    ShowDetail(avgs_array);

                    HashMap<String, Integer> calc=CalcCharge(avgs_array);

                    Detail_charge_range.setText(first_Year+"."+first_Month+"."+first_Date+"~"+second_Year+"."+second_Month+"."+second_Date+"의 요금정보");
                    Detail_charge_value.setText("사용량 : "+calc.get("사용량")+"[Kwh]"+"\n청구금액 : "+decimalFormat.format(calc.get("총요금"))+"\n(청구 금액 = 전기 요금계 + 부가가치세 + 산업기반기금)"+"\n\n[세부내용]"+"\n기　본　료 : "+decimalFormat.format(calc.get("기본료"))+"\n전력량요금 : "+decimalFormat.format(calc.get("전력량요금"))+"\n전기요금계 : "+decimalFormat.format(calc.get("전기요금계"))+"\n(전기요금계 = 기본요금 + 전력량요금 - 필수사용량 보장공제)"+"\n부가가치세 : "+decimalFormat.format(calc.get("부가가치세"))+"\n산업기반기금 : "+decimalFormat.format(calc.get("산업기반기금")));
                }else{
                    Toast.makeText(getApplicationContext(),"선택하신 기간의 데이터가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        }

        //  백그라운드 작업 진행.
        @Override
        protected String doInBackground(String... params) {
            first_Year = params[0];    // 첫번째 선택한 년.
            first_Month= params[1];    // 첫번째 선택한 월.
            first_Date = params[2];    // 첫번째 선택한 일.
            second_Year=params[3];    // 두 번째 선택한 년.
            second_Month=params[4];   // 두 번째 선택한 월.
            second_Date=params[5];    // 두 번째 선택한 일.

            if(Integer.parseInt(second_Month)<Integer.parseInt(first_Month)){
                return "Error Code 1";
            }else if((Integer.parseInt(second_Month)==Integer.parseInt(first_Month)) && (Integer.parseInt(second_Date)<Integer.parseInt(first_Date))){
                return "Error Code 2";
            }

           if(!first_Year.equals("0")){
               String serverURL = "http://211.178.109.157/Haniem/select_basedDate.php";
               String postParameters = "first_Year=" + first_Year + "&first_Month=" + first_Month+"&first_Date="+first_Date;
               postParameters+="&second_Year="+second_Year+"&second_Month="+second_Month+"&second_Date="+second_Date;

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

                   Log.d(TAG1, " 전송한 데이터(파라미터) - " + postParameters);

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
                       ddd += "ID:" + jOb.getString("ID")+ "\n" + "Created_Date:" + jOb.getString("Created_Date") +"\n"+ "Avgs:" + jOb.getString("Avgs")+"\n";
                   }

                   Log.d(TAG1, " 전송 받은 데이터 - " + ddd);

                   // "\n"을 기준으로 문자열 ddd를 나눈다.
                   // 이는 parse_data의 각각의 인덱스에 순차적으로 저장된다.
                   String[] parse_data=ddd.split("\n");

                   // 이렇게 문자열 parse_data에 저장된 문자열을 ArrayList로 변환한다.
                   // add(index, value) 메소드는 ArrayList에 값을 추가하는 동작을 한다.
                   // 아래에서 substring을 사용하여 필요한 부분만 선택하여 ArrayList에 추가한다.
                   // ex) id.add(parse_data[i].substring(3,parse_data[i].length()));
                   // 위의 문장은 parse_data[i]를 인덱스 3번과 가장 끝의 인덱스 사이의 문자열을 id ArrayList에 입력한다.
                   // 이는 ddd의 문자열에서 "id:1" 과 같은 문자열에서 1만 선택하여 ArrayList에 저장한다.

                   // id, date, avgs ArrayList에 각각의 값을 입력한다.
                   for(int i=0; i<parse_data.length; i=i+3){
                       id.add(parse_data[i].substring(3,parse_data[i].length()));
                       date.add(parse_data[i+1].substring(13,parse_data[i+1].length()));
                       avgs.add(parse_data[i+2].substring(5,parse_data[i+2].length()));
                   }
                   return ddd;
               }catch (Exception e){
                   return new String("Exception Error: " + e.getMessage());
               }
           }else{
               return "Error Code 3";
           }
        }
    }

    // 선택 날짜에 따라 그래프를 그리는 메소드.      --> 가져온 데이터에 맞는 그래프를 그린다.
    public void drawGraph(String[] id_array, String[] date_array, String[] avgs_array){

        id.toArray(id_array);
        date.toArray(date_array);
        avgs.toArray(avgs_array);

        // entries   좌표 값.
        //entries=new ArrayList<>();

        for(int i=0; i<id.size(); i++){
            entries.add(new Entry(Float.parseFloat(avgs_array[i]), i));
        }

        // labels x축 이름
        // labels=new ArrayList<>();
        for(int i=0; i<date_array.length; i++){
            labels.add(date_array[i]);
        }

        lineDataSet = new LineDataSet(entries, date_array[0]+"~"+date_array[date_array.length-1]+"의 사용량");
        lineDataSet.setColors(ColorTemplate.PASTEL_COLORS);     // 색상 관련.
        lineDataSet.setDrawCubic(true);                         // 각각의 포인트를 곡선으로 연결.
        lineDataSet.setDrawFilled(true);                        // 선아래로 색상표시
        lineDataSet.setColor(Color.BLACK);                      // 그래프 라인의 색상 표시.
        lineDataSet.setDrawValues(true);                        // 각 포인트의 값을 표시
        lineDataSet.setFillColor(Color.BLUE);                // 선 아래의 채워지는 색상.
        lineDataSet.setHighlightEnabled(false);                 // ???

        lineData = new LineData(labels, lineDataSet);
        lineData.setDrawValues(true);                           // 좌표 포인트 위에 데이터 수치 표시
        lineData.setHighlightEnabled(true);
        lineData.setValueTextSize(10);

        lineChart = (LineChart) findViewById(R.id.chart);
        lineChart.setData(lineData);                        // set the data and list of lables into chart
        lineChart.getAxisRight().setEnabled(false);         // y축 우측 범위표 제거.
        lineChart.getAxisLeft();                            //lineChart.getAxisLeft().setEnabled(false);
                                                            // 가로 세부선 없애고 좌측 y축 범위표 삭제.(false)
//        lineChart.setBackgroundColor(Color.WHITE);
        lineChart.setContentDescription("선택기간 사용량"); // ???
//        lineChart.setBackgroundColor(Color.WHITE);        --> 그래프 레이아웃 테두리 색상이 바뀜.
        lineChart.setBorderColor(Color.WHITE);


        YAxis y = lineChart.getAxisRight();     // y축의 방향 조절 getAxisRight => 왼쪽
        y.setDrawAxisLine(true);                //  ??
        y.setTextColor(Color.BLACK);            // 우측 범위 텍스트 색상 조절.

        XAxis x = lineChart.getXAxis();
        x.setAvoidFirstLastClipping(true);
        x.setTextColor(Color.BLACK);            // x축 라벨의 텍스트 색상
        x.setDrawAxisLine(false);               // ?? true와 false와 차이 못 찾음.
        x.setDrawGridLines(true);               // x축 데이터에 대한 세부선(Grid line)을 그린다.
        x.setDrawLabels(true);                  // x축 라벨을 그린다.
        x.setEnabled(true);                     // 위의 XAis에 관련된 모든 것을 조절.

        // legend 그래프 위에 위치시키는 상태표.
        Legend legend = lineChart.getLegend();
        legend.setTextColor(Color.BLACK);
        legend.setEnabled(true);

        lineChart.animateXY(1000,1000);             // X축, Y축 애니메이션 속도.
        lineChart.invalidate();
        graph_toggle=1;

    }

    // "그래프" 버튼 눌렀을 때. -> 그래프 창 출력/사라짐
    public void graph_time_btn(View v){
        if(Based_Time_graph_layout.getVisibility()==View.VISIBLE){
            Based_Time_graph_layout.setVisibility(View.GONE);
        }else{
            Based_Time_graph_layout.setVisibility(View.VISIBLE);
        }
    }

    // "요금정보" 버튼 눌렀을 때. -> 요금정보 창 출력/사라짐
    public void Detail_btn_charge(View v){
        if(Detail_charge_layout.getVisibility()==View.VISIBLE){
            Detail_charge_layout.setVisibility(View.GONE);
        }else{
            Detail_charge_layout.setVisibility(View.VISIBLE);
        }
    }

    // "조회 내용" 버튼 눌렀을 때.
    public void Detail_btn(View v){
        if(Detail_layout.getVisibility()==View.VISIBLE){
            Detail_layout.setVisibility(View.GONE);
        }else{
            Detail_layout.setVisibility(View.VISIBLE);
        }
    }

    // 선택 날짜에 따른 세부사항 표시하는 메소드.
    public void ShowDetail(String[] avgs_array){

        //  1. 일평균이 가장 높은 날짜 표시.
        //  2. 일평균이 가장 낮은 날짜 표시.
        //  3. 선택한 기간의 평균적인 사용량 표시.

        float max_value=0;
        float min_value=10000;
        float sum_value=0;
        for(int i=0; i<avgs_array.length; i++){
            max_value=Math.max(max_value, Float.parseFloat(avgs_array[i]));
            min_value=Math.min(min_value, Float.parseFloat(avgs_array[i]));
            sum_value+=Float.parseFloat(avgs_array[i]);
        }

        String max=String.format("%.2f", max_value);
        String min=String.format("%.2f", min_value);
        String avg=String.format("%.2f", sum_value/avgs_array.length);

        Detail_Max_Value.setText("최고 사용량 : "+max+" [wh]");
        Deatil_Min_Value.setText("최저 사용량 : "+min+" [wh]");
        Detail_Avg_Value.setText("평균 사용량 : "+avg+" [wh]");
    }

    // 선택 날짜에 따른 요금 정보 계산 및 출력 메소드.
    public HashMap<String, Integer> CalcCharge(String[] avgs_array){
        String charge="사용량 계산 로그:";
        HashMap<String, Integer> calc_detail=new HashMap<>();

        // 1. 선택 기간에 대한 요금 정보를 출력.
        // 2. 6월 15일 ~ 6월 20일 까지가 조회 범위라면 15, 16, 17, 18, 19, 20 일에 대한 각각의 요금을 출력.

        /*
        주택용 저압 - 일반 다세대, 단독 주택, 빌라  --> 기준으로 구성
        처음 200KWh까지 1KHW당 93.3원
        200KWh~400KWh까지 1Kwh당 187.9원
        400KWh 초과시 1KWh 당 280.6원

        */
        // 주택용 고압 - APT(변전실이 있는 건물)

        float usage=0;              // 사용량
        int electricity_rate=0; // 전력량 요금
        int elec_base=0;
        int charge_sum=0;     // 사용총 요금
        int vat=0;            // 부가가치세
        int Base_rate=910;       // 기본료
        int Industry_Based_Fund=0;  // 전력 산업 기반기금
        int Guarantee_deduction=0;

        for(int i=0; i<avgs_array.length; i++){
            usage+=Float.parseFloat(avgs_array[i]);
        }

        // ex) 430 Kwh
        // 0~200Kwh 일때.   - 1단계
        // 200Kwh ~ 400Kwh  - 2단계
        // 400Kwh 초과      - 3단계

        if(usage<=200){  // 1단계
            Base_rate=910;
            Guarantee_deduction=4000;
            electricity_rate=(int)(usage*93.3);
            elec_base=Base_rate+electricity_rate-Guarantee_deduction;

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

        charge_sum=(charge_sum/10)*10;   // 10원 미만 절사

        calc_detail.put("사용량",(int)usage);
        calc_detail.put("전력량요금",electricity_rate);
        calc_detail.put("기본료",Base_rate);
        calc_detail.put("전기요금계", elec_base);
        calc_detail.put("총요금",charge_sum);
        calc_detail.put("부가가치세",vat);
        calc_detail.put("산업기반기금",Industry_Based_Fund);
        calc_detail.put("필수사용량보장공제",Guarantee_deduction);

        return calc_detail;
    }

//+++++++++++++++++++++++++[시간별 조회 관련 메소드]+++++++++++++++++++++++++++++++++++*****************
//  선택한 날짜에 맞는 데이터가 존재하지 않을 경우에 대한 예외 처리 필요함.                            *
//  선택한 시간에 따른 데이터를 DB로 부터 가져오려 했지만 선택한 시간 값을 넣었을 때                   *
//  이를 가지고 DB의 각 어트리뷰트를 구분하는 방법을 모르겠음.                                         *
//  ex) 13시~18시 데이터를 가져오려 한다면, 이는 DB의 T_13_14와 T_18_19에 각각 대응한다.               *
//      이 때, DB에서 어트리뷰트의 13으로 T_13_14를 판단하고, 18로 T_18_19를 판단하여                  *
//      SQL 문에서 범위를 주어 해당되는 범위에 대한 데이터를 가져와야 하는데.                          *
//      보통은 어트리뷰트의 도메인 값을 가지고 값의 범위를 지정한다.                                   *
//      즉, 과연 어트리뷰트의 값을 가지고 SQL문의 범위를 지정하여 선택한 데이터를 가져올 수 있는가?    *
//******************************************************************************************************

    // "일간별 조회"의 "날짜 선택" 버튼을 눌렀을 때.
    public void btn_time_date(View v){

        java.util.Calendar cal= java.util.Calendar.getInstance();
        int dyear=cal.get(cal.YEAR);
        int dmonth=cal.get(cal.MONTH);
        int dday=cal.get(cal.DAY_OF_MONTH);

        DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                first_Year_day=year;
                first_Month_day=month+1;
                first_Day_day=day;
                //Toast.makeText(getApplicationContext(), first_Year+". "+first_Month+". "+first_Day, Toast.LENGTH_SHORT).show();
                Select_time_date.setText(first_Year_day+"년 "+first_Month_day+"월 "+first_Day_day+"일");
            }
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, dateSetListener, dyear, dmonth, dday-7);
        datePickerDialog.show();
    }

    // "일간별 조회"의 "조회" 버튼 눌렀을 때.
    public void btn_search_time(View v){
        task2=new search_based_time();

        ddd="";

        if(time_graph_toggle==1){
            labels_time.clear();
            entries_time.clear();
            lineData_time.clearValues();
            lineDataSet_time.clear();
            lineChart_time.clear();

            task2.execute(Integer.toString(first_Year_day), Integer.toString(first_Month_day), Integer.toString(first_Day_day));    // int형 이기에 String으로 변환 필요.
        }else{
            task2.execute(Integer.toString(first_Year_day), Integer.toString(first_Month_day), Integer.toString(first_Day_day));    // int형 이기에 String으로 변환 필요.
        }
    }

    // (일간별 조회) 백 그라운드에서 네트워크 통신하는 스레드.
    public class search_based_time extends AsyncTask<String, Void, String[]> {

        String first_Year_day = null;    // 첫번째 선택한 년.
        String first_Month_day= null;    // 첫번째 선택한 월.
        String first_Date_day=null;

        // BackGround 스레드를 실행하기 전에 할 행동들을 정의한다. 변수 초기화, 네트워크 연결 등.
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        // doInBackground 메소드에의 결과를 가지고 UI작업 수행.
        @Override
        protected void onPostExecute(String[] result) {
            super.onPostExecute(result);
            Log.d(TAG1, " 수신한 데이터  - " + ddd);

            //Toast.makeText(getApplicationContext(), first_Year_day+"."+first_Month_day+"."+first_Day_day+result[0], Toast.LENGTH_SHORT).show();

            if(result[0].equals("Error Code 3")){
                Toast.makeText(getApplicationContext(), "날짜를 선택하세요.", Toast.LENGTH_SHORT).show();
            }else{
                // "시간별 조회"의 그래프 그리는 메소드.

                drawGraph_time(result);

                // 선택된 날짜에 대한 상세치 표시 메소드
                ShowDetail_time(result);
            }
        }

        //  백그라운드 작업 진행.
        @Override
        protected String[] doInBackground(String... params) {
            first_Year_day = params[0];    // 첫번째 선택한 년.
            first_Month_day= params[1];    // 첫번째 선택한 월.
            first_Date_day = params[2];    // 첫번째 선택한 일.
            String[] parse_data=new String[1];

            if(!first_Year_day.equals("0")){

                String serverURL = "http://211.178.109.157/Haniem/select_basedTime.php";
                String postParameters = "first_Year=" + first_Year_day + "&first_Month=" + first_Month_day+"&first_Date="+first_Date_day;

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

                    Log.d(TAG1, "POST Transfer Data - " + postParameters);
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
                        ddd += jOb.getString("id")+ "\n" + jOb.getString("cds_date") +"\n"+ jOb.getString("avgs")+"\n"+jOb.getString("T_00_01")
                                +"\n"+jOb.getString("T_01_02")+"\n"+jOb.getString("T_02_03")+"\n"+jOb.getString("T_03_04")+"\n"+jOb.getString("T_04_05")
                                +"\n"+jOb.getString("T_05_06")+"\n"+jOb.getString("T_06_07")+"\n"+jOb.getString("T_07_08")+"\n"+jOb.getString("T_08_09")
                                +"\n"+jOb.getString("T_09_10")+"\n"+jOb.getString("T_10_11")+"\n"+jOb.getString("T_11_12")+"\n"+jOb.getString("T_12_13")
                                +"\n"+jOb.getString("T_13_14")+"\n"+jOb.getString("T_14_15")+"\n"+jOb.getString("T_15_16")+"\n"+jOb.getString("T_16_17")
                                +"\n"+jOb.getString("T_17_18")+"\n"+jOb.getString("T_18_19")+"\n"+jOb.getString("T_19_20")+"\n"+jOb.getString("T_20_21")
                                +"\n"+jOb.getString("T_21_22")+"\n"+jOb.getString("T_22_23")+"\n"+jOb.getString("T_23_00");
                    }
                    parse_data=ddd.split("\n");
                    return parse_data;

                }catch (Exception e){
                    e.printStackTrace();
                    parse_data[0]="Error";
                    return parse_data;
                }
            }else{
                parse_data[0]="Error Code 3";
                return parse_data;
            }
        }
    }

    // "일간별 조회" 그래프 그리는 메소드.
    public void drawGraph_time(String[] result){

        // entries   좌표 값.
        //entries=new ArrayList<>();

        // result 문자열 배열은 task2의 DoinBackfround() 내의 parse_data와 같다.
        // 이 parse_data에는 0번 인덱스부터 id, cds_date, avgs, T_00_01 ..... T_23_00 의 값이 들어 있다.
        // 그래프를 그리는데 0번, 1번, 2번 인덱스 값은 필요없기에 i=3부터 시작한다.
        for(int i=3; i<result.length; i++){
            entries_time.add(new Entry(Float.parseFloat(result[i]), i-3));
        }

        // labels x축 이름
        // labels=new ArrayList<>();
        for(int i=3; i<result.length; i++){
            labels_time.add((i-3)+"시");
        }

        lineDataSet_time = new LineDataSet(entries_time, first_Year_day+"."+first_Month_day+"."+first_Day_day+"의 사용량");
        lineDataSet_time.setColors(ColorTemplate.VORDIPLOM_COLORS);  // 색상 관련.
        lineDataSet_time.setDrawCubic(true);                         // 각각의 포인트를 곡선으로 연결.
        lineDataSet_time.setDrawFilled(true);                        // 선아래로 색상표시
        lineDataSet_time.setDrawValues(true);                        // 각 포인트의 값을 표시
        lineDataSet_time.setColor(Color.BLACK);                      // 그래프 라인의 색상 표시.
        lineDataSet_time.setFillColor(Color.YELLOW);

        lineData_time = new LineData(labels_time, lineDataSet_time);
        lineData_time.setDrawValues(true);                           // 좌표 포인트 위에 데이터 수치 표시
        lineData_time.setHighlightEnabled(true);
        lineData_time.setValueTextSize(10);

        lineChart_time = (LineChart) findViewById(R.id.chart_time);
        lineChart_time.setData(lineData_time);                        // set the data and list of lables into chart
        lineChart_time.getAxisRight().setEnabled(false);         // y축 우측 범위표 제거.
        lineChart_time.getAxisLeft();                            //lineChart.getAxisLeft().setEnabled(false);

        // 가로 세부선 없애고 좌측 y축 범위표 삭제.(false)


        YAxis y = lineChart_time.getAxisRight();     // y축의 방향 조절 getAxisRight => 왼쪽
        y.setDrawAxisLine(true);                //  ??
        y.setTextColor(Color.BLACK);            // 우측 범위 텍스트 색상 조절.

        XAxis x = lineChart_time.getXAxis();
        x.setAvoidFirstLastClipping(true);
        x.setTextColor(Color.BLACK);            // x축 라벨의 텍스트 색상
        x.setDrawAxisLine(false);               // ?? true와 false와 차이 못 찾음.
        x.setDrawGridLines(true);               // x축 데이터에 대한 세부선(Grid line)을 그린다.
        x.setDrawLabels(true);                  // x축 라벨을 그린다.
        x.setEnabled(true);                     // 위의 XAis에 관련된 모든 것을 조절.

        // legend 그래프 위에 위치시키는 상태표.
        Legend legend = lineChart_time.getLegend();
        legend.setTextColor(Color.WHITE);
        legend.setEnabled(false);

        lineChart_time.animateXY(1000,1000);             // X축, Y축 애니메이션 속도.
        lineChart_time.invalidate();
        time_graph_toggle=1;
    }

    // "일간별 조회"의 세부사항 버튼 눌렀을 때.
    public void Detail_time_btn(View v){
        if(Detail_time_layout.getVisibility()==View.VISIBLE){
            Detail_time_layout.setVisibility(View.GONE);
        }else{
            Detail_time_layout.setVisibility(View.VISIBLE);
        }
    }

    // "그래프" 버튼 눌렀을 때. -> 그래프 창 출력/사라짐
    public void Detail_btn_graph(View v){
        if(Based_date_graph_layout.getVisibility()==View.VISIBLE){
            Based_date_graph_layout.setVisibility(View.GONE);
        }else{
            Based_date_graph_layout.setVisibility(View.VISIBLE);
        }
    }

    // "일간별 조회" 결과에 따른 상세정보 메소드
    public void ShowDetail_time(String[] reslut){

        float max_value=0;
        float min_value=10000;
        float sum_value=0;


        for(int i=3; i<reslut.length; i++){
            // result가 "" 상태이면 에러. -> 종료,
            max_value=Math.max(max_value, Float.parseFloat(reslut[i]));
            min_value=Math.min(min_value, Float.parseFloat(reslut[i]));
            sum_value+=Float.parseFloat(reslut[i]);
        }

        String max=String.format("%.2f", max_value);
        String min=String.format("%.2f", min_value);
        String avg=String.format("%.2f", sum_value/reslut.length-3);

        Detail_Max_Value_time.setText("최고 사용량 : "+max+" [wh]");
        Detail_Min_Value_time.setText("최저 사용량 : "+min+" [wh]");
        Detail_Avg_Value_time.setText("평균 사용량 : "+avg+" [wh]");
    }

//******************************************************************************************************
//******************************************************************************************************

    // "월간 조회"의 "시작 월 선택" 버튼 눌렀을 때
    public void select_first_month(View v){
        Calendar c = Calendar.getInstance();
        int cyear = c.get(Calendar.YEAR);
        int cmonth = c.get(Calendar.MONTH);

        DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                //날짜가 선택 된 이후 할일
                //ex.텍스트뷰에 날짜를 표시등등
                first_Year_month=year;
                first_Month_month=monthOfYear+1;
                Select_first_month.setText(first_Year_month+"년 "+first_Month_month+"월");
            }
        };

        final DatePickerDialog dpd = new DatePickerDialog(this, mDateSetListener, cyear, cmonth, 1);

        dpd.getDatePicker().init(cyear, cmonth, 1, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dpd.setTitle("년도 선택");
            }
        });
        Calendar calendar = Calendar.getInstance();
        //현재 년도 표현을 max로 설정하여 현재 년도 이후 년도는 나오지 않도록 설정
        dpd.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2010);

        //2000년를 min으로 설정하여 2000년 이전 년도는 나오지 않도록 설정
        dpd.getDatePicker().setMinDate(cal.getTimeInMillis());
        dpd.setTitle("년도 선택");

        try {
            Field[] f = dpd.getClass().getDeclaredFields();
            for (Field dateField : f) {
                if (dateField.getName().equals("mDatePicker")) {
                    dateField.setAccessible(true);

                    DatePicker datePicker = (DatePicker) dateField
                            .get(dpd);

                    Field datePickerFields[] = dateField.getType()
                            .getDeclaredFields();

                    for (Field datePickerField : datePickerFields) {
                        if ("mDaySpinner".equals(datePickerField.getName())) {
                            datePickerField.setAccessible(true);
                            Object dayPicker = new Object();
                            dayPicker = datePickerField.get(datePicker);
                            ((View) dayPicker).setVisibility(View.GONE);
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        dpd.getDatePicker().setCalendarViewShown(false);
        dpd.show();
    }

    // "월간 조회"의 "종료 월 선택" 버튼 눌렀을 때
    public void select_second_month(View v){
        Calendar c = Calendar.getInstance();
        int cyear = c.get(Calendar.YEAR);
        int cmonth = c.get(Calendar.MONTH);

        DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                //날짜가 선택 된 이후 할일
                //ex.텍스트뷰에 날짜를 표시등등
                second_Year_month=year;
                second_Month_month=monthOfYear+1;
                Select_second_month.setText(second_Year_month+"년 "+second_Month_month+"월");
            }
        };

        final DatePickerDialog dpd = new DatePickerDialog(this, mDateSetListener, cyear, cmonth, 1);

        dpd.getDatePicker().init(cyear, cmonth, 1, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dpd.setTitle("년도 선택");
            }
        });
        Calendar calendar = Calendar.getInstance();
        //현재 년도 표현을 max로 설정하여 현재 년도 이후 년도는 나오지 않도록 설정
        dpd.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2010);

        //2000년를 min으로 설정하여 2000년 이전 년도는 나오지 않도록 설정
        dpd.getDatePicker().setMinDate(cal.getTimeInMillis());
        dpd.setTitle("년도 선택");

        try {
            Field[] f = dpd.getClass().getDeclaredFields();
            for (Field dateField : f) {
                if (dateField.getName().equals("mDatePicker")) {
                    dateField.setAccessible(true);

                    DatePicker datePicker = (DatePicker) dateField
                            .get(dpd);

                    Field datePickerFields[] = dateField.getType()
                            .getDeclaredFields();

                    for (Field datePickerField : datePickerFields) {
                        if ("mDaySpinner".equals(datePickerField.getName())) {
                            datePickerField.setAccessible(true);
                            Object dayPicker = new Object();
                            dayPicker = datePickerField.get(datePicker);
                            ((View) dayPicker).setVisibility(View.GONE);
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        dpd.getDatePicker().setCalendarViewShown(false);
        dpd.show();
    }

    // "월간 조회"의 "조회" 버튼 눌렀을 때
    public void btn_search_month(View v){
        //Toast.makeText(getApplicationContext(), "기간별 조회 합니다.", Toast.LENGTH_SHORT).show();
        task3=new search_data_month();

        ddd="";

        if(graph_toggle_month==1){
            date_month.clear();
            avgs_month.clear();

            labels_month.clear();
            entries_month.clear();
            lineData_month.clearValues();
            lineDataSet_month.clear();
            lineChart_month.clear();


            //Toast.makeText(getApplicationContext(), first_Year_month+"."+first_Month_month+"-"+second_Year_month+"."+second_Month_month, Toast.LENGTH_SHORT).show();
            task3.execute(Integer.toString(first_Year_month), Integer.toString(first_Month_month), Integer.toString(second_Year_month), Integer.toString(second_Month_month));    // int형 이기에 String으로 변환 필요.
        }else{
            //Toast.makeText(getApplicationContext(), first_Year_month+"."+first_Month_month+"-"+second_Year_month+"."+second_Month_month, Toast.LENGTH_SHORT).show();
            task3.execute(Integer.toString(first_Year_month), Integer.toString(first_Month_month), Integer.toString(second_Year_month), Integer.toString(second_Month_month));    // int형 이기에 String으로 변환 필요.
        }
    }

    // "월간 조회"의 백 그라운드 스레드
    public class search_data_month extends AsyncTask<String, Void, String> {

        String first_Year = null;    // 첫번째 선택한 년.
        String first_Month= null;    // 첫번째 선택한 월.
        String second_Year= null;    // 두 번째 선택한 년.
        String second_Month= null;   // 두 번째 선택한 월.

        // BackGround 스레드를 실행하기 전에 할 행동들을 정의한다. 변수 초기화, 네트워크 연결 등.
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        // doInBackground 메소드에의 결과를 가지고 UI작업 수행.
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(Month_Tag, "POST response  - " + result);
            //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();

            String[] date_array=new String[date_month.size()];
            String[] avgs_array=new String[avgs_month.size()];

            if(result.equals("Error Code 1")){
                ddd="종료월이 시작월을 앞설 수 없습니다. \n 다시 선택하세요.";
                Toast.makeText(getApplicationContext(), ddd, Toast.LENGTH_SHORT).show();
            }else if(result.equals("Error Code 3")){
                ddd="범위를 선택하세요.";
                Toast.makeText(getApplicationContext(), ddd, Toast.LENGTH_SHORT).show();
            }else{
                if(avgs_array.length>0){
                    // 그래프 그리는 메소드.
                    //Toast.makeText(getApplicationContext(), avgs_array[0]+", "+avgs_array[1], Toast.LENGTH_SHORT).show();

                    drawGraph_month(date_array, avgs_array);

                    HashMap<String, Integer[]> calc=CalcCharge_month(date_array,avgs_array);

                    String charge_metion="";
                    for(int i=0; i<date_array.length; i++){
                        charge_metion+=date_array[i].toString()+"월 요금\n";
                        charge_metion+="　사용량 : "+calc.get("사용량")[i]+"[Kwh]"+"\n";
                        charge_metion+="　청구금액 : "+decimalFormat.format(calc.get("총요금")[i])+"\n";

                        // 더 출력을 원하면 여기에 기입.
                    }

                    Detail_charge_range_month.setText(first_Year+"."+first_Month+"월"+"~"+second_Year+"."+second_Month+"월의 요금정보");
                    Detail_charge_value_month.setText(charge_metion);

                    ShowDetail_month(calc);

                    //Detail_charge_value.setText("사용량 : "+calc.get("사용량")+"[Kwh]"+"\n청구금액 : "+decimalFormat.format(calc.get("총요금"))+"\n(청구 금액 = 전기 요금계 + 부가가치세 + 산업기반기금)"+"\n\n[세부내용]"+"\n기　본　료 : "+decimalFormat.format(calc.get("기본료"))+"\n전력량요금 : "+decimalFormat.format(calc.get("전력량요금"))+"\n전기요금계 : "+decimalFormat.format(calc.get("전기요금계"))+"\n(전기요금계 = 기본요금 + 전력량요금 - 필수사용량 보장공제)"+"\n부가가치세 : "+decimalFormat.format(calc.get("부가가치세"))+"\n산업기반기금 : "+decimalFormat.format(calc.get("산업기반기금")));
                }else{
                    Toast.makeText(getApplicationContext(),"선택하신 기간의 데이터가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        }

        //  백그라운드 작업 진행.
        @Override
        protected String doInBackground(String... params) {
            first_Year = params[0];    // 첫번째 선택한 년.
            first_Month= params[1];    // 첫번째 선택한 월.
            second_Year=params[2];    // 두 번째 선택한 년.
            second_Month=params[3];   // 두 번째 선택한 월.

            if(Integer.parseInt(second_Month)<Integer.parseInt(first_Month)){
                return "Error Code 1";
            }

            if(!first_Year.equals("0") && !first_Month.equals("0")){
                String serverURL = "http://211.178.109.157/Haniem/select_basedMonth.php";
                String postParameters = "first_Year=" + first_Year + "&first_Month=" + first_Month;
                postParameters+="&second_Year="+second_Year+"&second_Month="+second_Month;

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

                    Log.d(Month_Tag, " 전송한 데이터(파라미터) - " + postParameters);
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
                        String value=(jOb.getString("Value").equals("null")) ?  "0" : jOb.getString("Value");
                        ddd += jOb.getString("Month")+"\n"+ value +"\n";
                    }

                    String[] parse_data=ddd.split("\n");

                    for(int i=0; i<parse_data.length; i=i+2){
                        date_month.add(parse_data[i]);
                        avgs_month.add(parse_data[i+1]);
                    }

                    return ddd;

                }catch (Exception e){
                    return new String("Exception Error: " + e.getMessage());
                }
            }else{
                return "Error Code 3";
            }
        }
    }

    // 선택한 기간에 따른 그래프 그리는 메소드
    public void drawGraph_month(String[] date_array, String[] avgs_array){

        date_month.toArray(date_array);
        avgs_month.toArray(avgs_array);

        // entries   좌표 값.
        //entries=new ArrayList<>();

        for(int i=0; i<date_month.size(); i++){
            entries_month.add(new Entry(Float.parseFloat(avgs_array[i]), i));
        }

        // labels x축 이름
        // labels=new ArrayList<>();
        for(int i=0; i<date_array.length; i++){
            labels_month.add(date_array[i]+"월");
        }

        lineDataSet_month = new LineDataSet(entries_month, date_array[0]+"월"+"~"+date_array[date_array.length-1]+"월의 사용량");
        lineDataSet_month.setColors(ColorTemplate.PASTEL_COLORS);     // 색상 관련.
        lineDataSet_month.setDrawCubic(true);                         // 각각의 포인트를 곡선으로 연결.
        lineDataSet_month.setDrawFilled(true);                        // 선아래로 색상표시
        lineDataSet_month.setColor(Color.BLACK);                      // 그래프 라인의 색상 표시.
        lineDataSet_month.setDrawValues(true);                        // 각 포인트의 값을 표시
        lineDataSet_month.setFillColor(Color.BLUE);                   // 선 아래의 채워지는 색상.
        lineDataSet_month.setHighlightEnabled(false);                 // ???

        lineData_month = new LineData(labels_month, lineDataSet_month);
        lineData_month.setDrawValues(true);                           // 좌표 포인트 위에 데이터 수치 표시
        lineData_month.setHighlightEnabled(true);
        lineData_month.setValueTextSize(10);

        lineChart_month = (LineChart) findViewById(R.id.chart_month);
        lineChart_month.setData(lineData_month);                        // set the data and list of lables into chart
        lineChart_month.getAxisRight().setEnabled(false);         // y축 우측 범위표 제거.
        lineChart_month.getAxisLeft();                            //lineChart.getAxisLeft().setEnabled(false);
        // 가로 세부선 없애고 좌측 y축 범위표 삭제.(false)
//        lineChart.setBackgroundColor(Color.WHITE);
        lineChart_month.setContentDescription("선택기간 사용량"); // ???
//        lineChart.setBackgroundColor(Color.WHITE);        --> 그래프 레이아웃 테두리 색상이 바뀜.
        lineChart_month.setBorderColor(Color.WHITE);


        YAxis y = lineChart_month.getAxisRight();     // y축의 방향 조절 getAxisRight => 왼쪽
        y.setDrawAxisLine(true);                //  ??
        y.setTextColor(Color.BLACK);            // 우측 범위 텍스트 색상 조절.

        XAxis x = lineChart_month.getXAxis();
        x.setAvoidFirstLastClipping(true);
        x.setTextColor(Color.BLACK);            // x축 라벨의 텍스트 색상
        x.setDrawAxisLine(false);               // ?? true와 false와 차이 못 찾음.
        x.setDrawGridLines(true);               // x축 데이터에 대한 세부선(Grid line)을 그린다.
        x.setDrawLabels(true);                  // x축 라벨을 그린다.
        x.setEnabled(true);                     // 위의 XAis에 관련된 모든 것을 조절.

        // legend 그래프 위에 위치시키는 상태표.
        Legend legend = lineChart_month.getLegend();
        legend.setTextColor(Color.BLACK);
        legend.setEnabled(true);

        lineChart_month.animateXY(1000,1000);             // X축, Y축 애니메이션 속도.
        lineChart_month.invalidate();
        graph_toggle_month=1;
    }

    // "그래프" 버튼 눌렀을 때
    public void Detail_btn_graph_month(View v){
        if(Based_month_graph_layout.getVisibility()==View.VISIBLE){
            Based_month_graph_layout.setVisibility(View.GONE);
        }else{
            Based_month_graph_layout.setVisibility(View.VISIBLE);
        }
    }

    // "요금조회" 버튼 눌렀을 때
    public void Detail_btn_charge_month(View v){
        if(Detail_charge_layout_month.getVisibility()==View.VISIBLE){
            Detail_charge_layout_month.setVisibility(View.GONE);
        }else{
            Detail_charge_layout_month.setVisibility(View.VISIBLE);
        }
    }

    // "조회 내용" 버튼 눌렀을 때
    public void Detail_btn_month(View v){
        if(Detail_layout_month.getVisibility()==View.VISIBLE){
            Detail_layout_month.setVisibility(View.GONE);
        }else{
            Detail_layout_month.setVisibility(View.VISIBLE);
        }
    }

    // 선택한 월에 따른 요금계산 메소드
    public HashMap<String, Integer[]> CalcCharge_month(String[] date_array, String[] avgs_array){
        String charge="사용량 계산 로그:";
        HashMap<String, Integer[]> calc_detail=new HashMap<>();

        // 1. 선택 기간에 대한 요금 정보를 출력.
        // 2. 6월 15일 ~ 6월 20일 까지가 조회 범위라면 15, 16, 17, 18, 19, 20 일에 대한 각각의 요금을 출력.

        /*
        주택용 저압 - 일반 다세대, 단독 주택, 빌라  --> 기준으로 구성
        처음 200KWh까지 1KHW당 93.3원
        200KWh~400KWh까지 1Kwh당 187.9원
        400KWh 초과시 1KWh 당 280.6원

        */
        // 주택용 고압 - APT(변전실이 있는 건물)


        Integer[] usage_month=new Integer[date_month.size()];               // 사용량
        Integer[] electricity_rate_month=new Integer[date_month.size()];    // 전력량 요금
        Integer[] elec_base_month=new Integer[date_month.size()];
        Integer[] charge_sum_month=new Integer[date_month.size()];          // 사용총 요금
        Integer[] vat_month=new Integer[date_month.size()];                 // 부가가치세
        Integer[] Base_rate_month=new Integer[date_month.size()];           // 기본료
        Integer[] Industry_month=new Integer[date_month.size()];            // 전력 산업 기반기금
        Integer[] Guarantee_month=new Integer[date_month.size()];

        for(int i=0; i<date_month.size(); i++){
            usage_month[i]=Integer.parseInt(avgs_array[i].substring(0,avgs_array[i].indexOf(".")));

            if(usage_month[i]<=200){  // 1단계
                Base_rate_month[i]=910;
                Guarantee_month[i]=4000;
                electricity_rate_month[i]=(int)(usage_month[i]*93.3);
                elec_base_month[i]=Base_rate_month[i]+electricity_rate_month[i]-Guarantee_month[i];

            }else if(usage_month[i]>200 && usage_month[i]<=400){
                Base_rate_month[i]=1600;
                electricity_rate_month[i]=(int)(200*93.3+((usage_month[i]-200)*187.9));
                elec_base_month[i]=Base_rate_month[i]+electricity_rate_month[i];

            }else if(usage_month[i]>400){
                Base_rate_month[i]=7300;
                electricity_rate_month[i]=(int)((200*93.3)+(200*187.9)+((usage_month[i]-400)*280.6));
                elec_base_month[i]=Base_rate_month[i]+electricity_rate_month[i];
            }

            vat_month[i]=(int)(elec_base_month[i]*0.1);
            Industry_month[i]=(int)(elec_base_month[i]*0.037);
            charge_sum_month[i]=vat_month[i]+elec_base_month[i]+Industry_month[i];
            charge_sum_month[i]=(charge_sum_month[i]/10)*10;   // 10원 미만 절사
        }

        calc_detail.put("사용량",usage_month);
        calc_detail.put("전력량요금",electricity_rate_month);
        calc_detail.put("기본료",Base_rate_month);
        calc_detail.put("전기요금계", elec_base_month);
        calc_detail.put("총요금",charge_sum_month);
        calc_detail.put("부가가치세",vat_month);
        calc_detail.put("산업기반기금",Industry_month);
        calc_detail.put("필수사용량보장공제",Guarantee_month);

        return calc_detail;
    }

    // 선택한 월에 따른 조회내용 계산 메소드
    public void ShowDetail_month(HashMap<String, Integer[]> charge_info){

        //  1. 일평균이 가장 높은 날짜 표시.
        //  2. 일평균이 가장 낮은 날짜 표시.
        //  3. 선택한 기간의 평균적인 사용량 표시.

        float max_value=0;
        float min_value=10000;
        float sum_value=0;

        int maxIndex=0;
        int minIndex=0;

        for(int i=0; i<date_month.size(); i++){
            max_value=Math.max(max_value, (charge_info.get("사용량")[i]));
            if(charge_info.get("사용량")[i]==max_value){
                maxIndex=i;
            }

            min_value=Math.min(min_value, (charge_info.get("사용량")[i]));
            if(charge_info.get("사용량")[i]==min_value){
                minIndex=i;
            }

            sum_value+=charge_info.get("사용량")[i];
        }


        String max=String.format("%.0f", max_value);
        String min=String.format("%.0f", min_value);
        String avg=String.format("%.0f", sum_value/date_month.size());

        Detail_Max_Value_month.setText("최고 사용량("+date_month.get(maxIndex)+"월"+"): "+max+" [wh]");
        Detail_Min_Value_month.setText("최저 사용량("+date_month.get(minIndex)+"월"+") : "+min+" [wh]");
        Detail_Avg_Value_month.setText("평균 사용량 : "+avg+" [wh]");
    }
}