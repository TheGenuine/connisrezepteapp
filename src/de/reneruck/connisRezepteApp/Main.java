package de.reneruck.connisRezepteApp;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import de.reneruck.connisRezepteApp.DB.DBManager;
import de.reneruck.connisRezepteApp.DB.DatabaseAbstraction;
import de.reneruck.connisRezepteApp.development.DatabaseOverview;
import de.reneruck.connisRezepteApp.fragments.DocumentInfo;
/**
 * 
 * @author Rene Ruck
 *
 */
public class Main extends Activity {

	private static final String TAG = "RezepteApp-Main";
	protected static final int DOCUMENT_EDIT = 0;
	private Menu menu;
	private AppContext context;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "------------- onStart --------------");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		this.context = (AppContext) getApplicationContext();
		
		DBManager manager = new DBManager(getApplicationContext(), Configurations.databaseName, null, Configurations.databaseVersion);
		DocumentsBean documentsBean = new DocumentsBean();
		documentsBean.addPropertyChangeListener(this.newDocumentsPropertyChangeListener);
		
		DatabaseAbstraction dal = new DatabaseAbstraction(manager);
		
		this.context.setManager(manager);
		this.context.setDocumentsBean(documentsBean);
		this.context.setDatabaseAbstraction(dal);
		
		buildAllDocumentsList();
	}
	
	/**
	 * Builds an initial list of all Documents in the center list view
	 * @param db
	 */
	private void buildAllDocumentsList() {
		this.context.getDatabaseAbstraction().getAllDocuments(new DatabaseCallback() {			
			@Override
			public void onsSelectCallback(List<?> result) {
				if(result.get(0) instanceof Rezept){
					dismissDialog(Configurations.DIALOG_WAITING_FOR_QUERY);
					((ListView) findViewById(R.id.listView)).setAdapter(new RezepteListAdapter(getApplicationContext(),(List<Rezept>)result));
					((ListView) findViewById(R.id.listView)).setOnItemClickListener(rezepteListEntyListener);
				}
			}

			@Override
			public void onStoreCallback(boolean result) {}
		});
		showDialog();
	}
	
	
	/**
	 * All the listeners are implemented here
	 */
	OnItemClickListener rezepteListEntyListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> adapter, View view, int arg2, long arg3) {
			
			 // Check what fragment is currently shown, replace if needed.
            DocumentInfo documentInfo = (DocumentInfo) getFragmentManager().findFragmentById(R.id.document_preview_fragment);
            if (documentInfo != null) {
            	int documentId = (Integer) view.getTag();
            	Rezept document = ((AppContext) getApplicationContext()).getDatabaseAbstraction().getDocument(documentId);

            	View fragment_container = findViewById(R.id.fragment_container);
            	if( fragment_container != null && fragment_container.getVisibility() == View.GONE) {
            		fragment_container.setVisibility(View.VISIBLE);
            	}
            	
                documentInfo = DocumentInfo.newInstance(document);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.document_preview_fragment, documentInfo);
                ft.addToBackStack(null);
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
				if(newValue instanceof List<?>){
					updateNewDocumentsIndicator(((List<File>) newValue).size());
				}
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
				//FIXME crashed wenn display gedreht wird (nullpointer)
				if(menu != null) {
					MenuItem item = menu.findItem(R.id.menu_updated_documents);
					if(count == 0) {
						item.setVisible(false);
					} else {
						item.setVisible(true);
						item.setTitle(count + " neue Dokumente");
					}
				} else {
					Log.d(TAG, "Menu was null: " + menu);
				}
			}
		});
	}
	
	/* Creates the menu items */
    public boolean onCreateOptionsMenu(Menu menu) {
    	Log.d(TAG, "------------- onCreateMenue --------------");
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.main_actionbar, menu);
	    SearchView searchView = (SearchView) menu.findItem(R.id.menu_search_action).getActionView();
	    this.menu = menu;
	    startFileScanner();
        return true;
    }

    /**
     * Initializes and starts the background Filescanner
     * ATM ONLY ONE TIME SCAN, LATER BACKGROUND SERVICE AND ALWASY SCANNING
     */
    private void startFileScanner() {
		// initialize and start the background file scanner
		FileScanner filescanner = new FileScanner(((AppContext)getApplicationContext()).getDocumentsBean(), ((AppContext)getApplicationContext()).getDBManager());
		filescanner.setRunnig(true);
		filescanner.execute("");		
	}

	/* 
     * Menü Items
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        
    	switch(item.getItemId()){
    	
        case R.id.menu_debug_showdb:
			Intent dbintent = new Intent(getApplicationContext(), DatabaseOverview.class);
			startActivity(dbintent);
        	break;
        case  R.id.menu_exit:
        	this.finish();
        	break;
        case R.id.menu_updated_documents:
			Intent i = new Intent(getApplicationContext(), DocumentEdit.class);
			i.putExtra(Configurations.LIST_SOURCE, Configurations.NEW_DOCUMENTS);
			startActivityIfNeeded(i, DOCUMENT_EDIT);
        	break;
        case R.id.menu_search_action:
        	break;
        case android.R.id.home:
            // app icon in action bar clicked; go home
            Intent intent = new Intent(this, Main.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        default:
            return super.onOptionsItemSelected(item);
    	}
		return true;
     }
    
	void showDialog() {
		DialogFragment newFragment = WaitingDialog.newInstance(R.string.inprogress);
	    newFragment.show(getFragmentManager(), "dialog");
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
    @Override
    protected void onResume() {
    	Log.d(TAG, "------------- onResume --------------");
    	super.onResume();
    }
    
    @Override
    protected void onStop() {
    	Log.d(TAG, "------------- onStop --------------");
    	super.onStop();
    }
    
    @Override
    protected void onPause() {
		Log.d(TAG, "------------- onPause --------------");
    	super.onPause();
    }
    
	protected void onDestroy() {
		Log.d(TAG, "------------- onDestroy --------------");
		Utils.copyFile(new File("/data/data/de.reneruck.connisRezepteApp/databases/rezepte.db"), 
				new File("/sdcard/ConnisRezepteApp/rezepte-"+System.currentTimeMillis()+".db.backup"));

		super.onDestroy();
	}
}