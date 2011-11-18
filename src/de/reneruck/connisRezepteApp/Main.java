package de.reneruck.connisRezepteApp;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends Activity {

	private static Context context;
	private List<String> rezepteList;
	private static NewDocumentsBean newDocumentsBean;
	private DBManager manager;

	public static Context getContext() {
		return context;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.context = getApplicationContext();

		setContentView(R.layout.main);
		this.manager = new DBManager(getApplicationContext(), Configurations.databaseName, null, Configurations.databaseVersion);
		this.newDocumentsBean = new NewDocumentsBean();
		this.newDocumentsBean.addPropertyChangeListener(newDocumentsPropertyChangeListener);
		new FileScanner(this.newDocumentsBean).doInBackground();

		buildDocumentsList(this.manager.getReadableDatabase());
	}

	private void buildDocumentsList(SQLiteDatabase db) {

		this.rezepteList = new LinkedList<String>();
		try {
			Cursor c = db.query(Configurations.table_Rezepte, new String[] { "*" }, null, null, null, null, null);
			// Cursor c = db.rawQuery("select * from "
			// +Configurations.table_Rezepte+ "", null);

			if (c.getCount() == 0) {
				this.rezepteList.add("Keine Rezepte gefunden");
			} else {
				c.moveToFirst();
				do {
					int name = c.getColumnIndex(Configurations.rezept_Name);
					int doc = c.getColumnIndex(Configurations.rezept_Document);

					rezepteList.add(c.getString(name) + " - " + c.getString(doc));
				} while (c.moveToNext());
			}

		} catch (SQLException e) {
			e.fillInStackTrace();
		}
		ListView lv = (ListView) findViewById(R.id.listView);
		lv.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.rezeptelistentry, this.rezepteList));
		lv.setOnItemClickListener(this.rezepteListEntyListener);
	}

	/**
	 * All the listeners are implemented here
	 */
	OnItemClickListener rezepteListEntyListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> adapter, View view, int arg2, long arg3) {
			Toast.makeText(getApplicationContext(), ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
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
				}else{
					((TextView) findViewById(R.id.newDocsText)).setText(1);
					((TextView) findViewById(R.id.newDocsText)).setTextColor(Color.RED);
					((TextView) findViewById(R.id.newDocsText)).setOnClickListener(newDocumentsListener);
				}
				
			}

		}
	};

	OnClickListener newDocumentsListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			SQLiteDatabase db = manager.getWritableDatabase();
			List<String> liste = newDocumentsBean.getNeueDokumente();
			ContentValues values = new ContentValues(liste.size());
			for (String string : liste) {
				values.put(Configurations.rezept_Name, string);
				values.put(Configurations.rezept_Document, "/sdcard/Rezepte/" + string);
				long result = db.insert(Configurations.table_Rezepte, null, values);
				if(result > -1){
					TextView newDocsIndicator = (TextView) findViewById(R.id.newDocsText);
					int countBevore = Integer.parseInt((String) newDocsIndicator.getText());
					newDocsIndicator.setText(String.valueOf(countBevore-1));
					newDocsIndicator.setTextColor(Color.WHITE);
				}
			}
			newDocumentsBean.clearList();
			buildDocumentsList(manager.getReadableDatabase());
			((TextView) findViewById(R.id.newDocsText)).setOnClickListener(null);
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
        		buildDocumentsList(manager.getReadableDatabase());
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