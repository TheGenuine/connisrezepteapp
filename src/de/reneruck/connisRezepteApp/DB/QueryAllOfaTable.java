package de.reneruck.connisRezepteApp.DB;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import de.reneruck.connisRezepteApp.Configurations;
import de.reneruck.connisRezepteApp.Configurations.ListType;

public class QueryAllOfaTable extends AsyncTask<Map<String, Object>, Void, Object> {

	private DatabaseManager manager;
	private DatabaseQueryCallback listener;

	@Override
	protected Object doInBackground(Map<String, Object>... params) {
		Map<String, Object> param = params[0];
		ListType type = (ListType) param.get(DatabaseManager.QUERY);
		this.manager = (DatabaseManager)param.get(DatabaseManager.DB_MANAGER);
		this.listener = (DatabaseQueryCallback) param.get(DatabaseManager.CALLBACK);
		
		switch (type) {
			case Kategorie:
				return getAllKategorien();
			case Zubereitungsart:
				return getAllZubereitungen();
			case ZutatKategorie:
				break;
			case Zutat:
				return getAllZutatenForZutatenKategorie();
		}
		return new LinkedList<String>();
	}
	
	@Override
	protected void onPostExecute(Object result) {
		if(result instanceof Map<?, ?>){
			((DatabaseZutatenQueryCallback)this.listener).onSelectCallback((Map<String, List<String>>) result);
		} else {
			this.listener.onSelectCallback((List<?>) result);
		}
		super.onPostExecute(result);
	}

	private List<String> getAllKategorien() {
		 SQLiteDatabase db = this.manager.getDbHelper().getReadableDatabase();
		 Cursor query = db.query(Configurations.TABLE_KATEGORIEN, new String[]{Configurations.VALUE}, null, null, null, null, null);
		 List<String> results = new LinkedList<String>();
		 if(query.getCount() > 0){
			 for (query.moveToFirst(); !query.isAfterLast(); query.moveToNext()){
				results.add(query.getString(0));
			}
		 }
		 query.close();
		 db.close();
		return results;
	}

	private List<String> getAllZubereitungen() {
		 SQLiteDatabase db = this.manager.getDbHelper().getReadableDatabase();
		 Cursor query = db.query(Configurations.TABLE_ZUBEREITUNGSARTEN, new String[]{Configurations.VALUE}, null, null, null, null, null);
		 List<String> results = new LinkedList<String>();
		 if(query.getCount() > 0){
			 for (query.moveToFirst(); !query.isAfterLast(); query.moveToNext()){
				results.add(query.getString(0));
			}
		 }
		 query.close();
		 db.close();
		return results;
	}

	private Map<String, List<String>> getAllZutatenForZutatenKategorie() {
		SQLiteDatabase db = this.manager.getDbHelper().getReadableDatabase();
		Map<String, Set<String>> result = new HashMap<String, Set<String>>();
		
		Cursor allZutaten = db.rawQuery("Select  Zutaten.value,  Zutaten_Kategorie.value As kategorie From  Zutaten " +
				"Inner Join  Zutaten_Kategorie On Zutaten_Kategorie.idZutaten_Kategorie = Zutaten.Zutaten_Kategorie_idZutaten_Kategorie Order By  kategorie", null);
		 if(allZutaten.getCount() > 0){
			 for (allZutaten.moveToFirst(); !allZutaten.isAfterLast(); allZutaten.moveToNext()){
				 String zutatenKategorie = allZutaten.getString(1);
				Set<String> set = result.get(zutatenKategorie);
				 if(set == null) {
					 set = new LinkedHashSet<String>();
					 result.put(zutatenKategorie, set);
				 }
				 set.add(allZutaten.getString(0));
			}
		 }
		allZutaten.close();
		db.close();
		return repackMap(result);
	}
	
	private Map<String, List<String>> repackMap(Map<String, Set<String>> map) {
		Map<String, List<String>> result = new HashMap<String, List<String>>();
		Set<String> keySet = result.keySet();
		for (String key : keySet) {
			result.put(key, new LinkedList<String>(map.get(key)));
		}
		return result;
	}
}
