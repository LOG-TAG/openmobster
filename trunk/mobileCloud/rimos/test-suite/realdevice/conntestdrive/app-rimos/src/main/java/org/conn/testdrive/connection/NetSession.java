/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.conn.testdrive.connection;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import javax.microedition.io.SocketConnection;

//import net.rim.device.api.io.SocketConnectionEnhanced;


/**
 * @author openmobster@gmail.com
 *
 */
public final class NetSession 
{
	private SocketConnection socket;
	private DataInputStream is;
	private OutputStream os;
	
	public NetSession(SocketConnection socket) throws NetworkException
	{
		if(socket == null)
		{
			throw new IllegalArgumentException("Socket cannot be null!!!");
		}
		try
		{
			this.socket = socket;
			this.is = socket.openDataInputStream();
			this.os = socket.openOutputStream();
		}
		catch(IOException ioe)
		{
			throw new NetworkException(this.getClass().getName(), "constructor", new Object[]{
				"Message="+ioe.getMessage()
			});
		}
	}
	
	public String sendPayloadTwoWay(String payload) throws NetworkException
	{
		try
		{
			String response = null;
			
			this.writePayLoad(payload, this.os);
			
			response = this.read(this.is);
			
			return response;
		}
		catch(Exception e)
		{
			throw new NetworkException(this.getClass().getName(), "sendPayloadTwoWay", new Object[]{
				"Payload="+payload,
				"Message="+e.getMessage()
			});
		}
		finally
		{
			this.close();
		}
	}
	
	public void close()
	{
		try
		{
			this.is.close();
			this.os.close();
			this.socket.close();
		}
		catch(IOException ioe)
		{
		}
	}
	
	public String establishCloudSession(String request) throws NetworkException
	{
		try
		{
			String response = null;
						
			this.writeCloudPayLoad(request.trim(), this.os);
			
			response = this.readCloud(this.is);
			
			return response;
		}
		catch(Exception e)
		{
			throw new NetworkException(this.getClass().getName(), "establishCloudSession", new Object[]{
				"Request="+request,
				"Message="+e.getMessage()
			});
		}
	}
	
	public String sendTwoWayCloudPayload(String request) throws NetworkException
	{
		try
		{
			String response = null;
						
			this.writeCloudPayLoad(request, this.os);
			
			response = this.readCloud(this.is);
			
			return response;
		}
		catch(Exception e)
		{
			throw new NetworkException(this.getClass().getName(), "sendTwoWayCloudPayload", new Object[]{
				"Request="+request,
				"Message="+e.getMessage()
			});
		}
	}
	//-----Connection management--------------------------------------------------------------------------------------------------------------------------------------
	public String waitForNotification() throws NetworkException
	{
		try
		{												
			String push = null;
			
			boolean condition = true;
			while(condition)
			{
				push = this.readCloud(this.is);
				System.out.println("PushReceive---------------------------------------");
				System.out.println(push);
				System.out.println("--------------------------------------------------");
				if(push != null && push.trim().length()>0 && !push.trim().startsWith("@"))
				{
					return push;
				}
			}
			
			return null;
		}
		catch(Exception e)
		{
			throw new NetworkException(this.getClass().getName(), "waitForNotification", new Object[]{
				"Message="+e.getMessage()
			});
		}
	}
		
	//some client controlled keep-alive code for persistent connection which did not work on a real device
	//it should work in theory, but tcp/network stream implementation of bb os is a bit flaky
	/*public void activatePush() throws NetworkException
	{
		try
		{
			((SocketConnectionEnhanced)this.socket).setSocketOptionEx(SocketConnectionEnhanced.READ_TIMEOUT, 120000);
		}
		catch(Exception e)
		{
			throw new NetworkException(this.getClass().getName(), "activatePush", new Object[]{
				"Message="+e.getMessage(),
				"Exception:"+e.toString()
			});
		}
		
		try
		{			
			System.out.println("---------------------------------------------------");
			System.out.println("Before KeepAlive: "+this.socket.getSocketOption(SocketConnection.KEEPALIVE));
			this.socket.setSocketOption(SocketConnection.KEEPALIVE, 1);
			System.out.println("After KeepAlive: "+this.socket.getSocketOption(SocketConnection.KEEPALIVE));
			System.out.println("---------------------------------------------------");
		}
		catch(Exception e)
		{
			throw new NetworkException(this.getClass().getName(), "activatePush", new Object[]{
				"Message="+e.getMessage(),
				"Exception:"+e.toString()
			});
		}
	}*/
	//------------------------------------------------------------------------------------------------------------------------------------------
	private String read(InputStream is) throws IOException
	{
		String data = null;
		int received = 0;
		ByteArrayOutputStream bos = null;
		try
		{
			bos = new ByteArrayOutputStream();
			while(true)
			{			
				received = is.read();	
				
				if(received == '\n')
				{					
					break;
				}
				
				bos.write(received);
			}			
			
			
			bos.flush();			
			StringBuffer buffer = new StringBuffer();
			byte[] cour = bos.toByteArray();
			buffer.append(new String(cour));						
			data = buffer.toString();
			
			return data;
		}
		finally
		{
			if(bos != null)
			{
				try{bos.close();}catch(IOException e){}
			}
		}
	}
	
	private void writePayLoad(String payLoad, OutputStream os) throws IOException
	{
		os.write(payLoad.getBytes());
		os.flush();		
	}
	//------------------------------------------------------------------------------------------------------------------------------------------
	private String readCloud(InputStream is) throws IOException
	{
		String data = null;
		int received = 0;
		ByteArrayOutputStream bos = null;
		try
		{
			bos = new ByteArrayOutputStream();
			boolean carriageFound = false;
			while(true)
			{			
				received = is.read();	
				
				//Detect closing
				if(received == -1)
				{
					throw new IOException("InputStream is closed!!");
				}
												
				if(carriageFound)
				{
					carriageFound = false;
					if(received == '\n')
					{
						break;
					}					
				}				
				if(received == '\r')
				{
					carriageFound = true;
				}
				
				bos.write(received);
			}			
			bos.flush();
			
			StringBuffer buffer = new StringBuffer();
			byte[] cour = bos.toByteArray();
			buffer.append(new String(cour));						
			data = buffer.toString();
			
			return data;
		}
		finally
		{
			if(bos != null)
			{
				try{bos.close();}catch(IOException e){}
			}
		}
	}
	
	private void writeCloudPayLoad(String payLoad, OutputStream os) throws IOException
	{
		int startIndex = 0;
		int endIndex = 0;
		boolean eofSent = false;
		while((endIndex=payLoad.indexOf("\n", startIndex))!=-1)
		{				
			String packet = payLoad.substring(startIndex, endIndex);
			os.write((packet+"\n").getBytes());
			
			startIndex = endIndex +1;
			
			//Check if startIndex has exceeded beyond the last index of the string
			if(startIndex >= payLoad.length()-1)
			{
				os.write("EOF\n".getBytes());
				os.flush();
				eofSent = true;
				break;
			}
		}
		if(!eofSent)
		{
			String packet = payLoad.substring(startIndex);
			os.write((packet+"EOF\n").getBytes());
			os.flush();
		}
	}
}
