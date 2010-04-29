/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.sensor;

import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.RadioStatusListener;
import net.rim.device.api.system.SystemListener2;

import org.openmobster.core.mobileCloud.rimos.configuration.Configuration;
import org.openmobster.core.mobileCloud.rimos.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.rimos.errors.SystemException;
import org.openmobster.core.mobileCloud.kernel.DeviceContainer;
import org.openmobster.core.mobileCloud.rimos.module.connection.NotificationListener;

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
	/*public synchronized void networkServiceChange(int networkId, int service) 
	{	
		try
		{
			if(!DeviceContainer.getInstance().isContainerActive())
			{
				return;
			}
			
			if(!Configuration.getInstance().isActive())
			{
				return;				
			}
			
			//Detect if the radio is just turning on
			if((service & RadioInfo.STATE_TURNING_ON) > 0)
			{
				NotificationListener notify = NotificationListener.getInstance();
				if(notify!=null)
				{
					notify.restart();
				}
			}
		}
		catch(Exception e)
		{														
			ErrorHandler.getInstance().handle(new SystemException(
					this.getClass().getName(), "networkStarted", new Object[]{
						"Exception="+e.toString(),
						"Message="+e.getMessage(),
						"NetworkId="+networkId,
						"Service="+service
					} 
			));
		}
	}
	//Not necessary anymore since lack of network activity will automatically stop the push daemon
	public synchronized void radioTurnedOff() 
	{	
		try
		{
			if(!DeviceContainer.getInstance().isContainerActive())
			{
				return;
			}
			
			if(!Configuration.getInstance().isActive())
			{
				return;				
			}
			
			NotificationListener notify = NotificationListener.getInstance();
			if(notify != null)
			{
				notify.stop();
			}
		}
		catch(Exception e)
		{														
			ErrorHandler.getInstance().handle(new SystemException(
					this.getClass().getName(), "radioTurnedOff", new Object[]{
						"Exception="+e.toString(),
						"Message="+e.getMessage()
					} 
			));
		}
	}*/
	
	/*public synchronized void signalLevel(int level) 
	{	
		boolean nocoverage = false;
		try
		{
			if(!DeviceContainer.getInstance().isContainerActive())
			{
				return;
			}
			
			if(!Configuration.getInstance().isActive())
			{
				return;				
			}
			
			NotificationListener notify = NotificationListener.getInstance();
			if(level == RadioInfo.LEVEL_NO_COVERAGE)
			{			
				nocoverage = true;
				if(notify!=null && notify.isActive())
				{
					notify.stop();
				}
			}	
		}
		catch(Exception e)
		{														
			ErrorHandler.getInstance().handle(new SystemException(
					this.getClass().getName(), "signalLevel", new Object[]{
						"Exception="+e.toString(),
						"Message="+e.getMessage(),
						"Level="+level,
						"No_Coverage="+nocoverage
					} 
			));
		}
	}*/
	//----Power UnpluggedSensor----------------------------------------------------------------------------------------------------------------------
	public synchronized void powerUp() 
	{	
		try
		{
			DeviceContainer.getInstance().startup();
		}
		catch(Exception e)
		{														
			ErrorHandler.getInstance().handle(new SystemException(
					this.getClass().getName(), "powerUp", new Object[]{
						"Exception="+e.toString(),
						"Message="+e.getMessage()						
					} 
			));
		}
	}
	
	public synchronized void fastReset() 
	{
		try
		{
			DeviceContainer.getInstance().shutdown();
		}
		catch(Exception e)
		{														
			ErrorHandler.getInstance().handle(new SystemException(
					this.getClass().getName(), "fastReset", new Object[]{
						"Exception="+e.toString(),
						"Message="+e.getMessage()						
					} 
			));
		}		
	}

	public synchronized void powerOffRequested(int reason) 
	{
		try
		{
			DeviceContainer.getInstance().shutdown();
		}
		catch(Exception e)
		{														
			ErrorHandler.getInstance().handle(new SystemException(
					this.getClass().getName(), "powerOffRequested", new Object[]{
						"Exception="+e.toString(),
						"Message="+e.getMessage()						
					} 
			));
		}		
	}
	
	public synchronized void powerOff() 
	{
		try
		{
			DeviceContainer.getInstance().shutdown();
		}
		catch(Exception e)
		{														
			ErrorHandler.getInstance().handle(new SystemException(
					this.getClass().getName(), "powerOff", new Object[]{
						"Exception="+e.toString(),
						"Message="+e.getMessage()						
					} 
			));
		}
	}
	//---------------------------------------------------------------------------------------------------------------------------------------	
	public void networkStateChange(int state) 
	{		
	}
	public void networkStarted(int networkId, int service) 
	{
		
	}
	public void signalLevel(int level) 
	{
		
	}
	public void networkServiceChange(int networkId, int service)
	{
		
	}
	public void radioTurnedOff() 
	{
		
	}
	//----------------------------------------------------------------------------------------------------------------------------------------
	public void baseStationChange() 
	{		
	}
			
	public void networkScanComplete(boolean success) 
	{		
	}	
	public void pdpStateChange(int apn, int state, int cause) 
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
