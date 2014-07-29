package com.gameld.gameldgm;

import java.io.IOException;
import java.lang.ref.WeakReference;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.*;
import android.app.ProgressDialog;

public class LoginActivity extends Activity
{
	private static final String TAG = "LoginActivity";
	private EditText mUserID;
	private EditText mUserPwd;
	private Button mLogin;
	private TextView mInfo;
	private ProgressDialog mProgressDlg;
	private LoginHandler mHandler;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
    	mUserID = (EditText)findViewById(R.id.login_id);
    	mUserPwd = (EditText)findViewById(R.id.login_pwd);
    	mLogin = (Button)findViewById(R.id.login_btn);
    	mLogin.setEnabled(false);
    	mInfo = (TextView)findViewById(R.id.info);
    	mHandler = new LoginHandler(this);
    	((MyApp)getApplication()).setLoginHandler(mHandler);
    }
    
    @Override
    public void onDestroy(){
    	mHandler.removeCallbacksAndMessages(null);
    	
    	super.onDestroy();
    }
    
    static class LoginHandler extends Handler {
    	private WeakReference<LoginActivity> mOuter;
    	
    	public LoginHandler(LoginActivity activity){
    		mOuter = new WeakReference<LoginActivity>(activity);
    	}
    	
		@Override
		public void handleMessage(Message msg){
			super.handleMessage(msg);
			LoginActivity activity = mOuter.get();
			if(activity == null)
				return;
			
			activity.mProgressDlg.dismiss();
			
			if(msg.what == MyTcpClient.XYID_A){
				if(msg.arg1 < 0){
					activity.mInfo.setText("Get key failed!");
				}
				else{
					// enable login button
					activity.mLogin.setEnabled(true);
				}
			}
		}
    }

    public void startLogin(View view){
    	TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
    	if(tm == null){
    		mInfo.setText(getString(R.string.error_getphone));
    		return;
    	}
    	MyApp.myPhone = tm.getLine1Number();
    	if(MyApp.myPhone.length() == 0){
    		mInfo.setText(getString(R.string.error_getphone));
    		return;   		
    	}
    	if(BuildConfig.DEBUG)
    		mInfo.setText(MyApp.myPhone);
    	
    	String sID = mUserID.getText().toString().trim();
    	String sPwd = mUserPwd.getText().toString().trim();

    	if(sID.length() == 0){
    		mUserID.setError(getString(R.string.error_loginid));
    		return;
    	}
		try {
			byte[] bytes_id = sID.getBytes("GB2312");
			if (bytes_id.length > 10) {
				return;
			}
		}
    	catch(IOException e){
    		Log.e(TAG, e.toString());
    		return;
    	}
		
    	if(sPwd.length() == 0 || sPwd.length() > 16){
    		mUserPwd.setError(getString(R.string.error_loginpwd));
    		return;
    	}
    	
        ((MyApp)getApplication()).startLogin(sID, sPwd);
        
        // start waiting
        mProgressDlg = ProgressDialog.show(this, getString(R.string.app_name),
        		getString(R.string.wait_login));
    }
}
