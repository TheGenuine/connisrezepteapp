package de.reneruck.connisRezepteApp;

import android.os.Environment;

public class Configurations {

	public static final  String databaseName = "rezepte.db";
	public static final int databaseVersion = 1;
	public static final String rezepteDirPath = Environment.getExternalStorageDirectory() + "/Rezepte/";
	
	public static String rezept_Id = "rezeptID";
	public static String rezept_Name = "rezeptName";
	public static String rezept_Document = "document";
	public static String rezepte_stichwörter = "keywords";
	
	public static final  String table_Rezepte = "rezepte";
}
