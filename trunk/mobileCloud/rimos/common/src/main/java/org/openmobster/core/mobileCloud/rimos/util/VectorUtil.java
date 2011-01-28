/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.rimos.util;

import java.util.Vector;

/**
 * @author openmobster@gmail.com
 *
 */
public final class VectorUtil 
{
	public static void addAll(Vector to, Vector from)
	{
		if(to == null || from == null)
		{
			return;
		}
		
		for(int i=0,size=from.size(); i<size; i++)
		{
			to.addElement(from.elementAt(i));
		}
	}
	
	/**
	 * 
	 * @param from
	 * @param elements
	 */
	public static void removeElements(Vector from, Vector elements)
	{
		if(from == null || elements == null)
		{
			return;
		}
		
		for(int i=0,size=elements.size(); i<size; i++)
		{
			Object o = elements.elementAt(i);
			from.removeElement(o);
		}
	}
	
	public static Object[] toArray(Vector vector)
	{
		Object[] array = null;
		
		if(vector != null)
		{
			int size = vector.size();
			array = new Object[size];
			for(int i=0; i<size; i++)
			{
				array[i] = vector.elementAt(i);
			}
		}
		
		return array;
	}
}
