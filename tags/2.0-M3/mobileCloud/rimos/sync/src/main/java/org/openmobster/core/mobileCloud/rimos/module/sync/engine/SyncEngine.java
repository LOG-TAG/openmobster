/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.rimos.module.sync.engine;

import java.util.Hashtable;
import java.util.Vector;

import org.openmobster.core.mobileCloud.rimos.module.bus.MobilePushInvocation;
import org.openmobster.core.mobileCloud.rimos.module.bus.MobilePushMetaData;
import org.openmobster.core.mobileCloud.rimos.module.mobileObject.DeviceSerializer;
import org.openmobster.core.mobileCloud.rimos.module.mobileObject.MobileObject;
import org.openmobster.core.mobileCloud.rimos.module.mobileObject.MobileObjectDatabase;
import org.openmobster.core.mobileCloud.rimos.module.sync.AbstractOperation;
import org.openmobster.core.mobileCloud.rimos.module.sync.Add;
import org.openmobster.core.mobileCloud.rimos.module.sync.Anchor;
import org.openmobster.core.mobileCloud.rimos.module.sync.Delete;
import org.openmobster.core.mobileCloud.rimos.module.sync.Item;
import org.openmobster.core.mobileCloud.rimos.module.sync.Replace;
import org.openmobster.core.mobileCloud.rimos.module.sync.Session;
import org.openmobster.core.mobileCloud.rimos.module.sync.Status;
import org.openmobster.core.mobileCloud.rimos.module.sync.SyncAdapter;
import org.openmobster.core.mobileCloud.rimos.module.sync.SyncCommand;
import org.openmobster.core.mobileCloud.rimos.module.sync.SyncException;
import org.openmobster.core.mobileCloud.rimos.module.sync.SyncXMLTags;
import org.openmobster.core.mobileCloud.rimos.util.GeneralTools;
import org.openmobster.core.mobileCloud.rimos.util.XMLUtil;


/**
 * 
 * @author openmobster@gmail.com
 *
 */
public class SyncEngine 
{
	/**
	 * 
	 */
	public static String OPERATION_ADD = "Add";

	public static String OPERATION_UPDATE = "Replace";

	public static String OPERATION_DELETE = "Delete";
	
	public static String OPERATION_MAP = "Map";
	
	/**
	 * 
	 * 
	 */
	public SyncEngine()
	{
	}	
	//------Synchronization related services--------------------------------------------------------------------	
	public Vector getSlowSyncCommands(int messageSize, String storageId) throws SyncException
	{
		try
		{
			Vector commands = new Vector();

			//Getting all the records of the client since this is a SlowSync
			Vector allRecords = MobileObjectDatabase.getInstance().readAll(storageId);
			if(allRecords != null)
			{
				for (int i = 0,size=allRecords.size(); i < size; i++)
				{
					MobileObject mo = (MobileObject) allRecords.elementAt(i);
	
					// Create a Sync Add Command from this record data
					commands.addElement(this.getAddCommand(mo));
				}
			}
			
			return commands;
		}
		catch (Exception e)
		{
			throw new SyncException(this.getClass().getName(), "getSlowSyncCommands", new Object[]
			  {
				"StorageId=" + storageId,				
			  }
			);
		}
	}
		
	public Vector getAddCommands(int messageSize,String storageId, String syncType) throws SyncException
	{
		Vector changeLog = null;
		try
		{
			Vector commands = new Vector();

			changeLog = this.getChangeLog(storageId, SyncEngine.OPERATION_ADD);
			for (int i = 0,size=changeLog.size(); i < size; i++)
			{
				ChangeLogEntry entry = (ChangeLogEntry) changeLog.elementAt(i);
				MobileObject cour = MobileObjectDatabase.getInstance().read(storageId, entry.getRecordId());
				if (cour != null)
				{
					// Create a Sync Add Command from this record data				
					commands.addElement(this.getAddCommand(cour));
				}
			}

			return commands;
		}
		catch (Exception e)
		{
			StringBuffer buffer = new StringBuffer();
			if(changeLog != null)
			{
				for (int i = 0,size=changeLog.size(); i < size; i++)
				{
					ChangeLogEntry entry = (ChangeLogEntry) changeLog.elementAt(i);
					buffer.append(entry.getRecordId()+",");
				}
			}
			throw new SyncException(this.getClass().getName(), "getAddCommands", new Object[]
			  {
				"StorageId=" + storageId + ", SyncType="+syncType,
				"ChangeLogIds=" + buffer.toString()
			  }
			);
		}
	}
		
