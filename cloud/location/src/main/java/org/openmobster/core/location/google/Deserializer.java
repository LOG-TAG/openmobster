/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.location.google;

import java.util.List;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

import org.openmobster.core.common.XMLUtilities;
import org.openmobster.core.location.PlaceSPI;

/**
 *
 * @author openmobster@gmail.com
 */
public final class Deserializer
{
	public Deserializer()
	{
		
	}
	
	public List<PlaceSPI> deserializeNearByPlaces(String xml) throws Exception
	{
		List<PlaceSPI> places = new ArrayList<PlaceSPI>();
		
		if(xml == null || xml.trim().length()==0)
		{
			throw new IllegalArgumentException("Invalid Xml data!!");
		}
		
		Document root = XMLUtilities.parse(xml);
		
		//Get the Status
		Element statusElement = (Element)root.getElementsByTagName("status").item(0);
		String status = statusElement.getTextContent();
		if(!status.toLowerCase().equalsIgnoreCase("ok"))
		{
			return places;
		}
		
		NodeList resultNodes = root.getElementsByTagName("result");
		if(resultNodes == null || resultNodes.getLength() ==0)
		{
			return places;
		}
		
		int length = resultNodes.getLength();
		for(int i=0; i<length; i++)
		{
			Element resultElement = (Element)resultNodes.item(i);
			PlaceSPI place = new PlaceSPI();
			
			//name
			String name = this.getValue(resultElement, "name");
			place.setName(name);
			
			//vicinity
			String vicinity = this.getValue(resultElement, "vicinity");
			place.setVicinity(vicinity);
			
			//rating
			NodeList ratingNodes = resultElement.getElementsByTagName("rating");
			if(ratingNodes != null && ratingNodes.getLength()>0)
			{
				String rating = ratingNodes.item(0).getTextContent();
				place.setRating(rating);
			}
			
			//icon
			String icon = resultElement.getElementsByTagName("icon").item(0).getTextContent();
			place.setIcon(icon);
			
			//reference
			String reference = resultElement.getElementsByTagName("reference").item(0).getTextContent();
			place.setReference(reference);
			
			//id
			String id = resultElement.getElementsByTagName("id").item(0).getTextContent();
			place.setId(id);
			
			//types
			NodeList typeNodes = resultElement.getElementsByTagName("type");
			if(typeNodes != null && typeNodes.getLength()>0)
			{
				int typeLength = typeNodes.getLength();
				for(int j=0; j<typeLength; j++)
				{
					Element typeElement = (Element)typeNodes.item(j);
					String type = typeElement.getTextContent();
					place.addType(type);
				}
			}
			
			Element geometryElement = (Element)resultElement.getElementsByTagName("geometry").item(0);
			
			//latitude
			String latitude = geometryElement.getElementsByTagName("lat").item(0).getTextContent();
			place.setLatitude(latitude);
			
			//longitude
			String longitude = geometryElement.getElementsByTagName("lng").item(0).getTextContent();
			place.setLongitude(longitude);
			
			//html attribtion
			NodeList htmlAttributionNodes = resultElement.getElementsByTagName("html_attribution");
			if(htmlAttributionNodes != null && htmlAttributionNodes.getLength()>0)
			{
				String htmlAttribution = htmlAttributionNodes.item(0).getTextContent();
				place.setHtmlAttribution(htmlAttribution);
			}
			
			places.add(place);
		}
		
		return places;
	}
	
	public PlaceSPI deserializePlace(String xml) throws Exception
	{
		PlaceSPI place = new PlaceSPI();
		
		if(xml == null || xml.trim().length()==0)
		{
			throw new IllegalArgumentException("Invalid Xml data!!");
		}
		
		Document root = XMLUtilities.parse(xml);
		
		//Get the Status
		Element statusElement = (Element)root.getElementsByTagName("status").item(0);
		String status = statusElement.getTextContent();
		if(!status.toLowerCase().equalsIgnoreCase("ok"))
		{
			return place;
		}
		
		NodeList resultNodes = root.getElementsByTagName("result");
		if(resultNodes == null || resultNodes.getLength() ==0)
		{
			return place;
		}
		
		Element resultElement = (Element)resultNodes.item(0);
		
		//name
		String name = resultElement.getElementsByTagName("name").item(0).getTextContent();
		place.setName(name);
		
		//vicinity
		String vicinity = resultElement.getElementsByTagName("vicinity").item(0).getTextContent();
		place.setVicinity(vicinity);
		
		//rating
		NodeList ratingNodes = resultElement.getElementsByTagName("rating");
		if(ratingNodes != null && ratingNodes.getLength()>0)
		{
			String rating = ratingNodes.item(0).getTextContent();
			place.setRating(rating);
		}
		
		//icon
		String icon = resultElement.getElementsByTagName("icon").item(0).getTextContent();
		place.setIcon(icon);
		
		//reference
		String reference = resultElement.getElementsByTagName("reference").item(0).getTextContent();
		place.setReference(reference);
		
		//id
		String id = resultElement.getElementsByTagName("id").item(0).getTextContent();
		place.setId(id);
		
		//types
		NodeList typeNodes = resultElement.getElementsByTagName("type");
		if(typeNodes != null && typeNodes.getLength()>0)
		{
			int typeLength = typeNodes.getLength();
			for(int j=0; j<typeLength; j++)
			{
				Element typeElement = (Element)typeNodes.item(j);
				String type = typeElement.getTextContent();
				place.addType(type);
			}
		}
		
		Element geometryElement = (Element)resultElement.getElementsByTagName("geometry").item(0);
		
		//latitude
		String latitude = geometryElement.getElementsByTagName("lat").item(0).getTextContent();
		place.setLatitude(latitude);
		
		//longitude
		String longitude = geometryElement.getElementsByTagName("lng").item(0).getTextContent();
		place.setLongitude(longitude);
		
		//formatted_address
		String formatted_address = resultElement.getElementsByTagName("formatted_address").item(0).getTextContent();
		place.setAddress(formatted_address);
		
		//formatted_phone_number
		NodeList phoneNodes = resultElement.getElementsByTagName("formatted_phone_number");
		if(phoneNodes != null && phoneNodes.getLength()>0)
		{
			String formatted_phone_number = phoneNodes.item(0).getTextContent();
			place.setPhone(formatted_phone_number);
		}
		
		//international_phone_number
		String international_phone_number = this.getValue(resultElement, "international_phone_number");
		place.setInternationalPhoneNumber(international_phone_number);
		
		//url
		String url = resultElement.getElementsByTagName("url").item(0).getTextContent();
		place.setUrl(url);
		
		//website
		String website = this.getValue(resultElement, "website");
		place.setWebsite(website);
		
		return place;
	}
	
	private String getValue(Element resultElement, String nodeName)
	{
		NodeList nodes = resultElement.getElementsByTagName(nodeName);
		if(nodes != null && nodes.getLength()>0)
		{
			String value = nodes.item(0).getTextContent();
			return value;
		}
		return null;
	}
}
