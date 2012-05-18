package de.reneruck.connisRezepteApp.DB;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import de.reneruck.connisRezepteApp.Configurations;
import de.reneruck.connisRezepteApp.Rezept;

public class QueryDocument extends AsyncTask<Map<String, Object>, Void, Rezept> {

	private DatabaseManager manager;
	private DatabaseQueryCallback listener;

	@Override
	protected Rezept doInBackground(Map<String, Object>... params) {
		Map<String, Object> param = params[0];
		this.manager = (DatabaseManager)param.get(DatabaseManager.DB_MANAGER);
		this.listener = (DatabaseQueryCallback) param.get(DatabaseManager.CALLBACK);
		int documentId = (Integer) param.get(DatabaseManager.REZEPT);
		return getDocument(documentId);
	}

	@Override
	protected void onPostExecute(Rezept result) {
		super.onPostExecute(result);
		List<Rezept> resultList = new LinkedList<Rezept>();
		resultList.add(result);
		this.listener.onSelectCallback(resultList);
	}
	
	public Rezept getDocument(int documentId) {
		SQLiteDatabase db= this.manager.getDbHelper().getReadableDatabase();
		Cursor c = db.query(Configurations.TABLE_REZEPTE, new String[]{"*"}, Configurations.ID_REZEPTE + "=" + documentId, null, null, null, null);
		
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
		
//		String query = "select " + Configurations.TABLE_KATEGORIEN + "." + Configurations.VALUE + " from " + Configurations.table_Rezept_to_Kategorie + " JOIN " + Configurations.table_Kategorien+ " ON ("
//				+ Configurations.table_Kategorien + "." + Configurations.kategorien_Id + " = " + Configurations.table_Rezept_to_Kategorie + "." + Configurations.rezept_to_kategorie_kategorieId + ")" + " where "
//				+ Configurations.rezept_to_kategorie_rezeptId + " = " + documentId;
//		Cursor kategorien = db.rawQuery(query, null);
//
//		if(kategorien.getCount() > 0){
//			kategorien.moveToFirst();
//			do{
//				rezept.addKategorie(kategorien.getString(0));
//			}while(kategorien.moveToNext());
//		}
//		kategorien.close();
	}

	/**
	 * Queries for all Zutaten for this Rezept and add them to the Object
	 * @param rezept
	 * @param documentId
	 * @param db
	 */
	private void fillZutaten(Rezept rezept, int documentId, SQLiteDatabase db) {
//		/*
//		 * select Zutaten.value from Rezept_Zutat JOIN Zutaten ON (Zutaten.ID = Rezept_Zutat.zutatId) where Rezept_Zutat.rezeptId=1234
//		 */
//		String query = "select " + Configurations.TABLE_ZUTATEN + "." + Configurations.zutaten_value + " from " + Configurations.table_Rezept_to_Zutat + " JOIN " + Configurations.table_Zutaten + " ON ("
//				+ Configurations.table_Zutaten + "." + Configurations.zutaten_Id + " = " + Configurations.table_Rezept_to_Zutat + "." + Configurations.rezept_to_zutat_zutatId + ")" + " where "
//				+ Configurations.rezept_to_zutat_rezeptId + " = " + documentId;
//		Cursor zutaten = db.rawQuery(query, null);
//
//		if(zutaten.getCount() > 0){
//			zutaten.moveToFirst();
//			do{
//				rezept.addZutat(zutaten.getString(zutaten.getColumnIndex(Configurations.zutaten_value)));
//			}while(zutaten.moveToNext());
//		}
//		zutaten.close();
	}
}
