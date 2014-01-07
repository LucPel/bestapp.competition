package com.ats.bestapp;

import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.ats.bestapp.utils.Commons;
import com.ats.bestapp.vuforia.VuforiaWrapper;
import com.ats.bestapp.R;
import com.qualcomm.vuforia.CameraDevice;
import com.qualcomm.vuforia.ImageTracker;
import com.qualcomm.vuforia.State;
import com.qualcomm.vuforia.TargetFinder;
import com.qualcomm.vuforia.TargetSearchResult;
import com.qualcomm.vuforia.Trackable;
import com.qualcomm.vuforia.TrackerManager;
import com.qualcomm.vuforia.Vuforia;
import com.qualcomm.vuforia.Vuforia.UpdateCallbackInterface;



public class MyCloudReco extends Activity implements UpdateCallbackInterface{

private static final String LOGTAG = "CloudReco";

    VuforiaWrapper vufWrap;
    
    // Our OpenGL view:
    private SampleApplicationGLView mGlView;
    
    // Our renderer:
    private boolean mFlash = false;
    private boolean mContAutofocus = false;
    private boolean mExtendedTracking = false;
    
    // Display size of the device:
 	 private int mScreenWidth = 0;
 	 private int mScreenHeight = 0;
	 private boolean isPortrait;
    
    private View mFlashOptionView;
    
    // The textures we will use for rendering:
    //private Vector<Texture> mTextures;
    
    // View overlays to be displayed in the Augmented View
    private RelativeLayout mUILayout;
    
    // Error message handling:
    private int mlastErrorCode = 0;
    private int mInitErrorCode = 0;
    private boolean mFinishActivityOnError;
    
    // Alert Dialog used to display SDK errors
    private AlertDialog mErrorDialog;
    
    private GestureDetector mGestureDetector;
    
    private double mLastErrorTime;
    
    boolean mIsDroidDevice = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(LOGTAG, "onCreate");
        super.onCreate(savedInstanceState);
        vufWrap=new VuforiaWrapper();
        //vuforiaAppSession = new SampleApplicationSession(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        updateActivityOrientation();
        storeScreenDimensions();
		// As long as this window is visible to the user, keep the device's
        // screen turned on and bright:
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        try{
        	Log.i(LOGTAG, "init AR");
            vufWrap.initAR(this);
            Log.i(LOGTAG, "init Tracker");
            vufWrap.doInitTrackers();
            Log.i(LOGTAG, "Load Tracker Cloud Data");
            vufWrap.doLoadTrackersData();
            Log.i(LOGTAG, "Register Callback");
            vufWrap.registerVuforiaCallbackHandler(this);
            Log.i(LOGTAG, "init GL View");
            initApplicationAR();
            Log.i(LOGTAG, "Start AR");
            onInitARDone();
            mIsDroidDevice = android.os.Build.MODEL.toLowerCase().startsWith(
                    "droid");
        }
        catch(Exception e){
        	Log.e(LOGTAG, "Error onCreate ");
        	e.printStackTrace();
        }
        //startLoadingAnimation();
        
        //vuforiaAppSession.initAR(this, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        // Creates the GestureDetector listener for processing double tap
        //mGestureDetector = new GestureDetector(this, new GestureListener());
        
