/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.offlineApp.android.app.screen;

import java.util.Map;
import org.openmobster.android.api.sync.MobileBean;
import org.openmobster.core.mobileCloud.android.configuration.Configuration;
import org.openmobster.core.mobileCloud.android_native.framework.CloudService;
import org.openmobster.core.mobileCloud.android_native.framework.ServiceException;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.offlineApp.android.app.R;
import com.offlineApp.android.app.command.ChannelBootupHelper;
import com.offlineApp.android.app.command.DemoDetails;
import com.offlineApp.android.app.command.DemoMobileRPC;
import com.offlineApp.android.app.command.PushTrigger;
import com.offlineApp.android.app.command.ResetChannel;
import com.offlineApp.android.app.system.ActivationRequest;
import com.offlineApp.android.app.system.MyBootstrapper;

/**
 * This is the home screen of the App. It displays the 'List' of data synchronized with the Cloud. It also presents a 'Menu' for displaying other functions of the App
 * 
 * @author openmobster@gmail.com
 */

public class HomeScreen extends Activity{
	ListView listView=null;
	MobileBean activeBeans[]=null;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		listView=(ListView)findViewById(R.id.list);
		
		
		MyBootstrapper.getInstance().bootstrapUIContainerOnly(this);
		
	}
	@Override
	protected void onStart()
	{
		
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
								showList();
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
		}else{
			showList();
		}
		
		Configuration configuration = Configuration.getInstance(this);		
		//Check to see if the 'Demo Beans' are synchronized from the 'Cloud'. If not, a 'Boot Sync' is issued.
		if(configuration.isActive() && !MobileBean.isBooted("offlineapp_demochannel"))
		{
			Handler handler=new Handler(){
				@Override
				public void handleMessage(Message msg){
					int what=msg.what;
					if(what==1){
						showList();						
					}					
				}
			};
			new ChannelBootupHelper(HomeScreen.this,handler).execute();
			return;
		}		
		//Show the List of "Demo Beans" synchronized from the 'Cloud'. 
		this.showList();		
		
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
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		menu.add("Reset Channel");
		menu.add("Push Trigger");
		menu.add("Make RPC Invocation");		
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item){	
		String action=item.getTitle().toString();
		if(action.equalsIgnoreCase("Reset Channel")){
			Handler handler=new Handler(){
				@Override
				public void handleMessage(Message msg){
					int what=msg.what;
					if(what==1){
						
						Toast.makeText(HomeScreen.this,"Reset success", 1).show();
						showList();
					}					
				}
			};
			new ResetChannel(HomeScreen.this,handler).execute();			
		}
		else if(action.equalsIgnoreCase("Push Trigger")){
			Handler handler=new Handler(){
				@Override
				public void handleMessage(Message msg){
					int what=msg.what;
					if(what==1){
						Toast.makeText(HomeScreen.this,"Push trigger success", 1).show();
						showList();
					}
				}
			};
			new PushTrigger(HomeScreen.this,handler).execute();			
		}
		else if(action.equalsIgnoreCase("Make RPC Invocation")){
			
			Handler handler=new Handler(){
				@Override
				public void handleMessage(Message msg){
					int what=msg.what;
					if(what==1){
						Map map=(Map) msg.obj;
						String param1=map.get("param1").toString();
						String param2=map.get("param1").toString();
						
						Toast.makeText(HomeScreen.this,"Param1 : "+param1+"\nParam2 : "+param2,1).show();
						
					}					
				}
			};
			new DemoMobileRPC(HomeScreen.this,handler).execute();			
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	private void showList()
	{
		//Reads the synchronized/locally stored demo beans from the 'offlineapp_demochannel' channel on the device
		activeBeans = MobileBean.readAll("offlineapp_demochannel");
		
		//Shows these beans in a List
		if(activeBeans != null && activeBeans.length >0)
		{
			String[] ui = new String[activeBeans.length];
			for(int i=0,size=ui.length;i<size;i++)
			{
				ui[i] = activeBeans[i].getValue("demoString");
			}
			listView.setAdapter(new ArrayAdapter(HomeScreen.this,android.R.layout.simple_list_item_1,ui));
			//List Listener
			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,	int selectedIndex, long arg3){
					MobileBean selectedBean = activeBeans[selectedIndex];
					Handler handler=new Handler(){
						@Override
						public void handleMessage(Message msg){
							int what=msg.what;
							if(what==1){
								String response=msg.obj.toString();
								Toast.makeText(HomeScreen.this,response, 1).show();
							}
						}
					};	
					String demoString=selectedBean.getValue("demoString");
					new DemoDetails(HomeScreen.this, handler,demoString).execute();										
				}				
			});
		}
	}
}