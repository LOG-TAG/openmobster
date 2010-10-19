/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.rim_native.framework;

import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.UiApplication;

/**
 * @author openmobster@gmail
 *
 */
public class AppScreen extends MainScreen
{
	public void close()
	{
		UiApplication uiApplication = UiApplication.getUiApplication();
		uiApplication.popScreen(uiApplication.getActiveScreen());
		uiApplication.requestBackground();
	}
}
