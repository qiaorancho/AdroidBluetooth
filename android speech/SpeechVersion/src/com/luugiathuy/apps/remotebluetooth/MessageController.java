package com.luugiathuy.apps.remotebluetooth;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TimerTask;
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
     static  int mIndex=3;

     //variables for gyroscope data
     static Queue<Double> _gyrx_bias_queue;
     static Queue<Double> _gyry_bias_queue;
     static Queue<Double> _gyrz_bias_queue;
     static double _gyrx_bias;
     static double _gyry_bias;
     static double _gyrz_bias;
     static double _gyrx_std;
     static double _gyry_std;
     static double _gyrz_std;
     static int QueueMaxSize = 150;
     static double movementvariable = 0;
     static LowpassFilter _l1;

     //variables for gestures
     static LinkedList<Double[]> _rawgesturedata;
     static boolean _hasgesturestarted;
     
     static double _gesture_threshold[] =new double []{8,4,2,1,0.5};
     static int datasetno[] =new int []{130,80,50,30,15};
     
     static Perceptron p1;
     static int mylabel;
     static double[] _GlobalFeatureVector;
     static boolean _Classification_done;
     static  RawDebug mRawDebugger;
     
     // perceptron matrix parameter.
     static int NClASSES=11;
     static int NDIMENSIONS=28;

     
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

            p1 = new Perceptron(NClASSES, NDIMENSIONS);

	        _l1 = new LowpassFilter();
	        _rawgesturedata = new LinkedList<Double[]>();

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
                
                //raw data debugger.
                mRawDebugger.Debug(datapoint.myDebug, ProfileManager.getinstant());  
                
                // If it haven't do calibration then do it . 
                if (isCalibrationdone == false)
                {
                    if (_gyrx_bias_queue.size() < QueueMaxSize)
                        _gyrx_bias_queue.add(datapoint.GyrX());
                    if (_gyry_bias_queue.size() < QueueMaxSize)
                        _gyry_bias_queue.add(datapoint.GyrY());
                    if (_gyrz_bias_queue.size() < QueueMaxSize)
                        _gyrz_bias_queue.add(datapoint.GyrZ());
                    if (_gyrx_bias_queue.size() == QueueMaxSize)
                    {
                        estimate_bias_value();
                        estimate_std_value();
                        Calibrationdone();

                        backflag=-1;
                        System.out.println("Bias Values:");
                        System.out.println(_gyrx_bias + "+\\-" + _gyrx_std + "," + _gyry_bias + "+\\-" + _gyry_std + "," + _gyrz_bias + "+\\-" + _gyrz_std);
                    
                    }
                }
                else
                {
                    movementvariable = Math.pow(datapoint.GyrX() - _gyrx_bias, 2) +
                        Math.pow(datapoint.GyrY() - _gyry_bias, 2) + Math.pow(datapoint.GyrZ() - _gyrz_bias, 2);
                    
                    
                    movementvariable = _l1.filtervalue(movementvariable,mIndex-1);
                    if (movementvariable > _gesture_threshold[mIndex-1])
                    {
                        Double[] gesturepoint = new Double[6];
                        gesturepoint[0] = datapoint.AccX(); 	
                        gesturepoint[1] = datapoint.AccY();
                        gesturepoint[2] = datapoint.AccZ();
                        gesturepoint[3] = datapoint.GyrX() - _gyrx_bias;
                        gesturepoint[4] = datapoint.GyrY() - _gyry_bias;
                        gesturepoint[5] = datapoint.GyrZ() - _gyrz_bias;
                                      
                        _rawgesturedata.add(gesturepoint);
                    }
                    else
                    {
                        if (_rawgesturedata.size()> datasetno[mIndex-1])     //has to be at least 10 seconds
                        {
                        	System.out.println("totall data"+_rawgesturedata.size());
                        	
                        	FeatureDescriptor fd1 = new FeatureDescriptor(_rawgesturedata);
                            double[] featurevector = fd1.getFeaturevector();
                        	int label = p1.Predict(featurevector);
                        	_Classification_done = true;
                            _GlobalFeatureVector = featurevector;
                             
                        	System.out.println("movement detected"+label);
                        	mylabel=label;
                        	backflag=label+1;
                        	//every movement send data to debug
                        //	recordData();
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
        _gyrx_bias /= _gyrx_bias_queue.size();
        _gyry_bias /= _gyry_bias_queue.size();
        _gyrz_bias /= _gyrz_bias_queue.size();
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
    }
    
    
    // Update the Perceptron Weight Matrix.
    public void Update(int label)
    {
        mylabel = label-1;
        if(_Classification_done==true)
        p1.UpdatePerceptron(_GlobalFeatureVector, mylabel);
        _Classification_done = false;
    }
    
    //send data to debugwriter.() for movemnet debug...but now we don't use it.
    public static  void recordData(){
    	@SuppressWarnings("unchecked")
		LinkedList<Double[]>  mDebugdata= (LinkedList<Double[]>) _rawgesturedata.clone();
    	Debugger mDebbugger = new Debugger(mDebugdata);
        Thread t = new Thread(mDebbugger);
        t.start();
    }
}