/**
 * Copyright (c) {2003,2009} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.hello.mobster.screen;

import java.util.Vector;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;

import org.openmobster.core.mobileCloud.api.ui.framework.navigation.NavigationContext;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.Screen;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;

import org.openmobster.core.mobileCloud.rim_native.framework.AppScreen;

import org.openmobster.hello.mobster.domain.Tweet;

/**
 * Screen used to display the tweets that are fetched from the Cloud
 * 
 * @author openmobster@gmail.com
 */
public class TweetsScreen extends Screen
{		
	private MainScreen screen;
	private ListField listField;
	
	public TweetsScreen()
	{									
	}
	
	public void render()
	{
		//Fetching the list of tweets from the MVC State Management system
		//The tweets are associated with this screen
		NavigationContext navigation = NavigationContext.getInstance();
		Vector tweets = (Vector)navigation.getAttribute(this.getId(), "tweets");
		String twitterAccount = (String)navigation.getAttribute(this.getId(), "twitterAccount");
		
		//Setting up the MainScreen and its title
		this.screen = new AppScreen();		
		this.screen.setTitle("Tweets - "+twitterAccount);					
		
		//Add the 'List' of Actions that can be clicked on for this App
		this.listField = new ListField(tweets.size());
		this.listField.setCallback(new ListFieldCallbackImpl(tweets));						
		this.screen.add(listField);
		
		//SetUp Menu Items that user can click and interact to start a process
		this.setMenuItems();
	}
	
	public Object getContentPane() 
	{		
		return this.screen;
	}
	//--------------------------------------------------------------------------------------------------------------------------------------
	private void setMenuItems()
	{							
		MenuItem details = new MenuItem("Details", 1, 1){
			public void run()
			{
				//GUI Event Handling code. This is executed in response to a user interaction with the UI component in question
				//This code always executes on the Event Dispatch Thread (EDT)
				
				//Get details about the Tweet selected by the user
				int selectedIndex = TweetsScreen.this.listField.getSelectedIndex();
				
				NavigationContext navigation = Services.getInstance().getNavigationContext();
				Vector tweets = (Vector)navigation.getAttribute(TweetsScreen.this.getId(), "tweets");
				
				//Display the Tweet to the user in a Dialog box
				Tweet tweet = (Tweet)tweets.elementAt(selectedIndex);				
				Dialog.alert(tweet.getDetails());
			}
		}; 
		
		MenuItem back = new MenuItem("Back", 2, 2){
			public void run()
			{
				Services.getInstance().getNavigationContext().back();
			}
		}; 
										
		this.screen.addMenuItem(details);
		this.screen.addMenuItem(back);
	}
	
	private static class ListFieldCallbackImpl implements ListFieldCallback
	{
		private Vector tweets = new Vector();
		
		private ListFieldCallbackImpl(Vector tweets)
		{			
			this.tweets = tweets;			
		}

		public void drawListRow(ListField listField, Graphics graphics, int index,
		int y, int width) 
		{
			Tweet tweet = (Tweet)this.tweets.elementAt(index);									
			graphics.drawText(tweet.getTitle(), 0, y);
		}
		
		public Object get(ListField listField, int index) 
		{			
			return this.tweets.elementAt(index);
		}

		public int getPreferredWidth(ListField listField) 
		{			
			return Display.getWidth();
		}

		public int indexOfList(ListField listField, String prefix, int start) 
		{			
			return this.tweets.indexOf(prefix, start);
		}		
	}		
}
