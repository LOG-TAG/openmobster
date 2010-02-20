/**
 * 
 */
package org.openmobster.core.mobileCloud.test.mock;

import org.openmobster.core.mobileCloud.android.testsuite.Test;

import android.content.Context;

/**
 * @author openmobster
 *
 */
public class StorageTestDrive extends Test
{
	public void runTest()
	{
		Database database = null;
		try
		{
			Context context = (Context)this.getTestSuite().getContext().
			getAttribute("android:context");
		
			database = new Database(context, "mobster.db",1);
			database.open();
		
			//perform some queries
			database.dump();
		
			database.close();
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
		finally
		{
			database.delete();
		}
	}
}
