package com.luugiathuy.apps.remotebluetooth;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import android.os.Environment;

public class Debugger implements Runnable {
	
	private ProfileManager mPM = ProfileManager.getinstant();
    private LinkedList<Double[]>  rawgesturedata;

    public Debugger( LinkedList<Double[]>  data) {
        this.rawgesturedata= data;
    }
    public void run() {
        // code in the other thread, can reference "var" variable
		    		try { 
		    			//get date
		    			SimpleDateFormat s = new SimpleDateFormat("yyyy.MM.dd");
		    			String today = s.format(new Date());
		    			
		    			//get file path
		    			File sdCard = Environment.getExternalStorageDirectory();
		    			File dir = new File (sdCard.getAbsolutePath() + "/myfiles/GestureSpeaker/debug");
		    			dir.mkdirs();
		    			
		    			//every has a data file
		    			File file = new File(dir,today+"_debug.txt");
		    			if(!file.exists()){
		        			file.createNewFile();
		        		}
		    			//true means we can append.
		    			FileWriter fileWritter = new FileWriter(file ,true);
		    	        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
		    	        
		    	        //here we write the debug data to file 
		    	        SimpleDateFormat sss = new SimpleDateFormat("HH,mm,ss");
		    	        for (int i=0;i<rawgesturedata.size();i++){
		    				Double [] mydata= rawgesturedata.get(i);
		    				String datastring =sss.format(new Date());
		    				int seconds100 = (int) ((System.currentTimeMillis() / 10) % 100);
		    				datastring=datastring.concat(String.format(",%2d,",seconds100)+mPM.mProfile.getName());
		    				for(int j=0;j<mydata.length;j++){
		    					datastring=datastring.concat(","+String.valueOf(mydata[j]));
		    				}
		    				bufferWritter.write(datastring+"\n");
		    			}
		    	        bufferWritter.flush();
		    	        bufferWritter.close();
		    	      	
		                System.out.println("Saved the file");
		    		} catch (IOException e) {
		    		// TODO Auto-generated catch block
		    		e.printStackTrace();
		    		System.out.println("Saved the file failed");
		    		}
    }
    public void Debug(Double [] data){
    	
    }
}
