/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.phonegap.plugin;

import org.json.JSONArray;
import org.json.JSONObject;

import org.openmobster.android.api.sync.MobileBean;
import org.openmobster.android.api.sync.BeanListEntry;
import org.openmobster.android.api.sync.BeanList;
import org.openmobster.core.mobileCloud.android.util.GeneralTools;

import com.phonegap.api.PhonegapActivity;
import com.phonegap.api.Plugin;
import com.phonegap.api.PluginResult;
import com.phonegap.api.PluginResult.Status;

/**
 *
 * @author openmobster@gmail.com
 */
public final class MobileBeanPlugin extends Plugin
{	
	@Override
	public void setContext(PhonegapActivity ctx) 
	{
		super.setContext(ctx);
	}

	@Override
	public synchronized PluginResult execute(String action, JSONArray input, String callbackId) 
	{
		PluginResult result = null;
		try
		{
			String returnValue = "";
			if(action.equals("readall"))
			{
				returnValue = this.readall(input);
			}
			else if(action.equals("value"))
			{
				returnValue = this.value(input);
			}
			else if(action.equals("test"))
			{
				returnValue = this.test(input);
			}
			else if(action.equals("insertIntoArray"))
			{
				returnValue = this.insertIntoArray(input);
			}
			else if(action.equals("arrayLength"))
			{
				returnValue = this.arrayLength(input);
			}
			else if(action.equals("commit"))
			{
				returnValue = this.commit(input);
			}
			else if(action.equals("addNewBean"))
			{
				returnValue = this.addNewBean(input);
			}
			else if(action.equals("deleteBean"))
			{
				returnValue = this.deleteBean(input);
			}
			else if(action.equals("updateBean"))
			{
				returnValue = this.updateBean(input);
			}
			
			result = new PluginResult(Status.OK,returnValue);
			return result;
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			result = new PluginResult(Status.ERROR,e.toString()+":"+e.getMessage());
			return result;
		}
	}
	
	
	
	@Override
	public boolean isSynch(String action) 
	{
		// TODO Auto-generated method stub
		//return super.isSynch(action);
		return true;
	}

	private String readall(JSONArray input) throws Exception
	{
		String channel = input.getString(0);
		
		MobileBean[] beans = MobileBean.readAll(channel);
		if(beans == null || beans.length == 0)
		{
			return "0";
		}
		
		JSONArray oids = new JSONArray();
        int length = beans.length;
        for(int i=0; i<length; i++)
        {
            MobileBean local = beans[i];
            oids.put(local.getId());
        }
        
        return oids.toString();
	}
	
	private String value(JSONArray input) throws Exception
    {
		String channel = input.getString(0);
		String oid = input.getString(1);
		String fieldUri = input.getString(2);
		
		MobileBean bean = SyncSession.getInstance().readBean(oid);
		if(bean == null)
		{
			bean = MobileBean.readById(channel, oid);
			SyncSession.getInstance().cacheBean(bean);
		}
		
		//used for debugging
		/*try
		{
			return bean.getValue(fieldUri);
		}
		catch(Exception e)
		{
			System.out.println("Broken on:"+fieldUri);
			throw e;
		}*/
		
		return bean.getValue(fieldUri);
    }
	
	private String insertIntoArray(JSONArray input) throws Exception
	{
		String channel = input.getString(0);
		String oid = input.getString(1);
		String fieldUri = input.getString(2);
		JSONObject value = input.getJSONObject(3); 
		
		MobileBean bean = SyncSession.getInstance().readBean(oid);
		if(bean == null)
		{
			bean = MobileBean.readById(channel, oid);
			SyncSession.getInstance().cacheBean(bean);
		}
		
		//Parse the JSONObject
		BeanListEntry arrayBean = new BeanListEntry();
		if(value.length() == 1)
		{
			//just a string array
			JSONArray names = value.names();
			int length = names.length();
			for(int i=0; i<length; i++)
			{
				String name = names.getString(i);
				String element = value.getString(name);
				arrayBean.setValue(element);
			}
		}
		else
		{
			//an object array
			JSONArray names = value.names();
			int length = names.length();
			for(int i=0; i<length; i++)
			{
				String name = names.getString(i);
				String element = value.getString(name);
				arrayBean.setProperty(name, element);
			}
		}
		
		bean.addBean(fieldUri, arrayBean);
		
		return this.arrayLength(input);
	}
	
	private String arrayLength(JSONArray input) throws Exception
	{
		String channel = input.getString(0);
		String oid = input.getString(1);
		String arrayUri = input.getString(2);
		
		MobileBean bean = SyncSession.getInstance().readBean(oid);
		if(bean == null)
		{
			bean = MobileBean.readById(channel, oid);
			SyncSession.getInstance().cacheBean(bean);
		}
		
		BeanList array = bean.readList(arrayUri);
		if(array == null)
		{
			return "0";
		}
		
		return ""+array.size();
	}
	
	private String test(JSONArray input) throws Exception
	{
		String tag = input.getString(0);
		
		System.out.println(tag);
		
		return tag;
	}
	
	private String commit(JSONArray input) throws Exception
	{
		SyncSession.getInstance().commit();
		return "0";
	}
	
	private String addNewBean(JSONArray input) throws Exception
	{
		String channel = input.getString(0);
		
		MobileBean newBean = MobileBean.newInstance(channel);
		
		//generate a temporary oid for this bean
		//temporary because the real one comes after sync with the Cloud
		String tempOid = GeneralTools.generateUniqueId();
		
		//cache this bean
		SyncSession.getInstance().cacheBean(tempOid, newBean);
		
		return tempOid;
	}
	
	private String deleteBean(JSONArray input) throws Exception
	{
		String channel = input.getString(0);
		String oid = input.getString(1);
		
		MobileBean bean = MobileBean.readById(channel, oid);
    	String deletedBeanId = bean.getId();
    	
    	bean.deleteWithoutSync();
    	
    	return deletedBeanId;
	}
	
	private String updateBean(JSONArray input) throws Exception
	{
		String channel = input.getString(0);
		String oid = input.getString(1);
		String fieldUri = input.getString(2);
		String value = input.getString(3);
		
		MobileBean bean = SyncSession.getInstance().readBean(oid);
		if(bean == null)
		{
			bean = MobileBean.readById(channel, oid);
			SyncSession.getInstance().cacheBean(bean);
		}
		
		bean.setValue(fieldUri, value);
		
		return "0";
	}
}
