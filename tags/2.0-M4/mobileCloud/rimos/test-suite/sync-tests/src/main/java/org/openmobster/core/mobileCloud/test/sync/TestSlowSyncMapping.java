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
public final class TestSlowSyncMapping extends AbstractMapSyncTest
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
			SyncService.getInstance().performSlowSync(service, service, false);			
			//These records from the server should show up on the device
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
			SyncService.getInstance().performSlowSync(service, service, false);
			MobileObject replacedRecord = this.getRecord("unique-2-luid");			
			//These records from the server should show up on the device
			this.assertRecordPresence("unique-1-luid", this.getInfo()+"/replace");
			this.assertRecordPresence("unique-2-luid", this.getInfo()+"/replace");
			this.assertRecordAbsence("unique-1", this.getInfo()+"/replace");
			this.assertRecordAbsence("unique-2", this.getInfo()+"/replace");
			this.assertEquals(replacedRecord.getRecordId(), "unique-2-luid", "/TestSlowSync/replace/updated");
			this.assertEquals(replacedRecord.getValue("message"), 
			"<tag apos='apos' quote=\"quote\" ampersand='&'>unique-2/Updated/Client</tag>", "/TestSlowSync/replace/updated");
			this.tearDown();
			
			//Delete test case
			this.setUp("delete");
			SyncService.getInstance().performSlowSync(service, service, false);							
			//These records deleted from the device should *show up* on the device
			//Should not be deleted in a SlowSync Setup
			this.assertRecordPresence("unique-1-luid", this.getInfo()+"/delete");
			this.assertRecordPresence("unique-2-luid", this.getInfo()+"/delete");
			this.assertRecordAbsence("unique-1", this.getInfo()+"/delete");
			this.assertRecordAbsence("unique-2", this.getInfo()+"/delete");
			this.tearDown();
			
			//Conflict test case
			this.setUp("conflict");						
			SyncService.getInstance().performSlowSync(service, service, false);
			MobileObject conflictedRecord = this.getRecord("unique-1-luid");			
			//These records from the server should show up on the device
			this.assertRecordPresence("unique-1-luid", this.getInfo()+"/conflict");
			this.assertRecordPresence("unique-2-luid", this.getInfo()+"/conflict");
			this.assertRecordAbsence("unique-1", this.getInfo()+"/conflict");
			this.assertRecordAbsence("unique-2", this.getInfo()+"/conflict");
			this.assertEquals(conflictedRecord.getValue("message"), 
			"<tag apos='apos' quote=\"quote\" ampersand='&'>unique-1/Updated/Client</tag>", "/TestSlowSync/conflict/unique-1");
			this.tearDown();
		}
		catch(Exception e)
		{
			throw new RuntimeException(e.toString());
		}
	}	
}
