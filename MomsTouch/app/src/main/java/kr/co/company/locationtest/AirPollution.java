package kr.co.company.locationtest;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.Serializable;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class AirPollution extends AsyncTask<String, Void, Document> implements Serializable {
    ImageView airIcon;
    Document doc = null;
    TextView air,airSuggetion;
    String warning = new String();
    int str1 = 0;
    int imageId = 0;
    String str2 = new String();
    String str3 = new String();
    public AirPollution(TextView air){
        this.air = air;
    }
    public AirPollution(TextView air,ImageView airIcon,TextView airSuggetion){
        this.air = air;
        this.airIcon=airIcon;
        this.airSuggetion=airSuggetion;
    }

    public TextView getAir(){
        return air;
    }
    public int getImageId(){
        return imageId;
    }
    public String getWarning(){
        return warning;
    }

    @Override
    protected Document doInBackground(String... urls) {
        URL url;
        try {
            url = new URL(urls[0]);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder(); //XML
            doc = db.parse(new InputSource(url.openStream())); //XML
            doc.getDocumentElement().normalize();

        } catch (Exception e) {
            //Toast.makeText( "Parsing Error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return doc;
    }

    @Override
    protected void onPostExecute(Document doc) {
        String s = "";
        //data태그가 있는 노드를 찾아서 리스트 형태로 만들어서 반환
        NodeList nodeList = doc.getElementsByTagName("item");
        //data 태그를 가지는 노드를 찾음, 계층적인 노드 구조를 반환

        //날씨 데이터를 추출
        Node node = nodeList.item(0); //data엘리먼트 노드
        Element fstElmnt = (Element) node;
        NodeList nameList = fstElmnt.getElementsByTagName("pm10Value24");
        Element nameElement = (Element) nameList.item(0);
        nameList = nameElement.getChildNodes();
        s = "예측농도: " + ((Node) nameList.item(0)).getNodeValue()+"㎍/m²";
        int value = Integer.valueOf(((Node) nameList.item(0)).getNodeValue());
        str1 = value;

        if(value<=30){
            imageId = R.drawable.good;
            airIcon.setImageResource(R.drawable.good);
            warning ="";
        }else if (value <=80){
            imageId = R.drawable.nomal;
            airIcon.setImageResource(R.drawable.nomal);
            warning ="";
        }else if(value <=120){
            imageId = R.drawable.notbad;
            airIcon.setImageResource(R.drawable.notbad);
            warning ="장시간 실외활동 가급적 자제!";
        }else if(value <=200){
            imageId = R.drawable.bad;
            airIcon.setImageResource(R.drawable.bad);
            warning = "무리한 실외활동 자제!";
        }else{
            imageId = R.drawable.verybad;
            airIcon.setImageResource(R.drawable.verybad);
            warning = "실외 활동 제한!";
        }
        airSuggetion.setText(warning);
        str2 = s;
        //   NodeList websiteList = fstElmnt.getElementsByTagName("wfKor");
        //<wfKor>맑음</wfKor> =====> <wfKor> 태그의 첫번째 자식노드는 TextNode 이고 TextNode의 값은 맑음
        //   s += "날씨: " + websiteList.item(0).getChildNodes().item(0).getNodeValue() + "\n";

        air.setText(s);

        //   String weather = websiteList.item(0).getChildNodes().item(0).getNodeValue();
        //   weatherIcon = (ImageView) findViewById(R.id.weatherIcon);
        //  s += "날씨: " + weather + "\n";


        super.onPostExecute(doc);
    }
}