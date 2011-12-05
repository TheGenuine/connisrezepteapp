package de.reneruck.connisRezepteApp;

import android.os.Environment;

public class Configurations {

	public static final  String databaseName = "rezepte.db";
	public static final int databaseVersion = 1;
	public static final String dirPath = Environment.getExternalStorageDirectory() +"/Rezepte/";
	
	// available SQL Tables
	public static final String table_Keywords = "Keywords";
	public static final String table_Rezepte = "Rezepte";
	public static final String table_Zutaten = "Zutaten";
	public static final String table_Kategorien = "Kategorien";
	
	// collums of Rezepte Table
	public static String rezepte_Id = "ID";
	public static String rezepte_Name = "rezeptName";
	public static String rezepte_DocumentName = "documentName";
	public static String rezepte_DocumentHash = "hash";
	public static String rezepte_PathToDocument = "documentPath";	
	public static String rezepte_Stichwoerter = "keywords";
	
	// Collums of Keywords Table
	public static String keywords_Id = "ID";
	public static String keywords_keyword = "keyword";

	// Collums of Zutaten
	public static String zutaten_Id = "ID";
	public static String zutaten_value = "value";

	// Collums of Kategorien
	public static String kategorien_Id = "ID";
	public static String kategorien_value = "value";
}
