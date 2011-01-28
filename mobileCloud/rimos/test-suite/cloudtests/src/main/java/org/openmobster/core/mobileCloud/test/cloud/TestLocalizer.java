/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.test.cloud;

import test.openmobster.core.mobileCloud.rimos.testsuite.Test;
import org.openmobster.core.mobileCloud.rimos.util.Localizer;

import net.rim.device.api.i18n.Locale;

/**
 * @author openmobster@gmail.com
 *
 */
public final class TestLocalizer extends Test
{
	public void runTest()
	{		
		try
		{
			Localizer en = Localizer.getInstance("moblet-app/localize", Locale.get("en"));
			Localizer en_GB = Localizer.getInstance("moblet-app/localize", Locale.get("en", "GB"));
			Localizer defaultLocalizer = Localizer.getInstance("moblet-app/localize");
			
			System.out.println("Locale(en)-----------------------------------------");
			System.out.println("Color="+en.getString("color"));
			System.out.println("Neighbor="+en.getString("neighbor"));
			
			System.out.println("Locale(en_GB)-----------------------------------------");
			System.out.println("Color="+en_GB.getString("color"));
			System.out.println("Neighbor="+en_GB.getString("neighbor"));
			
			System.out.println("Locale(Default)-----------------------------------------");
			System.out.println("Color="+defaultLocalizer.getString("color"));
			System.out.println("Neighbor="+defaultLocalizer.getString("neighbor"));
			
			//Assert
			assertEquals("color", en.getString("color"), this.getInfo()+"://Locale/en/MatchFailed");
			assertEquals("colour", en_GB.getString("color"), this.getInfo()+"://Locale/en/GB/MatchFailed");
			assertEquals("color(default)", defaultLocalizer.getString("color"), this.getInfo()+"://Locale/en/GB/MatchFailed");
			assertEquals("neighbor", en.getString("neighbor"), this.getInfo()+"://Locale/en/MatchFailed");
			assertEquals("neighbour", en_GB.getString("neighbor"), this.getInfo()+"://Locale/en/GB/MatchFailed");
			assertEquals("neighbor(default)", defaultLocalizer.getString("neighbor"), this.getInfo()+"://Locale/en/MatchFailed");
		}
		catch(Exception e)
		{
			throw new RuntimeException(e.toString());
		}
	}		
}
