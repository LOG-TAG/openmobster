/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.android.api.d2d;

import org.openmobster.core.mobileCloud.android.module.connection.Constants;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 *
 * @author openmobster@gmail.com
 */
public final class D2DReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		Bundle input = intent.getBundleExtra(Constants.d2dMessage);
		if(input == null)
		{
			//do nothing
			return;
		}
		
		String from = input.getString(Constants.from);
		String to = input.getString(Constants.to);
		String msg = input.getString(Constants.message);
		String source_deviceid = input.getString(Constants.source_deviceid);
		String destination_deviceid = input.getString(Constants.destination_deviceid);
		String timestamp = input.getString(Constants.timestamp);
		String app_id = input.getString(Constants.app_id);
		
		D2DMessage message = new D2DMessage();
		message.setFrom(from);
		message.setTo(to);
		message.setMessage(msg);
		message.setSenderDeviceId(source_deviceid);
		message.setTimestamp(timestamp);
		
		
		D2DSession session = D2DSession.getSession();
		if(session.isActive())
		{
			//App is in the foreground
			session.callback(message);
		}
		else
		{
			//App is in the background, send as a push notification
			String appId = context.getPackageName();
			Intent pushIntent = new Intent(appId);
			
			pushIntent.putExtra("message", message.getMessage());
			pushIntent.putExtra("title", "Device-To-Device Message");
			pushIntent.putExtra("detail", message.toString());
			pushIntent.putExtra("app-id", appId);
			
			context.sendBroadcast(pushIntent);
		}
	}
}
