/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.api.push;

/**
 * A PushListener is an Application component that is registered with the MobilePush service to receive MobilePush notifications to refresh the state
 * of its MobileBeans. To save processing overhead, MobilePush registry allows a single instance of a PushListener per application.
 * 
 * What the application does with MobilePush notifications it receives is driven completely by the logic/requirements of the application
 * 
 * @author openmobster@gmail.com
 *
 */
public interface PushListener 
{
	/**
	 * Receives a push notification from the Server
	 * 
	 * @param an instance of mobile push
	 */
	public void receivePush(MobilePush push);
	
	public void clearNotification();
	
	public MobilePush getPush();
}
