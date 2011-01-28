/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.test.sync;

import org.openmobster.core.mobileCloud.rimos.module.mobileObject.MobileObjectDatabase;
import org.openmobster.core.mobileCloud.rimos.module.sync.engine.SyncDataSource;



/**
 * 
 * @author openmobster@gmail.com
 *
 */
public abstract class AbstractNotification extends AbstractSyncTest 
{		
	public void setUp()
	{
		this.getTestSuite().getContext().setAttribute("service", "emailConnector");
		this.service = (String)this.getTestSuite().getContext().getAttribute("service");
	}
	
	public void tearDown()
	{
		super.tearDown();
		this.getTestSuite().getContext().setAttribute("service", "testServerBean");
	}
		
	protected void setUp(String operation)
	{
		try
		{
			MobileObjectDatabase.getInstance().deleteAll(this.service);
			SyncDataSource.getInstance().clearAll();										
		}
		catch(Exception e)
		{
			throw new RuntimeException(e.toString());
		}
		finally
		{
			//reset the server side sync adapter
			this.setUpServerSideSync(operation);
		}
	}
	
	
	protected void setUpServerSideSync(String operation)
	{
		this.resetServerAdapter("setUp=Notification://"+this.getClass().getName()+"/"+operation+"\n");
	}			
}
