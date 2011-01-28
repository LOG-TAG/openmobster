/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.core.mobileCloud.rimos.testsuite.ui;

import java.util.Vector;

import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.RichTextField;

import test.openmobster.core.mobileCloud.rimos.testsuite.TestSuite;

/**
 * @author openmobster@gmail.com
 *
 */
public final class LaunchScreen extends MainScreen 
{
	private TestSuite suite;
	private String errorMessage;
	private boolean finished;
	
	private LabelField status;
	
	public LaunchScreen()
	{				
		this.setTitle("TestSuite........");	
		this.status = new LabelField();
		this.add(this.status);				
	}	
		
	public TestSuite getSuite() 
	{
		return suite;
	}

	public void setSuite(TestSuite suite) 
	{
		this.suite = suite;
	}

	public String getErrorMessage() 
	{
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) 
	{
		this.errorMessage = errorMessage;
	}
	//------------------------------------------------------------------------------------------------------------------------------------------
	protected void onVisibilityChange(boolean visible)
	{		
		if(visible && !finished)
		{
			if(errorMessage != null && errorMessage.trim().length()>0)
			{
				this.showError();
				finished = true;
			}
			else if(this.suite != null)
			{
				this.runTestSuite();
				finished = true;
			}
		}
	}
	//------------------------------------------------------------------------------------------------------------------------------------------
	private void runTestSuite()
	{
		Runnable runnable = new Runnable(){
			public void run()
			{
				try
				{												
					Status.show("Starting the TestSuite..........");
																																
					suite.execute();
					
					Status.show("TestSuite Finished..........");
										
					RichTextField message = new RichTextField();
					message.setEditable(false);
					
					Vector errors = suite.getErrors();
					if(errors == null || errors.isEmpty())
					{
						message.setText("TestSuite executed successfully.......");
					}
					else
					{
						StringBuffer buffer = new StringBuffer();
						int size = errors.size();
						for(int i=0; i<size; i++)
						{
							buffer.append(errors.elementAt(i)+"\n");
							buffer.append("----------------------------------\n");
						}
						message.setText(buffer.toString());
					}
					add(message);
				}
				catch(Exception e)
				{
					//Show an Error Dialog					
					Dialog.alert("Exception ="+e+", "+e.getMessage());
				}
			}
		};
		Application.getApplication().invokeLater(runnable);
	}	
	
	private void showError()
	{
		Runnable runnable = new Runnable(){
			public void run()
			{
				Dialog.alert(errorMessage);
			}
		};
		Application.getApplication().invokeLater(runnable);
	}	
}
