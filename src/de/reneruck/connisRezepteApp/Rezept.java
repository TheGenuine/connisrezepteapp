package de.reneruck.connisRezepteApp;

import java.util.Arrays;
import java.util.List;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class Rezept {

	private int id;
	private String name;
	private String documentName;
	private String documentPath;
	private boolean stored;
	
	public Rezept(String documentName) {
		this.name = documentName;
		this.documentName = documentName;
		this.documentPath = Configurations.dirPath+documentName;
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
	
	/**
	 * Saves all important fields to the database.
	 * 
	 * @param db - a Database to save to (must contain the right tables)
	 * @return true, if the operation was successful, false if not
	 * @throws SQLException
	 */
	public boolean saveToDB(SQLiteDatabase db) throws SQLException{
		ContentValues values2 = new ContentValues(2);
		values2.put(Configurations.rezepte_Name, this.name);
		values2.put(Configurations.rezepte_PathToDocument, this.documentPath);
		values2.put(Configurations.rezepte_DocumentName, this.documentName);
		
		long id = db.insert(Configurations.table_Rezepte, null, values2);
		
		if(id != -1){
			stored = true;
			return true;
		}else{
			return false;
		}
	}
	public boolean isStored() {
		return stored;
	}
	public void setStored(boolean stored) {
		this.stored = stored;
	}
	public void setKeywords(String text) {
		if(text != null && !text.isEmpty()){
			List<String> keywords = Arrays.asList(text.split(","));
		}
	}
	public String getDocumentName() {
		return documentName;
	}
	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}
	
}
