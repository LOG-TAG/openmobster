/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.storage;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;

import org.openmobster.core.mobileCloud.android.util.GeneralTools;

/**
 * @author openmobster@gmail.com
 */
final class CloudDB
{
	private static CloudDB singleton;
	
	private SQLiteDatabase db;
	private CloudDBManager manager;
	
	private CloudDB(Context context)
	{
		List<String> tables = new ArrayList<String>();
		tables.add(Database.config_table); //exposed via provider
		tables.add(Database.sync_anchor);
		tables.add(Database.sync_changelog_table);
		tables.add(Database.sync_error);
		tables.add(Database.sync_recordmap);
		tables.add(Database.bus_registration); //exposed via provider
		tables.add(Database.provisioning_table); //exposed via provider
		tables.add(Database.system_errors);
		
		this.manager = new CloudDBManager(tables, context, "cloudb", 2);
	}
	
	static CloudDB getInstance(Context context)
	{
		if(CloudDB.singleton == null)
		{
			synchronized(CloudDB.class)
			{
				if(CloudDB.singleton == null)
				{
					CloudDB.singleton = new CloudDB(context);
				}
			}
		}
		return CloudDB.singleton;
	}
	
	SQLiteDatabase getDb()
	{
		if(!this.isConnected())
		{
			throw new IllegalStateException("CloudDB is closed!!");
		}
		return this.db;
	}
	//--------------------------------------------------------------------------------------------------------------------
	void connect()
	{
		if(this.db == null || !this.db.isOpen())
		{
			this.db = this.manager.getWritableDatabase();
		}
	}
	
	boolean isConnected()
	{
		return (this.db != null && this.db.isOpen());
	}
	
	void disconnect()
	{
		if(this.isConnected())
		{
			this.db.close();
			this.db = null;
		}
	}
	//-----------------------------------------------------------------------------------------------------------------------
	Set<String> listTables() throws DBException
	{
		Cursor cursor = null;
		try
		{
			Set<String> tables = null;
			
			cursor = this.db.rawQuery("SELECT name FROM sqlite_master WHERE type=?", new String[]{"table"});
			
			if(cursor.getCount() > 0)
			{
				tables = new HashSet<String>();
				int nameIndex = cursor.getColumnIndex("name");
				cursor.moveToFirst();
				do
				{
					String name = cursor.getString(nameIndex);
					tables.add(name);
					cursor.moveToNext();
				}while(!cursor.isAfterLast());
			}
			
			return tables;
		}
		finally
		{
			if(cursor != null)
			{
				cursor.close();
			}
		}
	}
	
	void dropTable(String table) throws DBException
	{
		try
		{
			this.db.beginTransaction();
			
			String tableSql = "DROP TABLE IF EXISTS " + table + ";";
			this.db.execSQL(tableSql);
			
			this.db.setTransactionSuccessful();
		}
		finally
		{
			this.db.endTransaction();
		}
	}
	
	void createTable(String table) throws DBException
	{
		try
		{
			this.db.beginTransaction();
			
			String tableSql = "CREATE TABLE IF NOT EXISTS " + table + " ("
			+ "id INTEGER PRIMARY KEY," + "recordid TEXT," + "name TEXT," + "value TEXT"
			+ ");";
			this.db.execSQL(tableSql);
			
			this.db.setTransactionSuccessful();
		}
		finally
		{
			this.db.endTransaction();
		}
	}
	
	boolean doesTableExist(String table) throws DBException
	{
		Cursor cursor = null;
		try
		{
			cursor = this.db.rawQuery("SELECT name FROM sqlite_master WHERE type=? AND name=?", 
			new String[]{"table", table});
			
			if(cursor.getCount()>0)
			{
				return true;
			}
			
			return false;
		}
		finally
		{
			if(cursor != null)
			{
				cursor.close();
			}
		}
	}
	
	boolean isTableEmpty(String table) throws DBException
	{
		Cursor cursor = null;
		try
		{
			cursor = this.db.rawQuery("SELECT count(*) FROM "+table, null);
			
			int countIndex = cursor.getColumnIndex("count(*)");
			cursor.moveToFirst();
			int rowCount = cursor.getInt(countIndex);
			if(rowCount > 0)
			{
				return false;
			}
			
			return true;
		}
		finally
		{
			if(cursor != null)
			{
				cursor.close();
			}
		}
	}
	//--------------------------------------------------------------------------------------------------------------------
	String insert(String table, Record record) throws DBException
	{
		try
		{
			this.db.beginTransaction();
			
			//SetUp the RecordId
			String recordId = record.getRecordId();
			if(recordId == null || recordId.trim().length() == 0)
			{
				recordId = GeneralTools.generateUniqueId();
				record.setRecordId(recordId);
			}
			
			//SetUp the DirtyStatus
			record.setDirtyStatus(GeneralTools.generateUniqueId());
			
			//Delete a record if one exists by this id...cleanup
			this.delete(table, record);
			
			Set<String> names = record.getNames();
			for(String name: names)
			{
				String value = record.getValue(name);
				
				//insert this row
				String insert = "INSERT INTO "+table+" (recordid,name,value) VALUES (?,?,?);";
				this.db.execSQL(insert,new Object[]{recordId,name,value});
			}
			
			this.db.setTransactionSuccessful();
			
			return recordId;
		}
		finally
		{
			this.db.endTransaction();
		}
	}
	
