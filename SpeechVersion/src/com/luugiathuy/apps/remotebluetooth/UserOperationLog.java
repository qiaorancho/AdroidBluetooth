package com.luugiathuy.apps.remotebluetooth;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;

public class UserOperationLog {
	   
    SimpleDateFormat s1 = new SimpleDateFormat("yyyy.MM.dd");
	String today1 = s1.format(new Date());
	
	//get file path
	File sdCard1 = Environment.getExternalStorageDirectory();
	File dir1 = new File (sdCard1.getAbsolutePath() + "/myfiles/GestureSpeaker/debug");
	File file1;
	//every has a data file
	//true means we can append.
	
	FileWriter fileWritter1 ;
    SimpleDateFormat sss = new SimpleDateFormat("HH,mm,ss");
    
    Double [] myData;
    public void Start(){
    	try{
    		dir1.mkdirs();
        	file1= new File(dir1,today1+"_Operation_debug.txt");
        	
        	if(!file1.exists()){
        		file1.createNewFile();
        	}
    	}
    	catch(IOException e){
    		e.getStackTrace();
    		System.out.println("Operation data exception!");
    	}
    }
    /*
    public RawDebug (Double [] input) throws IOException{
    	try{
    		
    		myData=input.clone();
    		dir1.mkdirs();
        	file1= new File(dir1,today1+"_Raw_debug.txt");
        	if(!file1.exists()){
        		file1.createNewFile();
        	}
        	fileWritter1 = new FileWriter(file1 ,true);
        	//bufferWritter1 = new BufferedWriter(fileWritter1);	
    	}
    	catch(IOException e){
    		e.getStackTrace();
    		System.out.println("Raw data exception!");
    	}
    } 
    */
    
    public synchronized void  Debug(int input,int label,ProfileManager mPM,MessageController mcl){
    	if( mPM.mProfile.getDebug()==1){
    		Start();
    		Date myDay=new Date();
    		String datastring =sss.format(myDay);
    		try {
    			fileWritter1 = new FileWriter(file1 ,true);
    		} catch (IOException e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    			System.out.println("Operation File writer wrong!");
    		}
    		BufferedWriter bufferWritter1 = new BufferedWriter(fileWritter1);
    		int seconds100 = (int) ((myDay.getTime()/ 10) % 100);
    		datastring=datastring.concat(String.format(",%2d",seconds100));
    		datastring=datastring+","+mPM.mProfile.getName()+","+mPM.mProfile.getSensitivity()+
			","+mcl._gyrx_bias+","+mcl._gyry_bias+","+mcl._gyrz_bias;
    		if (input == -1){
    			String addstr=","+input;
    			for(int count =0;count<28;count++){
    				addstr=addstr+","+ " ";
    			}
    			datastring=datastring+addstr;
    			addstr=","+label;
    			for(int count =0;count<28;count++){
    				addstr=addstr+","+ " ";
    			}
    			datastring=datastring+addstr;
    		}
    		else{
    			String addstr=","+input;
    			for(int count =0;count<28;count++){
    				addstr=addstr+","+mcl.p1._WeightMatrix[input][count];
    			}
    			datastring=datastring+addstr;
    			addstr=","+label;
    			for(int count =0;count<28;count++){
    				addstr=addstr+","+mcl.p1._WeightMatrix[label][count];
    			}
    			datastring=datastring+addstr;
    		}
    		
    		try {
    			bufferWritter1.write(datastring+"\n");
    			bufferWritter1.flush();
    			bufferWritter1.close();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    			System.out.println("Operation data write wrong!");
    		}
    	}
    }
}
