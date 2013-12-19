/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.offlineApp.android.app.command;

import java.util.HashMap;
import java.util.Map;
import org.openmobster.android.api.rpc.MobileService;
import org.openmobster.android.api.rpc.Request;
import org.openmobster.android.api.rpc.Response;
import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.api.ui.framework.command.AppException;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

/**
 * Demonstrates an 'Asynchronous' RPC invocation on the '/demo/mobile-rpc' service in the Cloud
 * 
 * @author openmobster@gmail.com
 *
 */

public class DemoMobileRPC extends AsyncTask<Void,Void,Void>{

	Context context;
	ProgressDialog dialog = null;
	Handler handler;
	Message message;
	
	public DemoMobileRPC(Context context,Handler handler){
		this.context=context;
		this.handler=handler;
	}
	
	@Override
	protected Void doInBackground(Void... arg0)
	{
		try
		{
			//Setting up the RPC request
			Request request = new Request("/demo/mobile-rpc");	
			request.setAttribute("param1", "paramValue1");
			request.setAttribute("param2", "paramValue2");
			
			//Making the RPC call
			Response response = new MobileService().invoke(request);
			message=handler.obtainMessage();
			
			Map map=new HashMap();
			map.put("param1",response.getAttribute("param1"));
			map.put("param2",response.getAttribute("param2"));			
			message.obj=map;
			message.what=1;
						
		}
		catch(Exception e)
		{
			AppException appe = new AppException();
			appe.setMessage(e.getMessage());
			ErrorHandler.getInstance().handle(appe);
			
			throw appe;
		}
		return null;
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
}