/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.test.service.testdrive;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * @author openmobster@gmail.com
 *
 */
public class BusRPC extends Service
{
	private IBusRPCImpl handler;
	
	public BusRPC()
	{
		this.handler = new IBusRPCImpl();
	}
	
	@Override
	public IBinder onBind(Intent arg0)
	{
		System.out.println("Successfully bound to the busrpc service.....");
		return this.handler;
	}
}
