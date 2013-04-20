package com.app.GestureGame.Controller;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TimerTask;

import com.app.GestureGame.Debug.BasisDebugger;
import com.app.GestureGame.Debug.FreLogger;
import com.app.GestureGame.Debug.RawDebug;
import com.app.GestureGame.Debug.TrainingDebugger;
import com.app.GestureGame.Filter.LowpassFilter;
import com.app.GestureGame.Perceptron.Datapoint;
import com.app.GestureGame.Perceptron.FeatureDescriptor;
import com.app.GestureGame.Perceptron.Perceptron;
import com.app.GestureGame.Profile.ProfileManager;


/*
 * This class do data processing, so it contians many math classes: Datapoint and LowpassFilter.  
 * */
public  class MessageController 
{
	 public static  MessageController mcontroller = null; 
	 static boolean _continue;
	 //Define flag message 
     private static final int NoMovement = 0;
     //timers and threads
     Thread readThread;
     boolean started = false;
     static boolean isCalibrationdone = false;
     static long _starttime;
     static TimerTask falsepositive_timer;
     
     //sensitivity
     public static  int mIndex=3;
     
     //counter
     public static int updatcounter=0;

     //variables for gyroscope data
     static Queue<Double> _gyrx_bias_queue;
     static Queue<Double> _gyry_bias_queue;
     static Queue<Double> _gyrz_bias_queue;
     static Queue<Double> _acc_bias_queue;
     
     public static double _gyrx_bias;
     public static double _gyry_bias;
     public static double _gyrz_bias;
     public static double _acc_bias;
     
     public static double _gyrx_std;
     public static double _gyry_std;
     public static double _gyrz_std;
     public static double _acc_std;
     
     static int QueueMaxSize = 150;
     static double movementvariable_gyro = 0;
     static double movementvariable_acce = 0;
     static LowpassFilter _l1;

     //variables for gestures
     static LinkedList<Double[]> _rawgesturedata;
     public static LinkedList<Double[]> _clonerawgesturedata;
     static boolean _hasgesturestarted;
     
     static double _gesture_threshold_gyro[] =new double []{8,4,2,1,0.5};
     static double _gesture_threshold_acce[] =new double []{2,1.5,1,0.8,0.6};
     static int datasetno[] =new int []{130,80,50,30,15};
     
     public static Perceptron p1;
     static int mylabel;
     static double[] _GlobalFeatureVector;
     static double [] _GlobalFeatureVectorTime;
     static boolean _Classification_done;
     static  RawDebug mRawDebugger;
     
     // perceptron matrix parameter.
     public static int NClASSES=11;
     public static int NDIMENSIONS=34;
     
     //index for traning and game;
     public static int mDebugIndex=0;
     
     
     //data matrix
     public static Datapoint public_data=null; 
     public static boolean ShouldWork=true;

     
     
     //freLogger
     public static FreLogger mFreLogger= new FreLogger();
     
     
    public static MessageController getinstant(){
    	if(mcontroller== null){
    		doSync ();
    	}
    	return mcontroller;
    }
    
    public static synchronized void doSync ()
    {
        if (mcontroller== null) {
        	mcontroller = new  MessageController();
        }
    }
    
	public  MessageController(){
	        _gyrx_bias_queue = new LinkedList <Double>();

	        _gyry_bias_queue = new LinkedList <Double>();
	        _gyrz_bias_queue = new LinkedList <Double>();
	        _acc_bias_queue = new LinkedList <Double>();

            p1 = new Perceptron(NClASSES, NDIMENSIONS);

	        _l1 = new LowpassFilter();
	        _rawgesturedata = new LinkedList<Double[]>();
	        _clonerawgesturedata=new LinkedList<Double[]>();

	        _hasgesturestarted = false;
	        started = false;
	        isCalibrationdone = false;
	        _Classification_done = false;
	        try {
				mRawDebugger=new RawDebug();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				 System.out.println("Raw initialization");
			}
	}
	public   void setIndex(int in){
    	mIndex=in;
    }

	public synchronized   void setCalibration(boolean in){
		isCalibrationdone =in;
    }
	
	public  int getmylabel(){
    	return mylabel;
    }
	
	/**
	 * The most important function. get datapoint  calibration movement judgment send msg back to control panel.
	 * */
	