	public Vector getReplaceCommands(int messageSize,String storageId, String syncType)
			throws SyncException
	{
		Vector changeLog = null;
		try
		{
			Vector commands = new Vector();

			changeLog = this.getChangeLog(storageId,
					SyncEngine.OPERATION_UPDATE);
			for (int i = 0,size=changeLog.size(); i < size; i++)
			{
				ChangeLogEntry entry = (ChangeLogEntry) changeLog.elementAt(i);
				MobileObject cour = MobileObjectDatabase.getInstance().read(storageId, entry.getRecordId());
				if (cour != null)
				{
					// Create a Sync Add Command from this record data				
					commands.addElement(this.getReplaceCommand(cour));
				}
			}

			return commands;
		}
		catch (Exception e)
		{
			StringBuffer buffer = new StringBuffer();
			if(changeLog != null)
			{				
				for (int i = 0,size=changeLog.size(); i < size; i++)
				{
					ChangeLogEntry entry = (ChangeLogEntry) changeLog.elementAt(i);
					buffer.append(entry.getRecordId()+",");
				}
			}
			throw new SyncException(this.getClass().getName(), "getReplaceCommands", new Object[]
			  {
				"StorageId=" + storageId + ", SyncType="+syncType,
				"ChangeLogIds=" + buffer.toString()
			  }
			);
		}
	}
		
	public Vector getDeleteCommands(String storageId, String syncType)
			throws SyncException
	{
		Vector changeLog = null;
		try
		{
			Vector commands = new Vector();

			changeLog = this.getChangeLog(storageId,
					SyncEngine.OPERATION_DELETE);
			for (int i = 0,size=changeLog.size(); i < size; i++)
			{
				ChangeLogEntry entry = (ChangeLogEntry) changeLog.elementAt(i);
				Delete delete = new Delete();
				
				Item item = new Item();
				item.setData(this.marshalId(entry.getRecordId()));
				delete.getItems().addElement(item);
				
				commands.addElement(delete);
			}

			return commands;
		}
		catch (Exception e)
		{
			StringBuffer buffer = new StringBuffer();
			if(changeLog != null)
			{				
				for (int i = 0,size=changeLog.size(); i < size; i++)
				{
					ChangeLogEntry entry = (ChangeLogEntry) changeLog.elementAt(i);
					buffer.append(entry.getRecordId()+",");
				}
			}
			throw new SyncException(this.getClass().getName(), "getDeleteCommands", new Object[]
			  {
				"StorageId=" + storageId + ", SyncType="+syncType,
				"ChangeLogIds=" + buffer.toString()
			  }
			);
		}
	}
	
	public Vector processSlowSyncCommand(Session session,String storageId, SyncCommand syncCommand)
	throws SyncException
	{
		try
		{
			Vector status = new Vector();
			Vector allCommands = syncCommand.getAllCommands();
			
			
			for(int i=0,size=allCommands.size(); i<size; i++)
			{
				AbstractOperation command = (AbstractOperation)allCommands.elementAt(i);									
				/**
				 * Long Object Support
				 */
				if(command.isChunked())
				{
					continue;
				}
				
				Item item = (Item)command.getItems().elementAt(0);			
				if(command instanceof Add)
				{
					//Remove this item if it exists from the client database				
					//Add this item to the client database
					try
					{										
						MobileObject mobileObject = this.unmarshal(storageId,item.getData());
						this.deleteRecord(session, mobileObject);
						this.saveRecord(session, mobileObject);
						
						status.addElement(this.getStatus(SyncAdapter.SUCCESS, command));
					}
					catch (Exception e)
					{					
						status.addElement(this.getStatus(SyncAdapter.COMMAND_FAILURE, command));
					}
				}
				else if(command instanceof Delete)
				{
					//Remove this item if it exists from the client database
					try
					{
						MobileObject mobileObject = this.unmarshal(storageId,item.getData());
						this.deleteRecord(session, mobileObject);
						status.addElement(this.getStatus(SyncAdapter.SUCCESS, command));
					}
					catch (Exception e)
					{
						status.addElement(this.getStatus(SyncAdapter.COMMAND_FAILURE, command));
					}
				}
			}
	
			return status;
		}
		catch(Exception e)
		{
			throw new SyncException(this.getClass().getName(), "processSlowSyncCommand", new Object[]
			  {
				"StorageId=" + storageId				
			  }
			);
		}
	}
		
