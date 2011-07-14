/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.server;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import org.openmobster.core.common.ServiceManager;

import org.openmobster.core.security.Provisioner;
import org.openmobster.core.security.device.DeviceController;
import org.openmobster.core.security.identity.IdentityController;

import org.openmobster.core.synchronizer.server.SyncServer;
import org.openmobster.core.synchronizer.server.engine.ChangeLogEntry;
import org.openmobster.core.synchronizer.server.engine.ServerSyncEngine;

import org.openmobster.core.dataService.processor.Input;
import org.openmobster.core.dataService.processor.Processor;
import org.openmobster.core.dataService.processor.ProcessorException;
import org.openmobster.core.dataService.processor.SyncProcessor;

import test.openmobster.device.agent.sync.server.Email;
import test.openmobster.device.agent.sync.server.EmailConnector;
import test.openmobster.device.agent.sync.server.SyncServerAdapterWithMapErrorSimulation;
import test.openmobster.device.agent.sync.server.ServerRecord;
import test.openmobster.device.agent.sync.server.ServerRecordController;

import test.openmobster.device.agent.api.TicketConnector;

import org.openmobster.core.common.transaction.TransactionHelper;


/**
 * @author openmobster@gmail.com
 */
public class MockServer implements Processor
{
	private static Logger log = Logger.getLogger(MockServer.class);
	
	private String id;
		
	private ServerRecordController serverController = null;		
	private ServerSyncEngine serverSyncEngine = null;		
	private SyncServer originalAdapter = null;
	private SyncServer adapterWithErrors = null;
			
	private String deviceId = "IMEI:8675309";	
	private String service = "testServerBean";
	private String serviceWithMapping = "testServerBean.testMapping";
	
	private byte[] attachment = "blahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblah".getBytes();
	
	private DeviceController deviceController;
	private IdentityController identityController;
	private Provisioner provisioner;
	
	
	public ServerSyncEngine getServerSyncEngine()
	{
		return serverSyncEngine;
	}

	
	public void setServerSyncEngine(ServerSyncEngine serverSyncEngine)
	{
		this.serverSyncEngine = serverSyncEngine;
	}
	
	
	public SyncServer getOriginalAdapter() 
	{
		return originalAdapter;
	}

	
	public void setOriginalAdapter(SyncServer syncAdapter) 
	{
		this.originalAdapter = syncAdapter;
	}
	
			
	public SyncServer getAdapterWithErrors() 
	{
		return adapterWithErrors;
	}


	public void setAdapterWithErrors(SyncServer adapterWithErrors) 
	{
		this.adapterWithErrors = adapterWithErrors;
	}
	
	
	public DeviceController getDeviceController() 
	{
		return deviceController;
	}


	public void setDeviceController(DeviceController deviceController) 
	{
		this.deviceController = deviceController;
	}


	public IdentityController getIdentityController() 
	{
		return identityController;
	}


	public void setIdentityController(IdentityController identityController) 
	{
		this.identityController = identityController;
	}
	
	public Provisioner getProvisioner() 
	{
		return provisioner;
	}

	public void setProvisioner(Provisioner provisioner) 
	{
		this.provisioner = provisioner;
	}


	public void start()
	{		
		try
		{
			TransactionHelper.startTx();
			
			//Harness for testing Agent Provisioning related test cases
			if(this.provisioner.getIdentityController().read("blah2@gmail.com") == null)
			{
				this.provisioner.registerIdentity("blah2@gmail.com", "blahblah2");
			}
			
			log.info("------------------------------------------------------");
			log.info("MockServer successfully loaded for the server side testsuite.....");			
			log.info("------------------------------------------------------");
			
			TransactionHelper.commitTx();
		}
		catch(Exception e)
		{
			log.error(this, e);
			
			TransactionHelper.rollbackTx();
			
			throw new RuntimeException(e);
		}
	}
	
	
	public void stop()
	{		
	}
	
	
	private void setUpServerData()
	{
		//SetUp the Backend data for the "testRecordConnector" test service of the test suite
		this.serverController = ServerRecordController.getInstance();		
		
		this.serverController.deleteAll();
		
		for(int i=1; i<3; i++)
		{
			ServerRecord serverData = new ServerRecord();
			serverData.setObjectId("unique-"+i);
			serverData.setFrom(i+"@from.com");
			serverData.setTo(i+"@to.com");
			serverData.setSubject(i+"/Subject");
			serverData.setMessage("<tag apos='apos' quote=\"quote\" ampersand='&'>"+i+"/Message"+"</tag>");	
			serverData.setAttachment(this.attachment);
			this.serverController.create(serverData);
		}
	}
	
