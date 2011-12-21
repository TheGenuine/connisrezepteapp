package de.reneruck.connisRezepteApp.fragments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import de.reneruck.connisRezepteApp.Configurations;
import de.reneruck.connisRezepteApp.R;
import de.reneruck.connisRezepteApp.Rezept;

public class DocumentInfo extends Fragment {

	private View view;
	private String zubereitungsart;
	private String kategorie;
	private String zutaten;
	private String name;

	public DocumentInfo() {
	}
	
	public DocumentInfo(Rezept rezept) {
		if(rezept != null){
			this.name = rezept.getName();
			this.zubereitungsart = rezept.getZubereitungsart();
			if(!rezept.getKategorien().isEmpty())this.kategorie = rezept.getKategorien().get(0);
			else this.kategorie = "";
			this.zutaten = rezept.getZutaten().toString();
		}
	
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (container != null) {
			this.view = inflater.inflate(R.layout.fragment_document_preview, container, false);
			((TextView) this.view.findViewById(R.id.document_info_rezept_name)).setText(this.name);
			((TextView) this.view.findViewById(R.id.document_info_zubereitung)).setText(this.zubereitungsart);
			((TextView) this.view.findViewById(R.id.document_info_kategorie)).setText(this.kategorie);
			((TextView) this.view.findViewById(R.id.document_info_zutaten)).setText(this.zutaten);
		} else {
			this.view = new LinearLayout(getActivity());
		}
		return view;
	}

	public static DocumentInfo newInstance(Rezept rezept) {
		return new DocumentInfo(rezept);
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