	public Vector processSyncCommand(Session session,String storageId, SyncCommand syncCommand)
	throws SyncException
	{
		try
		{
			Vector status = new Vector();		
			MobilePushInvocation invocation = session.getPushInvocation();
			if(invocation == null && session.isBackgroundSync())
			{
				invocation = new MobilePushInvocation("MobilePushInvocation");
				session.setPushInvocation(invocation);
			}
	
			// process Add commands
			Vector commands = syncCommand.getAddCommands();
			for (int i = 0,size=commands.size(); i < size; i++)
			{
				Add add = (Add) commands.elementAt(i);			
				
				//Make sure this is not chunked
				/**
				 * Long Object Support
				 */
				if(add.isChunked())
				{
					continue;
				}
				
				try
				{
					Item item = (Item) add.getItems().elementAt(0);			
					MobileObject mobileObject = this.unmarshal(storageId, item.getData());
					
					String objectId = this.saveRecord(session, mobileObject);
					
					if(invocation != null)
					{
						MobilePushMetaData metaData = new MobilePushMetaData(storageId, objectId);
						metaData.setAdded(true);
						invocation.addMobilePushMetaData(metaData);
					}
					
					status.addElement(this.getStatus(SyncAdapter.SUCCESS, add));
				}
				catch (Exception e)
				{				
					status.addElement(this.getStatus(SyncAdapter.COMMAND_FAILURE, add));
				}
			}
	
			// process Replace commands
			commands = syncCommand.getReplaceCommands();
			for (int i = 0,size=commands.size(); i < size; i++)
			{
				Replace replace = (Replace) commands.elementAt(i);			
				
				//Make sure this is not chunked
				/**
				 * Long Object Support
				 */
				if(replace.isChunked())
				{
					continue;
				}
				
				try
				{
					Item item = (Item) replace.getItems().elementAt(0);
					MobileObject mobileObject = this.unmarshal(storageId, item.getData());
					
					//With this strategy, in case of a conflict, the change on the client
					//wins over the change on the server				
					String objectId = this.saveRecord(session, mobileObject);
					
					if(invocation != null)
					{
						MobilePushMetaData metaData = new MobilePushMetaData(storageId, objectId);
						metaData.setUpdated(true);
						invocation.addMobilePushMetaData(metaData);
					}
					
					status.addElement(this.getStatus(SyncAdapter.SUCCESS, replace));
				}
				catch (Exception e)
				{
					status.addElement(this.getStatus(SyncAdapter.COMMAND_FAILURE,replace));
				}
			}
	
			// process Delete commands
			commands = syncCommand.getDeleteCommands();
			for (int i = 0,size=commands.size(); i < size; i++)
			{
				Delete delete = (Delete) syncCommand.getDeleteCommands().elementAt(i);			
	
				//Make sure this is not chunked
				/**
				 * Long Object Support
				 */
				if(delete.isChunked())
				{
					continue;
				}
										
				try
				{				
					Item item = (Item) delete.getItems().elementAt(0);
					MobileObject mobileObject = this.unmarshal(storageId, item.getData());
					
					this.deleteRecord(session, mobileObject);
					
					if(invocation != null)
					{
						MobilePushMetaData metaData = new MobilePushMetaData(storageId, mobileObject.getRecordId());
						metaData.setDeleted(true);
						invocation.addMobilePushMetaData(metaData);
					}
					
					status.addElement(this.getStatus(SyncAdapter.SUCCESS, delete));
				}
				catch (Exception e)
				{
					status.addElement(this.getStatus(SyncAdapter.COMMAND_FAILURE, delete));
				}
			}
			
			return status;
		}
		catch(Exception e)
		{
			throw new SyncException(this.getClass().getName(), "processSyncCommand", new Object[]
			  {
				"StorageId=" + storageId				
			  }
			);
		}
	}	
	
