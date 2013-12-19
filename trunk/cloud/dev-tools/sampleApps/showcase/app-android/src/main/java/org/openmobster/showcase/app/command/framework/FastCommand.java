/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.showcase.app.command.framework;

import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.api.ui.framework.command.AppException;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

/**
 * @author openmobster@gmail.com
 *
 */

public class FastCommand extends AsyncTask<Void,Void,Void>{

	Context context;
	Handler handler;
	Message message;
	
	public FastCommand(Context context,Handler handler){
		this.handler=handler;
		this.context=context;
	}
	
	
	@Override
	protected void onPostExecute(Void result){

		handler.sendMessage(message);
		
	}

	@Override
	protected void onPreExecute(){
		Toast.makeText(context,"Pre-Action Processing in progess",1).show();
	}

	@Override
	protected Void doInBackground(Void... arg0){
		
		try
		{
			//There should be no delay in execution here
			//Fast command must execute fast so that UI is not frozen
			//for too long
			Thread.sleep(1000);
			message=handler.obtainMessage();
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
}