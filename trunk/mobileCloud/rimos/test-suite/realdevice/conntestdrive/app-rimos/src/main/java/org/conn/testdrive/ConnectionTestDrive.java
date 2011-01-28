/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.conn.testdrive;

import java.util.Vector;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.system.DeviceInfo;

import org.conn.testdrive.connection.NetworkConnector;
import org.conn.testdrive.connection.NetSession;
import org.conn.testdrive.connection.NotificationListener;

/**
 * @author openmobster@gmail
 * 
 */
public class ConnectionTestDrive extends UiApplication
{
	private boolean isActivated = false;
	
	public static void main(String[] args)
	{
		UiApplication app = new ConnectionTestDrive();
		app.enterEventDispatcher();
	}

	public void activate()
	{
		if(!this.isActivated)
		{
			this.pushScreen(AppScreen.getInstance());
			this.isActivated = true;
		}
		else
		{
			UiApplication.getUiApplication().requestForeground();
		}
	}

	// create a new screen that extends MainScreen, which provides
	// default standard behavior for BlackBerry applications
	final static class AppScreen extends MainScreen
	{
		private static AppScreen singleton;
		
		private NotificationListener pushListener;
		
		private AppScreen()
		{
			// invoke the MainScreen constructor
			super();

			// add a title to the screen
			LabelField title = new LabelField("Connection TestDrive - Push Infinite",
					LabelField.ELLIPSIS | LabelField.USE_ALL_WIDTH);
			setTitle(title);
			
			this.setupMenu();
		}
		
		public static AppScreen getInstance()
		{
			if(AppScreen.singleton == null)
			{
				synchronized(AppScreen.class)
				{
					if(AppScreen.singleton == null)
					{
						AppScreen.singleton = new AppScreen();
					}
				}
			}
			return AppScreen.singleton;
		}
		
		public boolean onClose()
        {
			UiApplication.getUiApplication().requestBackground();
            return true;
        }
		
		private void setupMenu()
		{
			MenuItem simpledb = new MenuItem("Google Direct TCP", 1, 1){
				public void run()
				{
					//UserInteraction/Event Processing...this is where the Commands can be executed
					AppScreen.this.handleSimpleGoogleConnection();
				}
			}; 
			
			MenuItem cloudConnect = new MenuItem("Cloud Direct TCP", 1, 1){
				public void run()
				{
					//UserInteraction/Event Processing...this is where the Commands can be executed
					AppScreen.this.handleCloudConnection();
				}
			}; 
			
			MenuItem startPush = new MenuItem("Start Push", 1, 1){
				public void run()
				{
					//UserInteraction/Event Processing...this is where the Commands can be executed
					AppScreen.this.startPush();
				}
			}; 
			
			MenuItem stopPush = new MenuItem("Stop Push", 1, 1){
				public void run()
				{
					//UserInteraction/Event Processing...this is where the Commands can be executed
					AppScreen.this.stopPush();
				}
			}; 
			
			MenuItem pushStatus = new MenuItem("Push Status", 1, 1){
				public void run()
				{
					//UserInteraction/Event Processing...this is where the Commands can be executed
					AppScreen.this.pushStatus();
				}
			}; 
			
			MenuItem viewPush = new MenuItem("View Pushed Content", 1, 1){
				public void run()
				{
					//UserInteraction/Event Processing...this is where the Commands can be executed
					AppScreen.this.viewPush();
				}
			}; 
			
			MenuItem viewBatteryLevel = new MenuItem("View Battery Level", 1, 1){
				public void run()
				{
					//UserInteraction/Event Processing...this is where the Commands can be executed
					AppScreen.this.viewBatteryLevel();
				}
			}; 
											
			this.addMenuItem(simpledb);	
			this.addMenuItem(cloudConnect);
			this.addMenuItem(startPush);
			this.addMenuItem(stopPush);
			this.addMenuItem(pushStatus);
			this.addMenuItem(viewPush);
			this.addMenuItem(viewBatteryLevel);
		}
		
