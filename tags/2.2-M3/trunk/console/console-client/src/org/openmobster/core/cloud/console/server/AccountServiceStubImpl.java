package org.openmobster.core.cloud.console.server;

import java.util.List;
import java.util.ArrayList;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.openmobster.core.cloud.console.client.common.Payload;
import org.openmobster.core.cloud.console.client.rpc.AccountService;

import org.openmobster.core.console.server.admin.AccountController;
import org.openmobster.core.console.server.admin.AdminAccount;
import org.openmobster.core.console.server.admin.AdminAccountException;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class AccountServiceStubImpl extends RemoteServiceServlet implements AccountService 
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
		if(action.equalsIgnoreCase("login"))
		{
			String username = parameters.get(1);
			String password = parameters.get(2);
			
			if(username.equalsIgnoreCase("null"))
			{
				username = "";
			}
			if(password.equalsIgnoreCase("null"))
			{
				password = "";
			}
			
			return this.login(username, password);
		}
		else if(action.equalsIgnoreCase("createAccount"))
		{
			String username = parameters.get(1);
			String password = parameters.get(2);
			String firstName = parameters.get(3);
			String lastName = parameters.get(4);
			
			if(username.equalsIgnoreCase("null"))
			{
				username = "";
			}
			if(password.equalsIgnoreCase("null"))
			{
				password = "";
			}
			if(firstName.equalsIgnoreCase("null"))
			{
				firstName = "";
			}
			if(lastName.equalsIgnoreCase("null"))
			{
				lastName = "";
			}
			
			return this.createAccount(username, password, firstName, lastName);
		}
		else if(action.equalsIgnoreCase("all"))
		{
			List<AdminAccount> accounts = new ArrayList<AdminAccount>();
			for(int i=0; i<5; i++)
			{
				AdminAccount local = new AdminAccount();
				local.setFirstName("firstName://"+i);
				local.setLastName("lastName://"+i);
				local.setUsername("username://"+i);
				
				if((i%2)==0)
				{
					local.deactivate();
				}
				else
				{
					local.activate();
				}
				accounts.add(local);
			}
			String xml = AdminAccount.toXml(accounts);
			return xml;
		}
		else if(action.equalsIgnoreCase("inactive"))
		{
			
		}
		
		return "500"; //If I get here, error occurred
	}
	
	private String login(String username, String password)
	{
		//FIXME: uncomment in non-hosted mode
		/*try
		{
			boolean success = AccountController.getInstance().login(username,password);
			if(success)
			{
				return "200";
			}
			
			return "0";
		}
		catch(Throwable t)
		{
			t.printStackTrace();
			return "500";
		}*/
		//return "200";
		
		//Send back a stub list of admin accounts
		/*List<AdminAccount> accounts = new ArrayList<AdminAccount>();
		for(int i=0; i<5; i++)
		{
			AdminAccount local = new AdminAccount();
			local.setFirstName("firstName://"+i);
			local.setLastName("lastName://"+i);
			local.setUsername("username://"+i);
			
			if((i%2)==0)
			{
				local.deactivate();
			}
			else
			{
				local.activate();
			}
			accounts.add(local);
		}
		String xml = AdminAccount.toXml(accounts);
		return xml;*/
		return "200";
	}
	
	private String createAccount(String username, String password, String firstName, String lastName)
	{
		try
		{
			AdminAccount adminAccount = new AdminAccount();
			adminAccount.setUsername(username);
			adminAccount.setPassword(password);
			adminAccount.setFirstName(firstName);
			adminAccount.setLastName(lastName);
			
			AccountController.getInstance().createAccount(adminAccount);
			
			return "200";
		}
		catch(AdminAccountException aae)
		{
			return ""+aae.getType();
		}
		catch(Throwable t)
		{
			t.printStackTrace();
			return "500";
		}
	}
}
