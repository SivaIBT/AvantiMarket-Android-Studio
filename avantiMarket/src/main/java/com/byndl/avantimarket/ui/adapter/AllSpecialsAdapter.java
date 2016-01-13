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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.byndl.avantimarket.R;
import com.byndl.avantimarket.event.AMSpecialsDao.SpecialsDao;
import com.byndl.avantimarket.ui.activity.MyAvantiActivity;

/**
 * @brief       Adapter class for All Specials fragment in Specials Container
 */
public class AllSpecialsAdapter extends ArrayAdapter<SpecialsDao>
{
	private int NORMAL_VIEW_TYPE = 1;
	private MyAvantiActivity context;
	private List<Integer> dateSortedIndex;
	private String runningYear;
	
	public AllSpecialsAdapter(Context context, int resource, ArrayList<SpecialsDao> objects, List<Integer> sortedIndex) 
	{
		super(context, resource, objects);
		this.context = (MyAvantiActivity) context;
		dateSortedIndex = sortedIndex;

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		runningYear = Integer.toString(calendar.get(Calendar.YEAR));
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
			convertView = inflator.inflate(com.byndl.avantimarket.R.layout.all_specials_item, null);
		}
		
		int index = dateSortedIndex.get(position).intValue();
		
		String endDate = getItem(index).mEndDate;
		if(endDate.contains(runningYear)) endDate = endDate.substring(0, endDate.length()-5);
		if(endDate.contains("201")) endDate = endDate.replace("201", "1");
		((TextView)convertView.findViewById(com.byndl.avantimarket.R.id.date)).setText("Ends "+endDate);
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
}