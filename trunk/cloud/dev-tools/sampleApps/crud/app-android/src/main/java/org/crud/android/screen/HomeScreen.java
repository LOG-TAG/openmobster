/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.crud.android.screen;

import java.util.ArrayList;
import java.util.HashMap;
import org.crud.android.screen.R;
import org.crud.android.command.DeleteTicket;
import org.crud.android.command.DemoPush;
import org.crud.android.command.PlainPush;
import org.crud.android.command.ResetChannel;
import org.crud.android.system.ActivationRequest;
import org.crud.android.system.MyBootstrapper;
import org.openmobster.android.api.sync.MobileBean;
import org.openmobster.core.mobileCloud.android_native.framework.CloudService;
import org.openmobster.core.mobileCloud.android_native.framework.ServiceException;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class HomeScreen extends Activity{
	public static MobileBean[] activeBeans;
	ListView listView=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		listView=(ListView) findViewById(R.id.list);				
		MyBootstrapper.getInstance().bootstrapUIContainerOnly(this);				
	}
	@Override
	protected void onStart()
	{
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
								showTicket();
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
			showTicket();
		}
		super.onStart();
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
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add("New Ticket");
		menu.add("Reset Channel");		
		menu.add("Demo Push");
		menu.add("Plain Push");		
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item)
	{
		String title=item.getTitle().toString();
		if(title.equalsIgnoreCase("New Ticket")){
			Intent intent=new Intent(HomeScreen.this,NewTicketScreen.class);
			startActivity(intent);
			finish();
		}		
		else if(title.equalsIgnoreCase("Reset Channel")){
			Handler handler=new Handler(){
				@Override
				public void handleMessage(Message msg)
				{
					int what=msg.what;
					if(what==1){
						Toast.makeText(HomeScreen.this,"Reset success",1).show();
						showTicket();
					}
				}				
			};
			new ResetChannel(HomeScreen.this,handler).execute();			
		}				
		else if(title.equalsIgnoreCase("Demo Push")){
			Handler handler=new Handler(){
				@Override
				public void handleMessage(Message msg)
				{
					int what=msg.what;
					if(what==1){
						Toast.makeText(HomeScreen.this,"Demo push success",1).show();
						showTicket();
					}
				}				
			};
			new DemoPush(HomeScreen.this,handler).execute();			
		}		
		else if(title.equalsIgnoreCase("Plain Push")){
			Handler handler=new Handler(){
				@Override
				public void handleMessage(Message msg)
				{
					int what=msg.what;
					if(what==1){
						Toast.makeText(HomeScreen.this,"Plain push success",1).show();
						showTicket();
					}
				}				
			};
			new PlainPush(HomeScreen.this,handler).execute();			
		}				
		return super.onMenuItemSelected(featureId, item);
	}
	public void showTicket(){		
		activeBeans = MobileBean.readAll("crm_ticket_channel");
		if(activeBeans != null && activeBeans.length > 0)
		{			
			ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
			for(MobileBean local:activeBeans)
			{
				HashMap<String, String> map = new HashMap<String, String>();				
				String customer = local.getValue("customer");
				String title = local.getValue("title");
				
				if(customer.length() > 25)
				{
					customer = customer.substring(0, 22)+"...";
				}
				
				if(title.length() > 25)
				{
					title = title.substring(0, 22)+"...";
				}
				
				map.put("customer", customer);
				map.put("title", title);
				mylist.add(map);
			}
			
			SimpleAdapter ticketAdapter = new SimpleAdapter(HomeScreen.this, mylist,R.layout.ticket_row,new String[] {"customer", "title"}, new int[] {R.id.customer,R.id.title});
		    listView.setAdapter(ticketAdapter);		 
		    listView.setOnItemClickListener(new MyItemClickListener(activeBeans));
		}
	}
	class MyItemClickListener implements OnItemClickListener{
		private MobileBean[] activeBeans;
		MyItemClickListener(MobileBean[] activeBeans){
			this.activeBeans=activeBeans;
		}
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1,final int selectedIndex,long arg3)
		{
			final MobileBean selectedBean = activeBeans[selectedIndex];
			AlertDialog.Builder builder = new AlertDialog.Builder(HomeScreen.this);
			builder.setMessage(selectedBean.getValue("comment"));
			builder.setCancelable(false);
			builder.setTitle("Specialist: "+selectedBean.getValue("specialist"));
			builder.setPositiveButton("Update", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int id){
					Intent intent=new Intent(HomeScreen.this,UpdateTicketScreen.class);
					intent.putExtra("SelectedIndex",selectedIndex);
					startActivity(intent);
					finish();
					dialog.dismiss();
				}
			});			
			builder.setNegativeButton("Delete", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int id){
					Handler handler = new Handler() {
						@Override
			        	public void handleMessage(Message msg) {
							int what= msg.what;
			        		if(what==1){
			        			Toast.makeText(HomeScreen.this,"Record successfully deleted",1).show();
			        		}
			        		showTicket();			        				
						}
			        };			        		
			        new DeleteTicket(HomeScreen.this, handler, selectedBean).execute();
			        dialog.dismiss();
				}
			});
			builder.setNeutralButton("Close", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int id){
					dialog.dismiss();
				}
			});			
			AlertDialog alert = builder.create();
			alert.show();			
		}		
	}
}


