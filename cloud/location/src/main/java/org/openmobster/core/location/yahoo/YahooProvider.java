/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.location.yahoo;

import java.util.List;
import java.util.ArrayList;
import java.net.URLEncoder;

import org.apache.log4j.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import org.openmobster.core.location.AddressSPI;
import org.openmobster.core.location.GeoCodeProvider;
import org.openmobster.core.location.LocationSPIException;

/**
 *
 * @author openmobster@gmail.com
 */
public final class YahooProvider implements GeoCodeProvider
{
	private static Logger log = Logger.getLogger(YahooProvider.class);
	
	private String appId;
	private Deserializer deserializer;
	
	public void start()
	{
		
	}
	
	public void stop()
	{
		
	}
	
	public String getAppId()
	{
		return appId;
	}

	public void setAppId(String appId)
	{
		this.appId = appId;
	}
	
	public Deserializer getDeserializer()
	{
		return deserializer;
	}

	public void setDeserializer(Deserializer deserializer)
	{
		this.deserializer = deserializer;
	}
	//--------------------------------------------------------------------------------------------------------
	public List<AddressSPI> reverseGeoCode(String latitude, String longitude) throws LocationSPIException
	{
		try
		{
			List<AddressSPI> addresses = new ArrayList<AddressSPI>();
			String location = URLEncoder.encode(latitude+" "+longitude, "UTF-8");
			String url = "http://where.yahooapis.com/geocode?gflags=R&appid="+appId;
			url += "&location="+ location;
			
			//setup the request object
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(url);
			
			//send the request
			HttpResponse response = client.execute(request);
			
			//read the response
			String xml = null;
			HttpEntity entity = response.getEntity();
			if(entity != null)
			{
				xml = EntityUtils.toString(entity);
			}
			
			if(xml == null || xml.trim().length()==0)
			{
				return addresses;
			}
			
			ResultSet resultSet = this.deserializer.deserialize(xml);
			if(resultSet.isHasError())
			{
				return addresses;
			}
			
			List<Result> results = resultSet.getResults();
			if(results == null || results.isEmpty())
			{
				return addresses;
			}
			
			for(Result local:results)
			{
				AddressSPI address = new AddressSPI();
				YahooAddress resultAddress = local.getAddress();
				
				address.setStreet(resultAddress.getLine1());
				address.setCity(resultAddress.getCity());
				address.setState(resultAddress.getState());
				address.setCountry(resultAddress.getCountry());
				address.setZipCode(resultAddress.getZipCode());
				address.setCounty(resultAddress.getCounty());
				address.setPostal(resultAddress.getPostal());
				
				//some address oriented meta data
				address.setLatitude(local.getLatitude());
				address.setLongitude(local.getLongitude());
				address.setRadius(local.getRadius());
				address.setWoeid(local.getWoeid());
				address.setWoetype(local.getWoetype());
				
				addresses.add(address);
			}
			
			return addresses;
		}
		catch(Throwable t)
		{
			log.error("", t);
			
			throw new LocationSPIException(t);
		}
	}
}
