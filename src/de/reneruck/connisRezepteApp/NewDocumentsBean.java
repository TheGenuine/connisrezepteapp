package de.reneruck.connisRezepteApp;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class NewDocumentsBean {
	private List<File> neueDokumente = new LinkedList<File>();
	private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	public void putEntry(File entry){
		neueDokumente.add(entry);
		this.propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this, null, null, entry));
	}
	
	public void putAllEntries(Collection<File> collection){
		neueDokumente.addAll(collection);
		this.propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this, null, null, collection));
	}
	
	public void removeEntry(String entry){
		neueDokumente.remove(entry);
		this.propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this, "neueDokumente", null, this.neueDokumente));
	}
	
	public void clearList(){
		neueDokumente.clear();
		this.propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this, null, null, this.neueDokumente));
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



	public List<File> getNeueDokumente() {
		return neueDokumente;
	}



	public void setNeueDokumente(List<File> neueDokumente) {
		this.neueDokumente = neueDokumente;
	} 
}
