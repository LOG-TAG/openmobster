/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.rimos.module.bus;

import java.util.Hashtable;
import java.util.Vector;

/**
 * @author openmobster@gmail.com
 *
 */
public final class MobilePushInvocation extends Invocation 
{	
	private Vector mobilePushMetaData;
	
	public MobilePushInvocation(String target)
	{
		super(target);
	}
	
	public Vector getMobilePushMetaData()
	{
		if(this.mobilePushMetaData == null)
		{
			this.mobilePushMetaData = new Vector();
		}
		return this.mobilePushMetaData;
	}
	
	public void addMobilePushMetaData(MobilePushMetaData pushMetaData)
	{
		this.getMobilePushMetaData().addElement(pushMetaData);
	}

	public Hashtable getShared()
	{
		Hashtable shared = super.getShared();
		
		if(!this.mobilePushMetaData.isEmpty())
		{
			int size = this.mobilePushMetaData.size();
			shared.put("pushMetaDataSize", ""+size);
			for(int i=0; i<size; i++)
			{
				MobilePushMetaData metadata = (MobilePushMetaData)this.mobilePushMetaData.elementAt(i);
				shared.put("pushMetaData["+i+"].service", metadata.getService());
				shared.put("pushMetaData["+i+"].id", metadata.getId());
				shared.put("pushMetaData["+i+"].isDeleted", 
				metadata.isDeleted()?Boolean.TRUE:Boolean.FALSE);
				shared.put("pushMetaData["+i+"].isAdded", 
				metadata.isAdded()?Boolean.TRUE:Boolean.FALSE);
				shared.put("pushMetaData["+i+"].isUpdated", 
				metadata.isUpdated()?Boolean.TRUE:Boolean.FALSE);
			}
		}
		
		return shared;
	}
}
