package de.reneruck.connisRezepteApp.DB;


public interface DatabaseStorageCallback extends DatabaseCallback{
	
	/**
	 * Will be called when a Database query is finished.<br>
	 * Returns if the Store Query was successful or not
	 * 
	 * @param result
	 *            - true of Query was successful, false if not
	 */
	public void onStoreCallback(boolean result);
		
}
