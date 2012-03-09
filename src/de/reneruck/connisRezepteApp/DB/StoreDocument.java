package de.reneruck.connisRezepteApp.DB;

import java.util.Map;

import de.reneruck.connisRezepteApp.Configurations;
import de.reneruck.connisRezepteApp.Rezept;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Async Task to store a given Rezept Object to the Database or update an
 * existing Rezept Entry
 * 
 * @author Rene
 * 
 */
public class StoreDocument extends AsyncTask<Map<String, Object>, Void, Boolean> {

	private static final String TAG = "StoreRezept AsyncTask";
	private DatabaseStorageCallback callback;
	private SQLiteDatabase db;
	private DatabaseManager manager;

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		this.callback.onStoreCallback(result);
	}
	
	@Override
	protected Boolean doInBackground(Map<String, Object>... params) {
		Map<String, Object> param = params[0];
		this.callback = (DatabaseStorageCallback) param.get(DatabaseManager.CALLBACK);
		this.manager = (DatabaseManager)param.get(DatabaseManager.DB_MANAGER);
		Rezept rezept = (Rezept) params[0].get(DatabaseManager.REZEPT);
		
		if(callback != null && rezept != null) {
			
			this.db = this.manager.getDbHelper().getWritableDatabase();
			
			try {
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
				if (rezeptId != -1 && statusKategorien && statusZutaten){
					success = true;
					this.db.setTransactionSuccessful();
				}
				return success & statusKategorien & statusZutaten;
			} catch (Exception e) {
				e.fillInStackTrace();
				return false;
			} finally {
				this.db.endTransaction();
				this.db.close();
			}
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
		return success & success2;
	}
	
	
	private boolean storeRezeptToKategorie(long rezeptId, long kategorieId, SQLiteDatabase db) {
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
		return success;
	}
	/**
	 * 
	 * @param rezeptId
	 * @param db
	 * @return true if the transaction was successful, false if an error occurred
	 */
	private boolean storeZutaten(Rezept rezept) throws SQLException{
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
		return success;
	}
	
	public boolean exists(String table, String where){
		Cursor c = this.db.query(table, new String[]{"*"}, where, null, null, null,null);
		boolean result = c.getCount() > 0 ? true : false;
		c.close();
		return result;
	}

}