        //mTextures = new Vector<Texture>();
        //loadTextures();
        
       
        
    }
    
    public void onInitARDone()
    {
            
            // Now add the GL surface view. It is important
            // that the OpenGL ES surface view gets added
            // BEFORE the camera is started and video
            // background is configured.
            addContentView(mGlView, new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
            
            // Start the camera:
            try
            {
                vufWrap.startAR(CameraDevice.CAMERA.CAMERA_DEFAULT,mScreenWidth , mScreenHeight, isPortrait);
            } catch (Exception e)
            {
                Log.e(LOGTAG, e.getMessage());
            }
            
            boolean result = CameraDevice.getInstance().setFocusMode(
                CameraDevice.FOCUS_MODE.FOCUS_MODE_CONTINUOUSAUTO);
            
            if (result)
                mContAutofocus = true;
            else
                Log.e(LOGTAG, "Unable to enable continuous autofocus");
            
            //mUILayout.bringToFront();
            
            // Hides the Loading Dialog
            //loadingDialogHandler.sendEmptyMessage(Commons.HIDE_LOADING_DIALOG);
            
           //mUILayout.setBackgroundColor(Color.TRANSPARENT);
            
    }
    
    // Initializes AR application components.
    private void initApplicationAR()
    {
        // Create OpenGL ES view:
        int depthSize = 16;
        int stencilSize = 0;
        boolean translucent = vufWrap.vuforiaRequiresAlpha();
        
        // Initialize the GLView with proper flags
        mGlView = new SampleApplicationGLView(this);
        mGlView.init(translucent, depthSize, stencilSize);
        
        // Setups the Renderer of the GLView
        //mRenderer = new CloudRecoRenderer(vuforiaAppSession);
        //mRenderer.setTextures(mTextures);
        mGlView.setRenderer(new ClearRender(vufWrap,this));
        LayoutInflater inflater = LayoutInflater.from(this);
        mUILayout = (RelativeLayout) inflater.inflate(R.layout.camera_overlay,
            null, false);
        mUILayout.setVisibility(View.VISIBLE);
        mUILayout.setBackgroundColor(Color.BLACK);
        
        addContentView(mUILayout, new LayoutParams(LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT));
        
    }
    
    // Stores screen dimensions
    private void storeScreenDimensions()
    {
        // Query display dimensions:
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mScreenWidth = metrics.widthPixels;
        mScreenHeight = metrics.heightPixels;
    }
    
 // Callback called every cycle
    @Override
    public void QCAR_onUpdate(State state)
    {
        // Get the tracker manager:
        TrackerManager trackerManager = TrackerManager.getInstance();
        
        // Get the image tracker:
        ImageTracker imageTracker = (ImageTracker) trackerManager
            .getTracker(ImageTracker.getClassType());
        
        // Get the target finder:
        TargetFinder finder = imageTracker.getTargetFinder();
        
        // Check if there are new results available:
        final int statusCode = finder.updateSearchResults();
        
        // Show a message if we encountered an error:
        if (statusCode < 0)
        {
            
            boolean closeAppAfterError = (
                statusCode == Commons.UPDATE_ERROR_NO_NETWORK_CONNECTION ||
                statusCode == Commons.UPDATE_ERROR_SERVICE_NOT_AVAILABLE);
            
            //showErrorMessage(statusCode, state.getFrame().getTimeStamp(), closeAppAfterError);
            
        } else if (statusCode == TargetFinder.UPDATE_RESULTS_AVAILABLE)
        {
            // Process new search results
            if (finder.getResultCount() > 0)
            {
                TargetSearchResult result = finder.getResult(0);
                
                // Check if this target is suitable for tracking:
                if (result.getTrackingRating() > 0)
                {
                    Trackable trackable = finder.enableTracking(result);
                    String toastMessage="riconosciuto Target "+trackable.getId()+" "+trackable.getName();
                    Toast.makeText(this, toastMessage , Toast.LENGTH_LONG).show();
                    if (mExtendedTracking)
                        trackable.startExtendedTracking();
                }
            }
        }
    }
    
    // Stores the orientation depending on the current resources configuration
    private void updateActivityOrientation()
    {
        Configuration config = this.getResources().getConfiguration();
        
        switch (config.orientation)
        {
            case Configuration.ORIENTATION_PORTRAIT:
                isPortrait = true;
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                isPortrait = false;
                break;
            case Configuration.ORIENTATION_UNDEFINED:
            default:
                break;
        }
        
        Log.i(LOGTAG, "Activity is in "
            + (isPortrait ? "PORTRAIT" : "LANDSCAPE"));
    }
    
    // The final call you receive before your activity is destroyed.
    @Override
    protected void onDestroy()
    {
        Log.d(LOGTAG, "onDestroy");
        super.onDestroy();
        
        try
        {
            vufWrap.stopAR();
        } catch (Exception e)
        {
            Log.e(LOGTAG, e.getMessage());
        }
        
        System.gc();
    }
    
 // Called when the system is about to start resuming a previous activity.
    @Override
    protected void onPause()
    {
        Log.d(LOGTAG, "onPause");
        super.onPause();
        
        // Turn off the flash
        if (mFlashOptionView != null && mFlash)
        {
            // OnCheckedChangeListener is called upon changing the checked state
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            {
                //((Switch) mFlashOptionView).setChecked(false);
            } else
            {
                //((CheckBox) mFlashOptionView).setChecked(false);
            }
        }
        
        try
        {
            vufWrap.pauseAR();
        } catch (Exception e)
        {
            Log.e(LOGTAG, e.getMessage());
        }
        
        // Pauses the OpenGLView
        if (mGlView != null)
        {
            mGlView.setVisibility(View.INVISIBLE);
            mGlView.onPause();
        }
    }
    
    // Called when the activity will start interacting with the user.
    @Override
    protected void onResume()
    {
        Log.d(LOGTAG, "onResume");
        super.onResume();
        
        // This is needed for some Droid devices to force portrait
        if (mIsDroidDevice)
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        
        try
        {
            vufWrap.resumeAR();
        } catch (Exception e)
        {
            Log.e(LOGTAG, e.getMessage());
        }
        
        // Resume the GL view:
        if (mGlView != null)
        {
            mGlView.setVisibility(View.VISIBLE);
            mGlView.onResume();
        }
        
    }
}