//public class HomeScreen extends Screen
//{
//	private Integer screenId;
//	
//	@Override
//	public void render()
//	{
//		try
//		{
//			//Lays out the screen based on configuration in res/layout/home.xml
//			final Activity currentActivity = Services.getInstance().getCurrentActivity();
//			
//			String layoutClass = currentActivity.getPackageName()+".R$layout";
//			String home = "home";
//			Class clazz = Class.forName(layoutClass);
//			Field field = clazz.getField(home);
//			
//			this.screenId = field.getInt(clazz);						
//		}
//		catch(Exception e)
//		{
//			SystemException se = new SystemException(this.getClass().getName(), "render", new Object[]{
//				"Message:"+e.getMessage(),
//				"Exception:"+e.toString()
//			});
//			ErrorHandler.getInstance().handle(se);
//			throw se;
//		}
//	}
//	
//	@Override
//	public Object getContentPane()
//	{
//		return this.screenId;
//	}
//	
//	@Override
//	public void postRender()
//	{
//		Activity app = Services.getInstance().getCurrentActivity();
//		
//		//Bootstrapping the App...These 3 checks should be sucked into the framework.instead of spilled into the App
//		AppResources res = Services.getInstance().getResources();
//		Configuration configuration = Configuration.getInstance(app);
//		
//		if(configuration.isActive() && !MobileBean.isBooted("crm_ticket_channel"))
//		{
//			CommandContext commandContext = new CommandContext();
//			commandContext.setTarget("/channel/bootup/helper");
//			Services.getInstance().getCommandService().execute(commandContext);
//			
//			return;
//		}
//		
//		//Setup the App Menu
//		this.setupMenu(app);
//		
//		//Display the tickets
//		this.showTickets(app);
//	}
//	//------------------------------------------------------------------------------------------------------------------
//	private void setupMenu(Activity app)
//	{
//		//Get the menu associated with this screen
//		Menu menu = (Menu)NavigationContext.getInstance().
//		getAttribute("options-menu");
//		
//		if(menu != null)
//		{
//			//Add the 'New Ticket' Menu Item
//			MenuItem newTicket = menu.add(Menu.NONE, Menu.NONE, 0, "New Ticket");
//			newTicket.setOnMenuItemClickListener(new OnMenuItemClickListener()
//			{
//				public boolean onMenuItemClick(MenuItem clickedItem)
//				{
//					//UserInteraction/Event Processing...This will navigate to the NewTicket Screen
//					NavigationContext.getInstance().navigate("/new/ticket");
//					return true;
//				}
//			});
//			
//			//Add the 'Reset Channel' Menu Item
//			MenuItem resetChannel = menu.add(Menu.NONE, Menu.NONE, 1, "Reset Channel");
//			resetChannel.setOnMenuItemClickListener(new OnMenuItemClickListener()
//			{
//				public boolean onMenuItemClick(MenuItem clickedItem)
//				{
//					//UserInteraction/Event Processing...this will cleanup the channel by fully
//					//resetting/booting up the channel with the Cloud
//					CommandContext commandContext = new CommandContext();
//					commandContext.setTarget("/reset/channel");
//					Services.getInstance().getCommandService().execute(commandContext);
//					return true;
//				}
//			});
//			
//			MenuItem pushTrigger = menu.add(Menu.NONE, Menu.NONE, 2, "Demo Push");
//			pushTrigger.setOnMenuItemClickListener(new OnMenuItemClickListener()
//			{
//				public boolean onMenuItemClick(MenuItem clickedItem)
//				{
//					//UserInteraction/Event Processing....This triggers the PushListener to create
//					//a mock ticket. This ticket is then pushed to the device in real time
//					//This triggers/simulates the usecase where a new instance appearing 
//					//on the Cloud side is pushed down to the device in real time
//					CommandContext commandContext = new CommandContext();
//					commandContext.setTarget("/demo/push");
//					Services.getInstance().getCommandService().execute(commandContext);
//					return true;
//				}
//			});
//			
//			MenuItem plainPush = menu.add(Menu.NONE, Menu.NONE, 3, "Plain Push");
//			plainPush.setOnMenuItemClickListener(new OnMenuItemClickListener()
//			{
//				public boolean onMenuItemClick(MenuItem clickedItem)
//				{
//					CommandContext commandContext = new CommandContext();
//					commandContext.setTarget("/plain/push");
//					Services.getInstance().getCommandService().execute(commandContext);
//					return true;
//				}
//			});
//		}
//	}
//	
//	private void showTickets(Activity app)
//	{
//		//Read all the ticket instances synchronized with the Cloud
//		MobileBean[] tickets = MobileBean.readAll("crm_ticket_channel");
//		if(tickets != null && tickets.length > 0)
//		{
//			//Populate the List View
//			ListView view = (ListView)ViewHelper.findViewById(app, "list");
//			
//			//Prepare the data for the adapter. Data is read from the ticket bean instances
//			ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
//			for(MobileBean local:tickets)
//			{
//				HashMap<String, String> map = new HashMap<String, String>();
//				
//				String customer = local.getValue("customer");
//				String title = local.getValue("title");
//				
//				if(customer.length() > 25)
//				{
//					customer = customer.substring(0, 22)+"...";
//				}
//				
//				if(title.length() > 25)
//				{
//					title = title.substring(0, 22)+"...";
//				}
//				
//				map.put("customer", customer);
//				map.put("title", title);
//				mylist.add(map);
//			}
//			
//			SimpleAdapter ticketAdapter = new SimpleAdapter(app, mylist, ViewHelper.findLayoutId(app, "ticket_row"),
//		            new String[] {"customer", "title"}, new int[] {ViewHelper.findViewId(app, "customer"), ViewHelper.findViewId(app, "title")});
//		    view.setAdapter(ticketAdapter);
//		    
//		    //List Listener...used to respond to selecting a ticket instance
//			OnItemClickListener clickListener = new ClickListener(tickets);
//			view.setOnItemClickListener(clickListener);
//		}	
//	}
//	
//	private static class ClickListener implements OnItemClickListener
//	{
//		private MobileBean[] activeBeans;
//		
//		private ClickListener(MobileBean[] activeBeans)
//		{
//			this.activeBeans = activeBeans;
//		}
//		
//		public void onItemClick(AdapterView<?> parent, View view, int position,
//				long id)
//		{
//			final Activity currentActivity = Services.getInstance().getCurrentActivity();
//			
//			//Get the ticket bean selected by the user
//			int selectedIndex = position;
//			final MobileBean selectedBean = activeBeans[selectedIndex];
//			
//			//Show the details of the ticket in an AlertDialog with three possible actions
//			//'Update', 'Close' (does nothing), and 'Delete'
//			AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);
//			builder.setMessage(selectedBean.getValue("comment"))
//			       .setCancelable(false).setTitle("Specialist: "+selectedBean.getValue("specialist")).
//			       setPositiveButton("Update", new DialogInterface.OnClickListener() 
//			       {
//			           public void onClick(DialogInterface dialog, int id) 
//			           {
//			        	   //navigate to the Update screen
//			        	   NavigationContext navContext = NavigationContext.getInstance();
//			        	   navContext.setAttribute("/update/ticket","ticket", selectedBean);
//			        	   NavigationContext.getInstance().navigate("/update/ticket");
//			        	   
//			        	   dialog.dismiss();
//			           }
//			       })
//			       .setNegativeButton("Delete", new DialogInterface.OnClickListener() 
//			       {
//			           public void onClick(DialogInterface dialog, int id) 
//			           {
//			        	   dialog.dismiss();
//			        	   
//			        	   //Delete this ticket instance. This CRUD operation is then seamlessly
//			        	   //synchronized back with the Cloud
//			        	   CommandContext commandContext = new CommandContext();
//			        	   commandContext.setTarget("/delete/ticket");
//			        	   commandContext.setAttribute("ticket", selectedBean);
//			        	   Services.getInstance().getCommandService().execute(commandContext);
//			           }
//			       })
//			       .setNeutralButton("Close", new DialogInterface.OnClickListener() 
//			       {
//			           public void onClick(DialogInterface dialog, int id) 
//			           {
//			                dialog.dismiss();
//			           }
//			       });
//			
//			AlertDialog alert = builder.create();
//			alert.show();
//		}
//	}
//}
