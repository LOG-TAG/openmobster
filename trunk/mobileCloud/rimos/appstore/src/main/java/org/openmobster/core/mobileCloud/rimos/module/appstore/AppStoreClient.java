/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.rimos.module.appstore;

import net.rim.device.api.system.CodeModuleManager;

import org.openmobster.core.mobileCloud.rimos.service.Registry;
import org.openmobster.core.mobileCloud.rimos.service.Service;
import org.openmobster.core.mobileCloud.rimos.util.IOUtil;


/**
 * There are some serious issues with using Code Management APIs
 * 
 * For now installing Apps is done via downloading them from the server
 * via the built-in browser
 * 
 * @author openmobster@gmail
 *
 */
public final class AppStoreClient extends Service
{
	public AppStoreClient()
	{
		
	}
	
	public void start()
	{
		
	}
	
	public void stop()
	{
		
	}
	
	public static AppStoreClient getInstance()
	{
		return (AppStoreClient)Registry.getInstance().lookup(AppStoreClient.class);
	}
	//----------------------------------------------------------------------------------------------------------------------------
	public void installApp(String appUri)
	{
		try
		{
			System.out.println("Installing App...............................");
			System.out.println("App Uri="+appUri);
			System.out.println("---------------------------------------------");
			
			/*Request request = new Request("moblet-management://appStore");
			request.setAttribute("uri", appUri);
			request.setAttribute("action", "getApp");
			
			Response response = MobileService.invoke(request);
			String bin = response.getAttribute("bin");
			
			System.out.println(bin);
			
			byte[] codBytes = GeneralTools.decodeBinaryData(bin);*/
			
			byte[] openmobster = IOUtil.read(AppStoreClient.class.getResourceAsStream("/openmobster.cod"));
			byte[] openmobster_1 = IOUtil.read(AppStoreClient.class.getResourceAsStream("/openmobster-1.cod"));
			
			System.out.println("OpenMobster Length: "+openmobster.length);
			System.out.println("OpenMobster-1 Length: "+openmobster_1.length);
			
			//Install this as a module
			System.out.println("1---------------------------");
			int moduleHandle = CodeModuleManager.createNewModule(openmobster.length, openmobster, 40000);
			System.out.println("2---------------------------");
			System.out.println("ModuleHandle: "+moduleHandle);
			boolean success = CodeModuleManager.writeNewModule(moduleHandle, 40000, openmobster, 40000, (openmobster.length)-40000);
			System.out.println("3---------------------------");
			System.out.println("Module Success: "+success);			
			//CodeModuleManager.saveNewModule(moduleHandle);		
			System.out.println("4---------------------------");
			
			System.out.println("1---------------------------");
			//moduleHandle = CodeModuleManager.createNewModule(openmobster_1.length, openmobster_1, 1000);
			System.out.println("2---------------------------");
			System.out.println("ModuleHandle: "+moduleHandle);
			int offset = openmobster.length;
			success = CodeModuleManager.writeNewModule(moduleHandle, offset, openmobster_1, 0, openmobster_1.length);
			System.out.println("3---------------------------");
			System.out.println("Module Success: "+success);			
			CodeModuleManager.saveNewModule(moduleHandle);		
			System.out.println("4---------------------------");
		}
		catch(Exception sie)
		{
			System.out.println("Error-------------------------------------------------");
			System.out.println("Exception: "+sie.toString());
			System.out.println("Message: "+sie.getMessage());
			throw new RuntimeException(sie.toString());
		}
	}
}
