package de.reneruck.connisRezepteApp;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.ListUtils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

public class FileScanner extends AsyncTask<String, Integer, List<String>>{

	private String[] documentsOnStorage;
	private String[] documentsInDatabase;
	private List<String> diff;
	@Override
	protected List<String> doInBackground(String... arg0) {

		DBManager manager = new DBManager(Main.getContext(), Configurations.databaseName, null, Configurations.databaseVersion);
		final SQLiteDatabase db = manager.getReadableDatabase();
				
		File rezepteDictionary = new File(Configurations.rezepteDirPath);
		
		if(rezepteDictionary.exists() && rezepteDictionary.isDirectory()){
			this.documentsOnStorage = rezepteDictionary.list();
			
			Cursor documentsCursor = db.rawQuery("select " + Configurations.rezept_Document + " from " + Configurations.table_Rezepte, null);
			
			if(documentsCursor.getCount() > 0){
				this.documentsInDatabase = new String[documentsCursor.getCount()];
				int count = 0;
				do {
					int docIndex = documentsCursor.getColumnIndex(Configurations.rezept_Document);
					this.documentsInDatabase[count] = documentsCursor.getString(docIndex);
					count ++;
				} while (documentsCursor.moveToNext());
				
				// hinzufügen = inDB - onDisc
				this.diff = ListUtils.subtract(Arrays.asList(this.documentsInDatabase), Arrays.asList(this.documentsOnStorage));
				
			}else{
				return Arrays.asList(this.documentsOnStorage);
			}
			
		}else{
			Log.e("FileScanner", "Rezepte Path: "+ Configurations.rezepteDirPath + "is no Directory or was not found");
			return null;
		}
		return this.diff;
	}
	
	

}
