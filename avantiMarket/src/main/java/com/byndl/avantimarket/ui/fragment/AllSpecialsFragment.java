package com.byndl.avantimarket.ui.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.byndl.avantimarket.R;
import com.byndl.avantimarket.ui.activity.MyAvantiActivity;
import com.byndl.avantimarket.ui.adapter.AllSpecialsAdapter;

public class AllSpecialsFragment extends BaseFragment {

	private ListView transactionsList = null;
	private AllSpecialsAdapter allSpecialsAdapter = null;
	private View rootView = null;
	private HashMap<Integer, String> specialsMap = null;
	private List<Integer> sortedIndex = null;
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.all_specials_fragment, container, false);
		parentActivity = (MyAvantiActivity)getActivity();
		prepareHashMap();
		initialiseClassMembers();
		return rootView;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		((TextView)parentActivity.findViewById(R.id.m_navBarTitle)).setText(parentActivity.getResources().getString(R.string.my_market_specials).toUpperCase());
		((ImageButton)parentActivity.findViewById(R.id.m_menuButton)).setImageResource(R.drawable.arrow_left);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/**
	 * Sets up listeners to accept/respond to user events
	 */
	private void initialiseClassMembers() {
		transactionsList = (ListView) rootView.findViewById(R.id.transactions_list);
		transactionsList.setDivider(null);
		allSpecialsAdapter = new AllSpecialsAdapter(parentActivity,
				R.layout.all_specials_item, mALSpecials, sortedIndex);
		transactionsList.setAdapter(allSpecialsAdapter);
	}

	@Override
	public void onResume() {
		super.onResume();

		ImageButton menuButton = (ImageButton)parentActivity.findViewById(R.id.m_menuButton);
		menuButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				launchDashBoard();
			}			
		});

		getView().setFocusableInTouchMode(true);
		getView().requestFocus();
		getView().setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				Log.d("Avanti", "Support onKey");
				if( keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP ){
					launchDashBoard();
					return true;
				}
				return false;
			}
		});		
	}

	@Override
	public void updateUI() {

	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	private void prepareHashMap() {
		specialsMap = new HashMap<Integer, String>();
		for(int i=0; i < mALSpecials.size(); i++) {
			specialsMap.put(i, getSortableDate(mALSpecials.get(i).mEndDate)+mALSpecials.get(i).mProdName);
		}
		specialsMap = sortHashMapByValuesD(specialsMap);
		sortedIndex = new ArrayList<Integer>(specialsMap.keySet());
//		for(int i=0; i< sortedIndex.size(); i++) Log.e("temp", "Sorted Index: "+sortedIndex.get(i).intValue());
	}
	
	private String getSortableDate(String endDate) {
		String sortableDate = endDate.substring(endDate.lastIndexOf("/")+1);
		sortableDate = sortableDate + endDate.substring(0, endDate.indexOf("/"));
		sortableDate = sortableDate + endDate.substring(endDate.indexOf("/")+1, endDate.lastIndexOf("/"));
		return sortableDate;
	}

	public LinkedHashMap<Integer, String> sortHashMapByValuesD(HashMap<Integer, String> passedMap) {
		List<Integer> mapKeys = new ArrayList<Integer>(passedMap.keySet());
		List<String> mapValues = new ArrayList<String>(passedMap.values());
		Collections.sort(mapValues);
		Collections.sort(mapKeys);

		LinkedHashMap<Integer, String> sortedMap = new LinkedHashMap<Integer, String>();

		Iterator<String> valueIt = mapValues.iterator();
		while (valueIt.hasNext()) {
			Object val = valueIt.next();
			Iterator<Integer> keyIt = mapKeys.iterator();

			while (keyIt.hasNext()) {
				Object key = keyIt.next();
				String comp1 = passedMap.get(key).toString();
				String comp2 = val.toString();

				if (comp1.equals(comp2)){
					passedMap.remove(key);
					mapKeys.remove(key);
					sortedMap.put((Integer)key, (String)val);
					break;
				}

			}

		}
		return sortedMap;
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);
	}

	/**
	 * Launch DashBoard screen from current screen.
	 * 
	 */
	private void launchDashBoard() {
		FragmentTransaction transaction = parentActivity.getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_block, parentActivity.getMyAvantiFragment(), parentActivity.getMyAvantiFragment().getTag());
		transaction.commit();
	}
	
}
