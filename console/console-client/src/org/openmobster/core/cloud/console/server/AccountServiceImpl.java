package org.openmobster.core.cloud.console.server;

import java.util.List;

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
public class AccountServiceImpl extends RemoteServiceServlet implements AccountService 
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
			return this.getAllAccounts();
		}
		else if(action.equalsIgnoreCase("deactivate"))
		{
			String username = parameters.get(1);
			if(username.equalsIgnoreCase("null"))
			{
				username = "";
			}
			return this.deactivate(username);
		}
		else if(action.equalsIgnoreCase("activate"))
		{
			String username = parameters.get(1);
			if(username.equalsIgnoreCase("null"))
			{
				username = "";
			}
			return this.activate(username);
		}
		
		return "500"; //If I get here, error occurred
	}
	
	private String login(String username, String password)
	{
		try
		{
			boolean success = AccountController.getInstance().login(username,password);
			if(success)
			{
				//Check if a list if admin accounts must be sent or not
				List<AdminAccount> inactive = AccountController.getInstance().inactive();
				
				if(inactive != null && !inactive.isEmpty())
				{
					return AdminAccount.toXml(inactive);
				}
				
				return "200";
			}
			
			return "0";
		}
		catch(AdminAccountException ade)
		{
			return ""+ade.getType();
		}
		catch(Throwable t)
		{
			t.printStackTrace();
			return "500";
		}
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
			
			boolean allowLogin = AccountController.getInstance().createAccount(adminAccount);
			
			if(allowLogin)
			{
				return "200";
			}
			else
			{
				return "201";
			}
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
	
	private String getAllAccounts()
	{
		try
		{
			List<AdminAccount> all = AccountController.getInstance().all(); 
			return AdminAccount.toXml(all);
		}
		catch(Throwable t)
		{
			t.printStackTrace();
			return "500";
		}
	}
	
	private String deactivate(String username)
	{
		try
		{
			AccountController.getInstance().deactivate(username);
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
	
	private String activate(String username)
	{
		try
		{
			AccountController.getInstance().activate(username);
			return "200";
		}
		catch(Throwable t)
		{
			t.printStackTrace();
			return "500";
		}
	}
}
