/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.pushmail.cloud.domain;

import java.util.List;

import org.apache.log4j.Logger;

import org.openmobster.core.common.ServiceManager;
import org.openmobster.core.common.Utilities;
import org.openmobster.pushmail.cloud.channel.MailBean;

import junit.framework.TestCase;


/**
 * 
 * @author openmobster@gmail.com
 */
public class TestMailProcessor extends TestCase
{
	private static Logger log = Logger.getLogger(TestMailProcessor.class);
	
	private MailProcessor mailProcessor;

	protected void setUp() throws Exception
	{
		ServiceManager.bootstrap();
		
		this.mailProcessor = (MailProcessor)ServiceManager.locate("mail_processor");
	}

	protected void tearDown() throws Exception
	{
		ServiceManager.shutdown();
	}

	//Disabling from the testsuite as Email credentials don't need to be part of the package
	//for security reasons
	public void testCheckMail()
	{
		/*log.info("Running testCheckMail..............");
		
		List<MailBean> inbox = this.mailProcessor.readInbox();
		assertTrue(inbox.size()>0);
		
		for(MailBean local:inbox)
		{
			byte[] messageBytes = Utilities.decodeBinaryData(local.getMessage());
			String msg = new String(messageBytes);
			log.info("****************************************");
			log.info("OID: "+local.getOid());
			log.info("From: "+local.getFrom());
			log.info("To: "+local.getTo());
			log.info("Subject: "+local.getSubject());
			log.info("Message: "+msg);
			log.info("ReceivedOn: "+local.getReceivedOn());
		}*/
	}
	
	public void testDeleteMail()
	{
		/*log.info("Running testDeleteMail..............");
		
		List<MailBean> inbox = this.mailProcessor.readInbox();
		assertTrue(inbox.size()>0);
		int before = inbox.size();
		
		MailBean toDelete = inbox.iterator().next();
		this.mailProcessor.delete(toDelete);
		
		 inbox = this.mailProcessor.readInbox();
		 assertTrue(inbox.size()>0);
		 int size = inbox.size();
		 assertTrue(before-1 == size);*/
	}
}
