/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.test.sync;

import org.openmobster.core.mobileCloud.api.model.MobileBeanMetaData;
import org.openmobster.core.mobileCloud.api.push.MobilePush;
import org.openmobster.core.mobileCloud.api.push.PushListener;

/**
 * @author openmobster@gmail.com
 *
 */
public class EmailPushListener implements PushListener
{
	static boolean pushReceived;
	
	public void receivePush(MobilePush push) 
	{
		MobileBeanMetaData[] pushBeans = push.getPushData();
		
		if(pushBeans != null)
		{
			System.out.println("Received Bean Push Notification---------------------------------------");
			for(int i=0,size=pushBeans.length; i<size; i++)
			{
				System.out.println("Service="+pushBeans[i].getService());
				System.out.println("Id="+pushBeans[i].getId());
				System.out.println("Deleted="+pushBeans[i].isDeleted());
				System.out.println("--------------------------------------------------------------------");
			}
		}
		
		pushReceived = true;
	}
	
	public void clearNotification()
	{
		
	}
}
