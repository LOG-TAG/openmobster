/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.core.mobileCloud.rimos.service.database.app2;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import test.openmobster.core.mobileCloud.rimos.testsuite.Test;

/**
 * @author openmobster@gmail.com
 *
 */
public class TestConcurrency extends Test
{
	private Vector boot = null;
	
	public void setUp() 
	{
		//Create the database 
		PersistentObject database = PersistentStore.getPersistentObject(1234567890L);
		Object data = database.getContents();
		if(data == null)
		{
			synchronized(PersistentStore.getSynchObject())
			{
				data = database.getContents();
				
				if(data == null)
				{
					Hashtable contents = new Hashtable();
					
					Vector bootValue = new Vector();
					
					contents.put("boot", bootValue);	
					
					database.setContents(contents);
					database.commit();
				}				
				this.boot = (Vector)((Hashtable)database.getContents()).get("boot");
			}
		}
		else
		{
			Hashtable contents = (Hashtable)data;
			this.boot = (Vector)contents.get("boot");			
		}
	}
	
	public void runTest()
	{
		try
		{						
			synchronized(this.boot)
			{
				System.out.println("----------------------------------------------");
				System.out.println(this.getInfo()+" I am in-----------------------");
				Enumeration bootValues = this.boot.elements();
				Hashtable record = null;
				if(bootValues.hasMoreElements())
				{
					record = (Hashtable)bootValues.nextElement();
					
					Enumeration keys = record.keys();
					while(keys.hasMoreElements())
					{
						String key = (String)keys.nextElement();
						System.out.println(this.getInfo()+" Name="+key+", Value="+record.get(key));
					}
				}
				else
				{
					record = new Hashtable();
					this.boot.addElement(record);
				}
				
				Thread.currentThread().sleep(5000);
				
				record.put(String.valueOf(System.currentTimeMillis()), this.getInfo());
			}
			PersistentStore.getPersistentObject(1234567890L).commit();
		}
		catch(Exception e)
		{
			throw new RuntimeException(e.getMessage());
		}
	}
}
