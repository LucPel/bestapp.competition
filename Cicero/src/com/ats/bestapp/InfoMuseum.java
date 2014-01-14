package com.ats.bestapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class InfoMuseum extends Activity{

	private String LOGTAG="InfoMuseum";
	
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(LOGTAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_museum_activity);
    }
}
