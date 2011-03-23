/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.util;

/**
 * @author openmobster@gmail.com
 *
 */
public final class XMLUtil 
{	
	public static String cleanupXML(String xml)
	{
		String cleanedup = "";
		
		if(xml != null)
		{
			cleanedup = StringUtil.replaceAll(xml, "&", "&amp;");
			cleanedup = StringUtil.replaceAll(cleanedup,"<", "&lt;");
			cleanedup = StringUtil.replaceAll(cleanedup,">", "&gt;");			
			cleanedup = StringUtil.replaceAll(cleanedup,"\"", "&quot;");
			cleanedup = StringUtil.replaceAll(cleanedup,"'", "&apos;");
		}
		
		return cleanedup;
	}
		
	public static String restoreXML(String xml)
	{
		String restored = "";
		
		if(xml != null)
		{
			restored = StringUtil.replaceAll(xml,"&apos;", "'");
			restored = StringUtil.replaceAll(restored,"&quot;", "\"");
			restored = StringUtil.replaceAll(restored,"&gt;", ">");			
			restored = StringUtil.replaceAll(restored,"&lt;", "<");
			restored = StringUtil.replaceAll(restored,"&amp;", "&");
		}
		
		return restored;
	}
					
	public static String removeCData(String xml)
	{
		String cleanXML = StringUtil.replaceAll(xml,"<![CDATA[", "");
		cleanXML = StringUtil.replaceAll(cleanXML,"]]>", "");
		
		return cleanXML;
	}
		
	public static String addCData(String xml)
	{
		String cdataXml = "<![CDATA[";
		cdataXml += xml;
		cdataXml += "]]>";
		return cdataXml;
	}	
}
