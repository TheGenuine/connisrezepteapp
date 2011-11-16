package de.reneruck.connisRezepteApp;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends Activity {

	private static Context context;
	private List<String> rezepteList;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getApplicationContext();
        
        setContentView(R.layout.main);
        
        new FileScanner().doInBackground();
        
        DBManager manager = new DBManager(getApplicationContext(), Configurations.databaseName, null, Configurations.databaseVersion);
		final SQLiteDatabase db = manager.getReadableDatabase();
		
		Cursor c = db.rawQuery("select * from " +Configurations.table_Rezepte+ "", null);
		
		this.rezepteList = new LinkedList<String>();
		if(c.getCount() == 0){
			this.rezepteList.add("Keine Rezepte gefunden");
		}else{
			do {
				int name = c.getColumnIndex(Configurations.rezept_Name);
				int doc = c.getColumnIndex(Configurations.rezepteDirPath);
				
				rezepteList.add(c.getString(name) + " - " + c.getString(doc));
			} while (c.moveToNext());
		}
		
		ListView lv = (ListView) findViewById(R.id.listView);
		lv.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.rezeptelistentry, this.rezepteList));
		lv.setOnItemClickListener(this.rezepteListEntyListener);
    }

    OnItemClickListener rezepteListEntyListener = new OnItemClickListener() {
		
		@Override
		public void onItemClick(AdapterView<?> adapter, View view, int arg2, long arg3) {
			Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
					Toast.LENGTH_SHORT).show();			
		}
	};
	public static Context getContext() {
		return context;
	}

}