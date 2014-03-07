package com.ats.bestapp.savefoods;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.plus.PlusClient;

import android.os.Bundle;
import android.accounts.Account;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.transition.Visibility;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class LoginActivity extends Activity implements View.OnClickListener,
		ConnectionCallbacks, OnConnectionFailedListener {

	private static final int REQUEST_CODE_RESOLVE_ERR = 9000;

	private ProgressDialog mConnectionProgressDialog;
	private final String LogTag = "Login";
	private PlusClient mPlusClient;
	private ConnectionResult mConnectionResult;
	private boolean loggedin = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		mPlusClient = new PlusClient.Builder(this, this, this).setActions(
				"http://schemas.google.com/AddActivity",
				"http://schemas.google.com/BuyActivity")
		// .setScopes(Scopes.PLUS_LOGIN) // recommended login scope for social
		// features
		// .setScopes("profile") // alternative basic login scope
				.build();
		SharedPreferences settings = getSharedPreferences(
				Constants.sharedPreferencesName, 0);
		String userName = settings.getString(Constants.userNameSP, null);
		if (userName != null) {
			mPlusClient.connect();
			startDialogLoading();
		} else {
			findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
			findViewById(R.id.sign_in_button).setOnClickListener(
					(OnClickListener) this);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (result.hasResolution()) {
			try {
				result.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
			} catch (SendIntentException e) {
				mPlusClient.connect();
				startDialogLoading();
			}
		}

		mConnectionResult = result;
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		mConnectionProgressDialog.dismiss();
		String accountName = mPlusClient.getAccountName();
		Toast.makeText(this, accountName + " is connected.", Toast.LENGTH_LONG)
				.show();
		SharedPreferences settings = getSharedPreferences(
				Constants.sharedPreferencesName, 0);
		String username=settings.getString(Constants.userNameSP, null);
		if(username!=null && !username.isEmpty()){
			SharedPreferences.Editor editor = settings.edit();
			editor.putString(Constants.userNameSP, accountName);
			// Commit the edits!
			editor.commit();
		}
		loggedin = true;
		Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
		startActivity(intent);
	}

	private void startDialogLoading() {
		mConnectionProgressDialog = new ProgressDialog(this);
		mConnectionProgressDialog.setMessage("Loading");
		mConnectionProgressDialog
				.setProgressStyle(ProgressDialog.THEME_HOLO_LIGHT);
		mConnectionProgressDialog.setCancelable(false);
		mConnectionProgressDialog.show();
	}

	@Override
	public void onDisconnected() {
		Log.d(LogTag, "disconnected");
		loggedin = false;

	}

	@Override
	protected void onActivityResult(int requestCode, int responseCode,
			Intent intent) {
		if (requestCode == REQUEST_CODE_RESOLVE_ERR
				&& responseCode == RESULT_OK) {
			mConnectionResult = null;
			mPlusClient.connect();
		} else {
			mConnectionProgressDialog.dismiss();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		// mPlusClient.connect();
	}

	@Override
	protected void onStop() {
		super.onStop();
		mPlusClient.disconnect();
	}

	public void onResume() {
		super.onResume();
		if (loggedin) {
			showDialog4Exit();
		}

	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.sign_in_button && !mPlusClient.isConnected()) {
			if (mConnectionResult == null) {
				mPlusClient.connect();
				startDialogLoading();
			} else {
				try {
					mConnectionResult.startResolutionForResult(this,
							REQUEST_CODE_RESOLVE_ERR);
				} catch (SendIntentException e) {
					// Try connecting again.
					mConnectionResult = null;
					mPlusClient.connect();
					startDialogLoading();
				}
			}
		}
	}

	public void onBackPressed() {
		mConnectionProgressDialog.dismiss();
		showDialog4Exit();
	}

	private void showDialog4Exit() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this,
				AlertDialog.THEME_HOLO_LIGHT);

		// set title
		alertDialogBuilder.setTitle("Exit");

		// set dialog message
		alertDialogBuilder
				.setMessage("Do you want to exit ?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// if this button is clicked, close
								// current activity
								LoginActivity.this.finish();
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// if this button is clicked, just close
						// the dialog box and do nothing
						dialog.cancel();
						mPlusClient.connect();
						startDialogLoading();
					}
				});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
		// show it
		alertDialog.show();
	}

}
