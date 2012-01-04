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
				" JOIN " + Configurations.rezept_to_zutat_rezeptId + " ON (" + Configurations.table_Rezepte + "." + Configurations.rezepte_Id + ")" +
				"  where " + Configurations.rezept_to_zutat_rezeptId + " = " + documentId;
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
