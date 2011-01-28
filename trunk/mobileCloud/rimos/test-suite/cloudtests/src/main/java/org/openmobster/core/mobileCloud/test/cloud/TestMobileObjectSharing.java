/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.test.cloud;

import test.openmobster.core.mobileCloud.rimos.testsuite.Test;

import org.openmobster.core.mobileCloud.rimos.module.bus.Bus;
import org.openmobster.core.mobileCloud.rimos.module.bus.Invocation;
import org.openmobster.core.mobileCloud.rimos.module.bus.InvocationResponse;
import org.openmobster.core.mobileCloud.rimos.module.mobileObject.*;

/**
 * @author openmobster@gmail.com
 *
 */
public final class TestMobileObjectSharing extends Test 
{
	public void runTest()
	{
		try
		{
			MobileObject mobileObject = new MobileObject();
			mobileObject.setRecordId("unique-1");
			mobileObject.setStorageId("test.sharing");
			mobileObject.setValue("blah", "blah");
			
			String recordId = MobileObjectDatabase.getInstance().create(mobileObject);
			
			//Assertion
			System.out.println("New Record Created with Id="+recordId);
			assertNotNull(recordId, this.getClass().getName()+":/RecordIdIsNull");
			assertEquals(recordId,"unique-1", this.getClass().getName()+":/RecordIdMismatch");
			
			//Read the object locally
			mobileObject = MobileObjectDatabase.getInstance().read("test.sharing", "unique-1");
			String localResult = mobileObject.getValue("blah"); 
			System.out.println("LocalResult------------------------------------");
			System.out.println(localResult);
			System.out.println("------------------------------------------------");
			assertEquals(localResult, "blah", this.getClass()+"://Local/ObjectValueMismatch");
		}
		catch(Exception e)
		{
			throw new RuntimeException(e.getMessage());
		}
	}
}
