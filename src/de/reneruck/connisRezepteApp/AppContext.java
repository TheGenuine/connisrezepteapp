package de.reneruck.connisRezepteApp;

import android.app.Application;
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
	private DocumentsBean newDocumentsBean;
	private DatabaseManager dal;
	private int actualInfoItem = 0;
	
	@Override
	public void onCreate() {
		super.onCreate();
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

	public DatabaseManager getDatabaseAbstraction() {
		return dal;
	}

	public void setDatabaseAbstraction(DatabaseManager dal) {
		this.dal = dal;
	}

	public int getActualInfoItem() {
		return actualInfoItem;
	}

	public void setActualInfoItem(int actualInfoItem) {
		this.actualInfoItem = actualInfoItem;
	}
}
