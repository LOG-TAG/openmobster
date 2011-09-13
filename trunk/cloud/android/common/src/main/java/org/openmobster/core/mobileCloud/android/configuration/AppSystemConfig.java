/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.android.configuration;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.util.GenericAttributeManager;
import org.openmobster.core.mobileCloud.android.util.IOUtil;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

/**
 *
 * @author openmobster@gmail.com
 */
public final class AppSystemConfig
{
	private static AppSystemConfig singleton;
	
	private GenericAttributeManager attrMgr;
	
	private AppSystemConfig()
	{
		
	}
	
	public static AppSystemConfig getInstance()
	{
		if(AppSystemConfig.singleton == null)
		{
			synchronized(AppSystemConfig.class)
			{
				if(AppSystemConfig.singleton == null)
				{
					AppSystemConfig.singleton = new AppSystemConfig();
				}
			}
		}
		return AppSystemConfig.singleton;
	}
	
	public static void stop()
	{
		AppSystemConfig.singleton = null;
	}
	
	public synchronized void start()
	{
		try
		{
			this.attrMgr = new GenericAttributeManager();
			
			//parse the openmobster-app.xml
			InputStream is = AppSystemConfig.class.getResourceAsStream("/openmobster-app.xml");
			
			if(is == null)
			{
				//configuration not found
				return;
			}
			
			String xml = new String(IOUtil.read(is));
			
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document root = builder.parse(new ByteArrayInputStream(xml.getBytes()));
			
			//Parse <encryption>true/false</encryption>
			NodeList encryptionNodes = root.getElementsByTagName("encryption");
			if(encryptionNodes != null && encryptionNodes.getLength()>0)
			{
				int length = encryptionNodes.getLength();
				for(int i=0; i<length; i++)
				{
					Element local = (Element)encryptionNodes.item(i);
					String encryption = local.getFirstChild().getNodeValue().trim();
					this.attrMgr.setAttribute("encryption", encryption);
				}
			}
			
			/**
			 * Parse the <push>
			 * 				<launch-activity-class></launch-activity-class>
			 * 				<icon-name></icon-name>
			 * 			 </push>
			 */
			NodeList push = root.getElementsByTagName("push");
			if(push != null && push.getLength()>0)
			{
				Element pushElement = (Element)push.item(0);
				Element launchActivityClass = (Element)pushElement.getElementsByTagName("launch-activity-class").item(0);
				Element iconName = (Element)pushElement.getElementsByTagName("icon-name").item(0);
				
				String pushActivityClass = launchActivityClass.getFirstChild().getNodeValue().trim();
				String pushIconName = iconName.getFirstChild().getNodeValue().trim();
				
				this.attrMgr.setAttribute("launch-activity-class",pushActivityClass);
				this.attrMgr.setAttribute("push-icon-name", pushIconName);
			}
		}
		catch(Throwable e)
		{
			e.printStackTrace(System.out);
			
			SystemException syse = new SystemException(this.getClass().getName(), "init", new Object[]{
				"Exception: "+e.toString(),
				"Message: "+e.getMessage()
			});
			
			throw syse;
		}
	}
	
	public boolean isEncryptionActivated()
	{
		if(this.attrMgr == null)
		{
			this.start();
		}
		
		String encryption = (String)this.attrMgr.getAttribute("encryption");
		
		if(encryption != null && encryption.trim().length()>0)
		{
			if(encryption.equals(""+Boolean.TRUE))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public String getPushLaunchActivityClassName()
	{
		return (String)this.attrMgr.getAttribute("launch-activity-class");
	}
	
	public String getPushIconName()
	{
		return (String)this.attrMgr.getAttribute("push-icon-name");
	}
}
