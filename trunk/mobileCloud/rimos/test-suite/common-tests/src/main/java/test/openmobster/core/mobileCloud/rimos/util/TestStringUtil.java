/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.core.mobileCloud.rimos.util;

import test.openmobster.core.mobileCloud.rimos.testsuite.Test;

import org.openmobster.core.mobileCloud.rimos.util.StringUtil;

/**
 * @author openmobster@gmail.com
 *
 */
public final class TestStringUtil extends Test 
{	
	public String getInfo() 
	{	
		return "TestStringUtil";
	}
	
	public void runTest() 
	{
		System.out.println("Starting "+this.getInfo()+"--------------------");
		
		
		String test = "/blah1/blah2/blah3/blah4";
		System.out.println("Testing for "+test);
		String[] tokens = StringUtil.tokenize(test, "/");
		for(int i=0; i<tokens.length; i++)
		{
			System.out.println("Token["+i+"]="+tokens[i]);
			switch(i)
			{
				case 0:
					this.assertEquals(tokens[i], "blah1", "TestStringUtil:/"+test);
				break;
			
				case 1:
					this.assertEquals(tokens[i], "blah2", "TestStringUtil:/"+test);
				break;
				
				case 2:
					this.assertEquals(tokens[i], "blah3", "TestStringUtil:/"+test);
				break;
				
				case 3:
					this.assertEquals(tokens[i], "blah4", "TestStringUtil:/"+test);
				break;
			}
		}
		
		test = "/blah1/blah2/blah3/blah4/";
		System.out.println("Testing for "+test);
		tokens = StringUtil.tokenize(test, "/");
		for(int i=0; i<tokens.length; i++)
		{
			System.out.println("Token["+i+"]="+tokens[i]);
			switch(i)
			{
				case 0:
					this.assertEquals(tokens[i], "blah1", "TestStringUtil:/"+test);
				break;
			
				case 1:
					this.assertEquals(tokens[i], "blah2", "TestStringUtil:/"+test);
				break;
				
				case 2:
					this.assertEquals(tokens[i], "blah3", "TestStringUtil:/"+test);
				break;
				
				case 3:
					this.assertEquals(tokens[i], "blah4", "TestStringUtil:/"+test);
				break;
			}
		}
		
		test = "blah1/blah2/blah3/blah4";
		System.out.println("Testing for "+test);
		tokens = StringUtil.tokenize(test, "/");
		for(int i=0; i<tokens.length; i++)
		{
			System.out.println("Token["+i+"]="+tokens[i]);
			switch(i)
			{
				case 0:
					this.assertEquals(tokens[i], "blah1", "TestStringUtil:/"+test);
				break;
			
				case 1:
					this.assertEquals(tokens[i], "blah2", "TestStringUtil:/"+test);
				break;
				
				case 2:
					this.assertEquals(tokens[i], "blah3", "TestStringUtil:/"+test);
				break;
				
				case 3:
					this.assertEquals(tokens[i], "blah4", "TestStringUtil:/"+test);
				break;
			}
		}
	}
}
