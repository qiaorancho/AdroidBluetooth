package com.app.GestureGame.Debug;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import com.app.GestureGame.Profile.ProfileManager;


import android.os.Environment;
/**
 * This class is nothing but a change the output name and some output data of class debugger.
 * */

public class GameDataDebugger implements Runnable {
	
	private ProfileManager mPM = ProfileManager.getinstant();
    private LinkedList<Double[]>  rawgesturedata;
    private int ed,rd,id,mycounter;

    public GameDataDebugger( LinkedList<Double[]>  data,int expected, int received, int ignored, int counter) {
        this.rawgesturedata= (LinkedList<Double[]>) data.clone();
        ed=expected;
        rd=received;
        id=ignored;
        mycounter=counter;
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
		    			File file = new File(dir,today+"_gamedata_debug.txt");
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
		    				datastring=datastring.concat(","+String.valueOf(ed)+","+String.valueOf(rd)+","+String.valueOf(id)+","+String.valueOf(mycounter));
		    				bufferWritter.write(datastring+"\n");
		    			}
		    	        
		    	        bufferWritter.flush();
		    	        bufferWritter.close();
		                System.out.println("Game_debug Saved the file");
		    		
		    		} catch (IOException e) {
		    		
		    		// TODO Auto-generated catch block
		    		e.printStackTrace();
		    		System.out.println("Saved the file failed");
		    		}
    }
    public void Debug(Double [] data){
    	
    }
}
