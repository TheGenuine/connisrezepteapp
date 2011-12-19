package de.reneruck.connisRezepteApp;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.reneruck.connisRezepteApp.data.Rezept;
import de.reneruck.connisRezepteApp.db.DBManager;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class QueryDocumentList extends AsyncTask<Map<String, Object>, String, List<Rezept>> {

	private ListView listView;
	private OnItemClickListener listener;

	@Override
	protected List<Rezept> doInBackground(Map<String, Object>... params) {
		Map<String, Object> param = params[0];
		DBManager manager = (DBManager) param.get("dbManager");
		String query = (String) param.get("query");
		this.listener = (OnItemClickListener) param.get("listener");
		this.listView = (ListView) param.get("listView");
		
		List<Rezept> rezepteList = new LinkedList<Rezept>();
		try {
			SQLiteDatabase db = manager.getReadableDatabase();
			Cursor c = db.rawQuery(query, null);

			if (c.getCount() == 0) {
				rezepteList.add(new Rezept("Keine Rezepte gefunden"));
			} else {
				c.moveToFirst();
				do {
					Rezept rezept = new Rezept(c);
					
					queryZutaten(db, rezept);
					queryKateorien(db, rezept);
					
					rezepteList.add(rezept);
				} while (c.moveToNext());
			}
			db.close();
		} catch (SQLException e) {
			e.fillInStackTrace();
		}
		
		return rezepteList;
	}
	

	private void queryZutaten(SQLiteDatabase db, Rezept rezept) {
		String query = "select " + Configurations.zutaten_value+ " from " + Configurations.table_Zutaten + ", " + Configurations.table_Rezept_to_Zutat
				+ " where " + Configurations.table_Rezept_to_Zutat+ "." + Configurations.rezept_to_zutat_rezeptId + "=" + rezept.getId()
				+ " and " + Configurations.table_Zutaten + "." + Configurations.zutaten_Id + " = " + Configurations.table_Rezept_to_Zutat + "." + Configurations.rezept_to_zutat_rezeptId;

		Cursor c = db.rawQuery(query, null);
		if(c.getCount() > 0){
			
		}
	}
	
	private void queryKateorien(SQLiteDatabase db, Rezept rezept) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onPostExecute(List<Rezept> result) {

		this.listView.setAdapter(new RezepteListAdapter(this.listView.getContext(), result));
		this.listView.setOnItemClickListener(this.listener);
//		this.listView.setOnItemClickListener(rezepteListEntyListener);
	}

}
