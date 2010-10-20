/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.offlineApp.rimos.app.command;


import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.PushCommand;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.NavigationContext;


/**
 * @author openmobster@gmail.com
 *
 */
public final class PushHandler implements PushCommand
{
	public void doAction(CommandContext commandContext)
	{		
	}

	public void doViewAfter(CommandContext commandContext)
	{
		NavigationContext.getInstance().refresh();
	}
	
	public void doViewError(CommandContext commandContext)
	{
	}
}
