package com.gameld.gameldgm;

import android.app.Application;
//import android.os.Bundle;
import android.os.Handler;
//import android.os.Message;

public class MyApp extends Application //implements Handler.Callback
{
    private static MyApp singleton;
    public static boolean logined;
    public static String gmUserID;
    public static String gmPassword;
    public static String myPhone;
    
    public static String targetUserID;

    private MyTcpClient mClient;
    //private Handler handler;
    
    // load dynamic library 
    //static {
    //	System.loadLibrary("mylib");
    //}

    // return singleton application stance
    public static MyApp getInstance(){
        return singleton;
    }

    @Override
    public final void onCreate(){
        super.onCreate();
        singleton = this;
        logined = false;
        
        // initialize jni library
        
        // initialize tcp client
        mClient = new MyTcpClient();
    }
    
    public void setLoginHandler(Handler handler){
    	mClient.setLoginHandler(handler);
        new Thread(mClient).start();
    }
    
    public void setAccountHandler(Handler handler){
    	mClient.setAccountHandler(handler);
    }

    public void startLogin(String login_id, String login_pwd){
    	gmUserID = login_id;
    	gmPassword = login_pwd;

		MyQueueData data = new MyQueueData(MyQueueData.CMD_NET);
		data.mCmd = MyQueueData.CMD_NET;
		data.setValue("xyid", String.valueOf(MyTcpClient.XYID_LOGIN));
		data.setValue("uid", gmUserID);
		data.setValue("pwd", gmPassword);
		data.setValue("device", myPhone);
		mClient.pushCommand(data);
    }
    
    public void startQuery(String user_id){
    	targetUserID = user_id;
    	
		MyQueueData data = new MyQueueData(MyQueueData.CMD_NET);
		data.mCmd = MyQueueData.CMD_NET;
		data.setValue("xyid", String.valueOf(MyTcpClient.XYID_B));
		data.setValue("uid", targetUserID);
		mClient.pushCommand(data);
    }
    
    /* A native method that is implemented by the
     * 'hello-jni' native library, which is packaged
     * with this application.
     */
    //public static native String stringFromJNI();
    //public static native int init();
    //public static native void clear();
}
