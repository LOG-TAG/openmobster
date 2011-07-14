/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.pushmail.cloud.channel;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.apache.log4j.Logger;

import org.openmobster.core.security.device.Device;
import org.openmobster.server.api.model.MobileBean;
import org.openmobster.server.api.model.Channel;
import org.openmobster.server.api.model.ChannelInfo;

import org.openmobster.pushmail.cloud.domain.MailProcessor;
import org.openmobster.core.synchronizer.server.SyncContext;

/**
 * The Mail Channel exposes MailBean instances to the Sync system all the way to the local db on the device
 * 
 * @author openmobster@gmail.com
 */
@ChannelInfo(uri="pushmail_channel", 
		     mobileBeanClass="org.openmobster.pushmail.cloud.channel.MailBean",
		     updateCheckInterval=60000
)
public class MailChannel implements Channel
{	
	private static Logger log = Logger.getLogger(MailChannel.class);
	
	//used to get mail from an imap server
	private MailProcessor mailProcessor;
	
	//keeps track of mail instances that should be pushed or not
	//its a deviceid/PushFilter map
	private Map<String,PushFilter> pushFilters; 
	
	
	public MailChannel()
	{
		
	}
	
	public void start()
	{
		this.pushFilters = new HashMap<String,PushFilter>();
	}
	
	public void stop()
	{
		
	}
	
	public MailProcessor getMailProcessor()
	{
		return mailProcessor;
	}

	public void setMailProcessor(MailProcessor mailProcessor)
	{
		this.mailProcessor = mailProcessor;
	}
	//---Channel implementation-------------------------------------------------------------------------------------------------
	//----Synchronization LifeCycle related callbacks---------------------------------------------------------------------------
	public List<? extends MobileBean> bootup() 
	{	
		List<MailBean> inbox = this.mailProcessor.readInbox();
		
		//If total # of emails in the inbox are greater than the favorable active number
		int active = 100;
		if(inbox.size() > active)
		{
			List<MailBean> list = new ArrayList<MailBean>();
			for(int i=0; i<active; i++)
			{
				list.add(inbox.get(i));
			}
			
			return list;
		}
		
		//Bootstrap the Push State, such that only new emails are pushed
		String deviceId = SyncContext.getInstance().getDeviceId();
		this.initPushFilter(deviceId, inbox);
		
		return inbox;
	}
	
	public List<? extends MobileBean> readAll() 
	{
		List<MailBean> inbox = this.mailProcessor.readInbox();
		
		//Bootstrap the Push State, such that only new emails are pushed
		String deviceId = SyncContext.getInstance().getDeviceId();
		this.initPushFilter(deviceId, inbox);
		
		//returns all the instances in the inbox
		return inbox;
	}
	
	public MobileBean read(String id) 
	{		
		List<MailBean> inbox = this.mailProcessor.readInbox();
		for(MailBean local:inbox)
		{
			if(local.getOid().equals(id))
			{
				return local;
			}
		}
		return null;
	}

	public String create(MobileBean mobileBean) 
	{
		//Not Applicable
		return null;
	}
	
	public void update(MobileBean mobileBean) 
	{
		//Not Applicable
	}	

	public void delete(MobileBean mobileBean) 
	{
		MailBean mailBean = (MailBean)mobileBean;
		
		//Deletes the email from the mail server
		this.mailProcessor.delete(mailBean);
	}
	//---Push Lifecycle related callbacks-----------------------------------------------------------------------------------------------
	public String[] scanForNew(Device device, Date lastScanTimestamp) 
	{
		PushFilter myFilter = this.pushFilters.get(device.getIdentifier());
		if(myFilter == null)
		{
			return null;
		}
		
		
		List<String> pushme = new ArrayList<String>();
		
		//Read the inbox
		List<MailBean> inbox = this.mailProcessor.readInbox();
		
		
		for(MailBean local:inbox)
		{
			String oid = local.getOid();
			if(!myFilter.isOnDevice(oid))
			{
				pushme.add(oid);
				myFilter.addToFilter(oid);
			}
			
			log.debug("*******Push Checking****************************");
			log.debug("OID: "+oid);
		}
		
		String[] returnMe = pushme.toArray(new String[]{});
		for(String push:returnMe)
		{
			log.info("*******Pushing****************************");
			log.info("OID: "+push);
		}
		
		return returnMe;
	}
	
	public String[] scanForDeletions(Device device, Date lastScanTimestamp) 
	{	
		return null;
	}

	public String[] scanForUpdates(Device device, Date lastScanTimestamp) 
	{	
		//Not Applicable
		return null;
	}
	//----------------------------------------------------------------------------------
	/**
	 * Initializes a PushFilter with oids that must not be sent as Push notifications
	 * 
	 * These are Email instances that are already synchronized with the device
	 * 
	 * Notifications should only be sent for Email instances that are newly arrived on
	 * the Mail server
	 */
	private void initPushFilter(String deviceId,List<MailBean> inbox)
	{
		if(this.pushFilters.get(deviceId) == null)
		{
			PushFilter pushFilter = new PushFilter(deviceId);
			for(MailBean local:inbox)
			{
				String oid = local.getOid();
				pushFilter.addToFilter(oid);
			}
			this.pushFilters.put(deviceId, pushFilter);
		}
	}
}
