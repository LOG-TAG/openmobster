/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.errors;

import java.util.Date;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.app.Activity;

import org.openmobster.core.mobileCloud.android.service.Registry;

/**
 * @author openmobster@gmail.com
 *
 */
public final class ErrorHandler
{
	private static final Uri uri;
	
	static
	{
		uri = Uri.
		parse("content://org.openmobster.core.mobileCloud.android.provider.mobile.system.errors");
	}
	
	private static ErrorHandler singleton;
	
	private ErrorHandler()
	{		
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
			Registry registry = Registry.getActiveInstance();
			Activity context = registry.getContext();
			boolean isCloud = registry.isContainer();
			Date date = new Date();
			
			//TODO: Collect some device related information
			PackageManager pm = context.getPackageManager();
			CharSequence applicationName = pm.getApplicationLabel(context.getApplicationInfo());
			
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

			this.save(context,buffer.toString());
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
			ContentResolver resolver = context.getContentResolver();
			
			Cursor cursor = resolver.query(uri, 
			null, 
			null, 
			null, 
			null);
			
			if(cursor != null && cursor.getCount()>0)
			{
				int valueIndex = cursor.getColumnIndex("value");
				cursor.moveToFirst();
				do
				{
					buffer.append(cursor.getString(valueIndex));
					buffer.append("\n----------------------------------------------\n");
					cursor.moveToNext();
				}while(!cursor.isAfterLast());
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
			ContentResolver resolver = context.getContentResolver();
			resolver.delete(uri, null, null);
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
	private void save(Context context,String message) throws Exception
	{
		ContentResolver resolver = context.getContentResolver();
		ContentValues errorRecord = new ContentValues();
		errorRecord.put("message", message);
		
		resolver.insert(uri, errorRecord);
	}		
}
