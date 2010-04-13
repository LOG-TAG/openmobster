/**
 * Copyright (c) {2003,2009} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.hello.mobster.rpc;

import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import org.openmobster.server.api.service.Request;
import org.openmobster.server.api.service.Response;
import org.openmobster.server.api.service.MobileServiceBean;
import org.openmobster.server.api.service.ServiceInfo;

/**
 * A "MobileServiceBean" used by the RPC framework to invoke business services from the mobile device.
 * 
 * This particular bean returns a list of tweets associated with the specified twitter account
 * 
 * @author openmobster@gmail.com
 */

//This URI uniquely identifies this MobileServiceBean. RPC calls from the device are addressed to this URI as the targetted 'service' for 
//invocation
@ServiceInfo(uri="/twitter/tweets")
public class TwitterService implements MobileServiceBean
{
	private static Logger log = Logger.getLogger(TwitterService.class);
	
	public TwitterService()
	{
		
	}
	
	//Invoked when the component is registered with the kernel
	public void start()
	{
		log.info("-----------------------------------------------");
		log.info("/twitter/tweets: was successfully started....");
		log.info("-----------------------------------------------");
	}
	
	//Invoked by the RPC framework
	public Response invoke(Request request) 
	{	
		//The Twitter account whose tweets need to be fetched
		String account = request.getAttribute("account");
		
		log.info("-------------------------------------------------");
		log.info("Accessing Tweets From: "+account);
		
		//The response that will be sent back to the caller
		Response response = new Response();
		
		//Read the tweets from a Data Source
		List<Tweet> tweets = TwitterDataSource.getTweets(account);
		
				
		//This code creates a "List" of each "Property" of the Tweet domain object
		//On the device side, these properties are then reassembled into a Tweet domain object
		//See the device side code under: "org.openmobster.hello.mobster.command.TwitterCommand" 
		//on how this re-assembly is done
		List<String> title = new ArrayList<String>();
		List<String> detail = new ArrayList<String>();
		List<String> time = new ArrayList<String>();
		for(Tweet tweet:tweets)
		{
			title.add(tweet.getTitle());
			detail.add(tweet.getDetails());
			time.add(tweet.getTime());
		}
		
		//Setup the payload that will be sent back inside the Response object
		//populating it with the "List" of tweet "Properties"
		response.setListAttribute("title", title);
		response.setListAttribute("detail", detail);
		response.setListAttribute("time", time);
		
		log.info("("+tweets.size()+") tweets sent...");
		log.info("-------------------------------------------------");
		
		return response;
	}
}
