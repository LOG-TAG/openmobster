/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.webappsync.activity;

import android.util.Log;

import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import org.openmobster.core.mobileCloud.android_native.framework.BaseCloudActivity;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;

import org.openmobster.core.mobileCloud.api.javascript.MobileBeanBridge;
import org.openmobster.core.mobileCloud.api.model.MobileBean;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;

/**
 * @author openmobster@gmail.com
 */
public class SyncWebApp extends BaseCloudActivity
{
    private static final String LOG_TAG = "SyncWebApp";
    private WebView webView;
    
    @Override
    public void displayMainScreen() 
    { 
        if(!MobileBean.isBooted("offlineapp_demochannel"))
        {
            CommandContext commandContext = new CommandContext();
            commandContext.setTarget("/channel/bootup/helper");
            Services.getInstance().getCommandService().execute(commandContext);
            return;
        }
        
        //Layout the home screen
        setContentView(ViewHelper.findLayoutId(this, "home"));
        
        //Get the activity's WebView instance
        this.webView = (WebView)ViewHelper.findViewById(this, "webview");
        
        //Configure the webview
        WebSettings webSettings = this.webView.getSettings();
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);
        
        this.webView.setWebChromeClient(new MyWebChromeClient());
        this.webView.addJavascriptInterface(new MobileBeanBridge(), "mobileBean");
        
        this.webView.loadUrl("file:///android_asset/html/index.html");
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
            Log.d(LOG_TAG, message);
            result.confirm();
            return true;
        }
    }
}
