/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.api.model;

import java.util.Vector;
import java.util.Hashtable;

import org.openmobster.core.mobileCloud.rimos.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.rimos.errors.SystemException;
import org.openmobster.core.mobileCloud.rimos.module.bus.Bus;
import org.openmobster.core.mobileCloud.rimos.module.bus.SyncInvocation;
import org.openmobster.core.mobileCloud.rimos.module.mobileObject.MobileObject;
import org.openmobster.core.mobileCloud.rimos.module.mobileObject.MobileObjectDatabase;
import org.openmobster.core.mobileCloud.rimos.module.mobileObject.LogicChain;
import org.openmobster.core.mobileCloud.rimos.module.mobileObject.LogicExpression;
import org.openmobster.core.mobileCloud.rimos.util.GenericAttributeManager;

/**
 * MobileBean is a managed Mobile Component which is an extension of its corresponding Mobile Component on the Server
 * 
 * MobileBean provides seamless access to service data to be used by the Mobile application in various contexts like showing reports, GUI for the 
 * service etc
 * 
 * It shields the Mobile Developer from low-level services like Offline Access, Receiving Notifications related to data changes on the server,
 * Synchronizing modified beans back with the server etc. This helps the developer to focus on business logic for their applications better on the
 * Mobile Application
 * 
 * @author openmobster@gmail.com
 */
public final class MobileBean 
{
	private MobileObject data;
	
	private boolean isDirty = false;
	private boolean isNew = false;
		
	private MobileBean(MobileObject data)
	{
		this.data = data;
	}	
	
	private MobileBean()
	{
		
	}
	//-------------Data operations-----------------------------------------------------------------------------------------------------------------
	/**
	 * Gets Mobile Service related to this bean
	 * 
	 * @return the Mobile Service related to this bean
	 */
	public String getService()
	{
		if(!this.isInitialized())
		{
			throw new IllegalStateException("MobileBean is uninitialized!!");
		}
		return this.data.getStorageId().trim();
	}
	
	/**
	 * Gets the unique identifier of the bean
	 * 
	 * @return the unique identifier for this Mobile Bean
	 */
	public String getId()
	{
		if(!this.isInitialized())
		{
			throw new IllegalStateException("MobileBean is uninitialized!!");
		}
		
		String recordId = this.data.getRecordId();
		if(recordId != null)
		{
			recordId = recordId.trim();
		}		
		return recordId;
	}
	
	/**
	 * Gets the unique identifier of this bean on the Server Side
	 * 
	 * @return the unique identifier for this bean on the server side
	 */
	public String getServerId()
	{
		if(!this.isInitialized())
		{
			throw new IllegalStateException("MobileBean is uninitialized!!");
		}
		
		String recordId = this.data.getServerRecordId();
		if(recordId != null)
		{
			recordId = recordId.trim();
		}		
		return recordId;
	}
	
	/**
	 * Checks if the bean instance is properly initialized
	 * 
	 * @return
	 */
	public boolean isInitialized()
	{
		return (this.data != null);
	}
	
	/**
	 * Checks if this bean was originally created on the device
	 * 
	 * @return
	 */
	public boolean isCreateOnDevice()
	{
		return this.data.isCreatedOnDevice();
	}
	
	/**
	 * Checks if the particular instance if in proxy state or state is fully downloaded from the server
	 * 
	 * @return
	 */
	public boolean isProxy()
	{
		if(!this.isInitialized())
		{
			throw new IllegalStateException("MobileBean is uninitialized!!");
		}
		return this.data.isProxy();
	}
	
	/**
	 * Gets the Value of a Field of the bean
	 * 
	 * @param fieldUri the Field
	 * @return the Value 
	 */
	public String getValue(String fieldUri)
	{
		if(!this.isInitialized())
		{
			throw new IllegalStateException("MobileBean is uninitialized!!");
		}
		if(this.data.isProxy())
		{
			throw new IllegalStateException("MobileBean is still in proxy state");
		}
		return this.data.getValue(fieldUri).trim();
	}
	
	/**
	 * Sets the Value of a Field of the bean
	 * @param fieldUri
	 * @param value
	 */
	public void setValue(String fieldUri, String value)
	{
		if(!this.isInitialized())
		{
			throw new IllegalStateException("MobileBean is uninitialized!!");
		}
		if(this.data.isProxy())
		{
			throw new IllegalStateException("MobileBean is still in proxy state");
		}
		this.data.setValue(fieldUri, value);
		this.isDirty = true;
	}
				
