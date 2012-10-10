/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.module.connection;

import java.util.Map;
import java.util.HashMap;
import java.net.URLDecoder;

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

import android.content.Intent;
import android.content.Context;
import android.os.Bundle;

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
				try
				{
					token = URLDecoder.decode(token, "UTF-8");
				}
				catch(Exception e)
				{
					e.printStackTrace(System.out);
					return;
				}
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
				else if(inputCommand.equals(Constants.deviceManagement))
				{
					this.deviceManagement(input);
				}
				else if(inputCommand.equals(Constants.d2d))
				{
					this.d2d(input);
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
	
	private void deviceManagement(Map<String,String> input)
	{
		Thread t = new Thread(new DeviceManagementCommandHandler(input));
		t.start();
	}
	
	private void d2d(Map<String,String> input)
	{
		Thread t = new Thread(new D2DCommandHandler(input));
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
				String appId = extrasData.get("app-id");
				if(appId == null || appId.trim().length() == 0)
				{
					//App Id must be specified. Thats the only way to know which App handles the notification
					return;
				}
				
				if(title == null || title.trim().length()==0)
				{
					title = message;
				}
				
				if(details == null || details.trim().length()==0)
				{
					details = "";
				}
				
				Context context = Registry.getActiveInstance().getContext();
				Intent pushIntent = new Intent(appId);
				
				pushIntent.putExtra("message", message);
				pushIntent.putExtra("title", title);
				pushIntent.putExtra("detail", details);
				pushIntent.putExtra("app-id", appId);
				
				context.sendBroadcast(pushIntent);
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
				String silent = input.get(Constants.silent);
				
				if(service == null)
				{
					SystemException se = new SystemException(this.getClass().getName(),"run", 
					new Object[]{
						"Error=Service to synchronize with is missing!!!"
					});
					ErrorHandler.getInstance().handle(se);
					return;
				}
								
				//Prepare the bundle
				Bundle bundle = new Bundle();
				bundle.putString("channel", service);
				bundle.putString("silent", silent);
				
				//Prepare the intent
				Intent intent = new Intent("org.openmobster.sync.start");
				intent.putExtra("bundle", bundle);
				
				//Send the broadcast
				Context context = Registry.getActiveInstance().getContext();
				context.sendBroadcast(intent);
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
					"org.openmobster.core.mobileCloud.api.ui.framework.push.PushRPCInvocationHandler",payload);
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
	
	private static class DeviceManagementCommandHandler implements Runnable
	{
		private Map<String,String> input;
		
		private DeviceManagementCommandHandler(Map<String,String> input)
		{
			this.input = input;
		}
		public void run()
		{
			try
			{
				String action = input.get(Constants.action);
				
				if(action.equals("lock"))
				{
					PolicyManager.getInstance().lock();
				}
				else if(action.equals("wipe"))
				{
					PolicyManager.getInstance().wipe();
				}
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
	
	private static class D2DCommandHandler implements Runnable
	{
		private Map<String,String> input;
		
		private D2DCommandHandler(Map<String,String> input)
		{
			this.input = input;
		}
		public void run()
		{
			try
			{
				String from = input.get(Constants.from);
				String to = input.get(Constants.to);
				String message = input.get(Constants.message);
				String source_deviceid = input.get(Constants.source_deviceid);
				String destination_deviceid = input.get(Constants.destination_deviceid);
				String timestamp = input.get(Constants.timestamp);
				String app_id = input.get(Constants.app_id);
				
				Bundle bundle = new Bundle();
				bundle.putString(Constants.from, from);
				bundle.putString(Constants.to, to);
				bundle.putString(Constants.message, message);
				bundle.putString(Constants.source_deviceid, source_deviceid);
				bundle.putString(Constants.destination_deviceid, destination_deviceid);
				bundle.putString(Constants.timestamp, timestamp);
				bundle.putString(Constants.app_id, app_id);
				
				//create the broadcast intent
				Intent intent = new Intent("d2d://"+app_id);
				
				intent.putExtra(Constants.d2dMessage, bundle);
				
				Context context = Registry.getActiveInstance().getContext();
				context.sendBroadcast(intent);
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
