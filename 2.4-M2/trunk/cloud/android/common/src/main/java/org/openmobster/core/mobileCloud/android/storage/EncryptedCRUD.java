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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;
import org.openmobster.core.mobileCloud.android.crypto.Cryptographer;
import org.openmobster.core.mobileCloud.android.util.GeneralTools;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 *
 * @author openmobster@gmail.com
 */
public class EncryptedCRUD implements CRUDProvider
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
			Cryptographer crypto = Cryptographer.getInstance();
			
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
			
			//insert the JSON representation
			String json = record.toJson();
			String encryptedJson = crypto.encrypt(json.getBytes());
			String insert = "INSERT INTO "+table+" (recordid,name,value) VALUES (?,?,?);";
			this.db.execSQL(insert,new Object[]{recordId,"om:json",encryptedJson});
			
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
			Cryptographer crypto = Cryptographer.getInstance();
			
			Set<Record> all = null;
			
			Map<String,Record> cached = this.cache.all(from);
			
			cursor = this.db.rawQuery("SELECT recordid,value FROM "+from, null);
			
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
					
					
					String encryptedJson = cursor.getString(valueIndex);
					
					//Decrypt
					String decryptedJson = crypto.decrypt(encryptedJson);
					
					Map<String,String> state = new HashMap<String,String>();
					JSONObject json = new JSONObject(decryptedJson);
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
					
					this.cache.put(from,record.getRecordId(),decryptedJson);
					
					cursor.moveToNext();
				}while(!cursor.isAfterLast());
			}
			
			return all;
		}
		catch(Throwable t)
		{
			throw new RuntimeException(t);
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
			Cryptographer crypto = Cryptographer.getInstance();
			
			Record record = null;
			record = this.cache.get(from, recordId);
			if(record != null)
			{
				return record;
			}
			
			cursor = this.db.rawQuery("SELECT value FROM "+from+" WHERE recordid=?", new String[]{recordId});
			
			if(cursor.getCount() > 0)
			{
				int valueIndex = cursor.getColumnIndex("value");
				cursor.moveToFirst();
				do
				{
					String encryptedJson = cursor.getString(valueIndex);
					
					//Decrypt
					String decryptedJson = crypto.decrypt(encryptedJson);
					
					Map<String,String> state = new HashMap<String,String>();
					JSONObject json = new JSONObject(decryptedJson);
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
					record = new Record(state);
					
					this.cache.put(from,record.getRecordId(), decryptedJson);
					
					cursor.moveToNext();
				}while(!cursor.isAfterLast());
			}
			
			return record;
		}
		catch(Throwable t)
		{
			throw new RuntimeException(t);
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
		//Cannot be implemented as a WHERE clause cannot be used on an encrypted record
		return null;
	}
	
	public Set<Record> selectByValue(String from, String name) throws DBException
	{
		//Cannot be implemented as a WHERE clause cannot be used on an encrypted record
		return null;
	}
	
	public Set<Record> selectByNotEquals(String from, String value) throws DBException
	{
		//Cannot be implemented as a WHERE clause cannot be used on an encrypted record
		return null;
	}
	
	public Set<Record> selectByContains(String from, String value) throws DBException
	{
		//Cannot be implemented as a WHERE clause cannot be used on an encrypted record
		return null;
	}
	
	public void update(String table, Record record) throws DBException
	{
		try
		{
			this.db.beginTransaction();
			Cryptographer crypto = Cryptographer.getInstance();
			
			//invalidate the cacched copy if present
			this.cache.invalidate(table, record.getRecordId());
			
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
			
			//insert the JSON representation
			String json = record.toJson();
			String encryptedJson = crypto.encrypt(json.getBytes());
			String insert = "INSERT INTO "+table+" (recordid,name,value) VALUES (?,?,?);";
			this.db.execSQL(insert,new Object[]{recordId,"om:json",encryptedJson});
			
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
	
	public Cursor testCursor(String table) throws DBException
	{
		return null;
	}
}
