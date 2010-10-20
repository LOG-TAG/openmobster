/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.bus.testdrive;

import org.bus.testdrive.module.bus.Bus;

import net.rim.device.api.system.Application;

/**
 * @author openmobster@gmail
 * 
 */
public class RemoteBus extends Application
{
	public RemoteBus()
	{
		try
		{								
			Bus bus = Bus.getInstance();
			if(!bus.isActive())
			{
				Bus.getInstance().start();
				Bus.getInstance().register(new RemoteInvocationHandler());
			}
			
			Sensor sensor = new Sensor();
	    	this.addRadioListener(sensor);
	    	this.addSystemListener(sensor);
		}
		catch(Exception e)
		{
			System.out.println("-----------------------------------------------");
			System.out.println("Exception: "+e.getMessage());
			System.out.println("-----------------------------------------------");
		}
	}
	
	public static void main(String[] args)
	{
		Application app = new RemoteBus();
		app.enterEventDispatcher();
	}
}
