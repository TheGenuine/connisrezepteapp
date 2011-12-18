package de.reneruck.connisRezepteApp;

import com.j256.ormlite.field.DatabaseField;

public class Zutat {
	
	@DatabaseField(index = true)
	private int ID;
	
	@DatabaseField(canBeNull = false)
	private String zutat;

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getZutat() {
		return zutat;
	}

	public void setZutat(String zutat) {
		this.zutat = zutat;
	}

}
