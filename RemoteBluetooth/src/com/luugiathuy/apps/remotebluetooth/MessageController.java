package com.luugiathuy.apps.remotebluetooth;

import java.util.AbstractQueue;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TimerTask;

import android.text.format.Time;


/*
 * This class do data processing, so it contians many math class: Datapoint and LowpassFilter.  
 * */
public  class MessageController
{
   
     static boolean _continue;

     //timers and threads
     Thread readThread;
     boolean started = false;
     static boolean isCalibrationdone = false;
     static long _starttime;
     static TimerTask falsepositive_timer;


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
     
     
  //   static boolean_hasgesturestarted;
     static int _gesture_threshold = 2;
  //   static List<double[]> _rawgesturedata;
  //   static Perceptron p1;
     static int mylabel;
     static Double[] _GlobalFeatureVector;

	
	public  MessageController(){

	        _gyrx_bias_queue = new LinkedList <Double>();

	        _gyry_bias_queue = new LinkedList <Double>();
	        _gyrz_bias_queue = new LinkedList <Double>();

	        _l1 = new LowpassFilter();
	        _rawgesturedata = new LinkedList<Double[]>();

	        _hasgesturestarted = false;
	        
	        started = false;
	        isCalibrationdone = false;
/*
	        falsepositive_timer = new System.Windows.Threading.DispatcherTimer();
	        falsepositive_timer.Tick += new EventHandler(falsepositive_timer_Tick);

	        InitializeComponent();
	        findallports();
	        */
	}
 
    
	
	
	/*
	 * The most important function.
	 * get datapoint 
	 * calibration
	 * movement judgement
	 * send msg back to control panel.
	 * 
	 * */
    public static String Read(String msg)
    {
    	String back="noting";
            try
            {
            	//Get datapoint.
                Datapoint datapoint = new Datapoint(msg);
                //long temp=System.currentTimeMillis();
                Time temp=new Time();
              //  TimeSpan span = temp.Subtract(_starttime);
                
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
                        isCalibrationdone = true;
                        System.out.println("Bias Values:");
                        System.out.println(_gyrx_bias + "+\\-" + _gyrx_std + "," + _gyry_bias + "+\\-" + _gyry_std + "," + _gyrz_bias + "+\\-" + _gyrz_std);
                    }
                    back="not calibrationdone";
                }
                else
                {
                    //_outfile.WriteLine(temp.Month + "/" + temp.Day + "/" + temp.Year + " " + temp.Hour + ":"
                    //    + temp.Minute + ":" + temp.Second + " " + temp.Millisecond + " " + datapoint.AccX
                    //    + "," + datapoint.AccY + "," + datapoint.AccZ + "," + (datapoint.GyrX - _gyrx_bias)
                    //    + "," + (datapoint.GyrY - _gyry_bias) + "," + (datapoint.GyrZ - _gyrz_bias));
                    movementvariable = Math.pow(datapoint.GyrX() - _gyrx_bias, 2) +
                        Math.pow(datapoint.GyrY() - _gyry_bias, 2) + Math.pow(datapoint.GyrZ() - _gyrz_bias, 2);
                    movementvariable = _l1.filtervalue(movementvariable);
                    if (movementvariable > _gesture_threshold)
                    {
                     /*   _outfile.WriteLine(temp.Month + "," + temp.Day + "," + temp.Year + "," + temp.Hour + ","
                            + temp.Minute + "," + temp.Second + "," + temp.Millisecond + "," + datapoint.AccX
                            + "," + datapoint.AccY + "," + datapoint.AccZ + "," + (datapoint.GyrX - _gyrx_bias)
                            + "," + (datapoint.GyrY - _gyry_bias) + "," + (datapoint.GyrZ - _gyrz_bias));
                     */ 
                        Double[] gesturepoint = new Double[6];
                        gesturepoint[0] = datapoint.AccX();
                        gesturepoint[1] = datapoint.AccY();
                        gesturepoint[2] = datapoint.AccZ();
                        gesturepoint[3] = datapoint.GyrX() - _gyrx_bias;
                        gesturepoint[4] = datapoint.GyrY() - _gyry_bias;
                        gesturepoint[5] = datapoint.GyrZ() - _gyrz_bias;
                        _rawgesturedata.add(gesturepoint);
                        
                        back= "add gesture";
                        System.out.println("add gesture");
                    }
                    
                    else
                    {
                        if (_rawgesturedata.size()> 50)     //has to be at least 10 seconds
                        {
                        	System.out.println(_rawgesturedata.size());
                        	
  /*                        FeatureDescriptor fd1 = new FeatureDescriptor(_rawgesturedata);
                            double[] featurevector = fd1.Featurevector;
 
                        	int label = p1.Predict(featurevector);
                            speak_from_label(label);
                            _GlobalFeatureVector = featurevector;
 */                     _rawgesturedata.clear();
 						System.out.println("movement detected");

                        	
                        }
      /*                  System.out.println(temp.month + "," + temp.monthDay + "," + temp.year + "," + temp.hour + ","
                            + temp.minute + "," + temp.second + "," + temp.toMillis(false) + ",0,0,0,0,0,0");
                       
      */
                        back= "still";
                    }
                }
            }
            catch (Exception e) { 
            	 System.out.println("Exception occurred here.");
            	 e.printStackTrace();
            }
			return back;
    }

    
   
/*
    static private void falsepositive_timer_Tick(object sender, EventArgs e)
    {
        falsepositive_timer.Stop();
        if (_hasgesturestarted == true)
        {
            
            _hasgesturestarted = false;
            Console.WriteLine("End");
        }
    }
 
*/


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


   
 
  /*  private void threshold_slider_ValueChanged(Object sender, RoutedPropertyChangedEventArgs<Double> e)
    {
        _gesture_threshold = (int)threshold_slider.Value;
    }

   */
}