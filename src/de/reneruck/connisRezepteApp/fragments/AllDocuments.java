package de.reneruck.connisRezepteApp.fragments;

import java.util.List;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import de.reneruck.connisRezepteApp.AppContext;
import de.reneruck.connisRezepteApp.R;
import de.reneruck.connisRezepteApp.Rezept;
import de.reneruck.connisRezepteApp.RezepteListAdapter;
import de.reneruck.connisRezepteApp.DB.DatabaseQueryCallback;

public class AllDocuments extends Fragment implements DatabaseQueryCallback {

	private AppContext appContext;
	private View layout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.appContext = (AppContext) getActivity().getApplicationContext();

	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.layout = inflater.inflate(R.layout.in_progress, null);
		this.appContext.getDatabaseManager().getAllRezepe(this);
		return this.layout;
	}
	
	@Override
	public void onSelectCallback(List<?> result) {
		List<Rezept> rezepte = (List<Rezept>) result;
		if (rezepte.size() == 0) {
			String string = getResources().getString(R.string.keine_rezepte_gefunden);
			rezepte.add(new Rezept(string));
		}
		View inflated = LayoutInflater.from(this.appContext).inflate(R.layout.all_documents, null);
		((ListView) inflated.findViewById(R.id.listView)).setAdapter(new RezepteListAdapter(rezepte));
		((ListView) inflated.findViewById(R.id.listView)).setOnItemClickListener(rezepteListEntyListener);
		ViewGroup fragmentContainer = (ViewGroup)this.layout.findViewById(R.id.main_fragment_container);
		this.getActivity().setContentView(inflated);
		this.layout = inflated;
	}
	
	/**
	 * All the listeners are implemented here
	 */
	OnItemClickListener rezepteListEntyListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> adapter, View view, int arg2, long arg3) {
			int documentId = (Integer) view.getTag();
			
			if(documentId > -1) {
				//Check what fragment is currently shown, replace if needed.
				if(((LinearLayout)layout.findViewById(R.id.fragment_container)).getChildCount() > 0) {
					Fragment fragment = getFragmentManager().findFragmentByTag(String.valueOf(documentId));
					if(fragment == null) { // No Fragment with the actual documentId found 
						replaceDocumentInfoFragment(documentId);
					} else if(appContext.getActualInfoItem() != documentId) {
						replaceDocumentInfoFragment(fragment);
					}
				} else { // if no DocumentInfo Fragment has been set, add it initialy
					addFragment(documentId);
				}
			}
		    
		}

	};
	
	private void addFragment(int documentId) {
		DocumentInfo documentInfo = new DocumentInfo();
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.add(R.id.fragment_container, documentInfo, String.valueOf(documentId));
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		transaction.commit();
		this.appContext.setActualInfoItem(documentId);
	}
	
	private void replaceDocumentInfoFragment(int documentId){
		((ViewGroup)this.layout.findViewById(R.id.fragment_container)).removeAllViews();
		DocumentInfo documentInfo = new DocumentInfo();
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
	    transaction.replace(R.id.fragment_container, documentInfo, String.valueOf(documentId));
	    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
	    transaction.commit();
	    this.appContext.setActualInfoItem(documentId);
	}	
	private void replaceDocumentInfoFragment(Fragment fragment){
		((ViewGroup)this.layout.findViewById(R.id.fragment_container)).removeAllViews();
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, fragment, fragment.getTag());
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		transaction.commit();
		this.appContext.setActualInfoItem(Integer.getInteger(fragment.getTag()));
	}	
}
