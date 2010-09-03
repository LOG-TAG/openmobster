/******************************************************************************
 * OpenMobster                                                                *
 * Copyright 2008, OpenMobster, and individual                                *
 * contributors as indicated by the @authors tag. See the                     *
 * copyright.txt in the distribution for a full listing of                    *
 * individual contributors.                                                   *
 *                                                                            *
 * This is free software; you can redistribute it and/or modify it            *
 * under the terms of the GNU Lesser General Public License as                *
 * published by the Free Software Foundation; either version 2.1 of           *
 * the License, or (at your option) any later version.                        *
 *                                                                            *
 * This software is distributed in the hope that it will be useful,           *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU           *
 * Lesser General Public License for more details.                            *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public           *
 * License along with this software; if not, write to the Free                *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA         *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.                   *
 ******************************************************************************/
package org.openmobster.core.mobileCloud.android.module.sync;


import org.openmobster.core.mobileCloud.android.module.mobileObject.MobileObject;


/**
 * 
 * @author openmobster@gmail.com
 *
 */
public final class TestSlowSync extends AbstractSyncTest 
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
			this.assertRecordPresence("unique-1", "/TestSlowSync/add");
			this.assertRecordPresence("unique-2", "/TestSlowSync/add");
			this.assertRecordPresence("unique-3", "/TestSlowSync/add");
			this.assertRecordPresence("unique-4", "/TestSlowSync/add");
			this.tearDown();
			
			//Replace test case
			this.setUp("replace");						
			SyncService.getInstance().performSlowSync(service, service, false);
			MobileObject replacedRecord = this.getRecord("unique-2");			
			//These records from the server should show up on the device
			this.assertRecordPresence("unique-1", "/TestSlowSync/replace");
			this.assertRecordPresence("unique-2", "/TestSlowSync/replace");
			this.assertEquals(replacedRecord.getValue("message"), "<tag apos='apos' quote=\"quote\" ampersand='&'>unique-2/Updated/Client</tag>", "/TestSlowSync/replace/updated");
			this.tearDown();
			
			//Delete test case
			this.setUp("delete");
			SyncService.getInstance().performSlowSync(service, service, false);							
			//These records deleted from the device should *show up* on the device
			//Should not be deleted in a SlowSync Setup
			this.assertRecordPresence("unique-1", "/TestSlowSync/delete");
			this.assertRecordPresence("unique-2", "/TestSlowSync/delete");
			this.tearDown();
			
			//Conflict test case
			this.setUp("conflict");						
			SyncService.getInstance().performSlowSync(service, service, false);
			MobileObject conflictedRecord = this.getRecord("unique-1");			
			//These records from the server should show up on the device
			this.assertRecordPresence("unique-1", "/TestSlowSync/conflict");
			this.assertRecordPresence("unique-2", "/TestSlowSync/conflict");
			this.assertEquals(conflictedRecord.getValue("message"), "<tag apos='apos' quote=\"quote\" ampersand='&'>unique-1/Updated/Client</tag>", "/TestSlowSync/conflict/unique-1");
			this.tearDown();
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			throw new RuntimeException(e.toString());
		}
	}	
}
