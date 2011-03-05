/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;

/**
 * @author openmobster@gmail.com
 * 
 */
public class MainActivity extends Activity
{
	private WebView webView;
	
	public MainActivity()
	{
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
	}

	@Override
	protected void onResume()
	{
		try
		{
			super.onResume();
			
			//render the main screen
			this.setContentView(ViewHelper.findLayoutId(this, "main"));
			
			//Find the WebView control
			this.webView = (WebView)ViewHelper.findViewById(this, "webview");
			
			//Enable Javascript...This is needed so that Javascript is allowed to execute
			//inside the WebView
			WebSettings webSettings = this.webView.getSettings();
			webSettings.setJavaScriptEnabled(true);
			
			//Register the 'Javascript Bridge' class under the 'jb' namespace
			//this class can be invoked from the HTML/Javascript side
			this.webView.addJavascriptInterface(new JavascriptBridge(), "jb");
			
			//Register the WebChromeClient to assist with alerts/debugging
			this.webView.setWebChromeClient(new MyWebChromeClient());
			
			//Load assets/html/index.html resource into the WebView control
			this.webView.loadUrl("file:///android_asset/html/index.html");
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
	}
	
	final class JavascriptBridge
	{
		public String callMe(String param1, String param2)
		{
			//Generate the returnValue from the bridge
			String toastValue = param1 + "," + param2;
			
			//Setup the Toast
			Toast toast = Toast.makeText(MainActivity.this, toastValue, Toast.LENGTH_LONG);
			
			//Show the Toast
			toast.show();
			
			return toastValue;
		}
	}
	
	/**
     * Provides a hook for calling "alert" from javascript. Useful for
     * debugging your javascript.
     */
    final class MyWebChromeClient extends WebChromeClient 
    {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) 
        {
            Log.d("JavascriptBridge", message);
            result.confirm();
            return true;
        }
    }
}
