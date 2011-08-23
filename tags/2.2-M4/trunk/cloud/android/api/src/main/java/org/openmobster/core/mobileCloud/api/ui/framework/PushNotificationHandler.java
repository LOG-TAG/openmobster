/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.api.ui.framework;

import org.openmobster.core.mobileCloud.api.ui.framework.push.MobilePush;

/**
 * @author openmobster@gmail
 *
 */
public interface PushNotificationHandler
{
	public void receiveNotification(MobilePush newPushInstance);
	
	public void clearNotification();
}
