package de.reneruck.connisRezepteApp.fragments;

import java.util.List;
import java.util.Map;

import android.app.Fragment;
import android.graphics.Color;
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
import de.reneruck.connisRezepteApp.Configurations.ListType;
import de.reneruck.connisRezepteApp.R;
import de.reneruck.connisRezepteApp.DB.DatabaseManager;
import de.reneruck.connisRezepteApp.DB.DatabaseQueryCallback;

public class QueryList extends Fragment implements OnItemClickListener, DatabaseQueryCallback, ChooserListCallback {

	private AppContext appContext;
	private View view;
	private List<String> zubereitungsarten;
	private List<String> kategorien;
	private Map<String, List<String>> zutaten;
	private ListType displayListType;
	private DatabaseManager databaseManager;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.appContext = ((AppContext) getActivity().getApplicationContext());
		this.appContext.setChooserListCallback(this);
		this.databaseManager = this.appContext.getDatabaseManager();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.view = inflater.inflate(R.layout.empty, null);
		return this.view;
	}
	@Override
	public void onItemClick(AdapterView<?> parnet, View view, int position, long id) {
		boolean checked = (Boolean) view.getTag();
		String value = "";
		switch (this.displayListType) {
		case Zubereitungsart:
			value  = this.zubereitungsarten.get(position);
			break;
		case Kategorie:
			value = this.kategorien.get(position);
			break;
		default:
			break;
		}
		
		if(checked) {
			view.setBackgroundColor(Color.TRANSPARENT);
			view.setTag(false);
			this.appContext.removeFromQuery(this.displayListType, value);
			// remove from query
		} else {
			view.setTag(true);
			view.setBackgroundColor(Color.YELLOW);
			this.appContext.addToQuery(this.displayListType, value);
		}
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
				ViewGroup viewGroup = (ViewGroup)this.view.findViewById(R.id.query_list_container);
				viewGroup.removeAllViews();
				viewGroup.addView(list);
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
	
	@Override
	public void displayList(ListType type) {
		this.displayListType = type;

		switch (type) {
			case Zubereitungsart:
				if(this.zubereitungsarten != null && !this.zubereitungsarten.isEmpty()) {
					// display list
					onSelectCallback(this.zubereitungsarten);
				} else {
					this.databaseManager.getAllZubereitungsarten(this);
				}
				break;
			case Kategorie:
				if(this.kategorien != null && !this.kategorien.isEmpty()) {
					// display list
					onSelectCallback(this.kategorien);
				} else {
					// start query
					this.databaseManager.getAllKategorien(this);
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
