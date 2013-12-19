/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.app;

import java.util.ArrayList;
import java.util.HashMap;
import org.openmobster.android.api.sync.CommitException;
import org.openmobster.android.api.sync.MobileBean;
import org.openmobster.core.mobileCloud.android_native.framework.CloudService;
import org.openmobster.core.mobileCloud.android_native.framework.ServiceException;
import org.openmobster.system.ActivationRequest;
import org.openmobster.system.MyBootstrapper;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import com.icloud.android.app.R;

/**
 * Controls the 'home' screen that is displayed when the App is first launched.
 * 
 * Displays all the ticket instances that are synchronized with the Cloud
 * 
 * It displays the tickets in a 'ListView' 
 * 
 * The screen menu provides the following functions, 'New Ticket', 'Reset Channel', and 'Demo Push'
 * @author openmobster@gmail.com
 */

public class HomeScreen extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		MyBootstrapper.getInstance().bootstrapUIContainerOnly(this);
		
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		
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
								showBeans();
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
			showBeans();
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
	
	private void showBeans()
	{
		//Read all the beans from the channel
		MobileBean[] beans = MobileBean.readAll("cloud_channel");
		if(beans != null && beans.length > 0)
		{
			//Populate the List View
			ListView view = (ListView)findViewById(R.id.list);
			
			//Prepare the data for the adapter. Data is read from the ticket bean instances
			ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
			for(MobileBean local:beans)
			{
				HashMap<String, String> map = new HashMap<String, String>();
				
				String name = local.getValue("name");
				String value = local.getValue("value");
				
				if(name.length() > 25)
				{
					name = name.substring(0, 22)+"...";
				}
				
				if(value.length() > 25)
				{
					value = value.substring(0, 22)+"...";
				}
				
				map.put("name", name);
				map.put("value", value);
				mylist.add(map);
			}
			
			SimpleAdapter beanAdapter = new SimpleAdapter(this, mylist,R.layout.bean_row,
		            new String[] {"name", "value"}, new int[] {R.id.name, R.id.value});
		    view.setAdapter(beanAdapter);
		    
		    //List Listener...used to respond to selecting a ticket instance
			OnItemClickListener clickListener = new ClickListener(beans);
			view.setOnItemClickListener(clickListener);
		}	
		else
		{
			//Populate the List View
			ListView view = (ListView)findViewById(R.id.list);
			
			//Prepare the data for the adapter. Data is read from the ticket bean instances
			ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
			
			SimpleAdapter beanAdapter = new SimpleAdapter(this, mylist, R.layout.bean_row,
		            new String[] {"name", "value"}, new int[] {R.id.name,R.id.value});
		    view.setAdapter(beanAdapter);
		}
	}
	private class ClickListener implements OnItemClickListener
	{
		private MobileBean[] activeBeans;
		
		private ClickListener(MobileBean[] activeBeans)
		{
			this.activeBeans = activeBeans;
		}
		
		public void onItemClick(AdapterView<?> parent, View view, int position,	long id){
						
			//Get the ticket bean selected by the user
			int selectedIndex = position;
			final MobileBean selectedBean = activeBeans[selectedIndex];
			
			String name = selectedBean.getValue("name");
			String value = selectedBean.getValue("value");
			
			AlertDialog.Builder builder = new AlertDialog.Builder(HomeScreen.this).setCancelable(false);
			View dialogView=LayoutInflater.from(HomeScreen.this).inflate(R.layout.dialog,null);
			
			//Setup the Name Value
			int nameEditPointer = R.id.nameEdit; 
			final EditText nameEdit = (EditText)dialogView.findViewById(nameEditPointer);
			nameEdit.setText(name);
			
			//Setup the Value Value
			int valueEditPointer = R.id.valueEdit;
			final EditText valueEdit = (EditText)dialogView.findViewById(valueEditPointer);
			valueEdit.setText(value);
			
			//Setup the buttons on the dialog box
			builder.setPositiveButton("Update", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int id)
				{
					String name = nameEdit.getText().toString();
					String value = valueEdit.getText().toString();
					
					if(name == null || name.trim().length()==0)
					{
						Toast.makeText(HomeScreen.this,"'Name' is required",1).show();
						return;
					}
					if(value == null || value.trim().length()==0)
					{
						Toast.makeText(HomeScreen.this,"'Value' is required",1).show();
						return;
					}
					
					selectedBean.setValue("name", name);
					selectedBean.setValue("value", value);
					try
					{
						selectedBean.save();
					}
					catch(CommitException se)
					{
						try
						{
							selectedBean.refresh();
							selectedBean.setValue("name", name);
							selectedBean.setValue("value", value);
							selectedBean.save();
						}
						catch(Exception e)
						{
							//we tried, put up an error message
							Toast.makeText(HomeScreen.this,"Error",1).show();							
						}
					}
					
					showBeans();
				}
			});
			
			builder.setNegativeButton("Delete", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int id)
				{
					try
					{
						selectedBean.delete();
					}
					catch(CommitException se)
					{
						Toast.makeText(HomeScreen.this,"Error : "+se.getMessage(),1).show();						
						return;
					}
					showBeans();
				}
			});
			
			builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int id)
				{
					showBeans();
				}
			});
			
			AlertDialog beanDialog = builder.create();
			beanDialog.setView(dialogView);
			beanDialog.show();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		
		menu.add("New Ticket");
		
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		String action=item.getTitle().toString();
		if(action.equalsIgnoreCase("New Ticket")){
			AlertDialog.Builder builder = new AlertDialog.Builder(this).setCancelable(false);
			LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			int dialogLayout = R.layout.dialog;
			View dialogView = inflater.inflate(dialogLayout, null);
			
			//Setup the Name Value
			int nameEditPointer = R.id.nameEdit; 
			final EditText nameEdit = (EditText)dialogView.findViewById(nameEditPointer);
			nameEdit.setText("");
			
			//Setup the Value Value
			int valueEditPointer = R.id.valueEdit;
			final EditText valueEdit = (EditText)dialogView.findViewById(valueEditPointer);
			valueEdit.setText("");
			
			//Setup the buttons on the dialog box
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int id)
				{
					String name = nameEdit.getText().toString();
					String value = valueEdit.getText().toString();
					
					if(name == null || name.trim().length()==0)
					{
						Toast.makeText(HomeScreen.this,"'Name' is required",0).show();
						return;
					}
					if(value == null || value.trim().length()==0)
					{
						Toast.makeText(HomeScreen.this,"'Value' is required",0).show();						
						return;
					}
					
					MobileBean newBean = MobileBean.newInstance("cloud_channel");
					newBean.setValue("name", name);
					newBean.setValue("value", value);
					try
					{
						newBean.save();
					}
					catch(CommitException se){
						Toast.makeText(HomeScreen.this,"Error",0).show();						
						return;
					}
					
					showBeans();
					
				}
			});
			
			builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int id)
				{
					showBeans();
				}
			});
			
			AlertDialog beanDialog = builder.create();
			beanDialog.setView(dialogView);
			beanDialog.show();	
			
		}		
		return super.onOptionsItemSelected(item);
	}	
}