/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.core.mobileContainer;

import org.apache.log4j.Logger;

import org.openmobster.server.api.service.Request;
import org.openmobster.server.api.service.Response;
import org.openmobster.server.api.service.MobileServiceBean;
import org.openmobster.server.api.service.ServiceInfo;

/**
 * @author openmobster@gmail.com
 */
@ServiceInfo(uri="testMobileServiceInvocation")
public class MockMobileBeanService implements MobileServiceBean
{
	private static Logger log = Logger.getLogger(MockMobileBeanService.class);
	
	public MockMobileBeanService()
	{
		
	}
	
	public Response invoke(Request request) 
	{	
		log.info("-------------------------------------------------");
		log.info(this.getClass().getName()+" successfully invoked...");
		log.info("Service Invoked="+request.getService());		
		String[] names = request.getNames();
		for(String name: names)
		{
			log.info(name+"="+request.getAttribute(name));
		}
		log.info("-------------------------------------------------");
		
		Response response = new Response();
		response.setAttribute("param1", "boomerang");
		
		
		return response;
	}
}
