/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.core.mobileCloud.rimos.testsuite;

import java.io.InputStream;
import java.util.Vector;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.component.Dialog;

import org.openmobster.core.mobileCloud.rimos.util.IOUtil;
import org.openmobster.core.mobileCloud.rimos.util.StringUtil;

/**
 * 
 * @author openmobster@gmail.com
 *
 */
public final class TestSuite 
{
	/**
	 * 
	 */
	private Vector tests;
	private Vector errors;
	private TestContext context;
	
	/**
	 * 
	 *
	 */
	public TestSuite()
	{
		this.tests = new Vector();
		this.errors = new Vector();
	}
	
	/**
	 * 
	 * @param addTests
	 */
	public TestSuite(Vector addTests)
	{
		this();
		if(addTests != null)
		{
			for(int i=0,size=addTests.size(); i<size; i++)
			{
				this.addTest((Test)addTests.elementAt(i));
			}
		}
	}
	
	/**
	 * 
	 * @param test
	 */
	public void addTest(Test test)
	{
		this.tests.addElement(test);
	}
	
	public final void reportError(String error)
	{
		this.errors.addElement(error);
	}
	
	public final TestContext getContext()
	{
		return this.context;
	}
	
	public final void setContext(TestContext context)
	{
		this.context = context;
	}
	
	/**
	 * 
	 *
	 */
	public final void execute()
	{
		for(int i=0,size=this.tests.size(); i<size; i++)
		{
			Test test = (Test)this.tests.elementAt(i);
			
			
			System.out.println("Starting "+test.getInfo()+"......");
			
			//Show a status on the screen
			String testName = test.getInfo();
			if(testName.indexOf('.')!= -1)
			{
				testName = testName.substring(testName.lastIndexOf('.')+1);
			}
			Dialog dialog = new Dialog("Executing \n"+testName+" ("+(i+1)+" out of "+size+")",
					null,null,
					Dialog.NO,
					null
			);
			dialog.setEditable(false);
			dialog.setEscapeEnabled(false);
			
			Thread t = new Thread(new TestExecutor(test,dialog));
			t.start();
			
			dialog.doModal();
		}
		
		//Report the errors
		StringBuffer buffer = new StringBuffer();
		if(this.errors.size()>0)
		{			
			System.out.println("Errors---------------------------------");
			for(int i=0,errSize=this.errors.size(); i<errSize; i++)
			{
				String errorStr = this.errors.elementAt(i).toString();
				System.out.println(errorStr);
				buffer.append(errorStr+"\n\n\n");
			}
		}
		else
		{
			String success = "TestSuite succefully executed all tests...";
			System.out.println(success);
			buffer.append(success);
		}
		
		//Show the message
		Dialog.alert(buffer.toString());
	}
	
	public Vector getErrors()
	{
		return this.errors;
	}
	
	public final void load()
	{
		try
		{
			InputStream is = TestSuite.class.getResourceAsStream("/moblet-app/tests.cfg");
			if(is != null)
			{
				String contents = new String(IOUtil.read(is));
				if(contents != null)
				{
					String[] tokens = StringUtil.tokenize(contents, "\n");
					if(tokens != null)
					{
						int length = tokens.length;
						for(int i=0; i<length; i++)
						{
							String token = tokens[i].trim();
							if(token.startsWith("#") || token.trim().length() == 0)
							{
								continue;
							}
							
							try
							{
								this.addTest((Test)Class.forName(token).newInstance());
							}
							catch(Exception e)
							{
								
							}
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			
		}
	}
	//-----------------------------------------------------------------------------------------------------------------------------------
	private class TestExecutor implements Runnable
	{
		Dialog dialog;
		Test test;
		
		private TestExecutor(Test test,Dialog dialog)
		{
			this.test = test;
			this.dialog = dialog;
		}
		
		public void run()
		{
			try
			{				
				test.setTestSuite(TestSuite.this);				
				test.setUp();				
				test.runTest();				
				test.tearDown();				
				test.setTestSuite(null);				
			}
			catch(Exception e)
			{
				//eat this and keep going
				TestSuite.this.reportError("Exception:"+test.getInfo()+":"+e.toString()+":"+e.getMessage());
			}	
			finally
			{				
				Application.getApplication().invokeLater(new Runnable()
					{
						public void run()
						{
							dialog.close();
						}
					}
				);						
			}
		}
	}
}
