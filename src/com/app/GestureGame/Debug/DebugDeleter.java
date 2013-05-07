package com.app.GestureGame.Debug;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import android.os.Environment;

import com.app.GestureGame.Profile.ProfileManager;

public class DebugDeleter   {
	
	private ProfileManager mPM = ProfileManager.getinstant();
    private LinkedList<Double[]>  rawgesturedata;

    public void delet() {
					
					//get file path
					File sdCard = Environment.getExternalStorageDirectory();
					File dir = new File (sdCard.getAbsolutePath() + "/myfiles/GestureSpeaker/debug");
					dir.mkdirs();
					dir.delete();
					if (dir.isDirectory()) {
					    String[] children = dir.list();
					    for (int i = 0; i < children.length; i++) {
					        new File(dir, children[i]).delete();
					    }
					}
					
					System.out.println("Deleted the file");
    }
}
