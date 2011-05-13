package org.openmobster.core.cloud.console.server;

import java.util.List;
import java.util.ArrayList;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.openmobster.core.cloud.console.client.common.Payload;
import org.openmobster.core.cloud.console.client.rpc.PushAppService;

import org.openmobster.core.console.server.pushapp.PushAppUI;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class PushAppServiceStubImpl extends RemoteServiceServlet implements PushAppService 
{
	private static List<PushAppUI> pushApps = null;
	
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
		
		if(pushApps == null)
		{
			pushApps = new ArrayList<PushAppUI>();
			
			for(int i=0; i<5; i++)
			{
				PushAppUI local = new PushAppUI();
				local.setAppId("blah"+i);
				
				if((i%2)==0)
				{
					local.setActive(true);
				}
				else
				{
					local.setActive(false);
				}
				
				pushApps.add(local);
			}
		}
		
		String action = parameters.get(0);
		if(action.equalsIgnoreCase("all"))
		{
			String xml = PushAppUI.toXml(pushApps);	
			System.out.println(xml);
			return xml;
		}
		
		return "500"; //If I get here, error occurred
	}
}