	/**
	 * Reads a List of Beans under the Mobile Bean "parent" Object
	 * 
	 * @param listProperty - expression to specify the List
	 * @return
	 */
	public BeanList readList(String listProperty)
	{		
		BeanList beanList = new BeanList(listProperty);
		
		if(!this.isInitialized())
		{
			throw new IllegalStateException("MobileBean is uninitialized!!");
		}
		if(this.data.isProxy())
		{
			throw new IllegalStateException("MobileBean is still in proxy state");
		}
				
		int arrayLength = this.data.getArrayLength(listProperty);		
		for(int i=0; i<arrayLength; i++)
		{			
			Hashtable arrayElement = this.data.getArrayElement(listProperty, i);			
			BeanListEntry entry = new BeanListEntry(i, arrayElement);			
			beanList.addEntry(entry);
		}
		return beanList;		
	}
	
	/**
	 * Saves the List of Beans under the Mobile Bean "parent" Object
	 * If the list is null, a new list is created. If the list exists, then this list replaces the old list
	 * 
	 * @param list
	 */
	public void saveList(BeanList list)
	{
		if(!this.isInitialized())
		{
			throw new IllegalStateException("MobileBean is uninitialized!!");
		}
		if(this.data.isProxy())
		{
			throw new IllegalStateException("MobileBean is still in proxy state");
		}
		
		String listProperty = list.getListProperty();		
		this.data.clearArray(listProperty);
		
		int arrayLength = list.size();
		for(int i=0; i<arrayLength; i++)
		{
			BeanListEntry local = list.getEntryAt(i);
			this.data.addToArray(listProperty, local.getProperties());
		}
		
		this.isDirty = true;
	}
	
	/**
	 * Clears the List of Beans under the Mobile Bean "parent" Object
	 * 
	 * @param listProperty
	 */
	public void clearList(String listProperty)
	{
		if(!this.isInitialized())
		{
			throw new IllegalStateException("MobileBean is uninitialized!!");
		}
		if(this.data.isProxy())
		{
			throw new IllegalStateException("MobileBean is still in proxy state");
		}
		
		this.data.clearArray(listProperty);
		
		this.isDirty = true;
	}
	
	/**
	 * Add a Bean to the List of Beans under the Mobile Bean "parent" Object
	 * 
	 * @param listProperty
	 * @param bean
	 */
	public void addBean(String listProperty, BeanListEntry bean)
	{
		if(!this.isInitialized())
		{
			throw new IllegalStateException("MobileBean is uninitialized!!");
		}
		if(this.data.isProxy())
		{
			throw new IllegalStateException("MobileBean is still in proxy state");
		}
		
		this.data.addToArray(listProperty, bean.getProperties());
		
		this.isDirty = true;
	}
	
	/**
	 * Remove the Bean present at the specified index from the List of Beans under the Mobile Bean "parent" Object
	 * 
	 * @param listProperty
	 * @param elementAt
	 */
	public void removeBean(String listProperty, int elementAt)
	{
		if(!this.isInitialized())
		{
			throw new IllegalStateException("MobileBean is uninitialized!!");
		}
		if(this.data.isProxy())
		{
			throw new IllegalStateException("MobileBean is still in proxy state");
		}
		
		this.data.removeArrayElement(listProperty, elementAt);
		
		this.isDirty = true;
	}
	//----------Persistence operations-----------------------------------------------------------------------------------------------------------
	/**
	 * Persists the state of the Mobile Bean. This also makes sure the consistent bean state is reflected on the Server Side as well
	 * 
	 */
	public synchronized void save()
	{
		MobileObjectDatabase deviceDB = MobileObjectDatabase.getInstance();
		
		//If Bean Created on Device
		if(this.isNew)
		{
			String newId = deviceDB.create(this.data);			
			this.data = deviceDB.read(this.data.getStorageId(), newId);
			
			this.isNew = false;
			this.refresh();
			
			//Integration with the SyncService
			try
			{
				SyncInvocation syncInvocation = new SyncInvocation("org.openmobster.core.mobileCloud.invocation.SyncInvocationHandler", 
				SyncInvocation.updateChangeLog, this.getService(), this.getId(), SyncInvocation.OPERATION_ADD);		
				Bus.getInstance().invokeService(syncInvocation);
			}
			catch(Exception e)
			{
				SystemException sys = new SystemException(this.getClass().getName(), "save://Create", new Object[]{
					"Exception="+e.toString(),
					"Message="+e.getMessage()
				});
				ErrorHandler.getInstance().handle(sys);
			}			
			
			return;
		}
		
		//If Bean Updated on Device
		if(this.isDirty)
		{
			deviceDB.update(this.data);
			this.clearMetaData();
			
			//Integration with the SyncService
			try
			{
				SyncInvocation syncInvocation = new SyncInvocation("org.openmobster.core.mobileCloud.invocation.SyncInvocationHandler", 
				SyncInvocation.updateChangeLog, this.getService(), this.getId(), SyncInvocation.OPERATION_UPDATE);		
				Bus.getInstance().invokeService(syncInvocation);
			}
			catch(Exception e)
			{
				SystemException sys = new SystemException(this.getClass().getName(), "save://Update", new Object[]{
					"Exception="+e.toString(),
					"Message="+e.getMessage()
				});
				ErrorHandler.getInstance().handle(sys);
			}
		}
	}
	
