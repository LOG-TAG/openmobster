/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.cloud.console.client.common;

import java.util.List;
import java.util.ArrayList;

/**
 * 
 * @author openmobster@gmail.com
 */
public final class Payload 
{
	public static String encode(String[] parameters)
	{
		StringBuilder buffer = new StringBuilder();
		
		if(parameters != null)
		{
			for(String parameter:parameters)
			{
				parameter = parameter.replaceAll("&amp;", "amp;");
				buffer.append(parameter+"&amp;");
			}
		}
		
		return buffer.toString();
	}
	
	public static List<String> decode(String encodedParam)
	{
		List<String> params = new ArrayList<String>();
		
		String[] tokens = encodedParam.split("&amp;");
		for(String param:tokens)
		{
			param = param.replaceAll("amp;", "&amp;");
			
			if(param.trim().length()==0)
			{
				params.add("null");
			}
			else
			{
				params.add(param);
			}
		}
		
		return params;
	}
}
