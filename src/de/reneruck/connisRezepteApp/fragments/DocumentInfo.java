package de.reneruck.connisRezepteApp.fragments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import de.reneruck.connisRezepteApp.Configurations;
import de.reneruck.connisRezepteApp.R;
import de.reneruck.connisRezepteApp.Rezept;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class DocumentInfo extends Fragment {

	public DocumentInfo() {
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_document_preview, container);
		return view;
	}

	public static DocumentInfo newInstance(Rezept rezept) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void openDocument(String documentName){
		try {
			Intent intent = new Intent();
			intent.setAction(android.content.Intent.ACTION_VIEW);
			File file = new File(Configurations.dirPath + documentName);
			String mimeType = file.toURL().openConnection().getContentType();
			intent.setDataAndType(Uri.fromFile(file), mimeType );
			startActivity(intent); 
		} catch (ActivityNotFoundException e){
			Toast.makeText(getActivity().getApplicationContext(), "Kein Programm zum Öffnen von " + documentName + " gefunden!", Toast.LENGTH_LONG).show();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
