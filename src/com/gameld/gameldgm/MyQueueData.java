package com.gameld.gameldgm;

import java.util.*;

public class MyQueueData {
	public static final int CMD_CTRL = 0;
	public static final int CMD_NET = 1;
	
	public int mCmd;
	private HashMap<String,String> mMap = new HashMap<String,String>();
	
	public MyQueueData(int cmd){
		mCmd = cmd;
	}
	
	public void setValue(String key, String value){
		mMap.put(key, value);
	}
	
	public String getValue(String key){
		return mMap.get(key);
	}
}
