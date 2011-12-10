package de.reneruck.connisRezepteApp;

import android.os.Environment;

public class Configurations {

	public static final  String databaseName = "rezepte.db";
	public static final int databaseVersion = 1;
	public static final String dirPath = Environment.getExternalStorageDirectory() +"/Rezepte/";
	
	// available SQL Tables
	public static final String table_Rezepte = "Rezepte";
	public static final String table_Zutaten = "Zutaten";
	public static final String table_Kategorien = "Kategorien";
	public static final String table_Rezept_to_Kategorie = "Rezept_Kategorie";
	public static final String table_Rezept_to_Zutat = "Rezept_Zutat";
	public static final String ID = "ID";
	
	// collums of Rezepte Table
	public static String rezepte_Id = ID;
	public static String rezepte_Name = "rezeptName";
	public static String rezepte_DocumentName = "documentName";
	public static String rezepte_DocumentHash = "hash";
	public static String rezepte_PathToDocument = "documentPath";	
	public static String rezepte_Zubereitung = "zubereitung";
	
	// Collums of Zutaten
	public static String zutaten_Id = ID;
	public static String zutaten_value = "value";

	// Collums of Kategorien
	public static String kategorien_Id = ID;
	public static String kategorien_value = "value";

	// Collums of Rezepte-Kategorie
	public static String rezept_to_kategorie_Id = ID;
	public static String rezept_to_kategorie_rezeptId= "rezeptId";
	public static String rezept_to_kategorie_kategorieId= "katId";
	
	// Collums of Rezepte-Zutat
	public static String rezept_to_zutat_Id = ID;
	public static String rezept_to_zutat_rezeptId= "rezeptId";
	public static String rezept_to_zutat_zutatId= "zutatId";
	
}
