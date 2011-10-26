/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.location;

import junit.framework.TestCase;
import org.apache.log4j.Logger;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.methods.HttpGet;

import org.apache.http.HttpResponse;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author openmobster@gmail.com
 */
public class TestPlaceFinderTestDrive extends TestCase 
{
	private static Logger log = Logger.getLogger(TestPlaceFinderTestDrive.class);
	
	public void testGeoCodeAddress() throws Exception
	{
		log.info("Starting PlaceFinder Address GeoCoding........");
		
		String appId = "91XdMe7k";
		String url = "http://where.yahooapis.com/geocode?location=701+First+Ave,+Sunnyvale,+CA&appid="+appId+"&flags=J";
		
		//setup the request object
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		
		//send the request
		HttpResponse response = client.execute(request);
		
		//read the response
		String json = null;
		HttpEntity entity = response.getEntity();
		if(entity != null)
		{
			json = EntityUtils.toString(entity);
		}
		
		//process the JSON into an object model
		System.out.println(json);
	}
}
