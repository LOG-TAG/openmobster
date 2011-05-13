package org.openmobster.core.cloud.console.server;

import java.util.List;
import java.util.ArrayList;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.openmobster.core.cloud.console.client.common.Payload;
import org.openmobster.core.cloud.console.client.rpc.PushAppService;

import org.openmobster.core.console.server.pushapp.PushAppUI;
import org.openmobster.core.console.server.pushapp.ManagePushApp;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class PushAppServiceImpl extends RemoteServiceServlet implements PushAppService 
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
			String xml = this.all();	
			return xml;
		}
		else if(action.equalsIgnoreCase("testPush"))
		{
			String deviceId = parameters.get(1);
			String appId = parameters.get(2);
			
			return this.testPush(deviceId, appId);
		}
		
		return "500"; //If I get here, error occurred
	}
	
	private String all()
	{
		try
		{
			ManagePushApp managePushApp = ManagePushApp.getInstance();
			
			List<PushAppUI> all = managePushApp.readAll();
			
			return PushAppUI.toXml(all);
		}
		catch(Throwable t)
		{
			t.printStackTrace();
			return "500";
		}
	}
	
	private String testPush(String deviceId, String appId)
	{
		try
		{
			ManagePushApp managePushApp = ManagePushApp.getInstance();
			
			managePushApp.testPush(deviceId, appId);
			
			return "200";
		}
		catch(Throwable t)
		{
			t.printStackTrace();
			return "500";
		}	
	}
}
