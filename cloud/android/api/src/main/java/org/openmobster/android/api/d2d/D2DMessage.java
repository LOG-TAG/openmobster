/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.android.api.d2d;

import java.io.Serializable;

/**
 *
 * @author openmobster@gmail.com
 */
public final class D2DMessage implements Serializable
{
	private String from;
	private String to;
	private String message;
	private String senderDeviceId;
	private String timestamp;
	
	public D2DMessage()
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

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public String getSenderDeviceId()
	{
		return senderDeviceId;
	}

	public void setSenderDeviceId(String senderDeviceId)
	{
		this.senderDeviceId = senderDeviceId;
	}

	public String getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp(String timestamp)
	{
		this.timestamp = timestamp;
	}
	
	public String toString()
	{
		StringBuilder buffer = new StringBuilder();
		
		buffer.append("<d2d-message>\n");
		buffer.append("<message><![CDATA["+this.message+"]]></message>\n");
		buffer.append("<to><![CDATA["+this.to+"]]></to>\n");
		buffer.append("<from><![CDATA["+this.from+"]]></from>\n");
		buffer.append("<sender-device-id><![CDATA["+this.senderDeviceId+"]]></sender-device-id>\n");
		buffer.append("<timestamp><![CDATA["+this.timestamp+"]]></timestamp>\n");
		buffer.append("</d2d-message>\n");
		
		return buffer.toString();
	}
}