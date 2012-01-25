package de.reneruck.connisRezepteApp;

import java.util.List;

public interface DatabaseCallback {

	/**
	 * Will be called when a Database Query is finished.<br>
	 * The Result will be returned as List of Objects it has been queried for.<br>
	 * WILL BE CALLED ONLY ON SELECT QUERIES
	 * 
	 * @param result
	 *            - a List of Results or null
	 */
	public void onsSelectCallback(List<?> result);
	
	/**
	 * Will be called when a Database query is finished.<br>
	 * Returns if the Store Query was successful or not
	 * 
	 * @param result
	 *            - true of Query was successful, false if not
	 */
	public void onStoreCallback(boolean result);
		
}
