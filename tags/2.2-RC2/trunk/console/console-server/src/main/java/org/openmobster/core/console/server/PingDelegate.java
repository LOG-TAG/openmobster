/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.console.server;

import java.util.List;

import org.openmobster.core.common.transaction.TransactionHelper;
import org.openmobster.core.console.server.admin.AdminAccountException;
import org.openmobster.core.security.device.DeviceController;
import org.openmobster.core.security.device.Device;

/**
 * 
 * @author openmobster@gmail.com
 */
public class PingDelegate 
{
	public String invoke(String input)
	{
		boolean startedHere = TransactionHelper.startTx();
		try
		{
			DeviceController deviceController = DeviceController.getInstance();
			List<Device> devices = deviceController.readAll();
			
			System.out.println("Listing Devices.........");
			
			if(devices != null)
			{
				for(Device device:devices)
				{
					System.out.println("Device Id: "+device.getIdentifier());
				}
			}
			
			if(startedHere)
			{
				TransactionHelper.commitTx();
			}
			
			return "ping://updated/devices"+input;
		}
		catch(Exception e)
		{
			if(startedHere)
			{
				TransactionHelper.rollbackTx();
			}
			throw new RuntimeException(e);
		}
	}
}
