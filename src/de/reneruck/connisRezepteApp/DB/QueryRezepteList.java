package de.reneruck.connisRezepteApp.DB;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import de.reneruck.connisRezepteApp.Rezept;

/**
 * 
 * @author Rene
 *
 */
public class QueryRezepteList extends AsyncTask<Map<String, Object>, String, List<Rezept>> {

	private static final String TAG = "QueryDocumentList Task";
	private DatabaseQueryCallback listener;
	private DatabaseManager manager;

	@Override
	protected List<Rezept> doInBackground(Map<String, Object>... params) {
		Map<String, Object> param = params[0];
		String query = (String) param.get(DatabaseManager.QUERY);
		this.manager = (DatabaseManager)param.get(DatabaseManager.DB_MANAGER);
		this.listener = (DatabaseQueryCallback) param.get(DatabaseManager.CALLBACK);
		
		List<Rezept> rezepteList = new LinkedList<Rezept>();
		Cursor c = null;
		try {
			SQLiteDatabase db = manager.getDbHelper().getReadableDatabase();
			c = db.rawQuery(query, null);

			if (c.getCount() > 0) {
				for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
					Rezept rezept = new Rezept(c);
					rezepteList.add(rezept);
				}
			}
			c.close();
		} catch (SQLException e) {
			e.fillInStackTrace();
			if(c != null) c.close();
		}
		return rezepteList;
	}
	

	
	
	@Override
	protected void onPostExecute(List<Rezept> result) {
		this.listener.onSelectCallback(result);
	}

}
