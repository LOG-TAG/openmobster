/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.module.connection;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.HashMap;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android.service.Service;
import org.openmobster.core.mobileCloud.android.util.Base64;
import org.openmobster.core.mobileCloud.android.util.StringUtil;
import org.openmobster.core.mobileCloud.android.util.XMLUtil;
import org.openmobster.core.mobileCloud.android.module.bus.Bus;
import org.openmobster.core.mobileCloud.android.module.bus.PushRPCInvocation;
import org.openmobster.core.mobileCloud.android.module.bus.SyncInvocation;
import org.openmobster.core.mobileCloud.android.util.GeneralTools;

/**
 * 
 * @author openmobster@gmail.com
 *
 */
public final class CommandProcessor extends Service 
{	
	
	public CommandProcessor()
	{
		
	}
	
	public void start() 
	{	
	}
	
	public void stop() 
	{			
	}
	
	
	public static CommandProcessor getInstance()
	{
		return (CommandProcessor)Registry.getActiveInstance().
		lookup(CommandProcessor.class);
	}
	
	
	public void process(String command)
	{
		//Process the Command on a separate thread, not on the Notification Listening Thread		
		String[] tokens = StringUtil.tokenize(command, Constants.separator);
		Map<String,String> input = new HashMap<String,String>();
		
		if(tokens != null)
		{
			for(String token:tokens)
			{
				int index = token.indexOf('=');
				String name = token.substring(0, index);
				String value = token.substring(index+1);
				input.put(name.trim(), value.trim());
			}
			
			if(input.get(Constants.command)!=null)
			{
				String inputCommand = input.get(Constants.command);
				if(inputCommand.equals(Constants.sync))
				{
					this.sync(input);
				}
				else if(inputCommand.equals(Constants.pushrpc))
				{
					this.pushrpc(input);
				}
				else if(inputCommand.equals(Constants.push))
				{
					this.push(input);
				}
			}
		}
	}
		
	private void sync(Map<String,String> input)
	{
		Thread t = new Thread(new SyncCommandHandler(input));
		t.start();		
	}
	
	private void pushrpc(Map<String,String> input)
	{
		Thread t = new Thread(new PushRPCHandler(input));
		t.start();	
	}
	
	private void push(Map<String,String> input)
	{
		Thread t = new Thread(new PushCommandHandler(input));
		t.start();
	}
	
	private static class PushCommandHandler implements Runnable
	{
		private Map<String,String> input;
		
		private PushCommandHandler(Map<String,String> input)
		{
			this.input = input;
		}
		
		public void run()
		{
			try
			{
				String message = input.get("message");
				String extras = input.get("extras");
				Map<String, String> extrasData = XMLUtil.parseMap(extras);
				
				String title = extrasData.get("title");
				String details = extrasData.get("detail");
				
				if(title == null || title.trim().length()==0)
				{
					title = message;
				}
				
				if(details == null || details.trim().length()==0)
				{
					details = "";
				}
				
				//Get the Notification Service
				Context context = Registry.getActiveInstance().getContext();
				NotificationManager notifier = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
				
				//Get the icon for the notification
				int icon = this.findDrawableId(context, "icon");
				Notification notification = new Notification(icon,message,System.currentTimeMillis());
				
				//Setup the Intent to open this Activity when clicked
				Intent toLaunch = null;
				PendingIntent contentIntent = PendingIntent.getActivity(context, 0, toLaunch, 0);
				
				//Set the Notification Info
				notification.setLatestEventInfo(context, title, details, contentIntent);
				
				//Setting Notification Flags
				notification.flags |= Notification.FLAG_AUTO_CANCEL;
				notification.flags |= Notification.DEFAULT_SOUND;
				
				//Send the notification
				notifier.notify(GeneralTools.generateUniqueId().hashCode(), notification);
			}
			catch(Exception e)
			{
				SystemException se = new SystemException(this.getClass().getName(),"run", 
						new Object[]{
							"Exception="+e.toString(),
							"Message="+e.getMessage()
						});
				ErrorHandler.getInstance().handle(se);
			}
		}
		
		private int findDrawableId(Context context, String variable)
		{
			try
			{
				String idClass = context.getPackageName() + ".R$drawable";
				Class clazz = Class.forName(idClass);
				Field field = clazz.getField(variable);

				return field.getInt(clazz);
			} catch (Exception e)
			{
				return -1;
			}
		}
	}
	
	private static class SyncCommandHandler implements Runnable
	{
		private Map<String,String> input;
		
		private SyncCommandHandler(Map<String,String> input)
		{
			this.input = input;
		}
		public void run()
		{
			try
			{
				String service = input.get(Constants.service);
				
				if(service == null)
				{
					SystemException se = new SystemException(this.getClass().getName(),"run", 
					new Object[]{
						"Error=Service to synchronize with is missing!!!"
					});
					ErrorHandler.getInstance().handle(se);
					return;
				}
								
				SyncInvocation syncInvocation = new SyncInvocation("org.openmobster.core.mobileCloud.android.invocation.SyncInvocationHandler", 
				SyncInvocation.oneWayServerOnly, service);
				syncInvocation.activateBackgroundSync();
				Bus.getInstance().invokeService(syncInvocation);
			}
			catch(Exception e)
			{
				SystemException se = new SystemException(this.getClass().getName(),"run", 
						new Object[]{
							"Exception="+e.toString(),
							"Message="+e.getMessage()
						});
				ErrorHandler.getInstance().handle(se);
			}
		}
	}
	
	private static class PushRPCHandler implements Runnable
	{
		private Map<String,String> input;
		
		private PushRPCHandler(Map<String,String> input)
		{
			this.input = input;
		}
		public void run()
		{
			try
			{
				String payload = input.get(Constants.payload);
				
				byte[] decoded = Base64.decode(payload.getBytes());
				payload = new String(decoded);
				
				//leave for debugging
				//System.out.println(payload);
				//System.out.println("PushRPC Started......"+Registry.getActiveInstance().getContext().getPackageName());
				
				PushRPCInvocation invocation = new PushRPCInvocation(
					"org.openmobster.core.mobileCloud.api.push.PushRPCInvocationHandler",payload);
				Bus.getInstance().broadcast(invocation);
			}
			catch(Exception e)
			{
				SystemException se = new SystemException(this.getClass().getName(),"run", 
						new Object[]{
							"Exception="+e.toString(),
							"Message="+e.getMessage()
						});
				ErrorHandler.getInstance().handle(se);
			}
		}
	}
}