	private void cleanupServerSideState()
	{
		SyncProcessor syncProcessor = (SyncProcessor)ServiceManager.locate("sync");			
		syncProcessor.setSyncAdapter(this.originalAdapter);
		this.serverSyncEngine.clearChangeLog(this.deviceId, this.service);
		this.serverSyncEngine.deleteAnchor(this.deviceId+"/"+this.service);
		this.serverSyncEngine.deleteAnchor(this.deviceId+"/"+this.serviceWithMapping);
		this.serverSyncEngine.deleteAnchor(this.deviceId+"/emailConnector");
		this.serverSyncEngine.clearRecordMap();
		this.serverSyncEngine.clearConflictEngine();
		
		EmailConnector.close();											
	}
	
	/**
	 * This is to be used only in test suite running mode..This is used for setting up
	 * proper server side of the Server Sync Adapter in the context of the test being
	 * executed between the client and the server
	 * 
	 * @param info
	 */
	public void setUp(String info)
	{				
		String operation = info.substring(info.lastIndexOf('/')+1);
		
		if(info.contains("CleanUp"))
		{
			this.cleanupServerSideState();
			return;
		}
		
		if(info.contains("Errors") || info.contains("ErrorHandling"))
		{
			this.setUpErrorHarness(info);
			return;
		}	
		
		if(info.contains("Notification"))
		{
			this.setUpNotification(info);
			return;
		}
		
		if(info.contains("SetUpAPITestSuite"))
		{
			this.setUpAPITestSuite();
			return;
		}
		
		String serverNodeId = this.service;		
		if(info.contains(this.serviceWithMapping))
		{
			serverNodeId = this.serviceWithMapping;
		}
		
		
		this.setUpServerData();
				
		if(info.contains("SlowSync"))
		{
			if(operation.equals("add"))
			{
				//Making sure 'unique-1' and 'unique-2' as added are reflected in the
				//ChangeLog
				List serverChangeLog = new ArrayList();
				for(int i=1; i<3; i++)
				{
					ChangeLogEntry serverEntry = new ChangeLogEntry();
					serverEntry.setNodeId(serverNodeId);
					serverEntry.setOperation(ServerSyncEngine.OPERATION_ADD);
					serverEntry.setRecordId("unique-"+String.valueOf(i));
					serverChangeLog.add(serverEntry);
				}
				this.serverSyncEngine.addChangeLogEntries(this.deviceId, serverChangeLog);
			}
			else if(operation.equals("conflict"))
			{
				//Updating 'unique-1' on the server
				List serverChangeLog = new ArrayList();
				for(int i=1; i<2; i++)
				{
					String recordId = "unique-"+String.valueOf(i);
					ServerRecord record = this.serverController.readServerRecord(recordId);
					record.setMessage("<tag apos='apos' quote=\"quote\" ampersand='&'>"+recordId+"/Updated/Server"+"</tag>");
					this.serverController.save(record);
					
					ChangeLogEntry serverEntry = new ChangeLogEntry();
					serverEntry.setNodeId(serverNodeId);
					serverEntry.setOperation(ServerSyncEngine.OPERATION_UPDATE);
					serverEntry.setRecordId(recordId);
					serverChangeLog.add(serverEntry);
				}
				this.serverSyncEngine.addChangeLogEntries(this.deviceId, serverChangeLog);
			}
			else if(operation.equals("multirecord"))
			{				
				//also add unique-5, unique-6, and unique-7
				for(int i=5; i<8; i++)
				{
					String uniqueId = "unique-" + String.valueOf(i);
					ServerRecord serverData = new ServerRecord();
					serverData.setObjectId(uniqueId);
					serverData.setFrom(uniqueId+"@from.com");
					serverData.setTo(uniqueId+"@to.com");
					serverData.setSubject(uniqueId+"/Subject");
					serverData.setMessage("<tag apos='apos' quote=\"quote\" ampersand='&'>"+uniqueId+"/Message"+"</tag>");
					this.serverController.save(serverData);					
				}				
			}
		}				
		else if(info.contains("OneWayClientSync") || 
		   info.contains("OneWayServerSync") || 
		   info.contains("TwoWaySync") ||
		   info.contains("ChangeLogSupport") ||
		   info.contains("LongObjectSupport") ||
		   info.contains("AnchorSupport") ||
		   info.contains("ObjectStreaming") ||
		   info.contains("BootSync")
		)
		{
			if(operation.equals("add"))
			{
				//Making sure 'unique-1' and 'unique-2' as added are reflected in the
				//ChangeLog
				List serverChangeLog = new ArrayList();
				for(int i=1; i<3; i++)
				{
					ChangeLogEntry serverEntry = new ChangeLogEntry();
					serverEntry.setNodeId(serverNodeId);
					serverEntry.setOperation(ServerSyncEngine.OPERATION_ADD);
					serverEntry.setRecordId("unique-"+String.valueOf(i));
					serverChangeLog.add(serverEntry);
				}
				this.serverSyncEngine.addChangeLogEntries(this.deviceId, serverChangeLog);
			}
			else if(operation.equals("replace") || operation.equals("conflict"))
			{
				//Updating 'unique-1' on the server
				List serverChangeLog = new ArrayList();
				for(int i=1; i<2; i++)
				{
					String recordId = "unique-"+String.valueOf(i);
					ServerRecord record = this.serverController.readServerRecord(recordId);
					record.setMessage("<tag apos='apos' quote=\"quote\" ampersand='&'>"+recordId+"/Updated/Server"+"</tag>");
					this.serverController.save(record);
					
					ChangeLogEntry serverEntry = new ChangeLogEntry();
					serverEntry.setNodeId(serverNodeId);
					serverEntry.setOperation(ServerSyncEngine.OPERATION_UPDATE);
					serverEntry.setRecordId(recordId);
					serverChangeLog.add(serverEntry);
				}
				this.serverSyncEngine.addChangeLogEntries(this.deviceId, serverChangeLog);
			}
			else if(operation.equals("delete"))
			{
				//Deleting just 'unique-1' from the server
				List serverChangeLog = new ArrayList();
				for(int i=1; i<2; i++)
				{	
					String recordId = "unique-"+String.valueOf(i);
					ServerRecord record = this.serverController.readServerRecord(recordId);
					this.serverController.delete(record);
					
					ChangeLogEntry serverEntry = new ChangeLogEntry();
					serverEntry.setNodeId(serverNodeId);
					serverEntry.setOperation(ServerSyncEngine.OPERATION_DELETE);
					serverEntry.setRecordId(recordId);
					serverChangeLog.add(serverEntry);
				}
				this.serverSyncEngine.addChangeLogEntries(this.deviceId, serverChangeLog);
			}
			else if(operation.equals("multirecord"))
			{
				//Making sure 'unique-1' and 'unique-2' as added are reflected in the
				//ChangeLog
				List serverChangeLog = new ArrayList();
				for(int i=1; i<3; i++)
				{
					ChangeLogEntry serverEntry = new ChangeLogEntry();
					serverEntry.setNodeId(serverNodeId);
					serverEntry.setOperation(ServerSyncEngine.OPERATION_ADD);
					serverEntry.setRecordId("unique-"+String.valueOf(i));
					serverChangeLog.add(serverEntry);
				}				
				
				//also add unique-5, unique-6, and unique-7
				for(int i=5; i<8; i++)
				{
					String uniqueId = "unique-" + String.valueOf(i);
					ServerRecord serverData = new ServerRecord();
					serverData.setObjectId(uniqueId);
					serverData.setFrom(uniqueId+"@from.com");
					serverData.setTo(uniqueId+"@to.com");
					serverData.setSubject(uniqueId+"/Subject");
					serverData.setMessage("<tag apos='apos' quote=\"quote\" ampersand='&'>"+uniqueId+"/Message"+"</tag>");
					this.serverController.save(serverData);
					
					ChangeLogEntry serverEntry = new ChangeLogEntry();
					serverEntry.setNodeId(serverNodeId);
					serverEntry.setOperation(ServerSyncEngine.OPERATION_ADD);
					serverEntry.setRecordId("unique-"+String.valueOf(i));
					serverChangeLog.add(serverEntry);
				}
				
				//Updating 'unique-1' on the server
				for(int i=1; i<2; i++)
				{
					String recordId = "unique-"+String.valueOf(i);
					ServerRecord record = this.serverController.readServerRecord(recordId);
					record.setMessage("<tag apos='apos' quote=\"quote\" ampersand='&'>"+recordId+"/Updated/Server"+"</tag>");
					this.serverController.save(record);
					
					ChangeLogEntry serverEntry = new ChangeLogEntry();
					serverEntry.setNodeId(serverNodeId);
					serverEntry.setOperation(ServerSyncEngine.OPERATION_UPDATE);
					serverEntry.setRecordId(recordId);
					serverChangeLog.add(serverEntry);
				}				
				this.serverSyncEngine.addChangeLogEntries(this.deviceId, serverChangeLog);
			}
		}				
	}	
	
