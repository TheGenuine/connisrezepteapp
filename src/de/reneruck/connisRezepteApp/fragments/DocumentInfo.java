package de.reneruck.connisRezepteApp.fragments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
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
import de.reneruck.connisRezepteApp.DB.DatabaseQueryCallback;

/**
 * Zeigt eine Zusammenfassung aller Informationen an, die über dieses Rezept
 * gespeichert sind.
 * 
 * @author Rene
 * 
 */
public class DocumentInfo extends Fragment implements DatabaseQueryCallback {

	private Rezept rezept;
	private View view;
	private ViewHolder viewHolder = new ViewHolder();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int actualInfoItem = ((AppContext)this.getActivity().getApplicationContext()).getActualInfoItem();
		((AppContext)getActivity().getApplicationContext()).getDatabaseManager().getDocument(actualInfoItem, this);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.view = inflater.inflate(R.layout.fragment_document_preview, container, false);
		
		
		this.viewHolder.rezept_name = (TextView) this.view.findViewById(R.id.document_info_rezept_name);
		this.viewHolder.zubereitung = (TextView) this.view.findViewById(R.id.document_info_zubereitung);
		this.viewHolder.zeit = (TextView) this.view.findViewById(R.id.document_info_zeit);
		this.viewHolder.zutaten = (TextView) this.view.findViewById(R.id.document_info_zutaten);
		this.viewHolder.kategorien_container = (LinearLayout) view.findViewById(R.id.kategorien_container);
		
		this.viewHolder.waiting_layout = (LinearLayout) this.view.findViewById(R.id.waiting_layout);
		
		((Button) view.findViewById(R.id.button_open_document)).setOnClickListener(openDocumentClickListener);
		((Button) view.findViewById(R.id.button_edit_document)).setOnClickListener(editDocumentClickListener);
		((TextView) this.view.findViewById(R.id.document_info_rezept_name)).setOnClickListener(openDocumentClickListener);
		return this.view;
	}
	
	private void fillGui() {
		if(this.viewHolder.waiting_layout != null && View.VISIBLE == this.viewHolder.waiting_layout.getVisibility()) {
			this.viewHolder.waiting_layout.setVisibility(View.GONE);
		}
		
		this.viewHolder.rezept_name.setText(this.rezept.getName());
		
		this.viewHolder.zubereitung.setText(!this.rezept.getZubereitungsart().isEmpty() ? this.rezept.getZubereitungsart() : "-");
		this.viewHolder.zeit.setText(this.rezept.getZeit() + " min");
		this.viewHolder.zutaten.setText(!this.rezept.getZutaten().isEmpty() ? this.rezept.getZutaten().toString() : "-");
		if(this.rezept.getKategorien().isEmpty()) {
			TextView textview = new TextView(getActivity());
			textview.setTextSize(15);
			textview.setText("-");
			this.viewHolder.kategorien_container.addView(textview);
		} else {
			for (String kategorie : this.rezept.getKategorien()) {
				TextView textview = new TextView(getActivity());
				textview.setTextSize(15);
				textview.setText(kategorie);
				this.viewHolder.kategorien_container.addView(textview);
			}
		}
		((LinearLayout)this.view.findViewById(R.id.data_layout)).setVisibility(View.VISIBLE);
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
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Rezept getRezept() {
		return rezept;
	}

	@Override
	public void onSelectCallback(List<?> result) {
		if(result.size() > 0) {
			this.rezept = (Rezept) result.get(0);
			fillGui();
		}
	}

	private static class ViewHolder {
		protected TextView rezept_name;
		protected TextView zubereitung;
		protected TextView zeit;
		protected TextView zutaten;
		protected LinearLayout kategorien_container;
		protected LinearLayout waiting_layout;
	}
}
