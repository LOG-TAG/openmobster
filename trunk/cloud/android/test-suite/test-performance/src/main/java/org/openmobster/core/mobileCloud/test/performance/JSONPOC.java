/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.test.performance;

import org.openmobster.android.api.sync.MobileBean;
import org.openmobster.android.api.sync.BeanList;
import org.openmobster.android.api.sync.BeanListEntry;
import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.module.mobileObject.MobileObjectDatabase;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.AppException;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.RemoteCommand;
import org.openmobster.core.mobileCloud.android.util.GenericAttributeManager;

import android.app.Activity;
import android.content.Context;
import org.json.JSONObject;
import org.json.JSONArray;

/**
 * The MVC AsyncCommand. The 'AsyncCommand' is used to perform actions asynchronously (Ajax from the web world). 
 * 
 * This particular AsyncCommand, invokes the 'GetDetails' service in the Cloud and gets a fully populated Email instance for display.
 * 
 * @author openmobster@gmail.com
 *
 */
public final class JSONPOC implements RemoteCommand
{
	//Executes on the UI thread. All UI related operations are safe here. It is invoked to perform some pre-action UI related tasks.
	public void doViewBefore(CommandContext commandContext)
	{	
		//Nothing to do
	}

	//This does not execute on the UI thread. When this method is invoked, the UI thread is freed up, so that its not frozen while the 
	//information is being loaded from the Cloud
	public void doAction(CommandContext commandContext) 
	{
		try
		{
			//String json = "{\"org.openmobster.core.location.AddressSPI\":{\"street\":\"2046 Dogwood Gardens Dr\",\"city\":\"Germantown\"}}";
			//String json = "{\"street\":\"2046 Dogwood Gardens Dr\",\"city\":\"Germantown\"}";
			String json = "{\"address\":\"2046 Dogwood Gardens Dr\",\"name\":\"My Home\",\"types\":[\"home\",\"house\"]}";
			
			JSONObject object = new JSONObject(json);
			
			System.out.println("Address: "+object.getString("address"));
			System.out.println("Name: "+object.getString("name"));
			
			JSONArray types = object.getJSONArray("types");
			int length = types.length();
			for(int i=0; i<length; i++)
			{
				String local = types.getString(i);
				
				System.out.println("Type: "+local);
			}
			
			//Encode a Place
			JSONObject place = new JSONObject();
			place.put("address", "2046 Dogwood Gardens Dr");
			place.put("name", "My Home");
			JSONArray encodeTypes = new JSONArray();
			encodeTypes.put("home");
			encodeTypes.put("house");
			place.put("types", encodeTypes);
			
			System.out.println(json);
			System.out.println(place.toString());
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			
			//throw an AppException. If this happens, the doViewError will be invoked to alert the user with an error message
			AppException appe = new AppException();
			appe.setMessage(e.getMessage());
			
			//Record this error in the Cloud Error Log
			ErrorHandler.getInstance().handle(appe);
			
			throw appe;
		}
	}	
	
	//Executes on the UI thread. All UI operations are safe. It is invoked after the doAction is executed without any errors.
	//From an Ajax standpoint, consider this invocation as the UI callback
	public void doViewAfter(CommandContext commandContext)
	{

	}
	
	//Executes on the UI thread. All UI operations are safe. This method is invokes if there is an error during the doAction execution.
	//From an Ajax standpoint, consider this invocation as a UI callback
	public void doViewError(CommandContext commandContext)
	{
		//Shows an Error Alert
		Activity currentActivity = Services.getInstance().getCurrentActivity();
		ViewHelper.getOkModal(currentActivity, "App Error", 
		commandContext.getAppException().getMessage()).
		show();
	}
}
