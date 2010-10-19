/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.dataService.server;

import org.apache.mina.common.IoFilterAdapter;
import org.apache.mina.common.IoSession;

import org.openmobster.core.common.transaction.TransactionHelper;

/**
 * @author openmobster@gmail.com
 */
public class TransactionFilter extends IoFilterAdapter
{			
	public TransactionFilter()
	{
		
	}
		
	public void messageReceived(NextFilter nextFilter, IoSession session, Object message)
	{
		boolean isStartedHere = false;
		try
		{
			isStartedHere = TransactionHelper.startTx();
									
			nextFilter.messageReceived(session, message);
			
			if(isStartedHere)
			{
				if(session.getAttribute("tx-rollback") == null)
				{
					TransactionHelper.commitTx();
				}
				else
				{
					TransactionHelper.rollbackTx();
				}
			}
		}
		catch(Throwable t)
		{
			if(isStartedHere)
			{
				TransactionHelper.rollbackTx();
			}
			throw new RuntimeException(t);
		}
	}	
}
