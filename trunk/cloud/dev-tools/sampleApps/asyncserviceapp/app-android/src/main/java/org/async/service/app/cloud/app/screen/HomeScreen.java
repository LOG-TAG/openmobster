/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.async.service.app.cloud.app.screen;

import java.util.List;
import org.async.service.app.android.app.R;
import org.async.service.app.cloud.app.command.AsyncGetDetails;
import org.async.service.app.cloud.app.command.AsyncGetList;
import org.async.service.app.cloud.app.command.EmailBean;
import org.async.service.app.cloud.app.system.ActivationRequest;
import org.async.service.app.cloud.app.system.MyBootstrapper;
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

/**
 * The Main Screen of the App. This is rendered when the App is launched. It is registered as the 'bootstrap' screen
 * in 'resources/moblet-app/moblet-app.xml'
 * 
 * @author openmobster@gmail.com
 */

public class HomeScreen extends Activity{
	ListView listView=null; 
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		listView=(ListView)findViewById(R.id.list);		
		MyBootstrapper.getInstance().bootstrapUIContainerOnly(this);		
	}
	@Override
	protected void onStart(){			
		boolean isDeviceActivated = MyBootstrapper.getInstance().isDeviceActivated();
		if(!isDeviceActivated)
		{						
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
								show();
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
			show();
		}		
		super.onStart();
	}
	private void show(){
		Handler handler=new Handler(){
			@Override
			public void handleMessage(Message msg){
				int what=msg.what;
				if(what==1){
					final List<EmailBean> emails = (List<EmailBean>)msg.obj;
					final String[] ui = new String[emails.size()];
					for(int i=0,size=ui.length;i<size;i++)
					{
						EmailBean email = emails.get(i);
						ui[i] = email.getSubject();
					}
					listView.setAdapter(new ArrayAdapter(HomeScreen.this,android.R.layout.simple_list_item_1,ui));
					listView.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,	int position, long arg3){
							EmailBean emailBean=emails.get(position);
							Handler handler=new Handler(){
								public void handleMessage(Message msg) {
									int what=msg.what;
									if(what==1){
										String res=msg.obj.toString();
										
										AlertDialog.Builder builder=new AlertDialog.Builder(HomeScreen.this);										
										builder.setTitle("Detail");
										builder.setMessage(res);
										builder.setPositiveButton("Ok",null);
										builder.show();
										
									}									
								}								
							};
							new AsyncGetDetails(HomeScreen.this,handler, emailBean).execute();							
						}						
					});					
				}
			}
		};		
		new AsyncGetList(HomeScreen.this, handler).execute();		
		
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
}