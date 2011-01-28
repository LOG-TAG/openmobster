/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.offlineApp.android.app.command;

import java.lang.StringBuffer;

import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.LocalCommand;
import org.openmobster.core.mobileCloud.api.model.MobileBean;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android.util.GenericAttributeManager;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;

import android.app.Activity;


/**
 * @author openmobster@gmail.com
 *
 */
public final class DemoDetails implements LocalCommand
{
	public void doViewBefore(CommandContext commandContext)
	{		
	}

	public void doAction(CommandContext commandContext) 
	{
		try
		{
			String channel = "offlineapp_demochannel";
			String selectedBean = (String)commandContext.getAttribute("selectedBean");
			
			//System.out.println("---------------------------------------");
			//System.out.println("Bean: "+selectedBean);
			//System.out.println("---------------------------------------");
			
			String details = null;
			
			//Lookup by state..in this case, that of demoString
			GenericAttributeManager criteria = new GenericAttributeManager();
			criteria.setAttribute("demoString", selectedBean);
			
			MobileBean[] beans = MobileBean.queryByEqualsAll(channel, criteria);
			MobileBean unique = beans[0];
			
			//MobileBean unique = MobileBean.readById(channel, selectedBean);
			
			StringBuffer buffer = new StringBuffer();
			buffer.append("DemoString: "+unique.getValue("demoString"));
			details = buffer.toString();
			
			commandContext.setAttribute("details", details);
		}
		catch(Exception e)
		{
			//e.printStackTrace(System.out);
			throw new RuntimeException(e.toString());
		}
	}	
	
	public void doViewAfter(CommandContext commandContext)
	{
		Activity currentActivity = (Activity)Registry.getActiveInstance().getContext();
		ViewHelper.getOkModal(currentActivity, "Details", 
		(String)commandContext.getAttribute("details")).
		show();
	}
	
	public void doViewError(CommandContext commandContext)
	{
		Activity currentActivity = (Activity)Registry.getActiveInstance().getContext();
		ViewHelper.getOkModal(currentActivity, "App Error", 
		this.getClass().getName()+" had an error!!").
		show();
	}
}
