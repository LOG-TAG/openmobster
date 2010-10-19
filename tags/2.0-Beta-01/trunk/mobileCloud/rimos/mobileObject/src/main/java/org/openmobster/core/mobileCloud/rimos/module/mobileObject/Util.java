/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.rimos.module.mobileObject;

/**
 * @author openmobster@gmail.com
 */
final class Util 
{
	static String cleanArrayUri(String uri)
	{
		String cleanUri = uri;
		
		if(uri.indexOf('[')!= -1)
		{
			StringBuffer buffer = new StringBuffer();
			int beginIndex = 0;
			int index = uri.indexOf('[', beginIndex);
			int uriLength = uri.length();
			while(index!= -1)
			{
				buffer.append(uri.substring(beginIndex, index));
				
				beginIndex = uri.indexOf(']', beginIndex)+1;					
				if(beginIndex < uriLength-1)
				{
					index = uri.indexOf('[', beginIndex);
					if(index == -1)
					{
						buffer.append(uri.substring(beginIndex));
					}
				}
				else
				{
					break;
				}
			}
			
			
			cleanUri = buffer.toString();
		}
		
		return cleanUri;
	}
}
