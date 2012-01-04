package de.reneruck.connisRezepteApp;

import java.util.LinkedList;

import android.app.Application;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AppContext extends Application {

	private DBManager manager;
	
	@Override
	public void onCreate() {
		super.onCreate();
	}

	public Rezept getDocument(int documentId) {
		SQLiteDatabase db= this.manager.getReadableDatabase();
		Cursor c = db.query(Configurations.table_Rezepte, new String[]{"*"}, Configurations.rezepte_Id + "=" + documentId, null, null, null, null);
		String query = "select * from " + Configurations.table_Rezept_to_Zutat + 
				" INNER JOIN " + Configurations.table_Rezepte + 
				" ON (" + Configurations.table_Rezepte+ "." + Configurations.rezepte_Id + 
						" = "+ Configurations.table_Rezept_to_Zutat + "." + Configurations.rezept_to_zutat_rezeptId+ ")" +
				" where " + Configurations.rezept_to_zutat_rezeptId + " = " + documentId;
		Cursor zutaten = db.rawQuery(query, null);
		/*
		 * select Zutaten.value from Zutaten, Rezept_Zutat, Rezepte JOIN Rezepte ON (Rezepte.ID = Rezept_Zutat.rezeptId)  JOIN Rezept_Zutat ON (Rezept_Zutat.zutatId = Zutat.ID) where Rezepte.ID = 1891913874
		 */
		c.moveToFirst();
		if(c.getCount() > 0){
			c.moveToFirst();
			Rezept rezept = new Rezept(c);
			rezept.setZutaten("zutat, zutat");
			rezept.setKategorien(new LinkedList<String>());
			return rezept;
		}
		return null;
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
}
