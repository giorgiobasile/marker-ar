package it.poliba.giorgiobasile.markerar.markerdetection;

import org.opencv.samples.cameracalibration.CameraCalibrationActivity;

import it.poliba.giorgiobasile.markerar.R;
import it.poliba.giorgiobasile.markerar.properties.Property;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * 
 * @author Giorgio
 * DialogFragment per la richiesta di calibrazione
 */

public class CalibrationDialogFragment extends DialogFragment{
	
	private Dialog dialog;

	
	public static String TAG = "CALIBRATION_DIALOG_FRAGMENT";

	
	CalibrationDialogFragment(){

	}
	
	public static CalibrationDialogFragment newInstance() {
		CalibrationDialogFragment frag = new CalibrationDialogFragment();
        return frag;
    }
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder b = new Builder(getActivity());
		b.setTitle(getString(R.string.calibration));
		b.setIcon(R.drawable.icon2);
		b.setCancelable(false);
		b.setMessage(R.string.calibration_request);
		b.setNegativeButton(android.R.string.cancel, new OnClickListener(){
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}});
		b.setPositiveButton(android.R.string.ok, new OnClickListener(){
			public void onClick(DialogInterface dialog, int which) {		
				dialog.dismiss();
				startActivity(new Intent(getActivity(), CameraCalibrationActivity.class));
			}
		});

        dialog = b.create();
	    return dialog;
    }
	
    // Container Activity must implement this interface
	public interface OnEditListener {
        public void onPropertyEdited(Property prop);
    }
}
