/**
 * Copyright (c) {2003,2013} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.mysqlapp.cloud;

import java.io.Serializable;
import org.openmobster.cloud.api.sync.MobileBean;
import org.openmobster.cloud.api.sync.MobileBeanId;

/**
 * This is the data that will be mobilized to the device. Over there it will be accessed using the MobileBean generic API
 * 
 * 
 * 
 * @author openmobster@gmail.com
 */
public class DataObject implements Serializable, MobileBean
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6123909452633042987L;

	private long id; //database oid
	
	@MobileBeanId
	private String syncId; //a unique sync id to identify the object uniquely with the Sync Engine
	private String field1;
	private String field2;
	private String field3;
	private String field4;
	
	public DataObject()
	{
		
	}

	public long getId() 
	{
		return id;
	}

	public void setId(long id) 
	{
		this.id = id;
	}

	public String getSyncId() 
	{
		return syncId;
	}

	public void setSyncId(String syncId) 
	{
		this.syncId = syncId;
	}

	public String getField1() 
	{
		return field1;
	}

	public void setField1(String field1) 
	{
		this.field1 = field1;
	}

	public String getField2() 
	{
		return field2;
	}

	public void setField2(String field2) 
	{
		this.field2 = field2;
	}

	public String getField3() 
	{
		return field3;
	}

	public void setField3(String field3) 
	{
		this.field3 = field3;
	}

	public String getField4() 
	{
		return field4;
	}

	public void setField4(String field4) 
	{
		this.field4 = field4;
	}
}
