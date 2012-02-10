package de.reneruck.connisRezepteApp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Utils {

	public static void copyFile(File sourceFile, File targetFile){
		try {
			FileInputStream fileInputStream = new FileInputStream(sourceFile);
			FileOutputStream fileOutputStream = new FileOutputStream(targetFile);
			byte[] buffer = new byte[(int) sourceFile.length()];
			fileInputStream.read(buffer);
			fileInputStream.available();
			fileOutputStream.write(buffer);
			fileOutputStream.flush();
			fileInputStream.close();
			fileOutputStream.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
