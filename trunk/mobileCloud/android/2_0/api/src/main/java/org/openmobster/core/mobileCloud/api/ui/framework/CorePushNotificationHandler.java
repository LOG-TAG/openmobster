/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.api.ui.framework;

//FIXME: show a generic system notification
/*import net.rim.blackberry.api.homescreen.HomeScreen;
import net.rim.device.api.notification.NotificationsConstants;
import net.rim.device.api.notification.NotificationsManager;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.UiApplication;*/

import org.openmobster.core.mobileCloud.api.push.MobilePush;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.util.GeneralTools;

/**
 * @author openmobster@gmail
 *
 */
public final class CorePushNotificationHandler implements PushNotificationHandler
{
	public CorePushNotificationHandler()
	{
	}
	
	public void start()
	{
	}
	
	public void receiveNotification(final MobilePush newPushInstance)
	{					
		//Send a notification at the App-Level via a PushCommand invocation
		/*if(Application.getApplication().isForeground())
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
		}*/
		
		System.out.println("App Level Push--------------------------------------");
		System.out.println("Push received.........................");
		System.out.println("----------------------------------------------------");
	}
	
	public void clearNotification()
	{	
	}
}
