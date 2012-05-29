package de.reneruck.connisRezepteApp.DB;

import java.util.List;
import java.util.Map;

public interface DatabaseZutatenQueryCallback extends DatabaseCallback {

	/**
	 * Will be called when a Database Query is finished.<br>
	 * The Result will be returned as List of Objects it has been queried for.<br>
	 * WILL BE CALLED ONLY ON SELECT QUERIES
	 * 
	 * @param result
	 *            - a List of Results or null
	 */
	public void onSelectCallback(Map<String, List<String>> result);
}
