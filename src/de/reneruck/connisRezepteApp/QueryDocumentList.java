package de.reneruck.connisRezepteApp;

import java.util.HashMap;
import java.util.Map;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.ListView;

public class QueryDocumentList extends AsyncTask<Map<String, Object>, String, Map<Integer, String>> {

	private ListView listView;

	@Override
	protected Map<Integer, String> doInBackground(Map<String, Object>... params) {
		Map<String, Object> param = params[0];
		DBManager manager = (DBManager) param.get("dbManager");
		String query = (String) param.get("query");
		this.listView = (ListView) param.get("listView");
		
		Map<Integer, String> rezepteList = new HashMap<Integer, String>();
		try {
			SQLiteDatabase db = manager.getReadableDatabase();
			Cursor c = db.rawQuery(query, null);

			if (c.getCount() == 0) {
				rezepteList.put(-1, "Keine Rezepte gefunden");
			} else {
				c.moveToFirst();
				do {
					int id = c.getColumnIndex(Configurations.rezepte_Id);
					int name = c.getColumnIndex(Configurations.rezepte_Name);
					int doc = c.getColumnIndex(Configurations.rezepte_DocumentName);
					int path = c.getColumnIndex(Configurations.rezepte_PathToDocument);
					
					rezepteList.put(c.getInt(id),c.getString(name) + " - " + c.getString(path)+c.getString(doc));
				} while (c.moveToNext());
			}
			db.close();
		} catch (SQLException e) {
			e.fillInStackTrace();
		}
		
		return null;
	}
	
	@Override
	protected void onPostExecute(Map<Integer, String> result) {

		this.listView.setAdapter(new RezepteListAdapter(this.listView.getContext(), result));
//		this.listView.setOnItemClickListener(rezepteListEntyListener);
	}

}
