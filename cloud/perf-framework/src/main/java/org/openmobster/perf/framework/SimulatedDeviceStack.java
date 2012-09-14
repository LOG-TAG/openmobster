/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

/**
 * 
 */
package org.openmobster.perf.framework;

import java.io.InputStream;
import org.w3c.dom.Document;

import org.apache.log4j.Logger;

import org.openmobster.core.security.identity.IdentityController;
import org.openmobster.core.security.identity.GroupController;
import org.openmobster.core.security.device.DeviceController;
import org.openmobster.core.security.Provisioner;
import org.openmobster.core.common.event.EventManager;
import org.openmobster.core.common.validation.ObjectValidator;

import org.openmobster.device.agent.frameworks.mobileObject.StorageMonitor;
import org.openmobster.device.agent.frameworks.mobileObject.MobileObjectDatabase;
import org.openmobster.device.agent.service.database.Database;
import org.openmobster.device.agent.sync.engine.SyncDataSource;
import org.openmobster.device.agent.sync.engine.SyncEngine;
import org.openmobster.device.agent.sync.SyncService;
import org.openmobster.device.agent.test.framework.Configuration;
import org.openmobster.device.agent.test.framework.CometDaemon;

import org.openmobster.core.common.IOUtilities;
import org.openmobster.core.common.Utilities;
import org.openmobster.core.common.XMLUtilities;
import org.openmobster.core.common.ServiceManager;
import org.openmobster.core.common.database.HibernateManager;

/**
 * @author openmobster
 *
 */
public final class SimulatedDeviceStack
{
	private static Logger log = Logger.getLogger(SimulatedDeviceStack.class);
	private static long deviceCounter = 0;
	
	private DeviceStackRunner runner;
	private boolean pushSocketIsActive;
	private boolean isActivated;
	
	public SimulatedDeviceStack()
	{	
	}
	
	public synchronized void start()
	{	
		try
		{
			this.setupRunner();
		}
		catch(Exception e)
		{
			log.error(this, e);
			throw new RuntimeException(e);
		}
	}
	
	public synchronized void stop()
	{
	}
	
	public DeviceStackRunner getRunner()
	{
		return this.runner;
	}
	
	public void startPushSocket()
	{
		if(this.pushSocketIsActive)
		{
			return;
		}
		
		this.runner.startCometDaemon();
		this.pushSocketIsActive = true;
	}
	
	public boolean isActivated()
	{
		return this.isActivated;
	}
	
	public void setActivated(boolean isActivated)
	{
		this.isActivated = isActivated;
	}
	
	private synchronized void setupRunner() throws Exception
	{
		this.runner = new DeviceStackRunner();
		DeviceStackRunner originalRunner = (DeviceStackRunner)ServiceManager.locate("deviceStack");
		//String deviceId = ""+SimulatedDeviceStack.deviceCounter++;
		String deviceId = ""+Utilities.generateUID();
		deviceId = deviceId.replaceAll(":", "");
		deviceId = deviceId.replaceAll("-", "");
		String imei = "IMEI:"+deviceId;
		
		String user = "blah"+deviceId+"@gmail.com";
		
		this.runner.setDeviceId(imei);
		this.runner.setServerId(originalRunner.getServerId());
		this.runner.setService(originalRunner.getService());
		this.runner.setUser(user);
		this.runner.setCredential(originalRunner.getCredential());
		String server = PerfSuite.getCloudServer();
		if(server != null && server.trim().length()>0)
		{
			this.runner.setServerIp(server);
		}
		
		this.runner.setConfiguration(new Configuration());
		
		this.setUpProvisioner(this.runner);
		
		this.setUpSyncStack(this.runner);
		
		this.runner.start();
		
		//Setup the comet daemon
		CometDaemon cometDaemon = new CometDaemon();
		cometDaemon.setConfiguration(this.runner.getConfiguration());
		this.runner.setCometDaemon(cometDaemon);
	}
	
	private synchronized void setUpProvisioner(DeviceStackRunner runner) throws Exception
	{
		DeviceStackRunner originalRunner = (DeviceStackRunner)ServiceManager.locate("deviceStack");
		Provisioner originalProvisioner = originalRunner.getProvisioner();
		
		Provisioner provisioner = new Provisioner();
		IdentityController identityController = new IdentityController();
		GroupController groupController = new GroupController();
		DeviceController deviceController = new DeviceController();
		EventManager eventManager = originalProvisioner.getEventManager();
		ObjectValidator objectValidator = originalProvisioner.getDomainValidator();
		
		
		provisioner.setDomainValidator(objectValidator);
		provisioner.setEventManager(eventManager);
		
		HibernateManager securityHibernateManager = new HibernateManager();
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("security-perf-hibernate.cfg.xml");
		String xml = new String(IOUtilities.readBytes(is));
		
		
		String deviceId = runner.getDeviceId();
		deviceId = deviceId.substring(deviceId.indexOf(':')+1);
		Document doc = XMLUtilities.parse(xml.replace("${device}", deviceId));
		
		
		securityHibernateManager.startSessionFactory(doc);
		
		identityController.setHibernateManager(securityHibernateManager);
		groupController.setHibernateManager(securityHibernateManager);
		deviceController.setHibernateManager(securityHibernateManager);
		
		provisioner.setIdentityController(identityController);
		provisioner.setGroupController(groupController);
		provisioner.setDeviceController(deviceController);
		
		runner.setProvisioner(provisioner);
	}
	
	private synchronized void setUpSyncStack(DeviceStackRunner runner) throws Exception
	{	
		MobileObjectDatabase newDb = new MobileObjectDatabase();
		StorageMonitor storageMonitor = (StorageMonitor)ServiceManager.locate("mobileObject://StorageMonitor");
		
		HibernateManager mobileObjectHibernateManager = new HibernateManager();
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("hibernate-perf-mobileObject.cfg.xml");
		String xml = new String(IOUtilities.readBytes(is));
		
		
		String deviceId = runner.getDeviceId();
		deviceId = deviceId.substring(deviceId.indexOf(':')+1);
		Document doc = XMLUtilities.parse(xml.replace("${device}", deviceId));
		
		
		mobileObjectHibernateManager.startSessionFactory(doc);
		
		newDb.setHibernateManager(mobileObjectHibernateManager);
		newDb.setStorageMonitor(storageMonitor);
		newDb.start();
		
		runner.setDeviceDatabase(newDb);
		
		HibernateManager simulatorHibernateManager = new HibernateManager();
		is = Thread.currentThread().getContextClassLoader().getResourceAsStream("hibernate-perf-simulator.cfg.xml");
		xml = new String(IOUtilities.readBytes(is));
		
		
		doc = XMLUtilities.parse(xml.replace("${device}", deviceId));
		
		
		simulatorHibernateManager.startSessionFactory(doc);
		
		Database newSyncDb = new Database();
		newSyncDb.setHibernateManager(simulatorHibernateManager);
		
		SyncDataSource newSyncDataSource = new SyncDataSource();
		newSyncDataSource.setDatabase(newSyncDb);
		newSyncDataSource.start();
		
		SyncEngine newSyncEngine = new SyncEngine();
		newSyncEngine.setMobileObjectDatabase(newDb);
		newSyncEngine.setSyncDataSource(newSyncDataSource);
		
		SyncService syncService = new SyncService();
		syncService.setSyncEngine(newSyncEngine);
		runner.setDeviceSyncEngine(newSyncEngine);
		runner.setSyncService(syncService);
	}
}
