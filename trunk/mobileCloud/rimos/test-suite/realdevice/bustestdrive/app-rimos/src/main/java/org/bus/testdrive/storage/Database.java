/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.bus.testdrive.storage;

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Vector;

import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.system.PersistentObject;

import org.bus.testdrive.util.GeneralTools;

/**
 * @author openmobster@gmail.com
 * 
 * Concurrency Marker - Concurrent Component (concurrently used by multiple threads)
 *
 */
public final class Database 
{	
	private static Database singleton;
	
	
	//Shared System Tables/Data (Shared between the main device container and all other moblet applications installed on the device)
	public static String config_table = "tb_config"; //stores container configuration
	public static String sync_changelog_table = "tb_changelog"; //stores changelog for the sync service
	public static String sync_anchor = "tb_anchor"; //stores anchor related data for the sync service
	public static String sync_recordmap = "tb_recordmap"; //stores record map related data for the sync service
	public static String sync_error = "tb_sync_error"; //stores sync errors
	public static String bus_registration = "tb_bus_registration"; //stores service bus registrations used for inter-application invocations
	public static String provisioning_table = "tb_provisioning"; //stores device provisioning related information
	public static String system_errors = "tb_errorlog"; //stores runtime errors genenerated by mobile cloud and all the moblets
	
	
	private long uid;
	private PersistentObject database;
	
		
	private Database()
	{
		
	}
	
	public static Database getInstance() throws DBException
	{
		if(Database.singleton == null)
		{
			synchronized(Database.class)
			{
				if(Database.singleton == null)
				{
					Database.singleton = new Database();
					Database.singleton.init();
				}
			}
		}
		return Database.singleton;
	}
	//----------LifeCycle operations----------------------------------------------------------------------------------------------------------------------------------		
	private void init()	throws DBException
	{
		if(this.isConnected())
		{
			return;
		}
		
		//Make a best effort unique id for the database on the device
		//Make a long hash value of "org.openmobster.core.mobileCloud.rimos.database.Database"
		//TODO: Unique_Id: read the value to hashCode from resource configuration file
		long localUid = (long)this.getClass().getName().hashCode();
		
		PersistentObject localDatabase = PersistentStore.getPersistentObject(localUid);
		Object data = localDatabase.getContents();
		
		//Detect and make sure this database is used only by the container
		if(data != null)
		{
			boolean cannotRecover = false;
			
			if(data instanceof Hashtable)
			{
				Hashtable tables = (Hashtable)data;
				Object o = tables.get(config_table);
				if(o!= null && !(o instanceof Vector))
				{
					cannotRecover = true;
				}
			}
			else
			{
				cannotRecover = true;
			}			
			
			if(cannotRecover)
			{
				//This is bad.....This means there is some other application using a database
				//referenced by the specified UID
				//A new UID will have to be generated. This will require altering the UID value in
				//the Resource file and re-compiling to produce a new binary for this particular device
				//See the Reference Documentation to see how this can be done.
				
				//If this is too much, another option is doing a hard reset and installing the container first.
				//Note: this carries the risk of losing data on the device if not synchronized
				this.uid = 0;
				this.database = null;
				throw new DBException(this.getClass().getName(), "init", null, DBException.ERROR_INIT_IMPOSSIBLE);
			}
		}
		
		//If I am here the database can be successfully bootstrapped...yipee!!!
		if(data == null)
		{
			//Empty database, need to bootstap it
			synchronized(PersistentStore.getSynchObject())
			{
				data = localDatabase.getContents();
				if(data == null)
				{
					Vector table = new Vector();			
					Record record = new Record();
					record.setRecordId("1");
					record.setDirtyStatus(String.valueOf(GeneralTools.generateUniqueId()));
					record.setValue("databaseUid", ""+localUid);
					table.addElement(record.getState());
					
					Hashtable bootstrap = new Hashtable();
					bootstrap.put(config_table, table);
					
					localDatabase.setContents(bootstrap);
					localDatabase.commit();
				}
			}
		}
		
		//Now connect the database
		this.connect(localUid);
	}
		
