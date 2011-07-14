package org.openmobster.core.cloud.console.server;

import java.util.List;
import java.util.ArrayList;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.openmobster.core.cloud.console.client.common.Payload;
import org.openmobster.core.cloud.console.client.rpc.DeviceService;

import org.openmobster.core.console.server.device.DeviceUI;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class DeviceServiceStubImpl extends RemoteServiceServlet implements DeviceService 
{
	private static List<DeviceUI> devices = null;
	
	public String invoke(String input) 
	{
		List<String> parameters = Payload.decode(input);
		
		//Debug
		/*System.out.println("********************************");
		System.out.println(input);
		System.out.println("********************************");
		for(String param:parameters)
		{
			System.out.println("Param: "+param);
		}*/
		
		if(devices == null)
		{
			devices = new ArrayList<DeviceUI>();
			for(int i=0; i<5; i++)
			{
				DeviceUI local = new DeviceUI();
				local.setAccount("blah"+i+"@gmail.com");
				local.setDeviceIdentifier("IMEI: "+i);
				local.setOs("Android");
				
				if((i%2)==0)
				{
					local.setActive(true);
				}
				else
				{
					local.setActive(false);
				}
				devices.add(local);
			}
		}
		
		String action = parameters.get(0);
		if(action.equalsIgnoreCase("all"))
		{
			String xml = DeviceUI.toXml(devices);	
			System.out.println(xml);
			return xml;
		}
		else if(action.equalsIgnoreCase("activate"))
		{
			String deviceId = parameters.get(1);
			return this.activate(deviceId);
		}
		else if(action.equalsIgnoreCase("deactivate"))
		{
			String deviceId = parameters.get(1);
			return this.deactivate(deviceId);
		}
		else if(action.equalsIgnoreCase("reassign"))
		{
			String deviceId = parameters.get(1);
			return this.reassign(deviceId);
		}
		
		return "500"; //If I get here, error occurred
	}
	
	private String activate(String deviceId)
	{
		try
		{
			DeviceUI device = this.find(deviceId);
			
			if(device != null)
			{
				device.setActive(true);
			}
			
			return "200";
		}
		catch(Throwable t)
		{
			t.printStackTrace();
			return "500";
		}
	}
	
	private String deactivate(String deviceId)
	{
		try
		{
			DeviceUI device = this.find(deviceId);
			
			if(device != null)
			{
				device.setActive(false);
			}
			
			return "200";
		}
		catch(Throwable t)
		{
			t.printStackTrace();
			return "500";
		}
	}
	
	private String reassign(String deviceId)
	{
		try
		{
			DeviceUI device = this.find(deviceId);
			
			if(device != null)
			{
				devices.remove(device);
			}
			
			return "200";
		}
		catch(Throwable t)
		{
			t.printStackTrace();
			return "500";
		}
	}
	
	private DeviceUI find(String deviceId)
	{
		for(DeviceUI local:devices)
		{
			if(local.getDeviceIdentifier().equals(deviceId))
			{
				return local;
			}
		}
		return null;
	}
}
