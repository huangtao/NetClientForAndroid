package com.gameld.gameldgm;

import android.app.Activity;
import android.content.Intent;
//import android.content.Intent;
import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
import android.view.View;

public class MainActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    public void onAccount(View view)
    {
        Intent intent = new Intent(this, AccountActivity.class);
        startActivity(intent);
    }
    
    public void onMedal(View view)
    {
    }
}
