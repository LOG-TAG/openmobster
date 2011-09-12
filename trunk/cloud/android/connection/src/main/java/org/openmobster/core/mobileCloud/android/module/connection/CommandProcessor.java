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

import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android.service.Service;
import org.openmobster.core.mobileCloud.android.util.Base64;
import org.openmobster.core.mobileCloud.android.util.StringUtil;
import org.openmobster.core.mobileCloud.android.util.XMLUtil;
import org.openmobster.core.mobileCloud.android.module.bus.Bus;
import org.openmobster.core.mobileCloud.android.module.bus.Invocation;
import org.openmobster.core.mobileCloud.android.module.bus.PushRPCInvocation;
import org.openmobster.core.mobileCloud.android.module.bus.SyncInvocation;

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
				
				Invocation pushInvocation = new Invocation("org.openmobster.core.mobileCloud.api.ui.framework.push.PushInvocationHandler");
				pushInvocation.setDestinationBus(appId);
				pushInvocation.setValue("message", message);
				pushInvocation.setValue("detail", details);
				pushInvocation.setValue("title", title);
				Bus.getInstance().invokeService(pushInvocation);
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
								
				SyncInvocation syncInvocation = new SyncInvocation("org.openmobster.core.mobileCloud.android.invocation.SyncInvocationHandler", 
				SyncInvocation.twoWay, service);
				
				if(silent == null || silent.equals("false"))
				{
					syncInvocation.activateBackgroundSync();
				}
				else
				{
					syncInvocation.deactivateBackgroundSync();
				}
				
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
}
