/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.sync.app;

import java.io.Serializable;

import org.openmobster.server.api.model.MobileBean;
import org.openmobster.server.api.model.MobileBeanId;

/**
 * The 'SyncBean' that will be stored/synchronized with the device
 * 
 * @author openmobster@gmail.com
 */
public class SyncBean implements MobileBean, Serializable
{
	private static final long serialVersionUID = 8484238078447118385L;

	@MobileBeanId
	private String beanId; //uniquely identifies the bean to the sync system	
	private String value1;
	private String value2;
	private String value3;
	
	public SyncBean()
	{
		
	}

	public String getBeanId() 
	{
		return beanId;
	}

	public void setBeanId(String beanId) 
	{
		this.beanId = beanId;
	}

	public String getValue1() 
	{
		return value1;
	}

	public void setValue1(String value1)
	{
		this.value1 = value1;
	}

	public String getValue2() 
	{
		return value2;
	}

	public void setValue2(String value2) 
	{
		this.value2 = value2;
	}

	public String getValue3() 
	{
		return value3;
	}

	public void setValue3(String value3) 
	{
		this.value3 = value3;
	}
}
