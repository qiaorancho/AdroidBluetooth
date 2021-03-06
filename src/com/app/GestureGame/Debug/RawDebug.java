package com.app.GestureGame.Debug;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.app.GestureGame.Profile.ProfileManager;


import android.os.Environment;

public class RawDebug {
	private ProfileManager mPM = ProfileManager.getinstant();
	Double [] myData;
    public int endcount;
    private int dirFlag;
	
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
    
    
    
    public RawDebug () throws IOException{
    	endcount=0;
    	dirFlag=0;
    }
    
    public void Start(){
    	try{
    		dirFlag=1;
    		dir1.mkdirs();
        	file1= new File(dir1,today1+"_Raw_debug.txt");
        	
        	if(!file1.exists()){
        		file1.createNewFile();
        	}
    	}
    	catch(IOException e){
    		e.getStackTrace();
    		System.out.println("Raw data exception!");
    	}
    }
    
    public synchronized void  Debug(Double [] input ,int threshod,int end,int debugIndex){
    		Start();
    		myData=input;
    		Date myDay=new Date();
    		String datastring =sss.format(myDay);
    		try {
    			fileWritter1 = new FileWriter(file1 ,true);
    		} catch (IOException e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    			System.out.println("File writer wrong!");
    		}
    		BufferedWriter bufferWritter1 = new BufferedWriter(fileWritter1);
    		bufferWritter1 = new BufferedWriter(fileWritter1);	
    		int seconds100 = (int) ((myDay.getTime()/ 10) % 100);
    		datastring=datastring.concat(String.format(",%2d",seconds100));
    		for(int j=0;j<myData.length;j++){
    			datastring=datastring.concat(","+String.valueOf(myData[j]));
    		}
    		datastring=datastring.concat(","+mPM.mProfile.getName()+","+mPM.mProfile.getSensitivity());
    		try {
    			bufferWritter1.write(datastring+","+String.valueOf(threshod)+","+String.valueOf(end)+","+String.valueOf(debugIndex)+"\n");
    			bufferWritter1.flush();
    			bufferWritter1.close();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block 
    			e.printStackTrace();
    			System.out.println("Raw data write wrong!");
    		}
    }
}
