/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.crud.android.command;

import java.util.Vector;

import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;
import org.openmobster.core.mobileCloud.api.model.MobileBean;
import org.openmobster.core.mobileCloud.api.service.MobileService;
import org.openmobster.core.mobileCloud.api.service.Request;
import org.openmobster.core.mobileCloud.api.service.Response;
import org.openmobster.core.mobileCloud.api.ui.framework.command.AppException;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.AsyncCommand;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.NavigationContext;

import android.app.Activity;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

/**
 * 
 * @author openmobster@gmail.com
 */
public class AsyncLoadSpinners implements AsyncCommand
{
	public void doViewBefore(CommandContext commandContext)
	{
	}

	public void doAction(CommandContext commandContext)
	{
		try
		{
			Request request = new Request("/async/load/spinners");	
			Response response = new MobileService().invoke(request);
			
			Vector customers = response.getListAttribute("customers");
			Vector specialists = response.getListAttribute("specialists");
			
			commandContext.setAttribute("customers", customers);
			commandContext.setAttribute("specialists", specialists);
		}
		catch(Exception e)
		{
			AppException appe = new AppException();
			appe.setMessage(e.getMessage());
			ErrorHandler.getInstance().handle(appe);
			
			throw appe;
		}
	}

	public void doViewAfter(CommandContext commandContext)
	{
		Activity currentActivity = (Activity)commandContext.getAppContext();
		NavigationContext navContext = NavigationContext.getInstance();
		MobileBean ticket = (MobileBean)navContext.getAttribute(navContext.getCurrentScreen().getId(),"ticket");
		String selectedCustomer = "";
		String selectedSpecialist = "";
		if(ticket != null)
		{
			selectedCustomer = ticket.getValue("customer");
			selectedSpecialist = ticket.getValue("specialist");
		}
		
		//Load the customer spinner
		Vector customers = (Vector)commandContext.getAttribute("customers");
		Spinner customer = (Spinner)ViewHelper.findViewById(currentActivity, "customer");
		ArrayAdapter<CharSequence> customerAdapter = new ArrayAdapter<CharSequence>(currentActivity,android.R.layout.simple_spinner_item);
		int selectedCustomerPosition = -1;
		customerAdapter.add("--Select--");
		int size = customers.size();
		for(int i=0; i<size; i++)
		{
			String local = (String)customers.get(i);
			customerAdapter.add(local);
			if(local.equals(selectedCustomer))
			{
				selectedCustomerPosition = i;
			}
		}
		customerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		customer.setAdapter(customerAdapter);
		customer.setSelection(selectedCustomerPosition+1, true);
		
		//Load the specialist spinner
		Vector specialists = (Vector)commandContext.getAttribute("specialists");
		Spinner specialist = (Spinner)ViewHelper.findViewById(currentActivity, "specialist");
		ArrayAdapter<CharSequence> specialistAdapter = new ArrayAdapter<CharSequence>(currentActivity,android.R.layout.simple_spinner_item);
		int selectedSpecialistPosition = -1;
		specialistAdapter.add("--Select--");
		size = specialists.size();
		for(int i=0; i<size; i++)
		{
			String local = (String)specialists.get(i);
			specialistAdapter.add(local);
			if(local.equals(selectedSpecialist))
			{
				selectedSpecialistPosition = i;
			}
		}
		specialistAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		specialist.setAdapter(specialistAdapter);
		specialist.setSelection(selectedSpecialistPosition+1, true);
	}

	public void doViewError(CommandContext commandContext)
	{
		Activity currentActivity = (Activity)commandContext.getAppContext();
		ViewHelper.getOkModal(currentActivity, "Async Cloud Invocation Error", 
		"An unknown error occurred while trying to load the spinners").
		show();
		
		NavigationContext.getInstance().back();
	}
}
