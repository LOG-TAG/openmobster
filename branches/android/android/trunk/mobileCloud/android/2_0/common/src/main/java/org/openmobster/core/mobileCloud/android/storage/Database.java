/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.android.storage;


/**
 * @author openmobster@gmail.com
 */
public class Database
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
	//-----------------------------------------------------------------------------------------------------------------------
	private void init() throws DBException
	{
		
	}
}
