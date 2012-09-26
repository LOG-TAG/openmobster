package org.openmobster.core.cloud.console.server;

import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.openmobster.core.cloud.console.client.common.Payload;
import org.openmobster.core.cloud.console.client.rpc.DeviceManagementService;

import org.openmobster.core.console.server.device.ManageDevice;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class DeviceManagementServiceImpl extends RemoteServiceServlet implements DeviceManagementService 
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
		if(action.equalsIgnoreCase("lock"))
		{
			String account = parameters.get(1);
			return this.lock(account);
		}
		else if(action.equalsIgnoreCase("wipe"))
		{
			String account = parameters.get(1);
			return this.wipe(account);
		}
		
		return "500"; //If I get here, error occurred
	}
	
	private String lock(String account)
	{
		try
		{
			ManageDevice.getInstance().lock(account);
			return "200";
		}
		catch(Throwable t)
		{
			t.printStackTrace();
			return "500";
		}
	}
	
	private String wipe(String account)
	{
		try
		{
			ManageDevice.getInstance().wipe(account);
			return "200";
		}
		catch(Throwable t)
		{
			t.printStackTrace();
			return "500";
		}
	}
}
