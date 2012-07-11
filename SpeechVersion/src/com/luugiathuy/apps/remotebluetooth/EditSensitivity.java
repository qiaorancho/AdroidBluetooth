package com.luugiathuy.apps.remotebluetooth;
import java.util.LinkedList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class EditSensitivity extends Activity {


	private ProfileManager mPM = ProfileManager.getinstant();
    /** Items entered by the user is stored in this ArrayList variable */
    LinkedList<String> list = new LinkedList<String>();
	
    /** Declaring an ArrayAdapter to set items to ListView */
    ArrayAdapter adapter;
    
    String [] mySensi= new String[]{"1","2","3","4","5"};
    private ListView listView;
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editset);
		
		adapter = new ArrayAdapter(this,R.layout.list,mySensi);
		
		listView= (ListView) findViewById(R.id.listView1);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String line;
				line = ((TextView) arg1).getText().toString();
                Bundle extras = new Bundle();
                Intent i = new Intent();
				extras.putString("text", line);
				i.putExtras(extras);
				setResult(RESULT_OK, i);
                finish();
			}
		});
	}
}
