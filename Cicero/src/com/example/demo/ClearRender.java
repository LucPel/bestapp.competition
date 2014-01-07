package com.example.demo;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.qualcomm.vuforia.Renderer;
import com.qualcomm.vuforia.State;
import com.qualcomm.vuforia.TrackableResult;
import com.qualcomm.vuforia.VIDEO_BACKGROUND_REFLECTION;
import com.qualcomm.vuforia.Vuforia;

import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.widget.Toast;



public class ClearRender implements GLSurfaceView.Renderer{

	VuforiaWrapper vw;
	Activity cloudRepo;
	
	public ClearRender(VuforiaWrapper vw,Activity activity){
		this.vw=vw;
		cloudRepo=activity;
		
	}
	
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
       initRendering();
    }

    public void onSurfaceChanged(GL10 gl, int w, int h) {
    	vw.onSurfaceChanged(w, h);
    }

    public void onDrawFrame(GL10 gl) {
        //gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
    	 // Clear color and depth buffer
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        
        // Get the state from Vuforia and mark the beginning of a rendering
        // section
        State state = Renderer.getInstance().begin();
        
        // Explicitly render the Video Background
        Renderer.getInstance().drawVideoBackground();
        
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        if (Renderer.getInstance().getVideoBackgroundConfig().getReflection() == VIDEO_BACKGROUND_REFLECTION.VIDEO_BACKGROUND_REFLECTION_ON)
            GLES20.glFrontFace(GLES20.GL_CW);  // Front camera
        else
            GLES20.glFrontFace(GLES20.GL_CCW);   // Back camera
            
        // Did we find any trackables this frame?
        if (state.getNumTrackableResults() > 0)
        {
            // Gets current trackable result
            TrackableResult trackableResult = state.getTrackableResult(0);
            
            if (trackableResult == null)
            {
                return;
            }
            
            // Renders the Augmentation View with the 3D Book data Panel
            Log.i("RenderCustom", "pippiozzzoo");
            
        }
        
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        
        Renderer.getInstance().end(); 
    	
    }
    
    private void initRendering(){
        // Define clear color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, Vuforia.requiresAlpha() ? 0.0f
            : 1.0f);
        vw.onSurfaceCreated();
    }
}
