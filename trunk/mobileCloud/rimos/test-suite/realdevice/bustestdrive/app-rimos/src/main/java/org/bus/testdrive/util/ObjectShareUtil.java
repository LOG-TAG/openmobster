/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.bus.testdrive.util;

import net.rim.device.api.system.RuntimeStore;

/**
 * @author openmobster@gmail.com
 *
 */
public final class ObjectShareUtil 
{
	public static long start(Object object)
	{
		long handle = 0;
		
		int retry = 10;
		do
		{
			try
			{
				handle = GeneralTools.generateUniqueId();
				RuntimeStore.getRuntimeStore().put(handle, object);
			}
			catch(Exception e)
			{
				if(retry-- == 0)
				{
					//we tried 10 times to shared this object....giving up
					throw new RuntimeException("Share could not be started!!!");
				}
			}			
			break;
		}while(true);
		
		
		return handle;
	}
	
	public static Object get(long handle)
	{
		return RuntimeStore.getRuntimeStore().get(handle);
	}
	
	public static void replace(long handle, Object object)
	{
		RuntimeStore.getRuntimeStore().replace(handle, object);
	}
	
	public static void close(long handle)
	{
		RuntimeStore.getRuntimeStore().remove(handle);
	}
}
