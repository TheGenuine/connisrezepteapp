package de.reneruck.connisRezepteApp;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;

public class WaitingDialog extends DialogFragment{

	
	
	@Override
	public Dialog onCreateDialog(final Bundle savedInstanceState) {
	    final ProgressDialog dialog = new ProgressDialog(getActivity());

	    dialog.setTitle(R.string.inprogress);
	    dialog.setMessage(getString(R.string.inprogress));
	    dialog.setIndeterminate(true);
	    dialog.setCancelable(false);

	    return dialog;
	}

	public static DialogFragment newInstance(int title) {
		 WaitingDialog frag = new WaitingDialog(); 
		 Bundle bundle = new Bundle();
		 bundle.putInt("title", title);
		 frag.setArguments(bundle);
		 return frag;
	}
}
