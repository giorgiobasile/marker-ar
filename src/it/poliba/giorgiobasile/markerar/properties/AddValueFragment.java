package it.poliba.giorgiobasile.markerar.properties;


import it.poliba.giorgiobasile.markerar.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import android.view.View;

import android.widget.EditText;


/**
 * 
 * @author Giorgio
 * DialogFragment per l'aggiunta di un valore accettato ad uno schema
 */
public class AddValueFragment extends DialogFragment{
	
	private Dialog dialog;
	private EditText valueName;
	private OnAddValueListener mCallback;
	
	public static String fragment_tag = "ADD_VALUE_FRAGMENT";

	
	AddValueFragment(){
	}
	
	public static AddValueFragment newInstance() {
		AddValueFragment frag = new AddValueFragment();
        return frag;
    }

	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
           mCallback = (OnAddValueListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnAddValueListener");
        }
    }
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		AlertDialog.Builder b = new Builder(getActivity());
		b.setTitle(getString(R.string.add_value));
		b.setIcon(R.drawable.icon2);
		b.setCancelable(false);
		b.setNegativeButton(android.R.string.cancel, new OnClickListener(){
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}});
		b.setPositiveButton(android.R.string.ok, new OnClickListener(){
			public void onClick(DialogInterface dialog, int which) {
				if(!valueName.getText().toString().trim().equals("")){
					mCallback.onValueAdded(valueName.getText().toString());
					dialog.dismiss();
				}
			}
		});
		valueName = (EditText)View.inflate(getActivity(), R.layout.add_value_dialog, null);
		
        b.setView(valueName);
        dialog = b.create();
	    return dialog;
    }
	
    // Container Activity must implement this interface
	public interface OnAddValueListener {
        public void onValueAdded(String value);
    }
	
}