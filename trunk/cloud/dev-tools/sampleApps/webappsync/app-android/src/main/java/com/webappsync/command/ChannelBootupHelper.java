/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.webappsync.command;

import org.openmobster.android.api.sync.MobileBean;
import org.openmobster.core.mobileCloud.api.ui.framework.command.AppException;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

/**
 * Checks to make sure that the data is loaded in the sync channel used by the App
 * 
 * @author openmobster@gmail.com
 */

public class ChannelBootupHelper extends AsyncTask<Void,Void,Void>{

	Context context;
	Handler handler;
	ProgressDialog progressDialog;
	Message message;
	public ChannelBootupHelper(Context context,Handler handler){
		this.handler=handler;
		this.context=context;
		
	}
	
	@Override
	protected void onPostExecute(Void result){
		progressDialog.dismiss();
		handler.sendMessage(message);
	}

	@Override
	protected void onPreExecute(){
		progressDialog=new ProgressDialog(context);
		progressDialog.setMessage("Please wait...");
		progressDialog.setCancelable(false);
		progressDialog.show();
	}

	@Override
	protected Void doInBackground(Void... arg0){
		
		try
		{
		    int counter = 5;
			while(!MobileBean.isBooted("webappsync_ticket_channel"))
			{
				Thread.currentThread().sleep(1000);
				if(counter-- == 0)
				{
				    throw new RuntimeException();
				}
			}
		}
		catch(Exception e)
		{
			throw new AppException();
		}
		
		message=handler.obtainMessage();
		message.what=1;
		
		return null;
	}	
}