/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.dataService.server;

import java.io.ByteArrayOutputStream;

import org.apache.log4j.Logger;

/**
 *
 * @author openmobster@gmail.com
 */
public final class PayloadController
{
	private static Logger log = Logger.getLogger(PayloadController.class);
	
	private ByteArrayOutputStream buffer;
	private String payload;
	
	public PayloadController()
	{
	}
	
	public String getPayload()
	{
		return this.payload;
	}
	
	public void openBuffer()
	{
		try
		{
			this.buffer = new ByteArrayOutputStream();
		}
		catch(Exception e)
		{
			log.error(this, e);
			
			try{this.buffer.close();}catch(Exception ex){}
			
			throw new RuntimeException(e);
		}
	}
	
	public void writeBuffer(byte[] packet)
	{
		try
		{
			this.buffer.write(packet);
			this.buffer.flush();
		}
		catch(Exception e)
		{
			log.error(this, e);
			
			try{this.buffer.close();}catch(Exception ex){}
			
			throw new RuntimeException(e);
		}
	}
	
	public void closeBuffer()
	{
		try
		{
			this.payload = this.buffer.toString("UTF-8");
			
			this.buffer.close();
			this.buffer = null;
		}
		catch(Exception e)
		{
			log.error(this, e);
			
			try{this.buffer.close();}catch(Exception ex){}
			
			throw new RuntimeException(e);
		}
	}
}
