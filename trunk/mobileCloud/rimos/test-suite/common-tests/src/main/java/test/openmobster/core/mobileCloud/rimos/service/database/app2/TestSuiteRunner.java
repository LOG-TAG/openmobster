/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.core.mobileCloud.rimos.service.database.app2;

import net.rim.device.api.system.RadioStatusListener;
import net.rim.device.api.system.SystemListener;

import test.openmobster.core.mobileCloud.rimos.testsuite.TestSuite;

/**
 * TODO: Write ClientSideErrorHandling Test Case
 */

/**
 * 
 * @author openmobster@gmail.com
 *
 */
public class TestSuiteRunner implements RadioStatusListener, SystemListener
{	
	/**
	 * 
	 *
	 */
	public TestSuiteRunner()
	{		
	}	
	
	
	
	//--Network related events--------------------------------------------------------------------------------
	/**
	 * Invoked when the radio has been turned off
	 */
	public void radioTurnedOff() 
	{
		
	}
	
	/**
	 * Invoked when the radio has been turned on or has just been switched to a new network
	 */
	public void networkStarted(int networkId, int service) 
	{
		try
		{						
			TestSuite suite = new TestSuite();
			
			suite.addTest(new TestConcurrency());
									
			suite.execute();
		}
		catch(Exception e)
		{
			System.out.println("Container TestSuite Problems-----------------------------------------");
			System.out.println("Exception ="+e+", "+e.getMessage());
		}		
	}			
	//---device power related events--------------------------------------------------------------------------
	/**
	 * Invoked when the user is putting the device into a power off state
	 */
	public void powerOff() 
	{		
	}

	/**
	 * Invoked when the device has left the power off state
	 */
	public void powerUp() 
	{				
	}		
	//---device battery related events-------------------------------------------------------------------------
	/**
	 * Invoked when the internal battery voltage falls below a critical level
	 */
	public void batteryLow() 
	{			
	}
	
	/**
	 * Invoked when the internal battery state has changed
	 */
	public void batteryGood() 
	{			
	}
	//---------------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 */
	public void networkStateChange(int state) 
	{		
	}
	
	/**
	 * 
	 */
	public void baseStationChange() 
	{		
	}

	/**
	 * 
	 */
	public void mobilityManagementEvent(int arg0, int arg1) 
	{		
	}

	/**
	 * 
	 */
	public void networkScanComplete(boolean arg0) 
	{		
	}

	/**
	 * 
	 */
	public void networkServiceChange(int arg0, int arg1) 
	{		
	}

	/**
	 * 
	 */
	public void pdpStateChange(int arg0, int arg1, int arg2) 
	{		
	}
	
	/**
	 * 
	 */
	public void signalLevel(int level) 
	{	
	}
	//--------------------------------------------------------------------------------------------------------		
	/**
	 * 
	 */
	public void batteryStatusChange(int status) 
	{				
	}
	//-------------------------------------------------------------------------------------------------------		
}
