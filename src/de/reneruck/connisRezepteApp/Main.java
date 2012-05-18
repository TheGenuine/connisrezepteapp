package de.reneruck.connisRezepteApp;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
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
import de.reneruck.connisRezepteApp.development.DatabaseOverview;
import de.reneruck.connisRezepteApp.fragments.AllDocuments;
import de.reneruck.connisRezepteApp.fragments.QueryList;
/**
 * 
 * @author Rene Ruck
 *
 */
public class Main extends Activity implements TabListener{

	private static final String TAG = "RezepteApp-Main";
	protected static final int DOCUMENT_EDIT = 0;
	private static final String FRAGMENT_TAG_DIALOG = "dialog";
	private Menu menu;
	private AppContext context;
	private Fragment fragment;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "------------- onStart --------------");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		this.context = (AppContext) getApplicationContext();

		ActionBar actionBar = getActionBar();
	    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	    actionBar.setDisplayShowTitleEnabled(false);

	    actionBar.addTab(makeNewTab(actionBar, getResources().getString(R.string.tab_all_documents), AllDocuments.class));
	    actionBar.addTab(makeNewTab(actionBar, getResources().getString(R.string.tab_search), SearchBuilderActivity.class));
	    actionBar.addTab(makeNewTab(actionBar, getResources().getString(R.string.tab_last_search), SearchBuilderActivity.class));
	}
	

	
	private Tab makeNewTab(ActionBar actionBar, String text, Class<?> fClass) {
		return actionBar.newTab()
				.setTag(fClass)
				.setText(text)
				.setTabListener(this);
	}



	/* Creates the menu items */
    public boolean onCreateOptionsMenu(Menu menu) {
    	Log.d(TAG, "------------- onCreateMenue --------------");
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.main_actionbar, menu);
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



	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		Class<?> tClass = (Class<?>) tab.getTag();
		View findViewById = findViewById(R.id.main_fragment_container);
		if(this.fragment == null) {
			this.fragment = Fragment.instantiate(getApplicationContext(), tClass.getName());
			ft.add(R.id.main_fragment_container, this.fragment, tClass.getName());
		} else if(!tClass.getName().equals((String) this.fragment.getTag())) {
			this.fragment = Fragment.instantiate(getApplicationContext(), tClass.getName());
			ft.replace(R.id.main_fragment_container, this.fragment, tClass.getName());
		}
	}



	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		if(this.fragment != null) {
			ft.remove(fragment);
		}
	}
}