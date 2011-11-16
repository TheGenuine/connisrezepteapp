package de.reneruck.connisRezepteApp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBManager extends SQLiteOpenHelper {

	
	
	public DBManager(Context context, String name, CursorFactory factory, int version) {
		super(context, Configurations.databaseName, factory,  Configurations.databaseVersion);
	}

	/**
	 * Im moment haben wir nur eine Tabelle:
	 * Rezepte:
	 *  - [int] RezeptId <primaryKey>
	 *  - [String] RezeptName (z.B. Mein tolles Rezept)
	 *  - [String] Document-name (z.B. Mein-Rezept.doc, ...)
	 *  - [String] Stichwörter (z.B. Karotte, Spargel, ...)
	 *  
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL("create table " + Configurations.table_Rezepte + " (" + Configurations.rezept_Id + " integer primary key autoincrement, "
				+ Configurations.rezept_Name+ "test not null, "
				+ Configurations.rezepteDirPath + "text not null, " 
				+ Configurations.rezepte_stichwörter + " text not null)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		db.execSQL("DROP TABLE " +Configurations.table_Rezepte + "");
		onCreate(db);
	}

}
