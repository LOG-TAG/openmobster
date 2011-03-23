/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.perf.framework;

import org.apache.log4j.Logger;

import junit.framework.TestCase;

public class PerfDeviceActivation extends TestCase
{
	private static Logger log = Logger.getLogger(PerfDeviceActivation.class);
	
	private SimulatedDeviceStack deviceStack;
	
	public void setUp() throws Exception
	{
		log.info("Starting DeviceActivation....................................................");
		this.deviceStack = PerfSuite.getDevice();
		this.deviceStack.getRunner().activateDevice();
	}
	
	public void tearDown() throws Exception
	{
	}
	
	public void testDeviceInitialization() throws Exception
	{
		this.assertNotNull("Device Not Found for this test case", this.deviceStack);
	}
}
