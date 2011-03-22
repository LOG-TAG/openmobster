/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.app;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;

/**
 * @author openmobster@gmail.com
 * 
 */
public class MainActivity extends Activity
{
	public MainActivity()
	{
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		//Initialize the initial mock up data in the sqlite database
		TicketDAO.initialize(this);
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
		
			//Show the main screen with tickets displayed in a list
			this.loadTickets();
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		MenuItem newTicket = menu.add(Menu.NONE, Menu.NONE, 0, "New Ticket");
		newTicket.setOnMenuItemClickListener(new OnMenuItemClickListener(){
			public boolean onMenuItemClick(MenuItem clickedItem)
			{
				MainActivity.this.loadNewTicket();
				return true;
			}
		});
		
		return true;
	}

	private void loadTickets()
	{
		try
		{
			//render the main screen
			String layoutClass = this.getPackageName()+".R$layout";
			String main = "main";
			Class clazz = Class.forName(layoutClass);
			Field field = clazz.getField(main);
			int screenId = field.getInt(clazz);
			this.setContentView(screenId);
			
			ListView list = (ListView)ViewHelper.findViewById(this, "list");
			
			ArrayList<HashMap<String,String>> listData = new ArrayList<HashMap<String,String>>();
			
			//Read the tickets for display
			List<Ticket> tickets = TicketDAO.readAll();
			int size = tickets.size();
			for(int i=0; i<size; i++)
			{
				Ticket ticket = tickets.get(i);
				HashMap<String, String> local = new HashMap<String,String>();
				local.put("customer", ticket.getCustomer());
				local.put("title", ticket.getTitle());
				listData.add(local);
			}
			
			//SimpleAdapter
			int row = ViewHelper.findLayoutId(this, "ticket_row");
			int[] cells = new int[]{ViewHelper.findViewId(this, "customer"),ViewHelper.findViewId(this, "title")};
			String[] header = new String[]{"customer","title"};
			
			SimpleAdapter listAdapter = new SimpleAdapter(this,listData,row,header,cells);
			list.setAdapter(listAdapter);
			OnItemClickListener showTicket = new ShowTicket(tickets);
			list.setOnItemClickListener(showTicket);
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
	}
	
	private void loadNewTicket()
	{
		try
		{
			//render the main screen
			String layoutClass = this.getPackageName()+".R$layout";
			String new_ticket = "new_ticket";
			Class clazz = Class.forName(layoutClass);
			Field field = clazz.getField(new_ticket);
			int screenId = field.getInt(clazz);
			this.setContentView(screenId);
			
			//Fill in the customer spinner
			List customers = new ArrayList();
			customers.add("Google");
			customers.add("Microsoft");
			customers.add("Apple");
			Spinner customerSpinner = (Spinner)ViewHelper.findViewById(this, "customer");
			ArrayAdapter<CharSequence> customerAdapter = new ArrayAdapter<CharSequence>(this,android.R.layout.simple_spinner_item);
			int selectedCustomerPosition = -1;
			customerAdapter.add("--Select--");
			int size = customers.size();
			for(int i=0; i<size; i++)
			{
				String local = (String)customers.get(i);
				customerAdapter.add(local);
			}
			customerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			customerSpinner.setAdapter(customerAdapter);
			customerSpinner.setSelection(selectedCustomerPosition+1, true);
			
			//Fill in the specialist spinner
			List specialists = new ArrayList();
			specialists.add("Larry Page");
			specialists.add("Steve Ballmer");
			specialists.add("Steve Jobs");
			Spinner specialistSpinner = (Spinner)ViewHelper.findViewById(this, "specialist");
			ArrayAdapter<CharSequence> specialistAdapter = new ArrayAdapter<CharSequence>(this,android.R.layout.simple_spinner_item);
			int selectedSpecialistPosition = -1;
			specialistAdapter.add("--Select--");
			size = specialists.size();
			for(int i=0; i<size; i++)
			{
				String local = (String)specialists.get(i);
				specialistAdapter.add(local);
			}
			specialistAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			specialistSpinner.setAdapter(specialistAdapter);
			specialistSpinner.setSelection(selectedSpecialistPosition+1, true);
			
			//Save Button
			Button save = (Button)ViewHelper.findViewById(this, "save");
			save.setOnClickListener(new AddNewTicket());
			
			//Cancel Button
			Button cancel = (Button)ViewHelper.findViewById(this, "cancel");
			cancel.setOnClickListener(new OnClickListener(){
				public void onClick(View button)
				{
					Toast.makeText(MainActivity.this, 
							"Ticket Creation was cancelled!!", 
							Toast.LENGTH_LONG).show();
					MainActivity.this.loadTickets();
				}
			});
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
	}
	
	private class ShowTicket implements OnItemClickListener
	{
		private List<Ticket> tickets;
		
		private ShowTicket(List<Ticket> tickets)
		{
			this.tickets = tickets;
		}
		
		@Override
		public void onItemClick(AdapterView<?> adapter, View view, int position,long id) 
		{	
			final Ticket ticket = tickets.get(position);
			
			//Create an Alert Dialog box
			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
			String comments = ticket.getComments();
			String specialist = ticket.getSpecialist();
			builder = builder.setMessage(comments);
			builder = builder.setCancelable(true);
			builder = builder.setTitle(specialist);
			
			builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() 
		       {
		           public void onClick(DialogInterface dialog, int id) 
		           {   
		        	   dialog.dismiss();
		        	   
		        	   //Delete the ticket
		        	   TicketDAO.delete(ticket);
		        	   
		        	   //Reload
		        	   MainActivity.this.loadTickets();
		           }
		       })
		       .setNeutralButton("Close", new DialogInterface.OnClickListener() 
		       {
		           public void onClick(DialogInterface dialog, int id) 
		           {
		                dialog.dismiss();
		           }
		       });
			
			AlertDialog alert = builder.create();
			alert.show();
		}	
	}
	
	private class AddNewTicket implements OnClickListener
	{
		@Override
		public void onClick(View view) 
		{
			Ticket newTicket = new Ticket();
			
			//Get the Title
			EditText title = (EditText)ViewHelper.findViewById(MainActivity.this, "title");
			newTicket.setTitle(title.getText().toString());
			
			//Get the Comments
			EditText comments = (EditText)ViewHelper.findViewById(MainActivity.this, "comments");
			newTicket.setComments(comments.getText().toString());
			
			//Get the Customer
			Spinner customer = (Spinner)ViewHelper.findViewById(MainActivity.this, "customer");
			String customerValue = ((TextView)customer.getSelectedView()).getText().toString();
			newTicket.setCustomer(customerValue);
			
			//Get the Specialist
			Spinner specialist = (Spinner)ViewHelper.findViewById(MainActivity.this, "specialist");
			String specialistValue = ((TextView)specialist.getSelectedView()).getText().toString();
			newTicket.setSpecialist(specialistValue);
			
			//Persist the new ticket instance
			TicketDAO.insert(newTicket);
			
			//Go back to the main screen
			MainActivity.this.loadTickets();
		}
	}
}
