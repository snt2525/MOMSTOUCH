package kr.co.company.locationtest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by CJHM on 2016-08-25.
 */
public class AlramActivity extends Activity{
    Vibrator vibe;
    MediaPlayer mp ;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alram);
        //Vibrator 객체 생성
        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(3000);
        //소리 객체 생성
        mp = MediaPlayer.create(this,R.raw.siren);
        mp.start();

    }

    public void confirm(View v){

        Button btn = (Button) v;
        switch (btn.getId()){
            case R.id.ok :
                Toast.makeText(getApplicationContext(),"위치추적",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this,MapActivity.class);
                intent.putExtras(getIntent());
                startActivity(intent);
                break;
            case R.id.cancel :
                Toast.makeText(getApplication(),"CANCEL",Toast.LENGTH_SHORT).show();
                vibe.cancel();//진동 종료
                mp.stop();// 소리 종료
                finish(); //Activity 종료
                break;

        }
    }

}
