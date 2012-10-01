/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.console.server;

/**
 * 
 * @author openmobster@gmail.com
 */
public final class Server 
{
	private static Server singleton;
	
	private boolean isStarted;
	
	private Server()
	{
		
	}
	
	public static Server getInstance()
	{
		if(Server.singleton == null)
		{
			synchronized(Server.class)
			{
				if(Server.singleton == null)
				{
					Server.singleton = new Server();
				}
			}
		}
		return Server.singleton;
	}
	
	public synchronized void start()
	{
		if(isStarted)
		{
			return;
		}
		
		//Start HibernateManager
		HibernateManager hibernateManager = HibernateManager.getInstance();
		hibernateManager.setConfig("cs-hibernate.cfg.xml");
		hibernateManager.create();
		hibernateManager.start();
		
		isStarted = true;
	}
	
	public synchronized void stop()
	{
		HibernateManager hibernateManager = HibernateManager.getInstance();
		hibernateManager.stop();
		hibernateManager.destroy();
		
		isStarted = false;
	}
}
