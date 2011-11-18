package de.reneruck.connisRezepteApp;

import java.util.List;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class Rezept {

	private int id;
	private String name;
	private String documentPath;
	private List<String> keywords;
	
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
	public List<String> getKeywords() {
		return keywords;
	}
	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}
	
	/**
	 * Saves all important fields to the database.
	 * 
	 * @param db - a Database to save to (must contain the right tables)
	 * @return true, if the operation was successful, false if not
	 * @throws SQLException
	 */
	public boolean saveToDB(SQLiteDatabase db) throws SQLException{
		
		StringBuilder keywordsBuilder = new StringBuilder();
		for (String keyword : this.keywords) {
			ContentValues values = new ContentValues();
			values.put(Configurations.keywords_keyword, keyword);
			keywordsBuilder.append(db.insertWithOnConflict(Configurations.table_Keywords, null, values, 0) + ",");
		}
		String keywords = keywordsBuilder.toString();
		
		// trim the string to avoid a ',' at the end
		if(",".equals(keywords.charAt(keywords.length()))){
			keywords = keywords.substring(0, keywords.length()-1);
		}
		ContentValues values2 = new ContentValues(2);
		values2.put(Configurations.rezept_Name, this.name);
		values2.put(Configurations.rezept_DocumentPath, this.documentPath);
		values2.put(Configurations.rezepte_stichwoerter, keywords);
		
		long id = db.insert(Configurations.table_Rezepte, null, values2);
		
		if(id != -1){
			return true;
		}else{
			return false;
		}
	}
	
}
