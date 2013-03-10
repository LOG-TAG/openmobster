package org.openmobster.core.cloud.console.server;

import java.util.List;
import java.util.ArrayList;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.openmobster.core.cloud.console.client.common.Payload;
import org.openmobster.core.cloud.console.client.rpc.DeviceService;

import org.openmobster.core.console.server.device.DeviceUI;
import org.openmobster.core.console.server.device.ManageDevice;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class DeviceServiceImpl extends RemoteServiceServlet implements DeviceService 
{
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
		
		String action = parameters.get(0);
		if(action.equalsIgnoreCase("all"))
		{
			String xml = this.viewAllDevices();
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
		else if(action.equalsIgnoreCase("resetpassword"))
		{
			String deviceId = parameters.get(1);
			String newPassword = parameters.get(2);
			return this.resetPassword(deviceId,newPassword);
		}
		
		return "500"; //If I get here, error occurred
	}
	
	private String viewAllDevices()
	{
		try
		{
			ManageDevice manageDevice = ManageDevice.getInstance();
			List<DeviceUI> devices = manageDevice.getRegisteredDevices();
			return DeviceUI.toXml(devices);
		}
		catch(Throwable t)
		{
			t.printStackTrace();
			return "500";
		}
	}
	
	private String activate(String deviceId)
	{
		try
		{
			ManageDevice.getInstance().activate(deviceId);
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
			ManageDevice.getInstance().deactivate(deviceId);
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
			ManageDevice.getInstance().reassign(deviceId);
			return "200";
		}
		catch(Throwable t)
		{
			t.printStackTrace();
			return "500";
		}
	}
	
	private String resetPassword(String deviceId,String newPassword)
	{
		try
		{
			ManageDevice.getInstance().resetPassword(deviceId,newPassword);
			
			return "200";
		}
		catch(Throwable t)
		{
			t.printStackTrace();
			return "500";
		}
	}
}
