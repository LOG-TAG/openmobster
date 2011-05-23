/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.app;

import java.lang.reflect.Field;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.os.Messenger;
import android.content.ServiceConnection;
import android.os.Message;
import android.os.RemoteException;

import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;


/**
 * @author openmobster@gmail.com
 * 
 */
public class MainActivity extends Activity
{
	private Messenger messenger = null; //used to make an RPC invocation
	private boolean isBound = false;
	private ServiceConnection connection;//receives callbacks from bind and unbind invocations
	private Messenger replyTo = null; //invocation replies are processed by this Messenger
	
	public MainActivity()
	{
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.connection = new RemoteServiceConnection();
		this.replyTo = new Messenger(new IncomingHandler());
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		
		//Bind to the remote service
		Intent intent = new Intent();
		intent.setClassName("org.openmobster.remote.service.android.app", "org.openmobster.app.RemoteService");
		
		this.bindService(intent, this.connection, Context.BIND_AUTO_CREATE);
	}
	
	@Override
	protected void onStop() 
	{
		super.onStop();
		
		//Unbind if it is bound to the service
		if(this.isBound)
		{
			this.unbindService(connection);
			this.isBound = false;
		}
	}

	@Override
	protected void onResume()
	{
		try
		{
			super.onResume();
			
			//render the main screen
			String layoutClass = this.getPackageName()+".R$layout";
			String main = "main";
			Class clazz = Class.forName(layoutClass);
			Field field = clazz.getField(main);
			int screenId = field.getInt(clazz);
			this.setContentView(screenId);
			
			//Invoke Remote button
			Button invokeButton = (Button)ViewHelper.findViewById(this, 
			"invoke");
			invokeButton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View button) 
				{
					if(MainActivity.this.isBound)
					{
						//Setup the message for invocation
						Message message = Message.obtain(null, 1, 0, 0);
						try
						{
							//Set the ReplyTo Messenger for processing the invocation response
							message.replyTo = MainActivity.this.replyTo;
							
							//Make the invocation
							MainActivity.this.messenger.send(message);
						}
						catch(RemoteException rme)
						{
							//Show an Error Message
							Toast.makeText(MainActivity.this, "Invocation Failed!!", Toast.LENGTH_LONG).show();
						}
					}
					else
					{
						Toast.makeText(MainActivity.this, "Service is Not Bound!!", Toast.LENGTH_LONG).show();
					}
				}
			  }
			);
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
	}
	
	private class RemoteServiceConnection implements ServiceConnection
	{
		@Override
		public void onServiceConnected(ComponentName component, IBinder binder) 
		{	
			MainActivity.this.messenger = new Messenger(binder);
			
			MainActivity.this.isBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName component) 
		{	
			MainActivity.this.messenger = null;
			
			MainActivity.this.isBound = false;
		}
	}
	
	private class IncomingHandler extends Handler
	{
		@Override
        public void handleMessage(Message msg) 
		{
			System.out.println("*****************************************");
			System.out.println("Return successfully received!!!!!!");
			System.out.println("*****************************************");
			
			int what = msg.what;
			
			Toast.makeText(MainActivity.this.getApplicationContext(), "Remote Service replied-("+what+")", Toast.LENGTH_LONG).show();
        }
	}
}
