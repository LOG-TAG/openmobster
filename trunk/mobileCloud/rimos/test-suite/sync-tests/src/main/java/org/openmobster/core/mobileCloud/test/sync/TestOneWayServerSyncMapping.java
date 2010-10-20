/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.test.sync;

import org.openmobster.core.mobileCloud.rimos.module.mobileObject.MobileObject;
import org.openmobster.core.mobileCloud.rimos.module.sync.*;

/**
 * 
 * @author openmobster@gmail.com
 *
 */
public final class TestOneWayServerSyncMapping extends AbstractMapSyncTest
{		
	/**
	 * 
	 */
	public void runTest()
	{
		try
		{
			//Add test case
			this.setUp("add");
			SyncService.getInstance().performOneWayServerSync(service, service, false);
			this.assertRecordPresence("unique-1-luid", this.getInfo()+"/add");
			this.assertRecordPresence("unique-2-luid", this.getInfo()+"/add");
			this.assertRecordPresence("unique-3-luid", this.getInfo()+"/add");
			this.assertRecordPresence("unique-4-luid", this.getInfo()+"/add");
			this.assertRecordAbsence("unique-1", this.getInfo()+"/add");
			this.assertRecordAbsence("unique-2", this.getInfo()+"/add");
			this.assertRecordAbsence("unique-3", this.getInfo()+"/add");
			this.assertRecordAbsence("unique-4", this.getInfo()+"/add");
			this.tearDown();		
			
			//Replace test case
			this.setUp("replace");
			SyncService.getInstance().performOneWayServerSync(service, service, false);
			MobileObject afterUnique1 = this.getRecord("unique-1-luid");
			MobileObject afterUnique2 = this.getRecord("unique-2-luid");
			this.assertRecordPresence("unique-1-luid", this.getInfo()+"/replace");
			this.assertRecordPresence("unique-2-luid", this.getInfo()+"/replace");
			this.assertRecordAbsence("unique-1", this.getInfo()+"/replace");
			this.assertRecordAbsence("unique-2", this.getInfo()+"/replace");
			this.assertEquals(afterUnique1.getValue("message"), 
			"<tag apos='apos' quote=\"quote\" ampersand='&'>unique-1/Updated/Server</tag>", 
			this.getInfo()+"/replace/updated/unique-1");
			this.assertEquals(afterUnique2.getValue("message"), 
			"<tag apos='apos' quote=\"quote\" ampersand='&'>unique-2/Updated/Client</tag>", 
			this.getInfo()+"/replace/updated/unique-2");
			this.tearDown();
			
			//Delete test case
			this.setUp("delete");
			SyncService.getInstance().performOneWayServerSync(service, service, false);
			this.assertRecordAbsence("unique-1-luid", "/TestOneWayServerSync/delete");
			this.assertRecordAbsence("unique-2-luid", "/TestOneWayServerSync/delete");
			this.tearDown();
			
			//Conflict test case
			this.setUp("conflict");
			SyncService.getInstance().performOneWayServerSync(service, service, false);
			afterUnique1 = this.getRecord("unique-1-luid");
			afterUnique2 = this.getRecord("unique-2-luid");
			this.assertRecordPresence("unique-1-luid", this.getInfo()+"/conflict");
			this.assertRecordPresence("unique-2-luid", this.getInfo()+"/conflict");
			this.assertRecordAbsence("unique-1", this.getInfo()+"/conflict");
			this.assertRecordAbsence("unique-2", this.getInfo()+"/conflict");
			this.assertEquals(afterUnique1.getValue("message"), 
			"<tag apos='apos' quote=\"quote\" ampersand='&'>unique-1/Updated/Server</tag>", 
			this.getInfo()+"/conflict/updated/unique-1");
			this.assertEquals(afterUnique2.getValue("message"), 
			"<tag apos='apos' quote=\"quote\" ampersand='&'>2/Message</tag>", 
			this.getInfo()+"/conflict/updated/unique-2");
			this.tearDown();
		}
		catch(Exception e)
		{
			throw new RuntimeException(e.toString());
		}
	}	
}
