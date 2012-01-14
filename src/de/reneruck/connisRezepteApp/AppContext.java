package de.reneruck.connisRezepteApp;

import java.util.LinkedList;
import java.util.List;

import android.app.Application;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AppContext extends Application {

	private DBManager manager;
	private NewDocumentsBean newDocumentsBean;
	private List<Rezept> customDocumentsBean;
	
	@Override
	public void onCreate() {
		super.onCreate();
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
	}

	/**
	 * Queries for all Zutaten for this Rezept and add them to the Object
	 * @param rezept
	 * @param documentId
	 * @param db
	 */
	private void fillZutaten(Rezept rezept, int documentId, SQLiteDatabase db) {
		/*
		 * 
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

	public DBManager getManager() {
		return manager;
	}

	public void setManager(DBManager manager) {
		this.manager = manager;
	}

	public DBManager getDBManager() {
		return this.manager;
	}

	public NewDocumentsBean getNewDocumentsBean() {
		return this.newDocumentsBean;
	}

	public void setNewDocumentsBean(NewDocumentsBean newDocumentsBean) {
		this.newDocumentsBean = newDocumentsBean;
	}

	public List<Rezept> getCustomDocumentsBean() {
		return this.customDocumentsBean;
	}

	public void setCustomDocumentsBean(List<Rezept> customDocumentsBean) {
		this.customDocumentsBean = customDocumentsBean;
	}
}
