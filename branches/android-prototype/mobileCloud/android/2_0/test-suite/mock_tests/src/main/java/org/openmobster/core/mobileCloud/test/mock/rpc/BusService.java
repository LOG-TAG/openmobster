/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.test.mock.rpc;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * @author openmobster@gmail.com
 *
 */
public class BusService extends Service
{
	private IBusHandlerImpl handler;
	
	public BusService()
	{
		this.handler = new IBusHandlerImpl();
	}
	
	@Override
	public IBinder onBind(Intent arg0)
	{
		System.out.println("Successfully bound to the bus service.....");
		return this.handler;
	}
}
