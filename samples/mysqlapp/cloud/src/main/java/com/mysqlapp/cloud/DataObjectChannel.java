/**
 * Copyright (c) {2003,2013} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.mysqlapp.cloud;

import org.apache.log4j.Logger;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;

import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openmobster.cloud.api.sync.Channel;
import org.openmobster.cloud.api.sync.ChannelInfo;
import org.openmobster.cloud.api.sync.MobileBean;
import org.openmobster.cloud.api.ExecutionContext;

import org.openmobster.core.security.device.Device;
import org.openmobster.core.security.device.DeviceController;
import org.openmobster.core.common.database.HibernateManager;
import org.openmobster.core.common.transaction.TransactionHelper;

/**
 *
 * @author openmobster@gmail.com
 */
@ChannelInfo(uri="data_object_channel", mobileBeanClass="com.mysqlapp.cloud.DataObject")
public class DataObjectChannel implements Channel 
{
	private static Logger log = Logger.getLogger(DataObjectChannel.class);
	
	private HibernateManager hibernateManager;
	private NewBeanDetector newBeanDetector;
	private DeviceController deviceController;
	
	public DataObjectChannel()
	{
		this.newBeanDetector = new NewBeanDetector();
	}
	
	public HibernateManager getHibernateManager() 
	{
		return hibernateManager;
	}

	public void setHibernateManager(HibernateManager hibernateManager) 
	{
		this.hibernateManager = hibernateManager;
	}
	
	public DeviceController getDeviceController() 
	{
		return deviceController;
	}

	public void setDeviceController(DeviceController deviceController) 
	{
		this.deviceController = deviceController;
	}

	public void start()
	{
		boolean startedHere = TransactionHelper.startTx();
		try
		{
			List<Device> registeredDevices = deviceController.readAll();
			if(registeredDevices == null)
			{
				return;
			}
			for(Device device:registeredDevices)
			{
				this.newBeanDetector.load(device);
			}
			
			if(startedHere)
			{
				TransactionHelper.commitTx();
			}
		}
		catch(Exception e)
		{
			log.error(this, e);
			
			if(startedHere)
			{
				TransactionHelper.rollbackTx();
			}
		}
	}
	
	public void stop()
	{
		
	}

	/**
	 * Reads a subset of all Mobile Beans from the backend storage that are enough to 
	 * get a service functional on the device. This saves against loading up lots of unnecessary beans on the 
	 * storage constrained device. How many/which beans to return are totally at the discretion of the service being
	 * mobilized and could even contain all the beans on the server
	 * 
	 * @return a list of mobile beans
	 */
	public List<? extends MobileBean> bootup() 
	{
		//This implementation boots up the device with all the data in the database table to keep things simple
		return this.readAll();
	}

	/**
	 * Reads all the Mobile Beans managed by this connector from the corresponding backend storage system
	 * 
	 * @return a list of mobile beans
	 */
	public List<? extends MobileBean> readAll() 
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			List<DataObject> objects = new ArrayList<DataObject>();
			
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			String query = "from DataObject";
			
			List cour = session.createQuery(query).list();
			
			if(cour != null)
			{
				objects.addAll(cour);
			}
						
			tx.commit();
			
