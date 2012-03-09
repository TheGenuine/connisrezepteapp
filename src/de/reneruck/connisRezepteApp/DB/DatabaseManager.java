package de.reneruck.connisRezepteApp.DB;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.AsyncTask;
import android.util.Log;
import de.reneruck.connisRezepteApp.Configurations;
import de.reneruck.connisRezepteApp.DatabaseCallback;
import de.reneruck.connisRezepteApp.Rezept;

/**
 * Database Abstraction Layer<br>
 * handles all complex sql actions
 * 
 * @author Rene
 *
 */
public class DatabaseManager {
	
	private DatabaseHelper manager;

	
	public DatabaseManager(DatabaseHelper manager) {
		this.manager = manager;
	}

	public Rezept getDocument(int documentId) {
		SQLiteDatabase db= this.manager.getReadableDatabase();
		Cursor c = db.query(Configurations.table_Rezepte, new String[]{"*"}, Configurations.rezepte_Id + "=" + documentId, null, null, null, null);
		
		c.moveToFirst();
		if(c.getCount() > 0){
			c.moveToFirst();
			Rezept rezept = new Rezept(c);
			fillZutaten(rezept, documentId, db);
			fillKategorien(rezept, documentId, db);
			return rezept;
		}
		return null;
	}

	private void fillKategorien(Rezept rezept, int documentId, SQLiteDatabase db) {
		
		String query = "select " + Configurations.table_Kategorien + "." + Configurations.kategorien_value + " from " + Configurations.table_Rezept_to_Kategorie + " JOIN " + Configurations.table_Kategorien+ " ON ("
				+ Configurations.table_Kategorien + "." + Configurations.kategorien_Id + " = " + Configurations.table_Rezept_to_Kategorie + "." + Configurations.rezept_to_kategorie_kategorieId + ")" + " where "
				+ Configurations.rezept_to_kategorie_rezeptId + " = " + documentId;
		Cursor kategorien = db.rawQuery(query, null);

		if(kategorien.getCount() > 0){
			kategorien.moveToFirst();
			do{
				rezept.addKategorie(kategorien.getString(0));
			}while(kategorien.moveToNext());
		}
		kategorien.close();
	}

	/**
	 * Queries for all Zutaten for this Rezept and add them to the Object
	 * @param rezept
	 * @param documentId
	 * @param db
	 */
	private void fillZutaten(Rezept rezept, int documentId, SQLiteDatabase db) {
		/*
		 * select Zutaten.value from Rezept_Zutat JOIN Zutaten ON (Zutaten.ID = Rezept_Zutat.zutatId) where Rezept_Zutat.rezeptId=1234
		 */
		String query = "select " + Configurations.table_Zutaten + "." + Configurations.zutaten_value + " from " + Configurations.table_Rezept_to_Zutat + " JOIN " + Configurations.table_Zutaten + " ON ("
				+ Configurations.table_Zutaten + "." + Configurations.zutaten_Id + " = " + Configurations.table_Rezept_to_Zutat + "." + Configurations.rezept_to_zutat_zutatId + ")" + " where "
				+ Configurations.rezept_to_zutat_rezeptId + " = " + documentId;
		Cursor zutaten = db.rawQuery(query, null);

		if(zutaten.getCount() > 0){
			zutaten.moveToFirst();
			do{
				rezept.addZutat(zutaten.getString(zutaten.getColumnIndex(Configurations.zutaten_value)));
			}while(zutaten.moveToNext());
		}
		zutaten.close();
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
		parameter.put("callback", callback);
		parameter.put("rezept", rezept);
		new StoreRezept().execute(parameter);		
	}

	

	public void getAllDocuments(DatabaseCallback allDocumentsCallback) {
		
		String query = SQLiteQueryBuilder.buildQueryString(true, Configurations.table_Rezepte, new String[]{"*"}, null, null, null, Configurations.rezepte_Name, null);
		
		Map<String, Object> parameter = new HashMap<String, Object>();
		parameter.put("callback", allDocumentsCallback);
		parameter.put("query", query);
		
		new QueryDocumentList().execute(parameter);		
	}
	
	
	
	/**
	 * 
	 * @author Rene
	 *
	 */
	public class QueryDocumentList extends AsyncTask<Map<String, Object>, String, List<Rezept>> {

		private static final String TAG = "QueryDocumentList Task";
		private DatabaseCallback listener;

