/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.app;

import org.openmobster.android.api.sync.MobileBean;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

/**
 * 
 * @author openmobster@gmail.com
 */

public class ChannelBootupHelper extends AsyncTask<Void,Void,Void>{

	Context context;
	ProgressDialog dialog = null;
	Handler handler;
	Message message;
	MobileBean mobileBean;
	
	public ChannelBootupHelper(Context context,Handler handler,MobileBean mobileBean){
		this.context=context;
		this.handler = handler;
		this.mobileBean=mobileBean;		
	}
	
	@Override
	protected void onPostExecute(Void result){
		dialog.dismiss();
		handler.sendMessage(message);
	}

	@Override
	protected void onPreExecute(){
		dialog = new ProgressDialog(context);		
		dialog.setMessage("Please wait...");
		dialog.setCancelable(false);
		dialog.show();
	}

	@Override
	protected Void doInBackground(Void... arg0){
		try
		{
			while(!MobileBean.isBooted("cloud_channel"))
			{
				Thread.sleep(1000);
			}
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}		
		
		return null;
	}	
}