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

/**
 * 
 * @author Giorgio
 * DialogFragment per la scelta tra uno dei valori accettati
 */

public class ChoosePropertyFragment extends DialogFragment{
	
	private Dialog dialog;
	private Property prop;
	private OnEditListener mCallback;

	
	public static String fragment_tag = "CHOOSE_PROPERTY_FRAGMENT";

	
	ChoosePropertyFragment(Property prop){
		this.prop = prop;
	}
	
	public static ChoosePropertyFragment newInstance(Property prop) {
		ChoosePropertyFragment frag = new ChoosePropertyFragment(prop);
        return frag;
    }

	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
           mCallback = (OnEditListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnEditListener");
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
		b.setTitle(getString(R.string.modify_value));
		b.setIcon(R.drawable.icon2);
		b.setCancelable(true);

		b.setItems(prop.getAcceptedValues().toArray(new String[prop.getAcceptedValues().size()]), new OnClickListener(){
			public void onClick(DialogInterface dialog, int which) {
				prop.setCurrentValue(prop.getAcceptedValues().get(which));
				mCallback.onPropertyEdited(prop);
				dialog.dismiss();
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
