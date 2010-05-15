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
public class TestRemoteBusInvocation extends Test
{	
	public void runTest()
	{
		try
		{
			//Invoke this to setup testsuite state by registering the remote mockinv
			//ocation handler						
			Bus bus = Bus.getInstance();
			
			MobilePushMetaData metadata = new MobilePushMetaData("emailChannel", 
			"uid:blah@blah.com");
			metadata.setAdded(true);
			
			MobilePushInvocation invocation = new MobilePushInvocation(
			"org.openmobster.core.mobileCloud.android.invocation.MockInvocationHandler");
			invocation.addMobilePushMetaData(metadata);
			
			InvocationResponse response = null;
			int counter = 3;
			do
			{
				Thread.currentThread().sleep(2000);
				response = bus.invokeService(invocation);
			}while(response == null && (counter--)>0);
			
			if(response != null && 
			   response.getShared()!=null && 
			   !response.getShared().isEmpty())
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
