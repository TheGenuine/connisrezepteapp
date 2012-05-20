package de.reneruck.connisRezepteApp;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.map.HashedMap;

import android.app.Application;
import de.reneruck.connisRezepteApp.Configurations.ListType;
import de.reneruck.connisRezepteApp.DB.DatabaseHelper;
import de.reneruck.connisRezepteApp.DB.DatabaseManager;

/**
 * Application Context extension to hold beans and references for all Activities
 * 
 * @author Rene
 * 
 */
public class AppContext extends Application {

	private DatabaseHelper manager;
	private DocumentsBean newDocumentsBean = new DocumentsBean();
	private DatabaseManager databaseManager;
	private int actualInfoItem = 0;
	private ChooserListCallback chooserListCallback;
	private Map<ListType, Set<String>> rawQuery = new HashMap<Configurations.ListType, Set<String>>();
	
	@Override
	public void onCreate() {
		super.onCreate();
		this.databaseManager = new DatabaseManager(this);
	}

	public DatabaseHelper getManager() {
		return manager;
	}

	public void setManager(DatabaseHelper manager) {
		this.manager = manager;
	}

	public DatabaseHelper getDBManager() {
		return this.manager;
	}

	public DocumentsBean getDocumentsBean() {
		return this.newDocumentsBean;
	}

	public void setDocumentsBean(DocumentsBean newDocumentsBean) {
		this.newDocumentsBean = newDocumentsBean;
	}

	public DatabaseManager getDatabaseManager() {
		return databaseManager;
	}

	public int getActualInfoItem() {
		return actualInfoItem;
	}

	public void setActualInfoItem(int actualInfoItem) {
		this.actualInfoItem = actualInfoItem;
	}

	public ChooserListCallback getChooserListCallback() {
		return this.chooserListCallback;
	}
	
	public void setChooserListCallback(ChooserListCallback callback) {
		this.chooserListCallback = callback;
	}

	public void addToQuery(ListType displayListType, String value) {
		
		if (!this.rawQuery.containsKey(displayListType)) {
			this.rawQuery.put(displayListType, new LinkedHashSet<String>());
		}
		if(ListType.Zeit.equals(displayListType)) {
			LinkedHashSet<String> timeHashSet = new LinkedHashSet<String>();
			timeHashSet.add(value);
			this.rawQuery.put(displayListType, timeHashSet);
		} else {
			this.rawQuery.get(displayListType).add(value);
		}
	}
	
	public String rawQueryToString() {
		StringBuilder queryBuilder = new StringBuilder("select * from " + Configurations.TABLE_REZEPTE + "where ");
		for (ListType type : this.rawQuery.keySet()) {
			switch (type) {
			case Kategorie:
				queryBuilder.append(buildWhere(type, Configurations.TABLE_KATEGORIEN) + "AND ");				
				break;
			case Zubereitungsart:
				queryBuilder.append(buildWhere(type, Configurations.TABLE_ZUBEREITUNGSARTEN) + "AND ");				
				break;
			case Zutat:
				queryBuilder.append(buildWhere(type, Configurations.TABLE_ZUTATEN) + "AND ");				
				break;
			case Zeit:
				String[] values = this.rawQuery.get(type).toArray(new String[]{});
				queryBuilder.append("where " + Configurations.TABLE_REZEPTE + "." + Configurations.ZEIT + "=" + values[0] +" AND ");				
				break;
			default:
				break;
			}
		}
		String queryString = queryBuilder.toString();
		if(queryString.endsWith(" AND ")) {
			queryString = queryString.substring(0, queryString.length()-5);
		}
		return queryString;
	}

	private String buildWhere(ListType type, String table) {
		String[] values = this.rawQuery.get(type).toArray(new String[]{});
		StringBuilder where = new StringBuilder();
		for (int i = 0; i < values.length; i++) {
			String value = values[i]; 
			where.append(table + "." + Configurations.VALUE + "=" + value);
			if(i+1 < values.length) {
				where.append(" OR ");
			}
		}
	return where.toString();	
	}

	public void removeFromQuery(ListType displayListType, String value) {
		this.rawQuery.get(displayListType).remove(value);
	}

	public boolean isSet(ListType key) {
		return this.rawQuery.containsKey(key);
	}

	public Set<String> getEntriesFromRawQuery(ListType key) {
		return this.rawQuery.get(key);
	}
}
