/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.console.server;

import org.openmobster.core.common.validation.ObjectValidator;

/**
 *
 * @author openmobster@gmail.com
 */
public final class ObjectValidatorFactory 
{
	private static ObjectValidator singleton;
	
	public static ObjectValidator getInstance()
	{
		if(singleton == null)
		{
			synchronized(ObjectValidatorFactory.class)
			{
				if(singleton == null)
				{
					singleton = new ObjectValidator();
					singleton.setName("Console App Validator");
					singleton.setRulesFile("META-INF/console-validationRules.xml");
					singleton.start();
				}
			}
		}
		return singleton;
	}
}
