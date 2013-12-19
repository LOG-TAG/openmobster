/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.webappsync.activity;

import org.openmobster.android.api.sync.MobileBean;
import org.openmobster.core.mobileCloud.android_native.framework.CloudService;
import org.openmobster.core.mobileCloud.android_native.framework.ServiceException;
import org.openmobster.core.mobileCloud.jscript.bridge.MobileBeanBridge;
import org.webapp.android.app.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import com.webappsync.command.ChannelBootupHelper;
import com.webappsync.system.ActivationRequest;
import com.webappsync.system.MyBootstrapper;

/**
 * Android Activity that integrates the OpenMobster Cloud as a service. This activity displays its HTML5 based GUI via the
 * standard 'WebView' Android component
 * 
 * @author openmobster@gmail.com
 */

public class SyncWebApp extends Activity
{
	private static final String LOG_TAG = "SyncWebApp";
	private WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);

		this.webView = (WebView) findViewById(R.id.webview);

		MyBootstrapper.getInstance().bootstrapUIContainerOnly(this);
	
		// Configure the webview
		WebSettings webSettings = this.webView.getSettings();
		webSettings.setSavePassword(false);
		webSettings.setSaveFormData(false);
		webSettings.setSupportZoom(false);

		// Javascript must be enabled to take advantage of HTML5/Javascript
		// based UI layer
		webSettings.setJavaScriptEnabled(true);

		this.webView.setWebChromeClient(new MyWebChromeClient());

		// Javascript bridge to the OpenMobster MobileBean service. This
		// provides access to data loaded in via the sync channel
		this.webView.addJavascriptInterface(new MobileBeanBridge(),	"mobileBean");

		// The application's main content specified in index.html file bundled
		// with the App in the asset folder
		this.webView.loadUrl("file:///android_asset/html/index.html");

	}

	@Override
	protected void onStart()
	{
		super.onStart();

		boolean isDeviceActivated = MyBootstrapper.getInstance().isDeviceActivated();
		if(!isDeviceActivated){
			final AlertDialog builder=new AlertDialog.Builder(SyncWebApp.this).create();
			builder.setTitle("App Activation");
			View view=LayoutInflater.from(this).inflate(R.layout.appactivation,null);
			builder.setView(view);
			final EditText serverip_t=(EditText)view.findViewById(R.id.serverip);
			final EditText portno_t=(EditText)view.findViewById(R.id.portno);
			final EditText emailid_t=(EditText)view.findViewById(R.id.emailid);
			final EditText password_t=(EditText)view.findViewById(R.id.password);
			
			builder.setButton("Submit",new OnClickListener() {			
				@Override
				public void onClick(DialogInterface arg0, int arg1)
				{
					Handler handler=new Handler(){
						@Override
						public void handleMessage(Message msg)
						{
							int what=msg.what;
							if(what==1){								
								
							}
						}				
					};
					String serverip=serverip_t.getText().toString();
					int portno=Integer.parseInt(portno_t.getText().toString());
					String emailid=emailid_t.getText().toString();
					String password=password_t.getText().toString();
					ActivationRequest activationRequest=new ActivationRequest(serverip,portno,emailid,password);
					new ToActivateDevice(SyncWebApp.this,handler,activationRequest).execute();									
				}
			});		
		
			builder.setButton2("Cancel",new OnClickListener() {			
				@Override
				public void onClick(DialogInterface arg0, int arg1){
					finish();
				}
			});
			builder.show();	
		}
		else{		
			if (!MobileBean.isBooted("webappsync_ticket_channel")){
				Handler handler = new Handler() {
					@Override
					public void handleMessage(Message msg){
						int what = msg.what;
						if (what == 1){

						}
					}
				};
				new ChannelBootupHelper(SyncWebApp.this, handler).execute();
			}
		}
	}
	
	class ToActivateDevice extends AsyncTask<Void,Void,Void>{

		Context context;
		ProgressDialog dialog = null;
		Handler handler;
		Message message;
		ActivationRequest activationRequest;
		public ToActivateDevice(Context context,Handler handler,ActivationRequest activationRequest){
			this.context=context;
			this.handler = handler;
			this.activationRequest=activationRequest;
		}
		
		@Override
		protected void onPostExecute(Void result)
		{
			dialog.dismiss();
			handler.sendMessage(message);
		}

		@Override
		protected void onPreExecute()
		{
			dialog = new ProgressDialog(context);		
			dialog.setMessage("Please wait...");
			dialog.setCancelable(false);
			dialog.show();	
		}
		
		@Override
		protected Void doInBackground(Void... arg0){			 
			try
			{
				CloudService.getInstance().activateDevice(activationRequest.getServerIP(),activationRequest.getPortNo(),activationRequest.getEmailId(),activationRequest.getPassword());
				MyBootstrapper.getInstance().bootstrapUIContainer(SyncWebApp.this);
			}
			catch(ServiceException se)
			{
			
			}
			message=handler.obtainMessage();
			message.what=1;
			return null;
		}		
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item)
	{
		String action = item.getTitle().toString();
		if (action.equalsIgnoreCase("New Ticket"))
		{
			SyncWebApp.this.webView.loadUrl("file:///android_asset/html/newticketindex.html");
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add("New Ticket");
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * Provides a hook for calling "alert" from javascript. Useful for debugging
	 * your javascript.
	 */
	final class MyWebChromeClient extends WebChromeClient
	{
		@Override
		public boolean onJsAlert(WebView view, String url, String message,
				JsResult result)
		{
			Log.d(LOG_TAG, message);
			result.confirm();

			if (message.equals("jo-refresh"))
			{
				SyncWebApp.this.webView.loadUrl("file:///android_asset/html/index.html");
			}
			return true;
		}
	}
}