/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.cloud;

import java.util.Iterator;
import java.util.Vector;
import java.util.Set;
import java.util.HashSet;

import org.appcelerator.kroll.KrollInvocation;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.titanium.TiContext;

import org.json.JSONObject;
import org.json.JSONArray;

import org.openmobster.android.api.rpc.MobileService;
import org.openmobster.android.api.rpc.Request;
import org.openmobster.android.api.rpc.Response;

/**
 *
 * @author openmobster@gmail.com
 */
@Kroll.proxy
public final class RPCProxy extends KrollProxy
{	
	public RPCProxy(TiContext context)
	{
		super(context);
	}
	
	@Kroll.method
	public String invoke(KrollInvocation invocation,String service,String jsonPayload)
	{
		try
		{
			Request request = new Request(service);
			
			//parse the payload
    		JSONObject json = new JSONObject(jsonPayload);
    		Iterator keys = json.keys();
    		while(keys.hasNext())
    		{
    			String name = (String)keys.next();
    			Object value = json.get(name);
    			
    			if(value instanceof String)
    			{
    				request.setAttribute(name, (String)value);
    			}
    			else if (value instanceof JSONArray)
    			{
    				Vector listAttribute = new Vector();
    				JSONArray array = (JSONArray)value;
    				int length = array.length();
    				for(int i=0; i<length; i++)
    				{
    					listAttribute.add(array.getString(i));
    				}
    				request.setListAttribute(name, listAttribute);
    			}
    		}
    		
    		Response response = MobileService.invoke(request);
			
			//Parse this into JSON
			json = new JSONObject();
			String[] names = response.getNames();
			Set<String> arrayNames = this.findArrays(response);
			Set<String> processedArrays = new HashSet<String>();
			for(String name:names)
			{
				if(name.contains("[") && name.contains("]"))
				{
					int endIndex = name.indexOf('[');
					String arrayName = name.substring(0, endIndex);
					
					if(processedArrays.contains(arrayName))
					{
						continue;
					}
					
					processedArrays.add(arrayName);
					JSONArray value = this.parseListAttribute(response, arrayName);
					if(value != null)
					{
						json.put(arrayName, value);
					}
				}
				else
				{
					if(!arrayNames.contains(name))
					{
						String value = response.getAttribute(name);
						json.put(name, value);
					}
				}
			}
			
			
			String jsonStr = json.toString();
			
			return jsonStr;
		}
		catch(Exception e)
		{
			//e.printStackTrace();
    		try
    		{
    			JSONObject error = new JSONObject();
    			error.put("status", "500");
    			error.put("statusMsg", e.toString());
    			return error.toString();
    		}
    		catch(Exception ex)
    		{
    			//we tried
    			//e.printStackTrace();
    			return null;
    		}
		}
	}
	
	private JSONArray parseListAttribute(Response response, String arrayName)
	{
		Vector list = response.getListAttribute(arrayName);
		
		if(list != null && list.size()>0)
		{
			int length = list.size();
			JSONArray value = new JSONArray();
			for(int i=0; i<length; i++)
			{
				value.put(list.get(i));
			}
			
			return value;
		}
		
		return null;
	}
	
	private Set<String> findArrays(Response response)
	{
		Set<String> arrayNames = new HashSet<String>();
		String[] names = response.getNames();
		for(String name:names)
		{
			if(name.contains("[") && name.contains("]"))
			{
				int endIndex = name.indexOf('[');
				String arrayName = name.substring(0, endIndex);
				arrayNames.add(arrayName);
			}
		}
		return arrayNames;
	}
}
