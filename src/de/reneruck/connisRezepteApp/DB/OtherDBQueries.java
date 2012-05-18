package de.reneruck.connisRezepteApp.DB;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.reneruck.connisRezepteApp.Configurations;
import de.reneruck.connisRezepteApp.Rezept;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

public class OtherDBQueries extends AsyncTask<Map<String, Object>, Void, Void> {

	private DatabaseManager manager;

	@Override
	protected Void doInBackground(Map<String, Object>... params) {
		Map<String, Object> param = params[0];
		this.manager = (DatabaseManager)param.get(DatabaseManager.DB_MANAGER);
		return null;
	}

	private List<String> getAllKategorien() {
		 SQLiteDatabase db = this.manager.getDbHelper().getReadableDatabase();
		 Cursor query = db.query(Configurations.TABLE_KATEGORIEN, new String[]{Configurations.VALUE}, null, null, null, null, null);
		 List<String> results = new LinkedList<String>();
		 if(query.getCount() > 0){
			 for (query.moveToFirst(); query.isAfterLast(); query.moveToNext()){
				results.add(query.getString(0));
			}
		 }
		 query.close();
		 db.close();
		return results;
	}

	private List<String> getAllZubereitungen() {
		 SQLiteDatabase db = this.manager.getDbHelper().getReadableDatabase();
		 Cursor query = db.query(Configurations.TABLE_REZEPTE, new String[]{Configurations.FK_REZEPTE_ZUBEREITUNGSARTEN}, null, null, null, null, null);
		 List<String> results = new LinkedList<String>();
		 if(query.getCount() > 0){
			 for (query.moveToFirst(); query.isAfterLast(); query.moveToNext()){
				results.add(query.getString(0));
			}
		 }
		 query.close();
		 db.close();
		return results;
	}

	private void queryZutaten(SQLiteDatabase db, Rezept rezept) {
//		String query = "select " + Configurations.VALUE+ " from " + Configurations.table_Zutaten + ", " + Configurations.table_Rezept_to_Zutat
//				+ " where " + Configurations.table_Rezept_to_Zutat+ "." + Configurations.rezept_to_zutat_rezeptId + "=" + rezept.getId()
//				+ " and " + Configurations.table_Zutaten + "." + Configurations.zutaten_Id + " = " + Configurations.table_Rezept_to_Zutat + "." + Configurations.rezept_to_zutat_rezeptId;
//
//		Cursor c = db.rawQuery(query, null);
//		if(c.getCount() > 0){
//			
//		}
	}
}
