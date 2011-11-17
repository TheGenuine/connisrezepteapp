package de.reneruck.connisRezepteApp;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class NewDocumentsBean {
	private List<String> neueDokumente = new LinkedList<String>();
	private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	public void putEntry(String entry){
		neueDokumente.add(entry);
	}
	
	public void putAllEntries(Collection<String> collection){
		neueDokumente.addAll(collection);
	}
	
	public void removeEntry(String entry){
		neueDokumente.remove(entry);
	}
	
	public void clearList(){
		neueDokumente.clear();
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



	public List<String> getNeueDokumente() {
		return neueDokumente;
	}



	public void setNeueDokumente(List<String> neueDokumente) {
		this.neueDokumente = neueDokumente;
	} 
}
