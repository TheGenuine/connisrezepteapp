package de.reneruck.connisRezepteApp;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import de.reneruck.connisRezepteApp.Configurations.ListType;

public class CustomArrayAdapter extends BaseAdapter {

	private AppContext appContext;
	private ViewHolder holder;
	private List<String> entries = new LinkedList<String>();
	private ListType listType;
	
	public CustomArrayAdapter(Context context, List<String> entries, ListType type) {
		this.appContext = (AppContext) context;
		this.holder = new ViewHolder();
		this.entries = entries;
		this.listType = type;
	}

	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        
        if(convertView == null)
        {
            convertView = View.inflate(this.appContext, android.R.layout.select_dialog_multichoice, null);
            this.holder.txt = (CheckedTextView) convertView.findViewById(android.R.id.text1);
        }
        String text = this.entries.get(position);
        boolean checked = this.appContext.isChecked(this.listType, text);
        
        this.holder.txt.setText(text);
        this.holder.txt.setTextColor(Color.BLACK);
		this.holder.txt.setChecked(checked);
		if(checked) {
			this.holder.txt.setBackgroundColor(android.R.color.holo_blue_light);
		}
        
        return convertView;
    }
    
    static class ViewHolder
    {
        CheckedTextView txt;
    }

	@Override
	public int getCount() {
		return this.entries.size();
	}

	@Override
	public Object getItem(int arg0) {
		return this.entries.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return this.entries.get(arg0).hashCode();
	}
}