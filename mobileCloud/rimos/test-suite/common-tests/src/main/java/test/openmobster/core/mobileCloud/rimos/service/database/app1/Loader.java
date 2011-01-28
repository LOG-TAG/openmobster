/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.core.mobileCloud.rimos.service.database.app1;

//ui
import net.rim.device.api.ui.UiApplication;


/**
 * 
 */
public class Loader extends UiApplication
{
	
    /**
     * 
     * @param args
     */
    public static void main(String[] args)
    {
    	try
    	{    		
    		Loader loader = new Loader();
        
    		//To make the application enter the event thread and start processing messages, 
    		//we invoke the enterEventDispatcher method
    		loader.enterEventDispatcher();    		
    	}
    	catch(Exception e)
    	{ 
    		System.out.println(e.toString());
    	}
    }

    /**
     * 
     *
     */
    public Loader()
    {        	    	
    	//Starting local sync test
    	TestSuiteRunner runner = new TestSuiteRunner();
    	this.addRadioListener(runner);
    	this.addSystemListener(runner);
    }            
}



