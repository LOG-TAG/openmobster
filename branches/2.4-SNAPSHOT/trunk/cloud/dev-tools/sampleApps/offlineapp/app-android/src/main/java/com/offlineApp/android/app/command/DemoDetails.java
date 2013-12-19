/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.offlineApp.android.app.command;

import org.openmobster.android.api.sync.MobileBean;
import org.openmobster.core.mobileCloud.android.util.GenericAttributeManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

/**
 * Invoked from the 'HomeScreen' to show the details associated with a locally stored/synchronized bean
 * 
 * This is a 'LocalCommand' as it executes very fast, since it works with locally stored data.
 * 
 * @author openmobster@gmail.com
 *
 */

public class DemoDetails extends AsyncTask<Void,Void,Void>{
	
	Context context;
	ProgressDialog dialog = null;
	Handler handler;
	Message message;
	String selectedBean;
	public DemoDetails(Context context,Handler handler,String selectedBean){
		this.context=context;
		this.handler = handler;
		this.selectedBean=selectedBean;
	}
	
	@Override
	protected void onPostExecute(Void result)
	{
		dialog.dismiss();
		handler.sendMessage(message);
	}

	@Override
	protected void onPreExecute()
	{
		dialog = new ProgressDialog(context);		
		dialog.setMessage("Please wait...");
		dialog.setCancelable(false);
		dialog.show();	
	}

	@Override
	protected Void doInBackground(Void... arg0)
	{
		message = handler.obtainMessage();		
		try{
			
			String channel = "offlineapp_demochannel";	
						
			String details = null;
			
			//Lookup by state..in this case, that of 'demoString' field of the bean
			GenericAttributeManager criteria = new GenericAttributeManager();
			criteria.setAttribute("demoString", selectedBean);
			MobileBean[] beans = MobileBean.queryByEqualsAll(channel, criteria);
			MobileBean unique = beans[0];
		
			//Sets up the String that will be displayed
			StringBuffer buffer = new StringBuffer();
			buffer.append("DemoString: "+unique.getValue("demoString"));
			details = buffer.toString();
					
			message.what = 1;
			message.obj=details;
		}catch(Exception ex){
			
		}
		return null;
	}
}