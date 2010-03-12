/**
 * Copyright (c) {2003,2009} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.testsuite;

import java.io.InputStream;
import java.util.Vector;

import org.openmobster.core.mobileCloud.android.util.IOUtil;

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
			try
			{
				test.setTestSuite(this);
				test.setUp();
				test.runTest();
				test.tearDown();
				test.setTestSuite(null);
			}
			catch(Exception e)
			{
				//eat this and keep going
				this.reportError("Exception:"+test.getInfo()+":"+e.toString()+":"+e.getMessage());
			}			
		}
		
		//Report the errors
		if(this.errors.size()>0)
		{
			System.out.println("Errors---------------------------------");
			for(int i=0,errSize=this.errors.size(); i<errSize; i++)
			{
				System.out.println(this.errors.elementAt(i));
			}
		}
		else
		{
			System.out.println("TestSuite succefully executed all tests------------------------------------");
		}
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
					String[] tokens = contents.split("\n");
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
}
