/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.async.service.app.cloud.app.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
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
 * This particular AsyncTask, invokes the 'GetList' service in the Cloud and gets a list of email 'Subject' values for display.
 * 
 * @author openmobster@gmail.com
 *
 */

public class AsyncGetList extends AsyncTask<Void,Void,Void>{

	Context context;
	ProgressDialog dialog = null;
	Handler handler;
	Message message;
	
	public AsyncGetList(Context context,Handler handler){
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

		try	{
			// Setting up the Cloud Request object
			Request request = new Request("/asyncserviceapp/getlist");
			Response response = new MobileService().invoke(request);

			// Read the data from the Cloud Response
			Vector<String> list = response.getListAttribute("subjects");

			// Create a list of Email beans
			List<EmailBean> emails = new ArrayList<EmailBean>();
			for (String local : list)
			{
				EmailBean email = new EmailBean();

				String[] tokens = local.split(":");

				String idToken = tokens[0];
				String oid = idToken.substring(idToken.indexOf('=') + 1);
				email.setOid(oid);

				String subjectToken = tokens[1];
				String subject = subjectToken.substring(subjectToken.indexOf('=') + 1);
				email.setSubject(subject);

				emails.add(email);
			}
			message=handler.obtainMessage();
			message.what=1;
			message.obj=emails;
			
		} 
		catch (Exception e){
			// throw an AppException. If this happens, the doViewError will be
			// invoked to alert the user with an error message
			AppException appe = new AppException();
			appe.setMessage(e.getMessage());

			// Record this error in the Cloud Error Log
			ErrorHandler.getInstance().handle(appe);

			throw appe;
		}
		return null;
	}	
}