package de.reneruck.connisRezepteApp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import de.reneruck.connisRezepteApp.DB.DatabaseManager;
import de.reneruck.connisRezepteApp.DB.DatabaseQueryCallback;
import de.reneruck.connisRezepteApp.fragments.QueryList;

public class SearchBuilderActivity extends Fragment {

	private AppContext appContext;
	private List<String> allZubereitungsarten;
	private List<String> allKategorien;
	private Map<String, List<String>> zutaten = new HashMap<String, List<String>>();
	private Map<String, List<String>> query = new HashMap<String, List<String>>();
	private View layout;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.appContext = (AppContext) this.getActivity().getApplicationContext();
		DatabaseManager databaseManager = this.appContext.getDatabaseManager();
		
		// TODO: run Queries
		// - alle Zubereitungsarten
		// - alle Kategorien
		// - alle Zutaten
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.layout = inflater.inflate(R.layout.search_build_layout, null);
		((Button) this.layout.findViewById(R.id.button_zubereitung)).setOnClickListener(zubereitungClickListener);
		((Button) this.layout.findViewById(R.id.button_kategorie)).setOnClickListener(kategorieClickListener);
		((Button) this.layout.findViewById(R.id.button_zutaten)).setOnClickListener(zutatenClickListener);
		((Button) this.layout.findViewById(R.id.button_zeit)).setOnClickListener(zeitClickListener);
		return this.layout;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Fragment findFragment = getFragmentManager().findFragmentByTag(QueryList.class.getName());
		Fragment queryList;
		if(findFragment != null) {
			queryList = findFragment;
		} else {
			queryList = Fragment.instantiate(getActivity(), QueryList.class.getName());
		}
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.query_list_container, queryList, QueryList.class.getName());
		ft.commit();
	}
	
	private OnClickListener zubereitungClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			appContext.getChooserListCallback().displayList(QueryList.ListType.Zubereitungsart);
		}
	};
	private OnClickListener kategorieClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			appContext.getChooserListCallback().displayList(QueryList.ListType.Kategorie);
			
		}
	};
	private OnClickListener zutatenClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			appContext.getChooserListCallback().displayList(QueryList.ListType.Zutat);
			
		}
	};
	private OnClickListener zeitClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			appContext.getChooserListCallback().displayList(QueryList.ListType.Zeit);
		}
	};
	
	private DatabaseQueryCallback zubereitungsartenQueryCallback = new DatabaseQueryCallback() {
		

		@SuppressWarnings("unchecked")
		@Override
		public void onSelectCallback(List<?> result) {
			allZubereitungsarten = (List<String>) result;
		}
	};
	
	private DatabaseQueryCallback kategorienQueryCallback = new DatabaseQueryCallback() {
		
		@SuppressWarnings("unchecked")
		@Override
		public void onSelectCallback(List<?> result) {
			allKategorien = (List<String>) result;
		}
	};

	private DatabaseQueryCallback zutatenQueryCallback = new DatabaseQueryCallback() {
		
		@Override
		public void onSelectCallback(List<?> result) {
			// TODO Auto-generated method stub
			
		}
	};
	
}
