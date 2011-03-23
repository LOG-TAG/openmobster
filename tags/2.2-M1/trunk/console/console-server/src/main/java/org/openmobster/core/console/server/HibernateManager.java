/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.console.server;

import org.w3c.dom.Document;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import org.apache.log4j.Logger;

/**
 * 
 * @author openmobster@gmail.com
 */
public class HibernateManager
{
	private static HibernateManager singleton;
	
	private static Logger log = Logger.getLogger(HibernateManager.class);
	
	private String config = null;
		
	private SessionFactory sessionFactory = null;
	
	private HibernateManager()
	{
		
	}
	
	public static HibernateManager getInstance()
	{
		if(singleton == null)
		{
			synchronized(HibernateManager.class)
			{
				if(singleton == null)
				{
					singleton = new HibernateManager();
				}
			}
		}
		return singleton;
	}
		
	public String getConfig()
	{
		return this.config;
	}
		
	public void setConfig(String config)
	{
		this.config = config;
	}
	
	//service lifecycle------------------------------------------------------------------------------------------	
	public void create()
	{
		try
		{
			if(this.config != null && this.config.trim().length() > 0)
			{
				//Load using the specified configuration location
				Configuration configuration = new Configuration();
				configuration.configure(this.config);
				this.sessionFactory = configuration.buildSessionFactory();
			}
			else
			{
				//Load using the default location
				this.sessionFactory = new Configuration().configure().buildSessionFactory();
			}
		}
		catch(Exception e)
		{
			log.error(this, e);
			throw new RuntimeException(e);
		}
	}
		
	public void start()
	{	
	}
		
	public void stop()
	{
	}
	
	public void destroy()
	{
		if(this.sessionFactory != null)
		{
			this.sessionFactory.close();
		}
		
		this.config = null;
		this.sessionFactory = null;
	}
	//-------------------------------------------------------------------------------------------------------------
	/**
	 * return the Session Factory instance managed by this HibernateManager instance
	 */
	public SessionFactory getSessionFactory()
	{
		return this.sessionFactory;
	}
	
	public void startSessionFactory(Document doc)
	{
		//Load using the specified configuration location
		Configuration configuration = new Configuration();
		configuration.configure(doc);
		this.sessionFactory = configuration.buildSessionFactory();
	}
}
