/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.offlineApp.android.app.command;

import org.openmobster.android.api.sync.MobileBean;
import org.openmobster.core.mobileCloud.api.ui.framework.command.AppException;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

/**
 * This 'RemoteCommand' is executed during App startup from the 'HomeScreen'. It is used to start a 'Boot Sync' of the 'offlineapp_demochannel'
 * 
 * @author openmobster@gmail.com
 */

public class ChannelBootupHelper extends AsyncTask<Void,Void,Void>{
	Context context;
	ProgressDialog dialog = null;
	Handler handler;
	Message message;
	public ChannelBootupHelper(Context context,Handler handler){
		this.context=context;
		this.handler = handler;		
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
		try
		{
			//Wait for 30 seconds for bootstrapping...This should not hold up the App for too long. 'Boot Sync' is an innovation where
			//the basic beans required for App function are synchronized. Other beans get synchronized later in the background, without any
			//user intervention
			int counter = 30;
			while(!MobileBean.isBooted("offlineapp_demochannel"))
			{
				Thread.currentThread().sleep(1000);
				if(--counter == 0)
				{
					throw new AppException();
				}
			}
			message=handler.obtainMessage();
			message.what=1;
		}
		catch(Exception e)
		{
			if(e instanceof AppException)
			{
				throw (AppException)e;
			}
			throw new RuntimeException(e);
		}
		return null;
	}
}