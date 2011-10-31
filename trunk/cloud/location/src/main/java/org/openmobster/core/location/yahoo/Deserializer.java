/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.location.yahoo;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.openmobster.core.common.XMLUtilities;

/**
 *
 * @author openmobster@gmail.com
 */
public final class Deserializer
{
	public Deserializer()
	{
		
	}
	
	public ResultSet deserialize(String xml) throws Exception
	{
		if(xml == null || xml.trim().length()==0)
		{
			throw new IllegalArgumentException("Invalid Xml data!!");
		}
		
		Document root = XMLUtilities.parse(xml);
		
		Element resultSetElement = (Element)root.getElementsByTagName("ResultSet").item(0);
		ResultSet resultSet = new ResultSet();
		
		//Find hasError
		Element errorElement = (Element)resultSetElement.getElementsByTagName("Error").item(0);
		String error = errorElement.getTextContent();
		if(!error.equals("0"))
		{
			resultSet.setHasError(true);
			return resultSet;
		}
		
		//Result Nodes
		NodeList resultNodes = resultSetElement.getElementsByTagName("Result");
		if(resultNodes != null && resultNodes.getLength()>0)
		{
			int length = resultNodes.getLength();
			for(int i=0; i<length; i++)
			{
				Element resultElement = (Element)resultNodes.item(i);
				Result result = new Result();
				YahooAddress address = new YahooAddress();
				
				//latitude
				String latitude = resultElement.getElementsByTagName("latitude").item(0).getTextContent();
				result.setLatitude(latitude);
				
				//longitude
				String longitude = resultElement.getElementsByTagName("longitude").item(0).getTextContent();
				result.setLongitude(longitude);
				
				//radius
				String radius = resultElement.getElementsByTagName("radius").item(0).getTextContent();
				result.setRadius(radius);
				
				//woeid
				String woeid = resultElement.getElementsByTagName("woeid").item(0).getTextContent();
				result.setWoeid(woeid);
				
				//woetype
				String woetype = resultElement.getElementsByTagName("woetype").item(0).getTextContent();
				result.setWoetype(woetype);
				
				//quality
				String quality = resultElement.getElementsByTagName("quality").item(0).getTextContent();
				address.setQuality(Integer.parseInt(quality));
				
				//line1
				String line1 = resultElement.getElementsByTagName("line1").item(0).getTextContent();
				address.setLine1(line1);
				
				//line2
				String line2 = resultElement.getElementsByTagName("line2").item(0).getTextContent();
				address.setLine2(line2);
				
				//line3
				String line3 = resultElement.getElementsByTagName("line3").item(0).getTextContent();
				address.setLine3(line3);
				
				//line4
				String line4 = resultElement.getElementsByTagName("line4").item(0).getTextContent();
				address.setLine4(line4);
				
				//city
				String city = resultElement.getElementsByTagName("city").item(0).getTextContent();
				address.setCity(city);
				
				//county
				String county = resultElement.getElementsByTagName("county").item(0).getTextContent();
				address.setCounty(county);
				
				//state
				String state = resultElement.getElementsByTagName("state").item(0).getTextContent();
				address.setState(state);
				
				//country
				String country = resultElement.getElementsByTagName("country").item(0).getTextContent();
				address.setCountry(country);
				
				//zipcode
				String zip = resultElement.getElementsByTagName("uzip").item(0).getTextContent();
				address.setZipCode(zip);
				
				//postal
				String postal = resultElement.getElementsByTagName("postal").item(0).getTextContent();
				address.setPostal(postal);
				
				result.setAddress(address);
				resultSet.addResult(result);
			}
			
			resultSet.sort();
		}
		
		return resultSet;
	}
}
