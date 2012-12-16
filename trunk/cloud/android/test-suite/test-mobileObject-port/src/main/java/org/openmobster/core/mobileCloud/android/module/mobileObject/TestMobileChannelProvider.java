/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.module.mobileObject;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

import org.openmobster.core.mobileCloud.android.testsuite.Test;

/**
 * @author openmobster@gmail.com
 */
public class TestMobileChannelProvider extends Test 
{	
	@Override
	public void runTest()
	{
		try
		{
			//cleanup
			this.testDeleteAll();
			
			Set<String> ids = new HashSet<String>();
			for(int i=0; i<5; i++)
			{
				String id = this.testInsert();
				ids.add(id);
			}
						
			this.testQueryAll();
			
			String id = ids.iterator().next();
			this.testQuery(id);
			this.testUpdate(id);
			this.testDelete(id);
			
			this.testDeleteAll();
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			throw new RuntimeException(e);
		}
	}
	
	private String testInsert()
	{
		MobileObject mobileObject = new MobileObject();
		mobileObject.setStorageId("email");
		mobileObject.setCreatedOnDevice(false);
		mobileObject.setLocked(false);
		mobileObject.setProxy(false);
		
		String id = MobileObjectDatabase.getInstance().
		create(mobileObject);
		assertNotNull(id,"testInsert://IdMustNotBeNull");
						
		return id;
	}
	
	private void testQueryAll()
	{
		Set<MobileObject> all = MobileObjectDatabase.getInstance().readAll("email");		
		assertTrue(all!=null && !all.isEmpty(),"testQueryAll://objects_not_found");
		assertEquals(""+all.size(),"5","testQueryAll://#_of_objects_mismatch");
		
		for(MobileObject mo:all)
		{
			System.out.println("--------------------------------------------------");
			System.out.println("RecordId: "+mo.getRecordId());
			System.out.println("Channel: "+mo.getStorageId());
			System.out.println("--------------------------------------------------");
		}		
	}
	
	private void testQuery(String recordId)
	{
		MobileObject mobileObject = MobileObjectDatabase.getInstance().
		read("email", recordId);
		
		this.assertEquals(mobileObject.getRecordId(), 
		recordId, "testQuery://IdMisMatch");
	}
	
	private void testUpdate(String recordId)
	{
		MobileObjectDatabase database = MobileObjectDatabase.getInstance();
		MobileObject mobileObject = database.read("email", recordId);
		
		mobileObject.setProxy(true);
		database.update(mobileObject);
			
		mobileObject = database.read("email", recordId);		
		assertTrue(mobileObject.isProxy(),"testUpdate://isProxyMismatch");
	}
	
	private void testDelete(String recordId)
	{
		MobileObjectDatabase database = MobileObjectDatabase.getInstance();
		
		MobileObject mobileObject = database.read("email", recordId);
		database.delete(mobileObject);
		
		mobileObject = database.read("email", recordId);
		assertNull(mobileObject,"testDelete://RecordNotDeleted");
	}
	
	private void testDeleteAll()
	{
		MobileObjectDatabase database = MobileObjectDatabase.getInstance();
		database.deleteAll("email");
		
		Set<MobileObject> all = database.readAll("email");	
		assertTrue(all == null || all.isEmpty(),"testDeleteAll://ChannelShouldBeEmpty");
	}
}
