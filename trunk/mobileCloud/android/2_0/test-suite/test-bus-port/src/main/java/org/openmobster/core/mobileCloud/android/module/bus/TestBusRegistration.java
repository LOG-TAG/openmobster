/**
 * 
 */
package org.openmobster.core.mobileCloud.android.module.bus;

import java.util.Set;

import org.openmobster.core.mobileCloud.android.testsuite.Test;

/**
 * @author openmobster
 *
 */
public class TestBusRegistration extends Test
{	
	public void runTest()
	{
		try
		{
			for(int i=0; i<3; i++)
			{
				BusRegistration busRegistration = new BusRegistration(
					"org.openmobster.core.mobileCloud.android."+i);				
				busRegistration.addInvocationHandler(i+"://blah1");
				busRegistration.addInvocationHandler(i+"://blah2");
				busRegistration.addInvocationHandler(i+"://blah3");
				
				busRegistration.save();
				
				this.testQueryAll(busRegistration);
				
				this.testQueryById(busRegistration);
			}
			
			Set<BusRegistration> registeredBuses = BusRegistration.queryAll();
			for(BusRegistration reg:registeredBuses)
			{
				this.testDelete(reg);
			}
			
			registeredBuses = BusRegistration.queryAll();
			assertTrue(registeredBuses==null || registeredBuses.isEmpty(),
				"All Buses must be cleaned up!!");
			if(registeredBuses != null)
			{
				for(BusRegistration reg:registeredBuses)
				{
					System.out.println("Bus ID: "+reg.getBusId()+" must have been deleted!!");
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			throw new RuntimeException(e);
		}
	}
	
	private void testQueryAll(BusRegistration busRegistration) throws Exception
	{
		Set<BusRegistration> registeredBuses = BusRegistration.queryAll();			
		if(registeredBuses == null || registeredBuses.isEmpty())
		{
			assertFalse(true,this.getInfo()+"/BusRegistrationFailed!!");
		}
		else
		{
			System.out.println("---------------------------------------");
			for(BusRegistration cour:registeredBuses)
			{
				System.out.println("BusId: "+cour.getBusId());
				if(cour.getInvocationHandlers() != null)
				{
					for(String handler: cour.getInvocationHandlers())
					{
						System.out.println("Handler: "+handler);
					}
				}
			}
			System.out.println("---------------------------------------");
		}
	}
	
	private void testQueryById(BusRegistration busRegistration) throws Exception
	{
		BusRegistration queryById = BusRegistration.query(busRegistration.getBusId());
		if(queryById == null)
		{
			assertFalse(true,this.getInfo()+"/QueryById/BusRegistrationFailed!!");
		}
		else
		{
			assertEquals(queryById.getBusId(),busRegistration.getBusId(),
			"/QueryById/BusIdsMismatch");
			
			System.out.println("BusId: "+queryById.getBusId());
			if(queryById.getInvocationHandlers() != null)
			{
				for(String handler: queryById.getInvocationHandlers())
				{
					System.out.println("Handler: "+handler);
				}
			}
		}
	}
	
	private void testDelete(BusRegistration busRegistration) throws Exception
	{
		BusRegistration.delete(busRegistration.getBusId());
		BusRegistration queryById = BusRegistration.query(busRegistration.getBusId());
		if(queryById != null)
		{
			assertFalse(true,this.getInfo()+"/Delete/Failed");
		}
	}
}
