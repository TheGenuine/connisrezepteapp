package de.reneruck.connisRezepteApp;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import de.reneruck.connisRezepteApp.development.DatabaseOverview;
import de.reneruck.connisRezepteApp.fragments.DocumentInfo;
import de.reneruck.connisRezepteApp.fragments.DokumentEditDialog;
/**
 * 
 * @author Rene Ruck
 *
 */
public class Main extends Activity {

	private static final String TAG = "RezepteApp-Main";
	protected static final int DOCUMENT_EDIT = 0;
	private static Context context;
	private DBManager manager;
	private Menu menu;

	public static Context getContext() {
		return context;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Main.context = getApplicationContext();

		setContentView(R.layout.main);
		this.manager = new DBManager(getApplicationContext(), Configurations.databaseName, null, Configurations.databaseVersion);
		NewDocumentsBean newDocumentsBean = new NewDocumentsBean();
		newDocumentsBean.addPropertyChangeListener(newDocumentsPropertyChangeListener);
		
		((AppContext) getApplicationContext()).setManager(this.manager);
		((AppContext) getApplicationContext()).setNewDocumentsBean(newDocumentsBean);
		
		// initialize and start the background file scanner
		FileScanner filescanner = new FileScanner(newDocumentsBean);
		filescanner.setRunnig(true);
		filescanner.execute("");
		
		buildAllDocumentsList();
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
            DocumentInfo documentInfo = (DocumentInfo) getFragmentManager().findFragmentById(R.id.document_preview_fragment);
            if (documentInfo != null) {
            	int documentId = (Integer) view.getTag();
            	Rezept document = ((AppContext) getApplicationContext()).getDocument(documentId);

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
	 * 
	 */
	OnItemLongClickListener rezeptListEntryOptionsListener = new OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			// TODO Auto-generated method stub
			return false;
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
				MenuItem item = menu.findItem(R.id.menu_updated_documents);
				if(count == 0){
					item.setVisible(false);
				}else {
					item.setVisible(true);
					item.setTitle(count + " neue Dokumente");
				}
			}
		});
	}
	
	/* Creates the menu items */
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.main_actionbar, menu);
	    SearchView searchView = (SearchView) menu.findItem(R.id.menu_search_action).getActionView();
	    this.menu = menu;
        return true;
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
			Intent i = new Intent(getApplicationContext(), DocumentEditActivity.class);
			i.putExtra(Configurations.LIST_SOURCE, Configurations.NEW_DOCUMENTS);
			startActivityForResult(i, DOCUMENT_EDIT);
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