/**
 * @brief       Adapter class for All Specials fragment in Specials Container
 * @file        AllSpecialsAdapter.java
 * @version     1.2
 * @author      siva.rajendhra
 * @date        17-Nov-2015
 * @copyright   incedo inc.
 * 
 */
package com.byndl.avantimarket.ui.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.byndl.avantimarket.R;
import com.byndl.avantimarket.event.AMSpecialsDao.SpecialsDao;
import com.byndl.avantimarket.ui.activity.MyAvantiActivity;

/**
 * @brief       Adapter class for All Specials fragment in Specials Container
 */
public class JustAddedSpecialsAdapter extends ArrayAdapter<SpecialsDao>
{
	private int NORMAL_VIEW_TYPE = 1;
	private MyAvantiActivity context;
	private List<Integer> dateSortedIndex;
	private HashMap<Integer, String> dateMap;
	private static int sevenHeadingPosition = -1, thirtyHeadingPosition = -1, sixtyHeadingPosition = -1;
	private static boolean sevenHeadingVisible = false, thirtyHeadingVisible = false, sixtyHeadingVisible = false;
	private String todayDate, seventhDayDate, thirtiethDayDate, sixtiethDayDate, runningYear;

	public JustAddedSpecialsAdapter(Context context, int resource, ArrayList<SpecialsDao> objects, HashMap<Integer, String> dayMap) 
	{
		super(context, resource, objects);
		this.context = (MyAvantiActivity) context;
		dateMap = dayMap;
		dateSortedIndex = new ArrayList<Integer>(dateMap.keySet());
//		for(int i=0; i< dateSortedIndex.size(); i++) Log.e("temp", "Adapter Sorted Index: "+dateSortedIndex.get(i).intValue()+ " "+dateMap.get(dateSortedIndex.get(i)));
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		todayDate = sdf.format(date);
		todayDate = todayDate.replaceAll("-", "");

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		runningYear = Integer.toString(calendar.get(Calendar.YEAR));

		calendar.add(Calendar.DAY_OF_YEAR, -7);
		seventhDayDate = sdf.format(calendar.getTime());
		seventhDayDate = seventhDayDate.replaceAll("-", "");

		calendar.add(Calendar.DAY_OF_YEAR, -23);
		thirtiethDayDate = sdf.format(calendar.getTime());
		thirtiethDayDate = thirtiethDayDate.replaceAll("-", "");

		calendar.add(Calendar.DAY_OF_YEAR, -30);
		sixtiethDayDate = sdf.format(calendar.getTime());
		sixtiethDayDate = sixtiethDayDate.replaceAll("-", "");

		Log.e("temp", "Dates: \n"+todayDate +"\n"+ seventhDayDate +"\n"+ thirtiethDayDate +"\n"+ sixtiethDayDate );
	}

	public int ViewTypeCount()
	{
		return 1; 
	}

