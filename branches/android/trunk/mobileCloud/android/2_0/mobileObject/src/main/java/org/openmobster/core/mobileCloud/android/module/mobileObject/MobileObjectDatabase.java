/**
 * Copyright (c) {2003,2009} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.module.mobileObject;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;

import android.content.Context;

import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android.service.Service;
import org.openmobster.core.mobileCloud.android.storage.DBException;
import org.openmobster.core.mobileCloud.android.storage.Database;
import org.openmobster.core.mobileCloud.android.storage.Record;
import org.openmobster.core.mobileCloud.android.util.GenericAttributeManager;
import org.openmobster.core.mobileCloud.android.module.bus.Bus;
import org.openmobster.core.mobileCloud.android.module.bus.Invocation;
import org.openmobster.core.mobileCloud.android.module.bus.InvocationResponse;


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
	public MobileObject read(String storageId, String recordId)
	{
		try
		{
			MobileObject mobileObject = null;
						
			List<MobileObject> all = this.readAll(storageId);
			for(MobileObject curr: all)
			{	
				if(curr.getRecordId().equals(recordId))
				{
					return curr;
				}
			}
		
			return mobileObject;
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
	
	public List<MobileObject> readAll(String storageId)
	{	
		try
		{
			Context context = Registry.getActiveInstance().getContext();
			List<MobileObject> objects = new ArrayList<MobileObject>();
						
			this.checkStorage(storageId);
			Set<Record> all = Database.getInstance(context).selectAll(storageId);
			for(Record record: all)
			{
				objects.add(new MobileObject(record));
			}
			
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
	
	public String create(MobileObject mobileObject)
	{
		try
		{
			String newId = null;
			
			//Map the objectId locally to the device
			Invocation invocation = new Invocation(mobileObject.getStorageId());
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
			}						
			
			Context context = Registry.getActiveInstance().getContext();
			this.checkStorage(mobileObject.getStorageId());
			newId = Database.getInstance(context).insert(mobileObject.getStorageId(), mobileObject.getRecord());									
			
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
			Context context = Registry.getActiveInstance().getContext();
			
			this.checkStorage(mobileObject.getStorageId());
			
			Record recordToBeUpdated = mobileObject.getRecord();
			
			String dirtyStatus = recordToBeUpdated.getDirtyStatus();
			if(dirtyStatus == null || dirtyStatus.trim().length() == 0)
			{
				Record currentRecord = Database.getInstance(context).select(mobileObject.getStorageId(), 
				mobileObject.getRecordId());
				recordToBeUpdated.setDirtyStatus(currentRecord.getDirtyStatus());
			}
			
			Database.getInstance(context).update(mobileObject.getStorageId(), recordToBeUpdated);
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
			this.checkStorage(mobileObject.getStorageId());
			
			MobileObject objectToBeDeleted = this.read(mobileObject.getStorageId(), mobileObject.getRecordId());
			Record recordToBeDeleted = objectToBeDeleted.getRecord();
			
			Context context = Registry.getActiveInstance().getContext();
			Database.getInstance(context).delete(mobileObject.getStorageId(), recordToBeDeleted);
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
			this.checkStorage(storageId);
			Context context = Registry.getActiveInstance().getContext();
			Database.getInstance(context).deleteAll(storageId);
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
	public List<MobileObject> query(String storageId, GenericAttributeManager queryAttributes)
	{
		List<MobileObject> result = this.readAll(storageId);
		
		int logicLink = LogicChain.AND; //assumed by default
		if(queryAttributes.getAttribute("logicLink")!=null)
		{
			logicLink = ((Integer)queryAttributes.getAttribute("logicLink")).intValue();
		}
		
		List<LogicExpression> expressions = (List<LogicExpression>)queryAttributes.getAttribute("expressions");
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
	//----------------------------------------------------------------------------------------------------------------------------------------------
	private void checkStorage(String storageId) throws DBException
	{
		Context context = Registry.getActiveInstance().getContext();
		if(!Database.getInstance(context).doesTableExist(storageId))
		{
			Database.getInstance(context).createTable(storageId);
		}
	}
	//-----------------------------------------------------------------------------------------------------------------------------------
	
}
