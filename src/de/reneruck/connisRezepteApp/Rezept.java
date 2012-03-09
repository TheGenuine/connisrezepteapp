package de.reneruck.connisRezepteApp;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import android.database.Cursor;

public class Rezept {

	private int id;
	private String name;
	private String documentName;
	private String documentPath;
	private List<String> kategorien = new LinkedList<String>();
	private String zubereitungsart;
	private List<String> zutaten = new LinkedList<String>();
	private int zeit;
	private File originalFile;
	private boolean stored;
	
	public Rezept(String filename) {
		this(new File(filename));
	}
	
	public Rezept(File document) {
		String documentName = document.getName();
		this.originalFile = document;
		this.id = documentName.hashCode();
		String[] doucmentSplit = documentName.split("\\.");
		this.name =doucmentSplit .length > 0 ?doucmentSplit[0] : documentName;
		this.documentName = documentName;
		this.zeit = 0;
		this.documentPath = Configurations.dirPath+documentName;
	}
	
	public Rezept(Cursor cursor) {
		this.id = cursor.getInt(cursor.getColumnIndex(Configurations.rezepte_Id));
		this.name = cursor.getString(cursor.getColumnIndex(Configurations.rezepte_Name));
		this.documentName = cursor.getString(cursor.getColumnIndex(Configurations.rezepte_DocumentName));
		this.documentPath = Configurations.dirPath+this.documentName;
		this.zubereitungsart = cursor.getString(cursor.getColumnIndex(Configurations.rezepte_Zubereitung));
		this.zeit = cursor.getInt(cursor.getColumnIndex(Configurations.rezepte_Zeit));
		this.stored = true;
	}
	
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDocumentPath() {
		return documentPath;
	}
	public void setDocumentPath(String documentPath) {
		this.documentPath = documentPath;
	}
	public boolean isStored() {
		return stored;
	}
	public void setStored(boolean stored) {
		this.stored = stored;
	}
	public String getDocumentName() {
		return documentName;
	}
	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}
	public List<String> getKategorien() {
		return kategorien;
	}
	public void setKategorien(List<String> kategorien) {
		this.kategorien = kategorien;
	}
	public void addKategorie(String kategorie) {
		if(this.kategorien == null) {
			this.kategorien = new LinkedList<String>();
		}
		this.kategorien.add(kategorie);
	}
	public String getZubereitungsart() {
		return zubereitungsart;
	}
	public void setZubereitungsart(String zubereitungsart) {
		this.zubereitungsart = zubereitungsart;
	}
	public List<String> getZutaten() {
		return zutaten;
	}
	public void setZutaten(List<String> zutat) {
		this.zutaten = zutat;
	}
	public void setZutaten(String zutaten) {
		if(!zutaten.isEmpty() && zutaten.length() > 0){
			String[] zutatenSplit = zutaten.split(",");
			this.zutaten.clear();
			for (String string : zutatenSplit) {
				this.zutaten.add(string.trim());
			}
		}
	}
	public void addZutat(String zutat) {
		if(this.zutaten == null) {
			this.zutaten = new LinkedList<String>();
		}
		this.zutaten.add(zutat.trim());
	}

	public File getOriginalFile() {
		return originalFile;
	}

	public int getZeit() {
		return zeit;
	}

	public void setZeit(int zeit) {
		this.zeit = zeit;
	}
}
