package de.reneruck.connisRezepteApp.fragments;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import de.reneruck.connisRezepteApp.DBManager;
import de.reneruck.connisRezepteApp.NewDocumentsBean;
import de.reneruck.connisRezepteApp.R;
import de.reneruck.connisRezepteApp.Rezept;

public class DokumentEditDialog extends DialogFragment {

	private List<Rezept> entries = new LinkedList<Rezept>();
	private int actualEntry = 0;
	private static View view;
	private DBManager manager;
	private NewDocumentsBean newDocumentsBean;
	
	 public DokumentEditDialog() {
	}
	
	public DokumentEditDialog(NewDocumentsBean bean, DBManager manager) {
		List<File> liste = bean.getNeueDokumente();
		this.newDocumentsBean = bean;
		for (File file : liste) {
			this.entries.add(new Rezept(file.getName()));
		}
		this.manager = manager;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 

		View v = inflater.inflate(R.layout.fragment_rezept_edit_view, container, false);
		((TextView)v.findViewById(R.id.button_cancel)).setOnClickListener(cancel_listener);
		((TextView)v.findViewById(R.id.button_ok)).setOnClickListener(ok_listener);
		((TextView)v.findViewById(R.id.button_left)).setOnClickListener(left_button_listener);
		((TextView)v.findViewById(R.id.button_right)).setOnClickListener(right_button_listener);
		this.view = v;
		
		fillInActualEntryData();
		return v;
    }
	
	/**
	 * Get the Rezept Object from the entries list and fill the layout with it's data
	 */
	private void fillInActualEntryData() {
		// set the counter on top
		((TextView) view.findViewById(R.id.num_display)).setText(actualEntry+1 + "/" + entries.size());
		
		//fill in data
		setStorageIndicator(this.entries.get(actualEntry).isStored());
		((EditText) view.findViewById(R.id.input_rezept_name)).setText(entries.get(actualEntry).getName());
		((TextView) view.findViewById(R.id.rezept_document_path)).setText(entries.get(actualEntry).getDocumentPath());

	}
	
	/**
	 * gets all text and so from the gui entries and saves it to the corrosponding object
	 */
	private void saveGuiToObject(){
		Rezept rezept = this.entries.get(this.actualEntry);
		rezept.setName(((TextView)view.findViewById(R.id.input_rezept_name)).getText().toString());
		rezept.setZubereitungsart(((TextView)view.findViewById(R.id.input_zubereitung)).getText().toString());
		List<String> kategorien = new LinkedList<String>();
		kategorien.add(((TextView)view.findViewById(R.id.input_kategorie)).getText().toString());
		rezept.setKategorien(kategorien);
		rezept.setZutaten(((TextView)view.findViewById(R.id.input_zutaten)).getText().toString());
	}
	
	private OnClickListener cancel_listener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			dismiss();
		}
	};
	private OnClickListener ok_listener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			saveToDatabase();
		}
	};
	
	private void saveToDatabase() {
		saveGuiToObject();
		if(entries.get(actualEntry).saveToDB(manager.getWritableDatabase())){
			Toast.makeText(getActivity(), "The Object saved successfully", Toast.LENGTH_SHORT).show();
			setStorageIndicator(true);
		}else{
			Toast.makeText(getActivity(), "ERROR on saving object", Toast.LENGTH_SHORT).show();
		}
	}

	private void setStorageIndicator(boolean status) {
		if(status) {
			(view.findViewById(R.id.line)).setBackgroundColor(Color.GREEN);
		} else {
			view.findViewById(R.id.line).setBackgroundColor(Color.DKGRAY);
		}
	}
	private OnClickListener left_button_listener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			if(actualEntry == 0){
				actualEntry = entries.size()-1;
			}else{
				actualEntry--;
			}
			fillInActualEntryData();
		}
	};
	private OnClickListener right_button_listener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			if(actualEntry == entries.size()-1){
				actualEntry = 0;
			}else{
				actualEntry++;
			}
			fillInActualEntryData();
		}
	};
	TextView.OnEditorActionListener returnButtonListener = new TextView.OnEditorActionListener() {

		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (actionId == EditorInfo.IME_NULL) {
			}
			return true;
		}
	};
	public void onDismiss(android.content.DialogInterface dialog) {
		for (Rezept rezept: entries) {
			if (rezept.isStored()) {
				newDocumentsBean.removeEntry(rezept.getDocumentName());
			}
		}
		super.onDismiss(dialog);
	};
}
