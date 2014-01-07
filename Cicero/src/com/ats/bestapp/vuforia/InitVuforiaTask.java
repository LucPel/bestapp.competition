package com.ats.bestapp.vuforia;

import com.qualcomm.vuforia.Vuforia;

import android.app.Activity;
import android.os.AsyncTask;

public class InitVuforiaTask extends AsyncTask<Object, Integer, Boolean>{

	 // Initialize with invalid value:
    private int mProgressValue = -1;
    
    
    protected Boolean doInBackground(Object... activities)
    {
        // Prevent the onDestroy() method to overlap with initialization:
            Vuforia.setInitParameters((Activity)activities[0], Vuforia.GL_20);
            
            do
            {
                // Vuforia.init() blocks until an initialization step is
                // complete, then it proceeds to the next step and reports
                // progress in percents (0 ... 100%).
                // If Vuforia.init() returns -1, it indicates an error.
                // Initialization is done when progress has reached 100%.
                mProgressValue = Vuforia.init();
                
                // Publish the progress value:
                publishProgress(mProgressValue);
                
                // We check whether the task has been canceled in the
                // meantime (by calling AsyncTask.cancel(true)).
                // and bail out if it has, thus stopping this thread.
                // This is necessary as the AsyncTask will run to completion
                // regardless of the status of the component that
                // started is.
            } while (!isCancelled() && mProgressValue >= 0
                && mProgressValue < 100);
            
            return (mProgressValue > 0);
        }
    
    protected void onProgressUpdate(Integer... values)
    {
        // Do something with the progress value "values[0]", e.g. update
        // splash screen, progress bar, etc.
    }
}
