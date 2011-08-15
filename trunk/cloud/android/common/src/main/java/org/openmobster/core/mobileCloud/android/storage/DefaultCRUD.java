/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.android.storage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openmobster.core.mobileCloud.android.util.GeneralTools;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 *
 * @author openmobster@gmail.com
 */
public class DefaultCRUD implements CRUDProvider
{
	private SQLiteDatabase db;
	
	public void init(SQLiteDatabase db)
	{
		this.db = db;
	}
	
	public void cleanup()
	{
		this.db = null;
	}
	
	public String insert(String table, Record record) throws DBException
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
	
	public Set<Record> selectAll(String from) throws DBException
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
	
	public long selectCount(String from) throws DBException
	{
		Set<Record> all = this.selectAll(from);
		if(all != null)
		{
			return all.size();
		}
		
		return 0;
	}
	
	public Record select(String from, String recordId) throws DBException
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
	
	public Set<Record> select(String from, String name, String value) throws DBException
	{
		Cursor cursor = null;
		try
		{
			Set<Record> records = null;
			
			cursor = this.db.rawQuery("SELECT * FROM "+from+" WHERE name=? AND value=?", new String[]{name,value});
			
			if(cursor.getCount() > 0)
			{
				records = new HashSet<Record>();
				
				int recordidIndex = cursor.getColumnIndex("recordid");
				cursor.moveToFirst();
				do
				{
					String recordid = cursor.getString(recordidIndex);
					
					Record record = this.select(from, recordid);
				
					records.add(record);
					
					cursor.moveToNext();
				}while(!cursor.isAfterLast());
			}
			
			return records;
		}
		finally
		{
			if(cursor != null)
			{
				cursor.close();
			}
		}
	}
	
	public Set<Record> selectByValue(String from, String value) throws DBException
	{
		Cursor cursor = null;
		try
		{
			Set<Record> records = null;
			
			cursor = this.db.rawQuery("SELECT * FROM "+from+" WHERE value=?", new String[]{value});
			
			if(cursor.getCount() > 0)
			{
				records = new HashSet<Record>();
				
				int recordidIndex = cursor.getColumnIndex("recordid");
				cursor.moveToFirst();
				do
				{
					String recordid = cursor.getString(recordidIndex);
					
					Record record = this.select(from, recordid);
				
					records.add(record);
					
					cursor.moveToNext();
				}while(!cursor.isAfterLast());
			}
			
			return records;
		}
		finally
		{
			if(cursor != null)
			{
				cursor.close();
			}
		}
	}
	
	public Set<Record> selectByNotEquals(String from, String value) throws DBException
	{
		Cursor cursor = null;
		try
		{
			Set<Record> records = null;
			
			cursor = this.db.rawQuery("SELECT * FROM "+from+" WHERE value != ?", new String[]{value});
			
			if(cursor.getCount() > 0)
			{
				records = new HashSet<Record>();
				
				int recordidIndex = cursor.getColumnIndex("recordid");
				cursor.moveToFirst();
				do
				{
					String recordid = cursor.getString(recordidIndex);
					
					Record record = this.select(from, recordid);
				
					records.add(record);
					
					cursor.moveToNext();
				}while(!cursor.isAfterLast());
			}
			
			return records;
		}
		finally
		{
			if(cursor != null)
			{
				cursor.close();
			}
		}
	}
	
	public Set<Record> selectByContains(String from, String value) throws DBException
	{
		Cursor cursor = null;
		try
		{
			Set<Record> records = null;
			
			cursor = this.db.rawQuery("SELECT * FROM "+from+" WHERE value LIKE ?", new String[]{"%"+value+"%"});
			
			if(cursor.getCount() > 0)
			{
				records = new HashSet<Record>();
				
				int recordidIndex = cursor.getColumnIndex("recordid");
				cursor.moveToFirst();
				do
				{
					String recordid = cursor.getString(recordidIndex);
					
					Record record = this.select(from, recordid);
				
					records.add(record);
					
					cursor.moveToNext();
				}while(!cursor.isAfterLast());
			}
			
			return records;
		}
		finally
		{
			if(cursor != null)
			{
				cursor.close();
			}
		}
	}
	
	public void update(String table, Record record) throws DBException
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
	
	public void delete(String table, Record record) throws DBException
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
	
	public void deleteAll(String table) throws DBException
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
