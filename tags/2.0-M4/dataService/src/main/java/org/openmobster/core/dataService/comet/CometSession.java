/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.dataService.comet;

import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import org.apache.mina.common.IoSession;

import org.openmobster.core.common.bus.Bus;
import org.openmobster.core.common.bus.BusListener;
import org.openmobster.core.common.bus.BusMessage;
import org.openmobster.core.dataService.Constants;
import org.openmobster.core.services.subscription.Subscription;
import org.openmobster.core.services.subscription.SubscriptionManager;

/**
 * @author openmobster@gmail.com
 */
public final class CometSession implements Serializable,BusListener 
{	
	private static final long serialVersionUID = 6479883822795121829L;
	private static Logger log = Logger.getLogger(CometSession.class);
	
	
	//Active Session State
	private IoSession activeSession;
	
	
	private Subscription subscription;
	private Timer keepAlive;
	
	
	private CometSession()
	{
		
	}
	
	static CometSession createInstance(Subscription subscription)
	{
		if(subscription == null || subscription.getClientId() == null || 
		   subscription.getClientId().trim().length() == 0)
		{
			throw new IllegalArgumentException("Subscription is invalid!!!");
		}
		
		CometSession session = new CometSession();
		session.subscription = subscription;
		return session;
	}
	
	public String getUri()
	{
		return this.subscription.getClientId();
	}
	//-------------------------------------------------------------------------------------------------------------
	public void start()
	{
		String busUri = this.getUri();
		Bus.startBus(busUri);
		Bus.addBusListener(busUri, this);
	}
	
	public void stop()
	{
		Bus.stopBus(this.getUri());		
		this.deactivate();
	}
	
	public void activate(IoSession activeSession)
	{
		if(activeSession == null)
		{
			throw new IllegalArgumentException("Active Session cannot be null!!");
		}		
		if(!activeSession.isConnected())
		{
			throw new IllegalStateException("CometSession cannot be activated. Socket is closed!!");
		}
		
		this.activeSession = activeSession;	
		
		//restart the BusReader
		Bus.restartBus(this.getUri());				
	}
							
	public boolean isActive()
	{
		return (this.activeSession != null && this.activeSession.isConnected());
	}
	
	public void deactivate()
	{
		if(this.isActive())
		{
			this.activeSession.close();
		}
		
		this.cleanupKeepAliveDaemon();
	}
	
	public void sendHeartBeat()
	{
		this.sendPacket(null);
	}
	
	public void startKeepAliveDaemon(long pulseInterval)
	{
		this.cleanupKeepAliveDaemon();
		
		//Start the KeepAliveDaemon for this session
		this.keepAlive = new Timer(true); //run as a daemon
		TimerTask keepAliveDaemon = new KeepAliveDaemon(pulseInterval,this);
		long startDelay = 5000;
		this.keepAlive.schedule(keepAliveDaemon, startDelay, pulseInterval);
	}
	
	private void cleanupKeepAliveDaemon() 
	{
		if(this.keepAlive != null)
		{
			this.keepAlive.cancel();
		}
	}
	//-----Bus Listener implementation--------------------------------------------------------------------------------------------------------
	public void messageIncoming(BusMessage busMessage) 
	{
		String command = (String)busMessage.getAttribute(Constants.command);
		
		log.debug("-------------------------------------------------");
		log.debug("Bus Message received by: "+busMessage.getBusUri());
		log.debug("Sent by: "+busMessage.getSenderUri());
		log.debug("Command: "+command);
		log.debug("-------------------------------------------------");
		
		if(command != null)
		{
			this.sendPacket(command, busMessage);
		}
	}
	//----------------------------------------------------------------------------------------------------------
	private synchronized void sendPacket(String packet,BusMessage busMessage)
	{
		if(this.isActive())
		{
			if(this.allowNotification(busMessage))
			{
				log.debug("Actually Sending-------------------------------------------------");
				log.debug("Bus Message received by: "+busMessage.getBusUri());
				log.debug("Sent by: "+busMessage.getSenderUri());
				log.debug("-----------------------------------------------------------------");
				
				if(packet != null)
				{
					this.activeSession.write(packet);
				}
				this.activeSession.write(Constants.endOfStream);
			}
			
			busMessage.acknowledge();
		}
	}
	
	private synchronized void sendPacket(String packet)
	{
		if(this.isActive())
		{
			if(packet != null)
			{
				this.activeSession.write(packet);
			}
			this.activeSession.write(Constants.endOfStream);
		}
	}
	
	private boolean allowNotification(BusMessage busMessage)
	{
		String channel = busMessage.getSenderUri();
		
		SubscriptionManager mgr = (SubscriptionManager)this.activeSession.getAttribute(Constants.subscription);
		if(mgr != null && mgr.isMyChannel(channel))
		{
			return true;
		}
		return false;
	}
}