	/**
	 * Deletes the bean from the service. This also makes sure this action is reflected on the Server Side as well
	 */
	public synchronized void delete()
	{
		if(this.isNew)
		{
			throw new IllegalStateException("Instance is created on the device and not saved. Hence it cannot be deleted");
		}
		
		MobileObjectDatabase deviceDB = MobileObjectDatabase.getInstance();
		String service = this.getService();
		String id = this.getId();
		
		deviceDB.delete(this.data);
		
		this.clearAll();
		
		//Integration with the SyncService
		try
		{
			SyncInvocation syncInvocation = new SyncInvocation("org.openmobster.core.mobileCloud.invocation.SyncInvocationHandler", 
			SyncInvocation.updateChangeLog, service, id, SyncInvocation.OPERATION_DELETE);		
			Bus.getInstance().invokeService(syncInvocation);
		}
		catch(Exception e)
		{
			SystemException sys = new SystemException(this.getClass().getName(), "delete", new Object[]{
				"Exception="+e.toString(),
				"Message="+e.getMessage()
			});
			ErrorHandler.getInstance().handle(sys);
		}
	}
	
	/**
	 * Re-Read the state of the MobileBean from the database
	 *
	 */
	public synchronized void refresh()
	{
		if(this.isNew)
		{
			throw new IllegalStateException("Instance is created on the device and not saved. Hence it cannot be refreshed");
		}
		
		MobileObjectDatabase deviceDB = MobileObjectDatabase.getInstance();
		
		this.data = deviceDB.read(this.getService(), this.getId());
		
		this.clearMetaData();
	}
	//--------static operations-------------------------------------------------------------------------------------------------------------------
	/**
	 * Checks if the Channel has been booted up on the device with initial data or not
	 */
	public static boolean isBooted(String channel)
	{
		MobileObjectDatabase deviceDB = MobileObjectDatabase.getInstance();
		Vector allObjects = deviceDB.readAll(channel);
		
		return (allObjects !=null && !allObjects.isEmpty());
	}
	/**
	 * Provides all the instances of Mobile Beans for the specified service
	 * 
	 * @param service Mobile Service associated with the beans
	 */
	public static MobileBean[] readAll(String service)
	{
		MobileBean[] all = null;
		
		MobileObjectDatabase deviceDB = MobileObjectDatabase.getInstance();
		
		Vector allObjects = deviceDB.readAll(service);
		if(allObjects != null && !allObjects.isEmpty())
		{
			//filter out the proxies
			allObjects = MobileBean.filterProxies(allObjects);
			int size = allObjects.size();
			all = new MobileBean[size];
			for(int i=0; i<size; i++)
			{
				MobileObject curr = (MobileObject)allObjects.elementAt(i);
				all[i] = new MobileBean(curr);
			}
		}
		
		return all;
	}
	
	/**
	 * Provides an instance of a Mobile Bean
	 * 
	 * @param service service of the bean
	 * @param id id of the bean
	 * @return
	 */
	public static MobileBean readById(String service, String id)
	{
		MobileBean bean = null;
		
		MobileObject data = MobileObjectDatabase.getInstance().read(service, id);
		if(data != null && !data.isProxy())
		{
			bean = new MobileBean(data);
		}
		
		return bean;
	}
	
