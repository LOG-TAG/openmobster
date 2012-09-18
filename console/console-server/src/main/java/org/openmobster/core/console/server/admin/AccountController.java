/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.console.server.admin;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import org.openmobster.core.common.transaction.TransactionHelper;
import org.openmobster.core.console.server.ObjectValidatorFactory;
import org.openmobster.core.console.server.Server;

/**
 * 
 * @author openmobster@gmail.com
 */
public class AccountController 
{
	private static AccountController singleton;
	
	private AccountController()
	{
		
	}
	
	public static AccountController getInstance()
	{
		if(singleton == null)
		{
			synchronized(AccountController.class)
			{
				if(singleton == null)
				{
					singleton = new AccountController();
					Server.getInstance().start();
				}
			}
		}
		return singleton;
	}
	
	public boolean createAccount(AdminAccount adminAccount) throws AdminAccountException
	{
		boolean startedHere = TransactionHelper.startTx();
		try
		{
			boolean allowLogin = false;
			
			//Check Validation
			String username = adminAccount.getUsername();
			String password = adminAccount.getPassword();
			String firstName = adminAccount.getFirstName();
			String lastName = adminAccount.getLastName();
			
			if(username == null || username.trim().length() == 0 || 
			   password == null || password.trim().length() == 0 || 
			   firstName == null || firstName.trim().length()==0 || 
			   lastName == null || lastName.trim().length() == 0)
			{
				throw new AdminAccountException(AdminAccountException.VALIDATION_ERROR);
			}
			
			//Validate the Email Field
			Set<String> validationErrors = ObjectValidatorFactory.getInstance().validate(adminAccount, "username");
			if(validationErrors != null && !validationErrors.isEmpty())
			{
				throw new AdminAccountException(AdminAccountException.EMAIL_INVALID);
			}
			
			//Check for duplicates
			if(AccountDS.getInstance().exists(username))
			{
				throw new AdminAccountException(AdminAccountException.ACCOUNT_ALREADY_EXISTS);
			}
			
			List<AdminAccount> all = AccountDS.getInstance().readAll();
			if(all == null || all.isEmpty())
			{
				allowLogin = true;
				adminAccount.activate();
				AccountDS.getInstance().create(adminAccount);
			}
			else
			{
				allowLogin = false;
				adminAccount.deactivate();
				AccountDS.getInstance().create(adminAccount);
			}
			
			if(startedHere)
			{
				TransactionHelper.commitTx();
			}
			
			return allowLogin;
		}
		catch(Exception e)
		{
			if(startedHere)
			{
				TransactionHelper.rollbackTx();
			}
			if(e instanceof AdminAccountException)
			{
				throw (AdminAccountException)e;
			}
			else
			{
				throw new AdminAccountException(e);
			}
		}
	}
	
	public boolean login(String username, String password) throws AdminAccountException
	{
		boolean startedHere = TransactionHelper.startTx();
		try
		{
			AdminAccount account = AccountDS.getInstance().read(username);
			
			if(account != null)
			{
				//Make sure the account is activated
				if(!account.isActive())
				{
					if(startedHere)
					{
						TransactionHelper.commitTx();
					}
					throw new AdminAccountException(AdminAccountException.ACCOUNT_INACTIVE);
				}
				
				String storedPassword = account.getPassword();
				if(storedPassword.equals(password))
				{
					if(startedHere)
					{
						TransactionHelper.commitTx();
					}
					return true;
				}
			}
			
			if(startedHere)
			{
				TransactionHelper.commitTx();
			}
			
			return false;
		}
		catch(Exception e)
		{
			if(e instanceof AdminAccountException)
			{
				throw (AdminAccountException)e;
			}
			
			if(startedHere)
			{
				TransactionHelper.rollbackTx();
			}
			throw new AdminAccountException(e);
		}
	}
	
	public List<AdminAccount> all() throws AdminAccountException
	{
		boolean startedHere = TransactionHelper.startTx();
		try
		{
			List<AdminAccount> all = AccountDS.getInstance().readAll();
			
			if(startedHere)
			{
				TransactionHelper.commitTx();
			}
			
			return all;
		}
		catch(Exception e)
		{
			if(startedHere)
			{
				TransactionHelper.rollbackTx();
			}
			throw new AdminAccountException(e);
		}
	}
	
	public List<AdminAccount> inactive() throws AdminAccountException
	{
		boolean startedHere = TransactionHelper.startTx();
		try
		{
			List<AdminAccount> all = AccountDS.getInstance().readAll();
			List<AdminAccount> inactive = new ArrayList<AdminAccount>();
			
			if(all != null && !all.isEmpty())
			{
				for(AdminAccount local:all)
				{
					if(!local.isActive())
					{
						inactive.add(local);
					}
				}
			}
			
			if(startedHere)
			{
				TransactionHelper.commitTx();
			}
			
			return inactive;
		}
		catch(Exception e)
		{
			if(startedHere)
			{
				TransactionHelper.rollbackTx();
			}
			throw new AdminAccountException(e);
		}
	}
	
	public void deactivate(String username) throws AdminAccountException
	{
		boolean startedHere = TransactionHelper.startTx();
		try
		{
			List<AdminAccount> activeAccts = this.activeAccts();
			if(activeAccts.size() == 1)
			{
				throw new AdminAccountException(AdminAccountException.LAST_ACCOUNT_CANNOT_BE_DEACTIVATED);
			}
			
			AdminAccount account = AccountDS.getInstance().read(username);
			account.deactivate();
			AccountDS.getInstance().update(account);
			
			if(startedHere)
			{
				TransactionHelper.commitTx();
			}
		}
		catch(Exception e)
		{
			if(startedHere)
			{
				TransactionHelper.rollbackTx();
			}
			throw new AdminAccountException(e);
		}
	}
	
	public void activate(String username) throws AdminAccountException
	{
		boolean startedHere = TransactionHelper.startTx();
		try
		{
			AdminAccount account = AccountDS.getInstance().read(username);
			account.activate();
			AccountDS.getInstance().update(account);
			
			if(startedHere)
			{
				TransactionHelper.commitTx();
			}
		}
		catch(Exception e)
		{
			if(startedHere)
			{
				TransactionHelper.rollbackTx();
			}
			throw new AdminAccountException(e);
		}
	}
	//----------------------------------------------------------------------------------------------------------------------------
	private List<AdminAccount> activeAccts()
	{
		List<AdminAccount> all = AccountDS.getInstance().readAll();
		List<AdminAccount> active = new ArrayList<AdminAccount>();
		
		if(all != null && !all.isEmpty())
		{
			for(AdminAccount local:all)
			{
				if(local.isActive())
				{
					active.add(local);
				}
			}
		}
		
		return active;
	}
}
