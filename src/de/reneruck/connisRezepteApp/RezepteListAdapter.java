package de.reneruck.connisRezepteApp;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

public class RezepteListAdapter extends BaseAdapter implements Filterable {

	private Context context;
	private LayoutInflater mInflater;
	private List<String> data;

	public RezepteListAdapter(Context context, List<String> data) {
		this.data = data;
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
	}

	/**
	 * Make a view to hold each row.
	 * 
	 * @see android.widget.ListAdapter#getView(int, android.view.View,
	 *      android.view.ViewGroup)
	 */
	public View getView(final int position, View convertView, ViewGroup parent) {
		// A ViewHolder keeps references to children views to avoid
		// unneccessary calls
		// to findViewById() on each row.
		ViewHolder holder;

		// When convertView is not null, we can reuse it directly, there is
		// no need
		// to reinflate it. We only inflate a new View when the convertView
		// supplied
		// by ListView is null.
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.rezeptelist_entry, null);

			// Creates a ViewHolder and store references to the two children
			// views
			// we want to bind data to.
			holder = new ViewHolder();
			holder.icon = (ImageView) convertView.findViewById(R.id.icon);
			holder.toptext = (TextView) convertView.findViewById(R.id.toptext);
			holder.subtext = (TextView) convertView.findViewById(R.id.subtext);

			convertView.setTag(holder);
		} else {

			// Get the ViewHolder back to get fast access to the TextView
			// and the ImageView.
			holder = (ViewHolder) convertView.getTag();
		}

		String[] entrySplit = (data.get(position)).split(" - ");
		holder.toptext.setText(entrySplit[0]);
		holder.subtext.setText(entrySplit[1]);
		// Bind the data efficiently with the holder.
		return convertView;
	}

	class ViewHolder {
		TextView toptext;
		TextView subtext;
		ImageView icon;
	}

	@Override
	public Filter getFilter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

}