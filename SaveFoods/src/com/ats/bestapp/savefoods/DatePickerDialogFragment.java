package com.ats.bestapp.savefoods;

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.EditText;

@SuppressLint("ValidFragment")
public class DatePickerDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
	
	private EditText editableDate;
 
	public DatePickerDialogFragment() {
		// nothing to see here, move along
	}

	public DatePickerDialogFragment(EditText editDate) {
		editableDate=editDate;
	}
 
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Calendar cal = Calendar.getInstance();
 
		return new DatePickerDialog(getActivity(),
				this, cal.get(Calendar.YEAR), 
				cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		editableDate.setText(String.valueOf(dayOfMonth) + "-" + String.valueOf(monthOfYear+1) + "-" + String.valueOf(year));
		
	}
 
}