/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.offlineApp.rimos.app.command;

import java.lang.StringBuffer;

import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.component.Dialog;

import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.LocalCommand;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.NavigationContext;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.model.MobileBean;
import org.openmobster.core.mobileCloud.rimos.util.GenericAttributeManager;


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
			
			String details = null;
			
			//Lookup by state..in this case, that of demoString
			GenericAttributeManager criteria = new GenericAttributeManager();
			criteria.setAttribute("demoString", selectedBean);
			
			MobileBean[] beans = MobileBean.queryByEqualsAll(channel, criteria);
			MobileBean unique = beans[0];
			
			StringBuffer buffer = new StringBuffer();
			buffer.append("DemoString: "+unique.getValue("demoString"));
			details = buffer.toString();
			
			commandContext.setAttribute("details", details);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e.toString());
		}
	}	
	
	public void doViewAfter(CommandContext commandContext)
	{
		Dialog.alert((String)commandContext.getAttribute("details"));
		NavigationContext navigation = Services.getInstance().getNavigationContext();
		navigation.home();
	}
	
	public void doViewError(CommandContext commandContext)
	{
		Status.show(this.getClass().getName()+" had an error!!");
	}
}
