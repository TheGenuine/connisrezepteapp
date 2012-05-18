package de.reneruck.connisRezepteApp.development;

import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;

public class DatabaseOverview extends Activity implements TabListener {
//
//	private DatabaseHelper dbManager;
//    private String BACKUP_DIR = "/sdcard/ConnisRezepteApp/";
//
	@Override
	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		
//		setContentView(R.layout.debug_database_overview);
//		setUpActionbar();
//		this.dbManager = ((AppContext) getApplicationContext()).getDBManager();
	}
//	
//	private void setUpActionbar() {
//
//		ActionBar actionBar = getActionBar();
//		actionBar.setDisplayHomeAsUpEnabled(true);
//		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
//
//		Tab tab = actionBar.newTab().setText("Rezepte").setTabListener(this);
//		actionBar.addTab(tab);
//		Tab tab2 = actionBar.newTab().setText("Andere Tabellen").setTabListener(this);
//		actionBar.addTab(tab2);
//		
//
//	}
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//	    MenuInflater inflater = getMenuInflater();
//	    inflater.inflate(R.menu.debug_actionbar_menu, menu);
//	    return true;
//	}
//
//	private void importDBFile(String string) {
//		final String input = string;
//		
//		AlertDialog.Builder builder = new AlertDialog.Builder(this);
//		builder.setMessage(R.string.import_database)
//		       .setCancelable(false)
//		       .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
//				public void onClick(DialogInterface dialog, int id) {
//		        	   String trim = input.split(" - ")[1].replace("(", " ").replace(")", " ").trim();
//		        	   if(trim.endsWith(".backup")) trim = trim.replace(".backup", " ").trim();
//		        	   File backupFile = new File(BACKUP_DIR + trim);
//		        	   if(backupFile.exists() && backupFile.getName().endsWith(".db") && !backupFile.isDirectory()){
//		        		   File dbfile_toRemove = new File("/data/data/de.reneruck.connisRezepteApp/databases/rezepte.db");
//		        		   dbfile_toRemove.delete();
//		        		   Utils.copyFile(backupFile, dbfile_toRemove);
//		        	   }
//		                
//		           }
//		       })
//		       .setNegativeButton("Nein", new DialogInterface.OnClickListener() {
//		           public void onClick(DialogInterface dialog, int id) {
//		                dialog.cancel();
//		           }
//		       })
//		       .create().show();		
//	}
//	
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//	    switch (item.getItemId()) {
//	        case android.R.id.home:
//	            Intent intent = new Intent(this, Main.class);
//	            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//	            startActivity(intent);
//	            return true;
//	            
//	    	case R.id.menu_debug_copydb:
//	    		File dbfile = new File("/data/data/de.reneruck.connisRezepteApp/databases/rezepte.db");
//	    		File copiedFile = new File(BACKUP_DIR + "rezepte-"+System.currentTimeMillis()+".db");
//	        	File backupDir = new File(BACKUP_DIR);
//	        	if(!backupDir.exists()) backupDir.mkdirs(); 
//	    		Utils.copyFile(dbfile, copiedFile);
//					
//	            return true;
//	            
//	    	case R.id.menu_debug_importdb:
//	    		
//	    		File importFolder = new File(BACKUP_DIR);
//	    		boolean noFiles = false;
//	    		if(importFolder.isDirectory()){
//	    			String[] files = importFolder.list();
//	    			final List<String> items = new LinkedList<String>();
//	    			for (int i = 0; i < files.length; i++) {
//						if(files[i].matches("rezepte-(\\d+).db") || files[i].matches("rezepte-(\\d+).db.backup")){
//							String replace = files[i].replace("rezepte-", " ").replace(".db", " ").trim();
//				        	   if(replace.endsWith(".backup")) replace = replace.replace(".backup", " ").trim();
//							items.add((String) DateFormat.format("dd.MM.yyyy hh:mm", new Date(Long.decode(replace))) 
//									+ " - (" + files[i] + ") ");
//						}
//					}
//	    			
//	    			if(items.size() == 0) {
//	    				items.add("Keine Exportierten Datenbanken gefunden");
//	    				noFiles = true;
//	    			}
//	    			AlertDialog.Builder builder = new AlertDialog.Builder(this);
//	    			builder.setTitle("Exportierte Datenbank Importieren");
//	    			if(noFiles) {
//	    				builder.setItems(Arrays.copyOf(items.toArray(), items.size(), String[].class), new DialogInterface.OnClickListener() {
//	    					public void onClick(DialogInterface dialog, int item) {
//	    					}
//	    				});
//	    				
//	    			} else {
//	    				builder.setItems(Arrays.copyOf(items.toArray(), items.size(), String[].class), new DialogInterface.OnClickListener() {
//	    					public void onClick(DialogInterface dialog, int item) {
//	    						importDBFile(items.get(item));
//	    					}
//	    				});
//	    			}
//	    			builder.create().show();
//	    		}
//
//	    		return true;
//	    		
//	        case R.id.menu_debug_cleandb:
//	        	try {
//	        		AlertDialog.Builder builder = new AlertDialog.Builder(this);
//	        		builder.setMessage("Are you sure you want to clear the Database?")
//	        		       .setCancelable(false)
//	        		       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//	        		           public void onClick(DialogInterface dialog, int id) {
//	        		                File dbfile_toRemove = new File("/data/data/de.reneruck.connisRezepteApp/databases/rezepte.db");
//	        		                dbfile_toRemove.delete();
//	        		           }
//	        		       })
//	        		       .setNegativeButton("No", new DialogInterface.OnClickListener() {
//	        		           public void onClick(DialogInterface dialog, int id) {
//	        		                dialog.cancel();
//	        		           }
//	        		       })
//	        		       .create().show();
//	        		
//				} catch (SQLException e) {
//						e.printStackTrace();
//				}
//	        	return true;
//	        default:
//	            return super.onOptionsItemSelected(item);
//	    }
//	}
//
	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {

		if (tab.getText().toString().equals("Andere Tabellen")) {
			
		} else {
			
		}
	}
	
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
////		((ViewGroup)findViewById(R.id.debug_scroll)).removeAllViews();
//		
//		if (tab.getText().toString().equals("Andere Tabellen")) {
//			setContentView(R.layout.debug_database_others);
//			showSimple(Configurations.table_Kategorien, R.id.debug_table_kategorien);
//			showSimple(Configurations.table_Zutaten, R.id.debug_table_zutaten);
//			show3parted(Configurations.table_Rezept_to_Kategorie, Configurations.rezept_to_kategorie_kategorieId, R.id.debug_table_rezept_to_kategorien);
//			show3parted(Configurations.table_Rezept_to_Zutat, Configurations.rezept_to_zutat_zutatId, R.id.debug_table_rezept_to_zutat);
//			
//		} else if(tab.getText().toString().equals("Rezepte")) {
//			showRezepte();
//		}
	}
