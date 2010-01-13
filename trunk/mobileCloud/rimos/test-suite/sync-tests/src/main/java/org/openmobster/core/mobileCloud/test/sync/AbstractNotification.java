/******************************************************************************
 * OpenMobster                                                                *
 * Copyright 2008, OpenMobster, and individual                                *
 * contributors as indicated by the @authors tag. See the                     *
 * copyright.txt in the distribution for a full listing of                    *
 * individual contributors.                                                   *
 *                                                                            *
 * This is free software; you can redistribute it and/or modify it            *
 * under the terms of the GNU Lesser General Public License as                *
 * published by the Free Software Foundation; either version 2.1 of           *
 * the License, or (at your option) any later version.                        *
 *                                                                            *
 * This software is distributed in the hope that it will be useful,           *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU           *
 * Lesser General Public License for more details.                            *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public           *
 * License along with this software; if not, write to the Free                *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA         *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.                   *
 ******************************************************************************/
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