		@Override
		protected List<Rezept> doInBackground(Map<String, Object>... params) {
			Map<String, Object> param = params[0];
			String query = (String) param.get("query");
			this.listener = (DatabaseCallback) param.get("callback");
			
			List<Rezept> rezepteList = new LinkedList<Rezept>();
			try {
				SQLiteDatabase db = manager.getReadableDatabase();
				Cursor c = db.rawQuery(query, null);

				if (c.getCount() == 0) {
					rezepteList.add(new Rezept("Keine Rezepte gefunden"));
				} else {
					c.moveToFirst();
					do {
						Rezept rezept = new Rezept(c);
						rezepteList.add(rezept);
					} while (c.moveToNext());
				}
				try {
					c.close();
				} catch (IllegalStateException e) {
					Log.e(TAG, e.getLocalizedMessage());
				}
			} catch (SQLException e) {
				e.fillInStackTrace();
			}
			return rezepteList;
		}
		

		private void queryZutaten(SQLiteDatabase db, Rezept rezept) {
			String query = "select " + Configurations.zutaten_value+ " from " + Configurations.table_Zutaten + ", " + Configurations.table_Rezept_to_Zutat
					+ " where " + Configurations.table_Rezept_to_Zutat+ "." + Configurations.rezept_to_zutat_rezeptId + "=" + rezept.getId()
					+ " and " + Configurations.table_Zutaten + "." + Configurations.zutaten_Id + " = " + Configurations.table_Rezept_to_Zutat + "." + Configurations.rezept_to_zutat_rezeptId;

			Cursor c = db.rawQuery(query, null);
			if(c.getCount() > 0){
				
			}
		}
		
		@Override
		protected void onPostExecute(List<Rezept> result) {
			this.listener.onsSelectCallback(result);
		}

	}
	
	/**
	 * Async Task to store a given Rezept Object to the Database or update an
	 * existing Rezept Entry
	 * 
	 * @author Rene
	 * 
	 */
	public class StoreRezept extends AsyncTask<Map<String, Object>, Void, Boolean> {

		private static final String TAG = "StoreRezept AsyncTask";
		private DatabaseCallback callback;
		private SQLiteDatabase db;

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			this.callback.onStoreCallback(result);
		}
		
		@Override
		protected Boolean doInBackground(Map<String, Object>... params) {
			this.callback = (DatabaseCallback) params[0].get("callback");
			Rezept rezept = (Rezept) params[0].get("rezept");
			
			if(callback != null && rezept != null) {
				
				this.db = manager.getWritableDatabase();
				this.db.beginTransaction();
				boolean success = false;
				ContentValues values = new ContentValues();
				values.put(Configurations.rezepte_Id, rezept.getId());
				values.put(Configurations.rezepte_Name, rezept.getName());
				values.put(Configurations.rezepte_DocumentHash, rezept.getId());
				values.put(Configurations.rezepte_PathToDocument, rezept.getDocumentPath());
				values.put(Configurations.rezepte_DocumentName, rezept.getDocumentName());
				values.put(Configurations.rezepte_Zubereitung, rezept.getZubereitungsart());
				values.put(Configurations.rezepte_Zeit, rezept.getZeit());
				long rezeptId = -1;
				if(rezept.isStored()){
					rezeptId = this.db.update(Configurations.table_Rezepte, values, Configurations.rezepte_Id + "=" + rezept.getId() ,null);
				}else{
					rezeptId = this.db.insert(Configurations.table_Rezepte, null, values);
				}		
				boolean statusZutaten = storeZutaten(rezept);
				boolean statusKategorien = storeKategorien(rezept);
				if (rezeptId != -1){
					success = true;
					this.db.setTransactionSuccessful();
				}
				this.db.endTransaction();
				this.db.close();
				return success & statusKategorien & statusZutaten;
			} else {
				Log.e(TAG, "Callback or rezept param is NULL!");
				return false;
			}
		}
		
