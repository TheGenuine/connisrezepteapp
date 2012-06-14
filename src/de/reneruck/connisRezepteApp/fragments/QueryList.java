package de.reneruck.connisRezepteApp.fragments;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckedTextView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import de.reneruck.connisRezepteApp.AppContext;
import de.reneruck.connisRezepteApp.ChooserListCallback;
import de.reneruck.connisRezepteApp.Configurations.ListType;
import de.reneruck.connisRezepteApp.CustomArrayAdapter;
import de.reneruck.connisRezepteApp.R;
import de.reneruck.connisRezepteApp.DB.DatabaseManager;
import de.reneruck.connisRezepteApp.DB.DatabaseQueryCallback;
import de.reneruck.connisRezepteApp.DB.DatabaseZutatenQueryCallback;

public class QueryList extends Fragment implements OnItemClickListener, OnChildClickListener, DatabaseQueryCallback, DatabaseZutatenQueryCallback, ChooserListCallback {

	private AppContext appContext;
	private View view;
	private List<String> zubereitungsarten;
	private List<String> kategorien;
	private Map<String, List<String>> zutaten;
	private ListType displayListType;
	private DatabaseManager databaseManager;
	private ListAdapter zubereitungsartenAdapter;
	private ListAdapter kategorienAdapter;
	private ExpandableListAdapter zutatenAdapter;
	
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
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
		onItemClick(parent, v, childPosition, id);
		return true;
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		boolean checked = false;
		
		String value = "";
		switch (this.displayListType) {
			case Zubereitungsart:
				value  = this.zubereitungsarten.get(position);
				break;
			case Kategorie:
				value = this.kategorien.get(position);
				break;
			case Zutat:
				value = (String) ((TextView)view).getText();
				break;
			default:
				break;
		}
		
		CheckedTextView checkedTextView = (CheckedTextView)view;
		checked = checkedTextView.isChecked();
		
		if(!"".equals(value)) {
			if((Boolean) checked) {
				checkedTextView.setChecked(!checked);
//				view.setBackgroundColor(Color.TRANSPARENT);
				
				// remove from query
				this.appContext.removeFromQuery(this.displayListType, value);
			} else {
				checkedTextView.setChecked(!checked);
				
				// add to query
				this.appContext.addToQuery(this.displayListType, value);
			}
		}
	}
	
	@Override
	public void onSelectCallback(Map<String, List<String>> result) {
		this.zutaten = result;
		if(result.isEmpty()) {
			this.zutaten.put(getActivity().getString(R.string.keine_zutaten_erfasst), new LinkedList<String>());
		}
		this.zutatenAdapter = new ZutatenListAdapter(this.appContext, this.zutaten);
		createZutatenList(this.zutatenAdapter);
	}

	private void createZutatenList(ExpandableListAdapter adapter) {
		ExpandableListView list = new ExpandableListView(this.appContext);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		list.setOnChildClickListener(this);
		((ViewGroup)this.view).removeAllViews();
		((ViewGroup)this.view).addView(list);
	}

	@Override
	public void onSelectCallback(List<?> result) {
		
		switch (this.displayListType) {
			case Zubereitungsart:
				this.zubereitungsarten = (List<String>) result;
				this.zubereitungsartenAdapter = new CustomArrayAdapter(this.appContext, this.zubereitungsarten, this.displayListType);
//				this.zubereitungsartenAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,  this.zubereitungsarten);
				createList(this.zubereitungsartenAdapter);
				break;
			case Kategorie:
				this.kategorien = (List<String>) result;
				this.kategorienAdapter = new CustomArrayAdapter(this.appContext, this.kategorien, this.displayListType);
//				this.kategorienAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,  this.kategorien);
				createList(kategorienAdapter);
				break;
			case Zutat:
				// something went wrong
				break;
			case Zeit:
				LayoutInflater inflater = LayoutInflater.from(this.appContext);
				View timeChooserLayout = inflater.inflate(R.layout.time_chooser, null);
				
				TimePicker timePicker = (TimePicker)timeChooserLayout.findViewById(R.id.timePicker);
				timePicker.setOnTimeChangedListener(this.onTimeChangListener);
				timePicker.setIs24HourView(true);

				timePicker.setCurrentHour(0);
				timePicker.setCurrentMinute(Integer.valueOf((String) result.get(0)));
				
				((ViewGroup)this.view).removeAllViews();
				((ViewGroup)this.view).addView(timeChooserLayout);
				break;
			default:
				break;
		}
		
	}

	private void createList(ListAdapter adapter) {
		ListView list = new ListView(this.appContext);
		list.setAdapter(adapter);
		list.setOnItemClickListener(QueryList.this);
		((ViewGroup)this.view).removeAllViews();
		((ViewGroup)this.view).addView(list);
	}
	
	@Override
	public void displayList(ListType type) {
		this.displayListType = type;

		switch (type) {
			case Zubereitungsart:
				if(this.zubereitungsarten != null && !this.zubereitungsarten.isEmpty() && this.zubereitungsartenAdapter != null) {
					createList(this.zubereitungsartenAdapter);
				} else {
					this.databaseManager.getAllZubereitungsarten(this);
				}
				break;
			case Kategorie:
				if(this.kategorien != null && !this.kategorien.isEmpty() && this.kategorienAdapter != null) {
					createList(this.kategorienAdapter);
				} else {
					this.databaseManager.getAllKategorien(this);
				}
				break;
			case Zutat:
				if(this.zutaten != null && !this.zutaten.isEmpty()) {
					createZutatenList(this.zutatenAdapter);
				} else {
					this.databaseManager.getAllZutaten(this);
				}
				break;
			case Zeit:
				List<String> zeitList;
				if(this.appContext.isSet(ListType.Zeit)){
					zeitList = new LinkedList<String>(this.appContext.getEntriesFromRawQuery(ListType.Zeit));
				} else {
					zeitList = new LinkedList<String>();
					zeitList.add("0");
				}
				onSelectCallback(zeitList);
				break;
			default:
				break;
		}
	}
	
	private OnTimeChangedListener onTimeChangListener = new OnTimeChangedListener() {
		
		@Override
		public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
			
		}
	};
}
