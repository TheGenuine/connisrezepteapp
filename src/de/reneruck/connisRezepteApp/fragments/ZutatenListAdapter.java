package de.reneruck.connisRezepteApp.fragments;

import java.util.List;
import java.util.Map;

import de.reneruck.connisRezepteApp.AppContext;
import de.reneruck.connisRezepteApp.Configurations.ListType;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ExpandableListAdapter;
import android.widget.TextView;

public class ZutatenListAdapter implements ExpandableListAdapter {

	private Map<String, List<String>> zutaten;
	private String[] kategorien;
	private AppContext appContext;

	public ZutatenListAdapter(Context context, Map<String, List<String>> zutaten) {
		this.zutaten = zutaten;
		this.kategorien = zutaten.keySet().toArray(new String[]{});
		this.appContext = (AppContext) context;
	}
	
	@Override
	public boolean areAllItemsEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return this.zutaten.get(this.kategorien[groupPosition]).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View view = inflater.inflate(android.R.layout.select_dialog_multichoice, null);
		String textValue = this.zutaten.get(this.kategorien[groupPosition]).get(childPosition);
		
		((CheckedTextView)view).setText(textValue);
		((CheckedTextView)view).setTextColor(Color.BLACK);
		((CheckedTextView)view).setChecked(this.appContext.isChecked(ListType.Zutat, textValue));
		return view;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return this.zutaten.get(this.kategorien[groupPosition]).size();
	}

	@Override
	public long getCombinedChildId(long groupId, long childId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getCombinedGroupId(long groupId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return this.zutaten.get(this.kategorien[groupPosition]);
	}

	@Override
	public int getGroupCount() {
		return this.kategorien.length;
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View view = inflater.inflate(android.R.layout.simple_expandable_list_item_1, null);
		String textValue = this.kategorien[groupPosition];
		((TextView)view).setText(textValue);	
		((TextView)view).setTextColor(Color.BLACK);
		return view;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onGroupCollapsed(int groupPosition) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGroupExpanded(int groupPosition) {
		// TODO Auto-generated method stub

	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		// TODO Auto-generated method stub

	}
}
