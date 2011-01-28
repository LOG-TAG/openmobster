/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.rimos.module.sync.engine;

import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;

import org.openmobster.core.mobileCloud.rimos.errors.SystemException;
import org.openmobster.core.mobileCloud.rimos.module.sync.Anchor;
import org.openmobster.core.mobileCloud.rimos.service.Registry;
import org.openmobster.core.mobileCloud.rimos.service.Service;
import org.openmobster.core.mobileCloud.rimos.storage.DBException;
import org.openmobster.core.mobileCloud.rimos.storage.Database;
import org.openmobster.core.mobileCloud.rimos.storage.Record;


/**
 * 
 * @author openmobster@gmail.com
 *
 */
public final class SyncDataSource extends Service
{		
	/**
	 * 
	 *
	 */
	public SyncDataSource()
	{
	}
	
	/**
	 * 
	 * @return
	 */
	public static SyncDataSource getInstance()
	{
		return (SyncDataSource)Registry.getInstance().lookup(SyncDataSource.class);
	}
	
	public void start()
	{
		try
		{
			Database database = Database.getInstance();
			
			//Initialize the changelog table
			if(!database.doesTableExist(Database.sync_changelog_table))
			{
				database.createTable(Database.sync_changelog_table);
			}
			
			//Initialize the anchor table
			if(!database.doesTableExist(Database.sync_anchor))
			{
				database.createTable(Database.sync_anchor);
			}
			
			//Initialize the recordmap table
			if(!database.doesTableExist(Database.sync_recordmap))
			{
				database.createTable(Database.sync_recordmap);
			}
			
			//Initialize the syncerror table
			if(!database.doesTableExist(Database.sync_error))
			{
				database.createTable(Database.sync_error);
			}
		}
		catch(DBException dbe)
		{
			throw new SystemException(this.getClass().getName(), "start", new Object[]{
				"Database Exception="+dbe.getMessage()
			});
		}
	}
	
	public void stop()
	{
		
	}
	
	/**
	 * 
	 *
	 */
	public void clearAll()
	{
		try
		{
			Database.getInstance().deleteAll(Database.sync_recordmap);			
			Database.getInstance().deleteAll(Database.sync_error);	
			this.deleteChangeLog();
		}
		catch(DBException dbe)
		{
			throw new SystemException(this.getClass().getName(), "clearAll", new Object[]{
				"Database Exception="+dbe.getMessage()
			}); 
		}
	}
	//------Anchor related data services-------------------------------------------------------------------
	/**
	 * 
	 * @return
	 */
	public Anchor readAnchor() throws DBException
	{
		Anchor stored = null;
		
		Enumeration anchors = Database.getInstance().selectAll(Database.sync_anchor);
		if(anchors.hasMoreElements())
		{
			Record record = (Record)anchors.nextElement();
			stored = new Anchor(record);
		}
		
		return stored;		
	}
	
	/**
	 * 
	 *
	 */
	public void saveAnchor(Anchor anchor) throws DBException
	{		
		Anchor stored = this.readAnchor();
		
		if(stored == null)
		{
			//Create a new anchor in the database
			Record record = anchor.getRecord();
			Database.getInstance().insert(Database.sync_anchor, record);
		}
		else
		{
			Enumeration anchors = Database.getInstance().selectAll(Database.sync_anchor);
			Record record = (Record)anchors.nextElement();
			
			//Update the existing anchor in the database
			record.setValue("target", anchor.getTarget());			
			record.setValue("lastSync", anchor.getLastSync());			
			record.setValue("nextSync", anchor.getNextSync());			
			Database.getInstance().update(Database.sync_anchor, record);			
		}		
	}	
	
	/**
	 * 
	 * @throws DBException
	 */
	public void deleteAnchor() throws DBException
	{
		Database.getInstance().deleteAll(Database.sync_anchor);
	}
	//----ChangeLog Support-----------------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @param entries
	 */
	public void createChangeLogEntries(Vector entries) throws DBException
	{
		if(entries != null)
		{
			Enumeration enumeration = entries.elements();
			while(enumeration.hasMoreElements())
			{
				ChangeLogEntry cour = (ChangeLogEntry)enumeration.nextElement();
				Record record = cour.getRecord();
				Database.getInstance().insert(Database.sync_changelog_table, record);
			}
		}
	}
	
	/**
	 * 
	 */
	public Vector readChangeLog() throws DBException
	{
		Vector changeLog = new Vector();
		
		Enumeration changeLogRecords = Database.getInstance().selectAll(Database.sync_changelog_table);
		while(changeLogRecords.hasMoreElements())
		{
			Record record = (Record)changeLogRecords.nextElement();
			changeLog.addElement(new ChangeLogEntry(record));
		}
		
		return changeLog;
	}			