	private void setUpErrorHarness(String info)
	{				
		this.serverSyncEngine.clearChangeLog(this.deviceId, this.service);				
		String serverNodeId = this.service;		
		if(info.contains(this.serviceWithMapping))
		{
			serverNodeId = this.serviceWithMapping;
		}
				
		
		if(info.endsWith("deferMapUpdateToNextSync"))
		{
			List serverChangeLog = new ArrayList();
			this.serverController = ServerRecordController.getInstance();					
			this.serverController.deleteAll();
			for(int i=5; i<7; i++)
			{
				String uniqueId = "unique-" + String.valueOf(i);
				ServerRecord serverData = new ServerRecord();
				serverData.setObjectId(uniqueId);
				serverData.setFrom(uniqueId+"@from.com");
				serverData.setTo(uniqueId+"@to.com");
				serverData.setSubject(uniqueId+"/Subject");
				serverData.setMessage("<tag apos='apos' quote=\"quote\" ampersand='&'>"+uniqueId+"/Message"+"</tag>");
				this.serverController.save(serverData);
				
				ChangeLogEntry serverEntry = new ChangeLogEntry();
				serverEntry.setNodeId(serverNodeId);
				serverEntry.setOperation(ServerSyncEngine.OPERATION_ADD);
				serverEntry.setRecordId("unique-"+String.valueOf(i));
				serverChangeLog.add(serverEntry);
			}
			this.serverSyncEngine.addChangeLogEntries(this.deviceId, serverChangeLog);
			
			//swap serverSyncAdapter with the one that simulates errors
			SyncServer serverSyncAdapter = new SyncServerAdapterWithMapErrorSimulation(this.serverSyncEngine);
			((SyncServerAdapterWithMapErrorSimulation)serverSyncAdapter).
			activateProcessRecordMapFailure = true;
			
			SyncProcessor syncProcessor = (SyncProcessor)ServiceManager.locate("sync");			
			syncProcessor.setSyncAdapter(serverSyncAdapter);
		}
		else if(info.endsWith("deferMapUpdateToNextSyncFailure"))
		{
			List serverChangeLog = new ArrayList();
			this.serverController = ServerRecordController.getInstance();					
			this.serverController.deleteAll();
			for(int i=5; i<7; i++)
			{
				String uniqueId = "unique-" + String.valueOf(i);
				ServerRecord serverData = new ServerRecord();
				serverData.setObjectId(uniqueId);
				serverData.setFrom(uniqueId+"@from.com");
				serverData.setTo(uniqueId+"@to.com");
				serverData.setSubject(uniqueId+"/Subject");
				serverData.setMessage("<tag apos='apos' quote=\"quote\" ampersand='&'>"+uniqueId+"/Message"+"</tag>");
				this.serverController.save(serverData);
				
				ChangeLogEntry serverEntry = new ChangeLogEntry();
				serverEntry.setNodeId(serverNodeId);
				serverEntry.setOperation(ServerSyncEngine.OPERATION_ADD);
				serverEntry.setRecordId("unique-"+String.valueOf(i));
				serverChangeLog.add(serverEntry);
			}
			this.serverSyncEngine.addChangeLogEntries(this.deviceId, serverChangeLog);
			
			//swap serverSyncAdapter with the one that simulates errors
			SyncServer serverSyncAdapter = new SyncServerAdapterWithMapErrorSimulation(this.serverSyncEngine);
			((SyncServerAdapterWithMapErrorSimulation)serverSyncAdapter).
			activateProcessRecordMapFailure = true;
			((SyncServerAdapterWithMapErrorSimulation)serverSyncAdapter).
			activateDeferMapUpdateToNextSyncFailure= true;
			
			SyncProcessor syncProcessor = (SyncProcessor)ServiceManager.locate("sync");			
			syncProcessor.setSyncAdapter(serverSyncAdapter);
		}
		else if(info.endsWith("deferMapUpdateToNextSyncClientPersistFailure"))
		{
			List serverChangeLog = new ArrayList();
			this.serverController = ServerRecordController.getInstance();					
			this.serverController.deleteAll();
			for(int i=5; i<7; i++)
			{
				String uniqueId = "unique-" + String.valueOf(i);
				ServerRecord serverData = new ServerRecord();
				serverData.setObjectId(uniqueId);
				serverData.setFrom(uniqueId+"@from.com");
				serverData.setTo(uniqueId+"@to.com");
				serverData.setSubject(uniqueId+"/Subject");
				serverData.setMessage("<tag apos='apos' quote=\"quote\" ampersand='&'>"+uniqueId+"/Message"+"</tag>");
				this.serverController.save(serverData);
				
				ChangeLogEntry serverEntry = new ChangeLogEntry();
				serverEntry.setNodeId(serverNodeId);
				serverEntry.setOperation(ServerSyncEngine.OPERATION_ADD);
				serverEntry.setRecordId("unique-"+String.valueOf(i));
				serverChangeLog.add(serverEntry);
			}
			this.serverSyncEngine.addChangeLogEntries(this.deviceId, serverChangeLog);
			
			SyncProcessor syncProcessor = (SyncProcessor)ServiceManager.locate("sync");			
			syncProcessor.setSyncAdapter(this.originalAdapter);
		}
		else if(info.contains("ServerSideErrorHandling"))
		{			
			this.setUpServerData();
			List serverChangeLog = new ArrayList();
			for(int i=1; i<3; i++)
			{
				ChangeLogEntry serverEntry = new ChangeLogEntry();
				serverEntry.setNodeId(serverNodeId);
				serverEntry.setOperation(ServerSyncEngine.OPERATION_ADD);
				serverEntry.setRecordId("unique-"+String.valueOf(i));
				serverChangeLog.add(serverEntry);
			}
			this.serverSyncEngine.addChangeLogEntries(this.deviceId, serverChangeLog);
			
			SyncProcessor syncProcessor = (SyncProcessor)ServiceManager.locate("sync");			
			syncProcessor.setSyncAdapter(this.adapterWithErrors);
		}		
		else
		{
			this.setUpServerData();
			List serverChangeLog = new ArrayList();
			for(int i=1; i<3; i++)
			{
				ChangeLogEntry serverEntry = new ChangeLogEntry();
				serverEntry.setNodeId(serverNodeId);
				serverEntry.setOperation(ServerSyncEngine.OPERATION_ADD);
				serverEntry.setRecordId("unique-"+String.valueOf(i));
				serverChangeLog.add(serverEntry);
			}
			this.serverSyncEngine.addChangeLogEntries(this.deviceId, serverChangeLog);
		}
	}
	
