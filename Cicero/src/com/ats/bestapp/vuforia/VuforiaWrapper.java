package com.ats.bestapp.vuforia;

import com.ats.bestapp.utils.Commons;
import com.qualcomm.vuforia.CameraCalibration;
import com.qualcomm.vuforia.CameraDevice;
import com.qualcomm.vuforia.ImageTracker;
import com.qualcomm.vuforia.PIXEL_FORMAT;
import com.qualcomm.vuforia.Renderer;
import com.qualcomm.vuforia.State;
import com.qualcomm.vuforia.TargetFinder;
import com.qualcomm.vuforia.Tool;
import com.qualcomm.vuforia.Tracker;
import com.qualcomm.vuforia.TrackerManager;
import com.qualcomm.vuforia.Vec2I;
import com.qualcomm.vuforia.VideoBackgroundConfig;
import com.qualcomm.vuforia.VideoMode;
import com.qualcomm.vuforia.Vuforia;
import com.qualcomm.vuforia.Vuforia.UpdateCallbackInterface;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

public class VuforiaWrapper{

	private static final String LOGTAG = "VuforiaWrapper";
	private InitVuforiaTask initVuforiaTask;
	
	//pattern Singleton
	private static VuforiaWrapper instance = null;
	
	private VuforiaWrapper() {}

	public static synchronized VuforiaWrapper getInstance() {
	        if (instance == null) 
	        	instance = new VuforiaWrapper();
	        return instance;
	    }

	
	
	public void initAR(Activity currentARActivity) throws Exception{
			 //initVuforiaTask = new InitVuforiaTask();
			 //initVuforiaTask.execute(currentARActivity);
			 Vuforia.setInitParameters(currentARActivity, Vuforia.GL_20);
			 int mProgressValue = -1;
			 do
	            {
	                // Vuforia.init() blocks until an initialization step is
	                // complete, then it proceeds to the next step and reports
	                // progress in percents (0 ... 100%).
	                // If Vuforia.init() returns -1, it indicates an error.
	                // Initialization is done when progress has reached 100%.
	                mProgressValue = Vuforia.init();
	                
	                // Publish the progress value:
	                //publishProgress(mProgressValue);
	                
	                // We check whether the task has been canceled in the
	                // meantime (by calling AsyncTask.cancel(true)).
	                // and bail out if it has, thus stopping this thread.
	                // This is necessary as the AsyncTask will run to completion
	                // regardless of the status of the component that
	                // started is.
	            } while (mProgressValue >= 0
	                && mProgressValue < 100);
	}
    
    // Starts Vuforia, initialize and starts the camera and start the trackers
    public void startAR(int camera,int mScreenWidth,int mScreenHeight,boolean isPortrait) throws Exception
    {
        String error;
        if (!CameraDevice.getInstance().init(camera))
        {
            error = "Unable to open camera device: " + camera;
            Log.e(LOGTAG, error);
            throw new Exception(error);
        }
        
        configureVideoBackground(mScreenHeight,mScreenWidth,isPortrait);
        
        if (!CameraDevice.getInstance().selectVideoMode(
            CameraDevice.MODE.MODE_DEFAULT))
        {
            error = "Unable to set video mode";
            Log.e(LOGTAG, error);
            throw new Exception(error);
        }
        
        if (!CameraDevice.getInstance().start())
        {
            error = "Unable to start camera device: " + camera;
            Log.e(LOGTAG, error);
            throw new Exception(error);
        }
        
        Vuforia.setFrameFormat(PIXEL_FORMAT.RGB565, true);
        doStartTrackers();
        
        try
        {
            setFocusMode(CameraDevice.FOCUS_MODE.FOCUS_MODE_CONTINUOUSAUTO);
        } catch (Exception exceptionTriggerAuto)
        {
            setFocusMode(CameraDevice.FOCUS_MODE.FOCUS_MODE_NORMAL);
        }
    }
    
