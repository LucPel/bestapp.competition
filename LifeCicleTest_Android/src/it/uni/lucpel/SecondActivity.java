package it.uni.lucpel;

import android.os.Bundle;
import android.widget.TextView;


public class SecondActivity extends MyLifeCicleTestActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        TextView output = (TextView)findViewById(R.id.output);
        String text = getResources().getString(R.string.second_label);
        output.setText(text);
	}
	
    protected String getActivityName(){
    	return "SECOND ACTIVITY";
    }	
	

}
