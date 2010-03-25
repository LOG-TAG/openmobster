/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.android_native.framework;

import java.lang.reflect.Field;

import android.app.Activity;
import android.view.View;

/**
 * @author openmobster@gmail.com
 *
 */
public class ViewHelper
{
	public static View findViewById(Activity activity, String viewId)
	{
		try
		{
			String idClass = activity.getPackageName()+".R$id";
			Class clazz = Class.forName(idClass);
			Field field = clazz.getField(viewId);
			
			return activity.findViewById(field.getInt(clazz));
		}
		catch(Exception e)
		{
			return null;
		}
	}
}
