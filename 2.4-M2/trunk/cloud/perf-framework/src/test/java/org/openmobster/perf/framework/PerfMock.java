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

public class PerfMock extends TestCase
{
	private static Logger log = Logger.getLogger(PerfMock.class);
	
	
	public void setUp() throws Exception
	{
		log.info("Starting PerfMock....................................................");
	}
	
	public void tearDown() throws Exception
	{
	}
	
	public void testMock() throws Exception
	{
		log.info("*********************************");
		log.info("Executing testMock...............");
		log.info("*********************************");
	}
}
