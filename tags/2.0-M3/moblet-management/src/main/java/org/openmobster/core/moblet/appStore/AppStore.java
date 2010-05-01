/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.moblet.appStore;

import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.openmobster.server.api.service.Request;
import org.openmobster.server.api.service.Response;
import org.openmobster.server.api.service.MobileServiceBean;
import org.openmobster.server.api.service.ServiceInfo;

import org.openmobster.core.common.errors.ErrorHandler;
import org.openmobster.core.common.errors.SystemException;
import org.openmobster.core.common.IOUtilities;
import org.openmobster.core.common.Utilities;
import org.openmobster.core.common.ServiceManager;
import org.openmobster.core.moblet.MobletApp;
import org.openmobster.core.moblet.registry.Registry;

/**
 * @author openmobster@gmail.com
 */
@ServiceInfo(uri="moblet-management://appStore")
public class AppStore implements MobileServiceBean
{	
	private Registry registry;
	
	public AppStore()
	{
		
	}
		
	public Registry getRegistry() 
	{
		return registry;
	}


	public void setRegistry(Registry registry) 
	{
		this.registry = registry;
	}
	
	public static AppStore getInstance()
	{
		return (AppStore)ServiceManager.locate("moblet-management://appStore");
	}
	//--------------------------------------------------------------------------------------------------------
	public Response invoke(Request request) 
	{
		try
		{									
			String action = request.getAttribute("action");
												
			if(action.equalsIgnoreCase("getRegisteredApps"))
			{
				List<MobletApp> allApps = this.getRegisteredApps();
				
				//Prepare success response
				if(allApps != null && !allApps.isEmpty())
				{
					Response response = new Response();
					
					List<String> uris = new ArrayList<String>();
					List<String> names = new ArrayList<String>();
					List<String> descs = new ArrayList<String>();
					List<String> downloadUrls = new ArrayList<String>();
					for(MobletApp app: allApps)
					{						
						uris.add(app.getUri());
						names.add(app.getName());
						descs.add(app.getDescription());
						
						String downloadUrl = "/"+app.getUri()+app.getConfigLocation();
						downloadUrls.add(downloadUrl);
					}
					
					response.setListAttribute("uris", uris);
					response.setListAttribute("names", names);
					response.setListAttribute("descs", descs);
					response.setListAttribute("downloadUrls", downloadUrls);
					
					return response;
				}								
			}
			
			return null;
		}
		catch(Exception e)
		{
			ErrorHandler.getInstance().handle(e);
			throw new SystemException(e.getMessage(), e);
		}
	}
	//---------------------------------------------------------------------------------------------------------		
	public List<MobletApp> getRegisteredApps() throws Exception
	{
		return this.registry.getAllApps();		
	}
	
	public byte[] getAppConfig(String downloadUrl) throws Exception
	{
		StringTokenizer st = new StringTokenizer(downloadUrl, "/");
		String appUri = st.nextToken();
		InputStream is = this.registry.getAppConfig(appUri);
		return IOUtilities.readBytes(is);
	}
	
	public InputStream getAppBinary(String downloadUrl) throws Exception
	{
		StringTokenizer st = new StringTokenizer(downloadUrl, "/");
		String appUri = st.nextToken();
		return this.registry.getAppBinary(appUri);
	}
	
	public MobletApp findByDownloadUrl(String downloadUrl) throws Exception
	{
		StringTokenizer st = new StringTokenizer(downloadUrl, "/");
		String appUri = st.nextToken();
		return this.registry.getApp(appUri);
	}
}
