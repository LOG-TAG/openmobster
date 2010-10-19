/**
 * 
 */
package org.openmobster.core.mobileCloud.android.module.connection;

import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android.testsuite.Test;

/**
 * @author openmobster
 *
 */
public class TestSimplePush extends Test
{	
	public void runTest()
	{
		try
		{
			Registry.getActiveInstance().
			register(new NotificationListener());	
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			throw new RuntimeException(e);
		}
	}
}
