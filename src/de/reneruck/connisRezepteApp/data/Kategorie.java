package de.reneruck.connisRezepteApp.data;

import com.j256.ormlite.field.DatabaseField;

public class Kategorie {

	@DatabaseField(index = true)
	private int ID;
	
	@DatabaseField(canBeNull = false)
	
	private String kategorie;

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getKategorie() {
		return kategorie;
	}

	public void setKategorie(String kategorie) {
		this.kategorie = kategorie;
	}
	
}
