/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.showcase.app.crud;

import java.util.ArrayList;
import java.util.HashMap;
import org.openmobster.android.api.sync.MobileBean;
import org.openmobster.showcase.app.AppConstants;
import org.openmobster.showcase.app.system.MyBootstrapper;
import org.showcase.app.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

/**
 * Controls the 'home' screen that is displayed when the App is first launched.
 * 
 * It displays the Showcase options in a 'ListView' 
 * 
 * @author openmobster@gmail.com
 */

public class CRUDMainScreen extends Activity{
	
	public static MobileBean myActiveBean=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.crud);
		MyBootstrapper.getInstance().bootstrapUIContainerOnly(this);
		
		
	}
	
	@Override
	protected void onStart(){		
		super.onStart();
		if(MyBootstrapper.getInstance().isDeviceActivated()){
			showTickets();
		}else{
			Toast.makeText(this,"Record not found try again later.", 1).show();			
		}		
	}
	
	private void showTickets()
	{
		//Populate the List View
		ListView view = (ListView)findViewById(R.id.list);
				
		MobileBean[] beans = MobileBean.readAll(AppConstants.channel);
		
		ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
		for(MobileBean local:beans)
		{
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("empty", "");
			map.put("title", local.getValue("title"));
			mylist.add(map);
		}
		
		int rowId = R.layout.crud_row;
		String[] rows = new String[]{"empty","title"};
		int[] rowUI = new int[] {R.id.empty,R.id.title};
		SimpleAdapter showcaseAdapter = new SimpleAdapter(this, mylist, rowId, rows, rowUI);
	    view.setAdapter(showcaseAdapter);
	    
	    OnItemClickListener clickListener = new ClickListener(beans);
		view.setOnItemClickListener(clickListener);
	}
	
	private class ClickListener implements OnItemClickListener
	{
		private MobileBean[] activeBeans;
		
		private ClickListener(MobileBean[] activeBeans)
		{
			this.activeBeans = activeBeans;
		}
		
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id)
		{
			
			
			//Get the ticket bean selected by the user
			int selectedIndex = position;
			final MobileBean selectedBean = activeBeans[selectedIndex];
			
			String title = selectedBean.getValue("title");
			String comment = selectedBean.getValue("comment");
			
			//Show the details of the ticket in an AlertDialog with three possible actions
			//'Update', 'Close' (does nothing), and 'Delete'
			AlertDialog.Builder builder = new AlertDialog.Builder(CRUDMainScreen.this);
			builder.setMessage(comment)
			       .setCancelable(false).setTitle(title).
			       setPositiveButton("Update", new DialogInterface.OnClickListener() 
			       {
			           public void onClick(DialogInterface dialog, int id) 
			           {
			        	   
			        	   dialog.dismiss();
			        	   
			        	   myActiveBean=selectedBean;
			        	   
			        	   Intent intent=new Intent(CRUDMainScreen.this,SaveTicketScreen.class);
			        	   startActivity(intent);
			           }
			       })
			       .setNegativeButton("Delete", new DialogInterface.OnClickListener() 
			       {
			           public void onClick(DialogInterface dialog, int id) 
			           {
			        	   dialog.dismiss();
			        	
			        	   Handler handler=new Handler(){
			        		   @Override
			        		   public void handleMessage(Message msg){
			        			   int what=msg.what;
			        			   if(what==1){
			        				   Toast.makeText(CRUDMainScreen.this,"Delete success",1).show();
			        			   }		        			
			        		   }
			        	   };
			        	   new DeleteTicket(CRUDMainScreen.this,handler,selectedBean).execute();
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		menu.add("New Ticket");
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		
		String action=item.getTitle().toString();
		if(action.equalsIgnoreCase("New Ticket")){
			Intent intent=new Intent(CRUDMainScreen.this,SaveTicketScreen.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}
}