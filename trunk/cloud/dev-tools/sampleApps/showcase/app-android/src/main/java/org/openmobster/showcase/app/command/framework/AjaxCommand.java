/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.showcase.app.command.framework;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

/**
 * @author openmobster@gmail.com
 *
 */

public class AjaxCommand extends AsyncTask<Void,Void,Void>{

	Context context;
	Handler handler;
	Message message;
			
	public AjaxCommand(Context context,Handler handler){
		this.handler=handler;
		this.context=context;
	}
	
	@Override
	protected void onPostExecute(Void result){		
		handler.sendMessage(message);		
	}

	@Override
	protected void onPreExecute(){
		Toast.makeText(context,"Pre-Action Processing in progess", 1).show();
	}

	@Override
	protected Void doInBackground(Void... arg0){		
		message = handler.obtainMessage();		
		try{
			Thread.sleep(1000);
			message.what = 1;
		}catch(Exception ex){
			
		}		
		return null;
	}	
}