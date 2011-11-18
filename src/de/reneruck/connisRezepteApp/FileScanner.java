package de.reneruck.connisRezepteApp;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.ListUtils;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

public class FileScanner extends AsyncTask<String, Integer, List<String>>{

	private String[] documentsOnStorage;
	private String[] documentsInDatabase;
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
		
		if(rezepteDictionary.exists() && rezepteDictionary.isDirectory()){
			this.documentsOnStorage = rezepteDictionary.list();
			try {
				Cursor documentsCursor = db.query(Configurations.table_Rezepte, new String[]{Configurations.rezept_Document}, null, null, null, null, null);
				
				if(documentsCursor.getCount() > 0){
					this.documentsInDatabase = new String[documentsCursor.getCount()];
					int count = 0;
					documentsCursor.moveToFirst();
					do {
						int docIndex = documentsCursor.getColumnIndex(Configurations.rezept_Document);
						this.documentsInDatabase[count] = documentsCursor.getString(docIndex);
						count ++;
					} while (documentsCursor.moveToNext());
					
					// hinzufügen = inDB - onDisc
					this.diff = ListUtils.subtract(Arrays.asList(this.documentsInDatabase), Arrays.asList(this.documentsOnStorage));
					this.newDocumentBean.putAllEntries(diff);
					
				}else{
					this.newDocumentBean.putAllEntries(Arrays.asList(this.documentsOnStorage));
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
