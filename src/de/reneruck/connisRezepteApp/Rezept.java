package de.reneruck.connisRezepteApp;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class Rezept {

	private int id;
	private String name;
	private String documentName;
	private String documentPath;
	private List<String> kategorien = new LinkedList<String>();
	private String zubereitungsart;
	private List<String> zutaten = new LinkedList<String>();
	private int zeit;
	private File originalFile;
	private boolean stored;
	
	public Rezept(String filename) {
		this(new File(filename));
	}
	
	public Rezept(File document) {
		String documentName = document.getName();
		this.originalFile = document;
		this.id = documentName.hashCode();
		String[] doucmentSplit = documentName.split("\\.");
		this.name =doucmentSplit .length > 0 ?doucmentSplit[0] : documentName;
		this.documentName = documentName;
		this.zeit = 0;
		this.documentPath = Configurations.dirPath+documentName;
	}
	
	public Rezept(Cursor cursor) {
		this.id = cursor.getInt(cursor.getColumnIndex(Configurations.rezepte_Id));
		this.name = cursor.getString(cursor.getColumnIndex(Configurations.rezepte_Name));
		this.documentName = cursor.getString(cursor.getColumnIndex(Configurations.rezepte_DocumentName));
		this.documentPath = Configurations.dirPath+this.documentName;
		this.zubereitungsart = cursor.getString(cursor.getColumnIndex(Configurations.rezepte_Zubereitung));
		this.zeit = cursor.getInt(cursor.getColumnIndex(Configurations.rezepte_Zeit));
		this.stored = true;
	}
	/**
	 * Saves all important fields to the database.
	 * 
	 * @param db - a Database to save to (must contain the right tables)
	 * @return true, if the operation was successful, false if not
	 * @throws SQLException
	 */
	public boolean saveToDB(SQLiteDatabase db) throws SQLException{
		db.beginTransaction();
		boolean success = false;
		ContentValues values = new ContentValues();
		values.put(Configurations.rezepte_Id, this.id);
		values.put(Configurations.rezepte_Name, this.name);
		values.put(Configurations.rezepte_DocumentHash, this.id);
		values.put(Configurations.rezepte_PathToDocument, this.documentPath);
		values.put(Configurations.rezepte_DocumentName, this.documentName);
		values.put(Configurations.rezepte_Zubereitung, this.zubereitungsart);
		values.put(Configurations.rezepte_Zeit, this.zeit);
		long rezeptId = -1;
		if(this.stored){
			rezeptId = db.update(Configurations.table_Rezepte, values, Configurations.rezepte_Id + "=" + this.id ,null);
		}else{
			rezeptId = db.insert(Configurations.table_Rezepte, null, values);
		}		
		boolean statusZutaten = storeZutaten(rezeptId, db);
		boolean statusKategorien = storeKategorien(rezeptId, db);
		if (rezeptId != -1){
			success = true;
			db.setTransactionSuccessful();
		}
		db.endTransaction();
		return success & statusKategorien & statusZutaten;
	}

	/**
	 * Stores all the Kategories inserted into the form to the Database
	 * @param rezeptId
	 * @param db
	 * @return
	 */
	private boolean storeKategorien(long rezeptId, SQLiteDatabase db) {
		db.beginTransaction();
		boolean success = true;
		boolean success2 = true;
		for (String kategorie : this.kategorien) {
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
	private boolean storeZutaten(long rezeptId, SQLiteDatabase db) throws SQLException{
		db.beginTransaction();
		boolean success = true;
		boolean success2 = true;
		for (String zutat : this.zutaten) {
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
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDocumentPath() {
		return documentPath;
	}
	public void setDocumentPath(String documentPath) {
		this.documentPath = documentPath;
	}
	public boolean isStored() {
		return stored;
	}
	public void setStored(boolean stored) {
		this.stored = stored;
	}
	public String getDocumentName() {
		return documentName;
	}
	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}
	public List<String> getKategorien() {
		return kategorien;
	}
	public void setKategorien(List<String> kategorien) {
		this.kategorien = kategorien;
	}
	public void addKategorie(String kategorie) {
		if(this.kategorien == null) {
			this.kategorien = new LinkedList<String>();
		}
		this.kategorien.add(kategorie);
	}
	public String getZubereitungsart() {
		return zubereitungsart;
	}
	public void setZubereitungsart(String zubereitungsart) {
		this.zubereitungsart = zubereitungsart;
	}
	public List<String> getZutaten() {
		return zutaten;
	}
	public void setZutaten(List<String> zutat) {
		this.zutaten = zutat;
	}
	public void setZutaten(String zutaten) {
		String[] zutatenSplit = zutaten.split(",");
		this.zutaten.clear();
		for (String string : zutatenSplit) {
			this.zutaten.add(string.trim());
		}
	}
	public void addZutat(String zutat) {
		if(this.zutaten == null) {
			this.zutaten = new LinkedList<String>();
		}
		this.zutaten.add(zutat.trim());
	}

	public File getOriginalFile() {
		return originalFile;
	}

	public int getZeit() {
		return zeit;
	}

	public void setZeit(int zeit) {
		this.zeit = zeit;
	}
}
