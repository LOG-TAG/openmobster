/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.android_native.framework;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;

import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;

/**
 * @author openmobster@gmail.com
 * 
 */
public class App extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		try
		{
			super.onCreate(savedInstanceState);
									
			CommonApp.onCreate(this,savedInstanceState);       	
		} 
		catch (Exception e)
		{
			//e.printStackTrace(System.out);
			ErrorHandler.getInstance().handle(new SystemException(this.getClass().getName(), "onCreate", new Object[]{
				"Message:"+e.getMessage(),
				"Exception:"+e.toString()
			}));
			this.showDialog(0);
		}
	}
	
	@Override
	protected void onDestroy()
	{								
    	try
    	{    		    		    		    		    		    		
    		super.onDestroy();
    	}
    	catch(Exception e)
    	{
    		ErrorHandler.getInstance().handle(new SystemException(this.getClass().getName(), "onDestroy", new Object[]{
				"Message:"+e.getMessage(),
				"Exception:"+e.toString()
			}));
    	}
	}
	
	
	@Override
	protected void onStart()
	{
		try
		{
			super.onStart();
			
			CommonApp.onStart(this);
		}
		catch (Exception e)
		{
			//e.printStackTrace(System.out);
			ErrorHandler.getInstance().handle(new SystemException(this.getClass().getName(), "onStart", new Object[]{
				"Message:"+e.getMessage(),
				"Exception:"+e.toString()
			}));
			this.showDialog(0);
		}
	}

	@Override
	protected void onResume()
	{
		try
		{
			//Loads the home screen
			super.onResume();
			
			CommonApp.onResume(this);
		}
		catch(Exception e)
		{
			ErrorHandler.getInstance().handle(new SystemException(this.getClass().getName(), 
			"onResume", new Object[]{
				"Message:"+e.getMessage(),
				"Exception:"+e.toString()
			}));
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{				
		menu.clear();
		super.onPrepareOptionsMenu(menu);
		
	    return CommonApp.onPrepareOptionsMenu(this, menu);
	}
	
	protected void bootstrapContainer() throws Exception
	{
		//Initialize the kernel
		CommonApp.bootstrapContainer(this);
	}
	
	protected void showError()
	{
		CommonApp.showError(this);
	}
	//-----Global Event Handling------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(CommonApp.onKeyDown(keyCode, event))
		{
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	@Override
	protected Dialog onCreateDialog(int id)
	{
		AlertDialog dialog = ViewHelper.getOkAttachedModalWithCloseApp(this, "System Error", "CloudManager App is not found or activated");
		return dialog;
	}
}
