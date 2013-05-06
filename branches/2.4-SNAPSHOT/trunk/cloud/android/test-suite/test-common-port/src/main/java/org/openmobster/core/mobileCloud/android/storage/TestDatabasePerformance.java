/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.storage;

import android.content.Context;
import android.database.Cursor;

import org.openmobster.core.mobileCloud.android.module.mobileObject.MobileObject;
import org.openmobster.core.mobileCloud.android.module.mobileObject.MobileObjectDatabase;
import org.openmobster.core.mobileCloud.android.testsuite.Test;
import org.openmobster.core.mobileCloud.android.util.GenericAttributeManager;

/**
 * @author openmobster@gmail.com
 */
public class TestDatabasePerformance extends Test 
{
	
	@Override
	public void setUp()
	{
		try
		{
			super.setUp();
			
			Context context = (Context)this.getTestSuite().getContext().
			getAttribute("android:context");
			
			Database db = Database.getInstance(context);
			db.disconnect();
			db.connect();
			
			//some initial state setup
			db.createTable(Database.provisioning_table);
			db.dropTable("emailChannel");
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void runTest()
	{
		try
		{
			Context context = (Context)this.getTestSuite().getContext().
			getAttribute("android:context");
			
			//this.testSearchExactMatchAND(context);
			//this.testSearchExactMatchOR(context);
			//this.testStoreLargeJson(context);
			this.testMultipleJson(context);
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			throw new RuntimeException(e);
		}
	}
	
	private void testSearchExactMatchAND(Context context) throws DBException
	{
		Database db = Database.getInstance(context);
		db.dropTable("emailChannel");
		db.createTable("emailChannel");
		
		String from = "from@gmail.com";
		String to = "to@gmail.com";
		StringBuilder builder = new StringBuilder();
		
		/*for(int i=0;i<1024;i++)
		{
			for(int j=0;j<2100;j++)
			{
				builder.append("a");
			}
		}*/
		
		String message = builder.toString();
		
		MobileObject mobileObject = new MobileObject();
		mobileObject.setStorageId("emailChannel");
		mobileObject.setValue("from", from);
		mobileObject.setValue("to", to);
		mobileObject.setValue("message", message);
		mobileObject.setCreatedOnDevice(false);
		mobileObject.setLocked(false);
		mobileObject.setProxy(false);
		MobileObjectDatabase.getInstance().create(mobileObject);
		
		for(int i=0; i<5; i++)
		{
			from = "from("+i+")@blah.com";
			to = "to("+i+")@blah.com";
			
			mobileObject = new MobileObject();
			mobileObject.setStorageId("emailChannel");
			mobileObject.setValue("from", from);
			mobileObject.setValue("to", to);
			mobileObject.setValue("message", message);
			mobileObject.setCreatedOnDevice(false);
			mobileObject.setLocked(false);
			mobileObject.setProxy(false);
			MobileObjectDatabase.getInstance().create(mobileObject);
		}
		
		GenericAttributeManager criteria = new GenericAttributeManager();
		criteria.setAttribute("to", "to@gmail.com");
		criteria.setAttribute("from", "from@gmail.com");
		Cursor cursor = db.searchExactMatchAND("emailChannel", criteria);
		cursor.moveToFirst();
		do
		{
			String value = cursor.getString(0);
			System.out.println("Value: "+value);
			
			Record record = db.select("emailChannel", value);
			System.out.println("Record: "+record);
			
			cursor.moveToNext();
		}while(!cursor.isAfterLast());
		
		this.assertTrue(cursor.getCount()==1, this.getInfo()+"/testSearchExactMatchAND/MustFindOneRow");
	}
	
	private void testSearchExactMatchOR(Context context) throws DBException
	{
		Database db = Database.getInstance(context);
		db.dropTable("emailChannel");
		db.createTable("emailChannel");
		
		String from = "from@gmail.com";
		String to = "to@gmail.com";
		StringBuilder builder = new StringBuilder();
		
		/*for(int i=0;i<1024;i++)
		{
			for(int j=0;j<2100;j++)
			{
				builder.append("a");
			}
		}*/
		
		String message = builder.toString();
		
		MobileObject mobileObject = new MobileObject();
		mobileObject.setStorageId("emailChannel");
		mobileObject.setValue("from", from);
		mobileObject.setValue("to", to);
		mobileObject.setValue("message", message);
		mobileObject.setCreatedOnDevice(false);
		mobileObject.setLocked(false);
		mobileObject.setProxy(false);
		MobileObjectDatabase.getInstance().create(mobileObject);
		
		for(int i=0; i<5; i++)
		{
			from = "from("+i+")@blah.com";
			to = "to("+i+")@blah.com";
			
			mobileObject = new MobileObject();
			mobileObject.setStorageId("emailChannel");
			mobileObject.setValue("from", from);
			mobileObject.setValue("to", to);
			mobileObject.setValue("message", message);
			mobileObject.setCreatedOnDevice(false);
			mobileObject.setLocked(false);
			mobileObject.setProxy(false);
			MobileObjectDatabase.getInstance().create(mobileObject);
		}
		
		GenericAttributeManager criteria = new GenericAttributeManager();
		criteria.setAttribute("to", "blahblah");
		criteria.setAttribute("from", "from@gmail.com");
		Cursor cursor = db.searchExactMatchOR("emailChannel", criteria);
		cursor.moveToFirst();
		do
		{
			String value = cursor.getString(0);
			System.out.println("Value: "+value);
			
			Record record = db.select("emailChannel", value);
			System.out.println("Record: "+record);
			
			cursor.moveToNext();
		}while(!cursor.isAfterLast());
		
		this.assertTrue(cursor.getCount()==1, this.getInfo()+"/testSearchExactMatchOR/MustFindOneRow");
	}
	
	private void testStoreLargeJson(Context context) throws DBException
	{
		Database db = Database.getInstance(context);
		db.dropTable("emailChannel");
		db.createTable("emailChannel");
		
		String from = "from@gmail.com";
		String to = "to@gmail.com";
		StringBuilder builder = new StringBuilder();
		
		StringBuilder packetBuilder = new StringBuilder();
		for(int i=0; i<1000; i++)
		{
			packetBuilder.append("a");
		}
		String packet = packetBuilder.toString();
		for(int i=0;i<1900;i++)
		{
			builder.append(packet);
		}
		
		String message = builder.toString();
		
		MobileObject mobileObject = new MobileObject();
		mobileObject.setStorageId("emailChannel");
		mobileObject.setValue("from", from);
		mobileObject.setValue("to", to);
		mobileObject.setValue("message", message);
		mobileObject.setCreatedOnDevice(false);
		mobileObject.setLocked(false);
		mobileObject.setProxy(false);
		String recordId = MobileObjectDatabase.getInstance().create(mobileObject);
		
		Record largeRecord = db.select("emailChannel", recordId);
		this.assertNotNull(largeRecord, this.getInfo()+"/testStoreLargeJson/MustNotBeNull");
		
		mobileObject = new MobileObject(largeRecord);
		String largeMessage = mobileObject.getValue("message");
		
		this.assertEquals(""+largeMessage.length(), "1900000", this.getInfo()+"/testStoreLargeJson/MessageLengthDoesNotMatch");
	}
	
	private void testMultipleJson(Context context) throws DBException
	{
		Database db = Database.getInstance(context);
		db.dropTable("emailChannel");
		db.createTable("emailChannel");
		
		String from = "from@gmail.com";
		String to = "to@gmail.com";
		StringBuilder builder = new StringBuilder();
		
		StringBuilder packetBuilder = new StringBuilder();
		for(int i=0; i<1000; i++)
		{
			packetBuilder.append("a");
		}
		String packet = packetBuilder.toString();
		for(int i=0;i<100;i++)
		{
			builder.append(packet);
		}
		
		String message = builder.toString();
		
		for(int i=0; i<1000; i++)
		{
			System.out.println("Testing Multiple JSON # "+i);
			MobileObject mobileObject = new MobileObject();
			mobileObject.setStorageId("emailChannel");
			mobileObject.setValue("from", from);
			mobileObject.setValue("to", to);
			mobileObject.setValue("message", message);
			mobileObject.setCreatedOnDevice(false);
			mobileObject.setLocked(false);
			mobileObject.setProxy(false);
			String recordId = MobileObjectDatabase.getInstance().create(mobileObject);
			
			Record largeRecord = db.select("emailChannel", recordId);
			this.assertNotNull(largeRecord, this.getInfo()+"/testMultipleJson/MustNotBeNull");
			
			mobileObject = new MobileObject(largeRecord);
			String largeMessage = mobileObject.getValue("message");
			
			this.assertEquals(""+largeMessage.length(), "100000", this.getInfo()+"/testMultipleJson/MessageLengthDoesNotMatch");
			
			try{Thread.sleep(1000);}catch(Exception e){};
		}
	}
}
