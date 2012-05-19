package de.reneruck.connisRezepteApp;

import android.os.Environment;

public class Configurations {

	public static final  String databaseName = "rezepte.db";
	public static final int databaseVersion = 1;
	public static final String dirPath = Environment.getExternalStorageDirectory() +"/Rezepte/";
	
	// Rezepte
	public static final String TABLE_REZEPTE = "Rezepte";
	public static final String ID_REZEPTE = "idRezepte";
	public static final String NAME = "name";
	public static final String DOCUMENT_NAME = "documentName";
	public static final String DOCUMENT_HASH = "documentHash";
	public static final String ZUBEREITUNGSARTEN_ID_ZUBEREITUNGSART = "Zubereitungsarten_idZubereitungsart";
	public static final String ZEIT = "Zeit";
	public static final String KATEGORIE_ID_KATEGORIE = "Kategorie_idKategorie";
	public static final String FK_REZEPTE_KATEGORIE1 = "fk_Rezepte_Kategorie1";
	public static final String FK_REZEPTE_ZUBEREITUNGSARTEN = "fk_Rezepte_Zubereitungsarten";

	
	// Rezepte has zutaten
	public static final String TABLE_REZEPTE_HAS_ZUTATEN = "Rezepte_has_Zutaten";
	public static final String REZEPTE_ID_REZEPTE = "Rezepte_idRezepte";
	public static final String ZUTATEN_ID_ZUTATEN = "Zutaten_idZutaten";
	public static final String FK_REZEPTE_HAS_ZUTATEN_ZUTATEN1 = "fk_Rezepte_has_Zutaten_Zutaten1";
	public static final String FK_REZEPTE_HAS_ZUTATEN_REZEPTE1 = "fk_Rezepte_has_Zutaten_Rezepte1";
	
	// zutaten kategorie
	public static final String TABLE_ZUTATEN_KATEGORIE = "Zutaten_Kategorie";
	public static final String ID_ZUTATEN_KATEGORIE = "idZutaten_Kategorie";
	
	// Zutaten
	public static final String TABLE_ZUTATEN = "Zutaten";
	public static final String ID_ZUTATEN = "idZutaten";
	public static final String ZUTATEN_KATEGORIE_ID_ZUTATEN_KATEGORIE = "Zutaten_Kategorie_idZutaten_Kategorie";
	public static final String FK_ZUTATEN_ZUTATEN_KATEGORIE1 = "fk_Zutaten_Zutaten_Kategorie1";
	
	// Kategorie
	public static final String TABLE_KATEGORIEN = "Kategorie";
	public static final String ID_KATEGORIE = "idKategorie";
	
	public static final String VALUE = "value";
	
	// Zubereitungsarten
	public static final String TABLE_ZUBEREITUNGSARTEN = "Zubereitungsarten";
	public static final String ID_ZUBEREITUNGSART = "idZubereitungsart";
	
	
	// Communication Entries
	public static final String LIST_SOURCE = "ListSource";
	public static final int NEW_DOCUMENTS = 0;
	public static final int CUSTOM_LIST = 1;
	public static final int DIALOG_NO_NEW_DOCUMENTS = 55;
	protected static final int DIALOG_WAITING_FOR_QUERY = 56;
	public static final String LOCAL_FILE_DIR = "documents";
	
	public static final String[] Kategorien = {"Vegetarisch", "Fleisch", "Backwaren - sueß", "Backwaren - herzhaft", "Salat", "Fleisch+Gemuese", "Suppe/Eintopf"};
	public static final String[] Zubereitungsart = {"Herd", "Backofen", "Grill", "Herd + Backofen", "ohne Kochen"};
	public static final String[] ZutatenKategorie = {"Gemuese", "Fleisch", "Rohkost", "Getreide", "Huelsenfrüchte", "Milchprodukte", "Eier", "Obst", "Pilze"};
	
	public static final String[] ZutatenFleisch = {"Huhn", "Pute", "Kalb", "Rind", "Wild", "Schwein"};
	public static final String[] ZutatenMilchprodukete = {"Milch", "Sahne", "Creme fraiche - Sauerrahm", "Kaese"};
	
	public enum ListType {
		Zubereitungsart, Kategorie, Zutat, Zeit, ZutatKategorie
	}
}
