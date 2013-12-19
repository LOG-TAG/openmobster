/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.async.service.app.cloud.app.command;

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
 * The AsyncTask. The 'AsyncTask' is used to perform actions asynchronously (Ajax from the web world). 
 * 
 * This particular AsyncTask, invokes the 'GetDetails' service in the Cloud and gets a fully populated Email instance for display.
 * 
 * @author openmobster@gmail.com
 *
 */

public class AsyncGetDetails extends AsyncTask<Void,Void,Void>{

	EmailBean email;
	Context context;
	ProgressDialog dialog = null;
	Handler handler;
	Message message;
	
	public AsyncGetDetails(Context context,Handler handler,EmailBean emailBean){
		email=emailBean;
		this.context=context;
		this.handler = handler;	
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
		//Populate the Cloud Request
		try{
			Request request = new Request("/asyncserviceapp/getdetails");	
			request.setAttribute("oid", email.getOid());
		
			Response response = new MobileService().invoke(request);
		
			//Process the Cloud Response
			String oid = response.getAttribute("oid");
			String from = response.getAttribute("from");
			String to = response.getAttribute("to");
			String subject = response.getAttribute("subject");
			String date = response.getAttribute("date");
		
			email.setOid(oid);
			email.setFrom(from);
			email.setTo(to);
			email.setSubject(subject);
			email.setDate(date);
			
			message= handler.obtainMessage();
			message.what=1;
			
			StringBuilder buffer = new StringBuilder();
			buffer.append("From: "+email.getFrom()+"\n");
			buffer.append("To: "+email.getTo()+"\n");
			buffer.append("Date: "+email.getDate()+"\n\n");
			buffer.append("Subject: "+email.getSubject());
			
			message.obj=buffer.toString();
			
			
		}catch(Exception e){
			//throw an AppException. If this happens, the doViewError will be invoked to alert the user with an error message
			AppException appe = new AppException();
			appe.setMessage(e.getMessage());
			
			//Record this error in the Cloud Error Log
			ErrorHandler.getInstance().handle(appe);			
			throw appe;
		}
		return null;
	}	
}