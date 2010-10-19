/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

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
			this.waitForEmail();
			
			//Reseting the listener state
			EmailPushListener.pushReceived = false;
			
			//Assert
			MobileObject newEmail = MobileObjectDatabase.getInstance().read(this.service, newMailUid);
			this.assertNotNull(newEmail, this.getInfo()+"/NewEmailShouldNotBeNull");
			if(newEmail != null)
			{
				this.assertEquals(newEmail.getValue("from"), "from@blah.com", this.getInfo()+"/NewEmail/FromCheck");
				this.assertEquals(newEmail.getValue("to"), "to@blah.com", this.getInfo()+"/NewEmail/ToCheck");
				this.assertEquals(newEmail.getValue("subject"), "newSubject", this.getInfo()+"/NewEmail/SubjectCheck");
				this.assertEquals(newEmail.getValue("message"), "new message", this.getInfo()+"/NewEmail/MessageCheck");
				
				System.out.println("New Email received-----------------------------------");
				System.out.println("From="+newEmail.getValue("from"));
				System.out.println("To="+newEmail.getValue("to"));
				System.out.println("Subject="+newEmail.getValue("subject"));
				System.out.println("Message="+newEmail.getValue("message"));
			}
			
			this.tearDown();			
		}
		catch(Exception e)
		{
			throw new RuntimeException(e.toString());
		}
	}
	
	private void waitForEmail()
	{
		try
		{
			int retries = 90;
			do
			{
				if(EmailPushListener.pushReceived)
				{
					break;
				}
				Thread.currentThread().sleep(1000);
			}while(retries-- > 0);
		}
		catch(Exception e)
		{
			//nothing to do
		}
	}
}