	public void startBootSync(Session session,String storageId) throws SyncException
	{	
		try
		{
			//Clear the on device data
			this.clearAll(session, storageId);
			
			//Clear the on device changelog
			this.clearChangeLog(storageId);
		}
		catch(Exception e)
		{
			throw new SyncException(this.getClass().getName(), "startBootSync", new Object[]
			  {
				"StorageId=" + storageId				
			  }
			);
		}
	}		
	//-------ChangeLog Support---------------------------------------------------------------------------------------------------------------------------	
	public Vector getChangeLog(String nodeId, String operation) throws SyncException
	{
		try
		{
			Vector cour = new Vector();
			
			Vector changeLog = SyncDataSource.getInstance().readChangeLog();
			if(changeLog != null)
			{
				for(int i=0,size=changeLog.size(); i<size; i++)
				{
					ChangeLogEntry entry = (ChangeLogEntry)changeLog.elementAt(i);
					if(entry.getNodeId().equals(nodeId) && entry.getOperation().equals(operation))
					{
						cour.addElement(entry);
					}
				}
			}
			
			return cour;
		}
		catch(Exception dbe)
		{
			throw new SyncException(this.getClass().getName(), "getChangeLog", new Object[]{
				"NodeId="+nodeId,
				"Operation="+operation,
				"Database Error="+dbe.getMessage()
			});
		}
	}
		
	public void addChangeLogEntries(Vector entries) throws SyncException
	{
		try
		{
			SyncDataSource.getInstance().createChangeLogEntries(entries);
		}
		catch(Exception dbe)
		{
			throw new SyncException(this.getClass().getName(), "addChangeLogEntries", new Object[]{
				"Database Error="+dbe.getMessage()
			});
		}
	}
		
	public void clearChangeLogEntry(ChangeLogEntry logEntry) throws SyncException
	{
		try
		{
			SyncDataSource.getInstance().deleteChangeLogEntry(logEntry);
		}
		catch(Exception dbe)
		{
			throw new SyncException(this.getClass().getName(), "clearChangeLogEntry", new Object[]{
				"ChangeLogEntry="+logEntry.toString(),				
				"Database Error="+dbe.getMessage()
			});
		}
	}
		
	public void clearChangeLog() throws SyncException
	{
		try
		{
			SyncDataSource.getInstance().deleteChangeLog();
		}
		catch(Exception dbe)
		{
			throw new SyncException(this.getClass().getName(), "clearChangeLog", new Object[]{
				"Database Error="+dbe.getMessage()
			});
		}
	}
	
	public void clearChangeLog(String service) throws SyncException
	{
		try
		{			
			Vector changeLog = SyncDataSource.getInstance().readChangeLog();
			if(changeLog != null)
			{
				for(int i=0,size=changeLog.size(); i<size; i++)
				{
					ChangeLogEntry entry = (ChangeLogEntry)changeLog.elementAt(i);
					if(entry.getNodeId().equals(service))
					{
						this.clearChangeLogEntry(entry);
					}
				}
			}
		}
		catch(Exception dbe)
		{
			throw new SyncException(this.getClass().getName(), "getChangeLog", new Object[]{
				"NodeId="+service,
				"Database Error="+dbe.getMessage()
			});
		}
	}
	//---Anchor Management-----------------------------------------------------------------------------------------------------------------------------------	
	public Anchor createNewAnchor(String target) throws SyncException
	{
		try
		{
			Anchor currentAnchor = SyncDataSource.getInstance().readAnchor();
			
			if (currentAnchor != null)
			{	
				// Calculate next sync
				String nextSync = this.generateSync();
				currentAnchor.setNextSync(nextSync);
			}
			else
			{
				// This is the first time the anchor is established for the
				// target
				currentAnchor = new Anchor();
				currentAnchor.setTarget(target);
	
				// Calculate the last sync
				String lastSync = this.generateSync();
				currentAnchor.setLastSync(lastSync);
	
				// Calculate the next sync
				String nextSync = lastSync;
				currentAnchor.setNextSync(nextSync);
			}
			
			//Persist the new anchor
			this.updateAnchor(currentAnchor);
	
			return currentAnchor;
		}
		catch(Exception dbe)
		{
			throw new SyncException(this.getClass().getName(), "createNewAnchor", new Object[]{
				"Target="+target,
				"Database Error="+dbe.getMessage()
			});
		}
	}
			
