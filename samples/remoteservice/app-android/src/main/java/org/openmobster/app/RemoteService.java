/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.app;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.Toast;

/**
 * 
 * @author openmobster@gmail.com
 */
public class RemoteService extends Service
{
	private Messenger messenger; //receives remote invocations
	
	@Override
	public IBinder onBind(Intent intent) 
	{
		if(this.messenger == null)
		{
			synchronized(RemoteService.class)
			{
				if(this.messenger == null)
				{
					this.messenger = new Messenger(new IncomingHandler());
				}
			}
		}
		//Return the proper IBinder instance
		return this.messenger.getBinder();
	}
	
	private class IncomingHandler extends Handler
	{
		@Override
        public void handleMessage(Message msg) 
		{
			System.out.println("*****************************************");
			System.out.println("Remote Service successfully invoked!!!!!!");
			System.out.println("*****************************************");
			
			int what = msg.what;
			
			Toast.makeText(RemoteService.this.getApplicationContext(), "Remote Service invoked-("+what+")", Toast.LENGTH_LONG).show();
			
			//Setup the reply message
			Message message = Message.obtain(null, 2, 0, 0);
			try
			{
				//make the RPC invocation
				Messenger replyTo = msg.replyTo;
				replyTo.send(message);
			}
			catch(RemoteException rme)
			{
				//Show an Error Message
				Toast.makeText(RemoteService.this, "Invocation Failed!!", Toast.LENGTH_LONG).show();
			}
        }
	}
}
