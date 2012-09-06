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
	private static Stack<SimulatedDeviceStack> deviceCache;
	
	public static void main(String[] args)
	{
		if(args != null)
		{
			System.setProperty("perf-framework", "true");
			
			String currentParam = null;
			InVMAttributeManager attributeManager = new InVMAttributeManager();
			devices = new Stack<SimulatedDeviceStack>();
			deviceCache = new Stack<SimulatedDeviceStack>();
			for(String argument:args)
			{
				if(argument.equals("-XconcurrentUsers") || 
				   argument.equals("-Xtest") ||
				   argument.equals("-Xserver") ||
				   argument.equals("-Xduration")
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
			
			//Execute the TestSuite
			ServiceManager.bootstrap();
			
			String duration = (String)attributeManager.getAttribute("duration"); //duration in minutes
			long durationInMillis = 0;
			if(duration != null && duration.trim().length() > 0)
			{
				durationInMillis = Integer.parseInt(duration) * 60 * 1000;
			}
			long endTime = System.currentTimeMillis() + durationInMillis;
			long currentTime = System.currentTimeMillis();
			do
			{
				junit.textui.TestRunner.run(prepareSuite(attributeManager));
				try
				{
					Thread.sleep(1000);
				}
				catch(Throwable t)
				{
					//Don't worry about it
				}
				currentTime = System.currentTimeMillis();
			}while(endTime > currentTime);
			
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
			int iterations = 10;
			PerfSuite.suiteContext = attributeManager;
			devices.clear();
			
			//Load the simulated device stack for each user
			if(PerfSuite.deviceCache.isEmpty())
			{
				for(int i=0; i<concurrentUsers; i++)
				{
					SimulatedDeviceStack local = new SimulatedDeviceStack();
					local.start();
					
					devices.push(local);
				}
			}
			else
			{
				devices.addAll(deviceCache);
				deviceCache.clear();
			}
			
			TestSuite suite = new TestSuite();
			
			String[] tests = test.split(":");
			for(String local:tests)
			{
				Timer timer = new ConstantTimer(0);
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
	
	public static String getDuration()
	{
		return (String)suiteContext.getAttribute("duration");
	}
	
	public static SimulatedDeviceStack getDevice()
	{
		if(!PerfSuite.devices.empty())
		{
			SimulatedDeviceStack device = PerfSuite.devices.pop();
			
			//cache it for the next iteration
			PerfSuite.deviceCache.push(device);
			
			return device;
		}
		return null;
	}
}
