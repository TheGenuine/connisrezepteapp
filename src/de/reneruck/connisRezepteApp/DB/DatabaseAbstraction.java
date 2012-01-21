package de.reneruck.connisRezepteApp.DB;

import java.util.LinkedList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import de.reneruck.connisRezepteApp.Configurations;
import de.reneruck.connisRezepteApp.Rezept;

/**
 * Database Abstraction Layer<br>
 * handles all complex sql actions
 * 
 * @author Rene
 *
 */
public class DatabaseAbstraction {
	
	private DBManager manager;

	
	public DatabaseAbstraction(DBManager manager) {
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
			rezept.setKategorien(new LinkedList<String>());
			db.close();
			return rezept;
		}
		db.close();
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
	}
	
	/**
	 * Saves all important fields to the database.
	 * 
	 * @param db - a Database to save to (must contain the right tables)
	 * @return true, if the operation was successful, false if not
	 * @throws SQLException
	 */
	public boolean saveToDB(Rezept rezept) throws SQLException{
		SQLiteDatabase db = this.manager.getWritableDatabase();
		db.beginTransaction();
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
			rezeptId = db.update(Configurations.table_Rezepte, values, Configurations.rezepte_Id + "=" + rezept.getId() ,null);
		}else{
			rezeptId = db.insert(Configurations.table_Rezepte, null, values);
		}		
		boolean statusZutaten = storeZutaten(rezeptId, db, rezept);
		boolean statusKategorien = storeKategorien(rezeptId, db, rezept);
		if (rezeptId != -1){
			success = true;
			db.setTransactionSuccessful();
		}
		db.endTransaction();
		db.close();
		return success & statusKategorien & statusZutaten;
	}

	/**
	 * Stores all the Kategories inserted into the form to the Database
	 * @param rezeptId
	 * @param db
	 * @return
	 */
	private boolean storeKategorien(long rezeptId, SQLiteDatabase db, Rezept rezept) {
		db.beginTransaction();
		boolean success = true;
		boolean success2 = true;
		for (String kategorie : rezept.getKategorien()) {
			ContentValues values = new ContentValues(2);
			values.put(Configurations.kategorien_Id, kategorie.hashCode());
			values.put(Configurations.kategorien_value, kategorie);
			
			if(!exists(db, Configurations.table_Kategorien, Configurations.kategorien_Id + " = " + kategorie.hashCode())){
				long kategorieId = db.insertWithOnConflict(Configurations.table_Kategorien, null, values, SQLiteDatabase.CONFLICT_ROLLBACK);
				if(kategorieId != -1){
					success = true & success;
					success2 = success2 & storeRezeptToKategorie(rezeptId, kategorieId, db);
				} else {
					success = false;
				}
			} else {
				success = true & success;
				success2 = success2 & storeRezeptToKategorie(rezeptId, kategorie.hashCode(), db);
			}
			
		}
		if(success) db.setTransactionSuccessful();
		db.endTransaction();
		return success & success2;
	}
	
	
	private boolean storeRezeptToKategorie(long rezeptId, long kategorieId, SQLiteDatabase db) {
		db.beginTransaction();
		boolean success = false;
		if(!exists(db, Configurations.table_Rezept_to_Kategorie, 
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
	private boolean storeZutaten(long rezeptId, SQLiteDatabase db, Rezept rezept) throws SQLException{
		db.beginTransaction();
		boolean success = true;
		boolean success2 = true;
		for (String zutat : rezept.getZutaten()) {
			ContentValues values = new ContentValues(2);
			values.put(Configurations.zutaten_Id, zutat.hashCode());
			values.put(Configurations.zutaten_value, zutat);
			
			if(!exists(db, Configurations.table_Zutaten, Configurations.zutaten_Id + " = " + zutat.hashCode())){
				long zutatId = db.insertWithOnConflict(Configurations.table_Zutaten, null, values, SQLiteDatabase.CONFLICT_ROLLBACK);
				if(zutatId != -1){
					success = true & success;
					success2 = success2 & storeRezeptToZutat(rezeptId, zutatId, db);
				} else {
					success = false;
				}
			} else {
				success = true & success;
				success2 = success2 & storeRezeptToZutat(rezeptId, zutat.hashCode(), db);
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
		if(!exists(db, Configurations.table_Rezept_to_Zutat, 
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
	
	public static boolean exists(SQLiteDatabase db, String table, String where){
		Cursor c = db.query(table, new String[]{"*"}, where, null, null, null,null);
		return c.getCount() > 0 ? true : false;
	}
}
