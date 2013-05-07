package com.app.GestureGame.Debug;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;

import com.app.GestureGame.Profile.ProfileManager;

public class TimeLogger {
	
	private ProfileManager mPM = ProfileManager.getinstant();
    private Double[]  basis;
    

    public TimeLogger(  ) {
        
    }
    
    public void Start() {
        // code in the other thread, can reference "var" variable
		    		try { 
		    			//get date
		    			SimpleDateFormat s = new SimpleDateFormat("yyyy.MM.dd");
		    			String today = s.format(new Date());
		    			
		    			//get file path
		    			File sdCard = Environment.getExternalStorageDirectory();
		    			File dir = new File (sdCard.getAbsolutePath() + "/myfiles/GestureSpeaker/LogFile");
		    			dir.mkdirs();
		    			
		    			//every has a data file
		    			File file = new File(dir,"log.txt");
		    			if(!file.exists()){
		        			file.createNewFile();
		        		}
		    			
		    			//true means we can append.
		    			FileWriter fileWritter = new FileWriter(file ,true);
		    	        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
		    	        
		    	        //here we write the debug data to file 
		    	        SimpleDateFormat sss = new SimpleDateFormat("HH,mm,ss");
		    				String datastring =sss.format(new Date());
		    				int seconds100 = (int) ((System.currentTimeMillis() / 10) % 100);
		    				datastring=datastring.concat(String.format(",%2d,",seconds100)+mPM.mProfile.getName());
		    				bufferWritter.write(datastring+" started \n");
		    	        
		    				//oldtime=(int)System.currentTimeMillis()/1000;
		    				
		    				
		    	        bufferWritter.flush();
		    	        bufferWritter.close();
		                System.out.println("Saved the file");
		    		
		    		} catch (IOException e) {
		    		
		    		// TODO Auto-generated catch block
		    		e.printStackTrace();
		    		System.out.println("Saved the file failed");
		    		}
    }
    public void End(){
    	try { 
			//get date
			SimpleDateFormat s = new SimpleDateFormat("yyyy.MM.dd");
			String today = s.format(new Date());
			
			//get file path
			File sdCard = Environment.getExternalStorageDirectory();
			File dir = new File (sdCard.getAbsolutePath() + "/myfiles/GestureSpeaker/LogFile");
			dir.mkdirs();
			
			//every has a data file
			File file = new File(dir,"log.txt");
			if(!file.exists()){
    			file.createNewFile();
    		}
			
			//true means we can append.
			FileWriter fileWritter = new FileWriter(file ,true);
	        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
	        
	        //here we write the debug data to file 
	        SimpleDateFormat sss = new SimpleDateFormat("HH,mm,ss");
				String datastring =sss.format(new Date());
				int seconds100 = (int) ((System.currentTimeMillis() / 10) % 100);
				datastring=datastring.concat(String.format(",%2d,",seconds100)+mPM.mProfile.getName());
				bufferWritter.write(datastring+" ended \n");
	        
	        bufferWritter.flush();
	        bufferWritter.close();
            System.out.println("Saved the file");
		
		} catch (IOException e) {
		
		// TODO Auto-generated catch block
		e.printStackTrace();
		System.out.println("Saved the file failed");
		}
    }
}
