/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.testdrive.server;

import org.apache.log4j.Logger;

import org.apache.mina.common.IoSession;

import org.openmobster.core.dataService.processor.Input;
import org.openmobster.core.dataService.processor.Processor;
import org.openmobster.core.dataService.processor.ProcessorException;
import org.openmobster.core.dataService.Constants;

/**
 * @author openmobster@gmail.com
 *
 */
public class PushTestDrive implements Processor
{
	private static Logger log = Logger.getLogger(PushTestDrive.class);
	
	private String id;
	
	public String getId()
	{
		return this.id;
	}
	
	public void setId(String id)
	{
		this.id = id;
	}
	
	public void start()
	{
		log.info("----------------------------------------------");
		log.info("PushTestDrive service successfully started....");
		log.info("----------------------------------------------");
	}

	public String process(Input input) throws ProcessorException
	{
		String payload = input.getMessage();
		
		log.info("-----------------------------------------------");
		log.info("Push TestDrive: "+payload);
		log.info("-----------------------------------------------");
		
		IoSession session = input.getSession();
		for(int i=0; i<5; i++)
		{
			session.write("command=cometpush");
			session.write(Constants.endOfStream);
		}
		
		return "close";
	}
}
