/**
 * Copyright (c) {2003,2009} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.errors;

import java.util.Date;
import java.util.Set;

import android.content.Context;

import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android.storage.Database;
import org.openmobster.core.mobileCloud.android.storage.Record;

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
			Database database = Database.getInstance(Registry.getActiveInstance().getContext());
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
			Context context = Registry.getActiveInstance().getContext();
			boolean isCloud = Registry.getInstance(context).isContainer();
			Date date = new Date();
			
			//TODO: Collect some device related information
			String applicationName = context.getApplicationInfo().toString();
			
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
			
			Context context = Registry.getActiveInstance().getContext();
			Set<Record> errors = Database.getInstance(context).selectAll(Database.system_errors);
			if(errors != null)
			{
				for(Record record: errors)
				{
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
			Context context = Registry.getActiveInstance().getContext();
			Database.getInstance(context).deleteAll(Database.system_errors);
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
		Context context = Registry.getActiveInstance().getContext();
		Database.getInstance(context).insert(Database.system_errors, record);
	}		
}
