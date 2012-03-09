package de.reneruck.connisRezepteApp.DB;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteQueryBuilder;
import de.reneruck.connisRezepteApp.Configurations;
import de.reneruck.connisRezepteApp.Rezept;

/**
 * Database Abstraction Layer<br>
 * handles all complex sql actions
 * 
 * @author Rene
 *
 */
public class DatabaseManager {
	
	public static final String DB_MANAGER = "manager";
	public static final String CALLBACK = "callback";
	public static final String QUERY = "query";
	public static final String REZEPT = "rezept";
	private DatabaseHelper dbHelper;

	
	public DatabaseManager(Context context) {
		this.dbHelper = new DatabaseHelper(context, Configurations.databaseName, null, Configurations.databaseVersion);
	}

	/**
	 * Saves all important fields to the database.
	 * 
	 * @param db - a Database to save to (must contain the right tables)
	 * @return true, if the operation was successful, false if not
	 * @throws SQLException
	 */
	public void storeRezept(Rezept rezept, DatabaseCallback callback) throws SQLException{
		Map<String, Object> parameter = new HashMap<String, Object>();
		parameter.put(CALLBACK, callback);
		parameter.put(REZEPT, rezept);
		parameter.put(DB_MANAGER, this);
		new StoreDocument().execute(parameter);		
	}

	public void getAllDocuments(DatabaseCallback allDocumentsCallback) {
		String query = SQLiteQueryBuilder.buildQueryString(true, Configurations.table_Rezepte, new String[]{"*"}, null, null, null, Configurations.rezepte_Name, null);
		Map<String, Object> parameter = new HashMap<String, Object>();
		parameter.put(CALLBACK, allDocumentsCallback);
		parameter.put(QUERY, query);
		parameter.put(DB_MANAGER, this);
		
		new QueryDocumentList().execute(parameter);		
	}
	
	public void getDocument(int documentId, DatabaseCallback callback) {
		Map<String, Object> parameter = new HashMap<String, Object>();
		parameter.put(CALLBACK, callback);
		parameter.put(REZEPT, documentId);
		parameter.put(DB_MANAGER, this);
		new QueryDocument().execute(parameter);		
	}


	public DatabaseHelper getDbHelper() {
		return dbHelper;
	}

	public void setDbHelper(DatabaseHelper dbHelper) {
		this.dbHelper = dbHelper;
	}

	public List<Rezept> getAllZubereitungen() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Rezept> getAllKategorien() {
		// TODO Auto-generated method stub
		return null;
	}
}
