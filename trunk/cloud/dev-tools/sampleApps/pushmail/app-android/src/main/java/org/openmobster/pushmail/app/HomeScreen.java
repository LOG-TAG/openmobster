/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.pushmail.app;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openmobster.core.mobileCloud.android.configuration.Configuration;
import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;
import org.openmobster.core.mobileCloud.api.model.MobileBean;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.NavigationContext;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.Screen;
import org.openmobster.core.mobileCloud.android.util.Base64;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

/**
 * The Main Screen of the App. This is rendered when the App is launched. It is registered as the 'bootstrap' screen
 * in 'resources/moblet-app/moblet-app.xml'
 * 
 * @author openmobster@gmail.com
 */
public class HomeScreen extends Screen
{
	private Integer screenId;
	
	@Override
	public void render()
	{
		try
		{
			final Activity currentActivity = (Activity)Registry.getActiveInstance().
			getContext();
			
			//Layout the Home Screen. The layout is specified in 'res/layout/home.xml'
			String layoutClass = currentActivity.getPackageName()+".R$layout";
			String home = "home";
			Class clazz = Class.forName(layoutClass);
			Field field = clazz.getField(home);
			
			this.screenId = field.getInt(clazz);						
		}
		catch(Exception e)
		{
			SystemException se = new SystemException(this.getClass().getName(), "render", new Object[]{
				"Message:"+e.getMessage(),
				"Exception:"+e.toString()
			});
			ErrorHandler.getInstance().handle(se);
			throw se;
		}
	}
	
	@Override
	public Object getContentPane()
	{
		return this.screenId;
	}
	
	@Override
	public void postRender()
	{
		Activity app = (Activity)Registry.getActiveInstance().getContext();
		
		//App Title
		app.setTitle("Push Mail");
		
		CommandContext commandContext = new CommandContext();
		commandContext.setAttribute("delegate", this);
		commandContext.setTarget("/channel/bootup/helper");
		Services.getInstance().getCommandService().execute(commandContext);
	}
	
	void setupScreen(final Activity activity)
	{
		//Populate the List View
		ListView view = (ListView)ViewHelper.findViewById(activity, "list");
		
		ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
		
		//Reading all the 'Email' instances for display
		MobileBean[] mail = MobileBean.readAll(AppConstants.push_mail_channel);
		
		for(MobileBean local: mail)
		{
			//Showing the 'from' and 'subject' values on the list
			String from = local.getValue("from");
			String subject = local.getValue("subject");
			
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("empty", from);
			map.put("title", subject);
			mylist.add(map);
		}
		
		
		SimpleAdapter showcaseAdapter = new SimpleAdapter(activity, mylist, ViewHelper.findLayoutId(activity, "home_row"),
	     new String[] {"empty", "title"}, 
	     new int[] {ViewHelper.findViewId(activity, "empty"), ViewHelper.findViewId(activity, "title")});
	    view.setAdapter(showcaseAdapter);
	    
	    OnItemClickListener clickListener = new ClickListener(mail);
		view.setOnItemClickListener(clickListener);
	}
	
	private static class ClickListener implements OnItemClickListener
	{
		private MobileBean[] mail;
		private ClickListener(MobileBean[] mail)
		{
			this.mail = mail;
		}
		
		public void onItemClick(AdapterView<?> parent, View view, int position,long id)
		{
			try
			{
				final MobileBean activeBean = this.mail[position];
				
				//Read the attributes from the MobileBean
				String from = activeBean.getValue("from");
				String date = activeBean.getValue("receivedOn");
				String subject = activeBean.getValue("subject");
				String message = activeBean.getValue("message");
				message = new String(Base64.decode(message.getBytes()));
				
				//Build the string to be displayed
				StringBuilder buffer = new StringBuilder();
				buffer.append("From: "+from+"\n\n");
				buffer.append("Received: "+date+"\n\n");
				buffer.append("Subject: "+subject+"\n\n\n");
				buffer.append(message);
				
				Activity currentActivity = (Activity)Registry.getActiveInstance().getContext();
				AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);
				builder.setMessage(buffer.toString())
				       .setCancelable(false).setTitle("Email")
				       .setNegativeButton("Delete", new DialogInterface.OnClickListener() 
				       {
				           public void onClick(DialogInterface dialog, int id) 
				           {
				        	   dialog.dismiss();
				        	   
				        	   //Delete this Email instance. This CRUD operation is then seamlessly
				        	   //synchronized back with the Cloud
				        	   activeBean.delete();
				        	   NavigationContext.getInstance().home();
				           }
				       })
				       .setNeutralButton("Close", new DialogInterface.OnClickListener() 
				       {
				           public void onClick(DialogInterface dialog, int id) 
				           {
				                dialog.dismiss();
				           }
				       });
				
				AlertDialog alert = builder.create();
				alert.show();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
}