	/**
	 * Create a new transient instance of a Mobile Bean. The Mobile Bean has to be explicitly saved in order
	 * to persist it on the device and have it reflect on the server. In this case, when the bean is persisted
	 * the Id will be generated by the device since its not explicitly specified
	 * 
	 * @param service service of the bean
	 * @return
	 */
	public static MobileBean newInstance(String service)
	{
		MobileBean newInstance = null;
		
		MobileObject data = new MobileObject();
		data.setCreatedOnDevice(true);
		data.setStorageId(service);
		
		newInstance = new MobileBean(data);
		newInstance.isNew = true;
		
		return newInstance;
	}
	//---Query functionality--------------------------------------------------------------------------------------------------------------
	//TODO: This should be made more flexible once the Query Language is designed and implemented
	public static MobileBean[] queryByEqualsAll(String service,GenericAttributeManager criteria)
	{
		if(service == null)
		{
			throw new IllegalArgumentException("Service must be specified!!");
		}
		if(criteria == null || criteria.isEmpty())
		{
			throw new IllegalArgumentException("Query Criteria must be specified!!");
		}
		
		MobileBean[] beans = null;
		
		GenericAttributeManager input = new GenericAttributeManager();
		
		input.setAttribute("logicLink", new Integer(LogicChain.AND));
		
		Vector expressions = new Vector();
		input.setAttribute("expressions", expressions);
		
		String[] names = criteria.getNames();
		int size = names.length;
		for(int i=0; i<size; i++)
		{
			String rhs = criteria.getAttribute(names[i]).toString();
			expressions.addElement(LogicExpression.createInstance(names[i], rhs, LogicExpression.OP_EQUALS));
		}
		
		Vector result = MobileObjectDatabase.getInstance().query(service, input);		
		if(result != null && !result.isEmpty())
		{
			result = filterProxies(result);
			int resultSize = result.size();	
			beans = new MobileBean[resultSize];
			for(int i=0; i<resultSize; i++)
			{
				MobileObject cour = (MobileObject)result.elementAt(i);
				beans[i] = new MobileBean(cour);
			}
		}
		
		return beans;
	}
	
	public static MobileBean[] queryByEqualsAtleastOne(String service, GenericAttributeManager criteria)	
	{
		if(service == null)
		{
			throw new IllegalArgumentException("Service must be specified!!");
		}
		if(criteria == null || criteria.isEmpty())
		{
			throw new IllegalArgumentException("Query Criteria must be specified!!");
		}
		
		MobileBean[] beans = null;
		
		GenericAttributeManager input = new GenericAttributeManager();
		
		input.setAttribute("logicLink", new Integer(LogicChain.OR));
		
		Vector expressions = new Vector();
		input.setAttribute("expressions", expressions);
		
		String[] names = criteria.getNames();
		int size = names.length;
		for(int i=0; i<size; i++)
		{
			String rhs = criteria.getAttribute(names[i]).toString();
			expressions.addElement(LogicExpression.createInstance(names[i], rhs, LogicExpression.OP_EQUALS));
		}
		
		Vector result = MobileObjectDatabase.getInstance().query(service, input);		
		if(result != null && !result.isEmpty())
		{
			result = filterProxies(result);
			int resultSize = result.size();	
			beans = new MobileBean[resultSize];
			for(int i=0; i<resultSize; i++)
			{
				MobileObject cour = (MobileObject)result.elementAt(i);
				beans[i] = new MobileBean(cour);
			}
		}
		
		return beans;
	}
	
	public static MobileBean[] queryByNotEqualsAll(String service, GenericAttributeManager criteria)
	{
		if(service == null)
		{
			throw new IllegalArgumentException("Service must be specified!!");
		}
		if(criteria == null || criteria.isEmpty())
		{
			throw new IllegalArgumentException("Query Criteria must be specified!!");
		}
		
		MobileBean[] beans = null;
		
		GenericAttributeManager input = new GenericAttributeManager();
		
		input.setAttribute("logicLink", new Integer(LogicChain.AND));
		
		Vector expressions = new Vector();
		input.setAttribute("expressions", expressions);
		
		String[] names = criteria.getNames();
		int size = names.length;
		for(int i=0; i<size; i++)
		{
			String rhs = criteria.getAttribute(names[i]).toString();
			expressions.addElement(LogicExpression.createInstance(names[i], rhs, LogicExpression.OP_NOT_EQUALS));
		}
		
		Vector result = MobileObjectDatabase.getInstance().query(service, input);		
		if(result != null && !result.isEmpty())
		{
			result = filterProxies(result);
			int resultSize = result.size();	
			beans = new MobileBean[resultSize];
			for(int i=0; i<resultSize; i++)
			{
				MobileObject cour = (MobileObject)result.elementAt(i);
				beans[i] = new MobileBean(cour);
			}
		}
		
		return beans;
	}
	
