package com.ats.bestapp.savefoods.data;

import java.io.File;
import java.io.Serializable;

import android.net.Uri;

public class ImageWrapper implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private byte[] image;
	
	public void setImage(byte[] image){
		this.image=image;
	}
	
	public byte[] getImage(){
		return image;
	}
}