		private void handleSimpleGoogleConnection()
		{			
			try
			{
				NetSession session = null;
				String url = "www.google.com";
				String payload = "GET / HTTP/1.0\r\n\r\n";
				
				NetworkConnector connector = NetworkConnector.getInstance(url, "80");
				session = connector.openSession(false);
				
				String response = session.sendPayloadTwoWay(payload);
				
				Dialog.alert(url+": "+response);				
			}
			catch(Exception e)
			{				
				Dialog.alert("Error: "+e.toString());
			}	
		}
		
		private void handleCloudConnection()
		{
			NetSession session = null;
			try
			{
				LabelField serverLabel = new LabelField("IP :", LabelField.ELLIPSIS);
				BasicEditField serverField = new BasicEditField(BasicEditField.FILTER_URL);
				Dialog serverDialog = new Dialog(Dialog.D_OK,"Cloud Server IP",0,null,0);
				serverDialog.add(serverLabel);
				serverDialog.add(serverField);
				serverDialog.doModal();
				
				Status.show("Connecting to the CloudServer...............");
				
				String url = serverField.getText();
				String init = "processorid=/testdrive/pull";
				
				NetworkConnector connector = NetworkConnector.getInstance(url, "1502");
				session = connector.openSession(false);
				
				String response = session.establishCloudSession(init);
				
				if(response.indexOf("status=200")!=-1)
				{
					String payload = "<pull><caller name='blackberry'/></pull>";
					response = session.sendTwoWayCloudPayload(payload);
					
					Dialog.alert(url+": "+response);
				}
				else
				{
					Dialog.alert(url+"(Error): "+response);
				}												
			}
			catch(Exception e)
			{				
				Dialog.alert("Error: "+e.toString());
			}
			finally
			{
				if(session != null)
				{
					session.close();
				}
			}
		}
		
		private void startPush()
		{
			try
			{
				if(this.pushListener == null)
				{
					LabelField serverLabel = new LabelField("IP :", LabelField.ELLIPSIS);
					BasicEditField serverField = new BasicEditField(BasicEditField.FILTER_URL);
					Dialog serverDialog = new Dialog(Dialog.D_OK,"Cloud Server IP",0,null,0);
					serverDialog.add(serverLabel);
					serverDialog.add(serverField);
					serverDialog.doModal();
					
					this.pushListener = NotificationListener.getInstance(serverField.getText(), "1502");
				}
				
				if(this.pushListener.isStopped())
				{
					this.pushListener.start();
				}
				else
				{
					this.pushListener.triggerPush();
				}
				
				Status.show("Starting Push...", 2000);
			}
			catch(Exception e)
			{
				Dialog.alert("Error: "+e.toString());
			}
		}
		
		private void stopPush()
		{
			try
			{	
				if(this.pushListener != null)
				{
					this.pushListener.stop();
					
					Status.show("Stopping Push...", 2000);
				}
			}
			catch(Exception e)
			{
				Dialog.alert("Error: "+e.toString());
			}
		}
		
		private void pushStatus()
		{
			if(this.pushListener != null && !this.pushListener.isStopped())
			{
				Status.show("Push is Active!!", 2000);				
			}
			else
			{
				Status.show("Push is InActive!!", 2000);
			}
		}
		
		private void viewPush()
		{
			if(this.pushListener != null && !this.pushListener.isStopped())
			{
				StringBuffer buffer = new StringBuffer();
				Vector updates = this.pushListener.getUpdates();
				int size = updates.size();
				
				for(int i=0; i<size; i++)
				{
					String update = (String)updates.elementAt(i);
					buffer.append(update+"\n");
				}
				this.pushListener.clear();
				
				Dialog.alert(buffer.toString());
			}
		}
		
		private void viewBatteryLevel()
		{
			Dialog.alert("Battery Level: "+DeviceInfo.getBatteryLevel());
		}
	}
}
