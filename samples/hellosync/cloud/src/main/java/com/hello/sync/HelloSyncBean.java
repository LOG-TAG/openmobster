/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.hello.sync;

import org.openmobster.cloud.api.sync.MobileBean;
import org.openmobster.cloud.api.sync.MobileBeanId;

/**
 * @author openmobster@gmail.com
 */
public class HelloSyncBean implements MobileBean 
{
	private static final long serialVersionUID = 1L;

	@MobileBeanId
	private String oid;
	
	private String message;
	
	public HelloSyncBean()
	{
		
	}

	public String getOid() 
	{
		return oid;
	}

	public void setOid(String oid) 
	{
		this.oid = oid;
	}

	public String getMessage() 
	{
		return message;
	}

	public void setMessage(String message) 
	{
		this.message = message;
	}
}
