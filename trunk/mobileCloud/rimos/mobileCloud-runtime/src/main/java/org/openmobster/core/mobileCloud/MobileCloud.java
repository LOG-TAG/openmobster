/**
 * Copyright (c) {2003,2009} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud;

import net.rim.device.api.system.Application;

import org.openmobster.core.mobileCloud.sensor.Sensor;
import org.openmobster.core.mobileCloud.kernel.DeviceContainer;


/**
 * 
 */
public final class MobileCloud extends Application
{
	private static MobileCloud singleton;
	
    public MobileCloud()
    { 
    	DeviceContainer.getInstance().startup();
    	
    	//Start a device sensor    	
    	this.startSensor();    	
    }	          
    //-------------------------------------------------------------------------------------------------------------------------------------------
    private void startSensor()
    {
    	Sensor sensor = new Sensor();
    	this.addRadioListener(sensor);
    	this.addSystemListener(sensor);
    }
    //-------------------------------------------------------------------------------------------------------------------------------------------
    public static void main(String[] args)
    {
    	try
    	{
    		MobileCloud.singleton = new MobileCloud();      	
    		MobileCloud.singleton.enterEventDispatcher();
    	}
    	catch(Exception e)
    	{
    		//non-zero means abnormal termination, due to some system error that cannot be handled by the application
    		System.exit(1);
    	}
    }
}



