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
