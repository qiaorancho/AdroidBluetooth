package com.luugiathuy.apps.remotebluetooth;
import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Settings extends ListActivity {

	TextToSpeech talker;
	String text_final = "null";
	Bundle extras = new Bundle();
    private ArrayList<Order> m_orders = null;
    private OrderAdapter m_adapter;
    private ProfileManager mPM=ProfileManager.getinstant();
    private MessageController mMsgclr= MessageController.getinstant();
    private static final   int  ForSen=2;
    private static final   int   ForPro=1;
    private static final   int   ForLoad=3;
    ListView list ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		m_orders = new ArrayList<Order>();
        this.m_adapter = new OrderAdapter(this, R.layout.row, m_orders);
                setListAdapter(this.m_adapter);
                
		list = getListView();
		list.setTextFilterEnabled(true);
		StartSettings();
		list.invalidateViews();
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if (arg2 ==0){
					Intent i = new Intent(Settings.this, LoadProfile.class);
					startActivityForResult(i,ForLoad);		
				} 
				else if (arg2 ==1){
					Intent i = new Intent(Settings.this, ManageProfile.class);
					startActivityForResult(i,ForPro);	
				}
				else if (arg2 == 2){
					Intent i = new Intent(Settings.this, ManageSentence.class);
					startActivity(i);	
				}
				else if(arg2==3){
						Intent i = new Intent(Settings.this, EditSensitivity.class);
						startActivityForResult(i,ForSen);
					}
				else if(arg2==4){
					Intent i = new Intent(Settings.this, EditDebug.class);
					startActivity(i );
				}
				
				else if (arg2==5){
					mPM.reSet(mPM.mContext, mPM.mProfile.getName());
					mMsgclr.p1._WeightMatrix=mPM.mProfile.getWeight();
					mMsgclr.p1._PerceptronPoints.clear();
					//reset initialization part.
					Bundle extras1 = new Bundle();
    				Intent i = new Intent();
    				extras1.putString("text", text_final);
    				i.putExtras(extras1);
    				setResult(RESULT_OK, i);
    				
    				Toast.makeText(mPM.mContext,
							"Reset", Toast.LENGTH_SHORT).show();
					
				}
				
			}
		});
	}
	@Override
	protected void onResume(){
		super.onResume();
		updateview();
	}
	
		private class OrderAdapter extends ArrayAdapter<Order> {
	
	        private ArrayList<Order> items;
	
	        public OrderAdapter(Context context, int textViewResourceId, ArrayList<Order> items) {
	                super(context, textViewResourceId, items);
	                this.items = items;
	        }
	
	        @Override
	        public View getView(int position, View convertView, ViewGroup parent) {
	                View v = convertView;
	                if (v == null) {
	                    LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	                    v = vi.inflate(R.layout.row, null);
	                }
	                Order o = items.get(position);
	                if (o != null) {
	                        TextView tt = (TextView) v.findViewById(R.id.toptext);
	                        TextView bt = (TextView) v.findViewById(R.id.bottomtext);
	                        if (tt != null) {
	                              tt.setText(o.getOrderName());                            }
	                        if(bt != null){
	                              bt.setText(o.getOrderStatus());
	                        }
	                }
	                return v;
	        }
		}
		
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
	        
			switch (requestCode) {
			case ForPro:
		    			if (resultCode == RESULT_OK) {
		    				
		    				Bundle extras = new Bundle();
		    				extras = data.getExtras();
		    				String tag  = extras.getString("text");
		    				//mButton[index-1].setText(String.valueOf(index)+"." + tag);
		    				
		    					System.out.println("User changed on result in seting. add new ");
		    					Bundle extras1 = new Bundle();
		        				Intent i = new Intent();
		        				extras1.putString("text", text_final);
		        				i.putExtras(extras1);
		        				setResult(RESULT_OK, i);
		                        
		    				}
				break ;
			case ForSen:
    			if (resultCode == RESULT_OK) {
    				Bundle extras = new Bundle();
    				extras = data.getExtras();
    				String tag  = extras.getString("text");
    				//mButton[index-1].setText(String.valueOf(index)+"." + tag);
    				mPM.mProfile.SetSensitivity(Integer.valueOf(tag));
    				}
    			break ;
			case ForLoad:
    			if (resultCode == RESULT_OK) {
    				Bundle extras = new Bundle();
    				extras = data.getExtras();
    				String tag  = extras.getString("text");
    				//mButton[index-1].setText(String.valueOf(index)+"." + tag);
    				
    				if(!mPM.mProfile.getName().equals(tag)){
    					Bundle extras1 = new Bundle();
        				Intent i = new Intent();
        				extras1.putString("text", text_final);
        				i.putExtras(extras1);
        				setResult(RESULT_OK, i);
        				mMsgclr.p1._PerceptronPoints.clear();
    				}
    				mPM.Change(mPM.mContext, tag);
    				}
    			break ;
			}
			updateview();
	    }
		
		
		private void StartSettings(){
			Order o1 = new Order();
	        o1.setOrderName("Current Profile");
	        o1.setOrderStatus(mPM.mProfile.getName());
	        
	        Order o2 = new Order();
	        o2.setOrderName("Manage Profiles");
	        o2.setOrderStatus("");
	        
	        Order o3 = new Order();
	        o3.setOrderName("Manage Sentences");
	        o3.setOrderStatus("");
	        
	        Order o4 = new Order();
	        o4.setOrderName("Current Sensitivity");
	        o4.setOrderStatus(""+mPM.mProfile.getSensitivity());
	        
	        Order o5 = new Order();
	        o5.setOrderName("Debug");
	        if(mPM.mProfile.getDebug()==0)
	        o5.setOrderStatus("off");
	        else
	        o5.setOrderStatus("on");
	        
	        Order o6 = new Order();
	        o6.setOrderName("Reset Current Profile");
	        o6.setOrderStatus("");
	        
	        m_orders.add(o1);
	        m_orders.add(o2);
	        m_orders.add(o3);
	        m_orders.add(o4);
	        m_orders.add(o5);
	        m_orders.add(o6);
		}
		
		public void updateview(){
			m_orders.get(0).setOrderStatus(mPM.mProfile.getName());
			m_orders.get(3).setOrderStatus(""+mPM.mProfile.getSensitivity());
			if(mPM.mProfile.getDebug()==0)
				m_orders.get(4).setOrderStatus("off");
		        else
		        m_orders.get(4).setOrderStatus("on");
			list.invalidateViews();
		}
}
