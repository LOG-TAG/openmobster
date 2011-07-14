/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.synchronizer.server.engine;

import org.apache.log4j.Logger;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.openmobster.core.common.database.HibernateManager;
import org.openmobster.core.mobileObject.xml.MobileObjectSerializer;
import org.openmobster.core.synchronizer.SyncException;
import org.openmobster.server.api.model.MobileBean;

/**
 * FIXME: implement my persistence
 * 
 * @author openmobster@gmail.com
 */
public class ConflictEngine
{
	private static Logger log = Logger.getLogger(ConflictEngine.class);
	
	private MobileObjectSerializer serializer;
	private HibernateManager hibernateManager = null;
	
	public ConflictEngine()
	{
	}
	
	public void start()
	{
		
	}
	
	public void stop()
	{
		
	}
	
	public MobileObjectSerializer getSerializer()
	{
		return serializer;
	}

	public void setSerializer(MobileObjectSerializer serializer)
	{
		this.serializer = serializer;
	}
	
	
	public HibernateManager getHibernateManager()
	{
		return hibernateManager;
	}

	public void setHibernateManager(HibernateManager hibernateManager)
	{
		this.hibernateManager = hibernateManager;
	}
	//-------------------------------------------------------------------------------------------------------------------
	public void startOptimisticLock(MobileBean cloudBean) throws SyncException
	{
		String deviceId = Tools.getDeviceId();
		String serializedBean = this.serializer.serialize(cloudBean).trim();
		String oid = Tools.getOid(cloudBean);
		
		if(oid.startsWith("proxy[[") && oid.endsWith("]]"))
		{
			//don't lock...just proxy objects
			return;
		}
		
		ConflictEntry bean = this.readLock(deviceId, oid);
		
		log.debug("******StartLock*****************************************");
		log.debug("Serialized: "+serializedBean);
		log.debug("***********************************************");
		
		bean.setState(serializedBean);
		
		this.saveLock(bean);
	}
	
	public boolean checkOptimisticLock(MobileBean cloudBean) throws SyncException
	{
		String deviceId = Tools.getDeviceId();
		String oid = Tools.getOid(cloudBean);
		
		ConflictEntry bean = this.readLock(deviceId, oid);
		
		String state = bean.getState();
		if(state != null && state.trim().length()>0)
		{
			String serializedBean = this.serializer.serialize(cloudBean).trim();
			String checkAgainst = bean.getState();
			
			if(!serializedBean.equals(checkAgainst))
			{
				log.debug("**********Check Lock*************************************");
				log.debug("Serialized: "+serializedBean);
				log.debug("********************************************************");
				log.debug("Checkagainst: "+checkAgainst);
				log.debug("***********************************************");
				
				String channel = Tools.getChannel();
				
				this.handleConflict(deviceId, channel, cloudBean);
				return false;
			}
		}
		return true;
	}
	
	
	private void handleConflict(String deviceId,String channel, MobileBean bean)
	{
		//TODO: report this to the Console so that admins can pull and see what happened
	}
	
	public void clearAll()
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();

			List all = session.createQuery("from ConflictEntry").list();
			if(all != null && !all.isEmpty())
			{
				for(Object entry:all)
				{
					session.delete(entry);
				}
			}
			
			tx.commit();
		}
		catch(Exception e)
		{
			log.error(this, e);
			
			if(tx != null)
			{
				tx.rollback();
			}
			
			throw new SyncException(e);
		}
	}
	//------Persistence related code--------------------------------------------------------------------------------------------
	void saveLock(ConflictEntry conflictEntry)
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();

			if(conflictEntry.getId() == 0)
			{
				session.save(conflictEntry);
			}
			else
			{
				session.update(conflictEntry);
			}
			
			tx.commit();
		}
		catch(Exception e)
		{
			log.error(this, e);
			
			if(tx != null)
			{
				tx.rollback();
			}
			
			throw new SyncException(e);
		}
	}
	
	ConflictEntry readLock(String deviceId, String oid)
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			ConflictEntry local = null;
			
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			String query = "from ConflictEntry where deviceId=? AND oid=?";
			
			local = (ConflictEntry)session.createQuery(query).setParameter(0, deviceId).setParameter(1,oid).uniqueResult();
						
			tx.commit();
			
			if(local == null)
			{
				local = new ConflictEntry();
				local.setDeviceId(deviceId);
				local.setOid(oid);
			}
			
			return local;
		}
		catch(Exception e)
		{
			log.error(this, e);
			
			if(tx != null)
			{
				tx.rollback();
			}
			throw new SyncException(e);
		}
	}
}
