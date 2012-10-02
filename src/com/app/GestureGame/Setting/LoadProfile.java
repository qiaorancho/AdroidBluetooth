package com.app.GestureGame.Setting;


import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.GestureGame.R;
import com.app.GestureGame.Profile.ProfileManager;

public class LoadProfile extends ListActivity {
	private ProfileManager mPM = ProfileManager.getinstant();
	String text_final = "null";
	Bundle extras = new Bundle();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setListAdapter(new ArrayAdapter<String>(this, R.layout.list,mPM.mProfile.getList(mPM.mContext) ));
			
		ListView list = getListView();
		list.setTextFilterEnabled(true);
		list.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				text_final = ((TextView) arg1).getText().toString();
				Toast.makeText(getApplicationContext(),
						((TextView) arg1).getText(), Toast.LENGTH_SHORT).show();
				Intent i = new Intent();
				extras.putString("text", text_final);
				i.putExtras(extras);
				setResult(RESULT_OK, i);
				finish();
			}
		});
	}
}
