/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.rimos.module.connection;

import java.util.Date;
import java.util.Vector;
import java.util.TimerTask;
import java.util.Timer;

import net.rim.device.api.system.DeviceInfo; 

import org.openmobster.core.mobileCloud.rimos.configuration.Configuration;
import org.openmobster.core.mobileCloud.rimos.service.Registry;
import org.openmobster.core.mobileCloud.rimos.service.Service;

/**
 * 
 * @author openmobster@gmail.com
 *
 */
public final class NotificationListener extends Service
{
	private Worker worker;
	
	//pushThread
	private Thread pushThread;
	
	//poll timer
	private Timer pollTimer;
	
	private Date lastNotification;
	
	public NotificationListener()
	{
	}
	
	public static NotificationListener getInstance()
	{
		return (NotificationListener)Registry.getInstance().lookup(NotificationListener.class);
	}
	
	public void start()
	{	
		Configuration configuration = Configuration.getInstance();
		if(configuration.isActive())
		{			
			if(configuration.isInPushMode())
			{								
				//Making sure polling is deactiavted
				this.pollTimer = null; 
				
				//Starting the Push Thread
				this.worker = new Worker();
				this.pushThread = new Thread(this.worker);
				this.pushThread.start();								
			}
			else
			{								
				//Making Sure Push Thread is deactivated
				this.pushThread = null;
				
				//Start Polling according to configuration
				this.worker = new Worker();
				
				//Schedule using the specified poll interval
				this.pollTimer = new Timer();				
				this.pollTimer.scheduleAtFixedRate(this.worker, 5000, configuration.getCometPollInterval());				
				
				//System.out.println("-------------------------------------------");
				//System.out.println("PollingThread set to run every: "+configuration.getCometPollInterval()+"(ms)");
				//System.out.println("-------------------------------------------");
			}
		}		
	}
	
	public void stop()
	{
		//Stop the listening worker thread
		if(this.worker != null)
		{
			try
			{
				this.worker.isContainerStopping = true;
				
				if(this.worker.notifySession != null)
				{
					this.worker.notifySession.close();
				}								
			}
			catch(Exception e)
			{
				//Nothing to do...maybe some cleanup failure here
				//shouldn't disrupt the application
			}
			finally
			{
				//Wait for the worker thread to cleanup
				if(Configuration.getInstance().isInPushMode())
				{
					try{this.pushThread.join();}catch(Exception e){}
				}
				else
				{
					try{this.pollTimer.cancel();}catch(Exception e){}
				}
			}
		}		
		
		//Cleanup
		this.worker = null;
		this.lastNotification = null;
		this.pushThread = null;
		this.pollTimer = null;
	}
	
	public void restart()
	{
		try
		{			
			this.stop();
		}
		catch(Exception e)
		{
			//Nothing to do...maybe some cleanup failure here
			//shouldn't disrupt the application
		}
		finally
		{
			Registry.getInstance().restart(new NotificationListener());
		}
	}
			
	public boolean isActive()
	{
		Configuration conf = Configuration.getInstance();
		if(conf.isInPushMode())
		{
			if(this.worker != null && this.worker.notifySession != null && !this.worker.isDead)
			{			
				return true;
			}
		}
		else
		{
			if(this.worker != null && this.pollTimer != null)
			{			
				return true;
			}
		}
		return false;
	}
				
	Date getLastNotificationTimestamp()
	{
		return this.lastNotification;
	}		
	//---------------------------------------------------------------------------------------------------------------------------------------------
	private class Worker extends TimerTask
	{
		private NetSession notifySession;
		private boolean isContainerStopping;
		private boolean isDead;
				
		public void run()
		{			
			if(Configuration.getInstance().isInPushMode())
			{				
				this.startPushDaemon();
			}
			else
			{				
				this.startPollSession();
			}
		}
		
