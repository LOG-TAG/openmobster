/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.module.connection;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.Socket;

import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.module.connection.NetworkException;

/**
 * @author openmobster@gmail.com
 *
 */
public final class NetSession 
{
	private Socket socket;
	private InputStream is;
	private OutputStream os;
	
	public NetSession(Socket socket) throws NetworkException
	{
		if(socket == null)
		{
			throw new IllegalArgumentException("Socket cannot be null!!!");
		}
		try
		{
			this.socket = socket;
			this.is = socket.getInputStream();
			this.os = socket.getOutputStream();
		}
		catch(IOException ioe)
		{
			throw new NetworkException(this.getClass().getName(), "constructor", new Object[]{
				"Message="+ioe.getMessage()
			});
		}
	}
	
	public void sendOneWay(String request) throws NetworkException
	{
		try
		{
			this.writePayLoad(request.trim(), this.os);
		}
		catch(Exception e)
		{
			throw new NetworkException(this.getClass().getName(), "sendOneWay", new Object[]{
				"Request="+request,
				"Message="+e.getMessage()
			});
		}
	}
	
	public String sendTwoWay(String request) throws NetworkException
	{
		try
		{						
			this.writePayLoad(request.trim(), this.os);			
			return this.read(this.is);
		}
		catch(Exception e)
		{
			throw new NetworkException(this.getClass().getName(), "sendTwoWay", new Object[]{
				"Request="+request,
				"Message="+e.getMessage()
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
	}
	
	public String waitForNotification() throws NetworkException
	{
		try
		{
			String push = null;
			boolean condition = true;			
			do
			{
				push = this.read(this.is);
				
				if(push != null && push.trim().length()>0)
				{
					return push;
				}
				/*else
				{
					System.out.println("--------------------------------------------------");
					System.out.println("KeepAlive is active!!!");
					System.out.println("--------------------------------------------------");
				}*/
			}while(condition);
			
			return null;
		}
		catch(Exception e)
		{
			//e.printStackTrace(System.out);
			
			throw new NetworkException(this.getClass().getName(), "waitForNotification", new Object[]{
				"Message="+e.getMessage()
			});
		}
	}
	
	public String waitForPoll() throws NetworkException
	{
		try
		{
			String push = null;
			
			int pollCounter = 2; //stays open for about 2 minutes to catch a non-keepalive notification to process
			for(int i=0; i<pollCounter; i++)
			{
				push = this.read(this.is);
				
				if(push != null && push.trim().length()>0)
				{
					return push;
				}
				/*else
				{
					System.out.println("--------------------------------------------------");
					System.out.println("KeepAlive is active!!!");
					System.out.println("--------------------------------------------------");
				}*/
			}
			
			return null;
		}
		catch(Exception e)
		{
			//e.printStackTrace(System.out);
			throw new NetworkException(this.getClass().getName(), "waitForPoll", new Object[]{
				"Message="+e.getMessage()
			});
		}
	}
	
	public void unblock() throws NetworkException
	{
		this.is.notify();
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
			ErrorHandler.getInstance().handle(new NetworkException(this.getClass().getName(), "close", new Object[]{
				"Message="+ioe.getMessage()
			}));
		}
	}
	//------------------------------------------------------------------------------------------------------------------------------------------
	private String read(InputStream is) throws IOException
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
	
	private void writePayLoad(String payLoad, OutputStream os) throws IOException
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
