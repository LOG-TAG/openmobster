/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.module.mobileObject;

import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.content.ContentValues;

import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android.service.Service;
import org.openmobster.core.mobileCloud.android.storage.Record;
import org.openmobster.core.mobileCloud.android.util.GenericAttributeManager;

/**
 * @author openmobster@gmail.com
 */
public final class MobileObjectDatabase extends Service
{		
	public static String storageId = "storageId";
	public static String recordId = "recordId";
	public static String syncOperation = "syncOperation";
	
	public MobileObjectDatabase()
	{	
	}
	
	public void start()
	{
		
	}
	
	public void stop()
	{
		
	}
	
	public static MobileObjectDatabase getInstance()
	{
		return (MobileObjectDatabase)Registry.getActiveInstance().lookup(MobileObjectDatabase.class);
	}
	//---------------------------------------------------------------------------------------------------------------------------------------------
	public List<MobileObject> readAll(String channel)
	{	
		try
		{
			List<MobileObject> objects = new ArrayList<MobileObject>();
			Uri uri = Uri.
			parse("content://org.openmobster.core.mobileCloud.android.provider.mobile.channels/"+channel);
			Context context = Registry.getActiveInstance().getContext();
			
			ContentResolver resolver = context.getContentResolver();
			Cursor cursor = resolver.query(uri, 
			null, 
			null, 
			null, 
			null);
			
			objects = this.parse(cursor);
			
			return objects;
		}
		catch(Exception e)
		{
			throw new SystemException(this.getClass().getName(), "readAll", new Object[]{
				storageId,
				"Exception="+e.toString(),
				"Error="+e.getMessage()
			});
		}
	}
	
	public MobileObject read(String channel, String recordId)
	{
		try
		{			
			Uri uri = Uri.
			parse("content://org.openmobster.core.mobileCloud.android.provider.mobile.channels/"+
			channel);
			Context context = Registry.getActiveInstance().getContext();
			
			ContentResolver resolver = context.getContentResolver();
			Cursor cursor = resolver.query(uri, 
			null, 
			recordId, 
			null, 
			null);
						
			List<MobileObject> all = this.parse(cursor);
			if(all != null)
			{
				for(MobileObject curr: all)
				{	
					if(curr.getRecordId().equals(recordId))
					{
						return curr;
					}
				}
			}
		
			return null;
		}
		catch(Exception e)
		{
			throw new SystemException(this.getClass().getName(), "read", new Object[]{
				storageId, recordId,
				"Exception="+e.toString(),
				"Error="+e.getMessage()
			});
		}
	}
	
	public String create(MobileObject mobileObject)
	{
		try
		{
			String newId = null;
			
			//Not needed right now
			//Map the objectId locally to the device
			/*Invocation invocation = new Invocation(mobileObject.getStorageId());
			invocation.setValue("storageId", mobileObject.getStorageId());
			invocation.setValue("recordId", mobileObject.getRecordId());
			InvocationResponse response = Bus.getInstance().invokeService(invocation);			
			if(response != null)
			{
				String mappedRecordId = response.getValue(InvocationResponse.returnValue);
				if(mappedRecordId != null && mappedRecordId.trim().length()>0)
				{
					mobileObject.setRecordId(mappedRecordId);
				}
			}*/					
			
			String channel = mobileObject.getStorageId();
			Uri uri = Uri.
			parse("content://org.openmobster.core.mobileCloud.android.provider.mobile.channels/"+
			channel);
			Context context = Registry.getActiveInstance().getContext();				
			ContentResolver resolver = context.getContentResolver();
			
			ContentValues values = new ContentValues();
			this.prepareForStorage(values, mobileObject.getRecord());
			
			Uri insertUri = resolver.insert(uri, values);
			newId = insertUri.getQueryParameter("id");
			
			return newId;
		}
		catch(Exception be)
		{			
			throw new SystemException(this.getClass().getName(), "create", new Object[]
   			{
				"storageId="+mobileObject.getStorageId(),
				"recordId="+mobileObject.getRecordId(),
   				"error="+be.getMessage()
   			}
   			);
		}		
	}
		
	public void update(MobileObject mobileObject)
	{
		try
		{
			String channel = mobileObject.getStorageId();
			Uri uri = Uri.
			parse("content://org.openmobster.core.mobileCloud.android.provider.mobile.channels/"+
			channel);
			Context context = Registry.getActiveInstance().getContext();				
			ContentResolver resolver = context.getContentResolver();
			
			ContentValues values = new ContentValues();
			this.prepareForStorage(values, mobileObject.getRecord());
			
			int resultCode = resolver.update(uri, values, null, null);
			
			if(resultCode == -1)
			{
				throw new RuntimeException("Locking Error!!");
			}
		}
		catch(Exception e)
		{
			throw new SystemException(this.getClass().getName(), "update", new Object[]
   			{
				"storageId="+mobileObject.getStorageId(),
				"recordId="+mobileObject.getRecordId(),
   				"error="+e.getMessage()
   			}
   			);
		}		
	}
	
