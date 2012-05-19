package de.reneruck.connisRezepteApp.DB;

import de.reneruck.connisRezepteApp.Configurations;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String CREATE_REZEPTE_DB = "CREATE  TABLE IF NOT EXISTS `" + Configurations.TABLE_REZEPTE + "` ( " +
		  "`" + Configurations.ID_REZEPTE + "` INTEGER  PRIMARY_KEY AUTO_INCREMENT ," +
		  "`" + Configurations.NAME + "` TEXT NULL ," +
		  "`" + Configurations.DOCUMENT_NAME + "` TEXT NULL ," +
		  "`" + Configurations.DOCUMENT_HASH + "` INTEGER NULL ," +
		  "`" + Configurations.ZUBEREITUNGSARTEN_ID_ZUBEREITUNGSART + "` INTEGER NULL ," +
		  "`" + Configurations.ZEIT + "` INTEGER NULL ," +
		  "`" + Configurations.KATEGORIE_ID_KATEGORIE + "` INTEGER NULL ," +
		 " CONSTRAINT `" + Configurations.FK_REZEPTE_ZUBEREITUNGSARTEN + "`" +
		   " FOREIGN KEY (`" + Configurations.ZUBEREITUNGSARTEN_ID_ZUBEREITUNGSART + "` )" +
		   " REFERENCES `" + Configurations.TABLE_ZUBEREITUNGSARTEN + "` (`" + Configurations.ID_ZUBEREITUNGSART + "` )" +
		   " ON DELETE NO ACTION" +
		   " ON UPDATE NO ACTION," +
		 " CONSTRAINT `" + Configurations.FK_REZEPTE_KATEGORIE1 + "`" +
		  "  FOREIGN KEY (`" + Configurations.KATEGORIE_ID_KATEGORIE + "` )" +
		  "  REFERENCES `" + Configurations.TABLE_KATEGORIEN + "` (`" + Configurations.ID_KATEGORIE + "` )" +
		   " ON DELETE NO ACTION" +
		  "  ON UPDATE NO ACTION)";

	private static final String CREATE_KATEGORIEN_DB = "CREATE  TABLE IF NOT EXISTS `" + Configurations.TABLE_KATEGORIEN + "` " +
			"(   `" + Configurations.ID_KATEGORIE + "` INTEGER  PRIMARY_KEY AUTO_INCREMENT , " +
			" `" + Configurations.VALUE + "` TEXT NULL )";

	private static final String CREATE_ZUBEREITUNGSART_DB = "CREATE  TABLE IF NOT EXISTS `" + Configurations.TABLE_ZUBEREITUNGSARTEN + "`" +
			" (`" + Configurations.ID_ZUBEREITUNGSART + "` INTEGER  PRIMARY_KEY AUTO_INCREMENT ," +
			"`" + Configurations.VALUE + "` TEXT NULL )";

	private static final String CREATE_ZUTATEN_DB = "CREATE  TABLE IF NOT EXISTS `" + Configurations.TABLE_ZUTATEN + "` " +
		 "( " + " `" + Configurations.ID_ZUTATEN + "` INTEGER PRIMARY_KEY AUTO_INCREMENT ," +
		 " `" + Configurations.VALUE + "` TEXT NULL ," +
		 " `" + Configurations.ZUTATEN_KATEGORIE_ID_ZUTATEN_KATEGORIE + "` INTEGER NOT NULL ," +
		 " CONSTRAINT `" + Configurations.FK_ZUTATEN_ZUTATEN_KATEGORIE1 + "`" +
		 "   FOREIGN KEY (`" + Configurations.ZUTATEN_KATEGORIE_ID_ZUTATEN_KATEGORIE + "` )" +
		 "   REFERENCES `" + Configurations.TABLE_ZUTATEN_KATEGORIE + "` (`" + Configurations.ID_ZUTATEN_KATEGORIE + "` )" +
		 "   ON DELETE NO ACTION" +
		 "   ON UPDATE NO ACTION)";

	private static final String CREATE_ZUTATEN_KATEGORIE_DB = "	CREATE  TABLE IF NOT EXISTS `" + Configurations.TABLE_ZUTATEN_KATEGORIE + "`" +
			" (`" + Configurations.ID_ZUTATEN_KATEGORIE + "` INTEGER  PRIMARY_KEY AUTO_INCREMENT ," +
			"`" + Configurations.VALUE + "` TEXT NULL)";

	private static final String CREATE_REZEPT_HAS_ZUTATE_DB = "CREATE  TABLE IF NOT EXISTS `" + Configurations.TABLE_REZEPTE_HAS_ZUTATEN + "` ( " +
		  "`" + Configurations.REZEPTE_ID_REZEPTE + "` INTEGER NOT NULL ," +
		  "`" + Configurations.ZUTATEN_ID_ZUTATEN + "` INTEGER NOT NULL ," +
		 "PRIMARY KEY (`" + Configurations.REZEPTE_ID_REZEPTE + "`, `" + Configurations.ZUTATEN_ID_ZUTATEN + "`) ," +
		 " CONSTRAINT `" + Configurations.FK_REZEPTE_HAS_ZUTATEN_REZEPTE1 + "`" +
		 "   FOREIGN KEY (`" + Configurations.REZEPTE_ID_REZEPTE + "` )" +
		 "   REFERENCES `" + Configurations.TABLE_REZEPTE + "` (`" + Configurations.ID_REZEPTE + "` )" +
		 "   ON DELETE NO ACTION" +
		 "   ON UPDATE NO ACTION," +
		 " CONSTRAINT `" + Configurations.FK_REZEPTE_HAS_ZUTATEN_ZUTATEN1 + "`" +
		 "   FOREIGN KEY (`" + Configurations.ZUTATEN_ID_ZUTATEN + "` )" +
		 "   REFERENCES `" + Configurations.TABLE_ZUTATEN + "` (`" + Configurations.ID_ZUTATEN + "` )" +
		 "   ON DELETE NO ACTION" +
		 "   ON UPDATE NO ACTION)"; 

	public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, Configurations.databaseName, factory,  Configurations.databaseVersion);
	}

	

	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			System.out.println(CREATE_REZEPTE_DB);
			db.execSQL(CREATE_REZEPTE_DB);
			
			System.out.println(CREATE_ZUBEREITUNGSART_DB);
			db.execSQL(CREATE_ZUBEREITUNGSART_DB);
			
			System.out.println(CREATE_KATEGORIEN_DB);
			db.execSQL(CREATE_KATEGORIEN_DB);
			
			System.out.println(CREATE_ZUTATEN_KATEGORIE_DB);
			db.execSQL(CREATE_ZUTATEN_KATEGORIE_DB);
			
			System.out.println(CREATE_ZUTATEN_DB);
			db.execSQL(CREATE_ZUTATEN_DB);
			
			System.out.println(CREATE_REZEPT_HAS_ZUTATE_DB);
			db.execSQL(CREATE_REZEPT_HAS_ZUTATE_DB);
			
			setupKategorien(db);
			setupZubereitungsart(db);
			setupZutatenKategorie(db);
			setupZutaten(db);

		} catch (SQLException e) {
			System.err.println(e);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		/*
		 * TODO: Here should happen a data migration!!  
		 */
		db.execSQL("DROP TABLE IF EXISTS " + Configurations.TABLE_REZEPTE + "");
		db.execSQL("DROP TABLE IF EXISTS " + Configurations.TABLE_ZUTATEN + "");
		db.execSQL("DROP TABLE IF EXISTS " + Configurations.TABLE_KATEGORIEN + "");
		db.execSQL("DROP TABLE IF EXISTS " + Configurations.TABLE_REZEPTE_HAS_ZUTATEN + "");
		db.execSQL("DROP TABLE IF EXISTS " + Configurations.TABLE_ZUBEREITUNGSARTEN + "");
		db.execSQL("DROP TABLE IF EXISTS " + Configurations.TABLE_ZUTATEN_KATEGORIE + "");
		onCreate(db);
	}
	
	private void setupKategorien(SQLiteDatabase db) {
		for (String value : Configurations.Kategorien) {
			db.execSQL("INSERT INTO " + Configurations.TABLE_KATEGORIEN + " (" + Configurations.VALUE + ") values('" + value +"')");
		}
	}
	
	private void setupZubereitungsart(SQLiteDatabase db) {
		for (String value : Configurations.Zubereitungsart) {
			db.execSQL("INSERT INTO " + Configurations.TABLE_ZUBEREITUNGSARTEN + " (" + Configurations.VALUE + ") values('" + value +"')");
		}
	}
	
	private void setupZutatenKategorie(SQLiteDatabase db) {
		for (String value : Configurations.ZutatenKategorie) {
			db.execSQL("INSERT INTO " + Configurations.TABLE_ZUTATEN_KATEGORIE + " (" + Configurations.VALUE + ") values('" + value +"')");
		}
	}
	
	private void setupZutaten(SQLiteDatabase db) {
		for (String value : Configurations.ZutatenFleisch) {
			db.execSQL("INSERT INTO " + Configurations.TABLE_ZUTATEN + " (" + Configurations.VALUE + ", " + Configurations.ZUTATEN_KATEGORIE_ID_ZUTATEN_KATEGORIE + ") values('" + value +"' , 1)");
		}
		
		for (String value : Configurations.ZutatenMilchprodukete) {
			db.execSQL("INSERT INTO " + Configurations.TABLE_ZUTATEN + " (" + Configurations.VALUE + ", " + Configurations.ZUTATEN_KATEGORIE_ID_ZUTATEN_KATEGORIE + ") values('" + value +"' , 5)");
		}
	}
	
	
}
