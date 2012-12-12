/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.cloud.console.client.flow;

/**
 * @author openmobster@gmail.com
 */
public final class FlowServiceRegistry 
{
    private static TransitionService transitionService;
    
    public static void init()
    {
        transitionService = new TransitionService();
    }
    
    public static TransitionService getTransitionService()
    {
        if(transitionService == null)
        {
            throw new IllegalStateException("FlowServiceRegistry is uninitialized");
        }
        return transitionService;
    }
}
