/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.api.javascript;

import org.openmobster.core.mobileCloud.api.model.MobileBean;
import org.openmobster.core.mobileCloud.api.model.BeanList;

/**
 * @author openmobster@gmail.com
 */
public final class MobileBeanBridge 
{
    public String getValue(String channel,String oid,String fieldUri)
    {
        MobileBean bean = MobileBean.readById(channel, oid);
        return bean.getValue(fieldUri);
    }
    
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
}