	Set<Record> selectAll(String from) throws DBException
	{
		Cursor cursor = null;
		try
		{
			Set<Record> all = null;
			
			Map<String, Record> localCache = new HashMap<String, Record>();
			cursor = this.db.rawQuery("SELECT * FROM "+from, null);
			
			if(cursor.getCount() > 0)
			{
				all = new HashSet<Record>();
				int recordidIndex = cursor.getColumnIndex("recordid");
				int nameIndex = cursor.getColumnIndex("name");
				int valueIndex = cursor.getColumnIndex("value");
				cursor.moveToFirst();
				do
				{
					String name = cursor.getString(nameIndex);
					String value = cursor.getString(valueIndex);
					String recordid = cursor.getString(recordidIndex);
					
					Record record = localCache.get(recordid);
					if(record == null)
					{
						record = new Record();
						localCache.put(recordid, record);
						all.add(record);
					}
					
					record.setRecordId(recordid);
					record.setValue(name, value);
					
					cursor.moveToNext();
				}while(!cursor.isAfterLast());
			}
			
			return all;
		}
		finally
		{
			if(cursor != null)
			{
				cursor.close();
			}
		}
	}
	
	long selectCount(String from) throws DBException
	{
		Set<Record> all = this.selectAll(from);
		if(all != null)
		{
			return all.size();
		}
		
		return 0;
	}
	
	Record select(String from, String recordId) throws DBException
	{
		Cursor cursor = null;
		try
		{
			Record record = null;
			cursor = this.db.rawQuery("SELECT * FROM "+from+" WHERE recordid=?", new String[]{recordId});
			
			if(cursor.getCount() > 0)
			{
				record = new Record();
				int recordidIndex = cursor.getColumnIndex("recordid");
				int nameIndex = cursor.getColumnIndex("name");
				int valueIndex = cursor.getColumnIndex("value");
				cursor.moveToFirst();
				do
				{
					String name = cursor.getString(nameIndex);
					String value = cursor.getString(valueIndex);
					String recordid = cursor.getString(recordidIndex);
					
					record.setRecordId(recordid);
					record.setValue(name, value);
					
					cursor.moveToNext();
				}while(!cursor.isAfterLast());
			}
			
			return record;
		}
		finally
		{
			if(cursor != null)
			{
				cursor.close();
			}
		}
	}
	
	void update(String table, Record record) throws DBException
	{
		try
		{
			this.db.beginTransaction();
			
			Set<String> names = record.getNames();
			String recordId = record.getRecordId();
			record.setDirtyStatus(GeneralTools.generateUniqueId());
			
			
			/*for(String name: names)
			{
				String value = record.getValue(name);
				
				//update this row
				String update = "UPDATE "+table+" SET recordid='"+recordId+"',name='"+name+"',value='"+value+"';";
				this.db.execSQL(update);
			}*/
			//Delete a record if one exists by this id...cleanup
			this.delete(table, record);
			for(String name: names)
			{
				String value = record.getValue(name);
								
				//insert this row
				//insert this row
				String insert = "INSERT INTO "+table+" (recordid,name,value) VALUES (?,?,?);";
				this.db.execSQL(insert,new Object[]{recordId,name,value});
			}
			
			this.db.setTransactionSuccessful();
		}
		finally
		{
			this.db.endTransaction();
		}
	}
	
	void delete(String table, Record record) throws DBException
	{
		try
		{
			this.db.beginTransaction();
			
			//delete this record
			String delete = "DELETE FROM "+table+" WHERE recordid='"+record.getRecordId()+"'";
			this.db.execSQL(delete);
			
			this.db.setTransactionSuccessful();
		}
		finally
		{
			this.db.endTransaction();
		}
	}
	
	void deleteAll(String table) throws DBException
	{
		try
		{
			this.db.beginTransaction();
			
			//delete this record
			String delete = "DELETE FROM "+table;
			this.db.execSQL(delete);
			
			this.db.setTransactionSuccessful();
		}
		finally
		{
			this.db.endTransaction();
		}
	}
}
