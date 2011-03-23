/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.perf.framework;

import java.util.Stack;

import org.apache.log4j.Logger;

import junit.framework.Test;

import com.clarkware.junitperf.LoadTest;
import com.clarkware.junitperf.TestFactory;
import com.clarkware.junitperf.ConstantTimer;
import com.clarkware.junitperf.Timer;
import junit.framework.TestSuite;

import org.openmobster.core.common.InVMAttributeManager;
import org.openmobster.core.common.ServiceManager;

/**
 * 
 * @author openmobster@gmail.com
 */
public class PerfSuite
{
	private static Logger log = Logger.getLogger(PerfSuite.class);
	
	private static InVMAttributeManager suiteContext;
	private static Stack<SimulatedDeviceStack> devices;
	
	public static void main(String[] args)
	{
		if(args != null)
		{
			String currentParam = null;
			InVMAttributeManager attributeManager = new InVMAttributeManager();
			devices = new Stack<SimulatedDeviceStack>();
			for(String argument:args)
			{
				if(argument.equals("-XconcurrentUsers") || 
				   argument.equals("-Xtest") ||
				   argument.equals("-Xserver")
				 )
				{
					currentParam = argument;
				}
				else
				{
					attributeManager.setAttribute(currentParam.substring(2), argument);
					currentParam = null;
				}
			}
			
			ServiceManager.bootstrap();
			junit.textui.TestRunner.run(prepareSuite(attributeManager));
			ServiceManager.shutdown();
		}
		else
		{
			log.info("Performance Test Configuration was missing!!!");
		}
	}
	
	private static Test prepareSuite(InVMAttributeManager attributeManager)
	{
		try
		{
			int concurrentUsers = Integer.parseInt((String)attributeManager.getAttribute("concurrentUsers"));
			String test = (String)attributeManager.getAttribute("test");
			int iterations = 1;
			PerfSuite.suiteContext = attributeManager;
			
			//Load the simulated device stack for each user
			for(int i=0; i<concurrentUsers; i++)
			{
				SimulatedDeviceStack local = new SimulatedDeviceStack();
				local.start();
				
				devices.push(local);
			}
			
			TestSuite suite = new TestSuite();
			
			String[] tests = test.split(":");
			for(String local:tests)
			{
				Timer timer = new ConstantTimer(1000);
				TestFactory testFactory = new TestFactory(Thread.currentThread().getContextClassLoader().loadClass(local));
				LoadTest loadTest = new LoadTest(testFactory,concurrentUsers,iterations,timer);
				suite.addTest(loadTest);
			}
			
			return suite;
		}
		catch(Exception e)
		{
			log.error("PerfSuite", e);
			throw new RuntimeException(e);
		}
	}
	
	public static String getConcurrentUsers()
	{
		return (String)suiteContext.getAttribute("concurrentUsers");
	}
	
	public static String getCloudServer()
	{
		return (String)suiteContext.getAttribute("server");
	}
	
	public static SimulatedDeviceStack getDevice()
	{
		if(!PerfSuite.devices.empty())
		{
			return PerfSuite.devices.pop();
		}
		return null;
	}
}
