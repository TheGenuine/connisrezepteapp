package de.reneruck.connisRezepteApp;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.ListUtils;

import de.reneruck.connisRezepteApp.DB.DBManager;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

public class FileScanner extends AsyncTask<String, Integer, Object>{

	private DocumentsBean newDocumentBean;
	private boolean isRunnig = false;
	private DBManager manager;
	
	public FileScanner(DocumentsBean newDocumentBean, DBManager manager) {
		this.newDocumentBean = newDocumentBean;
		this.manager = manager;
	}
	
	@Override
	protected List<String> doInBackground(String... arg0) {
		final SQLiteDatabase db = manager.getReadableDatabase();
		File rezepteDictionary = new File(Configurations.dirPath);

		// check if the directory is existent
		// without it, there woun't be any documents
		if (rezepteDictionary.exists() && rezepteDictionary.isDirectory()) {

			List<Integer> documentsInDatabase = getDocsInDatabase(db);


				Map<Integer, File> documentsOnStorageMap = getDocsOnStorage(rezepteDictionary);
				List<File> list = new LinkedList<File>();
				
				if (documentsInDatabase.size() > 0) {
					List<Integer> documentsOnStorageHashList = new LinkedList<Integer>(documentsOnStorageMap.keySet());
					
					// hinzufügen = onDisc - inDB
					List<Integer> diff = ListUtils.subtract(documentsOnStorageHashList,documentsInDatabase);
					for (Integer integer : diff) {
						list.add(documentsOnStorageMap.get(integer));
					}
				} else {
					list = new LinkedList<File>(documentsOnStorageMap.values());
				}

				this.newDocumentBean.putAllEntries(list);
		} else {
			Log.e("FileScanner", "Rezepte Path: " + Configurations.dirPath
					+ "is no Directory or was not found");
		}

		return null;
	}
	
	/**
	 * Queries the Rezepte Database and saves all DocumentName-Entries as Hash
	 * into a list
	 * 
	 * @param db
	 *            - a reference to a SQLiteDatabase to query
	 * @return the count of entries found in the database
	 */
	private List<Integer> getDocsInDatabase(SQLiteDatabase db)
			throws SQLException {
		List<Integer> docsInDatabase = new LinkedList<Integer>();

		Cursor documentsCursor = db.query(Configurations.table_Rezepte,
				new String[] { Configurations.rezepte_DocumentHash }, null,
				null, null, null, null);

		if (documentsCursor.getCount() > 0) {
			// copy all entries from the cursor to a compareable list
			for (documentsCursor.moveToFirst(); !documentsCursor.isAfterLast(); documentsCursor.moveToNext()) {
				docsInDatabase.add(documentsCursor.getInt(0));
			}
		}
		documentsCursor.close();
		return docsInDatabase;
	}
	
	/**
	 * lists all files in the given Dir an saves the hash to documentsOnStorage
	 * and returns the count of found files
	 * 
	 * @param dir
	 *            - the dir to search into
	 * @return the count of found files
	 */
	private Map<Integer, File> getDocsOnStorage(File dir) {
		Map<Integer, File> fileList = new HashMap<Integer, File>();
		File[] liste = dir.listFiles();
		for (int i = 0; i < liste.length; i++) {
			if (liste[i].isDirectory()) {
				fileList.putAll(getDocsOnStorage(liste[i]));
			} else {
				fileList.put(liste[i].getName().hashCode(), liste[i]);
			}
		}
		return fileList;
	}

	public boolean isRunnig() {
		return isRunnig;
	}

	public void setRunnig(boolean isRunnig) {
		this.isRunnig = isRunnig;
	}
		

}
