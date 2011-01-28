/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.device.comet;

import junit.framework.TestCase;

import org.openmobster.core.common.ServiceManager;
import org.openmobster.device.agent.test.framework.MobileBeanRunner;

import org.apache.log4j.Logger;

/**
 * @author openmobster@gmail.com
 *
 */
public abstract class AbstractCometTest extends TestCase
{	
	public static Logger log = Logger.getLogger(AbstractCometTest.class);
	
	protected MobileBeanRunner device_12345;
	protected MobileBeanRunner device_67890;
	
	protected void setUp() throws Exception 
	{
		ServiceManager.bootstrap();
		
		this.device_12345 = (MobileBeanRunner)ServiceManager.locate("IMEI:12345");
		this.device_67890 = (MobileBeanRunner)ServiceManager.locate("IMEI:67890");
	}
	
	protected void tearDown() throws Exception 
	{						
		ServiceManager.shutdown();
	}		
}
