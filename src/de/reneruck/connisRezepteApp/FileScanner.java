package de.reneruck.connisRezepteApp;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.ListUtils;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

public class FileScanner extends AsyncTask<String, Integer, List<String>>{

	private List<String> documentsOnStorage;
	private List<String> documentsInDatabase = new LinkedList<String>();
	private List<String> diff;
	private NewDocumentsBean newDocumentBean;
	
	public FileScanner(NewDocumentsBean newDocumentBean) {
		this.newDocumentBean = newDocumentBean;
	}
	
	@Override
	protected List<String> doInBackground(String... arg0) {

		DBManager manager = new DBManager(Main.getContext(), Configurations.databaseName, null, Configurations.databaseVersion);
		final SQLiteDatabase db = manager.getReadableDatabase();
				
		File rezepteDictionary = new File(Configurations.rezepteDirPath);
		
		// check if the directory is existent
		// without it, there woun't be any rezepte
		if(rezepteDictionary.exists() && rezepteDictionary.isDirectory()){
			
			this.documentsOnStorage = Arrays.asList(rezepteDictionary.list());
			
			try {
				Cursor documentsCursor = db.query(Configurations.table_Rezepte, new String[]{Configurations.rezept_Name}, null, null, null, null, null);
				
				// there is something in the saved in the database
				if(documentsCursor.getCount() > 0){
					
					// copy all entries from the cursor to a compareable list
					for(documentsCursor.moveToFirst(); documentsCursor.moveToNext(); documentsCursor.isAfterLast()) {
						this.documentsInDatabase.add(documentsCursor.getString(0));
					}
					
					// hinzufügen = onDisc - inDB
					this.diff = ListUtils.subtract(this.documentsOnStorage, this.documentsInDatabase);
					this.newDocumentBean.putAllEntries(diff);
					
				}else{
					this.newDocumentBean.putAllEntries(this.documentsOnStorage);
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}else{
			Log.e("FileScanner", "Rezepte Path: "+ Configurations.rezepteDirPath + "is no Directory or was not found");
		}
		return this.diff;
	}
	
	

}