		private void startPushDaemon()
		{
			try
			{
				boolean secure = Configuration.getInstance().isSSLActivated();
				this.notifySession = NetworkConnector.getInstance().openSession(secure);				
				
				//Start the notification socket
				String deviceId = Configuration.getInstance().getDeviceId();
				String authHash = Configuration.getInstance().getAuthenticationHash();
			    String channel = this.getActiveChannels();				
				
				String command = 
					"<request>" +
						"<header>" +
							"<name>device-id</name>"+
							"<value><![CDATA["+deviceId+"]]></value>"+
						"</header>"+
						"<header>" +
							"<name>nonce</name>"+
							"<value><![CDATA["+authHash+"]]></value>"+
						"</header>"+
						"<header>" +
							"<name>command</name>"+
							"<value>notify</value>"+
						"</header>"+
						"<header>" +
							"<name>channel</name>"+
							"<value>"+channel+"</value>"+
						"</header>"+
						"<header>" +
							"<name>platform</name>"+
							"<value>blackberry</value>"+
						"</header>"+
							"<header>" +
							"<name>device</name>"+
							"<value>"+DeviceInfo.getDeviceName()+"</value>"+
						"</header>"+
					"</request>";
				
				//Used for debugging the daemon messages
				//System.out.println("Starting a Push Session---------------------------------------------");
				//System.out.println(command);
				//System.out.println("Platform Version: "+DeviceInfo.getPlatformVersion());
				//System.out.println("Software Version: "+DeviceInfo.getSoftwareVersion());
				//System.out.println("Device Name: "+DeviceInfo.getDeviceName());
				//System.out.println("---------------------------------------------------------------------");
												
				this.notifySession.sendOneWay(command);
																				
				//This is a blocking thread that receives data/commands pushed to it from the server				
				do
				{					
					String data = this.notifySession.waitForNotification();
																									
					if(this.isContainerStopping)
					{
						break;
					}
																														
					//Make sure a stopping of this thread was not ordered before consuming this notification
					lastNotification = new Date();
															
					//Process incoming data packets
					if(data.trim().length() != 0)
					{
						//Used for debugging the daemon messages
						//System.out.println("PushDaemon---------------------------------------------");
						//System.out.println(data);
						//System.out.println("-------------------------------------------------------");
						
						String incomingNotification = data.trim();
						
						//send the packet for application processing							
						if(incomingNotification.indexOf(Constants.command) != -1)
						{														
							CommandProcessor.getInstance().process(data.trim());
						}						
					}					
				}while(true);			
			}			
			catch(Exception e)
			{				
			}
			finally
			{
				this.isDead = true;
				//System.out.println("-------------------------------------------------------------");
				//System.out.println("Stopping the Push Session!!!");
				//System.out.println("-------------------------------------------------------------");				
				try
				{
					//close the notification net session
					if(this.notifySession != null)
					{
						this.notifySession.close();
					}										
				}
				catch(Exception e)
				{							
				}
			}
		}
		
		private void startPollSession()
		{
			try
			{
				boolean secure = Configuration.getInstance().isSSLActivated();
				this.notifySession = NetworkConnector.getInstance().openSession(secure);				
				
				//Start the notification socket
				String deviceId = Configuration.getInstance().getDeviceId();
				String authHash = Configuration.getInstance().getAuthenticationHash();
			    String channel = this.getActiveChannels();				
				
			    String command = 
					"<request>" +
						"<header>" +
							"<name>device-id</name>"+
							"<value><![CDATA["+deviceId+"]]></value>"+
						"</header>"+
						"<header>" +
							"<name>nonce</name>"+
							"<value><![CDATA["+authHash+"]]></value>"+
						"</header>"+
						"<header>" +
							"<name>command</name>"+
							"<value>notify</value>"+
						"</header>"+
						"<header>" +
							"<name>channel</name>"+
							"<value>"+channel+"</value>"+
						"</header>"+
						"<header>" +
							"<name>platform</name>"+
							"<value>blackberry</value>"+
						"</header>"+
							"<header>" +
							"<name>device</name>"+
							"<value>"+DeviceInfo.getDeviceName()+"</value>"+
						"</header>"+
					"</request>";
				
				//Used for debugging the daemon messages
				//System.out.println("Starting a Poll Session---------------------------------------------");
				//System.out.println(command);
				//System.out.println("Platform Version: "+DeviceInfo.getPlatformVersion());
				//System.out.println("Software Version: "+DeviceInfo.getSoftwareVersion());
				//System.out.println("Device Name: "+DeviceInfo.getDeviceName());
				//System.out.println("---------------------------------------------------------------------");
												
				this.notifySession.sendOneWay(command);
																				
				//This is a blocking thread that receives data/commands pushed to it from the server				
				String data = this.notifySession.waitForPoll();
				
				if(this.isContainerStopping)
				{					
					return;
				}
																													
				//Make sure a stopping of this thread was not ordered before consuming this notification
				lastNotification = new Date();
														
				//Process incoming data packets
				if(data.trim().length() != 0)
				{
					//Used for debugging the daemon messages
					//System.out.println("PollSession---------------------------------------------");
					//System.out.println(data);
					//System.out.println("--------------------------------------------------------");
					
					String incomingNotification = data.trim();
					
					//send the packet for application processing							
					if(incomingNotification.indexOf(Constants.command) != -1)
					{							
						CommandProcessor.getInstance().process(data.trim());														
					}						
				}				
			}			
			catch(Exception e)
			{				
			}
			finally
			{
				if(this.isContainerStopping)
				{
					this.cancel();
				}
				this.isDead = true;
				//System.out.println("-------------------------------------------------------------");
				//System.out.println("Stopping the Poll Session!!!");
				//System.out.println("-------------------------------------------------------------");			
				try
				{
					//close the notification net session
					if(this.notifySession != null)
					{
						this.notifySession.close();
					}										
				}
				catch(Exception e)
				{							
				}
			}						
		}
		
		private String getActiveChannels()
		{
			Vector myChannels = Configuration.getInstance().getMyChannels();
			if(myChannels != null)
			{
				int size = myChannels.size();
				StringBuffer buffer = new StringBuffer();
				for(int i=0; i<size; i++)
				{
					buffer.append((String)myChannels.elementAt(i));
					buffer.append("|");					
				}
				return buffer.toString();								
			}
			return "";
		}
	}	
}
