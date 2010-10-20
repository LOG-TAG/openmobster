/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
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
import org.openmobster.core.mobileCloud.android.util.StringUtil;
import org.openmobster.core.mobileCloud.android.module.bus.Bus;
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
			}
		}
	}
		
	private void sync(Map<String,String> input)
	{
		Thread t = new Thread(new SyncCommandHandler(input));
		t.start();		
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
}