	public static synchronized int Read(String msg)
    {
    	int backflag=NoMovement;
            try
            {
            	//Get datapoint.
                Datapoint datapoint = new Datapoint(msg);
                public_data=datapoint;
                
                if(!ShouldWork)
                {
                	//send back some junk data.
                	return -1;
                }
                
                //raw data debugger.
                //mRawDebugger.Debug(datapoint.myDebug, ProfileManager.getinstant());  

                //freLogger
                mFreLogger.Add();
                
                // If it haven't do calibration then do it . 
                if (isCalibrationdone == false)
                {
                    if (_gyrx_bias_queue.size() < QueueMaxSize)
                        _gyrx_bias_queue.add(datapoint.GyrX());
                    if (_gyry_bias_queue.size() < QueueMaxSize)
                        _gyry_bias_queue.add(datapoint.GyrY());
                    if (_gyrz_bias_queue.size() < QueueMaxSize)
                        _gyrz_bias_queue.add(datapoint.GyrZ());
                    _acc_bias_queue.add(
                    		Math.sqrt(Math.pow(datapoint.AccX(),2)+Math.pow(datapoint.AccY(),2)+Math.pow(datapoint.AccZ(),2))		
                    		);
                    if (_gyrx_bias_queue.size() == QueueMaxSize)
                    {
                        estimate_bias_value();
                        estimate_std_value();
                        Calibrationdone();
                        
                        //calibrations done send back -1
                        backflag=-1;
                        System.out.println("Bias Values:");
                        System.out.println(_gyrx_bias + "+\\-" + _gyrx_std + "," + _gyry_bias + "+\\-" + _gyry_std + "," + _gyrz_bias + "+\\-" + _gyrz_std);
                        System.out.println("Bias Values:"+ _acc_bias);
                        
                        //debug bias
                        Double[] bias=new Double[7];
                        bias[0]=_gyrx_bias;
                        bias[1]=_gyrx_std;
                        bias[2]=_gyry_bias;
                        bias[3]=_gyry_std;
                        bias[4]=_gyrz_bias;
                        bias[5]=_gyrz_std;
                        bias[6]=_acc_bias;
                        BasisDebugger bd=new BasisDebugger(bias);
                        Thread t = new Thread(bd);
                        t.start();
                    }
                }
                else
                {
                    movementvariable_gyro = Math.pow(datapoint.GyrX() - _gyrx_bias, 2) +
                        Math.pow(datapoint.GyrY() - _gyry_bias, 2) + Math.pow(datapoint.GyrZ() - _gyrz_bias, 2);
                    
                    movementvariable_acce=Math.sqrt(Math.pow(datapoint.AccX(),2)+Math.pow(datapoint.AccY(),2)+Math.pow(datapoint.AccZ(),2))-_acc_bias;
                    
                    //System.out.println("check acc : "+ datapoint.AccX()+";"+ datapoint.AccY()+";"+ datapoint.AccZ()+";" );
                    //if(Math.abs(movementvariable_acce )>0.5)
                    //System.out.println("check acc : "+ movementvariable_acce );
                    
                    movementvariable_acce=_l1.filtervalue(movementvariable_acce,mIndex-1);
                    movementvariable_gyro = _l1.filtervalue(movementvariable_gyro,mIndex-1);
                    
                    if (movementvariable_acce > _gesture_threshold_acce[mIndex-1] ||movementvariable_gyro > _gesture_threshold_gyro[mIndex-1] )
                    {
                        Double[] gesturepoint = new Double[6];
                        gesturepoint[0] = datapoint.AccX(); 	
                        gesturepoint[1] = datapoint.AccY();
                        gesturepoint[2] = datapoint.AccZ();
                        gesturepoint[3] = datapoint.GyrX() - _gyrx_bias;
                        gesturepoint[4] = datapoint.GyrY() - _gyry_bias;
                        gesturepoint[5] = datapoint.GyrZ() - _gyrz_bias;
                        _rawgesturedata.add(gesturepoint);
                        
                        //debug data
                        if (ProfileManager.getinstant().getprofile().getDebug() ==1)
                        mRawDebugger.Debug(datapoint.myDebug, 1, 0,mDebugIndex);
                    }
                    else
                    {
                        if (_rawgesturedata.size()> datasetno[mIndex-1])     //has to be at least 10 seconds
                        {
                        	//movement detected, so we record
                        	if (ProfileManager.getinstant().getprofile().getDebug() ==1)
                        	mRawDebugger.Debug(datapoint.myDebug, 0, 1,mDebugIndex);
                        	System.out.println("totall data"+_rawgesturedata.size());
                        	
                        	
                        	FeatureDescriptor fd1 = new FeatureDescriptor(_rawgesturedata);
                            double[] featurevector = fd1.getFeaturevector();
                            double [] mSize=fd1.getTime();
                             
                            int label = p1.Predict(featurevector,mSize);
                            //int label = p1.Predict(featurevector );
                        	_Classification_done = true;
                            _GlobalFeatureVector = featurevector;
                            _GlobalFeatureVectorTime=mSize; 
                        	System.out.println("movement detected "+label);
                        	mylabel=label;
                        	
                        	backflag=label+1;
                        	//use for debug
                        	_clonerawgesturedata.clear();
                        	_clonerawgesturedata=(LinkedList<Double[]>) _rawgesturedata.clone();
                        }
                        else{
                        	if (ProfileManager.getinstant().getprofile().getDebug() ==1)
                        	mRawDebugger.Debug(datapoint.myDebug, 0, 0,mDebugIndex);
                        }
                        //clear the data array after stop.
                        _rawgesturedata.clear();
                    }
                }
            }
            catch (Exception e) { 
            	 System.out.println("Exception occurred here. read");
            	 e.printStackTrace();
            }
			return backflag;
    }
    static void estimate_bias_value()
    {
        _gyrx_bias = _gyry_bias = _gyrz_bias = 0;
        for (double gyrxvalue : _gyrx_bias_queue)
        {
            _gyrx_bias += gyrxvalue;
        }
        for (double gyryvalue :_gyry_bias_queue)
        {
            _gyry_bias += gyryvalue;
        }
        for (double gyrzvalue : _gyrz_bias_queue)
        {
            _gyrz_bias += gyrzvalue;
        }
        for (double accvalue : _acc_bias_queue)
        {
            _acc_bias += accvalue;
        }
        
        _gyrx_bias /= _gyrx_bias_queue.size();
        _gyry_bias /= _gyry_bias_queue.size();
        _gyrz_bias /= _gyrz_bias_queue.size();
        _acc_bias /= _acc_bias_queue.size();
    }
    
