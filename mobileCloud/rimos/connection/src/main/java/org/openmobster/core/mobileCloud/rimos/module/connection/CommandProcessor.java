/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.rimos.module.connection;

import java.util.Hashtable;

import org.openmobster.core.mobileCloud.rimos.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.rimos.errors.SystemException;
import org.openmobster.core.mobileCloud.rimos.module.bus.Bus;
import org.openmobster.core.mobileCloud.rimos.module.bus.SyncInvocation;
import org.openmobster.core.mobileCloud.rimos.service.Registry;
import org.openmobster.core.mobileCloud.rimos.service.Service;
import org.openmobster.core.mobileCloud.rimos.util.StringUtil;





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
		return (CommandProcessor)Registry.getInstance().lookup(CommandProcessor.class);
	}
	
	
	public void process(String command)
	{
		//Process the Command on a separate thread, not on the Notification Listening Thread		
		String[] tokens = StringUtil.tokenize(command, Constants.separator);
		Hashtable input = new Hashtable();
		
		if(tokens != null)
		{
			int length = tokens.length;
			for(int i=0; i<length; i++)
			{
				int index = tokens[i].indexOf('=');
				String name = tokens[i].substring(0, index);
				String value = tokens[i].substring(index+1);
				input.put(name.trim(), value.trim());
			}
			
			if(input.get(Constants.command)!=null)
			{
				String inputCommand = (String)input.get(Constants.command);
				if(inputCommand.equals(Constants.sync))
				{
					this.sync(input);
				}
			}
		}
	}
		
	private void sync(Hashtable input)
	{
		Thread t = new Thread(new SyncCommandHandler(input));
		t.start();		
	}
	
	private static class SyncCommandHandler implements Runnable
	{
		private Hashtable input;
		
		private SyncCommandHandler(Hashtable input)
		{
			this.input = input;
		}
		public void run()
		{
			try
			{
				String service = (String)input.get(Constants.service);
				
				if(service == null)
				{
					SystemException se = new SystemException(this.getClass().getName(),"run", 
					new Object[]{
						"Error=Service to synchronize with is missing!!!"
					});
					ErrorHandler.getInstance().handle(se);
					return;
				}
								
				SyncInvocation syncInvocation = new SyncInvocation("org.openmobster.core.mobileCloud.invocation.SyncInvocationHandler", 
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
