/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.test.cloud;

import test.openmobster.core.mobileCloud.rimos.testsuite.AbstractAPITest;

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
			this.resetServerAdapter("setUp=TestOneWayServerSync/replace");
			this.startOneWayServerSync();
		}
		catch(Exception e)
		{
			throw new RuntimeException(e.toString());
		}
	}
}
