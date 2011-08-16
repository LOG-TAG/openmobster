/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.api.push;

import java.io.Serializable;

import org.openmobster.core.mobileCloud.api.sync.MobileBeanMetaData;

/**
 * A MobilePush represents a server notification containing meta information about the set of MobileBeans whose state is out-of-sync with that 
 * of the server. The MobilePush is processed by the application to refresh the state of these MobileBeans before their continued usage within the
 * application
 * 
 * @author openmobster@gmail.com
 *
 */
public final class MobilePush implements Serializable
{
	private static PushListener singleton;
	
	private MobileBeanMetaData[] pushData;
	private long numberOfUpdates;
	
	public MobilePush(MobileBeanMetaData[] pushData)
	{
		this.pushData = pushData;
	}
	
	public MobileBeanMetaData[] getPushData()
	{
		return this.pushData;
	}
	
	public long getNumberOfUpdates()
	{
		return numberOfUpdates;
	}

	public void setNumberOfUpdates(long numberOfUpdates)
	{
		this.numberOfUpdates = numberOfUpdates;
	}
	//----------------------------------------------------------------------------------------------------------------------------------------
	public static void registerPushListener(PushListener pushListener)
	{
		MobilePush.singleton = pushListener;
	}
	
	public static PushListener getPushListener()
	{
		return MobilePush.singleton;
	}
}