	private void updateAnchor(Anchor anchor) throws SyncException
	{
		try
		{
			Anchor update = new Anchor();
			
			update.setId(anchor.getId());
			update.setTarget(anchor.getTarget());
			
			//The sync was successful so swap last sync value that should be used when the next sync will be done
			update.setLastSync(anchor.getNextSync());
			update.setNextSync(anchor.getNextSync());			
			
			SyncDataSource.getInstance().saveAnchor(update);
		}
		catch(Exception dbe)
		{
			throw new SyncException(this.getClass().getName(), "updateAnchor", new Object[]{
				"Database Error="+dbe.getMessage()
			});
		}
	}
		
	public String generateSync() throws SyncException
	{
		try
		{
			String sync = null;
	
			sync = String.valueOf(GeneralTools.generateUniqueId());
	
			return sync;
		}
		catch(Exception e)
		{
			throw new SyncException(this.getClass().getName(), "generateSync", new Object[]
			  {
				"Exception="+e.toString(),
				"Message="+e.getMessage()
			  }
			);
		}
	}
	//-------Map Support--------------------------------------------------------------------------------------------------------------------------------------	
	/**
	 * This comes into play only when Map synchronization between the client and the server fails
	 * within a sync session
	 * 
	 * @param source
	 * @param target
	 * @param map
	 */
	public void saveRecordMap(String source, String target, Hashtable map) throws SyncException
	{
		try
		{
			SyncDataSource.getInstance().saveRecordMap(source, target, map);
		}
		catch(Exception dbe)
		{
			throw new SyncException(this.getClass().getName(), "saveRecordMap", new Object[]{
				"Source="+source,
				"Target="+target,
				"Database Error="+dbe.getMessage()
			});
		}
	}
		
	public Hashtable readRecordMap(String source, String target) throws SyncException
	{
		try
		{
			return SyncDataSource.getInstance().readRecordMap(source, target);
		}
		catch(Exception dbe)
		{
			throw new SyncException(this.getClass().getName(), "readRecordMap", new Object[]{
				"Source="+source,
				"Target="+target,
				"Database Error="+dbe.getMessage()
			});
		}
	}
	
	/**
	 * Cleans up the device record map once Map information is successfully processed by the server
	 * during a sync session 
	 * 
	 * @param source
	 * @param target
	 */
	public void removeRecordMap(String source, String target) throws SyncException
	{
		try
		{
			SyncDataSource.getInstance().removeRecordMap(source, target);
		}
		catch(Exception dbe)
		{
			throw new SyncException(this.getClass().getName(), "removeRecordMap", new Object[]{
				"Source="+source,
				"Target="+target,
				"Database Error="+dbe.getMessage()
			});
		}
	}
	//---Error Support------------------------------------------------------------------------------------------------------------------------------------	
	public void saveError(SyncError error) throws SyncException
	{
		try
		{
			SyncDataSource.getInstance().saveError(error);
		}
		catch(Exception dbe)
		{
			throw new SyncException(this.getClass().getName(), "saveError", new Object[]{				
				"Database Error="+dbe.getMessage()
			});
		}
	}
		
	public SyncError readError(String source, String target, String code) throws SyncException
	{
		try
		{
			SyncError error = SyncDataSource.getInstance().readError(source, target, code);
			return error;
		}
		catch(Exception dbe)
		{
			throw new SyncException(this.getClass().getName(), "saveError", new Object[]{
				"Source="+source,
				"Target="+target,
				"Code="+code,
				"Database Error="+dbe.getMessage()
			});
		}
	}
		
	public void removeError(String source, String target, String code) throws SyncException
	{
		try
		{
			SyncDataSource.getInstance().removeError(source, target, code);
		}
		catch(Exception dbe)
		{
			throw new SyncException(this.getClass().getName(), "removeError", new Object[]{
				"Source="+source,
				"Target="+target,
				"Code="+code,
				"Database Error="+dbe.getMessage()
			});
		}
	}
	//-------------------------------------------------------------------------------------------------------------------------------------------------------
	private String saveRecord(Session session, MobileObject mobileObject)
	{
		String recordId = mobileObject.getRecordId();
		MobileObject mo = MobileObjectDatabase.getInstance().read(mobileObject.getStorageId(), recordId);
		if(mo != null)
		{
			//Update
			mobileObject.setRecordId(mo.getRecordId());
			MobileObjectDatabase.getInstance().update(mobileObject);
		}
		else
		{
			//Create
			String serverId = recordId;
			String id = MobileObjectDatabase.getInstance().create(mobileObject);
			MobileObject newlyAdded = MobileObjectDatabase.getInstance().read(mobileObject.getStorageId(), id);
			String deviceId = newlyAdded.getRecordId();
			recordId = deviceId;
			if(serverId != null && deviceId != null)
			{
				if(!deviceId.equals(serverId))
				{
					session.getRecordMap().put(serverId, deviceId);
				}
			}
		}
		
		return recordId;
	}
	
