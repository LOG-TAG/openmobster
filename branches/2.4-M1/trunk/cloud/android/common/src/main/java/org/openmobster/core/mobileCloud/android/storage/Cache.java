/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.android.storage;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import org.json.JSONObject;

/**
 *
 * @author openmobster@gmail.com
 */
final class Cache
{
	private Map<String,JSONObject> cache;
	
	Cache()
	{
	}
	
	void start()
	{
		this.cache = new HashMap<String,JSONObject>();
	}
	
	void stop()
	{
		this.cache = null;
	}
	//----------------------------------------------------------------------------------------
	synchronized void put(String table,String recordId,String json) throws DBException
	{
		try
		{
			JSONObject record = new JSONObject(json);
			this.cache.put(table+":"+recordId, record);
		}
		catch(Exception e)
		{
			throw new DBException(Cache.class.getName(),"put", new Object[]{
				"Exception: "+e.getMessage()
			});
		}
	}
	
	Record get(String table,String recordId) throws DBException
	{
		try
		{
			JSONObject json = this.cache.get(table+":"+recordId);
			if(json == null)
			{
				return null;
			}
			
			Map<String,String> state = new HashMap<String,String>();
			Iterator keys = json.keys();
			if(keys != null)
			{
				while(keys.hasNext())
				{
					String localName = (String)keys.next();
					String localValue = json.getString(localName);
					state.put(localName, localValue);
				}
			}
			
			Record record = new Record(state);
			return record;
		}
		catch(Exception e)
		{
			throw new DBException(Cache.class.getName(),"get", new Object[]{
				"Exception: "+e.getMessage()
			});
		}
	}
	
	Map<String,Record> all(String table) throws DBException
	{
		try
		{
			Map<String,Record> all = new HashMap<String,Record>();
			
			Set<String> keys = this.cache.keySet();
			for(String key:keys)
			{
				if(key.startsWith(table+":"))
				{
					JSONObject json = this.cache.get(key);
					
					Map<String,String> state = new HashMap<String,String>();
					Iterator jsonKeys = json.keys();
					if(jsonKeys != null)
					{
						while(jsonKeys.hasNext())
						{
							String localName = (String)jsonKeys.next();
							String localValue = json.getString(localName);
							state.put(localName, localValue);
						}
					}
					
					Record record = new Record(state);
					all.put(record.getRecordId(),record);
				}
			}
			
			return all;
		}
		catch(Exception e)
		{
			throw new DBException(Cache.class.getName(),"all", new Object[]{
				"Exception: "+e.getMessage()
			});
		}
	}
	
	synchronized void invalidate(String table,String recordId) throws DBException
	{
		this.cache.remove(table+":"+recordId);
	}
	
	synchronized void clear(String table) throws DBException
	{
		Set<String> keys = this.cache.keySet();
		for(String key:keys)
		{
			if(key.startsWith(table+":"))
			{
				this.cache.remove(key);
			}
		}
	}
}
