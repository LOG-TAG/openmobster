/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.crud.cloud.crm.bootstrap;

import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import org.openmobster.server.api.service.Request;
import org.openmobster.server.api.service.Response;
import org.openmobster.server.api.service.MobileServiceBean;
import org.openmobster.server.api.service.ServiceInfo;

/**
 * 
 * @author openmobster@gmail.com
 */
@ServiceInfo(uri="/titanium/module/tester")
public class TitaniumModuleTester implements MobileServiceBean
{
	private static Logger log = Logger.getLogger(TitaniumModuleTester.class);
	
	public TitaniumModuleTester()
	{
		
	}
	
	public void start()
	{
		log.info("--------------------------------------------------------------------------");
		log.info("/titanium/module/tester: was successfully started....");
		log.info("--------------------------------------------------------------------------");
	}
	
	public Response invoke(Request request) 
	{	
		log.info("-------------------------------------------------");
		log.info(this.getClass().getName()+" successfully invoked...");	
		
		this.printRequest(request);
		
		Response response = new Response();
		
		//Other attributes
		response.setAttribute("name", "Jack");
		response.setAttribute("lastname", "Sparrow");
		
		//Customer options
		List<String> customers = new ArrayList<String>();
		customers.add("Apple");
		customers.add("Google");
		customers.add("Oracle");
		customers.add("Microsoft");
		response.setListAttribute("customers", customers);
		
		//Specialist options
		List<String> specialists = new ArrayList<String>();
		specialists.add("Steve J");
		specialists.add("Eric S");
		specialists.add("Larry E");
		specialists.add("Steve B");
		response.setListAttribute("specialists", specialists);
		
		log.info("-------------------------------------------------");
		
		return response;
	}
	
	private void printRequest(Request request)
	{
		String name = request.getAttribute("name");
		String lastname = request.getAttribute("lastname");
		
		List<String> customers = request.getListAttribute("customers");
		
		log.info("****************************");
		log.info("Name: "+name);
		log.info("LastName: "+lastname);
		
		for(String local:customers)
		{
			log.info("Customer: "+local);
		}
		log.info("****************************");
	}
}
