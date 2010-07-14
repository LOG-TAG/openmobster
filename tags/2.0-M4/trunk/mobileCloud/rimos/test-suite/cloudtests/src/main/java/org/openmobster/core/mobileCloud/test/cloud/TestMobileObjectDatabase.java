/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.test.cloud;

import test.openmobster.core.mobileCloud.rimos.testsuite.Test;

import org.openmobster.core.mobileCloud.rimos.module.mobileObject.*;

/**
 * @author openmobster@gmail.com
 *
 */
public final class TestMobileObjectDatabase extends Test 
{
	public void runTest()
	{		
		MobileObject mobileObject = new MobileObject();
		mobileObject.setRecordId("unique-1");
		mobileObject.setStorageId("blackberry.email.database");
		
		String recordId = MobileObjectDatabase.getInstance().create(mobileObject);
		
		//Assertion
		System.out.println("New Record Created with Id="+recordId);
		assertNotNull(recordId, this.getClass().getName()+"://TestMobileObjectDatabase/RecordIdIsNull");
		assertEquals(recordId,"unique-1", this.getClass().getName()+"://TestMobileObjectDatabase/RecordIdMismatch");
	}
}
