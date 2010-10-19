/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.test.api;

import org.openmobster.core.mobileCloud.api.model.BeanList;
import org.openmobster.core.mobileCloud.api.model.BeanListEntry;
import org.openmobster.core.mobileCloud.api.model.MobileBean;

import test.openmobster.core.mobileCloud.rimos.testsuite.AbstractAPITest;

/**
 * @author openmobster@gmail.com
 *
 */
public final class TestBeanRead extends AbstractAPITest 
{
	public void runTest()
	{		
		try
		{
			this.startBootSync();
			this.waitForBeans();
			
			MobileBean[] beans = MobileBean.readAll(this.service);
			this.assertNotNull(beans, this.getInfo()+"/MustNotBeNull");
			
			for(int i=0; i<beans.length; i++)
			{
				MobileBean curr = beans[i];
				
				assertEquals(curr.getService(), this.service, this.getInfo()+"://Service does not match");
				
				String id = curr.getId();
				assertTrue(id.equals("unique-1") || id.equals("unique-2"), this.getInfo()+"://Id Does not match");
				
				assertEquals(curr.getValue("from"), "from@gmail.com", this.getInfo()+"://From does not match");
				assertEquals(curr.getValue("to"), "to@gmail.com", this.getInfo()+"://To does not match");
				assertEquals(curr.getValue("subject"), "This is the subject<html><body>"+id+"</body></html>", this.getInfo()+"://Subject does not match");
				assertEquals(curr.getValue("message"), 
				"<tag apos='apos' quote=\"quote\" ampersand='&'>"+id+"/Message"+"</tag>",
				this.getInfo()+"://Message does not match");
				
				//Assert the IndexedProperty emails
				this.assertEmails(curr);				
				
				//Assert Fruits
				this.assertFruits(curr);
			}
		}
		catch(Exception e)
		{
			throw new RuntimeException(e.toString());
		}
	}
	
	private void assertEmails(MobileBean mobileBean)
	{
		BeanList emails = mobileBean.readList("emails");
		assertTrue(emails!=null && emails.size()>0, this.getInfo()+"/Emails/MustNotBeNull");
		for(int index=0; index<emails.size(); index++)
		{
			BeanListEntry email = emails.getEntryAt(index);
			System.out.println(email.getProperty("from"));
			System.out.println(email.getProperty("to"));
			System.out.println(email.getProperty("subject"));
			System.out.println(email.getProperty("message"));
			this.assertEquals(email.getProperty("from"), index+"://from", this.getClass()+"://from");
			this.assertEquals(email.getProperty("to"), index+"://to", this.getClass()+"://to");
			this.assertEquals(email.getProperty("subject"), index+"://subject", this.getClass()+"://subject");
			this.assertEquals(email.getProperty("message"), index+"://message", this.getClass()+"://message");
		}
	}
	
	private void assertFruits(MobileBean mobileBean)
	{
		BeanList fruits = mobileBean.readList("fruits");
		assertTrue(fruits!=null && fruits.size()>0, this.getInfo()+"/Fruits/MustNotBeNull");
		for(int index=0; index<fruits.size(); index++)
		{
			BeanListEntry fruit = fruits.getEntryAt(index);
			System.out.println(fruit.getProperty("fruits"));						
			this.assertEquals(fruit.getProperty("fruits"), index+"://fruit", this.getClass()+"://fruit");
		}
	}
}
