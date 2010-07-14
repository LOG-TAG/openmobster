/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.dev.tools.rimos;

import java.util.Vector;

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
    	this.startup();
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
    		
    		Configuration conf = Configuration.getInstance();
    		
    		System.out.println("-------------------------------------------------------");
    		System.out.println("Dev Cloud Successfully Activated");
    		System.out.println("Device Id: "+conf.getDeviceId());
    		System.out.println("Account: "+conf.getEmail());
    		System.out.println("IsActive: "+conf.isActive());
    		System.out.println("-------------------------------------------------------");
    	}
    	catch(Exception e)
    	{
    		System.out.println("Exception---------------------------------------------- ");
    		System.out.println("Exception: "+e.toString());
    		System.out.println("Message: "+e.getMessage());
    		System.out.println("-------------------------------------------------------");
    	}
    }
    
    private void startup() throws Exception
    {
    	//System.out.println("Starting the DeviceContainer..");
    	DeviceContainer.getInstance().startup();
    	
    	//Make a local copy of registered channels
    	//System.out.println("Copying the channels...........");
    	Configuration configuration = Configuration.getInstance();
    	Vector myChannels = configuration.getMyChannels();
    	
    	//drop the configuration so new one will be generated
    	//System.out.println("Dropping the configuration.......");
    	configuration.stop();
    	Database database = Database.getInstance();
    	database.dropTable(Database.provisioning_table);
    	
    	//restart the configuration
    	//System.out.println("Restarting the configuration.......");
    	configuration.start();
    	
    	//Now reload the registered channels if any were found
    	//System.out.println("Reloading the channels.......");
    	if(myChannels != null && myChannels.size()>0)
    	{
	    	configuration = Configuration.getInstance();
	    	int size = myChannels.size(); 
	    	for(int i=0; i<size; i++)
	    	{
	    		configuration.addMyChannel((String)myChannels.elementAt(i));
	    	}
	    	configuration.save();
    	}
    	
    	//System.out.println("Startup successfull.............");
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



