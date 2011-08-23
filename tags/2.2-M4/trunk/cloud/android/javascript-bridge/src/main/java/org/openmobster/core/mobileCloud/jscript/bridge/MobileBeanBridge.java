/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.jscript.bridge;

import org.json.JSONObject;
import org.json.JSONException;

import org.openmobster.android.api.sync.BeanList;
import org.openmobster.android.api.sync.MobileBean;

/**
 * A Javascript bridge that exposes the OpenMobster MobileBean service to the HTML5/Javascript layer of the App.
 * 
 * 
 * @author openmobster@gmail.com
 */
public final class MobileBeanBridge 
{
    /**
     * Access the value of a field of a domain object deployed in the Cloud
     * 
     * @param channel
     * @param oid
     * @param fieldUri
     * @return
     */
    public String getValue(String channel,String oid,String fieldUri)
    {
        MobileBean bean = MobileBean.readById(channel, oid);
        return bean.getValue(fieldUri);
    }
    
    /**
     * Reads oids of all the MobileBeans locally stored on the device
     * 
     * @param channel
     * @return
     */
    public String readAll(String channel)
    {
        MobileBean[] demoBeans = MobileBean.readAll(channel);
        if(demoBeans != null && demoBeans.length > 0)
        {
            StringBuilder buffer = new StringBuilder();
            int length = demoBeans.length;
            for(int i=0; i<length; i++)
            {
                MobileBean local = demoBeans[i];
                buffer.append(local.getId());
                if(i < length-1)
                {
                    buffer.append(",");
                }
            }
            
            return buffer.toString();
        }
        return null;
    }
    
    /**
     * Gets the length of an indexed/array property of the domain object
     * 
     * @param channel
     * @param oid
     * @param arrayUri
     * @return
     */
    public int arrayLength(String channel,String oid,String arrayUri)
    {
        MobileBean bean = MobileBean.readById(channel, oid);
        
        BeanList array = bean.readList(arrayUri);
        if(array != null)
        {
            return array.size();
        }
        return 0;
    }
    
    /**
     * Delete the bean locally and from the Cloud
     * 
     * @param channel
     * @param oid
     */
    public String deleteBean(String channel, String oid)
    {
    	MobileBean bean = MobileBean.readById(channel, oid);
    	String deletedBeanId = bean.getId();
    	
    	bean.delete();
    	
    	return deletedBeanId;
    }
    
    /**
     * Saves the specified 'field' value on the bean instance
     * 
     * @param channel
     * @param oid
     * @param fieldUri
     * @param value
     * @return
     */
    public String setValue(String channel,String oid,String fieldUri,String value)
    {
    	MobileBean bean = MobileBean.readById(channel, oid);
    	if(bean == null)
    	{
    		bean = MobileBean.newInstance(channel);
    	}
    	
    	bean.setValue(fieldUri, value);
    	bean.save();
    	
    	return bean.getId();
    }
}
