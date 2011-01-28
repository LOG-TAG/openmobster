/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.core.mobileCloud.rimos.testsuite.moblet;

import java.util.Vector;

import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;

import org.openmobster.core.mobileCloud.api.system.CometUtil;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.AppException;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.RemoteCommand;
import org.openmobster.core.mobileCloud.moblet.Moblet;
import org.openmobster.core.mobileCloud.rim_native.framework.SystemLocaleKeys;
import org.openmobster.core.mobileCloud.rimos.configuration.Configuration;
import org.openmobster.core.mobileCloud.rimos.storage.Database;

import test.openmobster.core.mobileCloud.rimos.testsuite.TestSuite;



/**
 * @author openmobster@gmail.com
 *
 */
public class TestSuiteBootstrapCommand implements RemoteCommand
{
	public void doViewBefore(CommandContext commandContext)
	{
		LabelField serverLabel = new LabelField("Server:", LabelField.ELLIPSIS);
		BasicEditField serverField = new BasicEditField(BasicEditField.FILTER_URL);
		
		Dialog serverDialog = new Dialog(Dialog.D_OK,"Activate",0,null,0);
		serverDialog.add(serverLabel);
		serverDialog.add(serverField);
		serverDialog.doModal();
		
		String server = serverField.getText();
		if(server != null && server.trim().length()>0)
		{
			ActivationUtil.cloudServerIp = server;
		}
	}

	public void doAction(CommandContext commandContext) 
	{	
		try
		{
			Moblet.getInstance().startup();					
			
			this.cleanup();
										
			ActivationUtil.activateDevice();			
			CometUtil.subscribeChannels();
		}
		catch(Exception e)
		{
			System.out.println("---------------------------------------------------------------");
			System.out.println("Exception: "+e.getMessage());
			System.out.println("---------------------------------------------------------------");
			throw new AppException();
		}
	}
	
	public void doViewAfter(CommandContext commandContext)
	{
		try
		{
			TestSuite suite = new TestSuite();
			suite.load();
			suite.execute();
		
			Services.getInstance().getNavigationContext().setHome("home");
			Services.getInstance().getNavigationContext().home();
		}
		catch(Exception e)
		{
			throw new RuntimeException(e.toString());
		}
	}
	
	public void doViewError(CommandContext commandContext)
	{
		Dialog.alert(Services.getInstance().getResources().localize(SystemLocaleKeys.moblet_startup_error, SystemLocaleKeys.moblet_startup_error));			
		System.exit(1);
	}
	//-----------------------------------------------------------------------------------------------------------------------------------
	private void cleanup() throws Exception
    {
    	//System.out.println("Starting the DeviceContainer..");
    	
    	//Make a local copy of registered channels
    	//System.out.println("Copying the channels...........");
    	Configuration configuration = Configuration.getInstance();
    	Vector myChannels = configuration.getMyChannels();
    	
    	//drop the configuration so new one will be generated
    	//System.out.println("Dropping the configuration.......");
    	configuration.stop();
    	Database database = Database.getInstance();
    	database.dropTable(Database.provisioning_table);
    	
    	//restart the configuration
    	//System.out.println("Restarting the configuration.......");
    	configuration.start();
    	
    	//Now reload the registered channels if any were found
    	//System.out.println("Reloading the channels.......");
    	if(myChannels != null && myChannels.size()>0)
    	{
	    	configuration = Configuration.getInstance();
	    	int size = myChannels.size(); 
	    	for(int i=0; i<size; i++)
	    	{
	    		configuration.addMyChannel((String)myChannels.elementAt(i));
	    	}
	    	configuration.save();
    	}
    	
    	//System.out.println("Startup successfull.............");
    }
}
