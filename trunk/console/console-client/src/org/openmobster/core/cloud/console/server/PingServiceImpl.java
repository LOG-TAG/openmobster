package org.openmobster.core.cloud.console.server;

import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.openmobster.core.cloud.console.client.common.Payload;
import org.openmobster.core.cloud.console.client.rpc.PingService;

import org.openmobster.core.console.server.PingDelegate;
import org.openmobster.core.console.server.admin.AdminAccount;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class PingServiceImpl extends RemoteServiceServlet implements PingService 
{
	public String ping(String input) throws IllegalArgumentException 
	{
		//PingDelegate pingDelegate = new PingDelegate();
		//return pingDelegate.invoke(input);
		
		/*List<String> params = Payload.decode(input);
		for(String param:params)
		{
			System.out.println(param);
		}
		
		String payload = Payload.encode(new String[]{"param3", "param4"});
		
		return payload;*/
		
		AdminAccount adminAccount = new AdminAccount("root", "password");
		return adminAccount.toXml();
	}
}