    static void estimate_std_value()
    {
        _gyrx_std = _gyry_std = _gyrz_std = 0;
        for (double gyrxvalue : _gyrx_bias_queue)
        {
            _gyrx_std += Math.pow((gyrxvalue-_gyrx_bias),2);
        }
        for(double gyryvalue : _gyry_bias_queue)
        {
            _gyry_std += Math.pow((gyryvalue - _gyry_bias), 2);
        }
        for(double gyrzvalue :_gyrz_bias_queue)
        {
            _gyrz_std += Math.pow((gyrzvalue - _gyrz_bias), 2);
        }
        _gyrx_std /= _gyrx_bias_queue.size();
        _gyry_std /= _gyry_bias_queue.size();
        _gyrz_std /= _gyrz_bias_queue.size();
        _gyrx_std = Math.sqrt(_gyrx_std);
        _gyry_std = Math.sqrt(_gyry_std);
        _gyrz_std = Math.sqrt(_gyrz_std);
    }
    
    static  void Calibrationdone(){
    	isCalibrationdone=true;
    	_rawgesturedata.clear();
    	_gyrx_bias_queue.clear();
    	_gyry_bias_queue.clear();
    	_gyrz_bias_queue.clear();
    	_acc_bias_queue.clear();
    }
    
    // Update the Perceptron Weight Matrix.
    public void Update(int label)
    {
    	mylabel = label-1;
    	//if debug is on.
    	if (ProfileManager.getinstant().getprofile().getDebug() ==1)
    	recordData(mylabel);

    	updatcounter++;
        if(_Classification_done==true)
        p1.UpdatePerceptron(_GlobalFeatureVector, mylabel,_GlobalFeatureVectorTime);
        _Classification_done = false;
    }
    
    //send data to debugwriter.() for movemnet debug...but now we don't use it.
    public static  void recordData(int label){
    	@SuppressWarnings("unchecked")
		LinkedList<Double[]>  mDebugdata= (LinkedList<Double[]>) _clonerawgesturedata.clone();
		TrainingDebugger mDebbugger = new TrainingDebugger(mDebugdata,label, updatcounter);
        Thread t = new Thread(mDebbugger);
        t.start();
    }
}