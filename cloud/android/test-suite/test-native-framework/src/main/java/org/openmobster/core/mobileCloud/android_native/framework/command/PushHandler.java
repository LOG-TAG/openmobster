/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android_native.framework.command;

import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.api.sync.MobileBeanMetaData;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.PushCommand;
import org.openmobster.core.mobileCloud.api.push.MobilePush;

import android.content.Context;
import android.widget.Toast;

/**
 * @author openmobster@gmail.com
 *
 */
public final class PushHandler implements PushCommand
{
	public void doViewBefore(CommandContext commandContext)
	{		
	}

	public void doAction(CommandContext commandContext) 
	{
		try
		{
			MobilePush push = commandContext.getPush();
			System.out.println("Handling Push----------------------------------------");
			System.out.println("Push Updates: "+push.getNumberOfUpdates());
			MobileBeanMetaData[] updates = push.getPushData();
			if(updates != null)
			{
				for(MobileBeanMetaData update:updates)
				{
					System.out.println("Bean: "+update.getId());
				}
			}
			System.out.println("----------------------------------------");
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			throw new RuntimeException(e.toString());
		}
	}	
	
	public void doViewAfter(CommandContext commandContext)
	{
		MobilePush push = commandContext.getPush();
		Context context = Registry.getActiveInstance().getContext();
		Toast.makeText(context, push.getNumberOfUpdates()+" Updates successfully received!!", 
		Toast.LENGTH_SHORT).show();
	}
	
	public void doViewError(CommandContext commandContext)
	{
		Context context = Registry.getActiveInstance().getContext();
		Toast.makeText(context, this.getClass().getName()+" had an error!!", 
		Toast.LENGTH_SHORT).show();
	}
}
