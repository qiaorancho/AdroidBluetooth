package com.app.GestureGame.GameGui;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.GestureGame.R;
import com.app.GestureGame.Profile.ProfileManager;
import com.app.GestureGame.R.color;
import com.app.GestureGame.Controller.MessageController;
import com.app.GestureGame.Debug.GameDataDebugger;

public class GameGui extends Activity implements
AdapterView.OnItemClickListener{

	//get broadcast from service.
	//private Myreceiver mReceiver;
	private boolean Isinfront =true;
	
	//set up views
	private GridView gridView;
	private TextView mScore;
	private TextView mTitle;
	private Button[]  mBt=new  Button[4];
	//set up button index
	private int[] mbtIndex=new int[4];
	
	//set up string for title
	private String mLeftTitle="Gesture Testing";
	private String mRightScore="Score: ";
	
	//set up counter
	private int counter=0;
	private int debugcounter=0;
	
	//sync with MC data
	private MessageController mMsgclr= MessageController.getinstant();
	
	//my number 
	private Integer[] mynumbers = new Integer[] { 
		0,0,0,0,
		0,0,0,0,
		0,0,0,0,
		0,0,0,0};
	
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        //full screen
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
	                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
	        setContentView(R.layout.grid_layout);
	        
	        //is in front
	        Isinfront=true;
	        mMsgclr.mDebugIndex=1;
	        
	        //register
	        IntentFilter ifilt = new IntentFilter("com.app.GestureGame.rawlabel");
	        registerReceiver(mReceiver, ifilt);
	 
		     //text view titles 
		    mTitle=(TextView) findViewById (R.id.mTitle);
		    mScore=(TextView) findViewById (R.id.mScore);
		    
		    //bt view
		    mBt[0]=(Button) findViewById (R.id.button1);
		    mBt[1]=(Button) findViewById (R.id.button2);
		    mBt[2]=(Button) findViewById (R.id.button3);
		    mBt[3]=(Button) findViewById (R.id.button4);
		    
		    //set on click
		    for (int i=0;i<4;i++){
		    	mBt[i].setOnClickListener(new OnClickListener() {
		    		public void onClick(View v) {
		    			ButtonRefresh();
		    			int i;
		    			for ( i=0;i<4;i++){
		    				if(mBt[i].getId() == v.getId())
		    					break;
		    			}
		    			System.out.println(""+i);
		    			mBt[i].setText("ON");
		    			mbtIndex[i]=1;
		    		}
		    	});
		    }
		    
		    ButtonRefresh();
		    
		    mTitle.setText(mLeftTitle);
		    mScore.setText(mRightScore+counter); 
		     //define my grid view
		    gridView= (GridView) findViewById(R.id.grid_view);
		   
		    // Array Adapter
			ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this,
					R.layout.stringadapter, mynumbers){
		
				        @Override
				        public View getView(int position, View convertView, ViewGroup parent) { 

				            View grid;
				            if(convertView==null){
				                grid = new View(getApplicationContext());
				                LayoutInflater inflater=getLayoutInflater();
				                grid=inflater.inflate(R.layout.stringadapter, parent, false);
				            }else{
				                grid = (View)convertView;
				            }

				            TextView tv = (TextView)grid.findViewById(R.id.list_item);
				            if(position%5==0){
				            	tv.setBackgroundResource(color.Yellow);
				            	tv.setTextColor(color.Black);
				            }
				            else{
				            	tv.setBackgroundResource(color.LightGrey);
				            	tv.setTextColor(color.DimGray);
				            }
				            tv.setText(String.valueOf(mynumbers[position]));
				            return grid;
				        }

				    };
		    gridView.setAdapter(adapter);
		    gridView.setOnItemClickListener((OnItemClickListener) this);
	 		} 
	 
	 public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		}
	 
	 private void ButtonRefresh(){
    	 for (int i=0;i<4;i++){
    		 mBt[i].setText("OFF");
    		 mbtIndex[i]=0;
    	 }
	 }
	 private void NumberRefresh(){
    	 for (int i=0;i<mynumbers.length;i++){
    		 mynumbers[i]=0;
    	 }
	 }
	 
	 private int GetButtonOn(){
		 
		 for (int i=0;i<4;i++){
    		 if (mbtIndex[i]== 1)
    			return i;
    	 }
		 return -1;
	 }
	 
	 @Override
	 public boolean onCreateOptionsMenu(Menu menu) {
			MenuInflater inflater = getMenuInflater();
	        inflater.inflate(R.menu.game_option_menu, menu);
	        return true;
		}
		
		@Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        switch (item.getItemId()) {
	        case R.id.gamereset: 
	        	ButtonRefresh();
	        	NumberRefresh();
	        	counter=0;
	        	gridView.invalidateViews();
	            return true;
	        }
	        return false;
	    }
	 
	 private void OnStart(){
    	 super.onStart();
		//not in front
	        Isinfront=true;
	        //set debug index to game mode.
	        mMsgclr.mDebugIndex=1;
	 }
     
	 private void OnPause(){
		 super.onPause();
		//not in front
	        Isinfront=false;
	        mMsgclr.mDebugIndex=0;
	 }
	 
	 private void OnResume(){
		 super.onResume();
		//is in front
	        Isinfront=true;
	        mMsgclr.mDebugIndex=1;
	 }
	 
	 protected void onStop()
	 {
	     unregisterReceiver(mReceiver);
	     Isinfront=false;
	     mMsgclr.mDebugIndex=0;
	     super.onStop();
	 }

	 
	 private void OnDestroy(){
		 mMsgclr.mDebugIndex=0;
		 super.onDestroy();
	 }
	 
	 private BroadcastReceiver mReceiver = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();
	        String value1="";
	        int input=0;
	        int ignoreflag=0;
	        
	        if (Isinfront){
	        	if (action.equals("com.app.GestureGame.rawlabel")) {
	        	    Bundle extras = intent.getExtras();
		        	    if (null != extras)
		        	    	 value1 = extras.getString("GameInput");
		        	    
		        	    input=Integer.valueOf(value1);
		        	    if(input >10){
		        	    	input-=10;
		        	    	ignoreflag=1;
		        	    }
		        	    input-=1;
		        	    
		        	    int on=GetButtonOn();
		        	    if (on>=0){
		        	    	//debug stuff
		        	    	if (ProfileManager.getinstant().getprofile().getDebug() ==1){
		        	    		GameDataDebugger mDebugger = new GameDataDebugger(mMsgclr._clonerawgesturedata,on,input,ignoreflag,debugcounter);
		        	    		Thread t = new Thread(mDebugger);
		        	    		t.start();
		        	    	}
		        	        
		        	    	//show change
		        	    	if(input>= 0 && input< 4){
		        	    		mynumbers[on+4*input]++;
		        	    		counter++;
		        	    		Toast.makeText(GameGui.this, String.valueOf(input+1), Toast.LENGTH_SHORT).show();
		        	    	}
		        	    	else{
		        	    		// Vibrate the mobile phone
		        	    		Toast.makeText(GameGui.this, String.valueOf(input+1), Toast.LENGTH_SHORT).show();
		        	    		Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		        	    		vibrator.vibrate(2000);
		        	    	}
		        	    	mScore.setText(mRightScore+counter);
		        	    	
		        	    	//update view
		        	    	gridView.invalidateViews();
		        	    	debugcounter++;
		        	    }
		        }
	        }
	    }
	 };
}
