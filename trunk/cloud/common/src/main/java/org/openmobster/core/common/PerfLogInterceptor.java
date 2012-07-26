/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.common;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

/**
 *
 * @author openmobster@gmail.com
 */
public final class PerfLogInterceptor
{
	private FileOutputStream createConnection;
	private long createConnectionCtr;
	
	private FileOutputStream failedConnection;
	private long failedConnectionCtr;
	
	private FileOutputStream requestSent;
	private long requestSentCtr;
	
	private FileOutputStream requestFailed;
	private long requestFailedCtr;
	
	private FileOutputStream responseReceived;
	private long responseReceivedCtr;
	
	private FileOutputStream responseFailed;
	private long responseFailedCtr;
	
	public PerfLogInterceptor()
	{
		
	}
	
	public void start()
	{
		try
		{
			String perfFramework = System.getProperty("perf-framework");
			if(perfFramework == null)
			{
				return;
			}
			
			//Connection Success
			File createConnectionFile = new File("connection-success.xml");
			createConnectionFile.delete();
			this.createConnection = new FileOutputStream("connection-success.xml",true);
			this.createConnectionCtr = 0;
			
			//Failed Connection
			File failedConnectionFile = new File("connection-failed.xml");
			failedConnectionFile.delete();
			this.failedConnection = new FileOutputStream("connection-failed.xml",true);
			this.failedConnectionCtr = 0;
			
			//Request Sent Successfully
			File requestSentFile = new File("request-success.xml");
			requestSentFile.delete();
			this.requestSent = new FileOutputStream("request-success.xml",true);
			this.requestSentCtr = 0;
			
			//Request Failed
			File requestFailedFile = new File("request-failed.xml");
			requestFailedFile.delete();
			this.requestFailed = new FileOutputStream("request-failed.xml",true);
			this.requestFailedCtr = 0;
			
			//Response Received
			File responseReceivedFile = new File("response-success.xml");
			responseReceivedFile.delete();
			this.responseReceived = new FileOutputStream("response-success.xml",true);
			this.responseReceivedCtr = 0;
			
			//Response Failed
			File responseFailedFile = new File("response-failed.xml");
			responseFailedFile.delete();
			this.responseFailed = new FileOutputStream("response-failed.xml",true);
			this.responseFailedCtr = 0;
		}
		catch(Throwable t)
		{
			throw new RuntimeException(t);
		}
	}
	
	public void stop()
	{
		try
		{
			String perfFramework = System.getProperty("perf-framework");
			if(perfFramework == null)
			{
				return;
			}
			
			if(this.createConnection != null)
			{
				this.createConnection.close();
			}
			
			if(this.failedConnection != null)
			{
				this.failedConnection.close();
			}
			
			if(this.requestSent != null)
			{
				this.requestSent.close();
			}
			
			if(this.requestFailed != null)
			{
				this.requestFailed.close();
			}
			
			if(this.responseReceived != null)
			{
				this.responseReceived.close();
			}
			
			if(this.responseFailed != null)
			{
				this.responseFailed.close();
			}
		}
		catch(Throwable t)
		{
			throw new RuntimeException(t);
		}
	}
	
	public static PerfLogInterceptor getInstance()
	{
		return (PerfLogInterceptor)ServiceManager.locate("org.openmobster.core.common.PerfLogInterceptor");
	}
	
	public void logCreateConnection()
	{
		String perfFramework = System.getProperty("perf-framework");
		if(perfFramework == null)
		{
			return;
		}
		try
		{
			synchronized(this.createConnection)
			{	
				Calendar calendar = Calendar.getInstance();
				int hour_of_day = calendar.get(Calendar.HOUR_OF_DAY);
				int minute = calendar.get(Calendar.MINUTE);
				int second = calendar.get(Calendar.SECOND);
				int milli = calendar.get(Calendar.MILLISECOND);
				
				
				StringBuilder buffer = new StringBuilder();
				
				buffer.append("<entry>\n");
				buffer.append("<time-millis>"+calendar.getTimeInMillis()+"</time-millis>\n");
				buffer.append("<time>"+hour_of_day+":"+minute+":"+second+":"+milli+"</time>\n");
				buffer.append("<connection-number>"+(++this.createConnectionCtr)+"</connection-number>\n");
				buffer.append("</entry>\n\n");
				
				this.createConnection.write(buffer.toString().getBytes());
				this.createConnection.flush();
			}
		}
		catch(Throwable t)
		{
			throw new RuntimeException(t);
		}
	}
	
	public void logConnectionFailed()
	{
		String perfFramework = System.getProperty("perf-framework");
		if(perfFramework == null)
		{
			return;
		}
		try
		{
			synchronized(this.failedConnection)
			{	
				Calendar calendar = Calendar.getInstance();
				int hour_of_day = calendar.get(Calendar.HOUR_OF_DAY);
				int minute = calendar.get(Calendar.MINUTE);
				int second = calendar.get(Calendar.SECOND);
				int milli = calendar.get(Calendar.MILLISECOND);
				
				
				StringBuilder buffer = new StringBuilder();
				
				buffer.append("<entry>\n");
				buffer.append("<time-millis>"+calendar.getTimeInMillis()+"</time-millis>\n");
				buffer.append("<time>"+hour_of_day+":"+minute+":"+second+":"+milli+"</time>\n");
				buffer.append("<connection-number>"+(++this.failedConnectionCtr)+"</connection-number>\n");
				buffer.append("</entry>\n\n");
				
				this.failedConnection.write(buffer.toString().getBytes());
				this.failedConnection.flush();
			}
		}
		catch(Throwable t)
		{
			throw new RuntimeException(t);
		}
	}
	
