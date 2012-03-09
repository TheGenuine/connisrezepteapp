package de.reneruck.connisRezepteApp;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import de.reneruck.connisRezepteApp.DB.DatabaseStorageCallback;

public class DocumentEdit extends Activity{

	
	private static final String TAG = "Document Edit";
	private int actualEntry = 0;
	private List<Rezept> entries = new LinkedList<Rezept>();
	private AppContext appContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "------------- onStart --------------");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_rezept_edit_view);
		setUpActionbar();
		
		this.appContext = (AppContext)getApplicationContext();
		
		switch(getIntent().getIntExtra(Configurations.LIST_SOURCE, Configurations.NEW_DOCUMENTS)){
			case  Configurations.NEW_DOCUMENTS:
				List<File> neueDokumente = appContext.getDocumentsBean().getNeueDokumente();
				if(neueDokumente.size() > 0){
					for (File file : neueDokumente) {
						this.entries.add(new Rezept(file));
					}
				} else {
					showDialog(Configurations.DIALOG_NO_NEW_DOCUMENTS);
				}
				break;
			case Configurations.CUSTOM_LIST:
				this.entries = appContext.getDocumentsBean().getCustomDocumentsList();
				break;
		}
		
		((TextView)findViewById(R.id.button_left)).setOnClickListener(left_button_listener);
		((TextView)findViewById(R.id.button_right)).setOnClickListener(right_button_listener);
//		((ImageView.findViewById(R.id.button_add_kategorie)).setOnClickListener(add_kategorie_listener);
		((TextView)findViewById(R.id.button_time_plus)).setOnClickListener(time_plus_listener);
		((TextView)findViewById(R.id.button_time_plus)).setOnLongClickListener(time_plus_longClick_listener);
		((TextView)findViewById(R.id.button_time_minus)).setOnClickListener(time_minus_listener);
		
		setKategorienAutocomplete();
		setZubereitungAutocomplete();
		fillInActualEntryData();
	}

	private void setZubereitungAutocomplete() {
//		Object[] query = this.appContext.getDatabaseManager().getAllZubereitungen().toArray();
//		String[] entries = Arrays.copyOf(query, query.length, String[].class);
//		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, entries);
//		((AutoCompleteTextView)findViewById(R.id.input_zubereitung)).setAdapter(adapter);		
	}

	private void setKategorienAutocomplete() {
//		Object[] query = this.appContext.getDatabaseManager().getAllKategorien().toArray();
//		String[] entries = Arrays.copyOf(query, query.length, String[].class);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, entries);
//        ((AutoCompleteTextView)findViewById(R.id.input_kategorie)).setAdapter(adapter);		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.new_document_actionbar, menu);
	    return true;
	}
	
	private void setUpActionbar() {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            Intent intent = new Intent(this, Main.class);
	            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(intent);
	            return true;
	        case R.id.menu_save:
				saveToDatabase();
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
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
			if(!zutaten.isEmpty()){
				for (String zutat : zutaten) {
					zutatenBuilder.append(zutat + ",");
				}
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
		this.appContext.getDatabaseManager().storeRezept(this.entries.get(actualEntry), new DatabaseStorageCallback() {
			
			@Override
			public void onStoreCallback(boolean result) {
				dismissDialog(Configurations.DIALOG_WAITING_FOR_QUERY);
				if(result) {
					Toast.makeText(getApplicationContext(), R.string.save_successfull, Toast.LENGTH_SHORT).show();
					setStorageIndicator(true);
					entries.get(actualEntry).setStored(true);
				} else {
					Toast.makeText(getApplicationContext(), R.string.error_save_to_db, Toast.LENGTH_SHORT).show();
				}
			}
			
		});
		showDialog(Configurations.DIALOG_WAITING_FOR_QUERY);
	}
	
	/**
	 * gets all texts and inputs from the gui entries and saves it to the corresponding object
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
	
	/**
	 * Colors the Storage Indicator Bar Green or Gray depending of the input
	 * 
	 * @param status - true -> the status bar gets green <br> false -> the status bar gets gray
	 */
	private void setStorageIndicator(boolean status) {
		if(status) {
			findViewById(R.id.line).setBackgroundColor(Color.GREEN);
		} else {
			findViewById(R.id.line).setBackgroundColor(Color.DKGRAY);
		}
	}
	
	/**
	 * Removes all stored Entries from the newDocumentsBean
	 */
	private void removeSavedDocumentes() {
		for (Rezept entry : this.entries) {
			if(entry.isStored()){
				this.appContext.getDocumentsBean().removeEntry(entry.getId());
			}
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		Log.d(TAG, "------------- onCreateDialog --------------");
		switch (id) {
			case Configurations.DIALOG_WAITING_FOR_QUERY:
				ProgressDialog dialog  = ProgressDialog.show(DocumentEdit.this, "Bitte warten", "speichere Dokument");
				return dialog;
			default:
				break;
		}
		
		return super.onCreateDialog(id);
	} 
	
	@Override
	protected void onResume() {
		Log.d(TAG, "------------- onResume --------------");
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		Log.d(TAG, "------------- onPause --------------");
		removeSavedDocumentes();
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		Log.d(TAG, "------------- onStop --------------");
		super.onStop();
	}
	

	@Override
	protected void onDestroy() {
		Log.d(TAG, "------------- onDestroy --------------");
		super.onDestroy();
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
