/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.showcase.app;

import java.util.ArrayList;
import java.util.HashMap;

import org.openmobster.core.mobileCloud.android_native.framework.CloudService;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;
import org.openmobster.showcase.app.crud.CRUDMainScreen;
import org.openmobster.showcase.app.system.ActivationRequest;
import org.showcase.app.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

/**
 * Controls the 'home' screen that is displayed when the App is first launched.
 * 
 * It displays the Showcase options in a 'ListView' 
 * 
 * @author openmobster@gmail.com
 */

public class HomeScreen extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);	
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		
		//Bootstrap the OpenMobster Service in the main activity of your App
		CloudService.getInstance().start(this);
	}
	
	
	@Override
	protected void onResume()
	{
		super.onResume();
		
		//Check to make sure the App is activated with the OpenMobster Backend
		boolean isDeviceActivated = CloudService.getInstance().isDeviceActivated();
		if(!isDeviceActivated)
		{						
			this.startDeviceActivation();
			return;
		}
		
		setupScreen();
	}
	
	private void startDeviceActivation()
	{
		final AlertDialog activationDialog = new AlertDialog.Builder(HomeScreen.this).create();
		activationDialog.setTitle("App Activation");
		
		View view=LayoutInflater.from(this).inflate(R.layout.appactivation,null);
		activationDialog.setView(view);
		final EditText serverip_t=(EditText)view.findViewById(R.id.serverip);
		final EditText portno_t=(EditText)view.findViewById(R.id.portno);
		final EditText emailid_t=(EditText)view.findViewById(R.id.emailid);
		final EditText password_t=(EditText)view.findViewById(R.id.password);
		activationDialog.setCancelable(false);
		
		activationDialog.setButton(AlertDialog.BUTTON_POSITIVE,"Submit", (DialogInterface.OnClickListener)null);		
	
		activationDialog.setButton2("Cancel",new OnClickListener() {			
			@Override
			public void onClick(DialogInterface arg0, int arg1){
				finish();
			}
		});
		
		activationDialog.setOnShowListener(new DialogInterface.OnShowListener() {
			
			@Override
			public void onShow(DialogInterface d)
			{
				Button submit = activationDialog.getButton(AlertDialog.BUTTON_POSITIVE);
				submit.setOnClickListener(new View.OnClickListener(){
					@Override
					public void onClick(View view)
					{
						String serverip=serverip_t.getText().toString();
						String portnoStr=portno_t.getText().toString();
						String emailid=emailid_t.getText().toString();
						String password=password_t.getText().toString();
						if(serverip == null || serverip.trim().length()==0 ||
						   portnoStr == null || portnoStr.trim().length()==0 ||
						   emailid == null || emailid.trim().length()==0 ||
						   password == null || password.trim().length()==0
						)
						{
							ViewHelper.getOkModal(HomeScreen.this, "App Activation Failure", "All the fields are required for a successful activation").show();
							return;
						}
						
						Handler handler=new Handler(){
							@Override
							public void handleMessage(Message msg)
							{
								int what=msg.what;
								if(what==1)
								{
									activationDialog.dismiss();
									setupScreen();
								}
							}				
						};
						
						int portno = Integer.parseInt(portnoStr);
						ActivationRequest activationRequest=new ActivationRequest(serverip,portno,emailid,password);
						new ToActivateDevice(HomeScreen.this,handler,activationRequest).execute();
					}
				});
			}
		});
		
		activationDialog.show();
	}
	
	private class ToActivateDevice extends AsyncTask<Void,Void,String>
	{

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
		protected void onPostExecute(String result)
		{
			dialog.dismiss();
			
			if(result != null)
			{
				ViewHelper.getOkModal(HomeScreen.this, "App Activation Failure", result).show();
			}
			else
			{
				handler.sendMessage(message);
			}
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
		protected String doInBackground(Void... arg0){			 
			try
			{
				//Start device activation
				CloudService.getInstance().activateDevice(activationRequest.getServerIP(),
				activationRequest.getPortNo(),activationRequest.getEmailId(),activationRequest.getPassword());
				
				//Start the local OpenMobster service after a successful activation
				CloudService.getInstance().start(HomeScreen.this);
				
				message=handler.obtainMessage();
				message.what=1;
				
				return null;
			}
			catch(Exception se)
			{
				
				return se.getMessage();
			}
		}		
	}
	//-------------------------------------------------------------------------------------------------------------------------
	private void setupScreen()
	{
		//Populate the List View
		ListView view = (ListView)findViewById(R.id.list);
		
		ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
		
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("empty", "");
		map.put("title", "CRUD/Sync Showcase");
		mylist.add(map);
		
		SimpleAdapter showcaseAdapter = new SimpleAdapter(this, mylist,R.layout.home_row,
	            new String[] {"empty", "title"}, new int[] {ViewHelper.findViewId(this, "empty"), ViewHelper.findViewId(this, "title")});
	    view.setAdapter(showcaseAdapter);
	    
	    OnItemClickListener clickListener = new ClickListener();
		view.setOnItemClickListener(clickListener);
	}
	
	private class ClickListener implements OnItemClickListener
	{
		private ClickListener()
		{
		}
		
		public void onItemClick(AdapterView<?> parent, View view, int position,long id)
		{
			int selectedIndex = position;
			
			if(selectedIndex == 0)
			{
				Intent intent=new Intent(HomeScreen.this,CRUDMainScreen.class);
				startActivity(intent);
			}
		}
	}	
}