    // Configures the video mode and sets offsets for the camera's image
    private void configureVideoBackground(int mScreenHeight,int mScreenWidth, boolean isPortrait)
    {
        CameraDevice cameraDevice = CameraDevice.getInstance();
        VideoMode vm = cameraDevice.getVideoMode(CameraDevice.MODE.MODE_DEFAULT);
        
        VideoBackgroundConfig config = new VideoBackgroundConfig();
        config.setEnabled(true);
        config.setSynchronous(true);
        config.setPosition(new Vec2I(0, 0));
        
        int xSize = 0, ySize = 0;
        if (isPortrait)
        {
            xSize = (int) (vm.getHeight() * (mScreenHeight / (float) vm
                .getWidth()));
            ySize = mScreenHeight;
            
            if (xSize < mScreenWidth)
            {
                xSize = mScreenWidth;
                ySize = (int) (mScreenWidth * (vm.getWidth() / (float) vm
                    .getHeight()));
            }
        } else
        {
            xSize = mScreenWidth;
            ySize = (int) (vm.getHeight() * (mScreenWidth / (float) vm
                .getWidth()));
            
            if (ySize < mScreenHeight)
            {
                xSize = (int) (mScreenHeight * (vm.getWidth() / (float) vm
                    .getHeight()));
                ySize = mScreenHeight;
            }
        }
        
        config.setSize(new Vec2I(xSize, ySize));
        
        Log.i(LOGTAG, "Configure Video Background : Video (" + vm.getWidth()
            + " , " + vm.getHeight() + "), Screen (" + mScreenWidth + " , "
            + mScreenHeight + "), mSize (" + xSize + " , " + ySize + ")");
        
        Renderer.getInstance().setVideoBackgroundConfig(config);
        
    }
    
   

    public boolean doStartTrackers()
    {
        // Indicate if the trackers were started correctly
        boolean result = true;
        
        // Start the tracker:
        TrackerManager trackerManager = TrackerManager.getInstance();
        ImageTracker imageTracker = (ImageTracker) trackerManager
            .getTracker(ImageTracker.getClassType());
        imageTracker.start();
        
        // Start cloud based recognition if we are in scanning mode:
        TargetFinder targetFinder = imageTracker.getTargetFinder();
        targetFinder.startRecognition();
        
        return result;
    }
    
    public boolean doStopTrackers()
    {
        // Indicate if the trackers were stopped correctly
        boolean result = true;
        
        TrackerManager trackerManager = TrackerManager.getInstance();
        ImageTracker imageTracker = (ImageTracker) trackerManager
            .getTracker(ImageTracker.getClassType());
        
        if(imageTracker != null)
        {
            imageTracker.stop();
            
            // Stop cloud based recognition:
            TargetFinder targetFinder = imageTracker.getTargetFinder();
            targetFinder.stop();
            
            // Clears the trackables
            targetFinder.clearTrackables();
        }
        else
        {
            result = false;
        }
        
        return result;
    }
    
    // Applies auto focus if supported by the current device
    private boolean setFocusMode(int mode) throws Exception
    {
        boolean result = CameraDevice.getInstance().setFocusMode(mode);
        
        if (!result)
            throw new Exception(
                "Failed to set focus mode: " + mode);
        
        return result;
    }
    
