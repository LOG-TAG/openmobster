/**
 * 
 */
package org.openmobster.hello.mobster.rpc;

import java.util.List;
import java.util.ArrayList;

/**
 * A data source for getting tweets associated with a twitter account. 
 * 
 * This is just a mock implementation. A real implementation would probably grab the tweets using some Twitter Platform API, RSS Feed, or may be
 * some other mechanism
 * 
 * @author openmobster@gmail
 *
 */
public class TwitterDataSource 
{
	public static List<Tweet> getTweets(String twitterAccount)
	{
		List<Tweet> tweets = new ArrayList<Tweet>();
		
		//Just using stub data for the sake of this demo
		//But this can be implemented to pull the actual tweets from the twitter database
		//using Twitter Platform APIs
		for(int i=0; i<3; i++)
		{
			Tweet tweet = new Tweet();
			
			switch(i)
			{
				case 0:
					tweet.setTitle("iPad Launched");
					tweet.setDetails("iPad Launched with lots of success. They sold a thousand gazillion on the first day!!");
					tweet.setTime(""+System.currentTimeMillis());
				break;
				
				case 1:
					tweet.setTitle("Windows Phone 7 Announced");
					tweet.setDetails("Microsoft back in the game with Windows Phone 7. Rightly so, the platform does not care much about Windows Mobile 6.5!!");
					tweet.setTime(""+System.currentTimeMillis());
				break;
				
				case 2:
					tweet.setTitle("Android grows");
					tweet.setDetails("Android platform is snagging up lots of developers. Way to go. Details: http://blah.bit.ly");
					tweet.setTime(""+System.currentTimeMillis());
				break;
			}
			
			tweets.add(tweet);
		}
		
		return tweets;
	}
}
