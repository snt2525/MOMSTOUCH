package kr.co.company.locationtest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends Activity {

    static final int REQUEST_ENABLE_BT=10;
    BluetoothAdapter mBluetoothAdapter;
    int mPairedDeviceCount=0;
    Set<BluetoothDevice> mDevices;
    BluetoothDevice mRemoteDevice;
    BluetoothSocket mSocket = null;
    OutputStream mOutputStream = null;
    InputStream mInputStream = null;

    TextView temper;
    TextView air,airSuggetion;
    ImageView weatherIcon,airIcon;
    double locationX, locationY;
    private GpsInfo gps;
    private AirPollution airTask;
    private Weather task;
    Button alramBtn;
    byte[] readBuffer;
    int readBufferPosition;
    char mCharDelimiter = '\n';
    Location location;
    Thread mWorkerThread;
    EditText mEditReceive;
    EditText mEditSend;
    String mStrDelimiter ="\n";
    double strollerLat;
    double strollerLong;
    GpsInfo gpsInfo;
    Vibrator vibe;
    LatLng latLng;
    int flag = 0;
    int alramFlag = 0;
    int smellFlag = 0;
    int strollerFlag = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        gpsInfo = new GpsInfo(this);
        temper = (TextView) findViewById(R.id.temper);
        weatherIcon = (ImageView) findViewById(R.id.weatherIcon);
        airIcon=(ImageView)findViewById(R.id.airIcon);
        air = (TextView) findViewById(R.id.air);
        airSuggetion = (TextView) findViewById(R.id.airSuggestion);

        //기상정보 현재날씨
        Weather task = new Weather(temper, weatherIcon);
        task.execute("http://www.kma.go.kr/wid/queryDFS.jsp?gridx="+(int)locationX+"gridy="+(int)locationY);

        //미세먼지 예측농도
        airTask = new AirPollution(air,airIcon,airSuggetion);
        airTask.execute("http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/" +
            "getMsrstnAcctoRltmMesureDnsty?stationName=%EC%A2%85%EB%A1%9C%EA%B5%AC&dataTerm=month&pageNo=1" +
            "&numOfRows=10&ServiceKey=8H2JcItuoZ6opOXGR186YwAUHnpdrMZrix2HJafBGAJH7%2FW0vMtYQvNERfJ1pr7BMImLBRKKL8n1lo5vTg0eow%3D%3D&ver=1.2");

        Intent initWeather = new Intent(this,AirPollutionReceiver.class);
        PendingIntent pIntent = PendingIntent.getBroadcast(this,1,initWeather,PendingIntent.FLAG_NO_CREATE);
        if(pIntent !=null){
            Switch sw = (Switch)findViewById(R.id.sw1);
            sw.setChecked(true);
        }
    }


    public void locationClick(View v) {
        gps = new GpsInfo(MainActivity.this);

        if (gps.isGetLocation()) {
            locationX = gps.getLatitude();
            locationY = gps.getLongitude();

            task.execute("http://www.kma.go.kr/wid/queryDFS.jsp?gridx="+(int)locationX+"gridy="+(int)locationY);
            airTask.execute("http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/" +
                    "getMsrstnAcctoRltmMesureDnsty?stationName=%EC%A2%85%EB%A1%9C%EA%B5%AC&dataTerm=month&pageNo=1" +
                    "&numOfRows=10&ServiceKey=8H2JcItuoZ6opOXGR186YwAUHnpdrMZrix2HJafBGAJH7%2FW0vMtYQvNERfJ1pr7BMImLBRKKL8n1lo5vTg0eow%3D%3D&ver=1.2");

        } else {
            gps.showSettingsAlert();
        }

    }

    public void switchs(View v) {
        Switch sw = (Switch) v;
        switch (sw.getId()){
            case R.id.sw1 :
                AirPollutionReceiver airPollutionReceiver = new AirPollutionReceiver();
                Intent intent = new Intent(this, airPollutionReceiver.getClass());
                //TextView temper, ImageView weatherIcon

                WeatherAlarm weatherAlarm = new WeatherAlarm(this,intent);
                if(sw.isChecked()){
                    Toast.makeText(getApplicationContext(), "날씨 ON", Toast.LENGTH_SHORT).show();
                    weatherAlarm.setAlarm();
                }else{
                    Toast.makeText(getApplicationContext(), "날씨 OFF", Toast.LENGTH_SHORT).show();
                    weatherAlarm.releaseAlarm();
                }
                break;
//            case R.id.sw2 :
//                //switch on
//                if(sw.isChecked()){
//                    checkBluetooth();
//                }else{ //switch off
//                    Toast.makeText(getApplication(),"제동경보 비활성화",Toast.LENGTH_SHORT).show();
//                }
//                break;
            case R.id.sw3 :
                if(sw.isChecked()){
                    smellFlag = 1;
                        checkBluetooth();
                    Toast.makeText(getApplicationContext(), "냄새알림 ON", Toast.LENGTH_SHORT).show();
                }else{
                    smellFlag = 0;
                    Toast.makeText(getApplicationContext(), "냄새알림 OFF", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.sw4 :
                if(sw.isChecked()){
                    strollerFlag = 1;
                    checkBluetooth();
                    Toast.makeText(getApplicationContext(), "도난방지 ON", Toast.LENGTH_SHORT).show();
                }else{
                    strollerFlag = 0;
                    Toast.makeText(getApplicationContext(), "도난방지 OFF", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    // 블루투스 연결 여부로 selectDevice를 보여줄지 cancel 할지 결정하는 메소드
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case REQUEST_ENABLE_BT:
                if(resultCode==RESULT_OK){
                    selectDevice();
                }else if(resultCode == RESULT_CANCELED){

                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //블루투스 연결상태 확인
    public void checkBluetooth(){

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> set =  mBluetoothAdapter.getBondedDevices();

        if( set.size() <= 0){
            //연결되있지 않은 경우 연결시도
            askConnectBluetooth();
        }
    }
    void askConnectBluetooth(){  //블루투스를 연결할까요? 하고 뜬다.

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter==null){
            finish();
        }else{
            if(!mBluetoothAdapter.isEnabled()){
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

            }else if(mBluetoothAdapter.isEnabled()) {
                selectDevice();
            }
        }
    }
    void selectDevice(){
        mDevices = mBluetoothAdapter.getBondedDevices();
        mPairedDeviceCount = mDevices.size();

        if(mPairedDeviceCount == 0){
            //스위치 비활성화
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("블루투스 장치 선택");

        List<String> listItems = new ArrayList<String>();
        for(BluetoothDevice device : mDevices){
            listItems.add(device.getName());
        }
        listItems.add("취소");

        final CharSequence[] items = listItems.toArray(new CharSequence[listItems.size()]);

        builder.setItems(items,new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if(item == mPairedDeviceCount){
                    // 스위치 비활성화
                    Toast.makeText(getApplication(),"장치를 연결해 주세요",Toast.LENGTH_SHORT).show();
//                    sw1.setChecked(false);
                    //bluetooth
                }else{
                    connectToSelectdDevice(items[item].toString());
                }
            }
        });
        builder.setCancelable(false);
        AlertDialog alert = builder.create();
        alert.show();
    }
    BluetoothDevice getDeviceFromBondedList(String name) {
        BluetoothDevice selectedDevice = null;

        for (BluetoothDevice device : mDevices) {
            if (name.equals(device.getName())) {
                selectedDevice = device;
                break;
            }
        }
        return selectedDevice;
    }
    @Override
    protected void onDestroy(){
        try{
            mWorkerThread.interrupt();
            mInputStream.close();
            mOutputStream.close();
            mSocket.close();
        }catch(Exception e){
            super.onDestroy();
        }
    }



    void connectToSelectdDevice(String selectedDeviceName) {
        mRemoteDevice = getDeviceFromBondedList(selectedDeviceName);
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

        try {
            // 소켓 생성
            mSocket = mRemoteDevice.createRfcommSocketToServiceRecord(uuid);
            // RFCOMM 채널을 통한 연결
            mSocket.connect();

            mInputStream = mSocket.getInputStream();
            mOutputStream = mSocket.getOutputStream();

            beginListenForData();
        }catch(Exception e) {

            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            finish();

        }
    }
    double deg2rad(double deg){
        return (double)(deg * Math.PI / (double)180d);
    }

    double rad2deg(double rad){
        return (double)(rad * (double)180d / Math.PI);
    }

    public String distance(double lat1, double lon1, double lat2, double lon2){

        double theta, dist;
        theta = lon1 - lon2;


        dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1609.344;
        String meter = new String();

        int temp = (int)dist/1000;
//        mEditSend.setText(dist+"/" + temp);
        if(temp>0){
            dist = dist/1000;
            DecimalFormat df = new DecimalFormat("#.##");

            meter = df.format(dist)+" Km";
        }else{
            meter = String.format("%d M",Math.round(dist));
            if(Math.round(dist)>5 && alramFlag==0){
                alramFlag = 1;
                Intent intent = new Intent(this,AlramActivity.class);
                intent.putExtra("strollLat",strollerLat);
                intent.putExtra("strollLong",strollerLong);
                startActivity(intent);

            }
        }
        return meter;
    }

    void beginListenForData(){
        final Handler handler = new Handler();
        readBuffer = new byte[1024];
        readBufferPosition = 0;
        mWorkerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(!Thread.currentThread().isInterrupted()){
                    try {
                        int bytesAvailable = mInputStream.available();
                        if(bytesAvailable>0){
                            byte[] packectBytes = new byte[bytesAvailable];
                            mInputStream.read(packectBytes);
                            for(int i=0;i<bytesAvailable;i++){
                                byte b = packectBytes[i];
                                if(b==mCharDelimiter){
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes,"UTF-8");
                                    readBufferPosition =0;

                                    handler.post(new Runnable(){
                                        public void run(){
                                            //수신된 문자열 데이터에 대한 처리 작업
                                            String lat ="Lat";
                                            String lng ="Long";
                                            String high="High";
                                            String fresh = "Fresh";
                                            if(data.trim().matches(fresh+".*")&& flag !=0){
                                                flag = 0;
                                            }
                                            //smellFlag 가 switchOn 일 때
                                            if(data.trim().matches(high+".*") && smellFlag==1){
                                                flag++;
                                                if(data.trim().matches(high+".*"))
                                                    if(flag>=10) {
                                                        getAlertDialog();
                                                        flag = 0;
                                                    }
                                            }

                                            if(data.trim().matches(lat+".*")){
                                                    String[] a1 = data.trim().split(":");
                                                    strollerLat = Double.parseDouble(a1[1]);
                                                }

                                                if(data.trim().matches(lng+".*")){
                                                    String[] a1 = data.trim().split(":");
                                                    strollerLong= Double.parseDouble(a1[1]);
                                            }
                                            location = gpsInfo.getLocation();
                                            latLng = new LatLng(location.getLatitude(),location.getLongitude());
                                         mEditSend.setText(mEditSend.getText()+"유모차 :" +(strollerLat)+"/"+(strollerLong)+"\n");
//                                            mEditSend.setText(mEditSend.getText()+"GPS : " +latLng.latitude+"/"+latLng.longitude+"\n");
                                            if(strollerLat!=0.0&&strollerLong!=0.0 && strollerFlag==1) {
                                                String meter = distance(latLng.latitude, latLng.longitude, strollerLat, strollerLong);
//                                                Toast.makeText(getApplicationContext(),meter,Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }else{
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }catch(IOException e){
                        finish();
                    }
                }
            }
        });
        mWorkerThread.start();
    }

    void sendData(String msg){
        msg+= mStrDelimiter;
        try{
            mOutputStream.write(msg.getBytes());
        }catch(Exception e){
            //문자열 전송 도중 오류가 발생한 경우
            finish();
        }
    }

    void getAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(3000);
        builder.setTitle("냄새 알림")
                .setMessage("기저귀를 확인해 주세요")
                .setCancelable(false)
                .setNeutralButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        vibe.cancel();
                        dialog.cancel();
                    }
                }).create().show();
    }
}