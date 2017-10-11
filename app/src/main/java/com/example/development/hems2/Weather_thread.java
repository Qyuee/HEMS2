package com.example.development.hems2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.os.Handler;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Development on 2017-08-02.
 */

// Http 통신을 통해서 기상청의 날씨 정보를 가져오는 스레드를 정의.
// 핸들러는 이 스레드를 사용하고자 하는 클래스에서 정의.

public class Weather_thread extends Thread{
    private String Tag_Nt="Network_Thread : ";
    Handler handler;
    Message msg;
    Bundle bd;
    String zoneCode="";

    // Http 통신 연결을 위한 클래스.
    URL url;
    HttpURLConnection httpURLConnection;


    // 네트워크 통신에서 입출력을 위한 클래스.
    OutputStream outputStream;
    InputStream inputStream;
    InputStreamReader inputStreamReader;
    BufferedReader bufferedReader;
    StringBuilder sb;

    Weather_thread(Handler handler, String Code){
        this.handler=handler;       // 외부에서 받은 핸들러를 내부로 지정.
        this.zoneCode=Code;
    }

    // 스레드가 호출되었을 때 동작의 순서 및 반복도.(지속성)
    @Override
    public void run(){
        while(isInterrupted() == false){
            try{
                Network();
                Thread.sleep(1000*60*60*3);   // 3시간에 한 번 날씨 정보 가져옴.
            }catch (InterruptedException e){
                e.printStackTrace();
                Log.e(Tag_Nt, "Network_Thread 스레드 중지");
                Thread.currentThread().interrupt();  // 스레드가 중지 되었는지 한번더 확인.
            }
        }
    }

    public void Network(){
        String tmp="";          // Http통신으로부터 받아오는 결과를 임시로 받아낼 문자열.
        String buffer="";

        // 기상청의 RSS url 주소 위치. http:// ~~~~?zone=지역번호
        // 지역 번호는 Spinner를 사용하여 입력 받는다.
        String serverURL="http://www.kma.go.kr/wid/queryDFSRSS.jsp?zone="+this.zoneCode;
        try{
            url=new URL(serverURL);
            httpURLConnection=(HttpURLConnection) url.openConnection();

            httpURLConnection.setRequestMethod("POST");
//            httpURLConnection.setConnectTimeout(5000);
//            httpURLConnection.setReadTimeout(5000);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setUseCaches(false);

            int responseCode=httpURLConnection.getResponseCode();
            if(responseCode==httpURLConnection.HTTP_OK){
                inputStream=httpURLConnection.getInputStream();
                Log.e(Tag_Nt, "기상청 url과 http연결 성공, 입력 스트림으로 수신.");

                inputStreamReader=new InputStreamReader(inputStream);
                BufferedReader in =new BufferedReader(inputStreamReader);

                while((tmp=in.readLine())!=null){
                    buffer+=tmp.trim()+"\n";
                }
                in.close();

            }else{
                inputStream=httpURLConnection.getErrorStream();
                Log.e(Tag_Nt, "기상청 url과 http연결 실패.");
            }

            // 문자열 buffer을 바이트 단위로 처리하여 xml 형식으로 바꾸는 듯..?
            ByteArrayInputStream bai = new ByteArrayInputStream(buffer.getBytes());
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(bai);

            // 원하는 데이터를 파싱하는 메소드 호출. doc를 넘긴다.
            Xml_to_String(doc);

        }catch (IOException e){
            Log.e(Tag_Nt, "IOException 에러 : ");
            e.printStackTrace();
        }catch (ParserConfigurationException e){
            e.printStackTrace();
        }catch (SAXException e){
            e.printStackTrace();
        }
    }

    class weather{
        String day=null;
        String[] hour=null;
        String[] temp=null;
        String[] status=null;
        String[] humi=null;

        weather(String day, String[] hour, String[]temp, String[] status, String[] humi){
            this.day=day;
            this.hour=hour;
            this.temp=temp;
            this.status=status;
            this.humi=humi;
        }
    }

