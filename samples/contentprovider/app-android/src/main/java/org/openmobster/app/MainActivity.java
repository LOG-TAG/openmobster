/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.app;

import java.lang.reflect.Field;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.ContentResolver;
import android.database.Cursor;
import android.content.ContentValues;

import org.openmobster.core.mobileCloud.android.util.GeneralTools;
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
			
			//render the main screen
			String layoutClass = this.getPackageName()+".R$layout";
			String main = "main";
			Class clazz = Class.forName(layoutClass);
			Field field = clazz.getField(main);
			int screenId = field.getInt(clazz);
			this.setContentView(screenId);
			
			this.setUpButtons();
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
	}
	
	private void setUpButtons()
	{
		//Read All
		Button read = (Button)ViewHelper.findViewById(this, "readAll");
		read.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) 
			{	
				System.out.println("*********ReadAll********************");
				
				//Set up the Content Provider URI where request needs to be made
				//in this case it is setup to read all the objects
				Uri uri = Uri.parse(TutorialContentProvider.CONTENT_URI+"/objects");
				
				//Get the ContentResolver to make the request
				ContentResolver resolver = MainActivity.this.getContentResolver();
				
				//Make the invocation
				Cursor cursor = resolver.query(uri, null, null, null, null);
				if(cursor.getCount() == 0)
				{
					return;
				}
				
				//Read the cursor and its data
				int idIndex = cursor.getColumnIndex(TutorialContentProvider.ID);
				int nameIndex = cursor.getColumnIndex(TutorialContentProvider.NAME);
				int valueIndex = cursor.getColumnIndex(TutorialContentProvider.VALUE);
				
				cursor.moveToFirst();
				do
				{
					String id = cursor.getString(idIndex);
					String name = cursor.getString(nameIndex);
					String value = cursor.getString(valueIndex);
					
					System.out.println("---------------------------------");
					System.out.println("ID: "+id);
					System.out.println("Name: "+name);
					System.out.println("Value: "+value);
					
					cursor.moveToNext();
				}while(!cursor.isAfterLast());
			}
		});
		
		//Read By OId
		Button readByOid = (Button)ViewHelper.findViewById(this, "readByOid");
		readByOid.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) 
			{	
				System.out.println("*********Read By Oid********************");
				Uri uri = Uri.parse(TutorialContentProvider.CONTENT_URI+"/object/3");
				
				ContentResolver resolver = MainActivity.this.getContentResolver();
				
				Cursor cursor = resolver.query(uri, null, null, null, null);
				if(cursor.getCount() == 0)
				{
					return;
				}
				
				int idIndex = cursor.getColumnIndex(TutorialContentProvider.ID);
				int nameIndex = cursor.getColumnIndex(TutorialContentProvider.NAME);
				int valueIndex = cursor.getColumnIndex(TutorialContentProvider.VALUE);
				
				cursor.moveToFirst();
				do
				{
					String id = cursor.getString(idIndex);
					String name = cursor.getString(nameIndex);
					String value = cursor.getString(valueIndex);
					
					System.out.println("---------------------------------");
					System.out.println("ID: "+id);
					System.out.println("Name: "+name);
					System.out.println("Value: "+value);
					
					cursor.moveToNext();
				}while(!cursor.isAfterLast());
			}
		});
		
		//Insert an object
		Button insert = (Button)ViewHelper.findViewById(this, "insert");
		insert.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) 
			{	
				System.out.println("*********Insert********************");
				
				//Setup the Uri for the invocation
				Uri uri = Uri.parse(TutorialContentProvider.CONTENT_URI+"/object");
				
				//Get the Resolver
				ContentResolver resolver = MainActivity.this.getContentResolver();
				
				//Setup the data to be inserted
				ContentValues newContent = new ContentValues();
				String id = GeneralTools.generateUniqueId();
				newContent.put("name", "NAME://"+id);
				newContent.put("value","VALUE://"+id);
				
				//Make the invocation. A new uri will be sent back
				Uri newUri = resolver.insert(uri, newContent);
				
				System.out.println("NewUri: "+newUri);
			}
		});
		
		//Update an object
		Button update = (Button)ViewHelper.findViewById(this, "update");
		update.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) 
			{	
				System.out.println("*********Update********************");
				
				//Setup the Uri for the invocation
				Uri uri = Uri.parse(TutorialContentProvider.CONTENT_URI+"/object/3");
				
				//Get the Resolver
				ContentResolver resolver = MainActivity.this.getContentResolver();
				
				//Setup the data to be updated
				ContentValues updateContent = new ContentValues();
				String updateValue = GeneralTools.generateUniqueId();
				updateContent.put("name", "NAME://"+updateValue);
				updateContent.put("value","VALUE://"+updateValue);
				
				//Make the invocation. # of rows updated will be sent back
				int rows = resolver.update(uri, updateContent, null, null);
				
				System.out.println("Rows Updated: "+rows);
			}
		});
		
		//Delete an object
		Button delete = (Button)ViewHelper.findViewById(this, "delete");
		delete.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) 
			{	
				System.out.println("*********Delete********************");
				
				//Setup the Uri for the invocation
				Uri uri = Uri.parse(TutorialContentProvider.CONTENT_URI+"/object/3");
				
				//Get the Resolver
				ContentResolver resolver = MainActivity.this.getContentResolver();
				
				//Make the invocation. # of rows deleted will be sent back
				int rows = resolver.delete(uri, null, null);
				
				System.out.println("Rows Deleted: "+rows);
			}
		});
		
		//DeleteAll
		Button deleteAll = (Button)ViewHelper.findViewById(this, "deleteAll");
		deleteAll.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) 
			{	
				System.out.println("*********DeleteAll********************");
				
				//Setup the Uri for the invocation
				Uri uri = Uri.parse(TutorialContentProvider.CONTENT_URI+"/objects");
				
				//Get the Resolver
				ContentResolver resolver = MainActivity.this.getContentResolver();
				
				//Make the invocation. # of rows deleted will be sent back
				int rows = resolver.delete(uri, null, null);
				
				System.out.println("Rows Deleted: "+rows);
			}
		});
	}
}
