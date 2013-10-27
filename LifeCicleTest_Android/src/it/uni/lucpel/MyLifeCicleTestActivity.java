package it.uni.lucpel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MyLifeCicleTestActivity extends Activity {

	// Valore per il Tag del Log
	private final static String ACTIVITY_TAG = "LIFECYCLE_TEST";

	// Contatore che descrive un possibile stato
	protected int counter = 0;

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Visualizziamo messaggio di Log
		Log.i(ACTIVITY_TAG, "ON_CREATE " + getActivityName());
		// Impostiamo il Layout
		setContentView(R.layout.main);
		// Bottone che permette di visualizzare la seconda Activity
		Button navButton = (Button) findViewById(R.id.navButton);
		navButton.setOnClickListener(new OnClickListener() {

			
			public void onClick(View arg0) {
				// Andiamo in modo esplicito alla seconda
				// Activity
				Log.i(ACTIVITY_TAG, "LAUNCHING SECOND ACTIVITY ");
				Intent intent = new Intent(MyLifeCicleTestActivity.this,
						SecondActivity.class);
				startActivity(intent);
				
			}


		});
		// Bottone che permette di terminare l'attività corrente
		Button killButton = (Button) findViewById(R.id.killButton);
		killButton.setOnClickListener(new OnClickListener() {
		
			public void onClick(View arg0) {
				Log.i(ACTIVITY_TAG, "finish() on " + getActivityName());
				finish();				
			}
		});
		// Bottone che incrementa il contatore come stato della attività
		Button countButton = (Button) findViewById(R.id.countButton);
		countButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				Log.i(ACTIVITY_TAG, "finish() on " + getActivityName());
				counter++;
				showCounterState();
			}
		});
		showCounterState();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.i(ACTIVITY_TAG, "ON_SAVE_INSTANCE_STATE " + getActivityName());
		// Salviamo lo stato del contatore
		outState.putInt("counter", counter);
	}

	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		Log.i(ACTIVITY_TAG, "ON_RESTORE_INSTANCE_STATE " + getActivityName());
		counter = savedInstanceState.getInt("counter");
		showCounterState();
	}

	protected String getActivityName() {
		return "FIRST ACTIVITY";
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i(ACTIVITY_TAG, "ON_DESTROY " + getActivityName());
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i(ACTIVITY_TAG, "ON_PAUSE " + getActivityName());
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.i(ACTIVITY_TAG, "ON_RESTART" + getActivityName());
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(ACTIVITY_TAG, "ON_RESUME " + getActivityName());
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.i(ACTIVITY_TAG, "ON_START " + getActivityName());
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.i(ACTIVITY_TAG, "ON_STOP " + getActivityName());
	}

	// Metodo di utilità che permette la visualizzazione dello stato del
	// contatore nella TextView
	private void showCounterState() {
		TextView textView = (TextView) findViewById(R.id.counterValue);
		textView.setText(" Counter: " + counter);
	}

}