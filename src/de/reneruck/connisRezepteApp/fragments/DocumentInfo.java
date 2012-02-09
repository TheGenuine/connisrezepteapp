package de.reneruck.connisRezepteApp.fragments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import de.reneruck.connisRezepteApp.AppContext;
import de.reneruck.connisRezepteApp.Configurations;
import de.reneruck.connisRezepteApp.DocumentEdit;
import de.reneruck.connisRezepteApp.R;
import de.reneruck.connisRezepteApp.Rezept;

/**
 * Zeigt eine Zusammenfassung aller Informationen an, die über dieses Rezept
 * gespeichert sind.
 * 
 * @author Rene
 * 
 */
public class DocumentInfo extends Fragment {

	private View view;
	private Rezept rezept;

	public DocumentInfo() {
	}
	
	public DocumentInfo(Rezept rezept) {
		if(rezept != null){
			this.rezept = rezept;
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
			((TextView) this.view.findViewById(R.id.document_info_rezept_name)).setText(this.rezept.getName());
			((TextView) this.view.findViewById(R.id.document_info_rezept_name)).setOnClickListener(openDocumentClickListener);
			((TextView) this.view.findViewById(R.id.document_info_zubereitung)).setText(this.rezept.getZubereitungsart());
			((TextView) this.view.findViewById(R.id.document_info_zeit)).setText(this.rezept.getZeit() + " min");
			((TextView) this.view.findViewById(R.id.document_info_zutaten)).setText(this.rezept.getZutaten().toString());
			
			for (String kategorie : this.rezept.getKategorien()) {
				TextView textview = new TextView(getActivity());
				textview.setTextSize(15);
				textview.setText(kategorie);
				((LinearLayout) this.view.findViewById(R.id.kategorien_container)).addView(textview);
			}
			((Button) this.view.findViewById(R.id.button_open_document)).setOnClickListener(openDocumentClickListener);
			((Button) this.view.findViewById(R.id.button_edit_document)).setOnClickListener(editDocumentClickListener);
		} else {
			this.view = new LinearLayout(getActivity());
		}
		return view;
	}
	
	OnClickListener editDocumentClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			startDocumentEdit();
		}

	};

	/**
	 * Starts the Document Edit Activity with the actual Rezept as Parameter
	 */
    void startDocumentEdit() {
    	List<Rezept> editEntries = new LinkedList<Rezept>();
    	editEntries.add(this.rezept);
    	((AppContext)getActivity().getApplicationContext()).getDocumentsBean().setCustomDocumentsList(editEntries);
    	Intent i = new Intent(getActivity(), DocumentEdit.class);
    	i.putExtra(Configurations.LIST_SOURCE, Configurations.CUSTOM_LIST);
    	startActivity(i);
    }
    
	public static DocumentInfo newInstance(Rezept rezept) {
		return new DocumentInfo(rezept);
	}
	
	OnClickListener openDocumentClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			openDocument(rezept.getDocumentName());
		}
	};
	
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