	public void connect() throws DBException
	{
		if(this.isConnected())
		{
			return;
		}				
		
		//Make a best effort unique id for the database on the device
		//Make a long hash value of "org.openmobster.core.mobileCloud.rimos.database.Database"
		//TODO: read the value to hashCode from resource configuration file
		this.uid = (long)this.getClass().getName().hashCode();
		
		//Go ahead and connect to the specified PersistentStore Object
		this.database = PersistentStore.getPersistentObject(this.uid);
		
		//Verify the integrity of the database
		Object data = this.database.getContents();
		if(data == null || !(data instanceof Hashtable))
		{
			this.uid = 0;
			this.database = null;			
			throw new DBException(this.getClass().getName(), "connect", new Object[]{new Long(uid)}, DBException.ERROR_UNINITIALIZED);
		}
		
		Hashtable cour = (Hashtable)data;
		if(!cour.containsKey(config_table))
		{
			this.uid = 0;
			this.database = null;
			throw new DBException(this.getClass().getName(), "connect", new Object[]{new Long(uid)}, DBException.ERROR_UNINITIALIZED);
		}
	}
	
	public void connect(long uid) throws DBException
	{
		if(this.isConnected())
		{
			return;
		}
		
		if(uid == 0)
		{
			throw new DBException(this.getClass().getName(), "connect", new Object[]{new Long(uid)}, DBException.ERROR_INVALID_UID);
		}		
				
		this.uid = uid;
		
		//Go ahead and connect to the specified PersistentStore Object
		this.database = PersistentStore.getPersistentObject(this.uid);
		
		//Verify the integrity of the database
		Object data = this.database.getContents();
		if(data == null || !(data instanceof Hashtable))
		{
			this.uid = 0;
			this.database = null;			
			throw new DBException(this.getClass().getName(), "connect", new Object[]{new Long(uid)}, DBException.ERROR_UNINITIALIZED);
		}
		
		Hashtable cour = (Hashtable)data;
		if(!cour.containsKey(config_table))
		{
			this.uid = 0;
			this.database = null;
			throw new DBException(this.getClass().getName(), "connect", new Object[]{new Long(uid)}, DBException.ERROR_UNINITIALIZED);
		}
	}
	
	public void disconnect() throws DBException
	{
		this.uid = 0;
		this.database = null;
	}	
	//---------------------------------------------------------------------------------------------------------------------------------------------
	public boolean isConnected() throws DBException
	{
		boolean connected = false;
		
		if(this.uid != 0 && this.database != null)
		{
			connected = true;
		}
		
		return connected;
	}
	
	public long getDatabaseUid() throws DBException
	{
		if(!this.isConnected())
		{			
			throw new DBException(this.getClass().getName(), "getDatabaseUid", null, DBException.ERROR_NOT_CONNECTED);
		}
		return this.uid;
	}	
	//----------Table related operations------------------------------------------------------------------------------------------------------------------------------
	public Enumeration enumerateTables() throws DBException
	{
		if(!this.isConnected())
		{
			throw new DBException(this.getClass().getName(), "eumerateTables", null, DBException.ERROR_NOT_CONNECTED);
		}
		
		return this.getDatabaseContent().keys();
	}
	
	public void createTable(String tableName) throws DBException
	{
		if(!this.isConnected())
		{
			throw new DBException(this.getClass().getName(), "createTable", new Object[]{tableName}, DBException.ERROR_NOT_CONNECTED);
		}
		
		Hashtable contents = this.getDatabaseContent();
				
		if(contents.containsKey(tableName))
		{			
			throw new DBException(this.getClass().getName(), "createTable", new Object[]{tableName}, DBException.ERROR_TABLE_ALREADY_EXISTS);
		}
		
		contents.put(tableName, new Vector());
		this.database.commit();
	}
	
	public boolean doesTableExist(String tableName) throws DBException
	{
		boolean doesTableExist = false;
		
		if(!this.isConnected())
		{
			throw new DBException(this.getClass().getName(), "createTable", new Object[]{tableName}, DBException.ERROR_NOT_CONNECTED);
		}
		
		Hashtable contents = this.getDatabaseContent();
				
		if(contents.containsKey(tableName))
		{			
			doesTableExist = true;
		}
		
		return doesTableExist;
	}
	
	public boolean isTableEmpty(String tableName) throws DBException
	{
		boolean isEmpty = false;
		
		if(!this.isConnected())
		{
			throw new DBException(this.getClass().getName(), "isTableEmpty", new Object[]{tableName}, DBException.ERROR_NOT_CONNECTED);
		}
		
		Hashtable contents = this.getDatabaseContent();
		if(!contents.containsKey(tableName))
		{
			throw new DBException(this.getClass().getName(), "isTableEmpty", new Object[]{tableName}, DBException.ERROR_TABLE_NOTFOUND);
		}
		
		Vector table = (Vector)contents.get(tableName);
		if(table.size()==0)
		{
			isEmpty = true;
		}
		
		return isEmpty;
	}
	
