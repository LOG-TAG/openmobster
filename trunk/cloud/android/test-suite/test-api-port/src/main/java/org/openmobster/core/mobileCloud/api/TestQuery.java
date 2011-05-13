/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.api;

import org.openmobster.core.mobileCloud.api.model.MobileBean;
import org.openmobster.core.mobileCloud.android.util.GenericAttributeManager;

/**
 * @author openmobster@gmail.com
 *
 */
public final class TestQuery extends AbstractAPITest 
{
	public void runTest()
	{		
		try
		{
			this.startBootSync();
			this.waitForBeans();
									
			MobileBean[] beans = MobileBean.readAll(this.service);
			this.assertNotNull(beans, this.getInfo()+"/MustNotBeNull");
			
			if(beans == null)
			{
				return;
			}
			
			//Download all data associated with the beans (takes care of proxy-lazy loaded beans)						
			//Wait for proxy loading to load the rest
			int attempts = 5;
			while(beans.length < 2 && attempts > 0)
			{
				System.out.println("Waiting on background proxy loading.........");
				Thread.currentThread().sleep(20000);
				beans = MobileBean.readAll(this.service);
				attempts--;
			}
			
			if(beans.length < 2)
			{
				throw new IllegalStateException("Background State Management was not able to get the State ready in time...Try again");
			}
			
			//AND Equals query testing
			this.testEqualsANDQuery();
			
			//OR Equals query
			this.testEqualsORQuery();
			
			//AND NotEquals query
			this.testNotEqualsANDQuery();
			
			//OR NotEquals query
			this.testNotEqualsORQuery();
			
			//AND Like query testing
			this.testLikeANDQuery();
			
			//OR Like query
			this.testLikeORQuery();
			
			//AND Contains query testing
			this.testContainsANDQuery();
			
			//OR Contains query
			this.testContainsORQuery();
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			throw new RuntimeException(e.toString());
		}
	}
	//--------------------------------------------------------------------------------------------------------------------------------
	private void testEqualsANDQuery() throws Exception
	{
		GenericAttributeManager criteria = new GenericAttributeManager();
		criteria.setAttribute("from", "from@gmail.com");
		criteria.setAttribute("subject", "This is the subject<html><body>unique-1</body></html>");
		
		MobileBean[] beans = MobileBean.queryByEqualsAll(this.service, criteria);
		this.assertNotNull(beans, getInfo()+"://testEqualsANDQuery/MustNotBeNull");
		this.assertTrue(beans.length==1, getInfo()+"://testEqualsANDQuery/MustHaveOneBean");
		
		int length = beans.length;
		System.out.println("testEqualsANDQuery-----------------------------------------");
		for(int i=0; i<length; i++)
		{
			MobileBean bean = beans[i];
						
			System.out.println("OID="+bean.getId());
			System.out.println("From="+bean.getValue("from"));
			System.out.println("Subject="+bean.getValue("subject"));
			System.out.println("-----------------------------------------");
			assertEquals(bean.getId(),"unique-1",getInfo()+"://testEqualsANDQuery/beanId/DoesNotMatch");
		}
	}
	
	private void testEqualsORQuery() throws Exception
	{
		GenericAttributeManager criteria = new GenericAttributeManager();
		criteria.setAttribute("from", "from@gmail.com");
		criteria.setAttribute("subject", "This is the subject<html><body>unique-1</body></html>");
		
		MobileBean[] beans = MobileBean.queryByEqualsAtleastOne(this.service, criteria);
		this.assertNotNull(beans, getInfo()+"://testEqualsORQuery/MustNotBeNull");
		this.assertTrue(beans.length==2, getInfo()+"://testEqualsORQuery/MustHaveTwoBeans");
		
		int length = beans.length;
		System.out.println("testEqualsORQuery-----------------------------------------");
		for(int i=0; i<length; i++)
		{
			MobileBean bean = beans[i];
						
			System.out.println("OID="+bean.getId());
			System.out.println("From="+bean.getValue("from"));
			System.out.println("Subject="+bean.getValue("subject"));
			System.out.println("-----------------------------------------");			
		}
	}
	//-----------------------------------------------------------------------------------------------------------------------------------
	private void testNotEqualsANDQuery() throws Exception
	{
		GenericAttributeManager criteria = new GenericAttributeManager();
		criteria.setAttribute("from", "from@gmail.com");
		criteria.setAttribute("subject", "This is the subject<html><body>unique-2</body></html>");
		
		MobileBean[] beans = MobileBean.queryByNotEqualsAll(this.service, criteria);
		this.assertTrue(beans==null || beans.length==0, getInfo()+"://testNotEqualsANDQuery/MustBeNull");		
	}
	
