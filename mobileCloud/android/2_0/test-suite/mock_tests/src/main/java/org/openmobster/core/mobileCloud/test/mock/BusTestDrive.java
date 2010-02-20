/**
 * 
 */
package org.openmobster.core.mobileCloud.test.mock;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.content.Intent;

import org.openmobster.core.mobileCloud.android.testsuite.Test;
import org.openmobster.core.mobileCloud.test.mock.rpc.IBusHandler;

/**
 * @author openmobster
 *
 */
public class BusTestDrive extends Test
{
	private IBusHandler remoteBus;
	private BusConnection connection;
	
	public void runTest()
	{
		Context context = (Context)this.getTestSuite().getContext().
		getAttribute("android:context");
		try
		{
			this.busConnect(context);
			
			int counter = 0;
			while(this.remoteBus == null)
			{
				System.out.println("Waiting on service to bind.......");
				Thread.currentThread().sleep(5000);
				if(this.remoteBus != null || counter++ == 5)
				{
					break;
				}
			}
			
			if(this.remoteBus != null)
			{
				for(int i=0; i<5; i++)
				{
					System.out.println("Remote Counter: "+this.remoteBus.getCounter());
				}
			}
			else
			{
				System.out.println("RemoteService was not bound yet!!!");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
		finally
		{
			this.busDisconnect(context);
		}
	}
	
	private void busConnect(Context context)
	{
		Intent busIntent = new Intent();
		busIntent.setClassName("org.openmobster.core.mobileCloud.test.mock", 
		"org.openmobster.core.mobileCloud.test.mock.rpc.BusService");		
		
		//Connect to this service		
		this.connection = new BusConnection();
		context.bindService(busIntent, this.connection, Context.BIND_AUTO_CREATE);
	}
	
	private void busDisconnect(Context context)
	{
		context.unbindService(connection);
	}
	
	private class BusConnection implements ServiceConnection
	{
		public void onServiceConnected(ComponentName component, IBinder binder)
		{
			BusTestDrive.this.remoteBus = IBusHandler.Stub.asInterface(binder);
		}

		public void onServiceDisconnected(ComponentName component)
		{	
			BusTestDrive.this.remoteBus = null;
		}	
	}
}
