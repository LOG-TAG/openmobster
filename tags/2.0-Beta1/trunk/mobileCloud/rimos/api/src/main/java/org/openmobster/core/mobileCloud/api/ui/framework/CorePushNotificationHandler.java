/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.api.ui.framework;

import java.util.Vector;

import net.rim.blackberry.api.homescreen.HomeScreen;
import net.rim.device.api.notification.NotificationsConstants;
import net.rim.device.api.notification.NotificationsManager;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.UiApplication;

import org.openmobster.core.mobileCloud.api.push.MobilePush;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.rimos.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.rimos.errors.SystemException;
import org.openmobster.core.mobileCloud.rimos.util.GeneralTools;

/**
 * @author openmobster@gmail
 *
 */
public final class CorePushNotificationHandler implements PushNotificationHandler
{
	private UiApplication app;
	private long notificationSource;
	private Vector eventIds;
	
	public CorePushNotificationHandler()
	{
	}
	
	public void start()
	{
		if(this.app == null)
		{
			this.app = UiApplication.getUiApplication();
			this.notificationSource = GeneralTools.generateUniqueId();
			NotificationsManager.registerSource(
			this.notificationSource, //sourceId 
			"openmobster", //Some Object
			NotificationsConstants.IMPORTANT);
			this.eventIds = new Vector();
		}
	}
	
	public void receiveNotification(final MobilePush newPushInstance)
	{	
		app.invokeLater(new Runnable(){
			public void run()
			{
				try
				{
					ApplicationDescriptor descriptor = ApplicationDescriptor.currentApplicationDescriptor();
					HomeScreen.setName("("+newPushInstance.getNumberOfUpdates()+")"+descriptor.getName());
					
					EncodedImage pushIcon = null;
					
					//First find an overriden icon
					try
					{
						pushIcon = (EncodedImage)Services.getInstance().getResources().getImage("/moblet-app/icon/push.png");
						if(pushIcon == null)
						{
							pushIcon = (EncodedImage)Services.getInstance().getResources().getImage("/system/images/push.png");
						}
					}
					catch(Exception e)
					{
						pushIcon = (EncodedImage)Services.getInstance().getResources().getImage("/system/images/push.png");
					}
					
					HomeScreen.updateIcon(pushIcon.getBitmap());
					
					//Send the Notification
					Long eventId = new Long(GeneralTools.generateUniqueId());
					CorePushNotificationHandler.this.eventIds.addElement(eventId);
					NotificationsManager.triggerImmediateEvent(
					CorePushNotificationHandler.this.notificationSource, //sourceId 
					eventId.longValue(), //EventId
							null, //some Object
							null //some ContextObject
					);
					
					//Some sample code to be really pushy...This one pops up a modal dialog on the Global screen
					//This can get very annoying for users...so reserve this to later use in the framework when
					//Push Engine gets more contextual/environmental smarts...
					/*UiEngine ui = Ui.getUiEngine();
					Screen screen = new Dialog(Dialog.D_OK, "Look out!!!",Dialog.OK,Bitmap.getPredefinedBitmap(Bitmap.EXCLAMATION),
					Manager.VERTICAL_SCROLL);
					ui.pushGlobalScreen(screen, 1, UiEngine.GLOBAL_QUEUE);*/										
				}
				catch(Exception e)
				{
					//record any issues here
					SystemException syse = new SystemException(this.getClass().getName(), "receiveNotification", new Object[]{
						"Class: "+CorePushNotificationHandler.class.getName(),
						"Exception: "+e.toString(),
						"Message: "+e.getMessage()
					});
					ErrorHandler.getInstance().handle(syse);
				}
			}
		});
		
		//Send a notification at the App-Level via a PushCommand invocation
		if(Application.getApplication().isForeground())
		{
			CommandContext commandContext = new CommandContext();
			commandContext.setTarget("push");
			commandContext.setPush(newPushInstance);
			
			int retry = 5;
			for(int i=0; i<retry; i++)
			{				
				Services.getInstance().getCommandService().execute(commandContext);
				
				if(commandContext.getAttribute("validation-error") == null)
				{
					//everything went ok.
					break;
				}
				
				//retry in 30 seconds
				try{Thread.currentThread().sleep(30000);}catch(Exception e){}
				
				if(!Application.getApplication().isForeground())
				{
					break;
				}
			}
		}
	}
	
	public void clearNotification()
	{	
		ApplicationDescriptor descriptor = ApplicationDescriptor.currentApplicationDescriptor();
		HomeScreen.setName(descriptor.getName());
		HomeScreen.updateIcon(descriptor.getIcon());
		
		//Use a background thread to cleanup all system notifications
		Thread t = new Thread(new Runnable(){
			public void run()
			{
				try
				{
					Vector eventIds = CorePushNotificationHandler.this.eventIds;
					if(eventIds != null)
					{
						int size = eventIds.size();
						for(int i=0; i<size; i++)
						{
							Long eventId = (Long)eventIds.elementAt(i);
							NotificationsManager.cancelImmediateEvent(
									CorePushNotificationHandler.this.notificationSource, 
							eventId.longValue(),
							null, 
							null);
						}
						CorePushNotificationHandler.this.eventIds.removeAllElements();
					}
				}
				catch(Exception e)
				{
					//tried to cleanup...but make sure if something goes wrong
					//trying to cleanup, it does not hinder the App.
					//a phone soft reset should cleanut the system notifications anyways
				}
			}
		});
		t.start();
	}
}