	private void testNotEqualsORQuery() throws Exception
	{
		GenericAttributeManager criteria = new GenericAttributeManager();
		criteria.setAttribute("from", "from@gmail.com");
		criteria.setAttribute("subject", "This is the subject<html><body>unique-1</body></html>");
		
		MobileBean[] beans = MobileBean.queryByNotEqualsAtleastOne(this.service, criteria);
		this.assertNotNull(beans, getInfo()+"://testNotEqualsORQuery/MustNotBeNull");
		this.assertTrue(beans.length==1, getInfo()+"://testNotEqualsORQuery/MustHaveTwoBeans");
		
		int length = beans.length;
		System.out.println("testNotEqualsORQuery-----------------------------------------");
		for(int i=0; i<length; i++)
		{
			MobileBean bean = beans[i];
						
			System.out.println("OID="+bean.getId());
			System.out.println("From="+bean.getValue("from"));
			System.out.println("Subject="+bean.getValue("subject"));
			System.out.println("-----------------------------------------");
			assertEquals(bean.getId(),"unique-2",getInfo()+"://testNotEqualsANDQuery/beanId/DoesNotMatch");
		}
	}
	//-----------------------------------------------------------------------------------------------------------------------------------
	private void testLikeANDQuery() throws Exception
	{
		GenericAttributeManager criteria = new GenericAttributeManager();
		criteria.setAttribute("from", "from");
		criteria.setAttribute("subject", "This is the subject<html><body>unique-1");
		
		MobileBean[] beans = MobileBean.queryByLikeAll(this.service, criteria);
		this.assertNotNull(beans, getInfo()+"://testLikeANDQuery/MustNotBeNull");
		this.assertTrue(beans.length==1, getInfo()+"://testLikeANDQuery/MustHaveOneBean");
		
		int length = beans.length;
		System.out.println("testLikeANDQuery-----------------------------------------");
		for(int i=0; i<length; i++)
		{
			MobileBean bean = beans[i];
						
			System.out.println("OID="+bean.getId());
			System.out.println("From="+bean.getValue("from"));
			System.out.println("Subject="+bean.getValue("subject"));
			System.out.println("-----------------------------------------");	
			assertEquals(bean.getId(),"unique-1",getInfo()+"://testEqualsANDQuery/beanId/DoesNotMatch");
		}
	}
	
	private void testLikeORQuery() throws Exception
	{
		GenericAttributeManager criteria = new GenericAttributeManager();
		criteria.setAttribute("from", "from");
		criteria.setAttribute("subject", "This is the subject<html><body>unique-1");
		
		MobileBean[] beans = MobileBean.queryByLikeAtleastOne(this.service, criteria);
		this.assertNotNull(beans, getInfo()+"://testLikeORQuery/MustNotBeNull");
		this.assertTrue(beans.length==2, getInfo()+"://testLikeORQuery/MustHaveTwoBeans");
		
		int length = beans.length;
		System.out.println("testLikeORQuery-----------------------------------------");
		for(int i=0; i<length; i++)
		{
			MobileBean bean = beans[i];
						
			System.out.println("OID="+bean.getId());
			System.out.println("From="+bean.getValue("from"));
			System.out.println("Subject="+bean.getValue("subject"));
			System.out.println("-----------------------------------------");			
		}
	}
	//----------------------------------------------------------------------------------------------------------------------------------
	private void testContainsANDQuery() throws Exception
	{
		GenericAttributeManager criteria = new GenericAttributeManager();
		criteria.setAttribute("subject", "unique-1");
		criteria.setAttribute("message", "unique-1");
		
		MobileBean[] beans = MobileBean.queryByContainsAll(this.service, criteria);
		this.assertNotNull(beans, getInfo()+"://testContainsANDQuery/MustNotBeNull");
		this.assertTrue(beans.length==1, getInfo()+"://testContainsANDQuery/MustHaveOneBean");
		
		int length = beans.length;
		System.out.println("testContainsANDQuery-----------------------------------------");
		for(int i=0; i<length; i++)
		{
			MobileBean bean = beans[i];
						
			System.out.println("OID="+bean.getId());
			System.out.println("From="+bean.getValue("from"));
			System.out.println("Subject="+bean.getValue("subject"));
			System.out.println("-----------------------------------------");	
			assertEquals(bean.getId(),"unique-1",getInfo()+"://testContainsANDQuery/beanId/DoesNotMatch");
		}
	}
	
	private void testContainsORQuery() throws Exception
	{
		GenericAttributeManager criteria = new GenericAttributeManager();
		criteria.setAttribute("subject", "unique-1");
		criteria.setAttribute("message", "unique-2");
		
		MobileBean[] beans = MobileBean.queryByContainsAtleastOne(this.service, criteria);
		this.assertNotNull(beans, getInfo()+"://testContainsORQuery/MustNotBeNull");
		this.assertTrue(beans.length==2, getInfo()+"://testContainsORQuery/MustHaveTwoBeans");
		
		int length = beans.length;
		System.out.println("testContainsORQuery-----------------------------------------");
		for(int i=0; i<length; i++)
		{
			MobileBean bean = beans[i];
						
			System.out.println("OID="+bean.getId());
			System.out.println("From="+bean.getValue("from"));
			System.out.println("Subject="+bean.getValue("subject"));
			System.out.println("-----------------------------------------");			
		}
	}
}
