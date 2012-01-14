package de.reneruck.connisRezepteApp;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class DocumentEditActivity extends Activity{

	
	private int actualEntry = 0;
	private List<Rezept> entries = new LinkedList<Rezept>();
	private DBManager manager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.fragment_rezept_edit_view);
		AppContext appContext = (AppContext)getApplicationContext();
		this.manager = appContext.getDBManager();
		
		switch(getIntent().getIntExtra(Configurations.LIST_SOURCE, Configurations.NEW_DOCUMENTS)){
			case  Configurations.NEW_DOCUMENTS:
				this.entries = appContext.getNewDocumentsBean().getNeueRezepte();
				break;
			case Configurations.CUSTOM_LIST:
				this.entries = appContext.getCustomDocumentsBean();
				break;
		}
		
		((TextView)findViewById(R.id.button_top_save)).setOnClickListener(ok_listener);
		((TextView)findViewById(R.id.button_top_cancel)).setOnClickListener(cancel_listener);
		((TextView)findViewById(R.id.button_left)).setOnClickListener(left_button_listener);
		((TextView)findViewById(R.id.button_right)).setOnClickListener(right_button_listener);
//		((ImageView.findViewById(R.id.button_add_kategorie)).setOnClickListener(add_kategorie_listener);
		((TextView)findViewById(R.id.button_time_plus)).setOnClickListener(time_plus_listener);
		((TextView)findViewById(R.id.button_time_plus)).setOnLongClickListener(time_plus_longClick_listener);
		((TextView)findViewById(R.id.button_time_minus)).setOnClickListener(time_minus_listener);
		
		fillInActualEntryData();
	}

	/**
	 * Get the Rezept Object from the entries list and fill the layout with it's data
	 */
	private void fillInActualEntryData() {
		// set the counter on top
		((TextView) findViewById(R.id.num_display)).setText(actualEntry+1 + "/" + entries.size());
		
		//allgemeine daten
		setStorageIndicator(this.entries.get(actualEntry).isStored());
		((EditText) findViewById(R.id.input_rezept_name)).setText(this.entries.get(this.actualEntry).getName());
		((TextView) findViewById(R.id.rezept_document_path)).setText(this.entries.get(this.actualEntry).getDocumentPath());
		
		// Zubereitungsart
		if(this.entries.get(this.actualEntry).getZubereitungsart() == null || this.entries.get(this.actualEntry).getZubereitungsart().length() < 1){
			tryToPredictZubereitungsart();
		}else{
			((TextView) findViewById(R.id.input_zubereitung)).setText(this.entries.get(this.actualEntry).getZubereitungsart());
		}
		
		// Zeit
		((TextView) findViewById(R.id.input_time)).setText(String.valueOf(this.entries.get(this.actualEntry).getZeit()));
	
		// Zutaten
		if(this.entries.get(this.actualEntry).getZutaten() == null || this.entries.get(this.actualEntry).getZutaten().size() < 1){
			tryToPredictZutaten();
		}else{
			List<String> zutaten = this.entries.get(this.actualEntry).getZutaten();
			StringBuilder zutatenBuilder = new StringBuilder();
			for (String zutat : zutaten) {
				zutatenBuilder.append(zutat + ",");
			}
			((TextView) findViewById(R.id.input_zutaten)).setText(zutatenBuilder.toString());
		}
		
		// Kategorien
		if(this.entries.get(this.actualEntry).getKategorien() == null || this.entries.get(this.actualEntry).getKategorien().size() < 1){
			tryToPredictKategorie();
		}else{
			((TextView) findViewById(R.id.input_kategorie)).setText(this.entries.get(this.actualEntry).getKategorien().get(0));
		}
	}
	
	private void tryToPredictKategorie() {
		((TextView) findViewById(R.id.input_kategorie)).setText("");
	}

	private void tryToPredictZutaten() {
		((TextView) findViewById(R.id.input_zutaten)).setText("");		
	}

	private void tryToPredictZubereitungsart() {
		((TextView) findViewById(R.id.input_zubereitung)).setText("");
	}
	
	private OnLongClickListener time_plus_longClick_listener = new OnLongClickListener() {

		@Override
		public boolean onLongClick(View arg0) {
			return true;
		}
	};
	
	/**
	 * stores all changes made to the Objects to the Database
	 */
	private void saveToDatabase() {
		saveGuiToObject();
		if(this.entries.get(actualEntry).saveToDB(manager.getWritableDatabase())){
			Toast.makeText(getApplicationContext(), "The Object saved successfully", Toast.LENGTH_SHORT).show();
			setStorageIndicator(true);
			entries.get(actualEntry).setStored(true);
		}else{
			Toast.makeText(getApplicationContext(), R.string.error_save_to_db, Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * gets all texts and so from the gui entries and saves it to the corresponding object
	 */
	private void saveGuiToObject(){
		Rezept rezept = this.entries.get(this.actualEntry);
		rezept.setName(((TextView) findViewById(R.id.input_rezept_name)).getText().toString());
		rezept.setZeit(Integer.parseInt(((TextView) findViewById(R.id.input_time)).getText().toString()));
		rezept.setZubereitungsart(((TextView) findViewById(R.id.input_zubereitung)).getText().toString());
		
		List<String> kategorienStrings = new LinkedList<String>();
		kategorienStrings.add(((TextView) findViewById(R.id.input_kategorie)).getText().toString());		
		rezept.setKategorien(kategorienStrings);
		
		rezept.setZutaten(((TextView) findViewById(R.id.input_zutaten)).getText().toString());
	}
	
	private void setStorageIndicator(boolean status) {
		if(status) {
			findViewById(R.id.line).setBackgroundColor(Color.GREEN);
		} else {
			findViewById(R.id.line).setBackgroundColor(Color.DKGRAY);
		}
	}
	
	
	/*
	 *  ---------------------------------------------------------------------------------------
	 *  
	 *  Listeners
	 *  
	 *  ---------------------------------------------------------------------------------------
	 */
	
	/**
	 * Listens for the plus icon to increment the time field
	 */
	private OnClickListener time_plus_listener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			EditText timeField = ((EditText)findViewById(R.id.input_time));
			timeField.setText(String.valueOf(Integer.parseInt(timeField.getText().toString())+1));
		}
	};
	
	/**
	 * Listens for the minus icon to decrement the time field
	 */
	private OnClickListener time_minus_listener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			EditText timeField = ((EditText)findViewById(R.id.input_time));
			int newTime = Integer.parseInt(timeField.getText().toString())-1;
			if(newTime < 0){
				newTime = 0;
			}
			timeField.setText(String.valueOf(newTime));
			
		}
	};
	
	/**
	 * ATM unused
	 */
	private OnClickListener add_kategorie_listener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
//			findViewById(R.id.button_add_kategorie).setVisibility(View.GONE);
//			LinearLayout kategorien_parent = ((LinearLayout) findViewById(R.id.kategorien_layout));
//			View newkategorie = inflate(getActivity(), R.layout.kategorie_input_template, kategorien_parent);
//			((ImageView) findViewById(R.id.button_add_kategorie)).setOnClickListener(add_kategorie_listener);
//			kategorien.add(findViewById(R.id.input_kategorie));
//			kategorien.add(newkategorie);
		}
	};
	
	/**
	 * Listens for the cancel Button and closes this Activity without saving
	 */
	private OnClickListener cancel_listener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {

		}
	};
	
	private OnClickListener ok_listener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			saveToDatabase();
		}
	};
	
	private OnClickListener left_button_listener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			saveGuiToObject();
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
			saveGuiToObject();
			if(actualEntry == entries.size()-1){
				actualEntry = 0;
			}else{
				actualEntry++;
			}
			fillInActualEntryData();
		}
	};

}
