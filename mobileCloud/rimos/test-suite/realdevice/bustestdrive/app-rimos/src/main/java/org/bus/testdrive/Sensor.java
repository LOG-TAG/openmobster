/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.bus.testdrive;

import org.bus.testdrive.module.bus.Bus;

import net.rim.device.api.system.RadioStatusListener;
import net.rim.device.api.system.SystemListener2;


/**
 * Start a listener that monitors wirless network activity
 * 
 * TODO: Does not apply to WIFI connections...add WIFI support
 * TODO: Implement Battery Sensor
 * 
 * @author openmobster@gmail.com
 *
 */
public final class Sensor implements RadioStatusListener,SystemListener2
{
	//---NetworkSensor----------------------------------------------------------------------------------------------------------------------
	public synchronized void networkStarted(int networkId, int service) 
	{
	}

		
	public synchronized void radioTurnedOff() 
	{	
	}
	
	public synchronized void signalLevel(int level) 
	{	
	}
	//----Power UnpluggedSensor----------------------------------------------------------------------------------------------------------------------
	public synchronized void powerUp() 
	{	
		try
		{
			Bus bus = Bus.getInstance();
			if(!bus.isActive())
			{
				Bus.getInstance().start();
				Bus.getInstance().register(new RemoteInvocationHandler());
			}
		}
		catch(Exception e)
		{														
			System.out.println("-----------------------------------------------");
			System.out.println("Exception: "+e.getMessage());
			System.out.println("-----------------------------------------------");
		}
	}
	
	public synchronized void fastReset() 
	{
		try
		{
			Bus.getInstance().stop();
		}
		catch(Exception e)
		{
			System.out.println("-----------------------------------------------");
			System.out.println("Exception: "+e.getMessage());
			System.out.println("-----------------------------------------------");
		}	
	}

	public synchronized void powerOffRequested(int reason) 
	{
		try
		{
			Bus.getInstance().stop();
		}
		catch(Exception e)
		{
			System.out.println("-----------------------------------------------");
			System.out.println("Exception: "+e.getMessage());
			System.out.println("-----------------------------------------------");
		}		
	}
	
	public synchronized void powerOff() 
	{
		try
		{
			Bus.getInstance().stop();
		}
		catch(Exception e)
		{
			System.out.println("-----------------------------------------------");
			System.out.println("Exception: "+e.getMessage());
			System.out.println("-----------------------------------------------");
		}
	}
	//---------------------------------------------------------------------------------------------------------------------------------------	
	public void networkStateChange(int state) 
	{		
	}

	public void pdpStateChange(int apn, int state, int cause) 
	{		
	}
	//----------------------------------------------------------------------------------------------------------------------------------------
	public void baseStationChange() 
	{		
	}
	
	public void networkServiceChange(int networkId, int service) 
	{		
	}
	
	public void networkScanComplete(boolean success) 
	{		
	}	
	//------------------------------------------------------------------------------------------------------------------------------------------
	public void batteryGood() 
	{				
	}

	public void batteryLow() 
	{				
	}

	public void batteryStatusChange(int status) 
	{				
	}	
	//-----------------------------------------------------------------------------------------------------------------------------------------
	public void backlightStateChange(boolean on) 
	{				
	}

	public void cradleMismatch(boolean mismatch) 
	{				
	}

	
	public void usbConnectionStateChange(int state) 
	{				
	}
}
