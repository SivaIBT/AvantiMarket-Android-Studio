/**
 * @brief       Adapter class for setting Hint on dropdown (spinner)
 * @file        HintAdapter.java
 * @version     1.0
 * @author      pawan.bawankar
 * @date        15-Apr-2015
 * @copyright   incedo inc.
 * 
 */
package com.byndl.avantimarket.ui.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;

/**
 * @brief       Adapter class for setting Hint on dropdown (spinner). The hint is not a selectable item.
 * 
*/
public class HintAdapter extends ArrayAdapter<String> {
	
	/**
	 * Constructor
	 * 
	 * @param theContext - Application context.
	 * @param objects - Objects to display.
	 * @param theLayoutResId - Layout res id.
	 */
	public HintAdapter(Context theContext, String[] objects, int theLayoutResId) {
	    super(theContext, theLayoutResId, objects);
	}

	@Override
	public int getCount() {
	    // don't display last item. It is used as hint.
	    int count = super.getCount();
	    return count > 0 ? count - 1 : count;
	}

}
