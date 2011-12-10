/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.cloud.console.client.ui.user;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.util.SC;

import org.openmobster.core.cloud.console.client.common.Constants;
import org.openmobster.core.cloud.console.client.common.Payload;
import org.openmobster.core.cloud.console.client.model.AdminAccount;
import org.openmobster.core.cloud.console.client.rpc.AccountService;
import org.openmobster.core.cloud.console.client.rpc.AccountServiceAsync;
import org.openmobster.core.cloud.console.client.state.ContextRegistry;
import org.openmobster.core.cloud.console.client.ui.TabController;

/**
 * 
 * @author openmobster@gmail.com
 */
public final class AdminScreenLoader 
{
	public static void load(final String id)
	{
		SC.showPrompt("Loading....");
		
		final AccountServiceAsync accountService = GWT.create(AccountService.class);
		String payload = Payload.encode(new String[]{"all"});
		
		accountService.invoke(payload,
				new AsyncCallback<String>() 
				{
					public void onFailure(Throwable caught) 
					{
						SC.clearPrompt();
						SC.say("System Error", "Unexpected Network Error. Please try again.",null);
					}

					public void onSuccess(String result)
					{
						SC.clearPrompt();
						if(result.trim().equals("500"))
						{
							//validation error
							SC.say("System Error", "Internal Server Error. Please try again.",null);
						}
						else
						{
							//A list of all accounts sent back to show some need activation
							List<AdminAccount> accounts = AdminAccount.toList(result);
							ContextRegistry.getAppContext().setAttribute(Constants.admin_accounts, accounts);
						
							TabController.getInstance().openTab(Constants.admin_accounts,true,new ApproveAdminScreen());
						}
					}
				}
		);
	}
}
