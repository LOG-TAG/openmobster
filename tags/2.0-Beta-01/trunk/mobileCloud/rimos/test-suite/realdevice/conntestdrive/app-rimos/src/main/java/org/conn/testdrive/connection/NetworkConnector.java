/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.conn.testdrive.connection;

import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.io.SocketConnection;

/**
 * @author openmobster@gmail.com
 *
 */
public final class NetworkConnector 
{	
	private String host;
	private String port;
	
	public NetworkConnector()
	{
		
	}
	
	public void start()
	{		
	}
	
	public void stop()
	{		
	}
	
	public static NetworkConnector getInstance(String host, String port)
	{
		NetworkConnector connector = new NetworkConnector();
		connector.host = host;
		connector.port = port;
		return  connector;
	}
	//--------------------------------------------------------------------------------------------------------------------------------------------
	public NetSession openSession(boolean secure) throws NetworkException
	{
		try
		{
			NetSession session = null;
			
			String connectionType = "socket://";	
			
			StringBuffer buffer = new StringBuffer();
			buffer.append(connectionType);
			buffer.append(host);
			buffer.append(":");
			buffer.append(port);			
			
			//NOTE: This is used for direct TCP/IP transport mode.
			//This requires an APN setup on the device which has some can of
			//worms associated as far as data charges, setup, access etc goes
			//this should not be the connection mode supported out of the box
			//out of the box, BIS should be the one used since thats what
			//ships out of the box on the device as well
			buffer.append(";deviceside=true;EndToEndRequired");			
			
			Object socketObject = Connector.open(buffer.toString(), 
					Connector.READ_WRITE, true);
			SocketConnection socket = (SocketConnection)socketObject;
			
			//Just use device's default timeout on an inactive socket of 2 minutes
			//((SocketConnectionEnhanced)socket).setSocketOptionEx(SocketConnectionEnhanced.READ_TIMEOUT, 10000);
			
			session = new NetSession(socket);
			
			return session;
		}
		catch(IOException ioe)
		{
			throw new NetworkException(this.getClass().getName(), "openSession", new Object[]{
				"Secure="+secure,
				"Message="+ioe.getMessage()
			});
		}
	}			
}