	public void logRequestSent()
	{
		String perfFramework = System.getProperty("perf-framework");
		if(perfFramework == null)
		{
			return;
		}
		try
		{
			synchronized(this.requestSent)
			{	
				Calendar calendar = Calendar.getInstance();
				int hour_of_day = calendar.get(Calendar.HOUR_OF_DAY);
				int minute = calendar.get(Calendar.MINUTE);
				int second = calendar.get(Calendar.SECOND);
				int milli = calendar.get(Calendar.MILLISECOND);
				
				
				StringBuilder buffer = new StringBuilder();
				
				buffer.append("<entry>\n");
				buffer.append("<time-millis>"+calendar.getTimeInMillis()+"</time-millis>\n");
				buffer.append("<time>"+hour_of_day+":"+minute+":"+second+":"+milli+"</time>\n");
				buffer.append("<request-number>"+(++this.requestSentCtr)+"</request-number>\n");
				buffer.append("</entry>\n\n");
				
				this.requestSent.write(buffer.toString().getBytes());
				this.requestSent.flush();
			}
		}
		catch(Throwable t)
		{
			throw new RuntimeException(t);
		}
	}
	
	public void logRequestFailed()
	{
		String perfFramework = System.getProperty("perf-framework");
		if(perfFramework == null)
		{
			return;
		}
		try
		{
			synchronized(this.requestFailed)
			{	
				Calendar calendar = Calendar.getInstance();
				int hour_of_day = calendar.get(Calendar.HOUR_OF_DAY);
				int minute = calendar.get(Calendar.MINUTE);
				int second = calendar.get(Calendar.SECOND);
				int milli = calendar.get(Calendar.MILLISECOND);
				
				
				StringBuilder buffer = new StringBuilder();
				
				buffer.append("<entry>\n");
				buffer.append("<time-millis>"+calendar.getTimeInMillis()+"</time-millis>\n");
				buffer.append("<time>"+hour_of_day+":"+minute+":"+second+":"+milli+"</time>\n");
				buffer.append("<request-number>"+(++this.requestFailedCtr)+"</request-number>\n");
				buffer.append("</entry>\n\n");
				
				this.requestFailed.write(buffer.toString().getBytes());
				this.requestFailed.flush();
			}
		}
		catch(Throwable t)
		{
			throw new RuntimeException(t);
		}
	}
	
	public void logResponseRead()
	{
		String perfFramework = System.getProperty("perf-framework");
		if(perfFramework == null)
		{
			return;
		}
		try
		{
			synchronized(this.responseReceived)
			{	
				Calendar calendar = Calendar.getInstance();
				int hour_of_day = calendar.get(Calendar.HOUR_OF_DAY);
				int minute = calendar.get(Calendar.MINUTE);
				int second = calendar.get(Calendar.SECOND);
				int milli = calendar.get(Calendar.MILLISECOND);
				
				
				StringBuilder buffer = new StringBuilder();
				
				buffer.append("<entry>\n");
				buffer.append("<time-millis>"+calendar.getTimeInMillis()+"</time-millis>\n");
				buffer.append("<time>"+hour_of_day+":"+minute+":"+second+":"+milli+"</time>\n");
				buffer.append("<response-number>"+(++this.responseReceivedCtr)+"</response-number>\n");
				buffer.append("</entry>\n\n");
				
				this.responseReceived.write(buffer.toString().getBytes());
				this.responseReceived.flush();
			}
		}
		catch(Throwable t)
		{
			throw new RuntimeException(t);
		}
	}
	
	public void logResponseFailed()
	{
		String perfFramework = System.getProperty("perf-framework");
		if(perfFramework == null)
		{
			return;
		}
		try
		{
			synchronized(this.responseFailed)
			{	
				Calendar calendar = Calendar.getInstance();
				int hour_of_day = calendar.get(Calendar.HOUR_OF_DAY);
				int minute = calendar.get(Calendar.MINUTE);
				int second = calendar.get(Calendar.SECOND);
				int milli = calendar.get(Calendar.MILLISECOND);
				
				
				StringBuilder buffer = new StringBuilder();
				
				buffer.append("<entry>\n");
				buffer.append("<time-millis>"+calendar.getTimeInMillis()+"</time-millis>\n");
				buffer.append("<time>"+hour_of_day+":"+minute+":"+second+":"+milli+"</time>\n");
				buffer.append("<response-number>"+(++this.responseFailedCtr)+"</response-number>\n");
				buffer.append("</entry>\n\n");
				
				this.responseFailed.write(buffer.toString().getBytes());
				this.responseFailed.flush();
			}
		}
		catch(Throwable t)
		{
			throw new RuntimeException(t);
		}
	}
}
