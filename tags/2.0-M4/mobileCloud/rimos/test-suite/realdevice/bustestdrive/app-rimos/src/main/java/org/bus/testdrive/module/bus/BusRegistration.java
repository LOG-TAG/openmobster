/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.bus.testdrive.module.bus;

import java.util.Vector;
import java.util.Enumeration;

import org.bus.testdrive.storage.Record;

/**
 * @author openmobster@gmail.com
 *
 */
public final class BusRegistration 
{
	private long busId;
	private Vector handlerCriteria;
	
	public BusRegistration(long busId)
	{
		this.busId = busId;
		this.handlerCriteria = new Vector();
	}
	
	public BusRegistration(Record record)
	{
		String recordId = record.getRecordId();
		this.busId = Long.parseLong(recordId);
		this.handlerCriteria = new Vector();
		
		Enumeration keys = record.getNames();
		while(keys.hasMoreElements())
		{
			String value = record.getValue((String)keys.nextElement());
			this.handlerCriteria.addElement(value);
		}
	}
	//---------------------------------------------------------------------------------------------------------------------------------------------
	public long getBusId()
	{
		return this.busId;
	}
	
	public void addHandler(InvocationHandler handler)
	{
		this.handlerCriteria.addElement(handler.getUri());
	}
	
	public void removeHandler(InvocationHandler handler)
	{
		this.handlerCriteria.removeElement(handler.getUri());
	}
		
	public boolean canBeHandled(Invocation invocation)
	{		
		String criteria = invocation.getTarget();	
		
		if(this.handlerCriteria.contains(criteria))
		{
			return true;
		}
		
		
		for(int i=0,size=this.handlerCriteria.size();i<size;i++)
		{
			String targetId = (String)this.handlerCriteria.elementAt(i);
			
			if(Bus.doesTargetMatch(targetId, criteria))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public String findHandler(Invocation invocation)
	{
		String handler = null;
		String target = invocation.getTarget();
		
		if(this.handlerCriteria != null)
		{
			for(int i=0,size=this.handlerCriteria.size();i<size;i++)
			{
				String curr = (String)this.handlerCriteria.elementAt(i);
				
				if(curr.equals(target) || Bus.doesTargetMatch(curr, target))
				{
					return curr;
				}
			}
		}
		
		return handler;
	}
	//-----Database related-------------------------------------------------------------------------------------------------------------------------
	public Record getRecord()
	{
		Record record = new Record();
		
		record.setRecordId(String.valueOf(this.busId));
		if(this.handlerCriteria != null)
		{
			for(int i=0,size=this.handlerCriteria.size(); i<size ; i++)
			{
				String curr = (String)this.handlerCriteria.elementAt(i);
				record.setValue("handler["+i+"]", curr);
			}
		}
		
		return record;
	}
}
