/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.db.testdrive;

import java.util.Enumeration;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;

import org.db.testdrive.storage.Database;

/**
 * @author openmobster@gmail
 * 
 */
public class DBTestDrive extends UiApplication
{
	public static void main(String[] args)
	{
		UiApplication app = new DBTestDrive();
		app.enterEventDispatcher();
	}

	public void activate()
	{
		this.pushScreen(new AppScreen());
	}

	// create a new screen that extends MainScreen, which provides
	// default standard behavior for BlackBerry applications
	final class AppScreen extends MainScreen
	{
		public AppScreen()
		{
			// invoke the MainScreen constructor
			super();

			// add a title to the screen
			LabelField title = new LabelField("DB TestDrive",
					LabelField.ELLIPSIS | LabelField.USE_ALL_WIDTH);
			setTitle(title);
			
			this.setupMenu();
		}
		
		private void setupMenu()
		{
			MenuItem simpledb = new MenuItem("Simple DB Usecase", 1, 1){
				public void run()
				{
					//UserInteraction/Event Processing...this is where the Commands can be executed
					AppScreen.this.handleSimpleDBUsecase();
				}
			}; 
											
			this.addMenuItem(simpledb);	
		}
		
		private void handleSimpleDBUsecase()
		{
			try
			{
				//System.out.println("1-------------");
				Database database = Database.getInstance();
				//System.out.println("2-------------");
				Enumeration tables = database.enumerateTables();
				//System.out.println("3-------------");
				StringBuffer buffer = new StringBuffer();
				//System.out.println("4-------------");
				
				while(tables.hasMoreElements())
				{
					//System.out.println("5-------------");
					String found = (String)tables.nextElement();
					//System.out.println("6-------------");
					buffer.append(found+"\n");
					//System.out.println("7-------------");
				}
				
				//System.out.println("8-------------");
				Dialog.alert("Bootup Tables: "+buffer.toString());
				//System.out.println("9-------------");
			}
			catch(Exception e)
			{
				//System.out.println("----------------------------------------------");
				//System.out.println("Exception: "+e.toString());
				//System.out.println("----------------------------------------------");
				Dialog.alert("Error: "+e.toString());
			}			
		}
	}
}
