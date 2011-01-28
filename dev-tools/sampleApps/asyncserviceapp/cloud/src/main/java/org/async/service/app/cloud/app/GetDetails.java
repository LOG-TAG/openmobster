/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.async.service.app.cloud.app;

import java.util.List;
import java.text.DateFormat;

import org.apache.log4j.Logger;

import org.openmobster.server.api.service.Request;
import org.openmobster.server.api.service.Response;
import org.openmobster.server.api.service.MobileServiceBean;
import org.openmobster.server.api.service.ServiceInfo;

/**
 * Service Bean that will be invoked from the device. It returns the "Email" selected for viewing by the user
 * 
 * @author openmobster@gmail.com
 */
@ServiceInfo(uri="/asyncserviceapp/getdetails")
public class GetDetails implements MobileServiceBean
{
	private static Logger log = Logger.getLogger(GetDetails.class);
	
	public GetDetails()
	{
		
	}
	
	public void start()
	{
		log.info("--------------------------------------------------------------------------");
		log.info("/asyncserviceapp/getdetails: was successfully started......................");
		log.info("--------------------------------------------------------------------------");
	}
	
	public Response invoke(Request request) 
	{	
		Response response = new Response();
		
		//Get the oid of the email selected for viewing
		String oid = request.getAttribute("oid");
		
		//Find the selected email
		List<EmailBean> mockBeans = EmailBean.generateMockBeans();
		for(EmailBean local:mockBeans)
		{
			if(local.getOid().equals(oid))
			{
				DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.SHORT);
				
				//Setup the state of the selected email inside the response object
				response.setAttribute("oid", local.getOid());
				response.setAttribute("from", local.getFrom());
				response.setAttribute("to", local.getTo());
				response.setAttribute("subject", local.getSubject());
				response.setAttribute("date", dateFormat.format(local.getDate()));
			}
		}
		
		return response;
	}
}
