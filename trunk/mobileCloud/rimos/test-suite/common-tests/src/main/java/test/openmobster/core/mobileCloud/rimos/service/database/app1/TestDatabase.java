/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.core.mobileCloud.rimos.service.database.app1;

import java.util.Enumeration;

import test.openmobster.core.mobileCloud.rimos.testsuite.Test;

import org.openmobster.core.mobileCloud.rimos.storage.Database;
import org.openmobster.core.mobileCloud.rimos.storage.Record;

/**
 * @author openmobster@gmail.com
 *
 */
public class TestDatabase extends Test
{	
	public void runTest() 
	{
		try
		{
			this.testInit();
			
			//Test Table Management
			this.testTableManagement();
			
			//Test Select operation for the Configuration table
			this.testSelect(Database.config_table);
			
			//Test inserting a PushMail record
			this.testInsertPushMail("unique-1");
			
			//Test Select operations on the PushMail table
			this.testSelect("pushmail");
			
			//Test Update operations on PushMail table
			this.testUpdatePushMail("unique-1");
			
			//Test Select operations on the PushMail table
			this.testSelect("pushmail");
			
			//Delete unique-1
			this.testInsertPushMail("unique-2");
			this.testDelete("unique-1");
			this.testSelect("pushmail");
			
			//Delete All
			this.testDeleteAll();
			this.testSelect("pushmail");
		}
		catch(Exception e)
		{
			throw new RuntimeException(e.toString());
		}
	}
	
	private void testInit() throws Exception
	{
		Database database = Database.getInstance();
		System.out.println("Database Connected="+database.getDatabaseUid());
		this.assertTrue(database.isConnected(), this.getInfo()+"://DatabaseInitialization");
		this.assertTrue(database.getDatabaseUid() == (long)Database.class.getName().hashCode(), 
		this.getInfo()+"://DatabaseUidCheck");
	}	
		
	private void testTableManagement() throws Exception
	{
		Database database = Database.getInstance();
		//Check for the presence of the Configuration table
		Enumeration tables = database.enumerateTables();
		boolean configTableFound = false;
		boolean pushMailFound = false;
		System.out.println("-------------------------------------------");
		while(tables.hasMoreElements())
		{
			String found = (String)tables.nextElement();
			System.out.println("Found Table="+found);
			if(found.equals(Database.config_table))
			{
				configTableFound = true;
			}
			else if(found.equals("pushmail"))
			{
				pushMailFound = true;
			}
		}			
		assertTrue(configTableFound, this.getInfo()+"://ConfigurationTableFound");	
		
		//if pushmail found drop it
		if(pushMailFound)
		{
			database.dropTable("pushmail");
		}		    
	    
	    //Add push mail table
		pushMailFound = false;
	    database.createTable("pushmail");
	    
	    //Assert presence of both tables
	    tables = database.enumerateTables();
		configTableFound = false;
		pushMailFound = false;
		System.out.println("-------------------------------------------");
		while(tables.hasMoreElements())
		{
			String found = (String)tables.nextElement();
			System.out.println("Found Table="+found);
			if(found.equals(Database.config_table))
			{
				configTableFound = true;
			}
			else if(found.equals("pushmail"))
			{
				pushMailFound = true;
			}
		}			
		assertTrue(configTableFound, this.getInfo()+"://ConfigurationTableFound");
		assertTrue(pushMailFound, this.getInfo()+"://PushMailFound");
		
		boolean isConfigTableEmpty = database.isTableEmpty(Database.config_table);
		boolean isPushMailEmpty = database.isTableEmpty("pushmail");
		assertFalse(isConfigTableEmpty, this.getInfo()+"://ConfigurationTable/NotEmpty");
		assertTrue(isPushMailEmpty, this.getInfo()+"://PushMailFound/Empty");
	}
	
	private void testSelect(String tableName) throws Exception
	{
		Database database = Database.getInstance();
		long rowCount = 0;
		Enumeration records = database.selectAll(tableName);
		System.out.println("------------------------------------------");
		while(records.hasMoreElements())
		{
			Record record = (Record)records.nextElement();
			rowCount++;
			
			//Assert Record data
			System.out.println("RecordId="+record.getRecordId());				
		}
		
		long numOfRecords = database.selectCount(tableName);
		this.assertTrue(numOfRecords == rowCount, this.getInfo()+"://SelectRowCountSuccess/"+tableName);
	}
	
	private void testInsertPushMail(String recordId) throws Exception
	{
		Database database = Database.getInstance();
		Record record = new Record(recordId);
		record.setValue("from", "from@gmail.com");
		record.setValue("to", "to@gmail.com");
					
		database.insert("pushmail", record);
	}
	
	private void testUpdatePushMail(String recordId) throws Exception
	{
		Database database = Database.getInstance();
		Record record = database.select("pushmail", recordId);
		String from = record.getValue("from")+"/updated";
		String to = record.getValue("to")+"/updated";
		record.setValue("from", from);
		record.setValue("to", to);
		
		database.update("pushmail", record);
		
		record = database.select("pushmail", recordId);
		this.assertEquals(record.getValue("from"), from, this.getInfo()+"://PushMailUpdate/From");
		this.assertEquals(record.getValue("to"), to, this.getInfo()+"://PushMailUpdate/To");
	}
	
	private void testDelete(String recordId) throws Exception
	{
		Database database = Database.getInstance();
		Record record = database.select("pushmail", recordId);
		
		database.delete("pushmail", record);
		
		record = database.select("pushmail", recordId);			
		this.assertNull(record, this.getInfo()+"://PushMailDelete/"+recordId);	
		
		long recordCount = database.selectCount("pushmail");
		assertTrue(recordCount>0, this.getInfo()+"://PushMailDelete/PushMailCount"+recordId);
	}
	
	private void testDeleteAll() throws Exception
	{
		Database database = Database.getInstance();
		database.deleteAll("pushmail");
		
		boolean isPushMailEmpty = database.isTableEmpty("pushmail");
		assertTrue(isPushMailEmpty, this.getInfo()+"://DeleteAll/Empty");
	}
}
