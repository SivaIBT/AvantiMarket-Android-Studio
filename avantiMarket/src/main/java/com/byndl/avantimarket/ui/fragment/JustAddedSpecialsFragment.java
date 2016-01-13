package com.byndl.avantimarket.ui.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.byndl.avantimarket.R;
import com.byndl.avantimarket.ui.activity.MyAvantiActivity;
import com.byndl.avantimarket.ui.adapter.JustAddedSpecialsAdapter;

public class JustAddedSpecialsFragment extends BaseFragment {

	private ListView transactionsList = null;
	private JustAddedSpecialsAdapter justAddedSpecialsAdapter = null;
	private View rootView = null;
	private HashMap<Integer, String> specialsMap = null;
	private List<Integer> sortedIndex = null;
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.just_added_specials_fragment, container, false);
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
		removeOutOfScopeDates();
		transactionsList = (ListView) rootView.findViewById(R.id.transactions_list);
		transactionsList.setDivider(null);
		justAddedSpecialsAdapter = new JustAddedSpecialsAdapter(parentActivity,
				R.layout.just_added_specials_item, mALSpecials, specialsMap);
		transactionsList.setAdapter(justAddedSpecialsAdapter);
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
				if( keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP ){
					launchDashBoard();
					return true;
				}
				return false;
			}
		});		
		justAddedSpecialsAdapter.notifyDataSetChanged();
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
			specialsMap.put(i, getSortableDate(mALSpecials.get(i).mStartDate)+mALSpecials.get(i).mProdName);
//			specialsMap.put(i, getSortableDate(mALSpecials.get(i).mStartDate));
		}
		specialsMap = sortHashMapByValuesD(specialsMap, true);
		sortedIndex = new ArrayList<Integer>(specialsMap.keySet());
		for(int i=0; i< sortedIndex.size(); i++)
		{
			String value = specialsMap.get(sortedIndex.get(i));
			value = value.substring(0, 8);
//			Log.e("temp", "Just Added Sorted Index: "+sortedIndex.get(i).intValue()+" "+specialsMap.get(sortedIndex.get(i)));
			specialsMap.put(sortedIndex.get(i), value);
		}
		specialsMap = sortHashMapByValuesD(specialsMap, false);
		sortedIndex = new ArrayList<Integer>(specialsMap.keySet());
	}
	
	private String getSortableDate(String endDate) {
		String sortableDate = endDate.substring(endDate.lastIndexOf("/")+1);
		
		String month = endDate.substring(0, endDate.indexOf("/"));
		if(month.length() < 2) month = "0" + month;
		sortableDate = sortableDate + month;
		 
		String day = endDate.substring(endDate.indexOf("/")+1, endDate.lastIndexOf("/"));
		if(day.length() < 2) day = "0" + day;
		sortableDate = sortableDate + day;
		
		return sortableDate;
	}

	public LinkedHashMap<Integer, String> sortHashMapByValuesD(HashMap<Integer, String> passedMap, boolean ascending) {
		List<Integer> mapKeys = new ArrayList<Integer>(passedMap.keySet());
		List<String> mapValues = new ArrayList<String>(passedMap.values());
		
		if(ascending) {
			Collections.sort(mapValues);
			Collections.sort(mapKeys);
		}
		else {
			Collections.sort(mapValues, Collections.reverseOrder());
			Collections.sort(mapKeys, Collections.reverseOrder());
		}
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
		if(isVisibleToUser) {
//			initialiseClassMembers();
		}
	}
	
	private void removeOutOfScopeDates() {
		String todayDate, sixtiethDayDate;

		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		todayDate = sdf.format(date);
		todayDate = todayDate.replaceAll("-", "");
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_YEAR, -60);
		sixtiethDayDate = sdf.format(calendar.getTime());
		sixtiethDayDate = sixtiethDayDate.replaceAll("-", "");

		sortedIndex = new ArrayList<Integer>(specialsMap.keySet());
		for(int i=0; i< sortedIndex.size(); i++) {
			String offerBeginDate = specialsMap.get(sortedIndex.get(i));
			offerBeginDate = offerBeginDate.substring(0, 8);
			if(offerBeginDate.compareTo(todayDate) > 0 ||
					offerBeginDate.compareTo(sixtiethDayDate) < 0) {
				specialsMap.remove(sortedIndex.get(i));
			}
		}
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
