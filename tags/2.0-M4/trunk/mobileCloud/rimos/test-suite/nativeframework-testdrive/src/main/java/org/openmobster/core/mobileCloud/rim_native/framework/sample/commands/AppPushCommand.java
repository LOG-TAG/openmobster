/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.rim_native.framework.sample.commands;


import net.rim.device.api.ui.component.Dialog;

import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.PushCommand;


/**
 * @author openmobster@gmail.com
 *
 */
public final class AppPushCommand implements PushCommand
{
	public void doAction(CommandContext commandContext)
	{		
	}

	public void doViewAfter(CommandContext commandContext)
	{
		Dialog.alert("App Push received!!!");		
	}
	
	public void doViewError(CommandContext commandContext)
	{
	}
}
