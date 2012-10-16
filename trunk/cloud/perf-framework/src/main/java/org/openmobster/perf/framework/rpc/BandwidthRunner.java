/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.perf.framework.rpc;

import org.apache.log4j.Logger;

import org.openmobster.cloud.api.rpc.MobileServiceBean;
import org.openmobster.cloud.api.rpc.Request;
import org.openmobster.cloud.api.rpc.Response;
import org.openmobster.cloud.api.rpc.ServiceInfo;

/**
 *
 * @author openmobster@gmail.com
 */
@ServiceInfo(uri="/perf-framework/bandwidthrunner")
public class BandwidthRunner implements MobileServiceBean
{
	private static Logger log = Logger.getLogger(BandwidthRunner.class);
	
	public void start()
	{
		log.info("******************************************");
		log.info("BandwidthRunner successfully started.......");
		log.info("*******************************************");
	}
	
	@Override
	public Response invoke(Request request)
	{
		Response response = new Response();
		
		String payload = request.getAttribute("payload");
		response.setAttribute("payload", payload);
		
		return response;
	}
}
