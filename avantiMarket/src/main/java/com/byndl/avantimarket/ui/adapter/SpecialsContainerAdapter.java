/**
 * @brief       Adapter class for setting Specials Pager
 * @file        SpecialsAdapter.java
 * @version     1.1
 * @author      siva.rajendhra
 * @date        06-Nov-2015
 * @copyright   incedo inc.
 * 
 */
package com.byndl.avantimarket.ui.adapter;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.byndl.avantimarket.ui.fragment.AllSpecialsFragment;
import com.byndl.avantimarket.ui.fragment.ExpiringSoonSpecialsFragment;
import com.byndl.avantimarket.ui.fragment.JustAddedSpecialsFragment;
import com.byndl.avantimarket.ui.fragment.MyProgressSpecialsFragment;
import com.viewpagerindicator.IconPagerAdapter;

/**
 * @brief       Adapter class for setting Specials Pager.
 * 
*/
public class SpecialsContainerAdapter extends FragmentStatePagerAdapter implements IconPagerAdapter {
	
	protected static final String[] CONTENT = new String[] { "ALL OFFERS", " MY PROGRESS ", " EXPIRING SOON ", " JUST ADDED"};
	private AllSpecialsFragment allSpecialsFragment = null;
	private JustAddedSpecialsFragment justAddedSpecialsFragment = null;
	private ExpiringSoonSpecialsFragment expiringSoonSpecialsFragment = null;
	private MyProgressSpecialsFragment myProgressSpecialsFragment = null;
	
	/**
	 * Constructor
	 * 
	 * @param theContext - Application context.
	 * @param objects - Objects to display.
	 * @param theLayoutResId - Layout res id.
	 */
	public SpecialsContainerAdapter(FragmentManager fragmentManager) {
	    super(fragmentManager);
	    allSpecialsFragment = new AllSpecialsFragment();
	    justAddedSpecialsFragment = new JustAddedSpecialsFragment();
	    expiringSoonSpecialsFragment = new ExpiringSoonSpecialsFragment();
		myProgressSpecialsFragment = new MyProgressSpecialsFragment();
}

	@Override
	public int getCount() {
		return CONTENT.length;
	}

	@Override
	public CharSequence getPageTitle(int position) {

		return CONTENT[position % CONTENT.length].toUpperCase();
	}

	@Override
	public Fragment getItem(int position) {
		switch (position) {
		case 0:
			return allSpecialsFragment;
			
		case 1:
			return myProgressSpecialsFragment;			
			
		case 2:
			return expiringSoonSpecialsFragment;
			
		case 3:
			return justAddedSpecialsFragment;
			
		default:
			break;
		}
		return null;
	}

	@Override
	public int getIconResId(int index) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
		
	}
}