	public boolean IsEnabled(int position)
	{
		if(getItem(position) == null)
		{
			return false;
		}
		else
		{
			return super.isEnabled(position);
		}
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{
		if(convertView == null) {
			LayoutInflater inflator = ((Activity)context).getLayoutInflater();
			convertView = inflator.inflate(com.byndl.avantimarket.R.layout.just_added_specials_item, null);
		}

		if(position >= dateSortedIndex.size()) {
			convertView.setVisibility(View.GONE);
			return convertView;
		}

		setExpirySpan(position, convertView);
		int index = dateSortedIndex.get(position).intValue();

		String endDate = getItem(index).mEndDate;
		if(endDate.contains(runningYear)) endDate = endDate.substring(0, endDate.length()-5);
		if(endDate.contains("201")) endDate = endDate.replace("201", "1");
		((TextView)convertView.findViewById(com.byndl.avantimarket.R.id.date)).setText("Ends "+endDate);
//		((TextView)convertView.findViewById(com.byndl.avantimarket.R.id.item)).setText(getItem(index).mProdName);
//		((TextView)convertView.findViewById(com.byndl.avantimarket.R.id.offer)).setText(getItem(index).mOfferDesc);
		TextView offer = ((TextView)convertView.findViewById(com.byndl.avantimarket.R.id.offer));
		if( ! getItem(index).mRequiredAmount.equalsIgnoreCase("0")) {
			String desc = getItem(index).mOfferDesc;
			int offerIndex = desc.indexOf("get ");
			if(offerIndex == -1) offerIndex = desc.indexOf("Get ");
			if(offerIndex == -1) offerIndex = desc.indexOf("GET ");
			
			if(offerIndex != -1) {
				((TextView)convertView.findViewById(com.byndl.avantimarket.R.id.item)).setText(desc.substring(0, offerIndex));
				offer.setText(desc.substring(offerIndex, desc.length()));
				offer.setTextSize(TypedValue.COMPLEX_UNIT_PX, ((Activity)context).getResources().getDimension(R.dimen.regular_text_size));
				offer.setTypeface(offer.getTypeface(), Typeface.BOLD);
			}
		}
		else {
			((TextView)convertView.findViewById(com.byndl.avantimarket.R.id.item)).setText(getItem(index).mProdName);
			offer.setText(getItem(index).mOfferDesc);
			offer.setTextSize(TypedValue.COMPLEX_UNIT_PX, ((Activity)context).getResources().getDimension(R.dimen.semi_regular_text_size));
			offer.setTypeface(Typeface.DEFAULT);
		}

		return convertView;
		//		return super.getView(position, convertView, parent);
	}

	public int GetItemViewType(int position)
	{
		return NORMAL_VIEW_TYPE;
	}

	private void setExpirySpan(int position, View convertView) {
		String itemDate = dateMap.get(dateSortedIndex.get(position));
		itemDate = itemDate.substring(0, 8);
		Button expirySpan = (Button)convertView.findViewById(com.byndl.avantimarket.R.id.m_expiringSpanButton);
		if(itemDate.compareTo(todayDate) <= 0 &&
				itemDate.compareTo(seventhDayDate) >= 0) {
			if(sevenHeadingPosition == position) {
				expirySpan.setText("ADDED IN THE LAST 7 DAYS");
				expirySpan.setVisibility(View.VISIBLE);
			}
			else {
				if(sevenHeadingVisible) {
					expirySpan.setVisibility(View.GONE);
				}
				else {
					expirySpan.setText("ADDED IN THE LAST 7 DAYS");
					sevenHeadingPosition = position;
					sevenHeadingVisible = true;
				}
			}
		}
		if(itemDate.compareTo(seventhDayDate) < 0 &&
				itemDate.compareTo(thirtiethDayDate) >= 0) {
			if(thirtyHeadingPosition == position) {
				expirySpan.setText("ADDED IN THE LAST 30 DAYS");
				expirySpan.setVisibility(View.VISIBLE);
			}
			else {
				if(thirtyHeadingVisible) {
					expirySpan.setVisibility(View.GONE);
				}
				else {
					expirySpan.setText("ADDED IN THE LAST 30 DAYS");
					thirtyHeadingPosition = position;
					thirtyHeadingVisible = true;
				}
			}
		}
		if(itemDate.compareTo(thirtiethDayDate) < 0 &&
				itemDate.compareTo(sixtiethDayDate) >= 0) {
			if(sixtyHeadingPosition == position) {
				expirySpan.setText("ADDED IN THE LAST 60 DAYS");
				expirySpan.setVisibility(View.VISIBLE);
			}
			else {
				if(sixtyHeadingVisible) {
					expirySpan.setVisibility(View.GONE);
				}
				else {
					expirySpan.setText("ADDED IN THE LAST 60 DAYS");
					sixtyHeadingPosition = position;
					sixtyHeadingVisible = true;
				}
			}
		}
	}

	@Override
	public int getCount() {
		//		return super.getCount();
		return dateSortedIndex.size();
	}
}