package de.reneruck.connisRezepteApp;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import de.reneruck.connisRezepteApp.fragments.DocumentInfo;
import de.reneruck.connisRezepteApp.fragments.DokumentEditDialog;

public class Main extends Activity {

	private static final String TAG = "RezepteApp-Main";
	private static Context context;
	private NewDocumentsBean newDocumentsBean;
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
		newDocumentsBean = new NewDocumentsBean();
		newDocumentsBean.addPropertyChangeListener(newDocumentsPropertyChangeListener);
		
		// initialize and start the background file scanner
		FileScanner filescanner = new FileScanner(newDocumentsBean);
		filescanner.setRunnig(true);
		filescanner.execute("");
		
		setupSearchBar();
		
//		searchView.setOnFocusChangeListener(searchFocusListener);
		buildAllDocumentsList();
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
		
		try {
			Cursor c = db.query(Configurations.table_Rezepte, new String[] { Configurations.rezepte_Name }, null, null, null, null, null);
			
			//get all entries from the database
			for (c.moveToFirst(); c.moveToNext(); c.isAfterLast()) {
				autoCompleteList.add(c.getString(0));
			}
			searchView.setAdapter(autoCompleteList);
			searchView.setOnEditorActionListener(returnButtonListener);
		} catch (SQLException e) {
			Log.e(TAG, e.getLocalizedMessage());
		} finally {
			db.close();
		}
		
		
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
	private void buildAllDocumentsList() {
		String query = SQLiteQueryBuilder.buildQueryString(true, Configurations.table_Rezepte, new String[]{"*"}, null, null, null, Configurations.rezepte_Name, null);
		
		Map<String, Object> parameter = new HashMap<String, Object>();
		parameter.put("listView", (ListView) findViewById(R.id.listView));
		parameter.put("dbManager", this.manager);
		parameter.put("listener", this.rezepteListEntyListener);
		parameter.put("query", query);
		
		new QueryDocumentList().execute(parameter);
	}

	/**
	 * All the listeners are implemented here
	 */
	OnItemClickListener rezepteListEntyListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> adapter, View view, int arg2, long arg3) {
			
			 // Check what fragment is currently shown, replace if needed.
            DocumentInfo documentInfo = (DocumentInfo) getFragmentManager().findFragmentById(R.id.document_preview);
            if (documentInfo == null) {
            	Long documentId = (Long) view.getTag();
            	
            	Rezept rezept = ((AppContext)getApplication()).getDocument(documentId);
            	
            	// Make new fragment to show this selection.
                documentInfo = DocumentInfo.newInstance(rezept);

                // Execute a transaction, replacing any existing fragment
                // with this one inside the frame.
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.document_preview, documentInfo);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
		}
	};

	/**
	 * Listens on changes of the
	 */
	PropertyChangeListener newDocumentsPropertyChangeListener = new PropertyChangeListener() {

		@Override
		public void propertyChange(final PropertyChangeEvent event) {
			if("neueDokumente".equals(event.getPropertyName())){
				Object newValue =  event.getNewValue();
				updateNewDocumentsIndicator(((List<File>) newValue).size());
			}
		}
	};

	/**
	 * Updates the New Documents Indicator in the top right corner with the given count of new Documents<br>
	 * If there are no new Documents, just call it with 0
	 * @param number - the number of new Documents
	 */
	private void updateNewDocumentsIndicator(int number) {
		final int count = number;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(count > 0) {
					TextView newDocsIndicator = (TextView) findViewById(R.id.newDocsText);
					newDocsIndicator.setText(String.valueOf(count));
					newDocsIndicator.setTextColor(Color.RED);
					newDocsIndicator.setBackgroundDrawable(getResources().getDrawable(R.drawable.newdocumentindicator_red_64));
					newDocsIndicator.setOnClickListener(newDocumentsListener);
				} else {
					TextView newDocsIndicator = (TextView) findViewById(R.id.newDocsText);
					newDocsIndicator.setText("");
					newDocsIndicator.setBackgroundDrawable(getResources().getDrawable(R.drawable.newdocumentindicator_gray_64));
					newDocsIndicator.setOnClickListener(null);
				}
			}
		});
	}
	
	/**
	 * Listener to react on clicks on the new Documents indicator
	 */
	OnClickListener newDocumentsListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			 showDialog();
		}
	};

	/* Creates the menu items */
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(0,  1, 0, "Clear Database").setIcon(R.drawable.settings);
        menu.add(0,  2, 0, "Exit").setIcon(R.drawable.exit);
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
        		buildAllDocumentsList();
			} catch (SQLException e) {
					e.printStackTrace();
			}
        	break;
        case 2:
        	this.finish();
        	break;
        default: return false;	
    	}
       return true;
     }
    
    void showDialog() {
    	DokumentEditDialog editFragment = new DokumentEditDialog(this.newDocumentsBean, this.manager, this.editDialogDismissListener);
    	editFragment.show(getFragmentManager(), "editDocumentDialog");
    }
    
    OnDismissListener editDialogDismissListener = new OnDismissListener() {
		
		@Override
		public void onDismiss(DialogInterface dialog) {
			updateNewDocumentsIndicator(newDocumentsBean.getNewDocumentsCount());
			buildAllDocumentsList();
		}
	};
    
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