	private void deleteRecord(Session session, MobileObject mobileObject)
	{
		String recordId = mobileObject.getRecordId();
		MobileObject objectToDelete = MobileObjectDatabase.getInstance().read(mobileObject.getStorageId(), recordId);
		if(objectToDelete != null)
		{
			MobileObjectDatabase.getInstance().delete(objectToDelete);
		}				
	}	
	//------------------------------------------------------------------------------------------------------------------------------------------------------
	private AbstractOperation getAddCommand(MobileObject mobileObject) throws SyncException
	{
		AbstractOperation commandInfo = null;

		commandInfo = new Add();		

		Item item = new Item();
		item.setData(this.marshal(mobileObject));

		commandInfo.getItems().addElement(item);

		return commandInfo;
	}
	
	private AbstractOperation getReplaceCommand(MobileObject mobileObject) throws SyncException
	{
		AbstractOperation commandInfo = null;

		commandInfo = new Replace();		

		Item item = new Item();
		item.setData(this.marshal(mobileObject));

		commandInfo.getItems().addElement(item);

		return commandInfo;
	}
	
	private String marshalId(String id) throws SyncException
	{
		String xml = null;
		
		StringBuffer buffer = new StringBuffer();	
		
		buffer.append("<mobileObject>\n");		
		buffer.append("<recordId>"+XMLUtil.cleanupXML(id)+"</recordId>\n");		
		buffer.append("</mobileObject>\n");

		
		xml = buffer.toString();
		
		return xml;
	}
	
	private String marshal(MobileObject mobileObject) throws SyncException
	{
		try
		{
			String recordXml = null;
			StringBuffer buffer = new StringBuffer();
			
			buffer.append(DeviceSerializer.getInstance().serialize(mobileObject));
			
			recordXml = buffer.toString();
			return recordXml;
		}
		catch(Exception e)
		{
			throw new SyncException(this.getClass().getName(), "marshal", new Object[]
	              {
					DeviceSerializer.getInstance().serialize(mobileObject)
	              }
			);
		}
	}	
	
	public MobileObject unmarshal(String storageId, String xml) throws SyncException
	{
		try
		{
			MobileObject mobileObject = DeviceSerializer.getInstance().
			deserialize(xml);
			
			mobileObject.setStorageId(storageId);									
												
			return mobileObject;
		}
		catch(Exception e)
		{
			if(e instanceof SyncException)
			{
				throw (SyncException)e;
			}
			else
			{
				throw new SyncException(this.getClass().getName(), "unmarshal", new Object[]
		           {
						storageId,
						xml,
						e.getMessage()
		           }
				);
			}
		}
	}
	//-----Miscellaneous services-----------------------------------------------------------------------------------------------------------------------------------------------------
	public void clearAll(Session session,String storageId) throws SyncException
	{
		try
		{
			MobileObjectDatabase.getInstance().deleteAll(storageId);		
			
			//TODO: cleanup all local mappings....
			//not urgent...the object sync technology does not use the
			//mapping feature of the sync engine, yet
		}
		catch(Exception e)
		{
			throw new SyncException(this.getClass().getName(), "clearAll", new Object[]
			  {
				"StorageId=" + storageId,
				"Exception="+e.toString(),
				"Message="+e.getMessage()
			  }
			);
		}
	}
		
	private Status getStatus(String statusCode, AbstractOperation operation)
	{
		Status status = new Status();

		if (operation instanceof Add)
		{
			status.setCmd(SyncXMLTags.Add);
		}
		else if (operation instanceof Replace)
		{
			status.setCmd(SyncXMLTags.Replace);
		}
		else if (operation instanceof Delete)
		{
			status.setCmd(SyncXMLTags.Delete);
		}

		status.setData(statusCode);
		status.setCmdRef(operation.getCmdId());
		Item item = (Item) operation.getItems().elementAt(0);

		if (item.getSource() != null && item.getSource().trim().length() > 0)
		{
			status.getSourceRefs().addElement(item.getSource());
		}

		return status;
	}	
}