	public void delete(MobileObject mobileObject)
	{	
		try
		{
			String channel = mobileObject.getStorageId();
			Uri uri = Uri.
			parse("content://org.openmobster.core.mobileCloud.android.provider.mobile.channels/"+
			channel);
			Context context = Registry.getActiveInstance().getContext();				
			ContentResolver resolver = context.getContentResolver();
			
			int resultCode = resolver.delete(uri, mobileObject.getRecordId(), 
			null);
			
			if(resultCode == -1)
			{
				throw new RuntimeException("Locking Error!!");
			}
		}
		catch(Exception e)
		{
			throw new SystemException(this.getClass().getName(), "delete", new Object[]
 			{
				"storageId="+mobileObject.getStorageId(),
				"recordId="+mobileObject.getRecordId(),
				"error="+e.getMessage()
 			}
 			);
		}		
	}
	
	public void deleteAll(String storageId)
	{
		try
		{
			Uri uri = Uri.
			parse("content://org.openmobster.core.mobileCloud.android.provider.mobile.channels/"+
			storageId);
			Context context = Registry.getActiveInstance().getContext();				
			ContentResolver resolver = context.getContentResolver();
			
			resolver.delete(uri, null, null);
		}
		catch(Exception e)
		{
			throw new SystemException(this.getClass().getName(), "deleteAll", new Object[]
   			{
				"storageId="+storageId,
				"error="+e.getMessage()
   			}
   			);
		}		
	}
	//---Query Integration------------------------------------------------------------------------------------------------------------------------------------------
	public List<MobileObject> query(String channel, GenericAttributeManager queryAttributes)
	{
		List<MobileObject> result = this.readAll(channel);
		
		int logicLink = LogicChain.AND; //assumed by default
		if(queryAttributes.getAttribute("logicLink")!=null)
		{
			logicLink = ((Integer)queryAttributes.getAttribute("logicLink")).intValue();
		}
		
		List<LogicExpression> expressions = (List<LogicExpression>)queryAttributes.
		getAttribute("expressions");
		if(expressions != null && !expressions.isEmpty())
		{
			LogicChain chain = null;
			switch(logicLink)
			{
				case LogicChain.AND:
					chain = LogicChain.createANDChain();					
				break;
				
				case LogicChain.OR:
					chain = LogicChain.createORChain();
				break;
				
				default:
				break;
			}
			if(chain != null)
			{
				for(LogicExpression courExpr:expressions)
				{
					chain.add(courExpr);
				}
				Query query = Query.createInstance(chain);
				result = query.executeQuery(result);
			}
		}
		
		return result;
	}
	//----------------------------------------------------------------------------------------------------------------------------------------------
	public void syncWithServer(String storageId)
	{
		
	}	
	//-------------------------------------------------------------------------------------------
	private List<MobileObject> parse(Cursor cursor)
	{
		List<MobileObject> mobileObjects = new ArrayList<MobileObject>();
		
		if(cursor != null && cursor.getCount()>0)
		{			
			int idIndex = cursor.getColumnIndex("recordId");
			int nameIndex = cursor.getColumnIndex("name");
			int valueIndex = cursor.getColumnIndex("value");
			Map<String,Record> records = new HashMap<String,Record>();
			
			cursor.moveToFirst();
			do
			{
				String recordId = cursor.getString(idIndex);
				String name = cursor.getString(nameIndex);
				String value = cursor.getString(valueIndex);
				
				if(name.equals("recordId"))
				{
					Record record = new Record();
					record.setRecordId(recordId);
					records.put(recordId, record);
				}
				else
				{
					records.get(recordId).setValue(name, value);
				}
				
				cursor.moveToNext();
			}while(!cursor.isAfterLast());		
			
			Set<String> recordIds = records.keySet();
			for(String recordId:recordIds)
			{
				mobileObjects.add(
				new MobileObject(records.get(recordId)));
			}
		}
		
		return mobileObjects;
	}
	
	private void prepareForStorage(ContentValues values, Record record)
	{
		Set<String> names = record.getNames();
		if(names != null)
		{
			for(String name: names)
			{
				values.put(name, record.getValue(name));
			}
		}		
	}
}