	/**
	 * 
	 * @param changeLogEntry
	 */
	public void deleteChangeLogEntry(ChangeLogEntry changeLogEntry) throws DBException
	{
		Record toBeDeleted = null;
		Enumeration log = this.readChangeLog().elements();
		while(log.hasMoreElements())
		{
			ChangeLogEntry cour = (ChangeLogEntry)log.nextElement();
			if(cour.equals(changeLogEntry))
			{
				toBeDeleted = cour.getRecord();
				break;
			}
		}
		if(toBeDeleted != null)
		{
			Database.getInstance().delete(Database.sync_changelog_table, toBeDeleted);
		}
	}
	
	/**
	 * 
	 *
	 */
	public void deleteChangeLog() throws DBException
	{
		Database.getInstance().deleteAll(Database.sync_changelog_table);
	}
	//-------Map Support------------------------------------------------------------------------------------
	/**
	 * 
	 */
	private Vector getMappedRecords() throws DBException
	{
		Vector mappedRecords = new Vector();
		
		Enumeration cour = Database.getInstance().selectAll(Database.sync_recordmap);
		while(cour.hasMoreElements())
		{
			Record record = (Record)cour.nextElement();
			
			mappedRecords.addElement(new RecordMap(record));
		}
		
		return mappedRecords;
	}
	
	/**
	 * This comes into play only when Map synchronization between the client and the server fails
	 * within a sync session
	 * 
	 * @param source
	 * @param target
	 * @param map
	 */
	public void saveRecordMap(String source, String target, Hashtable map) throws DBException
	{
		if(map != null)
		{
			Enumeration guids = map.keys();
			while(guids.hasMoreElements())
			{
				String guid = (String)guids.nextElement(); 
				String luid = (String)map.get(guid);
				
				RecordMap recordMap = new RecordMap();
				recordMap.setSource(source);
				recordMap.setTarget(target);
				recordMap.setGuid(guid);
				recordMap.setLuid(luid);
				
				Database.getInstance().insert(Database.sync_recordmap, recordMap.getRecord());
			}
		}
	}
	
	/**
	 * 
	 * @param source
	 * @param target
	 * @return
	 */
	public Hashtable readRecordMap(String source, String target) throws DBException
	{
		Hashtable map = null;
		
		map = new Hashtable();
		Vector recordMaps = this.getMappedRecords();
		for(int i=0,size=recordMaps.size(); i<size; i++)
		{
			RecordMap cour = (RecordMap)recordMaps.elementAt(i);
			if(cour.getSource().equals(source) && cour.getTarget().equals(target))
			{
				map.put(cour.getGuid(), cour.getLuid());
			}
		}
		
		return map;
	}
	
	/**
	 * Cleans up the device record map once Map information is successfully processed by the server
	 * during a sync session 
	 * 
	 * @param source
	 * @param target
	 */
	public void removeRecordMap(String source, String target) throws DBException
	{		
		Vector recordMaps = this.getMappedRecords();
		for(int i=0,size=recordMaps.size(); i<size; i++)
		{
			RecordMap cour = (RecordMap)recordMaps.elementAt(i);
			if(cour.getSource().equals(source) && cour.getTarget().equals(target))
			{
				Record record = Database.getInstance().select(Database.sync_recordmap, cour.getId());
				Database.getInstance().delete(Database.sync_recordmap, record);
			}
		}		
	}
	//---Error Support------------------------------------------------------------------------------------
	/**
	 * 
	 */
	private Vector getSyncErrors() throws DBException
	{
		Vector syncErrors = new Vector();
		
		Enumeration cour = Database.getInstance().selectAll(Database.sync_error);
		while(cour.hasMoreElements())
		{
			Record record = (Record)cour.nextElement();
			syncErrors.addElement(new SyncError(record));
		}
		
		return syncErrors;
	}
	
	/**
	 * 
	 */
	public void saveError(SyncError error) throws DBException
	{
		Record record = error.getRecord();
		Database.getInstance().insert(Database.sync_error, record);
	}
	
	/**
	 * 
	 * @param source
	 * @param target
	 * @param code
	 * @return
	 */
	public SyncError readError(String source, String target, String code) throws DBException
	{
		SyncError syncError = null;
		
		Vector syncErrors = this.getSyncErrors();
		for(int i=0,size=syncErrors.size(); i<size; i++)
		{
			SyncError cour = (SyncError)syncErrors.elementAt(i);
			if(cour.getSource().equals(source) && 
			   cour.getTarget().equals(target) && 
			   cour.getCode().equals(code))
			{
				syncError = cour;
				break;
			}
		}
		
		return syncError;
	}
	
	/**
	 * 
	 * @param source
	 * @param target
	 * @param code
	 */
	public void removeError(String source, String target, String code) throws DBException
	{
		Vector syncErrors = this.getSyncErrors();
		for(int i=0,size=syncErrors.size(); i<size; i++)
		{
			SyncError cour = (SyncError)syncErrors.elementAt(i);
			if(cour.getSource().equals(source) && 
			   cour.getTarget().equals(target) && 
			   cour.getCode().equals(code))
			{
				Database.getInstance().delete(Database.sync_error, cour.getRecord());
				break;
			}
		}
	}
}
