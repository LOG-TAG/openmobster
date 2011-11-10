/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.location;

import java.util.Set;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray;
import org.openmobster.cloud.api.location.LocationContext;
import org.openmobster.cloud.api.location.Request;
import org.openmobster.cloud.api.location.Response;
import org.openmobster.cloud.api.location.Address;
import org.openmobster.cloud.api.location.Place;
import org.openmobster.core.common.XMLUtilities;
import org.openmobster.core.common.errors.SystemException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author openmobster@gmail.com
 */
public final class PayloadHandler
{
	public PayloadHandler()
	{
		
	}
	
	public LocationContext deserializeRequest(String payload)
	{
		try
		{
			Document document = XMLUtilities.parse(payload);
			
			//Request
			Element locationRequest = (Element)document.getElementsByTagName("location-request").item(0);
			
			//Service
			Element serviceElement = (Element)locationRequest.getElementsByTagName("service").item(0);
			String service = serviceElement.getTextContent();
			
			//Request Payload
			Element requestPayloadElement = (Element)locationRequest.getElementsByTagName("request-payload").item(0);
			String requestPayload = requestPayloadElement.getTextContent();
			
			//Location Payload
			Element locationPayloadElement = (Element)locationRequest.getElementsByTagName("location-payload").item(0);
			String locationPayload = locationPayloadElement.getTextContent();
			
			//Assemble the Request object
			Request request = new Request(service);
			JSONParser parser = new JSONParser();
			JSONObject parsedRequest = (JSONObject)parser.parse(requestPayload);
			
			//get the keys
			Set<String> names = parsedRequest.keySet();
			for(String name:names)
			{
				Object value = parsedRequest.get(name);
				
				if(value instanceof JSONArray)
				{
					JSONArray array = (JSONArray)value;
					List<String> list = new ArrayList<String>();
					
					//list attribute
					int length = array.size();
					for(int i=0; i<length; i++)
					{
						String local = (String)array.get(i);
						list.add(local);
					}
					
					request.setListAttribute(name, list);
				}
				else if(value instanceof JSONObject)
				{
					//map attribute
					JSONObject map = (JSONObject)value;
					Map<String,String> mapAttr = new HashMap<String,String>();
					
					Set<String> keys = map.keySet();
					for(String key:keys)
					{
						String local = (String)map.get(key);
						mapAttr.put(key, local);
					}
					
					request.setMapAttribute(name, mapAttr);
				}
				else if(value instanceof String)
				{
					//string attribute
					request.setAttribute(name, (String)value);
				}
			}
			
			//Assemble the LocationContext
			LocationContext locationContext = LocationContext.getInstance();
			parsedRequest = (JSONObject)parser.parse(locationPayload);
			locationContext.setLatitude((String)parsedRequest.get("latitude"));
			locationContext.setLongitude((String)parsedRequest.get("longitude"));
			
			locationContext.setAttribute("request", request);
			
			return locationContext;
		}
		catch(Exception e)
		{
			throw new SystemException(e.getMessage());
		}
	}
	
	public String serializeResponse(LocationContext locationContext)
	{
		Response response = (Response)locationContext.getAttribute("response");
		
		//Prepare the response JSONObject
		JSONObject responseJSON = new JSONObject();
		String[] names = response.getNames();
		if(names != null && names.length>0)
		{
			for(String name:names)
			{
				Object value = response.get(name);
				responseJSON.put(name, value);
			}
		}
		
		//Prepare the location context JSONObject
		JSONObject locationJSON = new JSONObject();
		
		//Places
		List<Place> places = locationContext.getNearbyPlaces();
		if(places != null && !places.isEmpty())
		{
			JSONArray placesJSON = new JSONArray();
			for(Place place:places)
			{
				JSONObject placeJSON = this.serializePlace(place);
				placesJSON.add(placeJSON);
			}
			locationJSON.put("places", placesJSON);
		}
		
		//Place Details
		Place placeDetails = locationContext.getPlaceDetails();
		if(placeDetails != null)
		{
			JSONObject placeDetailsJSON = this.serializePlace(placeDetails);
			locationJSON.put("placeDetails", placeDetailsJSON);
		}
		
		//Address
		Address address = locationContext.getAddress();
		if(address != null)
		{
			JSONObject addressJSON = this.serializeAddress(address);
			locationJSON.put("address", addressJSON);
		}
		
		//Add the rest of the context
		names = locationContext.getNames();
		if(names != null && names.length>0)
		{
			for(String name:names)
			{
				Object value = locationContext.getAttribute(name);
				if(value instanceof String)
				{
					locationJSON.put(name, (String)value);
				}
			}
		}
		
		String responsePayload = responseJSON.toJSONString();
		String locationPayload = locationJSON.toJSONString();
		
		StringBuilder buffer = new StringBuilder();
		buffer.append("<location-response>\n");
		buffer.append("<response-payload>"+XMLUtilities.addCData(responsePayload)+"</response-payload>\n");
		buffer.append("<location-payload>"+XMLUtilities.addCData(locationPayload)+"</location-payload>\n");
		buffer.append("</location-response>\n");
		
		String xml = buffer.toString();
		
		return xml;
	}
	//---------------------------------------------------------------------------------------------
	private JSONObject serializePlace(Place place)
	{
		JSONObject json = new JSONObject();
		
		if(place.getAddress() != null && place.getAddress().trim().length()>0)
		{
			json.put("address", place.getAddress());
		}
		
		return json;
	}
	
	private JSONObject serializeAddress(Address address)
	{
		JSONObject json = new JSONObject();
		
		if(address.getStreet() != null && address.getStreet().trim().length()>0)
		{
			json.put("street", address.getStreet());
		}
		
		return json;
	}
}
