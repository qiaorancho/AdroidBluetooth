package com.app.GestureGame.Debug;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;

import com.app.GestureGame.Profile.ProfileManager;

public class FreLogger {
	
	   private ProfileManager mPM = ProfileManager.getinstant();
	   private long freCounter =0;
	   private long oldtime=0;
	   private long newtime=0;

	   
	   public FreLogger(){
		   
	   }
	   
	   public void Add()
    {
    	freCounter++;
    	check();
    }
	   
	   public void setOldTime(){
		   oldtime=System.currentTimeMillis()/1000;
	   }
	   
	   public void setNewTime(){
		   newtime=System.currentTimeMillis()/1000;
	   }
	   
	   public long getOldTime(){
		   return oldtime;
	   }
	   
	   public long getNewTime(){
		   return newtime;
	   }
	   
	   
	   public void start(){
		   setOldTime();
	   }
	   
	   public void check(){
		   setNewTime();
		  
		   if(freCounter ==1){
			   start();
		   }
		   
		   try{
			   if (600 == (newtime-oldtime)){
				   end(String.valueOf(freCounter/600));
				   freCounter=0;
				   setOldTime();
			   }
			   else if( (newtime-oldtime) <0 ){
				  setOldTime();
			   }   
		   }
		   catch(Exception e){
			   e.printStackTrace();
		   }
	   }
	   
	   public void end(String str){
		   try { 
				//get date
				SimpleDateFormat s = new SimpleDateFormat("yyyy.MM.dd");
				String today = s.format(new Date());
				
				//get file path
				File sdCard = Environment.getExternalStorageDirectory();
				File dir = new File (sdCard.getAbsolutePath() + "/myfiles/GestureSpeaker/LogFile");
				dir.mkdirs();
				
				//every has a data file
				File file = new File(dir,"frelog.txt");
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
					bufferWritter.write(datastring+" ended"+str+" \n");
		        
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