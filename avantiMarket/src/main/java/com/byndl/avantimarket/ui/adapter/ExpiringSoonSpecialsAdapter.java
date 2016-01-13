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
import android.graphics.Color;
import android.graphics.Typeface;
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
public class ExpiringSoonSpecialsAdapter extends ArrayAdapter<SpecialsDao>
{
	private Date today = null;
	private int NORMAL_VIEW_TYPE = 1;
	private MyAvantiActivity context;
	private List<Integer> dateSortedIndex;
	private HashMap<Integer, String> dateMap;
	private HashMap<Integer, String> expiryHeader = null;
	private static int todayHeadingPosition = -1, sevenHeadingPosition = -1, thirtyHeadingPosition = -1;
	private static boolean todayHeadingVisible = false, sevenHeadingVisible = false, thirtyHeadingVisible = false;
	private String todayDate, tomorrowDate, seventhDayDate, thirtiethDayDate, runningYear;
	
	public ExpiringSoonSpecialsAdapter(Context context, int resource, ArrayList<SpecialsDao> objects, HashMap<Integer, String> dayMap, Button fixedHeader) 
	{
		super(context, resource, objects);
		this.context = (MyAvantiActivity) context;
		
		dateMap = dayMap;
		dateSortedIndex = new ArrayList<Integer>(dateMap.keySet());
//		for(int i=0; i< dateSortedIndex.size(); i++) Log.e("temp", "Adapter Sorted Index: "+dateSortedIndex.get(i).intValue()+ " "+dateMap.get(dateSortedIndex.get(i)));
		today = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		todayDate = sdf.format(today);
		todayDate = todayDate.replaceAll("-", "");
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(today);
		runningYear = Integer.toString(calendar.get(Calendar.YEAR));
		
		calendar.add(Calendar.DAY_OF_YEAR, +1);
		tomorrowDate = sdf.format(calendar.getTime());
		tomorrowDate = tomorrowDate.replaceAll("-", "");

		calendar.add(Calendar.DAY_OF_YEAR, +6);
		seventhDayDate = sdf.format(calendar.getTime());
		seventhDayDate = seventhDayDate.replaceAll("-", "");

		calendar.add(Calendar.DAY_OF_YEAR, +23);
		thirtiethDayDate = sdf.format(calendar.getTime());
		thirtiethDayDate = thirtiethDayDate.replaceAll("-", "");
		
		expiryHeader = new HashMap<Integer, String>();
//		Log.e("temp", "Dates: \n"+todayDate +"\n"+ tomorrowDate +"\n"+ seventhDayDate +"\n"+ thirtiethDayDate);
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

	public  View getView(int position, View convertView, ViewGroup parent)
	{
		if(convertView == null) {
			LayoutInflater inflator = ((Activity)context).getLayoutInflater();
			convertView = inflator.inflate(com.byndl.avantimarket.R.layout.expiring_soon_specials_item, null);
		}
		
		if(position >= dateSortedIndex.size()) {
			convertView.setVisibility(View.GONE);
			return convertView;
		}
				
		setExpirySpan(position, convertView);
		
		int index = dateSortedIndex.get(position).intValue();
		SpecialsDao item = getItem(index);
		int dayDiff = getDateDifference(item.mEndDate);
		TextView ends = ((TextView)convertView.findViewById(com.byndl.avantimarket.R.id.date));
		if(dayDiff > 7) {
			String endDate = item.mEndDate;
			if(endDate.contains(runningYear)) endDate = endDate.substring(0, endDate.length()-5);
			if(endDate.contains("201")) endDate = endDate.replace("201", "1");
			ends.setText("Ends "+endDate);
			ends.setTextColor(Color.BLACK);
		}
		else if(dayDiff == 1) {
			ends.setText("Only 1 day left!");
			ends.setTextColor(Color.rgb(238, 128, 59));
		}
		else if(dayDiff > 1 && dayDiff <= 7) {
			ends.setText("Only "+dayDiff+" days left!");
			ends.setTextColor(Color.rgb(238, 128, 59));
		}

//		((TextView)convertView.findViewById(com.byndl.avantimarket.R.id.item)).setText(item.mProdName);
//		((TextView)convertView.findViewById(com.byndl.avantimarket.R.id.offer)).setText(item.mOfferDesc);
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
		if(itemDate.contains(todayDate)) {
			expirySpan.setText("EXPIRING TODAY ");
			if(todayHeadingPosition == position) {
				expirySpan.setVisibility(View.VISIBLE);
			}
			else {
				if(todayHeadingVisible) {
					expirySpan.setVisibility(View.GONE);
				}
				else {
					todayHeadingPosition = position;
					todayHeadingVisible = true;
				}
			}
		}
		if(itemDate.compareTo(todayDate) > 0 &&
				itemDate.compareTo(seventhDayDate) < 0) {
			expirySpan.setText("EXPIRING IN 7 DAYS");
			if(sevenHeadingPosition == position) {
				expirySpan.setVisibility(View.VISIBLE);
			}
			else {
				if(sevenHeadingVisible) {
					expirySpan.setVisibility(View.GONE);
				}
				else {
					sevenHeadingPosition = position;
					sevenHeadingVisible = true;
				}
			}
		}
		if(itemDate.compareTo(seventhDayDate) >= 0 &&
				itemDate.compareTo(thirtiethDayDate) <= 0) {
			expirySpan.setText("EXPIRING IN 30 DAYS");
			if(thirtyHeadingPosition == position) {
				expirySpan.setVisibility(View.VISIBLE);
			}
			else {
				if(thirtyHeadingVisible) {
					expirySpan.setVisibility(View.GONE);
				}
				else {
					thirtyHeadingPosition = position;
					thirtyHeadingVisible = true;
				}
			}
		}
		
		expiryHeader.put(dateSortedIndex.get(position), expirySpan.getText().toString());
	}
	
	@Override
	public int getCount() {
//		return super.getCount();
		return dateSortedIndex.size();
	}

	
	private int getDateDifference(String endDate) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
		if(endDate.length() < 10) {
			String year = endDate.substring(endDate.lastIndexOf("/")+1);

			String month = endDate.substring(0, endDate.indexOf("/"));
			if(month.length() < 2) month = "0" + month;

			String day = endDate.substring(endDate.indexOf("/")+1, endDate.lastIndexOf("/"));
			if(day.length() < 2) day = "0" + day;

			endDate = month+"\\"+day+"\\"+year;
		}
		try {
			Date lastDay = simpleDateFormat.parse(endDate);
			long diff = lastDay.getTime() - today.getTime();
			long days = diff / (24 * 60 * 60 * 1000);
			if(days == 0) {
				Calendar lastDayCalendar = Calendar.getInstance(); lastDayCalendar.setTime(lastDay);
				Calendar todayCalendar = Calendar.getInstance(); todayCalendar.setTime(today);
				if(lastDayCalendar.get(Calendar.DAY_OF_MONTH) != todayCalendar.get(Calendar.DAY_OF_MONTH)) days = 1;
				return (int) (days+1);
			}
			else {
				return (int) (days+2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
}