	public static MobileBean[] queryByNotEqualsAtleastOne(String service, GenericAttributeManager criteria)	
	{
		if(service == null)
		{
			throw new IllegalArgumentException("Service must be specified!!");
		}
		if(criteria == null || criteria.isEmpty())
		{
			throw new IllegalArgumentException("Query Criteria must be specified!!");
		}
		
		MobileBean[] beans = null;
		
		GenericAttributeManager input = new GenericAttributeManager();
		
		input.setAttribute("logicLink", new Integer(LogicChain.OR));
		
		Vector expressions = new Vector();
		input.setAttribute("expressions", expressions);
		
		String[] names = criteria.getNames();
		int size = names.length;
		for(int i=0; i<size; i++)
		{
			String rhs = criteria.getAttribute(names[i]).toString();
			expressions.addElement(LogicExpression.createInstance(names[i], rhs, LogicExpression.OP_NOT_EQUALS));
		}
		
		Vector result = MobileObjectDatabase.getInstance().query(service, input);		
		if(result != null && !result.isEmpty())
		{
			result = filterProxies(result);
			int resultSize = result.size();	
			beans = new MobileBean[resultSize];
			for(int i=0; i<resultSize; i++)
			{
				MobileObject cour = (MobileObject)result.elementAt(i);
				beans[i] = new MobileBean(cour);
			}
		}
		
		return beans;
	}
	
	public static MobileBean[] queryByLikeAll(String service,GenericAttributeManager criteria)
	{
		if(service == null)
		{
			throw new IllegalArgumentException("Service must be specified!!");
		}
		if(criteria == null || criteria.isEmpty())
		{
			throw new IllegalArgumentException("Query Criteria must be specified!!");
		}
		
		MobileBean[] beans = null;
		
		GenericAttributeManager input = new GenericAttributeManager();
		
		input.setAttribute("logicLink", new Integer(LogicChain.AND));
		
		Vector expressions = new Vector();
		input.setAttribute("expressions", expressions);
		
		String[] names = criteria.getNames();
		int size = names.length;
		for(int i=0; i<size; i++)
		{
			String rhs = criteria.getAttribute(names[i]).toString();
			expressions.addElement(LogicExpression.createInstance(names[i], rhs, LogicExpression.OP_LIKE));
		}
		
		Vector result = MobileObjectDatabase.getInstance().query(service, input);		
		if(result != null && !result.isEmpty())
		{	
			result = filterProxies(result);
			int resultSize = result.size();	
			beans = new MobileBean[resultSize];
			for(int i=0; i<resultSize; i++)
			{
				MobileObject cour = (MobileObject)result.elementAt(i);
				beans[i] = new MobileBean(cour);
			}
		}
		
		return beans;
	}
	
	public static MobileBean[] queryByLikeAtleastOne(String service, GenericAttributeManager criteria)	
	{
		if(service == null)
		{
			throw new IllegalArgumentException("Service must be specified!!");
		}
		if(criteria == null || criteria.isEmpty())
		{
			throw new IllegalArgumentException("Query Criteria must be specified!!");
		}
		
		MobileBean[] beans = null;
		
		GenericAttributeManager input = new GenericAttributeManager();
		
		input.setAttribute("logicLink", new Integer(LogicChain.OR));
		
		Vector expressions = new Vector();
		input.setAttribute("expressions", expressions);
		
		String[] names = criteria.getNames();
		int size = names.length;
		for(int i=0; i<size; i++)
		{
			String rhs = criteria.getAttribute(names[i]).toString();
			expressions.addElement(LogicExpression.createInstance(names[i], rhs, LogicExpression.OP_LIKE));
		}
		
		Vector result = MobileObjectDatabase.getInstance().query(service, input);		
		if(result != null && !result.isEmpty())
		{	
			result = filterProxies(result);
			int resultSize = result.size();	
			beans = new MobileBean[resultSize];
			for(int i=0; i<resultSize; i++)
			{
				MobileObject cour = (MobileObject)result.elementAt(i);
				beans[i] = new MobileBean(cour);
			}
		}
		
		return beans;
	}
	
