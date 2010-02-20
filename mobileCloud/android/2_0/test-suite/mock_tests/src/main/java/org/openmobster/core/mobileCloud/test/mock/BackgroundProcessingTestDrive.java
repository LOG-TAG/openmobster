/**
 * 
 */
package org.openmobster.core.mobileCloud.test.mock;

import android.content.Context;

import org.openmobster.core.mobileCloud.android.testsuite.Test;

/**
 * @author openmobster
 *
 */
public class BackgroundProcessingTestDrive extends Test
{
	public void runTest()
	{
		Context context = (Context)this.getTestSuite().getContext().
		getAttribute("android:context");
		try
		{
			Thread t = new Thread(new BackgroundProcess());
			t.start();
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
		finally
		{
		}
	}
	
	private static class BackgroundProcess implements Runnable
	{
		public void run()
		{
			try
			{
				int counter = 0;
				while(true)
				{
					Thread.currentThread().sleep(20000);
					if(counter++ == 3)
					{
						break;
					}
					
					System.out.println("Background Thread is running!!!");
				}
				System.out.println("Background Thread is finished!!!");
			}
			catch(Exception e)
			{
				e.printStackTrace(System.out);
			}
		}
	}
}
