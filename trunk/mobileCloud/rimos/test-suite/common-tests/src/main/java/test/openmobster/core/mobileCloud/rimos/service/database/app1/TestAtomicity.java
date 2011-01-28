/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.core.mobileCloud.rimos.service.database.app1;

import java.util.Vector;
import java.util.Enumeration;

import test.openmobster.core.mobileCloud.rimos.testsuite.Test;

import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.system.PersistentObject;

/**
 * @author openmobster@gmail.com
 *
 */
public class TestAtomicity extends Test 
{
	
	public void setUp() 
	{
		//Create the database 
		PersistentObject database = PersistentStore.getPersistentObject((long)this.getClass().getName().hashCode());
		Object data = database.getContents();
		if(data == null)
		{
			database.setContents(new Vector());
			database.commit();
		}
		else
		{
			Vector contents = (Vector)data;
			Enumeration values = contents.elements();
			System.out.println("Printing database contents upon startup---------------------------------");
			while(values.hasMoreElements())
			{
				String value = (String)values.nextElement();
				System.out.println("Value="+value);
			}
		}
	}
	
	public void runTest()
	{
		try
		{
			addRecord();
		}
		catch(Exception e)
		{
			throw new RuntimeException(e.getMessage());
		}
	}		
	
	private void addRecord()
	{
		PersistentObject database = PersistentStore.getPersistentObject((long)this.getClass().getName().hashCode());
		Vector contents = (Vector)database.getContents();
		
		//Perform this as a batch operation minus commit
		synchronized(contents)
		{
			contents.addElement(String.valueOf(System.currentTimeMillis()));
			database.commit();
		}
		
		//Re-Read without commiting
		contents = (Vector)database.getContents();
		
		//Print contents
		Enumeration values = contents.elements();
		System.out.println("Printing database contents in AddRecord---------------------------------");
		while(values.hasMoreElements())
		{
			String value = (String)values.nextElement();
			System.out.println("Value="+value);
		}
		
		Thread t = new Thread(new AsyncAddReader());
		t.start();
	}
	
	private static class AsyncAddReader implements Runnable
	{
		public void run()
		{
			PersistentObject database = PersistentStore.getPersistentObject((long)TestAtomicity.class.getName().hashCode());			
			Vector contents = (Vector)database.getContents();
			
			//Print contents						
			Enumeration values = contents.elements();
			System.out.println("Printing database contents in a Spawned Thread---------------------------------");
			while(values.hasMoreElements())
			{
				String value = (String)values.nextElement();
				System.out.println("Value="+value);
			}
		}
	}
}