	private void setUpNotification(String info)
	{				
		if(info.contains("/sendNewEmail/"))
		{
			//Add a new email as if an new email was received from the internet
			//Out of Band addition
			String newEmailUid = info.substring(info.lastIndexOf('/')+1);
			Email email = new Email(
					newEmailUid,
					"from@blah.com",
					"to@blah.com",
					"newSubject",
					"new message"
			);
			EmailConnector.add(this.deviceId, email);
		}
		else
		{
			EmailConnector.initialize(this.deviceId);
		}
		
		if(info.contains("org.openmobster.core.mobileCloud.api.TestMobilePushNotification"))
		{
			System.out.println("Resetting TicketConnector Push Status......");
			TicketConnector.resetPush();			
		}
	}
	private void setUpAPITestSuite()
	{
		String message = "<tag apos=''apos'' quote=\"quote\" ampersand=''&''>{0}/Message</tag>";
		String subject = "This is the subject<html><body>{0}</body></html>";
		ServerRecordController controller = ServerRecordController.getInstance();
		
		controller.deleteAll();
		
		ServerRecord serverData = new ServerRecord();
		serverData.setObjectId("unique-1");
		serverData.setFrom("from@gmail.com");
		serverData.setTo("to@gmail.com");
		serverData.setSubject(MessageFormat.format(subject,new Object[]{serverData.getObjectId()}));
		serverData.setMessage(MessageFormat.format(message,new Object[]{serverData.getObjectId()}));
		serverData.setAttachment(this.attachment);
		controller.create(serverData);

		serverData = new ServerRecord();
		serverData.setObjectId("unique-2");
		serverData.setFrom("from@gmail.com");
		serverData.setTo("to@gmail.com");
		serverData.setSubject(MessageFormat.format(subject,new Object[]{serverData.getObjectId()}));
		serverData.setMessage(MessageFormat.format(message,new Object[]{serverData.getObjectId()}));
		serverData.setAttachment(this.attachment);
		controller.create(serverData);
	}
	//------Processor implementation---------------------------------------------------------------------------------------------------------------------------------
	public String getId()
	{
		return this.id;
	}
	
	public void setId(String id)
	{
		this.id = id;
	}

	public String process(Input input) throws ProcessorException 
	{
		String payload = input.getMessage();
		
		this.setUp(payload);
		
		return null;
	}	
}
