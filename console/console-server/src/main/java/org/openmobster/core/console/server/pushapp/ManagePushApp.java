/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.console.server.pushapp;

import java.util.List;
import java.util.ArrayList;

import org.openmobster.core.common.transaction.TransactionHelper;
import org.openmobster.core.console.server.Server;
import org.openmobster.core.security.device.PushAppController;
import org.openmobster.core.security.device.PushApp;
import org.openmobster.core.security.device.DeviceController;
import org.openmobster.core.security.device.Device;
import org.openmobster.cloud.api.push.PushService;

/**
 * 
 * @author openmobster@gmail.com
 */
public final class ManagePushApp 
{
	private static ManagePushApp singleton;
	
	private ManagePushApp()
	{
		
	}
	
	public static ManagePushApp getInstance()
	{
		if(singleton == null)
		{
			synchronized(ManagePushApp.class)
			{
				if(singleton == null)
				{
					singleton = new ManagePushApp();
					Server.getInstance().start();
					
					//FIXME: Development time code only...comment me out when shipping
					//this is because PushApp instaces can only be created via
					//iphone registration
					boolean startedHere = TransactionHelper.startTx();
					try
					{
						PushAppController controller = PushAppController.getInstance();
						List<PushApp> pushApps = controller.readAll();
						if(pushApps == null || pushApps.isEmpty())
						{
							for(int i=0; i<5; i++)
							{
								String appId = "blah"+i;
								PushApp local = new PushApp();
								local.setAppId(appId);
								controller.create(local);
							}
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
						e.printStackTrace();
					}
				}
			}
		}
		return singleton;
	}
	
	public List<PushAppUI> readAll() throws ManagePushAppException
	{
		boolean startedHere = TransactionHelper.startTx();
		try
		{
			PushAppController controller = PushAppController.getInstance();
			List<PushApp> pushApps = controller.readAll();
			List<PushAppUI> ui = new ArrayList<PushAppUI>();
			
			if(pushApps != null && !pushApps.isEmpty())
			{
				for(PushApp local:pushApps)
				{
					PushAppUI pushApp = new PushAppUI();
					pushApp.setAppId(local.getAppId());
					if(local.getCertificate() != null)
					{
						pushApp.setActive(true);
					}
					else
					{
						pushApp.setActive(false);
					}
					ui.add(pushApp);
				}
			}
			
			if(startedHere)
			{
				TransactionHelper.commitTx();
			}
			
			return ui;
		}
		catch(Exception e)
		{
			if(startedHere)
			{
				TransactionHelper.rollbackTx();
			}
			throw new ManagePushAppException(e);
		}
	}
	
	public void uploadCertificate(String appId, String certificateName, byte[] certificate, String password) 
		throws ManagePushAppException
	{
		boolean startedHere = TransactionHelper.startTx();
		try
		{
			PushAppController controller = PushAppController.getInstance();
			
			PushApp pushApp = controller.readPushApp(appId);
			if(pushApp != null)
			{
				if(certificate != null && password != null)
				{
					pushApp.setCertificate(certificate);
					pushApp.setCertificatePassword(password);
					pushApp.setCertificateName(certificateName);
					controller.update(pushApp);
				}
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
			throw new ManagePushAppException(e);
		}
	}
	
	public Certificate downloadCertificate(String appId) throws ManagePushAppException
	{
		boolean startedHere = TransactionHelper.startTx();
		try
		{
			PushAppController controller = PushAppController.getInstance();
			PushApp pushApp = controller.readPushApp(appId);
			
			if(pushApp != null)
			{
				Certificate cert = new Certificate();
				cert.setCertificate(pushApp.getCertificate());
				cert.setFileName(pushApp.getCertificateName());
				
				return cert;
			}
			
			if(startedHere)
			{
				TransactionHelper.commitTx();
			}
			
			return null;
		}
		catch(Exception e)
		{
			if(startedHere)
			{
				TransactionHelper.rollbackTx();
			}
			throw new ManagePushAppException(e);
		}
	}
	
	public void testPush(String deviceId, String appId) throws ManagePushAppException
	{
		boolean startedHere = TransactionHelper.startTx();
		try
		{
			//TODO: implement me
			Device device = DeviceController.getInstance().read(deviceId);
			/*System.out.println("Test Push---------------------------");
			System.out.println("DeviceId: "+deviceId);
			System.out.println("AppID: "+appId);
			System.out.println("Device Token: "+device.getDeviceToken());
			System.out.println("Operating System: "+device.getOs());*/
			
			if(device != null)
			{
				PushService pushService = PushService.getInstance();
				pushService.push(device.getIdentity().getPrincipal(), appId, "Test Push", "Test Push", "Test Push");
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
			throw new ManagePushAppException(e);
		}
	}
	
	public void stopPush(String appId) 
	throws ManagePushAppException
	{	
		boolean startedHere = TransactionHelper.startTx();
		try
		{
			PushAppController controller = PushAppController.getInstance();
			
			PushApp pushApp = controller.readPushApp(appId);
			if(pushApp != null)
			{
				pushApp.setCertificate(null);
				pushApp.setCertificatePassword(null);
				pushApp.setCertificateName(null);
				controller.update(pushApp);
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
			throw new ManagePushAppException(e);
		}
	}
}
