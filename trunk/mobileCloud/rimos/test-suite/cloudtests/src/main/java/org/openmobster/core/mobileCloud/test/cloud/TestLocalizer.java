/******************************************************************************
 * OpenMobster                                                                *
 * Copyright 2008, OpenMobster, and individual                                *
 * contributors as indicated by the @authors tag. See the                     *
 * copyright.txt in the distribution for a full listing of                    *
 * individual contributors.                                                   *
 *                                                                            *
 * This is free software; you can redistribute it and/or modify it            *
 * under the terms of the GNU Lesser General Public License as                *
 * published by the Free Software Foundation; either version 2.1 of           *
 * the License, or (at your option) any later version.                        *
 *                                                                            *
 * This software is distributed in the hope that it will be useful,           *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU           *
 * Lesser General Public License for more details.                            *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public           *
 * License along with this software; if not, write to the Free                *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA         *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.                   *
 ******************************************************************************/
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
