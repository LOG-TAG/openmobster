/**
 * Copyright (c) {2003,2009} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
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
import net.rim.device.api.io.SocketConnectionEnhanced;


/**
 * @author openmobster@gmail.com
 *
 */
public final class NetSession 
{
	private SocketConnection socket;
	private DataInputStream is;
	private OutputStream os;
	private boolean manuallyClosed;
	
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
			this.manuallyClosed = true;
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
			/*String push = null;
			do
			{
				push = this.readPush();
			}while(push == null || push.trim().length()==0);
			
			return push;*/
			
			String push = null;
			boolean condition = true;
			while(condition && !this.manuallyClosed)
			{								
				int available = this.is.available();
				System.out.println("-----------------------------------------");
				System.out.println("# of bytes available: "+available);
				System.out.println("-----------------------------------------");
				
				if(available > 1)
				{
					push = this.readCloud(this.is);
					if(push != null)
					{
						return push;
					}
				}
				
				//otherwise just send a KEEP-ALIVE packet
				this.os.write("EOF\n".getBytes());
				this.os.flush();
				Thread.currentThread().sleep(90000);
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
	
	public void activatePush() throws NetworkException
	{
		/*try
		{
			((SocketConnectionEnhanced)this.socket).setSocketOptionEx(SocketConnectionEnhanced.READ_TIMEOUT, 120000);
		}
		catch(Exception e)
		{
			throw new NetworkException(this.getClass().getName(), "activatePush", new Object[]{
				"Message="+e.getMessage(),
				"Exception:"+e.toString()
			});
		}*/
		
		/*try
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
		}*/
	}
	
	private String readPush() throws IOException
	{
		boolean whileCondition = true;
		while(whileCondition)
		{
			this.is.read();
			this.is.skipBytes(this.is.available());
		}
		return null;
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
				
				//Detect a heartbeat
				if(received == '@')
				{
					this.is.skipBytes(this.is.available());
					
					bos.reset();
					carriageFound = false;
					
					continue;
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
