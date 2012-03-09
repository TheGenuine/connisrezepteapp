package de.reneruck.connisRezepteApp;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DocumentsBean {
	private Map<Integer,File> neueDokumente = new HashMap<Integer,File>();
	private List<Rezept> customDocumentList = new LinkedList<Rezept>();

	private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	public void putAllEntries(Collection<File> collection){
		this.propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this, "neueDokumente", this.neueDokumente, collection));
		for (File file : collection) {
			this.neueDokumente.put(file.hashCode(), file);
		}
	}
	
	public void removeEntry(int hash){
		this.propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this, "neueDokumente", this.neueDokumente, this.neueDokumente));
		this.neueDokumente.remove(hash);
	}
	
	public void clearList(){
		this.propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this, "neueDokumente", this.neueDokumente, this.neueDokumente));
		this.neueDokumente.clear();
	}

	public List<Rezept> getCustomDocumentsList() {
		return this.customDocumentList;
	}

	public void setCustomDocumentsList(List<Rezept> list) {
		this.customDocumentList.clear();
		this.customDocumentList.addAll(list);
	}

	public int getNewDocumentsCount(){
		return this.neueDokumente.size();
	}

	public List<File> getNeueDokumente() {
		return new LinkedList<File>(this.neueDokumente.values());
	}
	
	/**
	 * Method that all JavaBeans class need to implement to be able to inform
	 * PropertyChangeListeners when an attribute has been changed. This method
	 * adds a new Listener to the bean.
	 * 
	 * @param listener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		this.propertyChangeSupport.addPropertyChangeListener(listener);
	}
	
	/**
	 * Method that all JavaBeans class need to implement to be able to inform
	 * PropertyChangeListeners when an attribute has been changed. This method
	 * removes a Listener from the bean.
	 * 
	 * @param listener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		this.propertyChangeSupport.removePropertyChangeListener(listener);
	}

}
