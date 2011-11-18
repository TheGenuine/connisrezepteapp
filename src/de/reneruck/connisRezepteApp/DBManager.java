package de.reneruck.connisRezepteApp;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBManager extends SQLiteOpenHelper {

	private static final String CREATE_REZEPTE_DB = "create table " + Configurations.table_Rezepte + " (" + Configurations.rezept_Id + " integer primary key autoincrement, "
			+ Configurations.rezept_Name+ " text not null, "
			+ Configurations.rezept_DocumentPath + " text not null, " 
			+ Configurations.rezepte_stichwoerter + " text);"; 
	private static final String CREATE_KEYWODS_DB = "create table " + Configurations.table_Keywords + " (" + Configurations.keywords_Id + " integer primary key autoincrement, "
			+ Configurations.keywords_keyword + " text not null);"; 
	
	public DBManager(Context context, String name, CursorFactory factory, int version) {
		super(context, Configurations.databaseName, factory,  Configurations.databaseVersion);
	}

	/**
	 * Im moment haben wir nur eine Tabelle:
	 * Rezepte:
	 *  - [int] RezeptId <primaryKey>
	 *  - [String] RezeptName (z.B. Mein tolles Rezept)
	 *  - [String] Document-name (z.B. Mein-Rezept.doc, ...)
	 *  - [String] Stichw�örter (z.B. Karotte, Spargel, ...)
	 *  
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			db.execSQL(CREATE_REZEPTE_DB);
			db.execSQL(CREATE_KEYWODS_DB);
		} catch (SQLException e) {
			System.err.println(e);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		db.execSQL("DROP TABLE IF EXISTS " +Configurations.table_Rezepte + "");
		db.execSQL("DROP TABLE IF EXISTS " +Configurations.table_Keywords + "");
		onCreate(db);
	}

}
