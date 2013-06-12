/**
 * Copyright (c) {2003,2013} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.mysqlapp.cloud;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import org.hibernate.Session;
import org.hibernate.Transaction;

import org.openmobster.core.common.ServiceManager;
import org.openmobster.core.common.Utilities;
import org.openmobster.core.common.database.HibernateManager;

/**
 *
 * @author openmobster@gmail.com
 */
public class TestHibernate extends TestCase 
{
	private static Logger log = Logger.getLogger(TestHibernate.class);
	
	private HibernateManager hibernateManager;
	
	/**
	 * @param name
	 */
	public TestHibernate(String name) 
	{
		super(name);
	}

	/**
	 * 
	 */
	protected void setUp() throws Exception 
	{
		super.setUp();
		ServiceManager.bootstrap();
		
		this.hibernateManager = (HibernateManager)ServiceManager.locate("/mysqlapp/HibernateManager");
	}

	/**
	 * 
	 */
	protected void tearDown() throws Exception 
	{
		super.tearDown();
		
		ServiceManager.shutdown();
		this.hibernateManager = null;
	}
	//-------------------------------------------------------------------------------------------------------------
	public void testCreate() throws Exception
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			session = this.hibernateManager.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			String syncId = Utilities.generateUID();
			DataObject dataObject = new DataObject();
			dataObject.setField1("field1");
			dataObject.setField2("field2");
			dataObject.setField3("field3");
			dataObject.setField4("field4");
			dataObject.setSyncId(syncId);
			
			long id = (Long)session.save(dataObject);
			assertTrue(id>0);
						
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
}
