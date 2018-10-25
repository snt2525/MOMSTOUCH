package kr.co.company.locationtest;


import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Weather extends AsyncTask<String, Void, Document> {
    Document doc = null;
    TextView temper;
    ImageView weatherIcon;

    public Weather(TextView temper, ImageView weatherIcon) {
        this.temper = temper;
        this.weatherIcon = weatherIcon;

    }

    @Override
    protected Document doInBackground(String... urls) {
        URL url;
        try {
            url = new URL(urls[0]);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder(); //XML문서 빌더 객체를 생성
            doc = db.parse(new InputSource(url.openStream())); //XML문서를 파싱한다.
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

        NodeList nodeList = doc.getElementsByTagName("data");
        //data 태그를 가지는 노드를 찾음, 계층적인 노드 구조를 반환

        //날씨 데이터를 추출
        Node node = nodeList.item(0); //data엘리먼트 노드
        Element fstElmnt = (Element) node;
        NodeList nameList = fstElmnt.getElementsByTagName("temp");
        Element nameElement = (Element) nameList.item(0);
        nameList = nameElement.getChildNodes();
        s = "온도: " + ((Node) nameList.item(0)).getNodeValue() + "º \n";

        NodeList websiteList = fstElmnt.getElementsByTagName("wfKor");
        //<wfKor>맑음</wfKor> =====> <wfKor> 태그의 첫번째 자식노드는 TextNode 이고 TextNode의 값은 맑음
        s += "날씨: " + websiteList.item(0).getChildNodes().item(0).getNodeValue() + "\n";

        temper.setText(s);

        String weather = websiteList.item(0).getChildNodes().item(0).getNodeValue();
        //   weatherIcon = (ImageView) findViewById(R.id.weatherIcon);

        s += "날씨: " + weather + "\n";
        switch (weather) {
            case "맑음":
                weatherIcon.setImageResource(R.drawable.clear);
                break;
            case "구름 조금":
                weatherIcon.setImageResource(R.drawable.partlyclody);
                break;
            case "구름 많음":
                weatherIcon.setImageResource(R.drawable.mostlyclody);
                break;
            case "흐림":
                weatherIcon.setImageResource(R.drawable.cloudy);
                break;
            case "비":
                weatherIcon.setImageResource(R.drawable.rain);
                break;
            case "눈/비":
                weatherIcon.setImageResource(R.drawable.snowrain);
                break;
            case "눈":
                weatherIcon.setImageResource(R.drawable.snow);
                break;
        }

        super.onPostExecute(doc);
    }
}