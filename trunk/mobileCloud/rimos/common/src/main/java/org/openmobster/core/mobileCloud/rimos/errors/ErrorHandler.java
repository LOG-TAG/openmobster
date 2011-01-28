/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.rimos.errors;

import java.util.Date;
import java.util.Enumeration;

import net.rim.device.api.system.ApplicationDescriptor;

import org.openmobster.core.mobileCloud.rimos.service.Registry;
import org.openmobster.core.mobileCloud.rimos.storage.Database;
import org.openmobster.core.mobileCloud.rimos.storage.Record;

/**
 * @author openmobster@gmail.com
 *
 */
public final class ErrorHandler
{	
	private static ErrorHandler singleton;
	
	private ErrorHandler()
	{		
	}
		
	private void init() 
	{
		try
		{
			//Initialize the provisioning table
			Database database = Database.getInstance();
			if(!database.doesTableExist(Database.system_errors))
			{
				database.createTable(Database.system_errors);								
			}
		}
		catch(Exception e)
		{
			
		}
	}		
		
	public static ErrorHandler getInstance()
	{
		if(ErrorHandler.singleton == null)
		{
			synchronized(ErrorHandler.class)
			{
				if(ErrorHandler.singleton == null)
				{
					ErrorHandler.singleton = new ErrorHandler();
					ErrorHandler.singleton.init();
				}
			}
		}
		return ErrorHandler.singleton;
	}
	//-----------------------------------------------------------------------------------------------------------------------------------------	
	public void handle(Exception e)
	{
		try
		{
			//Collect some meta data
			boolean isCloud = Registry.getInstance().isContainer();
			Date date = new Date();
			
			//TODO: Collect some device related information
			String applicationName = ApplicationDescriptor.currentApplicationDescriptor().getLocalizedName();
			
			StringBuffer buffer = new StringBuffer();
			buffer.append(applicationName+" ");
			buffer.append(date.toString()+" ");
			if(isCloud)
			{
				buffer.append("MobileCloud ");
			}
			else
			{
				buffer.append("Moblet \n");
			}
			
			buffer.append(e.toString()+"\n");
			buffer.append(e.getMessage());

			this.save(buffer.toString());
		}
		catch(Exception ex)
		{
			//Crap Error Handler itself bombed....this can turn into an self-fulfilling prophecy
			//Has to be ignored
		}
	}
	
	public String generateReport() 
	{
		try
		{
			StringBuffer buffer = new StringBuffer();
			
			Enumeration errors = Database.getInstance().selectAll(Database.system_errors);
			if(errors != null)
			{
				while(errors.hasMoreElements())
				{
					Record record = (Record)errors.nextElement();
					buffer.append(record.getValue("message"));
					buffer.append("\n----------------------------------------------\n");
				}
			}
			
			return buffer.toString();
		}
		catch(Exception ex)
		{
			throw new SystemException(this.getClass().getName(), "generateReport", new Object[]{
				"Exception="+ex.toString(),
				"Message="+ex.getMessage()
			});
		}
	}
	
	public void clearAll()
	{
		try
		{
			Database.getInstance().deleteAll(Database.system_errors);
		}
		catch(Exception ex)
		{
			throw new SystemException(this.getClass().getName(), "clearAll", new Object[]{
				"Exception="+ex.toString(),
				"Message="+ex.getMessage()
			});
		}
	}
	//-----------------------------------------------------------------------------------------------------------------------------------------
	private void save(String message) throws Exception
	{
		Record record = new Record();		
		record.setValue("message", message);	
		Database.getInstance().insert(Database.system_errors, record);
	}		
}
