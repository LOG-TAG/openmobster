/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.rimos.module.mobileObject;

import java.util.Vector;
import java.util.Enumeration;

import org.openmobster.core.mobileCloud.rimos.errors.SystemException;
import org.openmobster.core.mobileCloud.rimos.module.bus.Bus;
import org.openmobster.core.mobileCloud.rimos.module.bus.Invocation;
import org.openmobster.core.mobileCloud.rimos.module.bus.InvocationResponse;
import org.openmobster.core.mobileCloud.rimos.service.Registry;
import org.openmobster.core.mobileCloud.rimos.service.Service;
import org.openmobster.core.mobileCloud.rimos.storage.DBException;
import org.openmobster.core.mobileCloud.rimos.storage.Database;
import org.openmobster.core.mobileCloud.rimos.storage.Record;
import org.openmobster.core.mobileCloud.rimos.util.GenericAttributeManager;


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
		return (MobileObjectDatabase)Registry.getInstance().lookup(MobileObjectDatabase.class);
	}
	//---------------------------------------------------------------------------------------------------------------------------------------------	
	public MobileObject read(String storageId, String recordId)
	{
		try
		{
			MobileObject mobileObject = null;
						
			Vector all = this.readAll(storageId);
			Enumeration cour = all.elements();
			while(cour.hasMoreElements())
			{
				MobileObject curr = (MobileObject)cour.nextElement();
				
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
	
	public Vector readAll(String storageId)
	{	
		try
		{
			Vector objects = new Vector();
						
			this.checkStorage(storageId);
			Enumeration all = Database.getInstance().selectAll(storageId);
			while(all.hasMoreElements())
			{
				Record record = (Record)all.nextElement();
				objects.addElement(new MobileObject(record));
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
			
			this.checkStorage(mobileObject.getStorageId());
			newId = Database.getInstance().insert(mobileObject.getStorageId(), mobileObject.getRecord());									
			
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
			this.checkStorage(mobileObject.getStorageId());
			
			Record recordToBeUpdated = mobileObject.getRecord();
			
			String dirtyStatus = recordToBeUpdated.getDirtyStatus();
			if(dirtyStatus == null || dirtyStatus.trim().length() == 0)
			{
				Record currentRecord = Database.getInstance().select(mobileObject.getStorageId(), mobileObject.getRecordId());
				recordToBeUpdated.setDirtyStatus(currentRecord.getDirtyStatus());
			}
			
			Database.getInstance().update(mobileObject.getStorageId(), recordToBeUpdated);
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
			
			Database.getInstance().delete(mobileObject.getStorageId(), recordToBeDeleted);
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
			Database.getInstance().deleteAll(storageId);
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
	public Vector query(String storageId, GenericAttributeManager queryAttributes)
	{
		Vector result = this.readAll(storageId);
		
		int logicLink = LogicChain.AND; //assumed by default
		if(queryAttributes.getAttribute("logicLink")!=null)
		{
			logicLink = ((Integer)queryAttributes.getAttribute("logicLink")).intValue();
		}
		
		Vector expressions = (Vector)queryAttributes.getAttribute("expressions");
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
				int size = expressions.size();
				for(int i=0; i<size; i++)
				{
					chain.add((LogicExpression)expressions.elementAt(i));
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
		if(!Database.getInstance().doesTableExist(storageId))
		{
			Database.getInstance().createTable(storageId);
		}
	}
	//-----------------------------------------------------------------------------------------------------------------------------------
	
}
