package kr.co.company.locationtest;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AirPollutionReceiver extends BroadcastReceiver {
 //   AirPollution airPollution;

    public void onReceive(Context context, Intent notificationIntent){

//
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Resources res = context.getResources();
        PendingIntent contentIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext())
                .setContentTitle("미세먼지 농도알림")
                .setContentText("장시간 실외활동 가급적 자제!")
                .setTicker("상태바 한줄 메시지")
                .setSmallIcon(R.drawable.ic_launcher)// 작은 이미지
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.nomal)) //큰 이미지
                .setContentIntent(contentIntent)
                .setAutoCancel(true) // 누르면 자동으로 꺼짐
                .setWhen(System.currentTimeMillis())
                .setDefaults( Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE|Notification.DEFAULT_LIGHTS)
                .setNumber(13);
        Notification  n = builder.build();
        nm.notify(1234, n);
    }
}