/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.app;

import java.lang.reflect.Field;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;

import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;

/**
 * @author openmobster@gmail.com
 * 
 */
public class MainActivity extends Activity
{
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
	}

	@Override
	protected void onResume()
	{
		try
		{
			super.onResume();
			
			//render the main screen
			String layoutClass = this.getPackageName()+".R$layout";
			String main = "main";
			Class clazz = Class.forName(layoutClass);
			Field field = clazz.getField(main);
			int screenId = field.getInt(clazz);
			this.setContentView(screenId);
			
			this.setupNotificationButtons();
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
	}
	
	private void setupNotificationButtons()
	{
		//Simple Notification
		Button simpleNotification = (Button)ViewHelper.findViewById(this, "simpleNotification");
		simpleNotification.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view) 
			{
				//Get the Notification Service
				NotificationManager notifier = (NotificationManager)MainActivity.this.
				getSystemService(Context.NOTIFICATION_SERVICE);
				
				//Get the icon for the notification
				int icon = ViewHelper.findDrawableId(MainActivity.this, "push");
				Notification notification = new Notification(icon,"Simple Notification",System.currentTimeMillis());
				
				//Setup the Intent to open this Activity when clicked
				Intent toLaunch = new Intent(MainActivity.this,MainActivity.class);
				PendingIntent contentIntent = PendingIntent.getActivity(MainActivity.this, 0, toLaunch, 0);
				
				//Set the Notification Info
				notification.setLatestEventInfo(MainActivity.this, "Hi!!", "This is a simple notification", contentIntent);
				
				//Setting Notification Flags
				notification.flags |= Notification.FLAG_AUTO_CANCEL;
				notification.flags |= Notification.DEFAULT_SOUND;
				
				//Send the notification
				notifier.notify(0x007, notification);
			}
		});
		
		//Update Notification
		Button updateNotification = (Button)ViewHelper.findViewById(this, "updateNotification");
		updateNotification.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view) 
			{
				//Get the Notification Service
				NotificationManager notifier = (NotificationManager)MainActivity.this.
				getSystemService(Context.NOTIFICATION_SERVICE);
				
				//Get the icon for the notification
				int icon = ViewHelper.findDrawableId(MainActivity.this, "push");
				Notification notification = new Notification(icon,"Simple Notification",System.currentTimeMillis());
				
				//Setup the Intent to open this Activity when clicked
				Intent toLaunch = new Intent(MainActivity.this,MainActivity.class);
				PendingIntent contentIntent = PendingIntent.getActivity(MainActivity.this, 0, toLaunch, 0);
				
				//Set the Notification Info
				notification.setLatestEventInfo(MainActivity.this, "Hello!!", "This is an update notification", contentIntent);
				
				//Set a number on the Status Bar
				notification.number = 2;
				
				//Setting Notification Flags
				notification.flags |= Notification.FLAG_AUTO_CANCEL;
				notification.flags |= Notification.DEFAULT_SOUND;
				
				//Send the notification
				notifier.notify(0x007, notification);
			}
		});
		
		//Custom Sound Notification
		Button customSound = (Button)ViewHelper.findViewById(this, "customSound");
		customSound.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view) 
			{
				//Get the Notification Service
				NotificationManager notifier = (NotificationManager)MainActivity.this.
				getSystemService(Context.NOTIFICATION_SERVICE);
				
				//Get the icon for the notification
				int icon = ViewHelper.findDrawableId(MainActivity.this, "push");
				Notification notification = new Notification(icon,"Custom Sound Notification",System.currentTimeMillis());
				
				//Setup the Intent to open this Activity when clicked
				Intent toLaunch = new Intent(MainActivity.this,MainActivity.class);
				PendingIntent contentIntent = PendingIntent.getActivity(MainActivity.this, 0, toLaunch, 0);
				
				//Set the Notification Info
				notification.setLatestEventInfo(MainActivity.this, "Hi!!", "This is a custom sound notification", contentIntent);
				
				//Setting Notification Flags
				notification.flags |= Notification.FLAG_AUTO_CANCEL;
				
				//Adding the Custom Sound
				notification.audioStreamType = AudioManager.STREAM_NOTIFICATION;
				String uri = "android.resource://org.openmobster.notify.android.app/"+
				MainActivity.this.findSoundId(MainActivity.this, "beep");
				notification.sound = Uri.parse(uri);
				
				System.out.println("**************************************");
				System.out.println("Uri: "+uri);
				System.out.println("**************************************");
				
				//Send the notification
				notifier.notify(0x009, notification);
			}
		});
	}
	
	private int findSoundId(Activity activity, String variable)
	{
		try
		{
			String idClass = activity.getPackageName()+".R$raw";
			Class clazz = Class.forName(idClass);
			Field field = clazz.getField(variable);
			
			return field.getInt(clazz);
		}
		catch(Exception e)
		{
			return -1;
		}
	}
}
