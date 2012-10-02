package com.app.GestureGame.GameGui;

import java.util.Random;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.app.GestureGame.R;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private Random  rmg=new Random();
    private int oldposiont =0;
 
    // Keep all Images in array
    public Integer[] mThumbIds = new Integer[16];
 
    // Constructor
    public ImageAdapter(Context c){
        mContext = c; 
        Refresh();
    }
    /**
     * change image at index to new one.
     */
	 protected void ChangeImage(int index){
		 index-=1;
		 int rd=(rmg.nextInt(12)%12);
		 //compare so it'll not the same.
		 if (rd==oldposiont){
			 rd=(rd+1)%12;
		 }
		 oldposiont=rd;

	   	 switch (index){
	   	 case 0 : mThumbIds[rd]=R.drawable.smile1;break; 
	   	 case 1 : mThumbIds[rd]=R.drawable.smile2;break;
	   	 case 2 : mThumbIds[rd]=R.drawable.smile3;break;
	   	 case 3 : mThumbIds[rd]=R.drawable.smile4;break;
	   	 default: mThumbIds[rd]=R.drawable.red;break;
	   	 }
	 }
	 protected void Refresh(){
		 for(int i=0;i<mThumbIds.length;i++ ){
			 mThumbIds[i]=R.drawable.green;
		 }
	 } 
	 
    @Override
    public int getCount() {
        return mThumbIds.length;
    }
 
    @Override
    public Object getItem(int position) {
        return mThumbIds[position];
    }
 
    @Override
    public long getItemId(int position) {
        return 0;
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(mContext);
        
//        if (1== position){
//        	
//        	imageView.setBackgroundColor(123);
//        	imageView.setContentDescription();
//        }else
        imageView.setImageResource(mThumbIds[position]);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(50, 50));
        return imageView;
    }
 
}