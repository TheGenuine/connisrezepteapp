package de.reneruck.connisRezepteApp;

import android.app.Application;
import de.reneruck.connisRezepteApp.DB.DBManager;
import de.reneruck.connisRezepteApp.DB.DatabaseAbstraction;

/**
 * Application Context extension to hold beans and references for all Activities
 * 
 * @author Rene
 * 
 */
public class AppContext extends Application {

	private DBManager manager;
	private DocumentsBean newDocumentsBean;
	private DatabaseAbstraction dal;
	
	@Override
	public void onCreate() {
		super.onCreate();
	}

	public DBManager getManager() {
		return manager;
	}

	public void setManager(DBManager manager) {
		this.manager = manager;
	}

	public DBManager getDBManager() {
		return this.manager;
	}

	public DocumentsBean getDocumentsBean() {
		return this.newDocumentsBean;
	}

	public void setDocumentsBean(DocumentsBean newDocumentsBean) {
		this.newDocumentsBean = newDocumentsBean;
	}

	public DatabaseAbstraction getDatabaseAbstraction() {
		return dal;
	}

	public void setDatabaseAbstraction(DatabaseAbstraction dal) {
		this.dal = dal;
	}
}
