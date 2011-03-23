/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.console.server.admin;

import java.util.List;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.openmobster.core.console.server.Server;

/**
 * 
 * @author openmobster@gmail.com
 */
public class TestAdminAccount extends TestCase 
{
	private static Logger log = Logger.getLogger(TestAdminAccount.class);
	
	protected void setUp() throws Exception 
	{
		Server server = Server.getInstance();
		server.start();
	}

	protected void tearDown() throws Exception 
	{
		Server server = Server.getInstance();
		server.stop();
	}
	
	public void testAccountStorage() throws Exception
	{
		//test create
		AdminAccount account = new AdminAccount("root","password");
		account.deactivate();
		long oid = AccountDS.getInstance().create(account);
		
		log.info("**************************************");
		log.info("Oid: "+oid+" was successfully created");
		log.info("***************************************");
		
		assertTrue(oid > 0);
		
		//test read
		account = AccountDS.getInstance().read("root");
		log.info("**************************************");
		log.info("Password: "+account.getPassword());
		log.info("Activated: "+account.isActive());
		log.info("***************************************");
		assertEquals(account.getPassword(),"password");
		assertFalse(account.isActive());
		
		//test update
		account.activate();
		AccountDS.getInstance().update(account);
		account = AccountDS.getInstance().read("root");
		log.info("**************************************");
		log.info("Password: "+account.getPassword());
		log.info("Activated: "+account.isActive());
		log.info("***************************************");
		assertEquals(account.getPassword(),"password");
		assertTrue(account.isActive());
		
		log.info(account.toXml());
		
		List<AdminAccount> all = AccountDS.getInstance().readAll();
		log.info(AdminAccount.toXml(all));
	}
}
