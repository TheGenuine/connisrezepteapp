package de.reneruck.connisRezepteApp.fragments;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
	private OnDismissListener listener;
	private List<View> kategorien = new LinkedList<View>();
	
	 public DokumentEditDialog() {
	}
	
	public DokumentEditDialog(NewDocumentsBean bean, DBManager manager, OnDismissListener listener) {
		List<File> liste = bean.getNeueDokumente();
		this.newDocumentsBean = bean;
		for (File file : liste) {
			this.entries.add(new Rezept(file));
		}
		this.manager = manager;
		this.listener = listener;
	}
	
	public DokumentEditDialog(List<Rezept> rezepte, DBManager manager, OnDismissListener listener) {
		this.entries = rezepte;
		this.manager = manager;
		this.listener = listener;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_rezept_edit_view, container, false);
		
		
		((TextView)v.findViewById(R.id.button_cancel)).setOnClickListener(cancel_listener);
		((TextView)v.findViewById(R.id.button_top_save)).setOnClickListener(ok_listener);
		((TextView)v.findViewById(R.id.button_top_cancel)).setOnClickListener(cancel_listener);
		((TextView)v.findViewById(R.id.button_ok)).setOnClickListener(ok_listener);
		((TextView)v.findViewById(R.id.button_left)).setOnClickListener(left_button_listener);
		((TextView)v.findViewById(R.id.button_right)).setOnClickListener(right_button_listener);
		((ImageView)v.findViewById(R.id.button_add_kategorie)).setOnClickListener(add_kategorie_listener);
		((ImageView)v.findViewById(R.id.button_time_plus)).setOnClickListener(time_plus_listener);
		((ImageView)v.findViewById(R.id.button_time_minus)).setOnClickListener(time_minus_listener);
		view = v;
		
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
		((EditText) view.findViewById(R.id.input_rezept_name)).setText(this.entries.get(this.actualEntry).getName());
		((TextView) view.findViewById(R.id.rezept_document_path)).setText(this.entries.get(this.actualEntry).getDocumentPath());
		
		if(this.entries.get(this.actualEntry).getZubereitungsart() == null || this.entries.get(this.actualEntry).getZubereitungsart().length() < 1){
			tryToPredictZubereitungsart();
		}else{
			((TextView) view.findViewById(R.id.input_zubereitung)).setText(this.entries.get(this.actualEntry).getZubereitungsart());
		}
		
		if(this.entries.get(this.actualEntry).getZutaten() == null || this.entries.get(this.actualEntry).getZutaten().size() < 1){
			tryToPredictZutaten();
		}else{
			List<String> zutaten = this.entries.get(this.actualEntry).getZutaten();
			StringBuilder zutatenBuilder = new StringBuilder();
			for (String zutat : zutaten) {
				zutatenBuilder.append(zutat + ",");
			}
			((TextView) view.findViewById(R.id.input_zutaten)).setText(zutatenBuilder.toString());
		}
		
		if(this.entries.get(this.actualEntry).getKategorien() == null || this.entries.get(this.actualEntry).getKategorien().size() < 1){
			tryToPredictKategorie();
		}else{
			((TextView) view.findViewById(R.id.input_kategorie)).setText(this.entries.get(this.actualEntry).getKategorien().get(0));
		}

	}
	
	private void tryToPredictKategorie() {
		((TextView) view.findViewById(R.id.input_kategorie)).setText("");
	}

	private void tryToPredictZutaten() {
		((TextView) view.findViewById(R.id.input_zutaten)).setText("");		
	}

	private void tryToPredictZubereitungsart() {
		((TextView) view.findViewById(R.id.input_zubereitung)).setText("");
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
	
	private OnClickListener time_plus_listener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			EditText timeField = ((EditText)view.findViewById(R.id.input_time));
			timeField.setText(String.valueOf(Integer.parseInt(timeField.getText().toString())+1));
		}
	};
	
	private OnClickListener time_minus_listener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			EditText timeField = ((EditText)view.findViewById(R.id.input_time));
			int newTime = Integer.parseInt(timeField.getText().toString())-1;
			if(newTime < 0){
				newTime = 0;
			}
			timeField.setText(String.valueOf(newTime));
			
		}
	};
	
	private OnClickListener add_kategorie_listener = new OnClickListener() {
		

		@Override
		public void onClick(View v) {
			
			view.findViewById(R.id.button_add_kategorie).setVisibility(View.GONE);
			LinearLayout kategorien_parent = ((LinearLayout)view.findViewById(R.id.kategorien_layout));
			View newkategorie = view.inflate(getActivity(), R.layout.kategorie_input_template, kategorien_parent);
			((ImageView)newkategorie.findViewById(R.id.button_add_kategorie)).setOnClickListener(add_kategorie_listener);
			kategorien.add(newkategorie);
		}
	};
	
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
		if(this.entries.get(actualEntry).saveToDB(manager.getWritableDatabase())){
			Toast.makeText(getActivity(), "The Object saved successfully", Toast.LENGTH_SHORT).show();
			setStorageIndicator(true);
			entries.get(actualEntry).setStored(true);
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
	public void onDismiss(DialogInterface dialog) {
		if(this.newDocumentsBean != null){
			for (Rezept rezept: this.entries) {
				if (rezept.isStored()) {
					this.newDocumentsBean.removeEntry(rezept.getOriginalFile());
				}
			}
		}
		if(this.listener != null){
			this.listener.onDismiss(dialog);
		}
		super.onDismiss(dialog);
	};
}
