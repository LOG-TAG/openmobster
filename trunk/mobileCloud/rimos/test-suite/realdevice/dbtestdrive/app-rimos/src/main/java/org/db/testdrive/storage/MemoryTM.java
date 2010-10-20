/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.db.testdrive.storage;

/**
 * TODO: Implement a Memory-Based TransactionManager to help with Storage/Database operations that must be performed as a Batch
 * 
 * @author openmobster@gmail.com
 *
 */
final class MemoryTM 
{
	private static MemoryTM singleton;
	
	private MemoryTM()
	{
		
	}
	
	static MemoryTM getInstance()
	{
		if(MemoryTM.singleton == null)
		{
			synchronized(MemoryTM.class)
			{
				if(MemoryTM.singleton == null)
				{
					MemoryTM.singleton = new MemoryTM();
				}
			}
		}
		return MemoryTM.singleton;
	}
	//-------------------------------------------------------------------------------------------------------------------------------------
	
}
