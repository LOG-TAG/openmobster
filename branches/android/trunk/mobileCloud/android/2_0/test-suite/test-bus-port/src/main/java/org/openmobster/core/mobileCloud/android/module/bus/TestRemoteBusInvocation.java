/**
 * 
 */
package org.openmobster.core.mobileCloud.android.module.bus;

import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.os.IBinder;

import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android.testsuite.Test;

import org.openmobster.core.mobileCloud.android.module.bus.Bus;
import org.openmobster.core.mobileCloud.android.module.bus.InvocationResponse;
import org.openmobster.core.mobileCloud.android.module.bus.MobilePushInvocation;
import org.openmobster.core.mobileCloud.android.module.bus.MobilePushMetaData;
import org.openmobster.core.mobileCloud.android.module.bus.rpc.IBinderManager;

import android.content.ServiceConnection;

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
			Bus bus = Bus.getInstance();
			IBinderManager bm = IBinderManager.getInstance();
			bm.bind("org.openmobster.core.mobileCloud.android.remote.bus");
			
			MobilePushMetaData metadata = new MobilePushMetaData("emailChannel", "uid:blah@blah.com");
			metadata.setAdded(true);
			
			MobilePushInvocation invocation = new MobilePushInvocation(
			"org.openmobster.core.mobileCloud.android.remote.bus.MockInvocationHandler");
			invocation.addMobilePushMetaData(metadata);
			
			InvocationResponse response = null;
			int counter = 3;
			do
			{
				Thread.currentThread().sleep(2000);
				response = bus.invokeService(invocation);
			}while(response == null && (counter--)>0);
			
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
		}
	}
}
