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

	public void putAllEntries(Collection<File> collection){
		this.propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this, "neueDokumente", this.neueDokumente, collection));
		this.neueDokumente.addAll(collection);
	}
	
	public void removeEntry(File entry){
		this.propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this, "neueDokumente", this.neueDokumente, this.neueDokumente));
		this.neueDokumente.remove(entry);
	}
	
	public void clearList(){
		this.propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this, "neueDokumente", this.neueDokumente, this.neueDokumente));
		this.neueDokumente.clear();
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
	
	public int getNewDocumentsCount(){
		return this.neueDokumente.size();
	}
}
