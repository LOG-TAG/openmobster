/**
 * Copyright (c) {2003,2009} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.hello.mobster.command;

import java.util.Vector;

import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.Dialog;

import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.RemoteCommand;
import org.openmobster.core.mobileCloud.api.ui.framework.command.AppException;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.NavigationContext;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;

import org.openmobster.core.mobileCloud.api.service.Request;
import org.openmobster.core.mobileCloud.api.service.Response;
import org.openmobster.core.mobileCloud.api.service.MobileService;

import org.openmobster.hello.mobster.domain.Tweet;

/**
 * A 'RemoteCommand' which is a component of the Command Framework
 * 
 * Being a 'RemoteCommand' the MVC Framework makes sure its properly managed with respect to the Event Dispatch Thread (EDT).
 * It makes sure that when a 'RemoteCommand' is executing, it does not freeze the UI
 * 
 * @author openmobster@gmail.com
 *
 */
public final class TwitterCommand implements RemoteCommand
{
	/**
	 * Contract Explanation
	 * 
	 * 'doViewBefore' is called before the actual action associated with the command is executed.
	 * 
	 * This method is invoked on the "Event Dispatch Thread" (EDT), so it is fine to execute UI related code
	 * inside this method such as "Showing Dialog Box", "Asking User to input some information" etc
	 * 
	 * This is called as a Pre-Processing step to do something UI related before the actual action is executed
	 * 
	 */
	public void doViewBefore(CommandContext commandContext)
	{	
		//Show Dialog to get the user's twitter account
		Dialog accountDialog = new Dialog(Dialog.D_OK,"Twitter Account",0,null,0);
		BasicEditField accountField = new BasicEditField(BasicEditField.FILTER_EMAIL);
		accountDialog.add(accountField);
		
		//Show the Dialog. Its shown as Modal, so the EDT thread waits here till user is done inputing the data
		accountDialog.doModal();
		
		//Process the user's input about the twitter account
		String twitterAccount = accountField.getText();
		
		//Provide this information to the commandContext for processing
		commandContext.setAttribute("twitterAccount", twitterAccount);
	}

	/**
	 * Contract Explanation
	 * 
	 * 'doAction' is called to execute the actual business process or service call. This does not execute on the Event Dispatch Thread
	 * (EDT). Hence, the code executed within this method should only stick to non-UI related logic of the App.
	 * 
	 * In this example, it makes an RPC call to the remote TwitterService
	 */
	public void doAction(CommandContext commandContext) 
	{
		try
		{
			//Read the twitter account whose tweets should be read
			String twitterAccount = (String)commandContext.getAttribute("twitterAccount");
			
			//Assemble a MobileRPC Request for the Twitter Service in the Cloud
			Request request = new Request("/twitter/tweets");	
			request.setAttribute("account", twitterAccount);
			Response response = new MobileService().invoke(request);
			
			//Read the tweets received from the RPC call
			//RPC call returns a "List" of "Properties" associated with the Tweet object
			//A list of "Tweet" objects are re-assembled from the RPC payload here
			Vector titles = response.getListAttribute("title");
			Vector times = response.getListAttribute("time");
			Vector details = response.getListAttribute("detail");
			Vector tweets = new Vector();
			for(int i=0,size=titles.size(); i<size; i++)
			{
				String title = (String)titles.elementAt(i);
				String time = (String)times.elementAt(i);
				String detail = (String)details.elementAt(i);
				
				Tweet tweet = new Tweet();
				tweet.setTitle(title);
				tweet.setTime(time);
				tweet.setDetails(detail);
				tweets.addElement(tweet);
			}
									
			//Display the returned 'tweets' on the "tweets" screen by establishing its state
			//and navigating to it
			NavigationContext navigation = NavigationContext.getInstance();
			
			//associates the tweets only with the TweetsScreen represented by a unique "tweets" id in moblet-app.xml
			navigation.setAttribute("tweets", "tweets", tweets);
			navigation.setAttribute("tweets", "twitterAccount", twitterAccount);
		}
		catch(Exception e)
		{
			AppException exception = new AppException();
			exception.setMessage(e.getMessage());
			throw exception;
		}
	}	
	
	/**
	 * Contract Explanation
	 * 
	 * 'doViewAfter' is called after the actual action associated with the command is executed.
	 * 
	 * This method is invoked on the "Event Dispatch Thread" (EDT), so it is fine to execute UI related code
	 * inside this method such as "Showing Dialog Box", "Navigating to the next screen in your App Flow", etc
	 * 
	 * This is called as a Post-Processing step to do something UI related
	 * 
	 */
	public void doViewAfter(CommandContext commandContext)
	{
		NavigationContext navigation = Services.getInstance().getNavigationContext();
		navigation.navigate("tweets");
	}
	
	/**
	 * Contract Explanation
	 * 
	 * 'doViewError' is called to show an error messages if there was some exception that occurred during the execution of the action
	 * 
	 * This method is invoked on the "Event Dispatch Thread" (EDT), so it is fine to execute UI related code
	 * inside this method such as "Showing Dialog Box", "Navigating to the next screen in your App Flow", etc
	 * 
	 * This is called as a Post-Processing step to do something UI related if an error occurs during the execution of the action
	 * 
	 */
	public void doViewError(CommandContext commandContext)
	{
		AppException exception = commandContext.getAppException();
		Dialog.alert(exception.getMessage());
	}
}
