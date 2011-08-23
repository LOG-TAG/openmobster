/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.console.server.device;

import java.util.List;
import java.util.ArrayList;

import org.openmobster.core.common.transaction.TransactionHelper;
import org.openmobster.core.console.server.Server;
import org.openmobster.core.security.Provisioner;
import org.openmobster.core.security.device.DeviceController;
import org.openmobster.core.security.device.Device;
import org.openmobster.core.security.device.DeviceAttribute;
import org.openmobster.core.security.identity.Identity;


/**
 * 
 * @author openmobster@gmail.com
 */
public final class ManageDevice 
{
	private static ManageDevice singleton;
	
	private ManageDevice()
	{
		
	}
	
	public static ManageDevice getInstance()
	{
		if(singleton == null)
		{
			synchronized(ManageDevice.class)
			{
				if(singleton == null)
				{
					singleton = new ManageDevice();
					Server.getInstance().start();
				}
			}
		}
		return singleton;
	}
	
	public List<DeviceUI> getRegisteredDevices() throws ManageDeviceException
	{
		boolean startedHere = TransactionHelper.startTx();
		try
		{
			List<DeviceUI> registeredDevices = new ArrayList<DeviceUI>();
			DeviceController deviceController = DeviceController.getInstance();
			
			List<Device> devices = deviceController.readAll();
			if(devices != null && !devices.isEmpty())
			{
				for(Device local:devices)
				{
					DeviceUI deviceUI = new DeviceUI();
					Identity user = local.getIdentity();
					
					deviceUI.setDeviceIdentifier(local.getIdentifier());
					deviceUI.setActive(user.isActive());
					deviceUI.setAccount(user.getPrincipal());
					
					DeviceAttribute osAttr = local.readAttribute("os");
					DeviceAttribute versionAttr = local.readAttribute("version");
					
					if(osAttr != null && versionAttr != null)
					{
						String os = osAttr.getValue();
						String version = versionAttr.getValue();
						
						StringBuilder buffer = new StringBuilder();
						
						buffer.append(os.substring(0,1).toUpperCase());
						buffer.append(os.substring(1));
						buffer.append(" "+version);
						deviceUI.setOs(buffer.toString());
					}
					else
					{
						deviceUI.setOs("Unknown");
					}
					
					registeredDevices.add(deviceUI);
				}
			}
			
			if(startedHere)
			{
				TransactionHelper.commitTx();
			}
			
			return registeredDevices;
		}
		catch(Exception e)
		{
			if(startedHere)
			{
				TransactionHelper.rollbackTx();
			}
			throw new ManageDeviceException(e);
		}
	}
	
	public void activate(String deviceId) throws ManageDeviceException
	{
		boolean startedHere = TransactionHelper.startTx();
		try
		{
			DeviceController deviceController = DeviceController.getInstance();
			Provisioner provisioner = Provisioner.getInstance();
			Device device = deviceController.read(deviceId);
			
			if(device != null)
			{
				String deviceOwner = device.getIdentity().getPrincipal();
				provisioner.activate(deviceOwner);
			}
			
			if(startedHere)
			{
				TransactionHelper.commitTx();
			}
		}
		catch(Exception e)
		{
			if(startedHere)
			{
				TransactionHelper.rollbackTx();
			}
			throw new ManageDeviceException(e);
		}
	}
	
	public void deactivate(String deviceId) throws ManageDeviceException
	{
		boolean startedHere = TransactionHelper.startTx();
		try
		{
			DeviceController deviceController = DeviceController.getInstance();
			Provisioner provisioner = Provisioner.getInstance();
			Device device = deviceController.read(deviceId);
			
			if(device != null)
			{
				String deviceOwner = device.getIdentity().getPrincipal();
				provisioner.deactivate(deviceOwner);
			}
			
			if(startedHere)
			{
				TransactionHelper.commitTx();
			}
		}
		catch(Exception e)
		{
			if(startedHere)
			{
				TransactionHelper.rollbackTx();
			}
			throw new ManageDeviceException(e);
		}
	}
	
	public void reassign(String deviceId) throws ManageDeviceException
	{
		boolean startedHere = TransactionHelper.startTx();
		try
		{
			DeviceController deviceController = DeviceController.getInstance();
			Device device = deviceController.read(deviceId);
			if(device != null)
			{
				deviceController.delete(device);
			}
			
			if(startedHere)
			{
				TransactionHelper.commitTx();
			}
		}
		catch(Exception e)
		{
			if(startedHere)
			{
				TransactionHelper.rollbackTx();
			}
			throw new ManageDeviceException(e);
		}
	}
}
