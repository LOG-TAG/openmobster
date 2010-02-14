/**
 * Copyright (c) {2003,2009} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.dev.tools.rimos;

import net.rim.device.api.system.Application;

import org.openmobster.core.mobileCloud.sensor.Sensor;
import org.openmobster.core.mobileCloud.kernel.DeviceContainer;
import org.openmobster.core.mobileCloud.rimos.configuration.Configuration;
import org.openmobster.core.mobileCloud.rimos.storage.Database;


/**
 * 
 */
public final class MobileCloud extends Application
{
	private static MobileCloud singleton;
	
    public MobileCloud() throws Exception
    { 
    	DeviceContainer.getInstance().startup();
    	ActivationUtil.parseConfig("/activation.properties");
    	
    	//Start a device sensor    	
    	this.startSensor(); 
    	
    	//auto activates the device...this should be used only in development mode
    	this.activateCloud();
    }	          
    //-------------------------------------------------------------------------------------------------------------------------------------------
    private void startSensor()
    {
    	Sensor sensor = new Sensor();
    	this.addRadioListener(sensor);
    	this.addSystemListener(sensor);
    }
    
    private void activateCloud()
    {
    	try
    	{
    		ActivationUtil.activateDevice();
    		
    		//System.out.println("-------------------------------------------------------");
    		//System.out.println("Dev Cloud Successfully Activated");
    		//System.out.println("Device Id: "+Configuration.getInstance().getDeviceId());
    		//System.out.println("Account: "+Configuration.getInstance().getEmail());
    		//System.out.println("-------------------------------------------------------");
    	}
    	catch(Exception e)
    	{
    		//System.out.println("Exception---------------------------------------------- ");
    		//System.out.println("Exception: "+e.toString());
    		//System.out.println("Message: "+e.getMessage());
    		//System.out.println("-------------------------------------------------------");
    	}
    }
    //-------------------------------------------------------------------------------------------------------------------------------------------
    public static void main(String[] args)
    {
    	try
    	{
    		Database database = Database.getInstance();
        	database.dropTable(Database.provisioning_table);
        	
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