	public void dropTable(String tableName) throws DBException
	{
		if(!this.isConnected())
		{
			throw new DBException(this.getClass().getName(), "dropTable", new Object[]{tableName}, DBException.ERROR_NOT_CONNECTED);
		}
		
		if(tableName.equals(config_table))
		{			
			throw new DBException(this.getClass().getName(), "dropTable", new Object[]{tableName}, DBException.ERROR_CONFIG_TABLE_DELETE_NOT_ALLOWED);
		}
		
		Hashtable contents = this.getDatabaseContent();		
		if(contents.containsKey(tableName))
		{
			Object table = contents.get(tableName);
			
			//Lock the table before its removed
			synchronized(table)
			{
				contents.remove(tableName);
				this.database.commit();
			}
		}
	}
	//----------Select operations------------------------------------------------------------------------------------------------------------
	public long selectCount(String from) throws DBException
	{
		long recordCount = 0;
		
		if(!this.isConnected())
		{
			throw new DBException(this.getClass().getName(), "selectCount", new Object[]{from}, DBException.ERROR_NOT_CONNECTED);
		}
		
		Hashtable contents = this.getDatabaseContent();
		
		if(!contents.containsKey(from))
		{
			throw new DBException(this.getClass().getName(), "selectCount", new Object[]{from}, DBException.ERROR_TABLE_NOTFOUND);
		}
		
		Vector table = (Vector)contents.get(from);
		recordCount = table.size();
		
		return recordCount;
	}
	
	public Enumeration selectAll(String from) throws DBException
	{
		Enumeration allRecords = null;
		
		if(!this.isConnected())
		{
			throw new DBException(this.getClass().getName(), "selectAll", new Object[]{from}, DBException.ERROR_NOT_CONNECTED);
		}
		
		Hashtable contents = this.getDatabaseContent();
		
		if(!contents.containsKey(from))
		{
			throw new DBException(this.getClass().getName(), "selectAll", new Object[]{from}, DBException.ERROR_TABLE_NOTFOUND);
		}
		
		Vector table = (Vector)contents.get(from);				
		allRecords = this.readAll(table);
				
		return allRecords;
	}
	
	public Record select(String from, String recordId) throws DBException
	{
		Record record = null;
		
		if(!this.isConnected())
		{
			throw new DBException(this.getClass().getName(), "select", new Object[]{from, recordId}, DBException.ERROR_NOT_CONNECTED);
		}
		
		Hashtable contents = this.getDatabaseContent();
		
		if(!contents.containsKey(from))
		{
			throw new DBException(this.getClass().getName(), "select", new Object[]{from, recordId}, DBException.ERROR_TABLE_NOTFOUND);
		}
		
		Vector table = (Vector)contents.get(from);
		record = this.read(table, recordId);
				
		return record;
	}	
	//--------Insert operations------------------------------------------------------------------------------------------------------------------
	public String insert(String into, Record record) throws DBException
	{
		if(!this.isConnected())
		{
			throw new DBException(this.getClass().getName(), "insert", new Object[]{into}, 
			DBException.ERROR_NOT_CONNECTED);
		}
		
		Hashtable contents = this.getDatabaseContent();
		
		if(!contents.containsKey(into))
		{
			throw new DBException(this.getClass().getName(), "insert", new Object[]{into}, 
			DBException.ERROR_TABLE_NOTFOUND);
		}
		
		Vector table = (Vector)contents.get(into);
		
		//Lock the table before adding a new entry to it
		synchronized(table)
		{
			if(record.getRecordId()==null || record.getRecordId().trim().length()==0)
			{
				//Generate a RecordId for this object
				record.setRecordId(String.valueOf(GeneralTools.generateUniqueId()));
			}
			record.setDirtyStatus(String.valueOf(GeneralTools.generateUniqueId()));
			
			//Remove an existing record if found
			this.delete(table, record);			
			table.addElement(record.getState());
			
			this.database.commit();
		}
		
		return record.getRecordId();
	}
	//--------Update operations------------------------------------------------------------------------------------------------------------------
	public void update(String into, Record record) throws DBException
	{
		if(!this.isConnected())
		{
			throw new DBException(this.getClass().getName(), "update", new Object[]{into}, 
					DBException.ERROR_NOT_CONNECTED);
		}
		
		Hashtable contents = this.getDatabaseContent();
		
		if(!contents.containsKey(into))
		{
			throw new DBException(this.getClass().getName(), "update", new Object[]{into}, 
					DBException.ERROR_TABLE_NOTFOUND);
		}
		
		Vector table = (Vector)contents.get(into);	
		
		//Lock the table before replacing one of its entries
		synchronized(table)
		{
			this.update(table, record);
			this.database.commit();
		}
	}
	//--------Delete operations------------------------------------------------------------------------------------------------------------------
	public void delete(String from, Record record) throws DBException
	{
		if(!this.isConnected())
		{
			throw new DBException(this.getClass().getName(), "delete", new Object[]{from}, 
			DBException.ERROR_NOT_CONNECTED);
		}
		
		Hashtable contents = this.getDatabaseContent();
		
		if(!contents.containsKey(from))
		{
			throw new DBException(this.getClass().getName(), "delete", new Object[]{from}, 
			DBException.ERROR_TABLE_NOTFOUND);
		}
		
		Vector table = (Vector)contents.get(from);
		
		//Lock the table before modifying it by deleting one of its entries
		synchronized(table)
		{
			this.delete(table, record);
			this.database.commit();
		}
	}
	
