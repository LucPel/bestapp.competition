package com.ats.bestapp.savefoods.data;

import java.io.File;

import android.net.Uri;

public class ImageWrapper {

	private byte[] image;
	
	public void setImage(byte[] image){
		this.image=image;
	}
	
	public byte[] getImage(){
		return image;
	}
}
