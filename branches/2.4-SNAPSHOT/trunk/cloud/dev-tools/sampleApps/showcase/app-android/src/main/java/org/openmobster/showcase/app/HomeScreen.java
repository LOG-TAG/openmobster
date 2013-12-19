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
import org.openmobster.android.api.sync.MobileBean;
import org.openmobster.core.mobileCloud.android_native.framework.CloudService;
import org.openmobster.core.mobileCloud.android_native.framework.ServiceException;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.showcase.app.camera.CameraMainScreen;
import org.openmobster.showcase.app.command.framework.MainScreen;
import org.openmobster.showcase.app.crud.CRUDMainScreen;
import org.openmobster.showcase.app.system.ActivationRequest;
import org.openmobster.showcase.app.system.MyBootstrapper;
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
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);	
		MyBootstrapper.getInstance().bootstrapUIContainerOnly(this);
		setupScreen();
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		
		if(!MyBootstrapper.getInstance().isDeviceActivated()){
			final AlertDialog builder=new AlertDialog.Builder(HomeScreen.this).create();
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
					new ToActivateDevice(HomeScreen.this,handler,activationRequest).execute();									
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
				MyBootstrapper.getInstance().bootstrapUIContainer(HomeScreen.this);
			}
			catch(ServiceException se)
			{
			
			}
			message=handler.obtainMessage();
			message.what=1;
			return null;
		}		
	}

	
	private void setupScreen()
	{
		//Populate the List View
		ListView view = (ListView)findViewById(R.id.list);
		
		ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
		
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("empty", "");
		map.put("title", "CRUD/Sync Showcase");
		mylist.add(map);
		
		HashMap<String, String> map2 = new HashMap<String, String>();
		map2.put("empty", "");
		map2.put("title", "Command Framework Showcase");
		mylist.add(map2);
		
		HashMap<String, String> map3 = new HashMap<String, String>();
		map3.put("empty", "");
		map3.put("title", "Camera Showcase");
		mylist.add(map3);
		
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
				if(!MobileBean.isBooted(AppConstants.channel))
				{
					CommandContext commandContext = new CommandContext();
					commandContext.setTarget("/channel/bootup/helper");
					Services.getInstance().getCommandService().execute(commandContext);
										
				}
				else
				{
					
					Intent intent=new Intent(HomeScreen.this,CRUDMainScreen.class);
					startActivity(intent);
								
					//NavigationContext.getInstance().navigate("/crud");
				}
			}
			else if(selectedIndex == 1)
			{
				
				Intent intent=new Intent(HomeScreen.this,MainScreen.class);
				startActivity(intent);
				
				//NavigationContext.getInstance().navigate("/command/framework");
			}
			else if(selectedIndex == 2)
			{
				Intent intent=new Intent(HomeScreen.this,CameraMainScreen.class);
				startActivity(intent);				
				
				//NavigationContext.getInstance().navigate("/camera");
			}
		}
	}	
}