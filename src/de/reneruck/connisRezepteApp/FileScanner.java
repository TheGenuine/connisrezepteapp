package de.reneruck.connisRezepteApp;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.ListUtils;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

public class FileScanner extends AsyncTask<String, Integer, Object>{

	private NewDocumentsBean newDocumentBean;
	private boolean isRunnig = false;
	
	public FileScanner(NewDocumentsBean newDocumentBean) {
		this.newDocumentBean = newDocumentBean;
	}
	
	@Override
	protected List<String> doInBackground(String... arg0) {

		DBManager manager = new DBManager(Main.getContext(),
				Configurations.databaseName, null,
				Configurations.databaseVersion);
		final SQLiteDatabase db = manager.getReadableDatabase();

		File rezepteDictionary = new File(Configurations.dirPath);

		// check if the directory is existent
		// without it, there woun't be any documents
		if (rezepteDictionary.exists() && rezepteDictionary.isDirectory()) {

			List<Integer> documentsInDatabase = getDocsInDatabase(db);

			while (isRunnig) {

				Map<Integer, File> documentsOnStorageMap = getDocsOnStorage(rezepteDictionary);
				if (documentsInDatabase.size() > 0) {
					Set<Integer> documentsOnStorageHashList = documentsOnStorageMap
							.keySet();
					// hinzufügen = onDisc - inDB
					List<Integer> diff = ListUtils.subtract(
							(List) documentsOnStorageHashList,
							documentsInDatabase);

					for (Integer integer : diff) {
						this.newDocumentBean.putEntry(documentsOnStorageMap
								.get(integer));
					}
					documentsInDatabase.addAll(diff);
					try {
						Thread.sleep(1200000);
					} catch (InterruptedException e) {
						Log.e(getClass().getName(),
								"Error while waiting in File Scanner", e);
					}
				} else {
					this.newDocumentBean.putAllEntries(documentsOnStorageMap
							.values());
				}
			}

		} else {
			Log.e("FileScanner", "Rezepte Path: " + Configurations.dirPath
					+ "is no Directory or was not found");
		}
		db.close();

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
				new String[] { Configurations.rezepte_DocumentName }, null,
				null, null, null, null);

		if (documentsCursor.getCount() > 0) {
			// copy all entries from the cursor to a compareable list
			for (documentsCursor.moveToFirst(); documentsCursor.moveToNext(); documentsCursor
					.isAfterLast()) {
				docsInDatabase.add(documentsCursor.getString(0).hashCode());
			}
		}
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
