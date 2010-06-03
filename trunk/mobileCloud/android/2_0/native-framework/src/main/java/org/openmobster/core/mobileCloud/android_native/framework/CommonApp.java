/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.android_native.framework;

import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android_native.framework.events.NativeEventBusSPI;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.spi.ui.framework.SPIServices;

import android.os.Bundle;

/**
 * @author openmobster@gmail.com
 *
 */
public final class CommonApp
{
	static void onCreate(Bundle savedInstanceState)
	{
		//Initialize the UI Framework
    	//Load API Services
		Services.getInstance().setResources(new NativeAppResources());
		Services.getInstance().setCommandService(new NativeCommandService());
		
		//Load SPI Services
		SPIServices.getInstance().setNavigationContextSPI(new NativeNavigationContextSPI());
		SPIServices.getInstance().setEventBusSPI(new NativeEventBusSPI());
		
		//Bootstrap the container
		bootstrapContainer();
	}
	
	static void bootstrapContainer()
	{
		
	}
}
