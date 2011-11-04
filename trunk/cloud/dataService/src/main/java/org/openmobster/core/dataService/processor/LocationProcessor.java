/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.dataService.processor;

/**
 *
 * @author openmobster@gmail.com
 */
public class LocationProcessor implements Processor
{
	private String id;
	
	public LocationProcessor()
	{
		
	}
	
	public void start()
	{
		
	}
	
	public void stop()
	{
		
	}
	
	public void setId(String id)
	{
		this.id = id;
	}
	//----------------------------------------------------------------------------------------------------------
	@Override
	public String getId()
	{
		return this.id;
	}

	@Override
	public String process(Input input) throws ProcessorException
	{
		return input.getMessage();
	}
}
