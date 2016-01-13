/**
 * @brief       Adapter class for List of items in Side Menu
 * @file        NavigationDrawerAdapter.java
 * @version     1.10
 * @author      siva.rajendhra
 * @date        02-Apr-2015
 * @copyright   incedo inc.
 * 
 */
package com.byndl.avantimarket.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * @brief       Adapter class for List of items in Side Menu
 */
public class NavigationDrawerAdapter extends ArrayAdapter<String>
{
	private int NORMAL_VIEW_TYPE = 1;
	public NavigationDrawerAdapter(Context context, int resource, String[] objects) 
	{
		super(context, resource, objects);
	}

	public int ViewTypeCount()
	{
		return 1; 
	}

	public boolean IsEnabled(int position)
	{
		if(getItem(position) == null || getItem(position).isEmpty())
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
		return super.getView(position, convertView, parent);
	}

	public int GetItemViewType(int position)
	{
		return NORMAL_VIEW_TYPE;
	}
}