package com.gameld.gameldgm;

import java.io.IOException;
import java.lang.ref.WeakReference;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.*;
import android.app.ProgressDialog;

public class AccountActivity extends Activity
{
	private static final String TAG = "AccountActivity";
	private EditText mUserID;
	private ProgressDialog mProgressDlg;
	private MyHandler mHandler;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account);
    	
        mUserID = (EditText)findViewById(R.id.editTextAccount);
    	mHandler = new MyHandler(this);
    	((MyApp)getApplication()).setLoginHandler(mHandler);
    }
    
    @Override
    public void onDestroy(){
    	mHandler.removeCallbacksAndMessages(null);
    	
    	super.onDestroy();
    }

    static class MyHandler extends Handler {
    	private WeakReference<AccountActivity> mOuter;
    	
    	public MyHandler(AccountActivity activity){
    		mOuter = new WeakReference<AccountActivity>(activity);
    	}
    	
		@Override
		public void handleMessage(Message msg){
			super.handleMessage(msg);
			AccountActivity activity = mOuter.get();
			if(activity == null)
				return;
			
			if(msg.what == MyTcpClient.XYID_B){
				// close wait dialog
				activity.mProgressDlg.dismiss();
			}
		}
    }

    public void onQuery(View view){   	
    	String sID = mUserID.getText().toString().trim();
    	if(sID.length() == 0){
    		mUserID.setError(getString(R.string.error_loginid));
    		return;
    	}
		try {
			byte[] bytes_id = sID.getBytes("GB2312");
			if (bytes_id.length > 10) {
	    		mUserID.setError(getString(R.string.error_loginid));
				return;
			}
		}
    	catch(IOException e){
    		Log.e(TAG, e.toString());
    		return;
    	}
		
		Button btn = (Button)findViewById(R.id.buttonQuery);
		btn.setEnabled(false);
		
		((MyApp)getApplication()).startQuery(sID);
		
        // start waiting
        mProgressDlg = ProgressDialog.show(this, getString(R.string.app_name),
        		getString(R.string.wait_login));
    }
}