//
//
//	private void show3parted(String table, String valueId, int parentView) {
//
//		if(this.dbManager == null){
//			this.dbManager = ((AppContext) getApplicationContext()).getDBManager();
//		}
//		SQLiteDatabase db = this.dbManager.getReadableDatabase();
//		Cursor query = db.query(table, new String[] { "*" }, null, null, null, null, Configurations.ID);
//		
//		if (query.getCount() > 0) {
//			
//			int index_id = query.getColumnIndex(Configurations.ID);
//			int index_id2 = query.getColumnIndex("rezeptId");
//			int index_id3 = query.getColumnIndex(valueId);
//			
//			for (query.moveToFirst(); !query.isAfterLast(); query.moveToNext()) {
//				
//				int id = query.getInt(index_id);
//				int id2 = query.getInt(index_id2);
//				int id3 = query.getInt(index_id3);
//				
//				View inflated_row = getLayoutInflater().inflate(R.layout.debug_db_row_three_parts, (ViewGroup) findViewById(parentView), false);
//				((TextView) inflated_row.findViewById(R.id.field_id)).setText(String.valueOf(id));
//				((TextView) inflated_row.findViewById(R.id.field_id2)).setText(String.valueOf(id2));
//				((TextView) inflated_row.findViewById(R.id.field_id3)).setText(String.valueOf(id3));
//				((TableLayout)findViewById(parentView)).addView(inflated_row);
//			}
//		}
//		db.close();		
//		
//	}
//
//	private void showSimple(String table, int parentView) {
//		
//		if(this.dbManager == null){
//			this.dbManager = ((AppContext) getApplicationContext()).getDBManager();
//		}		SQLiteDatabase db = this.dbManager.getReadableDatabase();
//		Cursor query = db.query(table, new String[] { "*" }, null, null, null, null, Configurations.ID);
//		
//		if (query.getCount() > 0) {
//			
//			int index_id = query.getColumnIndex(Configurations.ID);
//			int index_name = query.getColumnIndex("value");
//			
//			for (query.moveToFirst(); !query.isAfterLast(); query.moveToNext()) {
//				
//				int id = query.getInt(index_id);
//				String name = query.getString(index_name);
//				
//				View inflated_row = getLayoutInflater().inflate(R.layout.debug_db_row_simple, (ViewGroup) findViewById(parentView), false);
//				((TextView) inflated_row.findViewById(R.id.field_id)).setText(String.valueOf(id));
//				((TextView) inflated_row.findViewById(R.id.field_value)).setText(name);
//				((TableLayout)findViewById(parentView)).addView(inflated_row);
//			}
//		}
//		db.close();		
//	}
//
//	private void showRezepte() {
//		
//		setContentView(R.layout.debug_database_overview);
//		if(this.dbManager == null){
//			this.dbManager = ((AppContext) getApplicationContext()).getDBManager();
//		}
//		SQLiteDatabase db = this.dbManager.getReadableDatabase();
//		Cursor query = db.query(Configurations.table_Rezepte, new String[] { "*" }, null, null, null, null, Configurations.rezepte_Id);
//
//		if (query.getCount() > 0) {
//
//			int index_id = query.getColumnIndex(Configurations.rezepte_Id);
//			int index_name = query.getColumnIndex(Configurations.rezepte_Name);
//			int index_docName = query.getColumnIndex(Configurations.rezepte_DocumentName);
//			int index_docHash = query.getColumnIndex(Configurations.rezepte_DocumentHash);
//			int index_documentPath = query.getColumnIndex(Configurations.rezepte_PathToDocument);
//			int index_zubereitung = query.getColumnIndex(Configurations.rezepte_Zubereitungsart);
//			int index_zeit = query.getColumnIndex(Configurations.rezepte_Zeit);
//			for (query.moveToFirst(); !query.isAfterLast(); query.moveToNext()) {
//
//				int id = query.getInt(index_id);
//				String name = query.getString(index_name);
//				String docName = query.getString(index_docName);
//				int hash = query.getInt(index_docHash);
//				String path = query.getString(index_documentPath);
//				String zubereitung = query.getString(index_zubereitung);
//				int zeit = query.getInt(index_zeit);
//
//				View inflated_row = getLayoutInflater().inflate(R.layout.debug_db_table_row, (ViewGroup) findViewById(R.id.table_rezepte), false);
//				((TextView) inflated_row.findViewById(R.id.field_id)).setText(String.valueOf(id));
//				((TextView) inflated_row.findViewById(R.id.field_rezept_name)).setText(name);
//				((TextView) inflated_row.findViewById(R.id.field_document_name)).setText(docName);
//				((TextView) inflated_row.findViewById(R.id.field_hash)).setText(String.valueOf(hash));
//				((TextView) inflated_row.findViewById(R.id.field_document_path)).setText(path);
//				((TextView) inflated_row.findViewById(R.id.field_zubereitung)).setText(zubereitung);
//				((TextView) inflated_row.findViewById(R.id.field_zeit)).setText(String.valueOf(zeit));
//				((TableLayout)findViewById(R.id.table_rezepte)).addView(inflated_row);
//			}
//		}
//		db.close();		
//	}
}
