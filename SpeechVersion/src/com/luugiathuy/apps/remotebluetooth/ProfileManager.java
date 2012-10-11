package com.luugiathuy.apps.remotebluetooth;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

import android.content.Context;
import android.os.Environment;

public class ProfileManager {

	public static  ProfileManager mPMer = null;
	private String CurrentUser;
	public Profile mProfile;
	public Context mContext;
	
	public ProfileManager(){
		CurrentUser="";
		mProfile=new Profile(CurrentUser);
	}
	
	public String getCurrentUser(){
		return CurrentUser;
	}
	
	public static ProfileManager  getinstant(){
    	if(mPMer== null){
    		doSync ();
    	}
    	return mPMer;
    }
    public static synchronized void doSync ()
    {
        if (mPMer== null) {
        	mPMer = new  ProfileManager();
        }
    }
	
	public void Start(Context input){
		CurrentUser=getUser(input);
		mProfile =Profile.read_data_from_file(input, CurrentUser);
		mContext= input;
	}


	public void  reSet(Context input,String name){
		Profile.reset_profile(input, name,mProfile);
	}
	
	
	public Profile getprofile(){
		return mProfile;
	}
	
	public void Add(Context input, String name){
		Save(input);
		mProfile.add_profile(input, name);
		mProfile=Profile.read_data_from_file(input, name);
		WriteLog(input);
	}
	
	public void Change(Context input, String name){
		Save(input);
		mProfile=Profile.read_data_from_file(input, name);
		WriteLog(input);
	}

	public void Delete(Context input, String name){
		Save(input);
		mProfile.delet_profile(input, name);
		CurrentUser=getUser(input);
		if (CurrentUser.equals(name)){
			mProfile=Profile.read_data_from_file(input, "Root");
			CurrentUser=getUser(input);
			WriteLog(input);
		}
	}
	
	public void Save (Context input){
		Profile.write_data_to_file(input, mProfile);
		WriteLog(input);
	}
	
	//writelog for starting, so we know who is last user. 
	private void WriteLog(Context input){
		try { 
            FileOutputStream fOut = input.openFileOutput("LogFile",
                                                    Context.MODE_WORLD_READABLE);
            OutputStreamWriter osw = new OutputStreamWriter(fOut); 
            osw.write(mProfile.getName());
            osw.flush();
            osw.close();
		} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
	}
	
	
	private String getUser(Context input){
			String line="";
			try{
				FileInputStream fIn = input.openFileInput("LogFile");
				InputStreamReader isr = new InputStreamReader(fIn);
		        BufferedReader br = new BufferedReader(isr);
		        try{
			        	
			        	line =  br.readLine();
		        }
				 catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
			catch(FileNotFoundException e){
				Profile mProfileNew=new Profile("Root");
				mProfileNew.add_profile(input, "Root");
				line="Root";
			}
			return line;
	}
}