	public void deleteAll(String from) throws DBException
	{
		if(!this.isConnected())
		{
			throw new DBException(this.getClass().getName(), "deleteAll", new Object[]{from}, 
					DBException.ERROR_NOT_CONNECTED);
		}
		
		Hashtable contents = this.getDatabaseContent();
		
		if(!contents.containsKey(from))
		{
			throw new DBException(this.getClass().getName(), "deleteAll", new Object[]{from}, 
					DBException.ERROR_TABLE_NOTFOUND);
		}
		
		Vector table = (Vector)contents.get(from);	
		
		//Lock the table before deleting all its contents
		synchronized(table)
		{
			table.removeAllElements();
			this.database.commit();
		}
	}
	//-------------------------------------------------------------------------------------------------------------------------------------------
	private Hashtable getDatabaseContent()
	{
		Hashtable contents = (Hashtable)this.database.getContents();
		return contents;
	}
	//------Table related operations-------------------------------------------------------------------------------------------------------------	
	private Record read(Vector table, String recordId)
	{		
		Record record = null;
		Hashtable state = this.findRecord(table, recordId);		
		if(state != null)
		{			
			record = new Record(state);
		}
		return record;		
	}
	
	private Enumeration readAll(Vector table)
	{		
		Vector output = new Vector();
		int size = table.size();
		for(int i=0; i<size; i++)
		{
			Hashtable storedRecord = (Hashtable)table.elementAt(i);
			Record record = new Record(storedRecord);
			output.addElement(record);
		}
		return output.elements();		
	}
	
	private void update(Vector table, Record record) throws DBException
	{
		Hashtable state = this.findRecord(table, record.getRecordId());
		
		if(state != null)
		{
			//Make sure the input Record is not Stale
			Record stateRecord = new Record(state);
			if(!record.getDirtyStatus().equals(stateRecord.getDirtyStatus()))
			{
				//The input record is stale
				throw new DBException(this.getClass().getName(), "update", new Object[]{record.getRecordId()}, DBException.ERROR_RECORD_STALE);
			}
			
			//Record checks out, update it
			record.setDirtyStatus(String.valueOf(GeneralTools.generateUniqueId()));
			
			table.removeElement(state);
			table.addElement(record.getState());
		}
		else
		{
			//The Record that user is trying to update has been deleted from the database
			throw new DBException(this.getClass().getName(), "update", new Object[]{record.getRecordId()}, DBException.ERROR_RECORD_DELETED);
		}
	}
	
	private void delete(Vector table, Record record)
	{
		Hashtable state = this.findRecord(table, record.getRecordId());
		
		if(state != null)
		{
			//Don't check for staleness, since the idea is to delete this object from the system
			//regardless of its state			
			table.removeElement(state);
		}
	}
		
	
	private Hashtable findRecord(Vector table, String recordId)
	{
		Hashtable found = null;
		
		int size = table.size();
		for(int i=0; i<size; i++)
		{
			Hashtable cour = (Hashtable)table.elementAt(i);
			String courId = (String)cour.get("recordId");
			if(recordId.equals(courId))
			{
				return cour;
			}
		}
		
		return found;
	}	
}
