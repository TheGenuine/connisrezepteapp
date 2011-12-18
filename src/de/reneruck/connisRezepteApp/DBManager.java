package de.reneruck.connisRezepteApp;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class DBManager extends OrmLiteSqliteOpenHelper {

	private static final String TAG = "DBManager";
	private Dao<Rezept, Integer> rezeptDao = null;
	
	public DBManager(Context context, String name, CursorFactory factory, int version) {
		super(context, Configurations.databaseName, factory,  Configurations.databaseVersion);
	}

	@Override
	public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
		try {
			TableUtils.createTable(connectionSource, Rezept.class);
		} catch (SQLException e) {
			Log.e(TAG, e.getLocalizedMessage());
		}
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
			TableUtils.dropTable(connectionSource, Rezept.class, false);
			onCreate(database, connectionSource);
		} catch (SQLException e) {
			Log.e(TAG, e.getLocalizedMessage());
		}
	}
	
	/**
	 * Returns the Database Access Object (DAO) for our SimpleData class. It will create it or just give the cached
	 * value.
	 */
	public Dao<Rezept, Integer> getDao() throws SQLException {
		if (this.rezeptDao == null) {
			this.rezeptDao = getDao(Rezept.class);
		}
		return rezeptDao;
	}

}
