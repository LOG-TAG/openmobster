/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.android_native.framework;

import org.openmobster.core.mobileCloud.api.ui.framework.command.LocalCommand;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;

/**
 * @author openmobster@gmail.com
 *
 */
public class ShowErrorCommand implements LocalCommand
{
	public void doViewBefore(CommandContext commandContext)
	{	
	}

	public void doAction(CommandContext commandContext)
	{				
		throw new RuntimeException("blahblah!!");
	}

	public void doViewAfter(CommandContext commandContext)
	{	
	}

	public void doViewError(CommandContext commandContext)
	{	
	}
}