    public void Xml_to_String(Document doc){
        String result="";
        String area="";
        String today="";
        String tomorrow="";
        String day_after_tomorrow="";

        String[] hour=null;
        String[] day=null;
        String[] temp=null;
        String[] status=null;
        String[] humi=null;

        String[][] www=null;

        // 기상청 rss 정보(xml)를 파싱하여 원하는 값을 String[], String에 저장한다.
        // 여기서 Message, bundle, handler를 사용하여 스레드를 호출한 곳으로 정보를 넘긴다.

        NodeList area_node_list=doc.getElementsByTagName("item");

        for(int i=0; i<area_node_list.getLength(); i++){
            for(Node node=area_node_list.item(i).getFirstChild(); node!=null; node=node.getNextSibling()){

                if(node.getNodeName().equals("category")) {
                    area = node.getTextContent();
                }
            }
        }

        // "data"라는 노드를 Root 노드로 지정한다.
        NodeList root=doc.getElementsByTagName("data");
        hour=new String[root.getLength()];

        day=new String[root.getLength()];

        temp=new String[root.getLength()];

        status=new String[root.getLength()];

        humi=new String[root.getLength()];

        www=new String[root.getLength()][5];

        String[] temp_max=new String[root.getLength()];
        String[] temp_min=new String[root.getLength()];

        for(int i=0; i<root.getLength(); i++){
            for(Node node = root.item(i).getFirstChild(); node!=null; node=node.getNextSibling()){

                if(node.getNodeName().equals("hour")){
                    hour[i]=node.getTextContent();
                    result+="시간 : "+node.getTextContent();
                }else if(node.getNodeName().equals("day")){
                    day[i]=node.getTextContent();
                    result+="  day : "+node.getTextContent();
                }else if(node.getNodeName().equals("temp")) {
                    temp[i]=node.getTextContent();
                    result+="  온도 : " + node.getTextContent();
                }else if(node.getNodeName().equals("tmx")){
                    temp_max[i]=node.getTextContent();
                }else if(node.getNodeName().equals("tmn")){
                    temp_min[i]=node.getTextContent();
                }else if(node.getNodeName().equals("wfKor")){
                    status[i]=node.getTextContent();
                    result+="  날씨 : " + node.getTextContent();
                }else if(node.getNodeName().equals("reh")){
                    humi[i]=node.getTextContent();
                    result+="  습도 : " + node.getTextContent();
                }

            }

            if(day[i].equals("0")){

                if(Integer.parseInt(hour[i])>12){
                    hour[i]="오후 "+(Integer.parseInt(hour[i])-12)+"시";
                }else{
                    hour[i]="오전 "+hour[i]+"시";
                }
                today+=hour[i]+"  온도 : "+temp[i]+"  습도 : "+humi[i]+"  날씨 : "+status[i]+"\n";
            }else if(day[i].equals("1")){

                if(Integer.parseInt(hour[i])>12){
                    if((Integer.parseInt(hour[i])-12)!=12){
                        hour[i]="오후 0"+(Integer.parseInt(hour[i])-12)+"시";
                    }else{
                        hour[i]="오후 "+(Integer.parseInt(hour[i])-12)+"시";
                    }
                }else{
                    if(Integer.parseInt(hour[i])!=12){
                        hour[i]="오전 0"+hour[i]+"시";
                    }else{
                        hour[i]="오전 "+hour[i]+"시";
                    }
                }
                tomorrow+=hour[i]+"  온도 : "+temp[i]+"  습도 : "+humi[i]+"  날씨 : "+status[i]+"\n";

            }else if(day[i].equals("2")){

                if(Integer.parseInt(hour[i])>12){
                    hour[i]="오후 "+(Integer.parseInt(hour[i])-12)+"시";
                }else{
                    hour[i]="오전 "+hour[i]+"시";
                }
                day_after_tomorrow+=hour[i]+"  온도 : "+temp[i]+"  습도 : "+humi[i]+"  날씨 : "+status[i]+"\n";
            }
            result+="\n";
        }

        bd=new Bundle();
        bd.putString("총결과", result);
        bd.putString("오늘", today);
        bd.putString("내일", tomorrow);
        bd.putString("내일모레", day_after_tomorrow);
        bd.putString("지역", area);

        bd.putStringArray("시간", hour);
        bd.putStringArray("날짜", day);
        bd.putStringArray("온도", temp);
        bd.putStringArray("날씨상태", status);
        bd.putStringArray("습도", humi);
        bd.putStringArray("최고기온",temp_max);
        bd.putStringArray("최저온도",temp_min);

        msg=handler.obtainMessage();
        msg.setData(bd);
        handler.sendMessage(msg);

    }
}