		/**
		 * Stores all the Kategories inserted into the form to the Database
		 * @param rezeptId
		 * @param db
		 * @return
		 */
		private boolean storeKategorien(Rezept rezept) {
			db.beginTransaction();
			boolean success = true;
			boolean success2 = true;
			for (String kategorie : rezept.getKategorien()) {
				if(!kategorie.isEmpty() && kategorie.length() > 0){
					ContentValues values = new ContentValues(2);
					values.put(Configurations.kategorien_Id, kategorie.hashCode());
					values.put(Configurations.kategorien_value, kategorie);
					
					if(!exists(Configurations.table_Kategorien, Configurations.kategorien_Id + " = " + kategorie.hashCode())){
						long kategorieId = db.insertWithOnConflict(Configurations.table_Kategorien, null, values, SQLiteDatabase.CONFLICT_ROLLBACK);
						if(kategorieId != -1){
							success = true & success;
							success2 = success2 & storeRezeptToKategorie(rezept.getId(), kategorieId, db);
						} else {
							success = false;
						}
					} else {
						success = true & success;
						success2 = success2 & storeRezeptToKategorie(rezept.getId(), kategorie.hashCode(), db);
					}
				}
			}
			if(success) db.setTransactionSuccessful();
			db.endTransaction();
			return success & success2;
		}
		
		
		private boolean storeRezeptToKategorie(long rezeptId, long kategorieId, SQLiteDatabase db) {
			db.beginTransaction();
			boolean success = false;
			if(!exists(Configurations.table_Rezept_to_Kategorie, 
					Configurations.rezept_to_kategorie_rezeptId + " = " + rezeptId +
					" and " + Configurations.rezept_to_kategorie_kategorieId + " = " + kategorieId)){
				
				ContentValues values = new ContentValues(2);
				values.put(Configurations.rezept_to_kategorie_rezeptId, rezeptId);
				values.put(Configurations.rezept_to_kategorie_kategorieId, kategorieId);
				
				if(db.insert(Configurations.table_Rezept_to_Kategorie, null, values) != -1){
					success = true;
				}
				
			} else {
				success = true;
			}
			if(success)db.setTransactionSuccessful();
			db.endTransaction();
			return success;
		}
		/**
		 * 
		 * @param rezeptId
		 * @param db
		 * @return true if the transaction was successful, false if an error occurred
		 */
		private boolean storeZutaten(Rezept rezept) throws SQLException{
			db.beginTransaction();
			boolean success = true;
			boolean success2 = true;
			for (String zutat : rezept.getZutaten()) {
				ContentValues values = new ContentValues(2);
				values.put(Configurations.zutaten_Id, zutat.hashCode());
				values.put(Configurations.zutaten_value, zutat);
				
				if(!exists(Configurations.table_Zutaten, Configurations.zutaten_Id + " = " + zutat.hashCode())){
					long zutatId = db.insertWithOnConflict(Configurations.table_Zutaten, null, values, SQLiteDatabase.CONFLICT_ROLLBACK);
					if(zutatId != -1){
						success = true & success;
						success2 = success2 & storeRezeptToZutat(rezept.getId(), zutatId, db);
					} else {
						success = false;
					}
				} else {
					success = true & success;
					success2 = success2 & storeRezeptToZutat(rezept.getId(), zutat.hashCode(), db);
				}
			}
			if(success) db.setTransactionSuccessful();
			db.endTransaction();
			return success & success2;
		}
		
		/**
		 * 
		 * @param rezeptId
		 * @param zutatId
		 * @param db
		 * @return true if the transaction was successful, false if an error occurred
		 */
		private boolean storeRezeptToZutat(long rezeptId, long zutatId, SQLiteDatabase db) {
			db.beginTransaction();
			boolean success = false;
			if(!exists(Configurations.table_Rezept_to_Zutat, 
					Configurations.rezept_to_zutat_rezeptId + " = " + rezeptId +
					" and " + Configurations.rezept_to_zutat_zutatId + " = " + zutatId)){
				
				ContentValues values = new ContentValues(2);
				values.put(Configurations.rezept_to_zutat_rezeptId, rezeptId);
				values.put(Configurations.rezept_to_zutat_zutatId, zutatId);
				
				if(db.insert(Configurations.table_Rezept_to_Zutat, null, values) != -1){
					success = true;
				}
				
			} else {
				success = true;
			}
			if(success)db.setTransactionSuccessful();
			db.endTransaction();
			return success;
		}
		
		public boolean exists(String table, String where){
			SQLiteDatabase db = manager.getReadableDatabase();
			Cursor c = db.query(table, new String[]{"*"}, where, null, null, null,null);
			boolean result = c.getCount() > 0 ? true : false;
			c.close();
			db.close();
			return result;
		}

	}

	public List<String> getAllKategorien() {
		 SQLiteDatabase db = this.manager.getReadableDatabase();
		 Cursor query = db.query(Configurations.table_Kategorien, new String[]{Configurations.kategorien_value}, null, null, null, null, null);
		 List<String> results = new LinkedList<String>();
		 if(query.getCount() > 0){
			 for (query.moveToFirst(); query.isAfterLast(); query.moveToNext()){
				results.add(query.getString(0));
			}
		 }
		 query.close();
		 db.close();
		return results;
	}

	public List<String> getAllZubereitungen() {
		 SQLiteDatabase db = this.manager.getReadableDatabase();
		 Cursor query = db.query(Configurations.table_Rezepte, new String[]{Configurations.rezepte_Zubereitung}, null, null, null, null, null);
		 List<String> results = new LinkedList<String>();
		 if(query.getCount() > 0){
			 for (query.moveToFirst(); query.isAfterLast(); query.moveToNext()){
				results.add(query.getString(0));
			}
		 }
		 query.close();
		 db.close();
		return results;
	}

}
