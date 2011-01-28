/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.dev.tools.rimos;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import net.rim.device.api.system.DeviceInfo;

import org.openmobster.core.mobileCloud.api.service.Response;
import org.openmobster.core.mobileCloud.rimos.configuration.Configuration;
import org.openmobster.core.mobileCloud.rimos.module.bus.Bus;
import org.openmobster.core.mobileCloud.rimos.module.bus.Invocation;
import org.openmobster.core.mobileCloud.rimos.util.IOUtil;
import org.openmobster.core.mobileCloud.rimos.util.StringUtil;
import org.openmobster.core.mobileCloud.api.service.MobileService;
import org.openmobster.core.mobileCloud.api.service.Request;
import org.openmobster.core.mobileCloud.api.service.ServiceInvocationException;

/**
 * @author openmobster@gmail
 *
 */
public final class ActivationUtil
{
	public static void parseConfig(String resource) throws IOException
	{
		InputStream is = ActivationUtil.class.getResourceAsStream(resource);
		Hashtable resources = new Hashtable();
		if(is != null)
		{
			String contents = new String(IOUtil.read(is));
			String[] tokens = StringUtil.tokenize(contents, "\n");
			if(tokens != null)
			{
				int length = tokens.length;
				for(int i=0; i<length; i++)
				{
					String token = tokens[i].trim();
					
					if(token.indexOf('=')!=-1)
					{
						String[] values = StringUtil.tokenize(token, "=");
						if(values != null && values.length >=2)
						{
							resources.put(values[0].trim(), values[1].trim());
						}
					}
				}
			}
			
			String cloudServerIp = (String)resources.get("cloud_server_ip");
			String email = (String)resources.get("email");
			String password = (String)resources.get("password");
			String deviceIdentifier = "IMEI:"+DeviceInfo.getDeviceId();
			
			Configuration configuration = Configuration.getInstance();
			configuration.setDeviceId(deviceIdentifier);
			configuration.setServerIp(cloudServerIp);
			configuration.setEmail(email);
			configuration.setAuthenticationHash(password);
			configuration.save();
		}
	}
	
	public static synchronized void activateDevice() throws Exception
	{
		if(Configuration.getInstance().isActive())
		{
			return;				
		}
		
		String deviceIdentifier = Configuration.getInstance().getDeviceId();
		String cloudServerIp = Configuration.getInstance().getServerIp();
		String email = Configuration.getInstance().getEmail();
		String password = Configuration.getInstance().getAuthenticationHash();
		bootup(deviceIdentifier, cloudServerIp, null);
		
		
		Request request = new Request("provisioning");
		request.setAttribute("email", email);
		request.setAttribute("password", password);			
		request.setAttribute("identifier", deviceIdentifier);
		
		Response response = MobileService.invoke(request);
					
		if(response.getAttribute("idm-error") == null)
		{
			//Success Scenario
			processProvisioningSuccess(email, response);
			Invocation invocation = new Invocation("org.openmobster.core.mobileCloud.invocation.StartCometDaemon");
			Bus.getInstance().invokeService(invocation);
		}
		else
		{
			//Error Scenario
			String errorKey = response.getAttribute("idm-error");									
			throw new RuntimeException(errorKey);
		}
	}
	
	private static void processProvisioningSuccess(String email,Response response)
	{		
		Configuration configuration = Configuration.getInstance();
		
		String authenticationHash = response.getAttribute("authenticationHash");
		configuration.setEmail(email);
		configuration.setAuthenticationHash(authenticationHash);
		configuration.setActive(true);
		
		configuration.save();
	}	
	
	private static synchronized void bootup(String deviceIdentifier,String serverIp, String port) 
	throws ServiceInvocationException
	{
		Configuration conf = Configuration.getInstance();
		
		conf.deActivateSSL();
		
		conf.setDeviceId(deviceIdentifier);
		conf.setServerIp(serverIp);
		if(port != null && port.trim().length()>0)
		{
			conf.setPlainServerPort(port);
		}
		conf.setActive(false);
		
		conf.save();
		
		Request request = new Request("provisioning");
		request.setAttribute("action", "metadata");
		Response response = MobileService.invoke(request);
		
		//Read the Server Response
		String serverId = response.getAttribute("serverId");
		String plainServerPort = response.getAttribute("plainServerPort");
		String secureServerPort = response.getAttribute("secureServerPort");
		String isSSlActive = response.getAttribute("isSSLActive");
		String maxPacketSize = response.getAttribute("maxPacketSize");
		String httpPort = response.getAttribute("httpPort");
		
		//Setup the configuration
		conf.setServerId(serverId);
		conf.setPlainServerPort(plainServerPort);
		if(secureServerPort != null && secureServerPort.trim().length()>0)
		{
			conf.setSecureServerPort(secureServerPort);
		}
		
		if(isSSlActive.equalsIgnoreCase("true"))
		{
			conf.activateSSL();
		}
		else
		{
			conf.deActivateSSL();
		}
		
		conf.setMaxPacketSize(Integer.parseInt(maxPacketSize));
		conf.setHttpPort(httpPort);
				
		conf.save();
	}
}
