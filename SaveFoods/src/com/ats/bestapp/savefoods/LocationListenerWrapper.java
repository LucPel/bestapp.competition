package com.ats.bestapp.savefoods;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class LocationListenerWrapper implements LocationListener{

	private Activity linkedActivity;
	private LocationManager locationManager;
	private String provider;
	private String LogTag="LocationListenerWrapper";
	private double latitude;
	private double longitude;

	
	public LocationListenerWrapper(Activity activity){
		linkedActivity=activity;
		locationManager = (LocationManager) linkedActivity.getSystemService(Context.LOCATION_SERVICE);
	    // Define the criteria how to select the locatioin provider -> use
	    // default
	    Criteria criteria = new Criteria();
	    provider = locationManager.getBestProvider(criteria, false);
	    Location location = locationManager.getLastKnownLocation(provider);

	    // Initialize the location fields
	    if (location != null) {
	      Log.i(LogTag, "Provider " + provider + " has been selected.");
	      onLocationChanged(location);
	    }
	    else{
	    	locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
	    }
	}
	
	public LocationListenerWrapper(Application application){
		locationManager = (LocationManager) application.getSystemService(Context.LOCATION_SERVICE);
	    // Define the criteria how to select the locatioin provider -> use
	    // default
	    Criteria criteria = new Criteria();
	    provider = locationManager.getBestProvider(criteria, false);
	    Location location = locationManager.getLastKnownLocation(provider);

	    // Initialize the location fields
	    if (location != null) {
	      Log.i(LogTag, "Provider " + provider + " has been selected.");
	      onLocationChanged(location);
	    }
	    else{
	    	locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
	    }
	}
	
	@Override
	public void onLocationChanged(Location location) {
		latitude=location.getLatitude();
		longitude=location.getLongitude();
		Log.i(LogTag, "Latitude " + String.valueOf(latitude) + " Longitude " + String.valueOf(longitude));
	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.i(LogTag, "Disabled new provider " + provider);
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		Log.i(LogTag, "Enabled new provider " + provider);
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
	public void requestLocationUpdates(){
		locationManager.requestLocationUpdates(provider, 0, 0, this);
	}

	public void removeUpdates(){
		locationManager.removeUpdates(this);
	}
	
	public double getLatitude(){
		return latitude;
	}
	
	public double getLongitude(){
		return longitude;
	}
}
