package de.reneruck.connisRezepteApp;

import java.io.File;
import java.util.List;

import android.database.Cursor;

import com.j256.ormlite.field.DatabaseField;

public class Rezept {
	
	@DatabaseField(index = true)
	private int id;
	@DatabaseField
	private String name;
	@DatabaseField(canBeNull = false)
	private String documentName;
	@DatabaseField(canBeNull = false)
	private String documentPath;
	@DatabaseField
	private List<Kategorie> kategorien;
	@DatabaseField
	private String zubereitungsart;
	@DatabaseField
	private List<Zutat> zutat;

	private File originalFile;
	private boolean stored;
	
	public Rezept() {
	}
	
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
		this.documentPath = Configurations.dirPath+documentName;
	}
	
	public Rezept(Cursor cursor) {
		this.id = cursor.getInt(cursor.getColumnIndex(Configurations.rezepte_Id));
		this.name = cursor.getString(cursor.getColumnIndex(Configurations.rezepte_Name));
		this.documentName = cursor.getString(cursor.getColumnIndex(Configurations.rezepte_DocumentName));
		this.documentPath = Configurations.dirPath+this.documentName;
		this.zubereitungsart = cursor.getString(cursor.getColumnIndex(Configurations.rezepte_Zubereitung));
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
	public List<Kategorie> getKategorien() {
		return kategorien;
	}
	public void setKategorien(List<Kategorie> kategorien) {
		this.kategorien = kategorien;
	}
	public void addKategorien(Kategorie kategorien) {
		this.kategorien.add(kategorien);
	}
	
	public String getZubereitungsart() {
		return zubereitungsart;
	}
	public void setZubereitungsart(String zubereitungsart) {
		this.zubereitungsart = zubereitungsart;
	}
	public List<Zutat> getZutaten() {
		return zutat;
	}
	public void setZutaten(List<Zutat> zutat) {
		this.zutat = zutat;
	}
	public void addZutaten(Zutat zutat) {
		this.zutat.add(zutat);
	}
	public String getZutatenAsString(){
		StringBuilder builder = new StringBuilder();
		for (Zutat zutat : this.zutat) {
			builder.append(zutat.getZutat() + ", ");
		}
		String zutatenListe = builder.toString();
		zutatenListe.trim();
		if(zutatenListe.length() > 0){
			return zutatenListe.substring(0, zutatenListe.length()-2);
		}
		return zutatenListe;
	}
	public File getOriginalFile() {
		return originalFile;
	}
}
