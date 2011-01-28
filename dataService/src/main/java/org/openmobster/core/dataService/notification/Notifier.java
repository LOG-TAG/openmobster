/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.dataService.notification;

import org.apache.log4j.Logger;

import org.openmobster.core.dataService.Constants;

import org.openmobster.core.common.bus.Bus;
import org.openmobster.core.common.bus.BusMessage;

/**
 * This component processes all notifications/updates that must be sent to actively connected devices
 * 
 * @author openmobster@gmail.com
 */
public class Notifier 
{
	private static Logger log = Logger.getLogger(Notifier.class);
	
	
	public Notifier()
	{
		
	}
	
	public void start()
	{
		log.info("---------------------------------------");
		log.info("Notifier successfully started.........");
		log.info("---------------------------------------");
	}
	
	public void stop()
	{
		
	}
	
	public void process(Notification notification)
	{
		String deviceToNotify = null;
		String command = null;
		
		if(notification.getType() == NotificationType.SYNC)
		{
			log.debug("Sync Notification for "+notification.getMetaData(Constants.device));
			
			StringBuilder commandBuilder = new StringBuilder();
			commandBuilder.append(Constants.command+"="+Constants.sync+Constants.separator);
			commandBuilder.append(Constants.service+"="+notification.getMetaData(Constants.service));			
			
			command = commandBuilder.toString()+Constants.endOfCommand;
			deviceToNotify = notification.getMetaData(Constants.device);
		}
		
		if(deviceToNotify != null && command != null)
		{
			BusMessage busMessage = new BusMessage();
			busMessage.setBusUri(deviceToNotify);
			busMessage.setSenderUri(notification.getMetaData(Constants.service));
			busMessage.setAttribute(Constants.command, command);
			
			Bus.sendMessage(busMessage);
		}
	}
}
