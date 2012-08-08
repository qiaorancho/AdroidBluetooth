package com.luugiathuy.apps.remotebluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The main GUI interface.
 * Function	: Main GUI and wait for service message using handler.  
 * */

public class GestureSpeaker extends Activity {
	
	// member for test movement training.
	private ProfileManager mPM = ProfileManager.getinstant();
	private MessageController mMsgclr= MessageController.getinstant();
	private UserOperationLog mUol=new UserOperationLog(); 
	private static TextToSpeech talker;
	private  String[] mString ;
	int updateflag=2;
	public int mMsggotNo=0;
	Toast mToast;
	
	//testing list view
	/* String[] myvalues = new String[] { "Gesture1", "Gesture2", "Gesture3",
			"Gesture4", "Gesture5", "Gesture6",
			"Gesture7", "Gesture8", "Gesture9","Gesture10"};
   */
	
	// Layout view
	private TextView mTitle;
	private TextView mData;
	private TextView mIndex;
	private TextView mPass;
	private TextView mUserTitle;
	private ListView listView;
	
	//button
	private Button mButton;

	// Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 100;
    private static final int REQUEST_ENABLE_BT = 101;
    public static final int PROFILE_USER_CHANGED = 102;
    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    
    // Key names received from the BluetoothCommandService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
	
	// Name of the connected device
    private String mConnectedDeviceName = null;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for Bluetooth Command Service
    private BluetoothCommandService mCommandService = null;
    
    
    // First paramenter - Context
	// Second parameter - Layout for the row
	// Third parameter - ID of the View to which the data is written
	// Forth - the Array of data
    
    /** 
     * Called when the activity is first created. */
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        //set up ProfileManger
        mPM.Start(this);
        mMsgclr.setIndex(mPM.mProfile.getSensitivity());
        
        // Set up speaker.
        talker=new TextToSpeech(this,null);
        
        // Set up the window layout
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
        
        // Set up the custom title
        mTitle = (TextView) findViewById(R.id.title_left_text);
        mData = (TextView) findViewById(R.id.DataTextView);
        mIndex = (TextView) findViewById(R.id.mIndex);
        mUserTitle= (TextView) findViewById(R.id.mProfile);
        
        
        //Set up the button
        mPass= (Button) findViewById(R.id.bPass);
        mButton= (Button) findViewById(R.id.bGarbage);
        
        mButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	
                // Send a message using content of the edit text widget
            	if (updateflag<100)
            	talker.speak("Garbage collected.", TextToSpeech.QUEUE_FLUSH, null);
				if (updateflag==1){
					int backlabel=mMsgclr.getmylabel();
            		mMsgclr.Update(MessageController.NClASSES);
            		mIndex.setText("Caliberated");
            		mUol.Debug(10, backlabel, mPM, mMsgclr);
            		
				}
				else{
					mIndex.setText("Sensitivity: "+mPM.mProfile.getSensitivity());
				}
				if (updateflag<99)
            	updateflag+=1;
            }
        });
        
        
        mPass.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget
            	
            	if(mCommandService.getState()==BluetoothCommandService.STATE_CONNECTED)
            	{
            		if(mPass.getText().toString().equals("Initialize")){
            			
                		mCommandService.setFlag(true);
                		talker.speak("Initialization.", TextToSpeech.QUEUE_FLUSH, null);
                		mUol.Debug(-1, -1, mPM, mMsgclr);
                		
            		}
            		else {
                		updateflag=100;
                	}
            	}
            	else{
            		Toast.makeText(GestureSpeaker.this, "Please connect a device.", Toast.LENGTH_SHORT).show();
            	}
            	
            }
        });
        
        
        //set up my string
        mString =new String[MessageController.NClASSES-1];
        
        //mData.setTextSize(30);
        mData.setText("Waiting for movement." +"\nLong click to select sentence." );
        mTitle.setText(R.string.app_name);
        mTitle = (TextView) findViewById(R.id.title_right_text);
        mUserTitle.setText("User:"+mPM.mProfile.getName());
        
        /* for(int i=0;i<10;i++){
        	msgstring[i]=myvalues[i];	
        }
        */
        
        //set up listview
        listView= (ListView) findViewById(R.id.listView1);

        for(int i=0;i<MessageController.NClASSES-1;i++){
        	mString[i]=mPM.mProfile.mValues[i];
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
        		android.R.layout.simple_list_item_1, android.R.id.text1, mString);
		// Assign adapter to ListView
        listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (updateflag<100)
				talker.speak(mPM.mProfile.getString()[arg2], TextToSpeech.QUEUE_FLUSH, null);
				if (updateflag==1){
					int backlabel=mMsgclr.getmylabel();
            		mMsgclr.Update(arg2+1);
            		mIndex.setText("Caliberated");
            		mUol.Debug(arg2, backlabel, mPM, mMsgclr);
            	}
				else{
					mIndex.setText("Sensitivity: "+mPM.mProfile.getSensitivity());
				}
				if (updateflag<99)
            	updateflag+=1;
			}
		});
		
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				mIndex.setText("Sensitivity: "+mPM.mProfile.getSensitivity());
				Intent i = new Intent(GestureSpeaker.this, List.class);
				startActivityForResult(i, arg2+1);
				return false;
			}
		});

		
		// Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }
	@Override
	protected void onStart() {
		super.onStart();
		// If BT is not on, request that it be enabled.
        // setupCommand() will then be called during onActivityResult
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		}
		// otherwise set up the command service
		else {
			if (mCommandService==null)
				setupCommand();
		}
	}
	@Override
	protected void onResume() {
		super.onResume();
		
		// Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
		if (mCommandService != null) {
			if (mCommandService.getState() == BluetoothCommandService.STATE_NONE) {
				mCommandService.start();
			}
		}
		updateView();
	}

	private void setupCommand() {
		// Initialize the BluetoothChatService to perform bluetooth connections
        mCommandService = new BluetoothCommandService(this, mHandler);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if (mCommandService != null)
			mCommandService.stop();
		mPM.Save(this);
		System.out.println("User changed on result in main activity. main destroy");
		mCommandService.setFlag(false);
		mMsgclr.setCalibration(false);
	}
	
	// The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
                switch (msg.arg1) {
                case BluetoothCommandService.STATE_CONNECTED:
                    mTitle.setText(R.string.title_connected_to);
                    mTitle.append(mConnectedDeviceName);
                    break;
                case BluetoothCommandService.STATE_CONNECTING:
                    mTitle.setText(R.string.title_connecting);
                    break;
                case BluetoothCommandService.STATE_LISTEN:
                case BluetoothCommandService.STATE_NONE:
                    mTitle.setText(R.string.title_not_connected);
                    break;
                }
                break;
            case MESSAGE_DEVICE_NAME:
                // save the connected device's name
                mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                Toast.makeText(getApplicationContext(), "Connected to "
                               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_READ:
               
            	// here read the message and display.
            	try{
            		if(msg.arg1==-1){
            			mIndex.setText("Initialization finished.");
            			mPass.setText("   Pass   ");
            		}
            		else{
            			mMsggotNo++;
                    	mData.setText("Movement number: "+ mMsggotNo+"\nGesture "+msg.arg1);
                    	updateflag=0;
                    	//send the click message.
                    	if(msg.arg1==MessageController.NClASSES)
                    		updateflag=1;
                    	else
                    		listView.performItemClick(listView, msg.arg1-1,listView.getItemIdAtPosition(msg.arg1-1));
                    	mIndex.setText("Sensitivity: "+mPM.mProfile.getSensitivity());
            		}
            	}
            	catch (Exception e) {
            	    e.printStackTrace();
            	}
                break;
                
            case MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                               Toast.LENGTH_SHORT).show();
                break;
            }
        }
    };

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        
		for ( int index=1;index<=MessageController.NClASSES-1;index++){
    		if(index == requestCode ){
    			if (resultCode == RESULT_OK) {
    				Bundle extras = new Bundle();
    				extras = data.getExtras();
    				String tag  = extras.getString("text");
    				//mButton[index-1].setText(String.valueOf(index)+"." + tag);
    				mPM.mProfile.getValues()[index-1]=(String.valueOf(index)+"." + tag);
    				mPM.mProfile.getString()[index-1]=tag;
    				listView.invalidateViews();
    				}
    			break;
    			}
    	}
		switch (requestCode) {
        case REQUEST_CONNECT_DEVICE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                // Get the device MAC address
                String address = data.getExtras()
                                     .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                // Get the BLuetoothDevice object
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                // Attempt to connect to the device
                mCommandService.connect(device);
            }
            break;
            
            
        case PROFILE_USER_CHANGED:
            System.out.println("User changed on result in main activity. ");
            if (resultCode == Activity.RESULT_OK) {
            	if(mCommandService.getState()==BluetoothCommandService.STATE_CONNECTED)
            	{
            		System.out.println("User changed on result in main activity. settting");
                		mCommandService.setFlag(false);
                		mMsgclr.setCalibration(false);
            	}
            }
            break;
            
        case REQUEST_ENABLE_BT:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
                // Bluetooth is now enabled, so set up a chat session
                setupCommand();
            } else {
                // User did not enable Bluetooth or an error occured
                Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.scan:
            // Launch the DeviceListActivity to see devices and do scan
        	Intent serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
            return true;
        case R.id.reset: 
            // reset all the data here
        	Intent i = new Intent(GestureSpeaker.this, Settings.class);
        	 startActivityForResult(i,PROFILE_USER_CHANGED);
        	
            return true;
        }
        return false;
    }
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			mCommandService.write(BluetoothCommandService.VOL_UP);
			return true;
		}
		else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){
			mCommandService.write(BluetoothCommandService.VOL_DOWN);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public void  updateView(){

		mMsgclr.p1._WeightMatrix=mPM.mProfile.getWeight();
		for(int i=0;i<MessageController.NClASSES-1;i++){
        	mString[i]=mPM.mProfile.mValues[i];
        }
		mMsgclr.mIndex=mPM.mProfile.getSensitivity();
		mUserTitle.setText("User:"+mPM.mProfile.getName());
		mIndex.setText("Sensitivity: "+mPM.mProfile.getSensitivity());
		listView.invalidateViews();
		if(mCommandService==null || mCommandService.isCalibrationdone == false){
			updateflag=2;
			mPass.setText("Initialize");
			mData.setText("Movement number: "+ 0+"\nGesture");
		    mMsggotNo=0;
		}
		check_matrix (mMsgclr.p1._WeightMatrix);
		
	}
	
	//check reset works or not.
	private void check_matrix(double [][] _WeightMatrix)
    {
		int sum=0;
        for (int i = 0; i < MessageController.NClASSES; i++)
        {
            for (int j = 0; j < MessageController.NDIMENSIONS; j++)
            {
            	if(_WeightMatrix[i ][j]!=0)
                sum+=1;
            }
        }
        if (sum==0)
            ;//  Toast.makeText(this, "noting .. 0000", Toast.LENGTH_SHORT).show();
        
    }
	
	
}