    public void doLoadTrackersData() throws Exception
    {
        Log.d(LOGTAG, "initCloudReco");
        
        // Get the image tracker:
        TrackerManager trackerManager = TrackerManager.getInstance();
        ImageTracker imageTracker = (ImageTracker) trackerManager
            .getTracker(ImageTracker.getClassType());
        
        // Initialize target finder:
        TargetFinder targetFinder = imageTracker.getTargetFinder();
        
        // Start initialization:
        if (targetFinder.startInit(Commons.kAccessKey, Commons.kSecretKey))
        {
            targetFinder.waitUntilInitFinished();
        }
        
        int resultCode = targetFinder.getInitState();
        if (resultCode != TargetFinder.INIT_SUCCESS)
        {
            if(resultCode == TargetFinder.INIT_ERROR_NO_NETWORK_CONNECTION)
            {
                //mInitErrorCode = Commons.UPDATE_ERROR_NO_NETWORK_CONNECTION;
            }
            else
            {
               // mInitErrorCode = Commons.UPDATE_ERROR_SERVICE_NOT_AVAILABLE;
            }
                
            Log.e(LOGTAG, "Failed to initialize target finder.");
            throw new Exception("Failed to initialize target finder.");
        }
        
        // Use the following calls if you would like to customize the color of
        // the UI
        // targetFinder->setUIScanlineColor(1.0, 0.0, 0.0);
        // targetFinder->setUIPointColor(0.0, 0.0, 1.0);

    }
    
 // Resumes Vuforia, restarts the trackers and the camera
    public void resumeAR() throws Exception
    {
        // Vuforia-specific resume operation
        Vuforia.onResume();
        //startAR(mCamera);
    }
    
    
    // Pauses Vuforia and stops the camera
    public void pauseAR() throws Exception
    {
    	//stopCamera();
        Vuforia.onPause();
    }
    
    public void stopCamera()
    {
        doStopTrackers();
        CameraDevice.getInstance().stop();
        CameraDevice.getInstance().deinit();
    }
    
 // Stops any ongoing initialization, stops Vuforia
    public void stopAR() throws Exception
    {
        
        stopCamera();
            
            boolean unloadTrackersResult;
            boolean deinitTrackersResult;
            
            // Destroy the tracking data set:
            unloadTrackersResult = doUnloadTrackersData();
            
            // Deinitialize the trackers:
            deinitTrackersResult = doDeinitTrackers();
            
            // Deinitialize Vuforia SDK:
            Vuforia.deinit();
            
            if (!unloadTrackersResult)
                throw new Exception(
                    "Failed to unload trackers\' data");
            
            if (!deinitTrackersResult)
                throw new Exception(
                    "Failed to deinitialize trackers");
            
        }
    
    public boolean doUnloadTrackersData()
    {
        return true;
    }
    
    public boolean doDeinitTrackers()
    {
        // Indicate if the trackers were deinitialized correctly
        boolean result = true;
        
        TrackerManager tManager = TrackerManager.getInstance();
        tManager.deinitTracker(ImageTracker.getClassType());
        
        return result;
    }
    
 // Methods to be called to handle lifecycle
    public void onResume()
    {
        Vuforia.onResume();
    }
    
    
    public void onPause()
    {
        Vuforia.onPause();
    }
    
    
    public void onSurfaceChanged(int width, int height)
    {
        Vuforia.onSurfaceChanged(width, height);
    }
    
    
    public void onSurfaceCreated()
    {
        Vuforia.onSurfaceCreated();
    }
    
    public void doInitTrackers() throws Exception
    {
        TrackerManager tManager = TrackerManager.getInstance();
        Tracker tracker;
        tracker = tManager.initTracker(ImageTracker.getClassType());
        if (tracker == null)
        {
            Log.e(
                LOGTAG,
                "Tracker not initialized. Tracker already initialized or the camera is already started");
            throw new Exception("Tracker not initialized. Tracker already initialized or the camera is already started");
        } else
        {
            Log.i(LOGTAG, "Tracker successfully initialized");
        }
    }
    
    
    public void registerVuforiaCallbackHandler(UpdateCallbackInterface callbackHandler) throws Exception{
    	Vuforia.registerCallback(callbackHandler);
    }
    
    public boolean vuforiaRequiresAlpha(){
    	return Vuforia.requiresAlpha();
    }
    
    public void setCameraFlash(boolean activeFlash) throws Exception{
    	boolean result=CameraDevice.getInstance().setFlashTorchMode(activeFlash);
    	if(!result) throw new Exception("Flash non attivabile");
    }
    
    
}
