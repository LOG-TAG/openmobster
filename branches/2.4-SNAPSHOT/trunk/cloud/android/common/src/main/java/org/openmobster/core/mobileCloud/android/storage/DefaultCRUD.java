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
import java.util.List;
import java.util.ArrayList;
import java.text.MessageFormat;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.openmobster.core.mobileCloud.android.filesystem.FileSystem;
import org.openmobster.core.mobileCloud.android.util.GeneralTools;
import org.openmobster.core.mobileCloud.android.util.GenericAttributeManager;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.JsonReader;

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
			
			if(!record.isStoreable())
			{
				throw new DBException(DefaultCRUD.class.getName(),"insert", new Object[]{
					"Exception: The JSON Object exceeds the maximum size limit"
				});
			}
			
			//SetUp the RecordId
			String recordId = record.getRecordId();
			if(recordId == null || recordId.trim().length() == 0)
			{
				recordId = GeneralTools.generateUniqueId();
				record.setRecordId(recordId);
			}
			
			this.addRecord(table, record);
			
			this.db.setTransactionSuccessful();
			
			return recordId;
		}
		catch(Exception e)
		{
			throw new DBException(DefaultCRUD.class.getName(),"insert", new Object[]{
				"Exception: "+e.getMessage()
			});
		}
		finally
		{
			this.db.endTransaction();
		}
	}
	
	public long selectCount(String from) throws DBException
	{
		Cursor cursor = null;
		try
		{
			cursor = this.db.rawQuery("SELECT count(*) FROM "+from+" WHERE name=?", new String[]{"om:json"});
			if(cursor.getCount()>0)
			{
				cursor.moveToFirst();
				int count = cursor.getInt(0);
				return count;
			}
			
			return 0;
		}
		catch(Exception e)
		{
			throw new DBException(DefaultCRUD.class.getName(),"selectCount", new Object[]{
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
					FileSystem fileSystem = FileSystem.getInstance();
					InputStream in = fileSystem.openInputStream(value);
					JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
					try
					{
						reader.beginObject();
						while(reader.hasNext())
						{
							String localName = reader.nextName();
							String localValue = reader.nextString();
							state.put(localName, localValue);
						}
						reader.endObject();
					}
					finally
					{
						reader.close();
					}
					Record record = new Record(state);
					all.add(record);
					this.cache.put(from,record.getRecordId(),record);
					
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
					FileSystem fileSystem = FileSystem.getInstance();
					InputStream in = fileSystem.openInputStream(value);
					JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
					try
					{
						reader.beginObject();
						while(reader.hasNext())
						{
							String localName = reader.nextName();
							String localValue = reader.nextString();
							state.put(localName, localValue);
						}
						reader.endObject();
					}
					finally
					{
						reader.close();
					}
					record = new Record(state);
					this.cache.put(from,record.getRecordId(), record);
					
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
	
	public void update(String table, Record record) throws DBException
	{
		try
		{
			this.db.beginTransaction();
			
			if(!record.isStoreable())
			{
				throw new DBException(DefaultCRUD.class.getName(),"update", new Object[]{
					"Exception: The JSON Object exceeds the maximum size limit"
				});
			}
			
			String recordId = record.getRecordId();
			
			//invalidate the cacched copy if present
			this.cache.invalidate(table, recordId);
			
			this.addRecord(table, record);
			
			this.db.setTransactionSuccessful();
		}
		catch(Exception e)
		{
			throw new DBException(DefaultCRUD.class.getName(),"update", new Object[]{
				"Exception: "+e.getMessage()
			});
		}
		finally
		{
			this.db.endTransaction();
		}
	}
	
	public void delete(String table, Record record) throws DBException
	{
		Cursor cursor = null;
		try
		{
			this.db.beginTransaction();
			
			String recordId = record.getRecordId();
			
			//invalidate the cached object 
			this.cache.invalidate(table, recordId);
			
			//Get the JSonPointer that must be cleaned up along with deleting this record
			String jsonPointer = null;
			cursor = this.db.rawQuery("SELECT value FROM "+table+" WHERE recordid=? AND name=?", new String[]{recordId,"om:json"});
			if(cursor.getCount()>0)
			{
				int valueIndex = cursor.getColumnIndex("value");
				cursor.moveToFirst();
				do
				{
					jsonPointer = cursor.getString(valueIndex);
					cursor.moveToNext();
				}while(!cursor.isAfterLast());
			}
			
			//delete this record
			String delete = "DELETE FROM "+table+" WHERE recordid='"+recordId+"'";
			this.db.execSQL(delete);
			
			//delete the json pointer
			if(jsonPointer != null)
			{
				FileSystem fileSystem = FileSystem.getInstance();
				fileSystem.cleanup(jsonPointer);
			}
			
			this.db.setTransactionSuccessful();
		}
		finally
		{
			if(cursor != null)
			{
				cursor.close();
			}
			this.db.endTransaction();
		}
	}
	
	public void deleteAll(String table) throws DBException
	{
		Cursor cursor = null;
		try
		{
			this.db.beginTransaction();
			
			//clear the entire cache
			this.cache.clear(table);
			
			//Get JSONPointers that must be deleted for this table
			Set<String> jsonPointers = new HashSet<String>();
			cursor = this.db.rawQuery("SELECT value FROM "+table+" WHERE name=?", new String[]{"om:json"});
			if(cursor.getCount()>0)
			{
				int valueIndex = cursor.getColumnIndex("value");
				cursor.moveToFirst();
				do
				{
					String jsonPointer = cursor.getString(valueIndex);
					jsonPointers.add(jsonPointer);
					cursor.moveToNext();
				}while(!cursor.isAfterLast());
			}
			
			//delete this record
			String delete = "DELETE FROM "+table;
			this.db.execSQL(delete);
			
			//Cleanup the JSonPointers
			FileSystem fileSystem = FileSystem.getInstance();
			for(String jsonPointer:jsonPointers)
			{
				fileSystem.cleanup(jsonPointer);
			}
			
			this.db.setTransactionSuccessful();
		}
		finally
		{
			if(cursor != null)
			{
				cursor.close();
			}
			this.db.endTransaction();
		}
	}
	//-------------------------------------------------------------------------------------------------------------------------------------
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
	
	public Cursor readByNameValuePair(String from,String name,String value) throws DBException
	{
		Cursor cursor = this.db.rawQuery("SELECT recordid FROM "+from+" WHERE name=? AND value=?", new String[]{name,value});
		return cursor;
	}
	//-------------------------------------------------------------------------------------------------------------------------------------
	public Cursor searchExactMatchAND(String from, GenericAttributeManager criteria) throws DBException
	{
		if(criteria == null || criteria.isEmpty())
		{
			return null;
		}
		
		String fragment = "value LIKE ''%{0}={1}%''";
		List<String> fragments = new ArrayList<String>();
		String[] names = criteria.getNames();
		for(String name:names)
		{
			String value = (String)criteria.getAttribute(name);
			String queryFragment = MessageFormat.format(fragment, name, value);
			
			fragments.add(queryFragment);
		}
		
		StringBuilder queryBuffer = new StringBuilder();
		queryBuffer.append("SELECT recordid FROM "+from+" WHERE name='om:search' AND (");
		for(int i=0,size=fragments.size();i<size;i++)
		{
			String queryFragment = fragments.get(i);
			queryBuffer.append(queryFragment);
			
			if(i==(size-1))
			{
				//this is the last fragment
				queryBuffer.append(")");
			}
			else
			{
				queryBuffer.append(" AND ");
			}
		}
		
		
		String query = queryBuffer.toString();
		
		Cursor cursor = this.db.rawQuery(query, new String[]{});
		
		return cursor;
	}
	
	public Cursor searchExactMatchOR(String from, GenericAttributeManager criteria) throws DBException
	{
		if(criteria == null || criteria.isEmpty())
		{
			return null;
		}
		
		String fragment = "value LIKE ''%{0}={1}%''";
		List<String> fragments = new ArrayList<String>();
		String[] names = criteria.getNames();
		for(String name:names)
		{
			String value = (String)criteria.getAttribute(name);
			String queryFragment = MessageFormat.format(fragment, name, value);
			
			fragments.add(queryFragment);
		}
		
		StringBuilder queryBuffer = new StringBuilder();
		queryBuffer.append("SELECT recordid FROM "+from+" WHERE name='om:search' AND (");
		for(int i=0,size=fragments.size();i<size;i++)
		{
			String queryFragment = fragments.get(i);
			queryBuffer.append(queryFragment);
			
			if(i==(size-1))
			{
				//this is the last fragment
				queryBuffer.append(")");
			}
			else
			{
				queryBuffer.append(" OR ");
			}
		}
		
		
		String query = queryBuffer.toString();
		
		Cursor cursor = this.db.rawQuery(query, new String[]{});
		
		return cursor;
	}
	//------------------------------------------------Private Impl-------------------------------------------------------------------------------
	private void addRecord(String table,Record record) throws DBException
	{
		String recordId = record.getRecordId();
		
		//SetUp the DirtyStatus
		record.setDirtyStatus(GeneralTools.generateUniqueId());
		
		//Delete a record if one exists by this id...cleanup
		this.delete(table, record);
		
		Set<String> names = record.getNames();
		Map<String,String> nameValuePairs = new HashMap<String,String>();
		for(String name: names)
		{
			String value = record.getValue(name);
			boolean addProperty = true;
			
			//check if this is a name
			if(name.startsWith("field[") && name.endsWith("].name"))
			{
				nameValuePairs.put(name, value);
				addProperty = false;
			}
			
			//check fi this is a value
			if(name.startsWith("field[") && name.endsWith("].value"))
			{
				nameValuePairs.put(name, value);
				addProperty = false;
			}
			
			//insert this row
			if(addProperty)
			{	
				String insert = "INSERT INTO "+table+" (recordid,name,value) VALUES (?,?,?);";
				this.db.execSQL(insert,new Object[]{recordId,name,value});
			}
		}
		
		//insert the name value pairs
		Set<String> keys = nameValuePairs.keySet();
		StringBuilder builder = new StringBuilder();
		for(String key:keys)
		{
			if(key.endsWith("].value"))
			{
				continue;
			}
			
			String name = nameValuePairs.get(key);
			
			//Get the value
			String value = nameValuePairs.get(key.replace("].name", "].value"));
			
			if(value.length() < 1000)
			{
				String insert = "INSERT INTO "+table+" (recordid,name,value) VALUES (?,?,?);";
				this.db.execSQL(insert,new Object[]{recordId,name,value});
				
				builder.append(name+"="+value+"&amp;");
			}
		}
		
		String metaDataInsert = "INSERT INTO "+table+" (recordid,name,value) VALUES (?,?,?);";
		this.db.execSQL(metaDataInsert,new Object[]{recordId,"om:search",builder.toString()});
		
		//insert the JSON representation
		String jsonPointer = record.toJson();
		String insert = "INSERT INTO "+table+" (recordid,name,value) VALUES (?,?,?);";
		this.db.execSQL(insert,new Object[]{recordId,"om:json",jsonPointer});
	}
	//----------------------------------------------Deprecated------------------------------------------------------------------------
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
}