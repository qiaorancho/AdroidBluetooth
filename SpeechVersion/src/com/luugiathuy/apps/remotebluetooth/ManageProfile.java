package com.luugiathuy.apps.remotebluetooth;

import java.util.LinkedList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
 
public class ManageProfile extends ListActivity {
 
	private ProfileManager mPM = ProfileManager.getinstant();
	private MessageController mMsgclr= MessageController.getinstant();
    /** Items entered by the user is stored in this ArrayList variable */
    LinkedList<String> list = new LinkedList<String>();
	
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
        
        list = (LinkedList<String>) mPM.mProfile.getList(mPM.mContext).clone();
        /** Defining the ArrayAdapter to set items to ListView */
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, list);
 
        /** Defining a click event listener for the button "Add" */
        OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText edit = (EditText) findViewById(R.id.txtItem);
                String tag=edit.getText().toString();
                
                if (list.contains(tag)){
                	Toast.makeText(getApplicationContext(), "User exists.", Toast.LENGTH_SHORT).show();
                }
                else{
                	list.add(tag);
                    edit.setText("");
                    mPM.Add(mPM.mContext, tag);
                    mMsgclr.p1._PerceptronPoints.clear();
                    adapter.notifyDataSetChanged();
                    
                    Bundle extras = new Bundle();
                    Intent i = new Intent();
    				extras.putString("text", tag);
    				i.putExtras(extras);
    				setResult(RESULT_OK, i);
                }
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
                        if(list.get(i).equals("Root")){
                        	Toast.makeText(getApplicationContext(),"Can't delete Root.",Toast.LENGTH_SHORT ).show();
                        }
                        else{
                        	if(list.get(i).equals(mPM.mProfile.getName())){
                        		mMsgclr.p1._PerceptronPoints.clear();
                        		Toast.makeText(getApplicationContext(),"Current user deleted, change to root.",Toast.LENGTH_SHORT ).show();
                        	}
                        		
                        	mPM.Delete(mPM.mContext, list.get(i));	
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
