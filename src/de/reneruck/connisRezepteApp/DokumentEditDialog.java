package de.reneruck.connisRezepteApp;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DokumentEditDialog extends DialogFragment {

	private List<Rezept> entries = new LinkedList<Rezept>();
	private int actualEntry = 0;
	private static View view;
	private DBManager manager;
	private NewDocumentsBean newDocumentsBean;
	
	public DokumentEditDialog(NewDocumentsBean bean, DBManager manager) {
		List<String> liste = bean.getNeueDokumente();
		this.newDocumentsBean = bean;
		for (String string : liste) {
			this.entries.add(new Rezept(string));
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
		
		View v = inflater.inflate(R.layout.rezept_edit_view, container, false);
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
		((TextView) view.findViewById(R.id.num_display)).setText(actualEntry + "/" + entries.size());
		
		//fill in data
		if(entries.get(actualEntry).isStored()){
			((LinearLayout) view.findViewById(R.id.line)).setBackgroundColor(Color.GREEN);
		} else {
			((LinearLayout) view.findViewById(R.id.line)).setBackgroundColor(Color.DKGRAY);
		}
		((EditText) view.findViewById(R.id.rezept_name)).setText(entries.get(actualEntry).getName());
		((EditText) view.findViewById(R.id.rezept_name)).setOnEditorActionListener(returnButtonListener);
		((TextView) view.findViewById(R.id.rezept_document_path)).setText(entries.get(actualEntry).getDocumentPath());

		List<String> keywords = entries.get(actualEntry).getKeywords();
		if(keywords != null){
			((EditText) view.findViewById(R.id.rezept_keywords)).setText(StringUtils.join(keywords, ","));
			((EditText) view.findViewById(R.id.rezept_keywords)).setOnEditorActionListener(returnButtonListener);
		}
	}
	
	/**
	 * gets all text and so from the gui entries and saves it to the corrosponding object
	 */
	private void saveGuiToObject(){
		entries.get(actualEntry).setName(((EditText) view.findViewById(R.id.rezept_name)).getText().toString());
		entries.get(actualEntry).setKeywords(((EditText) view.findViewById(R.id.rezept_keywords)).getText().toString());
		
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
			((LinearLayout) view.findViewById(R.id.line)).setBackgroundColor(Color.GREEN);
		}else{
			Toast.makeText(getActivity(), "ERROR on saving object", Toast.LENGTH_SHORT).show();
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
//				saveToDatabase();
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
