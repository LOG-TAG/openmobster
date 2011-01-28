/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.rimos.module.mobileObject;

import java.util.Vector;

import org.openmobster.core.mobileCloud.rimos.util.XMLUtil;

/**
 * @author openmobster@gmail.com
 */
public final class DeviceSerializer 
{
	private static DeviceSerializer singleton;
	
	private DeviceSerializer()
	{
		
	}
	
	public static DeviceSerializer getInstance()
	{
		if(singleton == null)
		{
			singleton = new DeviceSerializer();
		}
		return singleton;
	}
	
	public String serialize(MobileObject mobileObject)
	{		
		StringBuffer xmlBuffer = new StringBuffer();
		
		if(!mobileObject.isCreatedOnDevice())
		{
			xmlBuffer.append("<mobileObject>\n");
		}
		else
		{
			xmlBuffer.append("<mobileObject createdOnDevice='true'>\n");
		}
		xmlBuffer.append("<recordId>"+XMLUtil.cleanupXML(mobileObject.getRecordId())+"</recordId>\n");
		xmlBuffer.append("<serverRecordId>"+XMLUtil.cleanupXML(mobileObject.getServerRecordId())+"</serverRecordId>\n");
		xmlBuffer.append("<object>\n");
		
		//Serialize the Fields
		Vector fields = mobileObject.getFields();
		if(fields != null && !fields.isEmpty())
		{
			xmlBuffer.append("<fields>\n");
			for(int i=0; i<fields.size(); i++)
			{
				Field field = (Field)fields.elementAt(i);
				xmlBuffer.append("<field>\n");
				xmlBuffer.append("<uri>"+field.getUri()+"</uri>\n");
				xmlBuffer.append("<name>"+field.getName()+"</name>\n");
				xmlBuffer.append("<value>"+XMLUtil.cleanupXML(field.getValue())+"</value>\n");
				xmlBuffer.append("</field>\n");
			}
			xmlBuffer.append("</fields>\n");
		}
		
		//Serialize the Array Meta Data
		Vector arrayMetaData = mobileObject.getArrayMetaData();
		if(arrayMetaData != null && !arrayMetaData.isEmpty())
		{
			xmlBuffer.append("<metadata>\n");
			
			for(int i=0; i<arrayMetaData.size(); i++)
			{
				ArrayMetaData metaData = (ArrayMetaData)arrayMetaData.elementAt(i);
				xmlBuffer.append("<array-metadata>\n");
				xmlBuffer.append("<uri>"+metaData.getArrayUri()+"</uri>\n");
				xmlBuffer.append("<array-length>"+metaData.getArrayLength()+"</array-length>\n");
				xmlBuffer.append("<array-class>"+metaData.getArrayClass()+"</array-class>\n");
				xmlBuffer.append("</array-metadata>\n");
			}
			
			xmlBuffer.append("</metadata>\n");
		}
		
		xmlBuffer.append("</object>\n");
		xmlBuffer.append("</mobileObject>\n");
				
		return xmlBuffer.toString();
	}
	
	public MobileObject deserialize(String xml)
	{
		MobileObjectReader reader = new MobileObjectReader();		
		return reader.parse(xml);
	}	
}
