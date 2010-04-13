/**
 * 
 */
package org.openmobster.hello.mobster.domain;

/**
 * A simple POJO representing a tweet
 * 
 * @author openmobster@gmail
 *
 */
public final class Tweet 
{
	private String title;
	private String details;
	private String time;
	
	public Tweet()
	{
		
	}

	public String getTitle() 
	{
		return title;
	}

	public void setTitle(String title) 
	{
		this.title = title;
	}

	public String getDetails() 
	{
		return details;
	}

	public void setDetails(String details) 
	{
		this.details = details;
	}

	public String getTime() 
	{
		return time;
	}

	public void setTime(String time) 
	{
		this.time = time;
	}
}
