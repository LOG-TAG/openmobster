/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

/**
 * 
 */
package org.conn.testdrive.connection;

import java.util.TimerTask;
import java.util.Vector;

/**
 * @author openmobster@gmail
 *
 */
public final class NotificationListener
{
	private String host;
	private String port;
	
	private NetSession session;
	private Thread pushThread;
	private TimerTask pushTask;
	
	private Vector updates;
	
	public NotificationListener()
	{
		
	}
	
	public void start()
	{
		try
		{
			this.updates = new Vector();
			
			NetworkConnector connector = NetworkConnector.getInstance(host, port);
			this.session = connector.openSession(false);
			this.pushTask = new Worker(this);
			this.pushThread = new Thread(this.pushTask);
			this.pushThread.start();
		}
		catch(Exception e)
		{
			throw new RuntimeException(e.toString());
		}
	}
	
	public void stop()
	{
		if(this.pushTask != null)
		{
			try
			{
				if(this.session != null)
				{
					this.session.close();
				}
			}
			catch(Exception e)
			{
				//nothing to do
			}
			finally
			{
				try{this.pushThread.join();}catch(Exception ex){}
			}
		}
		
		this.cleanup();
	}
	
	public boolean isStopped()
	{
		return (this.session == null);
	}
	
	private void cleanup()
	{
		this.session = null;
		this.pushThread = null;
		this.pushTask = null;
		this.updates = null;
	}
	
	public void clear()
	{
		this.updates.removeAllElements();
	}
	
	public Vector getUpdates()
	{
		return this.updates;
	}
	
	public static NotificationListener getInstance(String host, String port)
	{
		NotificationListener listener = new NotificationListener();
		listener.host = host;
		listener.port = port;		
		return listener;
	}
	
	public void triggerPush()
	{
		Thread triggerPush = new Thread(
			new Runnable()
			{
				public void run()
				{
					NetSession triggerSession = null;
					try
					{
						String payload = "trigger: pushme!!!";
						
						NetworkConnector connector = NetworkConnector.getInstance(host, port);
						triggerSession = connector.openSession(false);
						
						String command = "processorid=/testdrive/push";
						triggerSession.establishCloudSession(command);
						triggerSession.sendTwoWayCloudPayload(payload);											
					}
					catch(Exception e)
					{
						//System.out.println("-----------------------------------------");
						//System.out.println("Push Exception: "+e.getMessage());
						//System.out.println("-----------------------------------------");
					}
					finally
					{
						if(triggerSession != null)
						{
							try{triggerSession.close();}catch(Exception e){}
						}
					}
				}
			}
		);
		triggerPush.start();
	}
	//-------------------------------------------------------------------------------------------------------------------------------------
	private static class Worker extends TimerTask
	{
		private NotificationListener listener;
		
		public Worker(NotificationListener listener)
		{
			this.listener = listener;
		}
		
		public void run()
		{
			this.startPushDaemon();
		}
		
		private void startPushDaemon()
		{
			try
			{											
				String command = "processorid=/testdrive/push";
				
				//Used for debugging the daemon messages
				//System.out.println("PushDaemon Start Command---------------------------------------------");
				//System.out.println(command);
				//System.out.println("---------------------------------------------------------------------");
				
				NetSession session = this.listener.session;
				session.establishCloudSession(command);
				String stream = "<push><caller name='blackberry'/></push>";
				session.sendTwoWayCloudPayload(stream);
																				
				//This is a blocking thread that receives data/commands pushed to it from the server				
				do
				{					
					String data = session.waitForNotification();
																									
																																																							
					//Process incoming data packets
					if(data.trim().length() != 0)
					{
						//Used for debugging the daemon messages
						System.out.println("PushDaemon---------------------------------------------");
						System.out.println(data);
						System.out.println("-------------------------------------------------------");
						
						String incomingNotification = data.trim();
						
						//send the packet for application processing							
						//Update App state
						if(incomingNotification != null && incomingNotification.length()>0)
						{
							this.listener.updates.addElement(incomingNotification);
						}
					}					
				}while(true);			
			}			
			catch(Exception e)
			{	
				System.out.println("--------------------------------------------------");
				System.out.println("Shutting Down Push Daemon: "+e.toString());
				System.out.println("--------------------------------------------------");
			}
			finally
			{
				try
				{
					//close the notification net session
					if(this.listener.session != null)
					{
						try{this.listener.session.close();}catch(Exception e){}
						this.listener.cleanup();
					}										
				}
				catch(Exception e)
				{							
				}
			}
		}
	}
}
