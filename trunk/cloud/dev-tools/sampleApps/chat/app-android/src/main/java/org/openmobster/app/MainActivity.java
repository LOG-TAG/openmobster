/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.app;

import java.lang.reflect.Field;
import java.util.List;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.view.View;
import android.view.View.OnClickListener;

import org.openmobster.core.mobileCloud.android_native.framework.CloudService;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;

import org.openmobster.android.api.d2d.D2DActivity;
import org.openmobster.android.api.d2d.D2DMessage;
import org.openmobster.android.api.d2d.D2DService;

/**
 * @author openmobster@gmail.com
 * 
 */
public class MainActivity extends D2DActivity
{
	public static MainActivity mainActivity;
	
	private String to;
	
	public MainActivity()
	{
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		
		CloudService.getInstance().start(this);
	}

	@Override
	protected void onResume()
	{
		try
		{
			super.onResume();
			
			//See intent for message details
			Intent intent = this.getIntent();
			String details = intent.getStringExtra("detail");
			
			System.out.println("****************************");
			System.out.println("Detail: "+details);
			System.out.println("****************************");
			
			//render the main screen
			String layoutClass = this.getPackageName()+".R$layout";
			String main = "main";
			Class clazz = Class.forName(layoutClass);
			Field field = clazz.getField(main);
			int screenId = field.getInt(clazz);
			this.setContentView(screenId);
			
			//Send Button
			final Button send = (Button)ViewHelper.findViewById(this, "send");
			final EditText message = (EditText)ViewHelper.findViewById(this, "message");
			send.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View button) 
				{
					try
					{
						final Activity activity = MainActivity.this;
						
						String chat = message.getText().toString();
						message.setText("");
						
						D2DService service = D2DService.getInstance();
						
						if(MainActivity.this.to != null)
						{
							service.send(MainActivity.this.to, chat);
						}
					}
					catch(Exception e)
					{
						e.printStackTrace(System.out);
					}
				}
			});
			
			//User button
			final Button users = (Button)ViewHelper.findViewById(this, "users");
			users.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View button) 
				{
					try
					{
						final Activity activity = MainActivity.this;
						
						List<String> users = D2DService.getInstance().userList();
						if(users == null)
						{
							users = new ArrayList<String>();
						}
						
						UserSelectListener listener = new UserSelectListener(users);
						
						AlertDialog dialog = new AlertDialog.Builder(activity).
								setItems(users.toArray(new String[0]), listener).
						    	setCancelable(false).
						    	create();
						dialog.setTitle("Select User to Communicate With");
						dialog.show();
					}
					catch(Exception e)
					{
						e.printStackTrace(System.out);
					}
				}
			});
			
			if(details != null)
			{
				D2DMessage msg = D2DMessage.parse(details);
				if(msg != null)
				{
					this.callback(msg);
				}
			}
			
			if(this.to != null)
			{
				TextView user = (TextView)ViewHelper.findViewById(this, "user");
				user.setText(this.to);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
	}
	
	
	public void callback(D2DMessage message)
	{
		LinearLayout layout = (LinearLayout)ViewHelper.findViewById(MainActivity.this, "layout");
		TextView chatView = new TextView(MainActivity.this);
		chatView.setText(message.getMessage());
		layout.addView(chatView);
		
		Toast.makeText(MainActivity.this, message.getMessage(), Toast.LENGTH_SHORT).show();
		
		TextView user = (TextView)ViewHelper.findViewById(this, "user");
		user.setText(message.getFrom());
		this.to = message.getFrom();
	}
	
	private class UserSelectListener implements DialogInterface.OnClickListener
	{
		private List<String> users;
		
		private UserSelectListener(List<String> users)
		{
			this.users = users;
		}
		
		public void onClick(DialogInterface dialog, int status)
		{
			final Activity activity = MainActivity.this;
			
			String selectedUser = this.users.get(status);
			
			TextView user = (TextView)ViewHelper.findViewById(activity, "user");
			user.setText(selectedUser);
			
			MainActivity.this.to = selectedUser;
		}
	}
}
