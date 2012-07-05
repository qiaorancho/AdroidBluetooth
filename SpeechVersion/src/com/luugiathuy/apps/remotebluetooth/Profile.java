package com.luugiathuy.apps.remotebluetooth;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import android.content.Context;

public class Profile implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mName;
	public String[] mValues;
	public String[] mString;
	private int mSensitivity;
	private double [][] mWeight=new double[10][28];
	private LinkedList<String> mList=new LinkedList<String>();
	public ArrayList<String> mSentence;

	public Profile(){
		mName="Root";
		mSensitivity=3;
		mValues = new String[] { "Gesture 1", "Gesture 2", "Gesture 3",
				"Gesture 4", "Gesture 5", "Gesture 6",
				"Gesture 7", "Gesture 8", "Gesture 9","Gesture 10"};
		mString=new String[10];
		for(int i=0;i<10;i++){
        	mString[i]=mValues[i];	
        }
		InitializeSentence();
	}
	public Profile(String name){
		mName=name;
		mSensitivity=3;
		mValues = new String[] { "Gesture 1", "Gesture 2", "Gesture 3",
				"Gesture 4", "Gesture 5", "Gesture 6",
				"Gesture 7", "Gesture 8", "Gesture 9","Gesture 10"};
		mString=new String[10];
		for(int i=0;i<10;i++){
        	mString[i]=mValues[i];	
        }
		InitializeSentence();
	}
	
	public void InitializeSentence(){
		String [] array = new String[] {
				"Hi", "Good Morning", "Good Afternoon", "Good Evening", "Good Night",
				"Bye", "Its nice to meet you", "I am glad you are here",
				"I hope you are doing great", "I am doing great",
				"I hope you are having a great time here", "Fine , Thank you",
				"Thanks a lot for coming", "I was looking forward to meeting you",
				"Please have a seat", "Please make yourself comfortable",
				"I hope the travel was not too tiring" };
		mSentence=new ArrayList<String>(Arrays.asList(array));
	}
	
	//getter and setter
	public String  getName(){
		return mName;
	}
	public String[]  getString(){
		return mString;
	}

	public String[]  getValues(){
		return mValues;
	}
	public int  getSensitivity(){
		return mSensitivity;
	}
	
	public double[][]  getWeight(){
		return mWeight;
	}
	
	public LinkedList<String>  getList(Context input){
		regetList(input);
		return mList;
	}
	
	public ArrayList<String>  getSentence( ){
		return mSentence;
	}
	
	@SuppressWarnings("unchecked")
	public void  setSentence(ArrayList <String> in){
		 mSentence=(ArrayList<String>) in.clone();
	}
	
	public  void SetSensitivity (int sensitivity){
		 mSensitivity = sensitivity;
	}
	
	public void  setName(String name){
		 mName=name;
	}
	
	public static void write_data_to_file(Context input,Profile pro)
	{
		String name =pro.getName();
		try { 
			name.concat("data");
	        FileOutputStream fOut = input.openFileOutput(name, Context.MODE_WORLD_READABLE); 
	        ObjectOutputStream osw = new ObjectOutputStream(fOut);
	        pro.regetList(input);
	        osw.writeObject(pro);
	        osw.flush();
	        osw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Profile read_data_from_file(Context input, String name)
	{
		Profile pro= new Profile();
		try { 
			name.concat("data");
			FileInputStream fin = input.openFileInput(name);
	        ObjectInputStream ois = new ObjectInputStream(fin);
			   pro = (Profile) ois.readObject();
			   ois.close();
		} catch ( Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pro.regetList(input);
			try{
				pro.mSentence.clone();
			}
			catch(Exception e){
				pro.InitializeSentence();
				e.getStackTrace();
			}
		//
		return pro;
	}
	@SuppressWarnings("unchecked")
	public  void regetList(Context input){
		mList.clear();
		try{
	        FileInputStream fin = input.openFileInput("Profile");
	        ObjectInputStream ois = new ObjectInputStream(fin);
			   mList = (LinkedList<String>) ois.readObject();
			   ois.close();
		}
		catch ( Exception ioe) {
	        ioe.printStackTrace();
		}
	}
	
	public Profile add_profile(Context input,String name)
	{
		Profile mProfileNew=new Profile(name);
			try { 
				mList.add(name);
	            FileOutputStream fOut = input.openFileOutput("Profile", Context.MODE_PRIVATE); 
	            ObjectOutputStream osw = new ObjectOutputStream(fOut);
	            osw.writeObject(mList);
	            osw.flush();
	            osw.close();
	            
	            name.concat("data");
	            FileOutputStream fOut1 = input.openFileOutput(name, Context.MODE_PRIVATE); 
	            ObjectOutputStream osw1 = new ObjectOutputStream(fOut1);
	            osw1.writeObject(mProfileNew);
	            osw1.flush();
	            osw1.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mProfileNew.regetList(input);
		return mProfileNew;
	}
	
	//delete the item 
	public void delet_profile(Context input,String name)
	{
		mList.remove(name);
		try { 
            FileOutputStream fOut = input.openFileOutput("Profile", Context.MODE_PRIVATE); 
            ObjectOutputStream osw = new ObjectOutputStream(fOut);
            osw.writeObject(mList);
            osw.flush();
            osw.close(); 
            name.concat("data");
            input.deleteDatabase(name);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}finally{
	}
	}
	public static void clear(Profile pro){
		pro.mSensitivity=3;
		String[] re =new String[]{ "Gesture 1", "Gesture 2", "Gesture 3",
				"Gesture 4", "Gesture 5", "Gesture 6",
				"Gesture 7", "Gesture 8", "Gesture 9","Gesture 10"};
		for(int i=0;i<10;i++){
			pro.mValues[i]=re[i];
        	pro.mString[i]=pro.mValues[i];	
        }
		for (int i = 0; i < 10; i++)
        {
            for (int j = 0; j < 28; j++)
                pro.mWeight[i][j] = 0;
        }
		
	}
	
	public static void reset_profile(Context input,String name,Profile pro)
	{
			clear(pro);
			name.concat("data");
			try { 
	            FileOutputStream fOut = input.openFileOutput(name, Context.MODE_PRIVATE); 
	            ObjectOutputStream osw = new ObjectOutputStream(fOut);
	            osw.writeObject(pro);
	            osw.flush();
	            osw.close(); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void change_list_name(Context input,Profile pro,String name1, String name2)
	{
		pro.getList(input);
		pro.mList.remove(name1);
		pro.mList.add(name2);
		try { 
            FileOutputStream fOut = input.openFileOutput("Profile", Context.MODE_PRIVATE); 
            ObjectOutputStream osw = new ObjectOutputStream(fOut);
            osw.writeObject(pro.mList);
            osw.flush();
            osw.close(); 
            name1.concat("data");
	            input.deleteDatabase(name1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
		}
	}

}