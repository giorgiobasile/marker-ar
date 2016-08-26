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
import android.widget.LinearLayout;

/**
 * 
 * @author Giorgio
 * DialogFragment per la modifica di una proprietà
 */

public class EditPropertyFragment extends DialogFragment{
	
	private Dialog dialog;
	private Property prop;
	private EditText edit;
	private OnEditListener mCallback;

	
	public static String fragment_tag = "EDIT_PROPERTY_FRAGMENT";

	
	EditPropertyFragment(Property prop){
		this.prop = prop;
	}
	
	public static EditPropertyFragment newInstance(Property prop) {
		EditPropertyFragment frag = new EditPropertyFragment(prop);
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
		b.setCancelable(false);
		b.setNegativeButton(android.R.string.cancel, new OnClickListener(){
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}});
		b.setPositiveButton(android.R.string.ok, new OnClickListener(){
			public void onClick(DialogInterface dialog, int which) {
				prop.setCurrentValue(edit.getText().toString());
				mCallback.onPropertyEdited(prop);
				dialog.dismiss();
			}
		});
		LinearLayout dialogLayout = (LinearLayout)View.inflate(getActivity(), R.layout.edit_dialog, null);
		edit = (EditText) dialogLayout.findViewById(R.id.edit_label);
		edit.setText(prop.getCurrentValue());
        b.setView(dialogLayout);
        dialog = b.create();
	    return dialog;
    }
	
    // Container Activity must implement this interface
	public interface OnEditListener {
        public void onPropertyEdited(Property prop);
    }
}
