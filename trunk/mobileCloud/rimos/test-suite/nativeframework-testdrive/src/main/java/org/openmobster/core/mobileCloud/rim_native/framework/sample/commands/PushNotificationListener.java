/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.rim_native.framework.sample.commands;

import net.rim.device.api.system.Application;
import net.rim.device.api.notification.NotificationsEngineListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.blackberry.api.homescreen.HomeScreen;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiEngine;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.system.EncodedImage;

import org.openmobster.core.mobileCloud.api.ui.framework.Services;

/**
 * @author openmobster@gmail
 *
 */
public class PushNotificationListener implements NotificationsEngineListener
{
	private UiApplication app;
	
	public PushNotificationListener()
	{
		this.app = UiApplication.getUiApplication();
	}
	
	public void deferredEventWasSuperseded(long sourceID, long eventID, Object eventReference, Object context)
	{
		System.out.println("-----------------------------------------------------------------");
		System.out.println("DeferredEvent was superseded...........................................");
		System.out.println("-----------------------------------------------------------------");
	}

	public void notificationsEngineStateChanged(int stateInt, long sourceID, long eventID, Object eventReference, Object context)
	{	
		System.out.println("-----------------------------------------------------------------");
		System.out.println("NotificationEngineStateChanged...........................................");
		System.out.println("-----------------------------------------------------------------");
	}

	public void proceedWithDeferredEvent(final long sourceID, final long eventID, final Object eventReference, final Object context)
	{
		System.out.println("-----------------------------------------------------------------");
		System.out.println("DeferredEvent occurred...........................................");
		System.out.println("About to sleep-----------------------------------------------------------------");
		
		try{Thread.currentThread().sleep(5000);}catch(Exception e){}
		
		System.out.println("WokeUP.................................................................");
		
		app.invokeLater(new Runnable(){
				public void run()
				{
					System.out.println("Invoke Later: "+app.getClass());
					HomeScreen.setName("push");
					
					EncodedImage pushIcon = (EncodedImage)Services.getInstance().getResources().getImage("/system/images/push.png");
					HomeScreen.updateIcon(pushIcon.getBitmap());
					
					/*UiEngine ui = Ui.getUiEngine();
					Screen screen = new Dialog(Dialog.D_OK, "Look out!!!",Dialog.OK,Bitmap.getPredefinedBitmap(Bitmap.EXCLAMATION),
					Manager.VERTICAL_SCROLL);
					ui.pushGlobalScreen(screen, 1, UiEngine.GLOBAL_QUEUE);*/
				}
		});
	}
}
