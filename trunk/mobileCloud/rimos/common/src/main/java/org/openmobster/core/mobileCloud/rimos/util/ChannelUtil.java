/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.rimos.util;

import org.openmobster.core.mobileCloud.rimos.storage.Database;

/**
 * @author openmobster@gmail
 *
 */
public final class ChannelUtil 
{
	public static boolean isChannelActive(String channel) throws Exception
	{
		if(Database.getInstance().doesTableExist(channel) && !Database.getInstance().isTableEmpty(channel))
		{
			return true;
		}
		return false;
	}
}
