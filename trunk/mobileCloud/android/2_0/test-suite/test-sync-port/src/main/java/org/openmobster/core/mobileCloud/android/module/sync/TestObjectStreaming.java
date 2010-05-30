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

import org.openmobster.core.mobileCloud.android.module.mobileObject.MobileObject;


/**
 * 
 * @author openmobster@gmail.com
 *
 */
public final class TestObjectStreaming extends AbstractSyncTest 
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
			
			MobileObject unique1 = this.getRecord("unique-1");
			this.assertNull(unique1.getValue("attachment"), "/TestObjectStreaming/NoAttachmentDownloaded");
			
			SyncService.getInstance().performStreamSync(service, "unique-1", false);
			unique1 = this.getRecord("unique-1");
			this.assertNotNull(unique1.getValue("attachment"), "/TestObjectStreaming/AttachmentMustBeDownloaded");
			
			this.tearDown();
						
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			throw new RuntimeException(e.toString());
		}
	}	
}
