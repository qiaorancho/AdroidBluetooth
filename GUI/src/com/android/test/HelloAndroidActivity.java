package com.android.test;


import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;


public class HelloAndroidActivity extends Activity {
    /** Called when the activity is first created. */
	 MediaPlayer mPlayer =null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        
        ImageButton mBfood = (ImageButton) findViewById(R.id.imageButton1);
        ImageButton mBwater = (ImageButton) findViewById(R.id.imageButton2);
        ImageButton mBbathroom = (ImageButton) findViewById(R.id.imageButton3);
        ImageButton mByes = (ImageButton) findViewById(R.id.imageButton4);
        ImageButton mBno = (ImageButton) findViewById(R.id.imageButton5);
       
        
        mBfood.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	MymediaPlayer(R.raw.food);
            }
          });
        mBwater.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	MymediaPlayer(R.raw.water);
            }
          });
        mBbathroom.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	MymediaPlayer(R.raw.bathroom);
            }
          });
        mByes.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	MymediaPlayer(R.raw.yes);
            }
          });
        mBno.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	MymediaPlayer(R.raw.no);
            }
          });
        
    }
    
    public  void  MymediaPlayer ( int id){
    	if(mPlayer!=null){
    		mPlayer.pause();
    		mPlayer.stop();
    		mPlayer.reset();
    	}
    	mPlayer=MediaPlayer.create(getBaseContext(), id);
    	mPlayer.start(); // no need to call prepare(); create() does that for you
    }
   

}