package de.reneruck.connisRezepteApp.fragments;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import de.reneruck.connisRezepteApp.AppContext;
import de.reneruck.connisRezepteApp.ChooserListCallback;
import de.reneruck.connisRezepteApp.R;
import de.reneruck.connisRezepteApp.DB.DatabaseQueryCallback;

public class QueryList extends Fragment implements OnItemClickListener, DatabaseQueryCallback, ChooserListCallback {

	private AppContext appContext;
	private View view;
	private List<String> zubereitungsarten;
	private List<String> kategorien;
	private Map<String, List<String>> zutaten;
	private ListType displayListType;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.appContext = ((AppContext) getActivity().getApplicationContext());
		this.appContext.setChooserListCallback(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.view = inflater.inflate(R.layout.in_progress, null);
		return this.view;
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSelectCallback(List<?> result) {
		
		switch (this.displayListType) {
			case Zubereitungsart:
			case Kategorie:
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, (List<String>) result);
				ListView list = new ListView(appContext);
				list.setAdapter(adapter);
				list.setOnItemClickListener(QueryList.this);
				this.getActivity().setContentView(list);
				break;
			case Zutat:
				// TODO: build expandable list 
				break;
			case Zeit:
				// TODO: build timechooser view
				break;
			default:
				break;
		}
		
	}
	
	public enum ListType {
		Zubereitungsart, Kategorie, Zutat, Zeit
	}

	@Override
	public void displayList(ListType type) {
		this.displayListType = type;

		switch (type) {
			case Zubereitungsart:
				if(this.zubereitungsarten != null && !this.zubereitungsarten.isEmpty()) {
					// display list
				} else {
					// start query
				}
				break;
			case Kategorie:
				if(this.kategorien != null && !this.kategorien.isEmpty()) {
					// display list
				} else {
					// start query
				}
				break;
			case Zutat:
				if(this.zutaten != null && !this.zutaten.isEmpty()) {
					// display list
				} else {
					// start query
				}
				break;
			case Zeit:
				
				break;
			default:
				break;
		}
	}
}
