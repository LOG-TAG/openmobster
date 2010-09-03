/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.widgets.framework;

import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;

/**
 * @author openmobster@gmail.com
 */
public class SpinnerAdapter extends ArrayAdapter<CharSequence>
{
	private List<String> options;
	
	public SpinnerAdapter(Context context, int textViewResourceId, List<String> options)
	{
		super(context,textViewResourceId);
		this.options = options;
	}
	
	public int getCount() 
	{
		if(this.options != null)
		{
			return this.options.size();
		}
		return 0;
	}

	public CharSequence getItem(int position) 
	{
		if(this.options != null)
		{
			return this.options.get(position);
		}
		return null;
	}

	@Override
	public String toString() 
	{
		if(this.options != null && this.options.size()>0)
		{
			StringBuilder buffer = new StringBuilder();
			
			buffer.append("----------------------------------\n");
			for(String local:this.options)
			{
				buffer.append("Spinner Option: "+local+"\n");
			}
			
			return buffer.toString();
		}
		return super.toString();
	}
}
