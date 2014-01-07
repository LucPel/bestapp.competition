package com.example.demo;

import android.os.AsyncTask;

public class LoadTrackersTask extends AsyncTask<Object, Integer, Boolean>{

	 protected Boolean doInBackground(Object... params)
     {
             VuforiaWrapper cw=(VuforiaWrapper)params[0];
            // Load the tracker data set:
			try {
				cw.doLoadTrackersData();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return true;
     }
}
