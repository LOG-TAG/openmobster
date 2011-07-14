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
import org.openmobster.server.api.push.PushService;

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
					/*TransactionHelper.startTx();
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
					}
					finally
					{
						TransactionHelper.commitTx();
					}*/
				}
			}
		}
		return singleton;
	}
	
	public List<PushAppUI> readAll() throws ManagePushAppException
	{
		TransactionHelper.startTx();
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
			
			return ui;
		}
		finally
		{
			TransactionHelper.commitTx();
		}
	}
	
	public void uploadCertificate(String appId, String certificateName, byte[] certificate, String password) 
		throws ManagePushAppException
	{
		TransactionHelper.startTx();
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
		}
		finally
		{
			TransactionHelper.commitTx();
		}
	}
	
	public Certificate downloadCertificate(String appId) throws ManagePushAppException
	{
		TransactionHelper.startTx();
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
			
			return null;
		}
		finally
		{
			TransactionHelper.commitTx();
		}
	}
	
	public void testPush(String deviceId, String appId) throws ManagePushAppException
	{
		TransactionHelper.startTx();
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
		}
		finally
		{
			TransactionHelper.commitTx();
		}
	}
}
