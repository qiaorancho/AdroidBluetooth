package com.app.GestureGame.Setting;

import java.util.ArrayList;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.app.GestureGame.R;
import com.app.GestureGame.Profile.ProfileManager;
 
public class ManageSentence extends ListActivity {
 
	private ProfileManager mPM = ProfileManager.getinstant();
    /** Items entered by the user is stored in this ArrayList variable */
	ArrayList<String> list = new ArrayList<String>();
	
    /** Declaring an ArrayAdapter to set items to ListView */
    ArrayAdapter<String> adapter;
 
    /** Called when the activity is first created. */
    
    @SuppressWarnings("unchecked")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 
        /** Setting a custom layout for the list activity */
        setContentView(R.layout.manage);
 
        /** Reference to the add button of the layout main.xml */
        Button btn = (Button) findViewById(R.id.btnAdd);
 
        /** Reference to the delete button of the layout main.xml */
        Button btnDel = (Button) findViewById(R.id.btnDel);
        
        list = (ArrayList<String>) mPM.mProfile.mSentence.clone();
        /** Defining the ArrayAdapter to set items to ListView */
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, list);
 
        /** Defining a click event listener for the button "Add" */
        OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText edit = (EditText) findViewById(R.id.txtItem);
                String tag=edit.getText().toString();
                list.add(tag);
                mPM.mProfile.mSentence.add(tag);
                edit.setText("");
                adapter.notifyDataSetChanged();
            }
        };
 
        /** Defining a click event listener for the button "Delete" */
        OnClickListener listenerDel = new OnClickListener() {
            @Override
            public void onClick(View v) {
                /** Getting the checked items from the listview */
                SparseBooleanArray checkedItemPositions = getListView().getCheckedItemPositions();
                int itemCount = getListView().getCount();
                for(int i=itemCount-1; i >= 0; i--){
                    if(checkedItemPositions.get(i)){
                        {
                        	mPM.mProfile.mSentence.remove(list.get(i));
                        	adapter.remove(list.get(i));
                        }
                        
                    }
                }
                adapter.notifyDataSetChanged();
            }
        };
 
        /** Setting the event listener for the add button */
        btn.setOnClickListener(listener);
 
        /** Setting the event listener for the delete button */
        btnDel.setOnClickListener(listenerDel);
 
        /** Setting the adapter to the ListView */
        setListAdapter(adapter);
    }
}
