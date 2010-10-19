/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.rim_native.framework.sample.commands;


import net.rim.device.api.ui.component.Status;

import org.openmobster.core.mobileCloud.rimos.module.bus.Bus;
import org.openmobster.core.mobileCloud.rimos.module.bus.MobilePushInvocation;
import org.openmobster.core.mobileCloud.rimos.module.bus.MobilePushMetaData;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.AsyncCommand;


/**
 * @author openmobster@gmail.com
 *
 */
public final class PushNotificationCommand implements AsyncCommand
{
	public void doViewBefore(CommandContext commandContext)
	{				
		Status.show("PushNotificationCommand about to execute........");		
	}

	public void doAction(CommandContext commandContext) 
	{
		try
		{						
			//Should send 3 notifications 2 from twitter, one from email, and mockService should be discarded
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
			throw new RuntimeException(e.toString());
		}
	}	
	
	public void doViewAfter(CommandContext commandContext)
	{	
	}
	
	public void doViewError(CommandContext commandContext)
	{
	}
}
