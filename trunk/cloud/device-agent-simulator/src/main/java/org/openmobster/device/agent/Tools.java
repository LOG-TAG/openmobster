/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.device.agent;

import java.net.InetAddress;
import java.net.Socket;

/**
 * @author openmobster@gmail.com
 */
public class Tools 
{
	public static Socket getPlainSocket() throws Exception
	{
		Socket socket = null;
		
		//Create a socket
		String serverIp = org.openmobster.device.agent.configuration.Configuration.getInstance().getServerIp();
		InetAddress localhost = InetAddress.getByName(serverIp);
		String ip = localhost.getHostAddress();
		socket = new Socket(ip, 1502);
		
		return socket;
	}
}
