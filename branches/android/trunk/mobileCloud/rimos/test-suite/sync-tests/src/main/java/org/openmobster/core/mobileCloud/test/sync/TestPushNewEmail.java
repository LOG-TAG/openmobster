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
package org.openmobster.core.mobileCloud.test.sync;

import org.openmobster.core.mobileCloud.rimos.module.mobileObject.MobileObject;
import org.openmobster.core.mobileCloud.rimos.module.mobileObject.MobileObjectDatabase;
import org.openmobster.core.mobileCloud.rimos.module.sync.*;

/**
 * 
 * @author openmobster@gmail.com
 *
 */
public final class TestPushNewEmail extends AbstractNotification
{		
	/**
	 * 
	 */
	public void runTest()
	{
		try
		{
			this.setUp("pushNewEmail");
			
			//Perform a SlowSync to make sure device side has same records as server side			
			SyncService.getInstance().performSlowSync(service, service, false);
			
			//Add a new email as if an new email was received from the internet
			//Out of Band addition
			String newMailUid = "testUid";
			this.setUpServerSideSync("sendNewEmail/"+newMailUid);
			
			//Block here since a Notification and its Corresponding Sync will occur on a separate background thread(s)
			while(!EmailPushListener.pushReceived)
			{
				Thread.currentThread().sleep(5000);				
			}
			
			//Reseting the listener state
			EmailPushListener.pushReceived = false;
			
			//Assert
			MobileObject newEmail = MobileObjectDatabase.getInstance().read(this.service, newMailUid);
			this.assertNotNull(newEmail, this.getInfo()+"/NewEmailShouldNotBeNull");
			this.assertEquals(newEmail.getValue("from"), "from@blah.com", this.getInfo()+"/NewEmail/FromCheck");
			this.assertEquals(newEmail.getValue("to"), "to@blah.com", this.getInfo()+"/NewEmail/ToCheck");
			this.assertEquals(newEmail.getValue("subject"), "newSubject", this.getInfo()+"/NewEmail/SubjectCheck");
			this.assertEquals(newEmail.getValue("message"), "new message", this.getInfo()+"/NewEmail/MessageCheck");
			
			System.out.println("New Email received-----------------------------------");
			System.out.println("From="+newEmail.getValue("from"));
			System.out.println("To="+newEmail.getValue("to"));
			System.out.println("Subject="+newEmail.getValue("subject"));
			System.out.println("Message="+newEmail.getValue("message"));
			
			this.tearDown();			
		}
		catch(Exception e)
		{
			throw new RuntimeException(e.toString());
		}
	}	
}
