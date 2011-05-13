/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileContainer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

import org.openmobster.core.common.XMLUtilities;
import org.openmobster.core.mobileObject.xml.MobileObjectSerializer;
import org.openmobster.core.services.MobileObjectMonitor;

import org.openmobster.server.api.model.MobileBean;
import org.openmobster.server.api.model.Channel;
import org.openmobster.server.api.model.ChannelInfo;
import org.openmobster.server.api.model.MobileBeanId;


/**
 * @author openmobster@gmail.com
 */
public class SaveMobileBean implements ContainerService 
{
	private static Logger log = Logger.getLogger(SaveMobileBean.class);
	
	private String id;
	private MobileObjectMonitor monitor;
	private MobileObjectSerializer serializer;
	
	public SaveMobileBean()
	{
		
	}
	
	public void start()
	{
		
	}
	
	public void stop()
	{
		
	}
	
	public String getId() 
	{
		return id;
	}

	public void setId(String id) 
	{
		this.id = id;
	}
		
	public MobileObjectMonitor getMonitor() 
	{
		return monitor;
	}

	public void setMonitor(MobileObjectMonitor monitor) 
	{
		this.monitor = monitor;
	}
		
	public MobileObjectSerializer getSerializer() 
	{
		return serializer;
	}

	public void setSerializer(MobileObjectSerializer serializer) 
	{
		this.serializer = serializer;
	}
	//---------------------------------------------------------------------------------------------------
	public InvocationResponse execute(Invocation invocation) throws InvocationException
	{
		try
		{
			InvocationResponse response = InvocationResponse.getInstance();
			
			String connectorId = invocation.getConnectorId();
			String mappedObjectId = invocation.getBeanId();
			
			//Access the Channel in question
			Channel connector = monitor.lookup(connectorId);
			if(connector == null)
			{
				response.setStatus(InvocationResponse.STATUS_NOT_FOUND);
				return response;
			}
			
			ChannelInfo connectorInfo = (ChannelInfo)connector.getClass().
			getAnnotation(ChannelInfo.class);
			Class objectClazz = Thread.currentThread().
			getContextClassLoader().
			loadClass(connectorInfo.mobileBeanClass());
			
			String serializedBean = invocation.getSerializedBean();			
			if(!serializedBean.contains("createdOnDevice"))
			{
				//Object LifeCycle exists
				String beanId = this.saveSerializedObject(connector, objectClazz, serializedBean, 
				mappedObjectId);	
				response.setBeanId(beanId);
			}
			else
			{
				//New Object LifeCycle needs to be established...since this object
				//came into existence on the device, and needs to sync up with the server
				String beanId = this.saveDeviceObject(connector, objectClazz, serializedBean, 
				mappedObjectId);
				response.setBeanId(beanId);
			}
			
			return response;
		}
		catch(Throwable t)
		{
			throw new InvocationException(t.getMessage(), t);
		}
	}
	//---------------------------------------------------------------------------------------------------
	private String saveSerializedObject(Channel connector,
	Class objectClazz, String objectXml, String mappedRecordId) throws Exception
	{		
		MobileBean object = (MobileBean)this.serializer.deserialize(objectClazz, objectXml);
		
		//Get the Id of the object being saved
		String objectId = null;						
		Field[] declaredFields = objectClazz.getDeclaredFields();			
		for(Field field: declaredFields)
		{		
			Annotation id = field.getAnnotation(MobileBeanId.class);
			if(id != null)
			{
				//This field signifies unique Identifier of this object
				objectId = BeanUtils.getProperty(object, field.getName());
			}
		}
		
		if(objectId != null && objectId.trim().length() > 0)
		{
			//Map the objectId to its serverside equivalent if necessary
			if(mappedRecordId != null && mappedRecordId.trim().length()>0)
			{
				objectId = mappedRecordId;
			}
			
			if(connector.read(objectId) != null)
			{
				connector.update(object);
				return objectId;
			}
		}
		
		//If I get here, new instance of this object needs to be created in storage
		String newId = connector.create(object);		
		return newId;
	}
	
	private String saveDeviceObject(Channel connector,
	Class objectClazz, String deviceXml, String mappedRecordId) throws Exception
	{		
		//Parse the mobile Object and populate the object
		MobileBean record = (MobileBean)this.serializer.deserialize(objectClazz, deviceXml);
		Document root = XMLUtilities.parse(deviceXml);
		
		//Set the recordId
		Element idElement = (Element)root.getElementsByTagName("recordId").item(0);
		
		String recordId = idElement.getTextContent();
		//Map this to the serverside equivalent if necessary
		if(mappedRecordId != null && mappedRecordId.trim().length()>0)
		{
			recordId = mappedRecordId;
		}
		
		
		Field[] declaredFields = objectClazz.getDeclaredFields();			
		for(Field field: declaredFields)
		{		
			Annotation id = field.getAnnotation(MobileBeanId.class);
			if(id != null)
			{
				//This field signifies unique Identifier of this object
				BeanUtils.setProperty(record, field.getName(), recordId);
			}
		}
					
		String id = null;
		if(connector.read(recordId) != null)
		{
			//Update
			id = recordId;
			connector.update(record);
		}
		else
		{
			//Create
			id = connector.create(record);
		}
				
		return id;
	}
}
