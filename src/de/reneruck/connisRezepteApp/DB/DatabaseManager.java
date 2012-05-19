package de.reneruck.connisRezepteApp.DB;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.SQLException;
import de.reneruck.connisRezepteApp.Configurations;
import de.reneruck.connisRezepteApp.Rezept;
import de.reneruck.connisRezepteApp.Configurations.ListType;

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

	/**
	 * Queries and returns all {@link Rezept}e stored in the Database.
	 * 
	 * @param allDocumentsCallback
	 *            {@link DatabaseCallback} implementation to get the result
	 */
	public void getAllRezepe(DatabaseCallback allDocumentsCallback) {
		String query = "Select * From  Rezepte " +
				"Inner Join  Rezepte_has_Zutaten    On Rezepte.idRezepte = Rezepte_has_Zutaten.Rezepte_idRezepte " +
				"Inner Join  Zubereitungsarten On Zubereitungsarten.idZubereitungsart =    Rezepte.Zubereitungsarten_idZubereitungsart " +
				"Inner Join  Zutaten On Zutaten.idZutaten = Rezepte_has_Zutaten.Zutaten_idZutaten " +
				"Inner Join  Zutaten_Kategorie On Zutaten_Kategorie.idZutaten_Kategorie = Zutaten.Zutaten_Kategorie_idZutaten_Kategorie"; 
		
//		String query = SQLiteQueryBuilder.buildQueryString(true, Configurations.TABLE_REZEPTE, new String[]{"*"}, null, null, null, Configurations.NAME, null);
		Map<String, Object> parameter = new HashMap<String, Object>();
		parameter.put(CALLBACK, allDocumentsCallback);
		parameter.put(QUERY, query);
		parameter.put(DB_MANAGER, this);
		new QueryRezepteList().execute(parameter);		
	}
	
	/**
	 * Queries for a {@link Rezept} with the given documentId
	 * 
	 * @param documentId
	 *            documentId of the {@link Rezept} to query for
	 * @param callback
	 *            {@link DatabaseCallback} implementation to get the result
	 */
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
	
	private void queryAllofATable(DatabaseCallback callback, ListType type) {
		Map<String, Object> parameter = new HashMap<String, Object>();
		parameter.put(CALLBACK, callback);
		parameter.put(QUERY, type);
		parameter.put(DB_MANAGER, this);
		new QueryAllOfaTable().execute(parameter);	
	}

	/**
	 * Queries and returns all Zubereitungsarten stored in the database
	 * 
	 * @param allDocumentsCallback 
	 *            {@link DatabaseCallback} implementation to get the result
	 */
	public void getAllZubereitungsarten(DatabaseCallback allDocumentsCallback) {
		queryAllofATable(allDocumentsCallback, ListType.Zubereitungsart);
	}

	/**
	 * Queries and returns all Kategorien stored in the database
	 * 
	 * @param allDocumentsCallback 
	 *            {@link DatabaseCallback} implementation to get the result
	 */
	public void getAllKategorien(DatabaseCallback allDocumentsCallback) {
		queryAllofATable(allDocumentsCallback, ListType.Kategorie);
	}
	
	/**
	 * Queries and returns all Zutaten stored in the database<br>
	 * <b>Note:</b> requires an instance of {@link DatabaseZutatenQueryCallback}
	 * as callback
	 * 
	 * @param allDocumentsCallback
	 *            {@link DatabaseCallback} implementation to get the result
	 */
	public void getAllZutaten(DatabaseCallback allDocumentsCallback) {
		queryAllofATable(allDocumentsCallback, ListType.Zutat);
	}
	
	/**
	 * Queries and returns all ZutatenKategorien stored in the database<br>
	 * 
	 * @param allDocumentsCallback
	 *            {@link DatabaseCallback} implementation to get the result
	 */
	public void getAllZutatenKategorie(DatabaseCallback allDocumentsCallback) {
		queryAllofATable(allDocumentsCallback, ListType.ZutatKategorie);
	}
	
}
