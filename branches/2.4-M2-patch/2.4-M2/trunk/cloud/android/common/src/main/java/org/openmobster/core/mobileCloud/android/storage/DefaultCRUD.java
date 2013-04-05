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
import java.util.Iterator;

import org.openmobster.core.mobileCloud.android.util.GeneralTools;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import org.json.JSONObject;

/**
 *
 * @author openmobster@gmail.com
 */
public class DefaultCRUD implements CRUDProvider
{
	private SQLiteDatabase db;
	private Cache cache;
	
	public void init(SQLiteDatabase db)
	{
		this.db = db;
		this.cache = new Cache();
		this.cache.start();
	}
	
	public void cleanup()
	{
		this.db = null;
		
		this.cache.stop();
		this.cache = null;
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
			Map<String,String> nameValuePairs = new HashMap<String,String>();
			for(String name: names)
			{
				String value = record.getValue(name);
				
				//check if this is a name
				if(name.startsWith("field[") && name.endsWith("].name"))
				{
					nameValuePairs.put(name, value);
				}
				
				//check fi this is a value
				if(name.startsWith("field[") && name.endsWith("].value"))
				{
					nameValuePairs.put(name, value);
				}
				
				//insert this row
				String insert = "INSERT INTO "+table+" (recordid,name,value) VALUES (?,?,?);";
				this.db.execSQL(insert,new Object[]{recordId,name,value});
			}
			
			//insert the name value pairs
			Set<String> keys = nameValuePairs.keySet();
			for(String key:keys)
			{
				if(key.endsWith("].value"))
				{
					continue;
				}
				
				String name = nameValuePairs.get(key);
				
				//Get the value
				String value = nameValuePairs.get(key.replace("].name", "].value"));
				
				
				String insert = "INSERT INTO "+table+" (recordid,name,value) VALUES (?,?,?);";
				this.db.execSQL(insert,new Object[]{recordId,name,value});
			}
			
			
			//insert the JSON representation
			String json = record.toJson();
			String insert = "INSERT INTO "+table+" (recordid,name,value) VALUES (?,?,?);";
			this.db.execSQL(insert,new Object[]{recordId,"om:json",json});
			
			this.db.setTransactionSuccessful();
			
			return recordId;
		}
		finally
		{
			this.db.endTransaction();
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
	
	public Set<Record> selectAll(String from) throws DBException
	{
		Cursor cursor = null;
		try
		{
			Set<Record> all = null;
			
			Map<String,Record> cached = this.cache.all(from);
			
			cursor = this.db.rawQuery("SELECT recordid,value FROM "+from+" WHERE name=?", new String[]{"om:json"});
			
			if(cursor.getCount() > 0)
			{
				all = new HashSet<Record>();
				int recordidIndex = cursor.getColumnIndex("recordid");
				int valueIndex = cursor.getColumnIndex("value");
				cursor.moveToFirst();
				do
				{
					String recordid = cursor.getString(recordidIndex);
					if(cached.containsKey(recordid))
					{
						//object is cached...no need to read from the database
						all.add(cached.get(recordid));
						cursor.moveToNext();
						continue;
					}
					
					String value = cursor.getString(valueIndex);
					
					Map<String,String> state = new HashMap<String,String>();
					JSONObject json = new JSONObject(value);
					Iterator keys = json.keys();
					if(keys != null)
					{
						while(keys.hasNext())
						{
							String localName = (String)keys.next();
							String localValue = json.getString(localName);
							state.put(localName, localValue);
						}
					}
					
					//setup the Record
					Record record = new Record(state);
					all.add(record);
					
					this.cache.put(from,record.getRecordId(),value);
					
					cursor.moveToNext();
				}while(!cursor.isAfterLast());
			}
			
			return all;
		}
		catch(Exception e)
		{
			throw new DBException(DefaultCRUD.class.getName(),"selectAll", new Object[]{
				"Exception: "+e.getMessage()
			});
		}
		finally
		{
			if(cursor != null)
			{
				cursor.close();
			}
		}
	}
	
	
	
	public Record select(String from, String recordId) throws DBException
	{
		Cursor cursor = null;
		try
		{
			Record record = null;
			
			record = this.cache.get(from, recordId);
			if(record != null)
			{
				return record;
			}
			
			cursor = this.db.rawQuery("SELECT value FROM "+from+" WHERE recordid=? AND name=?", new String[]{recordId,"om:json"});
			
			if(cursor.getCount() > 0)
			{
				int valueIndex = cursor.getColumnIndex("value");
				cursor.moveToFirst();
				do
				{
					String value = cursor.getString(valueIndex);
					
					Map<String,String> state = new HashMap<String,String>();
					JSONObject json = new JSONObject(value);
					Iterator keys = json.keys();
					if(keys != null)
					{
						while(keys.hasNext())
						{
							String localName = (String)keys.next();
							String localValue = json.getString(localName);
							state.put(localName, localValue);
						}
					}
					
					record = new Record(state);
					
					this.cache.put(from,record.getRecordId(), value);
					
					cursor.moveToNext();
				}while(!cursor.isAfterLast());
			}
			
			return record;
		}
		catch(Exception e)
		{
			throw new DBException(DefaultCRUD.class.getName(),"select", new Object[]{
				"Exception: "+e.getMessage()
			});
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
			
			cursor = this.db.rawQuery("SELECT DISTINCT recordid FROM "+from+" WHERE name=? AND value=?", new String[]{name,value});
			
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
			
			cursor = this.db.rawQuery("SELECT DISTINCT recordid FROM "+from+" WHERE value=?", new String[]{value});
			
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
		/*Cursor cursor = null;
		try
		{
			Set<Record> records = null;
			
			cursor = this.db.rawQuery("SELECT DISTINCT recordid FROM "+from,null);
			
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
		}*/
		return this.selectAll(from);
	}
	
	public Set<Record> selectByContains(String from, String value) throws DBException
	{
		Cursor cursor = null;
		try
		{
			Set<Record> records = null;
			
			cursor = this.db.rawQuery("SELECT DISTINCT recordid FROM "+from+" WHERE value LIKE ?", new String[]{"%"+value+"%"});
			
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
			
			String recordId = record.getRecordId();
			
			//invalidate the cacched copy if present
			this.cache.invalidate(table, recordId);
			
			Set<String> names = record.getNames();
			
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
			Map<String,String> nameValuePairs = new HashMap<String,String>();
			for(String name: names)
			{
				String value = record.getValue(name);
				
				//check if this is a name
				if(name.startsWith("field[") && name.endsWith("].name"))
				{
					nameValuePairs.put(name, value);
				}
				
				//check fi this is a value
				if(name.startsWith("field[") && name.endsWith("].value"))
				{
					nameValuePairs.put(name, value);
				}
				
				//insert this row
				String insert = "INSERT INTO "+table+" (recordid,name,value) VALUES (?,?,?);";
				this.db.execSQL(insert,new Object[]{recordId,name,value});
			}
			
			//insert the name value pairs
			Set<String> keys = nameValuePairs.keySet();
			for(String key:keys)
			{
				if(key.endsWith("].value"))
				{
					continue;
				}
				
				String name = nameValuePairs.get(key);
				
				//Get the value
				String value = nameValuePairs.get(key.replace("].name", "].value"));
				
				String insert = "INSERT INTO "+table+" (recordid,name,value) VALUES (?,?,?);";
				this.db.execSQL(insert,new Object[]{recordId,name,value});
			}
			
			//insert the JSON representation
			String json = record.toJson();
			String insert = "INSERT INTO "+table+" (recordid,name,value) VALUES (?,?,?);";
			this.db.execSQL(insert,new Object[]{recordId,"om:json",json});
			
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
			
			//invalidate the cached object 
			this.cache.invalidate(table, record.getRecordId());
			
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
			
			//clear the entire cache
			this.cache.clear(table);
			
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
	
	public Cursor testCursor(String from) throws DBException
	{
		Cursor cursor = this.db.rawQuery("SELECT recordid FROM "+from+" WHERE name=? ORDER BY value DESC",new String[]{"timestamp"});
		return cursor;
	}
	
	public Cursor readProxyCursor(String from) throws DBException
	{
		Cursor cursor = this.db.rawQuery("SELECT recordid FROM "+from+" WHERE name=? AND value=?", new String[]{"isProxy","true"});
		return cursor;
	}
}