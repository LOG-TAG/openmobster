/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.dataService.server;

import org.apache.log4j.Logger;

import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;

import org.openmobster.core.dataService.Constants;

/**
 * @author openmobster@gmail.com
 */
public class PayloadFilter extends IoFilterAdapter
{	
	private static Logger log = Logger.getLogger(PayloadFilter.class);
	
	public PayloadFilter()
	{
		
	}
		
	public void messageReceived(NextFilter nextFilter, IoSession session, Object message)
	{
		//log.debug("RAWSocketMsg---------------------------------------------------------------------------");
		//log.debug(message);
		//log.debug("---------------------------------------------------------------------------------------");
		String payloadMessage = ((String)message).trim();
		if(this.processPayload(session, payloadMessage))
		{
			nextFilter.messageReceived(session, message);
			session.removeAttribute(Constants.payload);
			
			//session.write(Constants.status+"="+200+Constants.endOfStream);
			//session.removeAttribute(Constants.payload);
		}
		else
		{
			//payload is still being constructed...no need to go into further processing
			return;
		}
	}	
	
		
	private boolean processPayload(IoSession session, String message)
	{		
		PayloadController payloadController = (PayloadController)session.
		getAttribute(Constants.payload);
		
		if(payloadController == null)
		{
			payloadController = new PayloadController();
			session.setAttribute(Constants.payload,payloadController);
			payloadController.openBuffer();
		}
		
		if(!message.endsWith(Constants.eof))
		{
			payloadController.writeBuffer(message.getBytes());
			return false;
		}
		
		message = message.replace(Constants.eof, "");
		payloadController.writeBuffer(message.getBytes());
		
		payloadController.closeBuffer();
		return true;
	}				
}
