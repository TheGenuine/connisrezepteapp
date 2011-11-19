package de.reneruck.connisRezepteApp;

import android.os.Environment;

public class Configurations {

	public static final  String databaseName = "rezepte.db";
	public static final int databaseVersion = 3;
	public static final String rezepteDirPath = Environment.getExternalStorageDirectory() +"/Rezepte/";
	
	public static String rezept_Id = "rezeptID";
	public static String rezept_Name = "rezeptName";
	public static String rezept_DocumentName = "documentName";
	public static String rezept_PathToDocument = "documentPath";	
	public static String rezepte_stichwoerter = "keywords";
	
	public static String keywords_Id = "keyID";
	public static String keywords_keyword = "keyword";
	
	public static final String table_Keywords = "Keywords";
	public static final  String table_Rezepte = "Rezepte";
}
