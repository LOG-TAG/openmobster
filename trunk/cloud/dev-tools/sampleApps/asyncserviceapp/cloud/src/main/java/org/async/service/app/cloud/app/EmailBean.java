/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.async.service.app.cloud.app;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

/**
 * A simple POJO bean.
 * 
 * @author openmobster@gmail.com
 */
public class EmailBean
{
	private String oid;
	private String from;
	private String to;
	private String subject;
	private Date date;
	
	public EmailBean()
	{
		
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

	public Date getDate()
	{
		return date;
	}

	public void setDate(Date date)
	{
		this.date = date;
	}
	
	public String getOid()
	{
		return oid;
	}

	public void setOid(String oid)
	{
		this.oid = oid;
	}
	
	//------generating mock beans for demo purposes-------------------------------------------------------
	private static List<EmailBean> mockBeans;
	public static List<EmailBean> generateMockBeans()
	{
		if(mockBeans != null)
		{
			return mockBeans;
		}
		
		mockBeans = new ArrayList<EmailBean>();
		
		for(int i=0; i<5; i++)
		{
			EmailBean local = new EmailBean();
			
			local.setOid(""+i);
			local.setFrom("blah"+i+"@gmail.com");
			local.setTo("openmobster@gmail.com");
			local.setSubject("AsyncServiceApp Mock Bean #"+i);
			local.setDate(new Date());
			
			mockBeans.add(local);
		}
		
		return mockBeans;
	}
}
