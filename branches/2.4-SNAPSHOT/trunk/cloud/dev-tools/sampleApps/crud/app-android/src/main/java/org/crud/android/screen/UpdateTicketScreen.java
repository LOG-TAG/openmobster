/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.crud.android.screen;

import java.util.Map;
import java.util.Vector;

import org.crud.android.command.AsyncLoadSpinners;
import org.crud.android.command.UpdateTicket;
import org.openmobster.android.api.sync.MobileBean;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Controls the 'Ticket Modification' screen.
 * 
 * The UI presents a simple form with pre-populated data, and an 'OK' to save the changes, and 'Cancel' button to cancel the
 * operation
 * 
 * @author openmobster@gmail.com
 */


public class UpdateTicketScreen extends Activity{
	
	MobileBean ticket=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_ticket);
		
		Intent intent= getIntent();
		int index=intent.getIntExtra("SelectedIndex",-1);
		
		if(index==-1){
			return;
		}
		ticket=HomeScreen.activeBeans[index];
		
		EditText title = (EditText)findViewById(R.id.title);
		title.setText(ticket.getValue("title"));
		
		EditText comments = (EditText)findViewById(R.id.comments);
		comments.setText(ticket.getValue("comment"));
		
		Handler handler = new Handler() {
			@Override
        	public void handleMessage(Message msg) {
				int what= msg.what;
        		if(what==1){
        			Map map=(Map) msg.obj;
        			Vector customers= (Vector) map.get("customers");
        			Vector specialists=(Vector) map.get("specialists");
        			
        			String selectedCustomer = "";
        			String selectedSpecialist = "";
        			if(ticket != null)
        			{
        				selectedCustomer = ticket.getValue("customer");
        				selectedSpecialist = ticket.getValue("specialist");
        			}
        			
        			//Load the specialist customers
        			Spinner customer = (Spinner)findViewById(R.id.customer);
        			ArrayAdapter<CharSequence> customerAdapter = new ArrayAdapter<CharSequence>(UpdateTicketScreen.this,android.R.layout.simple_spinner_item);
        			int selectedCustomerPosition = -1;
        			customerAdapter.add("--Select--");
        			int size = customers.size();
        			for(int i=0; i<size; i++)
        			{
        				String local = (String)customers.get(i);
        				customerAdapter.add(local);
        				if(local.equals(selectedCustomer))
        				{
        					selectedCustomerPosition = i;
        				}
        			}
        			customerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        			customer.setAdapter(customerAdapter);
        			customer.setSelection(selectedCustomerPosition+1, true);
        			
        			
        			//Load the specialist spinner        			
        			Spinner specialist = (Spinner)findViewById(R.id.specialist);
        			ArrayAdapter<CharSequence> specialistAdapter = new ArrayAdapter<CharSequence>(UpdateTicketScreen.this,android.R.layout.simple_spinner_item);
        			int selectedSpecialistPosition = -1;
        			specialistAdapter.add("--Select--");
        			size = specialists.size();
        			for(int i=0; i<size; i++)
        			{
        				String local = (String)specialists.get(i);
        				specialistAdapter.add(local);
        				if(local.equals(selectedSpecialist))
        				{
        					selectedSpecialistPosition = i;
        				}
        			}
        			specialistAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        			specialist.setAdapter(specialistAdapter);
        			specialist.setSelection(selectedSpecialistPosition+1, true);
        			
        			
        		}        					        				
			}
        };	
		new AsyncLoadSpinners(UpdateTicketScreen.this,handler).execute();
        
		Button save = (Button)findViewById(R.id.save);
		save.setOnClickListener(new OnClickListener(){
			public void onClick(View button)
			{
				UpdateTicketScreen.this.save(ticket);
			}
		});
		
		Button cancel = (Button)findViewById(R.id.cancel);
		cancel.setOnClickListener(new OnClickListener(){
			public void onClick(View button)
			{
				Toast.makeText(UpdateTicketScreen.this,"Ticket Update was cancelled!!",Toast.LENGTH_LONG).show();
				Intent intent=new Intent(UpdateTicketScreen.this,HomeScreen.class);
				startActivity(intent);
				finish();
			}
		});
		
	}
	private void save(MobileBean ticket)
	{
		
		//Update the state of this ticket with the newly modified data from the user
		EditText title = (EditText)findViewById(R.id.title);
		ticket.setValue("title", title.getText().toString());
		
		EditText comments = (EditText)findViewById(R.id.comments);
		ticket.setValue("comment", comments.getText().toString());
		
		Spinner customer = (Spinner)findViewById(R.id.customer);
		ticket.setValue("customer", ((TextView)customer.getSelectedView()).getText().toString());
		
		Spinner specialist = (Spinner)findViewById(R.id.specialist);
		ticket.setValue("specialist", ((TextView)specialist.getSelectedView()).getText().toString());
		
		Handler handler = new Handler() {
			@Override
        	public void handleMessage(Message msg) {
				int what= msg.what;
        		if(what==1){
        			Toast.makeText(UpdateTicketScreen.this,"Record successfully updated",1).show();
        			Intent intent=new Intent(UpdateTicketScreen.this,HomeScreen.class);
        			startActivity(intent);
        			finish();
        		}        					        				
			}
        };	
		new UpdateTicket(this, handler,ticket).execute();		
	}
}

