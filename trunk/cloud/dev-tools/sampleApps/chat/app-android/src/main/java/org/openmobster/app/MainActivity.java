/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.app;

import java.util.ArrayList;
import java.util.List;
import org.openmobster.android.api.d2d.D2DActivity;
import org.openmobster.android.api.d2d.D2DMessage;
import org.openmobster.android.api.d2d.D2DService;
import org.openmobster.core.mobileCloud.android.configuration.Configuration;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android_native.framework.CloudService;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;
import org.openmobster.core.mobileCloud.mgr.AppActivation;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.chat.android.app.R;

/**
 * @author openmobster@gmail.com
 * 
 */

public class MainActivity extends D2DActivity
{
	public static MainActivity mainActivity;
	
	//The user with whom a chat session must be started
	private String to;
	
	public MainActivity()
	{
		
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
			
			//check if App activation is needed
			Configuration conf = Configuration.getInstance(Registry.getActiveInstance().getContext());
			if(!conf.isActive())
			{
				AppActivation appActivation = AppActivation.getInstance(this);
				appActivation.start();
				return;
			}
			
			//See intent for message details...This is to read a D2DMessage sent via the Notification system
			Intent intent = this.getIntent();
			String details = intent.getStringExtra("detail");
			
			System.out.println("****************************");
			System.out.println("Detail: "+details);
			System.out.println("****************************");
			
			//Display the message if one is found
			if(details != null)
			{
				D2DMessage msg = D2DMessage.parse(details);
				if(msg != null)
				{
					this.callback(msg);
				}
			}
			
			this.setContentView(R.layout.main);
			
			//Send Button
			final Button send = (Button)findViewById(R.id.send);
			final EditText message = (EditText)findViewById(R.id.message);
			send.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View button) 
				{
					try
					{
						//Get the typed in message and clear the text box
						String chat = message.getText().toString();
						message.setText("");
						
						//Send a chat message if a user to whom to communicate with is found
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
			
			//Users button. Displays a list of activated users on the system with whom a chat session can be established
			final Button users = (Button)findViewById(R.id.users);
			users.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View button) 
				{
					try
					{
						final Activity activity = MainActivity.this;
						
						//Get a list of activated users from the Cloud
						List<String> users = D2DService.getInstance().userList();
						if(users == null)
						{
							users = new ArrayList<String>();
						}
						
						//Setup an Alert Dialog to show the user and here they can be selected to establish a chat session
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
			
			if(this.to != null)
			{
				TextView user = (TextView)findViewById(R.id.user);
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
		//Append a newly received message from the chat system
		LinearLayout layout = (LinearLayout)findViewById(R.id.layout);
		TextView chatView = new TextView(MainActivity.this);
		chatView.setText(message.getMessage());
		layout.addView(chatView);
		
		Toast.makeText(MainActivity.this, message.getMessage(), Toast.LENGTH_SHORT).show();
		
		TextView user = (TextView)findViewById(R.id.user);
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
			
			//Select the user with whom communication will be done
			String selectedUser = this.users.get(status);
			
			//Display this selected user
			TextView user = (TextView)ViewHelper.findViewById(activity, "user");
			user.setText(selectedUser);
			
			//Establish a chat session with this user
			MainActivity.this.to = selectedUser;
		}
	}
}