			return objects;
		}
		catch(Exception e)
		{
			log.error(this, e);
			
			if(tx != null)
			{
				tx.rollback();
			}
			throw new RuntimeException(e);
		}
	}

	/**
	 * Reads the Mobile Bean uniquely identified by the specified id from the backend storage system
	 * 
	 * @param id must uniquely identify the Mobile Bean instance that needs to be read
	 * @return the Mobile Bean instance uniquely identified by the specified id
	 */
	public MobileBean read(String syncId) 
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			DataObject dataObject = null;
			
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			String query = "from DataObject where syncId=?";
			
			dataObject = (DataObject)session.createQuery(query).setParameter(0, syncId).uniqueResult();
						
			tx.commit();
			
			return dataObject;
		}
		catch(Exception e)
		{
			log.error(this, e);
			
			if(tx != null)
			{
				tx.rollback();
			}
			throw new RuntimeException(e);
		}
	}

	/**
	 * Creates a new instance of the specified Mobile Bean within the backend storage system
	 * 
	 * @param mobileBean the Mobile Bean that must be created
	 * @return unique identifier of the newly created instance
	 */
	public String create(MobileBean mobileBean) 
	{
		ExecutionContext context = ExecutionContext.getInstance();
		Device device = context.getDevice();
		DataObject newObject = (DataObject)mobileBean;
		Session session = null;
		Transaction tx = null;
		try
		{
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();

			session.save(newObject);
						
			tx.commit();
			
			this.newBeanDetector.addSyncId(device, newObject.getSyncId());
			
			return newObject.getSyncId();
		}
		catch(Exception e)
		{
			log.error(this, e);
			
			if(tx != null)
			{
				tx.rollback();
			}
			
			throw new RuntimeException(e);
		}
	}

	/**
	 * Updates an existing instance of the specified Mobile Bean within the backend storage system
	 * 
	 * @param mobileBean - the Mobile Bean that must be updated
	 */
	public void update(MobileBean mobileBean) 
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
						
			session.update((DataObject)mobileBean);
						
			tx.commit();
		}
		catch(Exception e)
		{
			log.error(this, e);
			
			if(tx != null)
			{
				tx.rollback();
			}
			throw new RuntimeException(e);
		}
	}

	/**
	 * Permanently deletes the specified MobileBean from the backend storage system
	 * 
	 * @param mobileBean - the Mobile Bean that must be deleted
	 */
	public void delete(MobileBean mobileBean) 
	{	
		Session session = null;
		Transaction tx = null;
		try
		{
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			session.delete((DataObject)mobileBean);
						
			tx.commit();
		}
		catch(Exception e)
		{
			log.error(this, e);
			
			if(tx != null)
			{
				tx.rollback();
			}
			throw new RuntimeException(e);
		}
	}
	//---------------------------------------------------------------------------------------------------------------------------------------------------------------------
	/**
	 * Scan for any Mobile Bean creations that need to be synchronized with the specified device
	 * 
	 * @param device Device for which the data applies
	 * @param lastScanTimestamp timestamp when the last scan was done
	 * @return an array of Strings which represent the unique ids of the new Mobile Beans
	 */
	public String[] scanForNew(Device device, Date lastScanTimestamp) 
	{
		Set<String> newBeans = this.newBeanDetector.scan(device);
		
		if(newBeans != null && !newBeans.isEmpty())
		{
			return newBeans.toArray(new String[0]);
		}
		
		return null;
	}

	/**
	 * Scan for any Mobile Bean modifications that need to be synchronized with the specified device
	 * 
	 * @param device Device for which the data changes apply
	 * @param lastScanTimestamp timestamp when the last scan was done
	 * @return an array of Strings which represent the unique ids of the modified Mobile Beans
	 */
	public String[] scanForUpdates(Device device, Date lastScanTimestamp) 
	{
		return null;
	}

	/**
	 * Scan for any Mobile Bean deletions that need to be synchronized with the specified device
	 * 
	 * @param device Device for which the data applies
	 * @param lastScanTimestamp timestamp when the last scan was done
	 * @return an array of Strings which represent the unique ids of the deleted Mobile Beans
	 */
	public String[] scanForDeletions(Device device, Date lastScanTimestamp) 
	{
		return null;
	}
	//---------------------------------------------------------------------------------------------------------------------------------------------------------------------
	private class NewBeanDetector
	{
		private Map<String,Set<String>> device_to_bean_map;
		
		private NewBeanDetector()
		{
			this.device_to_bean_map = new HashMap<String,Set<String>>();
		}
		
		/**
		 * Load a set of syncIds already loaded on this device. This makes sure a duplicate object is not pushed to the device
		 *  
		 * @param device
		 */
		private void load(Device device)
		{
			String deviceIdentifier = device.getIdentifier();
			Set<String> readAll = this.readAll();
			
			this.device_to_bean_map.put(deviceIdentifier, readAll);
		}
		
		private void addSyncId(Device device,String syncId)
		{
			String deviceIdentifier = device.getIdentifier();
			Set<String> deviceBeans = this.device_to_bean_map.get(deviceIdentifier);
			if(deviceBeans == null)
			{
				Set<String> syncIds = new HashSet<String>();
				syncIds.add(syncId);
				this.device_to_bean_map.put(deviceIdentifier, syncIds);
				return;
			}
			deviceBeans.add(syncId);
		}
		
		/**
		 * Scan the database for new syncIds. The new syncIds are pushed to the device during synchronization
		 * 
		 * @param device
		 * @return
		 */
		private Set<String> scan(Device device)
		{
			Set<String> newBeans = new HashSet<String>();
			
			String deviceIdentifier = device.getIdentifier();
			Set<String> all = this.readAll();
			
			Set<String> deviceBeans = this.device_to_bean_map.get(deviceIdentifier);
			if(deviceBeans == null)
			{
				this.device_to_bean_map.put(deviceIdentifier, all);
				return newBeans;
			}
			
			for(String syncId:all)
			{
				if(!deviceBeans.contains(syncId))
				{
					//this is a new bean
					newBeans.add(syncId);
					deviceBeans.add(syncId);
				}
			}
			
			return newBeans;
		}
		
		/**
		 * Load all the syncIds stored in the database. This is used for diff and detecting new syncIds on a given device
		 * 
		 * @return
		 */
		private Set<String> readAll()
		{
			Session session = null;
			Transaction tx = null;
			try
			{
				Set<String> allIds = new HashSet<String>();
				
				session = hibernateManager.getSessionFactory().getCurrentSession();
				tx = session.beginTransaction();
				
				String query = "from DataObject";
				
				List cour = session.createQuery(query).list();
				
				if(cour != null)
				{
					for(Object object:cour)
					{
						DataObject dataObject = (DataObject)object;
						allIds.add(dataObject.getSyncId());
					}
				}
							
				tx.commit();
				
				return allIds;
			}
			catch(Exception e)
			{
				log.error(this, e);
				
				if(tx != null)
				{
					tx.rollback();
				}
				throw new RuntimeException(e);
			}
		}
	}
}
