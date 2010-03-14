package org.openmobster.core.mobileCloud.android.module.test.bus;

import java.lang.reflect.Field;

import android.app.Activity;
import android.os.Bundle;

import org.openmobster.core.mobileCloud.android.testsuite.TestSuite;
import org.openmobster.core.mobileCloud.android.testsuite.TestContext;

import org.openmobster.core.mobileCloud.android.kernel.DeviceContainer;

public class TestApp extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
    	try
    	{
	    	//android.os.Debug.waitForDebugger();	    	
	        super.onCreate(savedInstanceState);
	        
	        String layoutClass = "org.openmobster.core.mobileCloud." +
	        "android.module.test.bus.R$layout";
	        String main = "main";
	        
	        Class clazz = Class.forName(layoutClass);	        
	        Field field = clazz.getField(main);
	        
	        setContentView(field.getInt(clazz));
	        
	        this.executeTestFramework();   
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace(System.out);
    	}
    }
    
    private void executeTestFramework() throws Exception
    {
    	Thread t = new Thread(new TestExecutor());
    	t.start();
    }
    
    private class TestExecutor implements Runnable
    {
    	public void run()
    	{
    		try
    		{
	    		TestSuite suite = new TestSuite();
	    		
	        	TestContext testContext = new TestContext();
	        	suite.setContext(testContext);
	        	suite.getContext().setAttribute("android:context", 
	        	TestApp.this);
	        	
	        	//Initialize the kernel
	        	DeviceContainer.getInstance(TestApp.this).startup();
	        	
	        	//Load the tests
	        	suite.load();
	        	
	        	suite.execute();
    		}
    		catch(Exception e)
        	{
        		e.printStackTrace(System.out);
        	}
    	}
    }
}
