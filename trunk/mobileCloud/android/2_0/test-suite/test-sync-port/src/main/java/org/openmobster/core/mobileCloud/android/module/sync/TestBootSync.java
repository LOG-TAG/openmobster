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
package org.openmobster.core.mobileCloud.android.module.sync;


/**
 * 
 * @author openmobster@gmail.com
 *
 */
public final class TestBootSync extends AbstractSyncTest 
{		
	/**
	 * 
	 */
	public void runTest()
	{
		try
		{
			this.setUp("add");
			SyncService.getInstance().performTwoWaySync(service, service, false);					
			this.assertRecordPresence("unique-1", "/TestTwoWaySync/add");
			this.assertRecordPresence("unique-2", "/TestTwoWaySync/add");
			this.assertRecordPresence("unique-3", "/TestTwoWaySync/add");
			this.assertRecordPresence("unique-4", "/TestTwoWaySync/add");			
			
			SyncService.getInstance().performBootSync(service, service, false);
			
			//Assert the device state
			this.assertRecordPresence("unique-1", "/TestBootSync/MustBeFound/unique-1");
			this.assertRecordPresence("unique-2", "/TestBootSync/MustBeFoundAsProxy/unique-2");
			this.assertRecordPresence("unique-3", "/TestBootSync/MustBeFoundAsProxy/unique-3");
			this.assertRecordPresence("unique-4", "/TestBootSync/MustBeFoundAsProxy/unique-4");
			this.assertFalse(this.getRecord("unique-1").isProxy(), "/TestBootSync/MustNotBeAProxy/unique-1");
			this.assertTrue(this.getRecord("unique-2").isProxy(), "/TestBootSync/MustBeAProxy/unique-2");
			this.assertTrue(this.getRecord("unique-3").isProxy(), "/TestBootSync/MustBeAProxy/unique-3");
			this.assertTrue(this.getRecord("unique-4").isProxy(), "/TestBootSync/MustBeAProxy/unique-4");
			
			this.tearDown();
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			throw new RuntimeException(e.toString());
		}
	}	
}
