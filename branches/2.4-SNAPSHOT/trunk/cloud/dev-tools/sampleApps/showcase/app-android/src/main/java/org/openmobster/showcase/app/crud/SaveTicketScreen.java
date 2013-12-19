/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.showcase.app.crud;

import org.openmobster.android.api.sync.MobileBean;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.NavigationContext;
import org.openmobster.showcase.app.AppConstants;
import org.showcase.app.R;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Controls the 'Save Ticket' screen. 
 * 
 * The UI presents a simple Form for inputing ticket details, and then you can 'OK' or 'Cancel' the changes
 * 
 * 
 * @author openmobster@gmail.com
 */


public class SaveTicketScreen extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState){		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.save_ticket);
		
		try
		{
			
			//Populate the screen with existing ticket instance state
			final MobileBean activeBean = CRUDMainScreen.myActiveBean;
			
			//If update, then populate the fields with selected ticket information
			if(activeBean != null)
			{
				EditText title = (EditText)findViewById(R.id.title);
				title.setText(activeBean.getValue("title"));
				
				EditText comments = (EditText)findViewById(R.id.comments);
				comments.setText(activeBean.getValue("comment"));
			}
			
			//Add Event Handlers
			Button save = (Button)findViewById(R.id.save);
			save.setOnClickListener(new OnClickListener(){
				public void onClick(View button)
				{
					SaveTicketScreen.this.save(activeBean);
				}
			});
			
			Button cancel = (Button)findViewById(R.id.cancel);
			cancel.setOnClickListener(new OnClickListener(){
				public void onClick(View button)
				{
					Toast.makeText(SaveTicketScreen.this, 
							"Save was cancelled!!", 
							Toast.LENGTH_LONG).show();
					NavigationContext.getInstance().back();
				}
			});
		}
		finally
		{
			CRUDMainScreen.myActiveBean=null;
		}
		
	}
	
	@Override
	protected void onStart(){

		super.onStart();
	}
	
	private void save(MobileBean activeBean)
	{
		if(activeBean != null)
		{
			//update
			this.update(activeBean);
		}
		else
		{
			//add
			this.add();
		}
	}
	
	private void add()
	{
		
		
		//Creates a new ticket instance on the device. Once 'saved', it will be seamlessly synchronized with the Cloud
		MobileBean activeBean = MobileBean.newInstance(AppConstants.channel);
		
		EditText title = (EditText)findViewById(R.id.title);
		activeBean.setValue("title", title.getText().toString());
		
		EditText comments = (EditText)findViewById(R.id.comments);
		activeBean.setValue("comment", comments.getText().toString());
		
		//execute the create ticket usecase. It creates a new ticket in the on-device db and
		//its synchronized automagically with the Cloud
		
		Handler handler=new Handler(){
			@Override
			public void handleMessage(Message msg){
				int what=msg.what;
				if(what==1){
					Toast.makeText(SaveTicketScreen.this,"Record successfully saved",1).show();
				}
			}
		};
		
		new SaveTicket(SaveTicketScreen.this,handler,activeBean).execute();
		
	}
	
	private void update(MobileBean activeBean)
	{
		
		
		EditText title = (EditText)findViewById(R.id.title);
		activeBean.setValue("title", title.getText().toString());
		
		EditText comments = (EditText)findViewById(R.id.comments);
		activeBean.setValue("comment", comments.getText().toString());
		
		
		//execute the create ticket usecase. It creates a new ticket in the on-device db and
		//its synchronized automagically with the Cloud
		
		
		Handler handler=new Handler(){
			@Override
			public void handleMessage(Message msg){
				int what=msg.what;
				if(what==1){
					Toast.makeText(SaveTicketScreen.this,"Record successfully saved",1).show();
				}
			}
		};
		
		new SaveTicket(SaveTicketScreen.this,handler,activeBean).execute();
	}
}