/*
public class UpdateTicketScreen extends Screen
{
	private Integer screenId;
	
	@Override
	public void render()
	{
		try
		{
			//lays out the UI specified in res/layout/update_ticket.xml
			final Activity currentActivity = Services.getInstance().getCurrentActivity();
			
			String layoutClass = currentActivity.getPackageName()+".R$layout";
			Class clazz = Class.forName(layoutClass);
			Field field = clazz.getField("new_ticket");
			
			this.screenId = field.getInt(clazz);						
		}
		catch(Exception e)
		{
			SystemException se = new SystemException(this.getClass().getName(), "render", new Object[]{
				"Message:"+e.getMessage(),
				"Exception:"+e.toString()
			});
			ErrorHandler.getInstance().handle(se);
			throw se;
		}
	}
	
	@Override
	public Object getContentPane()
	{
		return this.screenId;
	}
	
	@Override
	public void postRender()
	{
		MobileBean ticket = null;
		NavigationContext navContext = NavigationContext.getInstance();
		try
		{
			final Activity currentActivity = Services.getInstance().getCurrentActivity();
			
			//Load the spinners asynchronously
			CommandContext commandContext = new CommandContext();
			commandContext.setTarget("/async/load/spinners");
			Services.getInstance().getCommandService().execute(commandContext);
			
			//Populate the screen with existing ticket instance state
			ticket = (MobileBean)navContext.getAttribute(this.getId(),"ticket");
			
			EditText title = (EditText)ViewHelper.findViewById(currentActivity, "title");
			title.setText(ticket.getValue("title"));
			
			EditText comments = (EditText)ViewHelper.findViewById(currentActivity, "comments");
			comments.setText(ticket.getValue("comment"));
			
			//Add Event Handlers
			Button save = (Button)ViewHelper.findViewById(currentActivity, "save");
			save.setOnClickListener(new OnClickListener(){
				public void onClick(View button)
				{
					UpdateTicketScreen.this.save();
				}
			});
			
			Button cancel = (Button)ViewHelper.findViewById(currentActivity, "cancel");
			cancel.setOnClickListener(new OnClickListener(){
				public void onClick(View button)
				{
					Toast.makeText(currentActivity, 
							"Ticket Update was cancelled!!", 
							Toast.LENGTH_LONG).show();
					NavigationContext.getInstance().back();
				}
			});
		}
		finally
		{
		}
	}
	//--------------------------------------------------------------------------------------------------------------------------------------
	private void save()
	{
		final Activity currentActivity = Services.getInstance().getCurrentActivity();
		
		//Get the ticket being modified
		MobileBean ticket = (MobileBean)NavigationContext.getInstance().getAttribute(this.getId(),"ticket");
		
		//Update the state of this ticket with the newly modified data from the user
		EditText title = (EditText)ViewHelper.findViewById(currentActivity, "title");
		ticket.setValue("title", title.getText().toString());
		
		EditText comments = (EditText)ViewHelper.findViewById(currentActivity, "comments");
		ticket.setValue("comment", comments.getText().toString());
		
		Spinner customer = (Spinner)ViewHelper.findViewById(currentActivity, "customer");
		ticket.setValue("customer", ((TextView)customer.getSelectedView()).getText().toString());
		
		Spinner specialist = (Spinner)ViewHelper.findViewById(currentActivity, "specialist");
		ticket.setValue("specialist", ((TextView)specialist.getSelectedView()).getText().toString());
		
		//Save the changes. Once the changes are saved, these are seamlessly synchronized back with the Cloud
		CommandContext commandContext = new CommandContext();
		commandContext.setTarget("/update/ticket");
		commandContext.setAttribute("ticket", ticket);
		Services.getInstance().getCommandService().execute(commandContext);
	}
}
*/