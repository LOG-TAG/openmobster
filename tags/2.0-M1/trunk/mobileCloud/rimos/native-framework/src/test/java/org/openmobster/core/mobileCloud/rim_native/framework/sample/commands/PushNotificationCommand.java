/**
 * Copyright (c) {2003,2009} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.rim_native.framework.sample.commands;


import net.rim.device.api.ui.component.Status;

import net.rim.device.api.notification.NotificationsManager;
import net.rim.device.api.notification.NotificationsConstants;

import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.LocalCommand;
import org.openmobster.core.mobileCloud.rimos.module.bus.Bus;
import org.openmobster.core.mobileCloud.rimos.module.bus.MobilePushInvocation;
import org.openmobster.core.mobileCloud.rimos.module.bus.MobilePushMetaData;
import org.openmobster.core.mobileCloud.rimos.util.GeneralTools;


/**
 * @author openmobster@gmail.com
 *
 */
public final class PushNotificationCommand implements LocalCommand
{
	public void doViewBefore(CommandContext commandContext)
	{
		//Event Handling registration
		/*NotificationsManager.registerSource((long)"blahblah".hashCode(), "OpenMobster Push Event", 
		NotificationsConstants.IMPORTANT);
		NotificationsManager.registerNotificationsEngineListener((long)"blahblah".hashCode(), 
		new PushNotificationListener());*/
		
		Status.show("PushNotificationCommand about to execute........");		
	}

	public void doAction(CommandContext commandContext) 
	{
		try
		{
			/*System.out.println("-------------------------------------------------------");
			System.out.println("PushNotification Command successfully executed.........");
			System.out.println("-------------------------------------------------------");
			
			//Set the deferred event
			long sourceId = (long)"blahblah".hashCode();
			long eventId = GeneralTools.generateUniqueId();
			long eventDate = -1;
			int triggerIndex = NotificationsConstants.MANUAL_TRIGGER;
			long sharedObjectId = GeneralTools.generateUniqueId();
			NotificationsManager.negotiateDeferredEvent(sourceId, eventId, new Long(sharedObjectId), 
			eventDate, triggerIndex, null);*/
			
			//Start some background thread and see if it keeps running even after app is exited
			Thread t = new Thread(new Runnable(){
				public void run()
				{
					/*try
					{
						do
						{
							MobileBeanMetaData[] pushData = new MobileBeanMetaData[5];
							for(int i=0; i<pushData.length; i++)
							{
								MobileBeanMetaData cour = new MobileBeanMetaData("mockService", ""+i);
								cour.setDeleted(true);
								pushData[i] = cour;
							}
							
							MobilePush push = new MobilePush(pushData);
							Services.getInstance().getPushListener().receivePush(push);
							
							System.out.println("---------------------------------------");
							System.out.println("Push Notification sent.................");
							System.out.println("---------------------------------------");
							
							Thread.currentThread().sleep(20000);
						}while(true);
					}
					catch(Exception e)
					{
						System.out.println("---Background thread blew up------------------");
						System.out.println("Exception: "+e.getMessage());
						System.out.println("----------------------------------------------");
					}*/
					
					//Should send 3 notifications 2 from twitter, one from email, and mockService should be discarded
					try
					{
						Thread.currentThread().sleep(20000);
						MobilePushInvocation invocation = new MobilePushInvocation("MobilePushInvocation");
						for(int i=0; i<5; i++)
						{
							MobilePushMetaData cour = null;
							
							if(i < 2)
							{
								cour = new MobilePushMetaData("mockService", ""+i);
							}
							else if(i < 3)
							{
								cour = new MobilePushMetaData("emailChannel", ""+i);
							}
							else
							{
								cour = new MobilePushMetaData("twitterChannel", ""+i);
							}
							
							
							cour.setDeleted(true);
							invocation.addMobilePushMetaData(cour);
						}
						
						
						Bus.getInstance().broadcast(invocation);
						
						System.out.println("---------------------------------------");
						System.out.println("Push Notification sent.................");
						System.out.println("---------------------------------------");		
					}
					catch(Exception e)
					{
						System.out.println("---Background thread blew up------------------");
						System.out.println("Exception: "+e.getMessage());
						System.out.println("----------------------------------------------");
					}
				}
			});
			t.start();
		}
		catch(Exception e)
		{
			throw new RuntimeException(e.toString());
		}
	}	
	
	public void doViewAfter(CommandContext commandContext)
	{	
		//Immediate Event
		/*NotificationsManager.triggerImmediateEvent((long)this.getClass().getName().hashCode(), 
		0, 
		this, 
		null);
		HomeScreen.setName("(notification..)");
		Status.show("Notification was sent!!!");*/
	}
	
	public void doViewError(CommandContext commandContext)
	{
	}
}