	public static MobileBean[] queryByContainsAll(String service,GenericAttributeManager criteria)
	{
		if(service == null)
		{
			throw new IllegalArgumentException("Service must be specified!!");
		}
		if(criteria == null || criteria.isEmpty())
		{
			throw new IllegalArgumentException("Query Criteria must be specified!!");
		}
		
		MobileBean[] beans = null;
		
		GenericAttributeManager input = new GenericAttributeManager();
		
		input.setAttribute("logicLink", new Integer(LogicChain.AND));
		
		Vector expressions = new Vector();
		input.setAttribute("expressions", expressions);
		
		String[] names = criteria.getNames();
		int size = names.length;
		for(int i=0; i<size; i++)
		{
			String rhs = criteria.getAttribute(names[i]).toString();
			expressions.addElement(LogicExpression.createInstance(names[i], rhs, LogicExpression.OP_CONTAINS));
		}
		
		Vector result = MobileObjectDatabase.getInstance().query(service, input);		
		if(result != null && !result.isEmpty())
		{	
			result = filterProxies(result);
			int resultSize = result.size();	
			beans = new MobileBean[resultSize];
			for(int i=0; i<resultSize; i++)
			{
				MobileObject cour = (MobileObject)result.elementAt(i);
				beans[i] = new MobileBean(cour);
			}
		}
		
		return beans;
	}
	
	public static MobileBean[] queryByContainsAtleastOne(String service, GenericAttributeManager criteria)	
	{
		if(service == null)
		{
			throw new IllegalArgumentException("Service must be specified!!");
		}
		if(criteria == null || criteria.isEmpty())
		{
			throw new IllegalArgumentException("Query Criteria must be specified!!");
		}
		
		MobileBean[] beans = null;
		
		GenericAttributeManager input = new GenericAttributeManager();
		
		input.setAttribute("logicLink", new Integer(LogicChain.OR));
		
		Vector expressions = new Vector();
		input.setAttribute("expressions", expressions);
		
		String[] names = criteria.getNames();
		int size = names.length;
		for(int i=0; i<size; i++)
		{
			String rhs = criteria.getAttribute(names[i]).toString();
			expressions.addElement(LogicExpression.createInstance(names[i], rhs, LogicExpression.OP_CONTAINS));
		}
		
		Vector result = MobileObjectDatabase.getInstance().query(service, input);		
		if(result != null && !result.isEmpty())
		{		
			result = filterProxies(result);
			int resultSize = result.size();	
			beans = new MobileBean[resultSize];
			for(int i=0; i<resultSize; i++)
			{
				MobileObject cour = (MobileObject)result.elementAt(i);
				beans[i] = new MobileBean(cour);
			}
		}
		
		return beans;
	}
	//--------------------------------------------------------------------------------------------------------------------------------------------
	private synchronized void clearAll()
	{
		this.data = null;
		this.isDirty = false;
		this.isNew = false;
	}
	
	private synchronized void clearMetaData()
	{
		this.isDirty = false;
		this.isNew = false;
	}
		
	//Loading proxies on demand gives a bad app experience....proxies need to be loaded in the background
	//by the MobileCloud
	/*private synchronized void loadProxy()
	{
		String service = this.getService();
		String beanId = this.getId();
		try
		{			
			if(this.data.isProxy())
			{				
				//Integration with the SyncService				
				SyncInvocation syncInvocation = new SyncInvocation("org.openmobster.core.mobileCloud.invocation.SyncInvocationHandler", 
				SyncInvocation.stream, service, beanId);		
				Bus.getInstance().invokeService(syncInvocation);
				
				
				//Refresh the bean state with the newly downloaded data
				this.refresh();
			}
		}
		catch(Exception e)
		{
			SystemException sys = new SystemException(this.getClass().getName(), "loadProxy", new Object[]{
				"Service="+service,
				"BeanId="+beanId,
				"SyncType=stream",					
				"Exception="+e.toString(),
				"Message="+e.getMessage()
			});
			ErrorHandler.getInstance().handle(sys);
			
			throw sys;
		}
	}*/	
	
	private static Vector filterProxies(Vector mobileObjects)
	{
		Vector filtered = new Vector();
		if(mobileObjects != null)
		{
			int size = mobileObjects.size();
			for(int i=0; i<size; i++)
			{
				MobileObject mo = (MobileObject)mobileObjects.elementAt(i);
				if(!mo.isProxy())
				{
					filtered.addElement(mo);
				}
			}
		}
		return filtered;
	}
}
