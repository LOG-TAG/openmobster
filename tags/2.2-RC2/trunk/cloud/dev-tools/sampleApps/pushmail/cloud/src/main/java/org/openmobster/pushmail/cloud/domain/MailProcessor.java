/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.pushmail.cloud.domain;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;
import java.text.SimpleDateFormat;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;

import org.openmobster.pushmail.cloud.channel.MailBean;
import org.openmobster.core.common.Utilities;

/**
 * 
 * @author openmobster@gmail.com
 */
public final class MailProcessor
{
	private String storeAddress; //Eg: imap.gmail.com
	private String protocol; //Eg: imaps
	private String account; // your email account
	private String password;// your email password
	
	public MailProcessor()
	{
		
	}
	
	public void start()
	{
		
	}
	
	public void stop()
	{
		
	}
	
	public String getAccount()
	{
		return account;
	}

	public void setAccount(String account)
	{
		this.account = account;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}
	
	
    public String getStoreAddress()
	{
		return storeAddress;
	}

	public void setStoreAddress(String storedAddress)
	{
		this.storeAddress = storedAddress;
	}

	public String getProtocol()
	{
		return protocol;
	}

	public void setProtocol(String protocol)
	{
		this.protocol = protocol;
	}

	//------------------------------------------------------------------------------------------------------------------------------
	public List<MailBean> readInbox()
	{
		try
		{
			List<MailBean> inbox = new ArrayList<MailBean>();
			
			Properties props = System.getProperties();
			props.setProperty("mail.store.protocol", this.protocol);

			Session session = Session.getDefaultInstance(props, null);
			Store store = session.getStore(this.protocol);
			store.connect(this.storeAddress, this.account, this.password);

			Folder myInbox = store.getFolder("Inbox");
			myInbox.open(Folder.READ_ONLY);
			Message messages[] = myInbox.getMessages();
			for (Message message : messages)
			{
				Address[] from = message.getFrom();
				Date receivedDate = message.getReceivedDate();
				String subject = message.getSubject();
				Object content = message.getContent();
				String mail = this.extractMessage(content);
				SimpleDateFormat format = new SimpleDateFormat("EEE, MMM d, yyyy");
				String[] messageId = message.getHeader("Message-ID");
				
				MailBean local = new MailBean();
				local.setOid(messageId[0]);
				local.setFrom(from[0].toString());
				local.setTo(this.storeAddress);
				local.setSubject(subject);
				local.setReceivedOn(format.format(receivedDate));
				local.setMessage(Utilities.encodeBinaryData(mail.getBytes()));
				
				inbox.add(local);
			}
		
			return inbox;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public void delete(MailBean mailBean)
	{
		try
		{
			String oid = mailBean.getOid();
			
			Properties props = System.getProperties();
			props.setProperty("mail.store.protocol", this.protocol);

			Session session = Session.getDefaultInstance(props, null);
			Store store = session.getStore(this.protocol);
			store.connect(this.storeAddress, this.account, this.password);

			Folder myInbox = store.getFolder("Inbox");
			myInbox.open(Folder.READ_WRITE);
			Message messages[] = myInbox.getMessages();
			for (Message message : messages)
			{
				String[] messageId = message.getHeader("Message-ID");
				String localOid = messageId[0];
				
				if(localOid.equals(oid))
				{
					message.setFlag(Flags.Flag.DELETED, true);
				}
			}
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	private String extractMessage(Object content) throws Exception
	{
		if(content instanceof String)
		{
			return (String)content;
		}
		else if(content instanceof Multipart)
		{
			Multipart local = (Multipart)content;
			int parts = local.getCount();
			
			for(int i=0; i<parts; i++)
			{
				BodyPart b = local.getBodyPart(i);
				return this.extractMessage(b.getContent());
			}
		}
		return null;
	}
}
