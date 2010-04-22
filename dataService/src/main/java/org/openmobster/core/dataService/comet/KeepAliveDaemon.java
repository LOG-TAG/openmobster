/**
 * 
 */
package org.openmobster.core.dataService.comet;

import org.apache.log4j.Logger;
import java.util.TimerTask;

/**
 * @author openmobster@gmail
 *
 */
class KeepAliveDaemon extends TimerTask
{
	private static Logger log = Logger.getLogger(KeepAliveDaemon.class);
	
	private CometSession session;
	private long pulseInterval;
	
	KeepAliveDaemon(long pulseInterval,CometSession session)
	{
		if(session == null)
		{
			throw new IllegalArgumentException("CometSession should not be null!!");
		}
		
		this.session = session;
		this.pulseInterval = pulseInterval;
	}
	
	@Override
	public void run()
	{
		if(this.session.isActive())
		{
			log.info("---------------------------------------------------------------");
			log.info("Sender: "+this.hashCode());
			log.info("Sending a KeepAlive HeartBeat Every: ("+this.pulseInterval+" ms)");
			log.info("---------------------------------------------------------------");
			this.session.sendHeartBeat();
		}
		else
		{
			log.info("-----------------------------------------");
			log.info("KeepAlive Daemon is done!!!");
			log.info("-----------------------------------------");
			this.cancel(); //unschedules its execution
		}
	}	
}
