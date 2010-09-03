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
public class TestBusInvocation extends Test
{	
	public void runTest()
	{
		try
		{
			Bus bus = Bus.getInstance();
			bus.register(new MockInvocationHandler());
			
			MobilePushMetaData metadata = new MobilePushMetaData("emailChannel", "uid:blah@blah.com");
			metadata.setAdded(true);
			
			MobilePushInvocation invocation = new MobilePushInvocation(
			MockInvocationHandler.class.getName());
			invocation.addMobilePushMetaData(metadata);
			
			InvocationResponse response = bus.invokeService(invocation);
			
			if(response != null)
			{
				Map<String,String> shared = response.getShared();
				Set<String> keys = shared.keySet();
				for(String key:keys)
				{
					System.out.println(key+": "+shared.get(key));
				}
			}
			else
			{
				assertFalse(true,this.getInfo()+"/ResponseMustNotBeNull");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			throw new RuntimeException(e);
		}
	}
}
