/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.crud.cloud.crm.bootstrap;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.crud.cloud.crm.Ticket;
import org.crud.cloud.crm.hibernate.TicketDS;

import org.openmobster.cloud.api.rpc.MobileServiceBean;
import org.openmobster.cloud.api.rpc.Request;
import org.openmobster.cloud.api.rpc.Response;
import org.openmobster.cloud.api.rpc.ServiceInfo;

/**
 * This component is used to demonstrate real time push on the device. This is invoked to simulate a backend push usecase
 * 
 * It creates a new ticket into the database, which is then later 'Pushed' + 'Synced' with the device in real time
 * 
 * @author openmobster@gmail.com
 */
@ServiceInfo(uri="/listen/push")
public class PushListener implements MobileServiceBean
{
	private static Logger log = Logger.getLogger(PushListener.class);
	
	private TicketDS ds;
	private List<String> newTickets;
	
	public PushListener()
	{
		this.newTickets = new ArrayList<String>();
	}
		
	public TicketDS getDs()
	{
		return ds;
	}


	public void setDs(TicketDS ds)
	{
		this.ds = ds;
	}



	public void start()
	{
		log.info("--------------------------------------------------------------------------");
		log.info("/listen/push: was successfully started....");
		log.info("--------------------------------------------------------------------------");
		
		Thread t = new Thread(new AutoTicketGenerator());
		t.start();
	}
	
	public Response invoke(Request request) 
	{	
		log.info("-------------------------------------------------");
		log.info(this.getClass().getName()+" successfully invoked...");		
		
		Response response = new Response();
		
		Ticket local = new Ticket();
		local.setTitle("New Push Issue");
		local.setComment("Push Succeeded...");
		local.setCustomer("Google");
		local.setSpecialist("Eric S");
		
		String ticketId = this.ds.create(local);
		this.newTickets.add(ticketId);
		
		log.info("-------------------------------------------------");
		
		return response;
	}
	
	public synchronized String[] checkUpdates()
	{
		if(this.newTickets != null && !this.newTickets.isEmpty())
		{
			String[] local = this.newTickets.toArray(new String[0]);
			this.newTickets.clear();
			return local;
		}
		return null;
	}
	
	private class AutoTicketGenerator implements Runnable
	{
		public void run()
		{
			try
			{
				log.info("******************************************");
				log.info("AutoTicketGenerator started...............");
				log.info("******************************************");
				while(true)
				{
					File file = new File("/Users/openmobster/experiments/push/crud.sync");
					if(file.exists())
					{
						file.delete();
						PushListener.this.invoke(null);
					}
					
					Thread.sleep(60000);
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
