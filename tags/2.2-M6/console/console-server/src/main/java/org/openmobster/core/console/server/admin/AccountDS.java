/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.console.server.admin;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import org.hibernate.Session;
import org.hibernate.Transaction;

import org.openmobster.core.console.server.HibernateManager;
import org.openmobster.core.security.IDMException;

/**
 * 
 * @author openmobster@gmail.com
 */
public class AccountDS 
{
	private static Logger log = Logger.getLogger(AccountDS.class);
	private static AccountDS singleton;
	
	private AccountDS()
	{
		
	}
	
	public static AccountDS getInstance()
	{
		if(singleton == null)
		{
			synchronized(AccountDS.class)
			{
				if(singleton == null)
				{
					singleton = new AccountDS();
				}
			}
		}
		return singleton;
	}
	
	public long create(AdminAccount adminAccount)
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			session = HibernateManager.getInstance().getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			long oid = (Long)session.save(adminAccount);
						
			tx.commit();
			
			return oid;
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
	
	public AdminAccount read(String username)
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			AdminAccount account = null;
			
			session = HibernateManager.getInstance().getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			String query = "from AdminAccount where username=?";
			
			account = (AdminAccount)session.createQuery(query).setString(0, username).uniqueResult();
						
			tx.commit();
			
			return account;
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
	
	public void update(AdminAccount account) 
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			session = HibernateManager.getInstance().getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			session.update(account);
						
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
	
	public List<AdminAccount> readAll()
	{
		Session session = null;
		Transaction tx = null;
		try
		{
			List<AdminAccount> accounts = new ArrayList<AdminAccount>();
			
			session = HibernateManager.getInstance().getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			String query = "from AdminAccount";
			
			List cour = session.createQuery(query).list();
			accounts.addAll(cour);
						
			tx.commit();
			
			return accounts;
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
	
	public boolean exists(String username)
	{
		Session session = null;
		Transaction tx = null;
		try
		{			
			session = HibernateManager.getInstance().getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			String query = "from AdminAccount where username=?";
			
			List count = session.createQuery(query).setString(0, username).list();
									
			boolean exists = false;
			if(count != null && !count.isEmpty())
			{
				exists = true;
			}
			
			tx.commit();
			
			return exists;
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
