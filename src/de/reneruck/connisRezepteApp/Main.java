package de.reneruck.connisRezepteApp;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends Activity {

	private static Context context;
	private static NewDocumentsBean newDocumentsBean;
	private DBManager manager;

	public static Context getContext() {
		return context;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Main.context = getApplicationContext();

		setContentView(R.layout.main);
		this.manager = new DBManager(getApplicationContext(), Configurations.databaseName, null, Configurations.databaseVersion);
		Main.newDocumentsBean = new NewDocumentsBean();
		Main.newDocumentsBean.addPropertyChangeListener(newDocumentsPropertyChangeListener);
		
		// initialize and start the background file scanner
		FileScanner filescanner = new FileScanner(Main.newDocumentsBean);
		filescanner.setRunnig(true);
		filescanner.doInBackground("");
		
		setupSearchBar();
		
//		searchView.setOnFocusChangeListener(searchFocusListener);
		buildDocumentsList();
	}

	/**
	 * Initializes the Top search bar with its autocomplete input
	 */
	private void setupSearchBar() {

		TextView.OnEditorActionListener returnButtonListener = new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_NULL) {
					Toast.makeText(getApplicationContext(), "Retrun pressed, now we can search", Toast.LENGTH_SHORT).show();
				}
				return true;
			}
		};
		
		//setup the search view
		final AutoCompleteTextView searchView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);
		ArrayAdapter<String> autoCompleteList = new ArrayAdapter<String>(getApplicationContext(), R.layout.autocomplete_list_item);
		
		SQLiteDatabase db = manager.getReadableDatabase();
		Cursor c = db.query(Configurations.table_Rezepte, new String[] { Configurations.rezepte_Name }, null, null, null, null, null);
		
		//get all entries from the database
		for (c.moveToFirst(); c.moveToNext(); c.isAfterLast()) {
			autoCompleteList.add(c.getString(0));
		}
		searchView.setAdapter(autoCompleteList);
		searchView.setOnEditorActionListener(returnButtonListener);
		
//		OnFocusChangeListener searchFocusListener = new OnFocusChangeListener() {
//
//			@Override
//			public void onFocusChange(View v, boolean hasFocus) {
//				if (hasFocus) {
//					ArrayAdapter<String> autoCompleteList = new ArrayAdapter<String>(getApplicationContext(), R.layout.autocomplete_list_item);
//					SQLiteDatabase db = manager.getReadableDatabase();
//					Cursor c = db.query(Configurations.table_Rezepte, new String[] { Configurations.rezept_Name }, null, null, null, null, null);
//					for (c.moveToFirst(); c.moveToNext(); c.isAfterLast()) {
//						autoCompleteList.add(c.getString(0));
//					}
//					searchView.setAdapter(autoCompleteList);
//				}
//			}
//		};

	}
	
	/**
	 * Builds an initial list of all Documents in the center list view
	 * @param db
	 */
	private void buildDocumentsList() {
		String query = SQLiteQueryBuilder.buildQueryString(true, Configurations.table_Rezepte, new String[]{"*"}, null, null, null, Configurations.rezepte_Name, null);
		
		Map<String, Object> parameter = new HashMap<String, Object>();
		parameter.put("listView", (ListView) findViewById(R.id.listView));
		parameter.put("dbManager", this.manager);
		parameter.put("query", query);
		
		new QueryDocumentList().doInBackground(parameter);
	}

	/**
	 * All the listeners are implemented here
	 */
	OnItemClickListener rezepteListEntyListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> adapter, View view, int arg2, long arg3) {
			
			try {
				Intent intent = new Intent();
				intent.setAction(android.content.Intent.ACTION_VIEW);
				File file = new File(Configurations.dirPath + ((TextView) view.findViewById(R.id.toptext)).getText());
				String mimeType = file.toURL().openConnection().getContentType();
				intent.setDataAndType(Uri.fromFile(file), mimeType );
				startActivity(intent); 
			} catch (ActivityNotFoundException e){
				Toast.makeText(getApplicationContext(), "Kein Programm zum Öffnen von " + ((TextView) view.findViewById(R.id.toptext)).getText() + " gefunden!", Toast.LENGTH_LONG).show();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	/**
	 * Listens on changes of the
	 */
	PropertyChangeListener newDocumentsPropertyChangeListener = new PropertyChangeListener() {

		@Override
		public void propertyChange(PropertyChangeEvent event) {
			Object newValue =  event.getNewValue();
			if(newValue instanceof Collection){
				List<String> newDocuments = (List<String>) newValue;
				
				if (newDocuments != null && !newDocuments.isEmpty()) {
					TextView newDocsIndicator = (TextView) findViewById(R.id.newDocsText);
					newDocsIndicator.setText(String.valueOf(newDocuments.size()));
					newDocsIndicator.setTextColor(Color.RED);
					newDocsIndicator.setOnClickListener(newDocumentsListener);
				}
				
			}else{
				((TextView) findViewById(R.id.newDocsText)).setText("1");
				((TextView) findViewById(R.id.newDocsText)).setTextColor(Color.RED);
				((TextView) findViewById(R.id.newDocsText)).setOnClickListener(newDocumentsListener);
			}
			buildDocumentsList();
		}
	};

	/**
	 * Listener to react on clicks on the new Documents indicator
	 */
	OnClickListener newDocumentsListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			SQLiteDatabase db = manager.getWritableDatabase();
			List<File> liste = newDocumentsBean.getNeueDokumente();
			
			 DialogFragment newFragment = new DokumentEditDialog(newDocumentsBean, manager);
			 showDialog(newFragment);
			
//			newDocumentsBean.clearList();
//			buildDocumentsList(manager.getReadableDatabase());
//			((TextView) findViewById(R.id.newDocsText)).setOnClickListener(null);
		}
	};

	/* Creates the menu items */
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(0,  1, 0, "Clear Database").setIcon(R.drawable.settings);
//    	menu.add(0,  2, 0, "Übersicht").setIcon(R.drawable.about);
        menu.add(0,  3, 0, "Exit").setIcon(R.drawable.exit);
        return true;
    }

    /* 
     * Menü Items
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        
    	switch(item.getItemId()){
    	
        case 1:
        	try {
        		SQLiteDatabase db = manager.getWritableDatabase();
        		db.delete(Configurations.table_Rezepte, null, null);
        		db.close();
        		buildDocumentsList();
			} catch (SQLException e) {
					e.printStackTrace();
			}
        	break;
        case 2:
        	break;
        case 3:
        	this.finish();
        	break;
        default: return false;	
    	}
       return true;
     }
    
    void showDialog(DialogFragment newFragment) {

        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        newFragment.show(ft, "dialog");
    }
    
    @Override
    protected void onPause() {
		this.manager.close();
    	super.onPause();
    }
    
	protected void onDestroy() {
		this.manager.close();
		super.onDestroy();
	};
}