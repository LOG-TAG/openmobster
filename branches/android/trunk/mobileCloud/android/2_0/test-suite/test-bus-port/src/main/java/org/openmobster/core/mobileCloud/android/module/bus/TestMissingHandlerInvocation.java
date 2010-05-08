/**
 * 
 */
package org.openmobster.core.mobileCloud.android.module.bus;

import java.util.Map;
import java.util.Set;

import org.openmobster.core.mobileCloud.android.testsuite.Test;

import org.openmobster.core.mobileCloud.android.module.bus.Bus;
import org.openmobster.core.mobileCloud.android.module.bus.InvocationResponse;
import org.openmobster.core.mobileCloud.android.module.bus.MobilePushInvocation;
import org.openmobster.core.mobileCloud.android.module.bus.MobilePushMetaData;

/**
 * @author openmobster
 *
 */
public class TestMissingHandlerInvocation extends Test
{	
	public void runTest()
	{
		try
		{
			for(int i=0; i<2; i++)
			{
				this.makeBrokenInvocation();
				this.makeProperInvocation();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			throw new RuntimeException(e);
		}
	}
	
	private void makeBrokenInvocation() throws Exception
	{
		Bus bus = Bus.getInstance();
		
		MobilePushMetaData metadata = new MobilePushMetaData("emailChannel", "uid:blah@blah.com");
		metadata.setAdded(true);
		
		MobilePushInvocation invocation = new MobilePushInvocation(
		"missingHandler");
		invocation.addMobilePushMetaData(metadata);
		
		InvocationResponse response = bus.invokeService(invocation);
		if(response != null)
		{
			Map<String,String> shared = response.getShared();
			this.printResponse(shared);
		}	
	}
	
	private void makeProperInvocation() throws Exception
	{
		Bus bus = Bus.getInstance();
		
		MobilePushMetaData metadata = new MobilePushMetaData("emailChannel", "uid:blah@blah.com");
		metadata.setAdded(true);
		
		MobilePushInvocation invocation = new MobilePushInvocation(
		"org.openmobster.core.mobileCloud.android.module.bus.MockInvocationHandler");
		invocation.addMobilePushMetaData(metadata);
		
		InvocationResponse response = bus.invokeService(invocation);
		if(response != null)
		{
			Map<String,String> shared = response.getShared();
			this.printResponse(shared);
		}	
	}
	
	private void printResponse(Map<String,String> shared)
	{
		Set<String> keys = shared.keySet();
		for(String key:keys)
		{
			System.out.println(key+": "+shared.get(key));
		}
	}
}
