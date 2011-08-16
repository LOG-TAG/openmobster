/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.showcase.cloud.ajax;

import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import org.openmobster.cloud.api.service.MobileServiceBean;
import org.openmobster.cloud.api.service.Request;
import org.openmobster.cloud.api.service.Response;
import org.openmobster.cloud.api.service.ServiceInfo;

/**
 * Service Bean that will be invoked from the device. It returns a "List" of Email "Subject" values.
 * 
 * @author openmobster@gmail.com
 */
@ServiceInfo(uri="/asyncserviceapp/getlist")
public class GetList implements MobileServiceBean
{
	private static Logger log = Logger.getLogger(GetList.class);
	
	public GetList()
	{
		
	}
	
	public void start()
	{
		log.info("--------------------------------------------------------------------------");
		log.info("/asyncserviceapp/getlist: was successfully started........................");
		log.info("--------------------------------------------------------------------------");
	}
	
	public Response invoke(Request request) 
	{	
		Response response = new Response();
		
		//Get a list emails
		List<EmailBean> mockBeans = EmailBean.generateMockBeans();
		
		//Create a list of subjects to be returned for display
		List<String> subjects = new ArrayList<String>();
		for(EmailBean local:mockBeans)
		{
			subjects.add("id="+local.getOid()+":subject="+local.getSubject());
		}
		
		//Set the information in the response object
		response.setListAttribute("subjects", subjects);
		
		return response;
	}
}
