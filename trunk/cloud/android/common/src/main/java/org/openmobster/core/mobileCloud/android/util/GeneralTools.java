/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.util;

import java.util.UUID;

import android.provider.Settings.Secure;
import android.os.Build;
import android.content.Context;

/**
 * @author openmobster@gmail.com
 */
public class GeneralTools
{
	public static String generateUniqueId()
	{
		return UUID.randomUUID().toString();
	}
	
	public static String getDeviceIdentifier(Context context)
	{	
		String deviceIdentifier = "IMEI:";
		
		String androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
		String manufacturer = Build.MANUFACTURER;
		String model = Build.MODEL;
		String product = Build.PRODUCT;
		
		StringBuilder inputBuffer = new StringBuilder();
		if(androidId != null)
		{
			inputBuffer.append(androidId);
		}
		inputBuffer.append(manufacturer);
		inputBuffer.append(model);
		inputBuffer.append(product);
		
		String knownInput = inputBuffer.toString();
		
		//deviceIdentifier += Base64.encodeBytes(knownInput.getBytes());
		deviceIdentifier += knownInput;
		
		return deviceIdentifier;
	}
}
