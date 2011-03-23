/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.cloud.console.client.flow;

import org.openmobster.core.cloud.console.client.state.AppContext;
import org.openmobster.core.cloud.console.client.state.ContextRegistry;
import org.openmobster.core.cloud.console.client.ui.Screen;

import com.smartgwt.client.types.AnimationEffect;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Window;

/**
 * @author openmobster@gmail.com
 */
public final class TransitionService 
{
    TransitionService()
    {
        
    }
    
    public void transitionActiveWindow(Screen to)
    {
        AppContext appContext = ContextRegistry.getAppContext();
        
        //Close the currently active window
        Window activeWindow = (Window)appContext.getAttribute("active-window");
        if(activeWindow != null)
        {
            activeWindow.destroy();
        }
        
        //Open to the to screen
        Window newWindow = (Window)to.render();
        appContext.setAttribute("active-window", newWindow);
        newWindow.setAnimateShowTime(500);
        newWindow.animateShow(AnimationEffect.SLIDE);
    }
    
    public void closeActiveWindow()
    {
        AppContext appContext = ContextRegistry.getAppContext();
        
        //Close the currently active window
        Window activeWindow = (Window)appContext.getAttribute("active-window");
        if(activeWindow != null)
        {
            activeWindow.destroy();
            appContext.removeAttribute("active-window");
        }
    }
    
    public void transitionHost(Screen host)
    {
        AppContext appContext = ContextRegistry.getAppContext();
        Canvas canvas = (Canvas)appContext.getAttribute("host");
        if(canvas != null)
        {
            canvas.destroy();
        }
        
        //Display Authentication Screen
        Canvas newHost = host.render();
        appContext.setAttribute("host", newHost);
        
        newHost.setAnimateFadeTime(5000);
        newHost.animateShow(AnimationEffect.FADE);
    }
}
