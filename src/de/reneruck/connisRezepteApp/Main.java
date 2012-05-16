package de.reneruck.connisRezepteApp;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.List;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import de.reneruck.connisRezepteApp.DB.DatabaseQueryCallback;
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
	private static final String FRAGMENT_TAG_DIALOG = "dialog";
	private Menu menu;
	private AppContext context;
	private Bundle savedInstanceState;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "------------- onStart --------------");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		this.savedInstanceState = savedInstanceState;
		this.context = (AppContext) getApplicationContext();
		
		DocumentsBean documentsBean = new DocumentsBean();
		documentsBean.addPropertyChangeListener(this.newDocumentsPropertyChangeListener);
		
		this.context.setDocumentsBean(documentsBean);
		
		buildAllDocumentsList();
		if(this.context.getActualInfoItem() != 0){
			addFragment(this.context.getActualInfoItem());
		}
	}
	
	/**
	 * Builds an initial list of all Documents in the center list view
	 * @param db
	 */
	private void buildAllDocumentsList() {
		this.context.getDatabaseManager().getAllDocuments(new DatabaseQueryCallback() {			
			@Override
			public void onsSelectCallback(List<?> result) {
				List<Rezept> rezepte = (List<Rezept>) result;
				if(rezepte.size() == 0) {
					String string = getResources().getString(R.string.keine_rezepte_gefunden);
					rezepte.add(new Rezept(string));
				}
				DialogFragment dialog = (DialogFragment) getFragmentManager().findFragmentByTag(FRAGMENT_TAG_DIALOG);
				if(dialog != null){
					dialog.dismiss();
				}
				((ListView) findViewById(R.id.listView)).setAdapter(new RezepteListAdapter(rezepte));
				((ListView) findViewById(R.id.listView)).setOnItemClickListener(rezepteListEntyListener);
			}
		});
		showDialog();
	}
	
	/**
	 * All the listeners are implemented here
	 */
	OnItemClickListener rezepteListEntyListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> adapter, View view, int arg2, long arg3) {
			int documentId = (Integer) view.getTag();
			
			if(documentId > -1) {
				//Check what fragment is currently shown, replace if needed.
				if(((LinearLayout)findViewById(R.id.fragment_container)).getChildCount() > 0) {
					Fragment fragment = getFragmentManager().findFragmentByTag(String.valueOf(documentId));
					if(fragment == null) { // No Fragment with the actual documentId found 
						replaceDocumentInfoFragment(documentId);
					} else if(context.getActualInfoItem() != documentId) {
						replaceDocumentInfoFragment(fragment);
					}
				} else { // if no DocumentInfo Fragment has been set, add it initialy
					addFragment(documentId);
				}
			}
		    
		}

	};
	
	private void addFragment(int documentId) {
		DocumentInfo documentInfo = new DocumentInfo();
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.add(R.id.fragment_container, documentInfo, String.valueOf(documentId));
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		transaction.commit();
		this.context.setActualInfoItem(documentId);
	}
	
	private void replaceDocumentInfoFragment(int documentId){
		((ViewGroup)findViewById(R.id.fragment_container)).removeAllViews();
		DocumentInfo documentInfo = new DocumentInfo();
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
	    transaction.replace(R.id.fragment_container, documentInfo, String.valueOf(documentId));
	    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
	    transaction.commit();
	    this.context.setActualInfoItem(documentId);
	}	
	private void replaceDocumentInfoFragment(Fragment fragment){
		((ViewGroup)findViewById(R.id.fragment_container)).removeAllViews();
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, fragment, fragment.getTag());
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		transaction.commit();
		this.context.setActualInfoItem(Integer.getInteger(fragment.getTag()));
	}	
	
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
		FileScanner filescanner = new FileScanner(this.context.getDocumentsBean(), this.context.getDatabaseManager());
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
	    newFragment.show(getFragmentManager(), FRAGMENT_TAG_DIALOG);
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
//		Utils.copyFile(new File("/data/data/de.reneruck.connisRezepteApp/databases/rezepte.db"), 
//				new File("/sdcard/ConnisRezepteApp/rezepte-"+System.currentTimeMillis()+".db.backup"));

		super.onDestroy();
	}
}