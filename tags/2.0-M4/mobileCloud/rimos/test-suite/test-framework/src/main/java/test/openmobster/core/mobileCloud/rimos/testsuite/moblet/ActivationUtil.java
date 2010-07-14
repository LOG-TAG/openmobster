/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.core.mobileCloud.rimos.testsuite.moblet;

import org.openmobster.core.mobileCloud.api.service.Response;
import org.openmobster.core.mobileCloud.rimos.configuration.Configuration;
import org.openmobster.core.mobileCloud.rimos.module.bus.Bus;
import org.openmobster.core.mobileCloud.rimos.module.bus.Invocation;
import org.openmobster.core.mobileCloud.api.service.MobileService;
import org.openmobster.core.mobileCloud.api.service.Request;
import org.openmobster.core.mobileCloud.api.service.ServiceInvocationException;
import org.openmobster.core.mobileCloud.rimos.module.bus.BusException;

/**
 * @author openmobster@gmail
 *
 */
public final class ActivationUtil
{
	public static String cloudServerIp = "192.168.1.108"; //Modify Allowed: This should be the IP address of your server used for running the testsuite
	
	
	public static String deviceIdentifier = "IMEI:8675309"; //Do Not Modify
	public static String email = "blah2@gmail.com"; //Do Not Modify
	public static String password = "blahblah2"; //Do Not Modify
	
	public static void activateDevice() throws Exception
	{
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
	
	private static void processProvisioningSuccess(String email,Response response) throws BusException
	{		
		Configuration configuration = Configuration.getInstance();
		
		String authenticationHash = response.getAttribute("authenticationHash");
		configuration.setEmail(email);
		configuration.setAuthenticationHash(authenticationHash);
		configuration.setActive(true);
		
		configuration.save();
		
		System.out.println("Device Activation------------------------------------------------------");
		System.out.println("Email: "+email);
		System.out.println("AuthenticationHash: "+authenticationHash);
		System.out.println("Stored Email: "+configuration.getEmail());
		System.out.println("Stored Hash(Sent for Authorization): "+configuration.getAuthenticationHash());
		System.out.println("Stored Nonce: "+configuration.getAuthenticationNonce());
		System.out.println("-----------------------------------------------------------------------");
		
		//Start the Push Daemon
		Invocation invocation = new Invocation("org.openmobster.core.mobileCloud.invocation.CometRecycleHandler");
		Bus.getInstance().invokeService(invocation);
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
