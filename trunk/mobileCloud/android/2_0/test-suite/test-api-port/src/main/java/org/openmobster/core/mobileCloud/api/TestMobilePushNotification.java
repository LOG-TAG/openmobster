/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.api;

import org.openmobster.core.mobileCloud.android.module.bus.Bus;
import org.openmobster.core.mobileCloud.android.module.bus.Invocation;

/**
 * @author openmobster@gmail.com
 *
 */
public final class TestMobilePushNotification extends AbstractAPITest 
{
	public void runTest()
	{
		try
		{
			//this.startBootSyncForPush();
			Invocation invocation = new Invocation(
			"org.openmobster.core.mobileCloud.android.invocation.CometRecycleHandler");
			Bus.getInstance().invokeService(invocation);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e.toString());
		}
	}
}
