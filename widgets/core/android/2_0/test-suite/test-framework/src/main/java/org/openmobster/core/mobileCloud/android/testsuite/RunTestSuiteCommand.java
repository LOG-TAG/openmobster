/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.android.testsuite;

import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.api.ui.framework.command.RemoteCommand;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.android.testsuite.TestContext;
import org.openmobster.core.mobileCloud.android.testsuite.TestSuite;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.app.Activity;

/**
 * @author openmobster@gmail.com
 *
 */
public class RunTestSuiteCommand implements RemoteCommand
{
	public void doViewBefore(CommandContext commandContext)
	{	
	}

	public void doAction(CommandContext commandContext)
	{
		try
		{
    		TestSuite suite = new TestSuite();
    		commandContext.setAttribute("testsuite", suite);
    		
        	TestContext testContext = new TestContext();
        	suite.setContext(testContext);
        	suite.getContext().setAttribute("android:context", 
        	Registry.getActiveInstance().getContext());
        		        		        	
        	//Load the tests
        	suite.load();
        	
        	suite.execute();
		}
		catch(Exception e)
    	{
    		e.printStackTrace(System.out);
    		throw new RuntimeException(e);
    	}
	}

	public void doViewAfter(CommandContext commandContext)
	{
		Activity currentActivity = (Activity)Registry.getActiveInstance().
		getContext();
		TestSuite testsuite = (TestSuite)commandContext.getAttribute("testsuite");
		
		AlertDialog alert = new AlertDialog.Builder(currentActivity).
    	setTitle("TestSuite").
    	setMessage(testsuite.getStatus()).
    	setCancelable(false).
    	create();
    	
    	alert.setButton(DialogInterface.BUTTON_POSITIVE, "OK", 
			new DialogInterface.OnClickListener() 
			{
				
				public void onClick(DialogInterface dialog, int status)
				{
				}
			}
    	);
    	
    	alert.show();
	}

	public void doViewError(CommandContext commandContext)
	{	
	}
}
