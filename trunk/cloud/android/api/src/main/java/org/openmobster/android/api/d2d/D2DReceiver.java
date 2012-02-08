/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.android.api.d2d;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 *
 * @author openmobster@gmail.com
 */
public final class D2DReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		D2DMessage message = (D2DMessage)intent.getSerializableExtra("message");
		if(message == null)
		{
			//do nothing
			return;
		}
		
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
