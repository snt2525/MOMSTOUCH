package kr.co.company.locationtest;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class WeatherAlarm {
    private AlarmManager am;
    private PendingIntent pIntent;

    public WeatherAlarm(Context context, Intent intent){
        am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        pIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 1, intent, 0);
    }
    public void releaseAlarm(){
        am.cancel(pIntent); //알람 해제
        pIntent.cancel();
    }
    public void setAlarm(){

        // YEAR,MONTH,DAY_OF_MONTH,HOUR_OF_DAY,MINUTE,SECOND
        Calendar curTime = new GregorianCalendar(Locale.KOREA);//현재 시간으로 설정 후 시,분,초만 7시로 변경
        curTime.set(Calendar.HOUR_OF_DAY,7);//시
        curTime.set(Calendar.MINUTE,0);//분
        curTime.set(Calendar.SECOND, 0);//초
        long triggerTime = curTime.getTimeInMillis();   //GC로 설정한 시간 Millis로 변경
        long intervalMillis = 1000*60*60*24;            //어떤 시간 기준으로 반복할 지?
        am.setRepeating(AlarmManager.RTC,triggerTime,intervalMillis,pIntent);
    }
}