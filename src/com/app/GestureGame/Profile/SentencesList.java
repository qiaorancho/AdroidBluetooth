package com.app.GestureGame.Profile;
import java.util.Locale;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.GestureGame.R;

public class SentencesList extends ListActivity {

	TextToSpeech talker;
	String text_final = "null";
	Bundle extras = new Bundle();
	private ProfileManager mPM = ProfileManager.getinstant();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		/*
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		*/
		setListAdapter(new ArrayAdapter<String>(this, R.layout.list, mPM.getprofile().getSentence()));

		talker = new TextToSpeech(SentencesList.this, new TextToSpeech.OnInitListener() {

			@Override
			public void onInit(int status) {
				// TODO Auto-generated method stub

				if (status != TextToSpeech.ERROR) {
					talker.setLanguage(Locale.ENGLISH);
				}
			}
		});
		
		ListView list = getListView();
		list.setTextFilterEnabled(true);
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				text_final = ((TextView) arg1).getText().toString();

				talker.speak(text_final, TextToSpeech.QUEUE_FLUSH, null);
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

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		if (talker != null) {

			talker.stop();
			talker.shutdown();
		}

		super.onPause();
	}

	
}
