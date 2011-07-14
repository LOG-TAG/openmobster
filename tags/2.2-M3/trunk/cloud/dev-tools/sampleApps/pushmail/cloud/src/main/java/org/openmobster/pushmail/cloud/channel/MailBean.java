/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.pushmail.cloud.channel;

import org.openmobster.server.api.model.MobileBean;
import org.openmobster.server.api.model.MobileBeanId;

/**
 * 'Mail' models the Domain data for an Email object
 * 
 * @author openmobster@gmail.com
 */
public final class MailBean implements MobileBean
{
	@MobileBeanId
	private String oid; //unique identifier of the bean
	
	private String from;
	private String to;
	private String subject;
	private String message;
	private String receivedOn;
	
	public MailBean()
	{
		
	}
	
	
	public String getOid()
	{
		return oid;
	}

	public void setOid(String oid)
	{
		this.oid = oid;
	}

	public String getFrom()
	{
		return from;
	}

	public void setFrom(String from)
	{
		this.from = from;
	}

	public String getTo()
	{
		return to;
	}

	public void setTo(String to)
	{
		this.to = to;
	}

	public String getSubject()
	{
		return subject;
	}

	public void setSubject(String subject)
	{
		this.subject = subject;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public String getReceivedOn()
	{
		return receivedOn;
	}

	public void setReceivedOn(String receivedOn)
	{
		this.receivedOn = receivedOn;
	}
}
