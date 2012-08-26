package com.luugiathuy.apps.remotebluetooth;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ToggleButton;

public class EditDebug extends Activity {

	private ProfileManager mPM = ProfileManager.getinstant();
    /** Items entered by the user is stored in this ArrayList variable */
    
	ToggleButton toggleButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.debug);


		
		toggleButton=(ToggleButton) findViewById(R.id.toggleButton1);
		toggleButton.setChecked(mPM.mProfile.getDebug()==1);
		toggleButton.setOnClickListener(new View.OnClickListener() {

		    @Override
		    public void onClick(View v) {
		        if (toggleButton.isChecked()) {
		        	mPM.mProfile.setDebug(1);
		        } else {
		        	mPM.mProfile.setDebug(0);
		        }
		    }
		});
	}
	
}
