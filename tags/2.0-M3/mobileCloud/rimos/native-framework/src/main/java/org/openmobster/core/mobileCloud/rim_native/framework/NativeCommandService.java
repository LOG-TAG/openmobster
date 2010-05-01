/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.rim_native.framework;

import java.util.Timer;
import java.util.TimerTask;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.system.Application;

import org.openmobster.core.mobileCloud.rimos.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.rimos.errors.SystemException;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.AppException;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandService;
import org.openmobster.core.mobileCloud.api.ui.framework.resources.AppResources;
import org.openmobster.core.mobileCloud.api.ui.framework.command.UIInitiatedCommand;
import org.openmobster.core.mobileCloud.api.ui.framework.command.SystemInitiatedCommand;
import org.openmobster.core.mobileCloud.api.ui.framework.command.LocalCommand;
import org.openmobster.core.mobileCloud.api.ui.framework.command.RemoteCommand;
import org.openmobster.core.mobileCloud.api.ui.framework.command.AsyncCommand;
import org.openmobster.core.mobileCloud.api.ui.framework.command.PushCommand;


/**
 * @author openmobster@gmail.com
 */
final class NativeCommandService extends CommandService
{	
	private boolean isBlockingCommandActive = false;
	private boolean isAsyncCommandActive  = false;
	private boolean isSystemCommandActive = false;
	
	public void execute(CommandContext commandContext) 
	{
		try
		{
			if(!this.validate(commandContext))
			{
				if(this.isAsyncRequest(commandContext))
				{
					Dialog.alert("An Async Command is already in progress. Please retry!!");
					return;
				}
				else
				{
					commandContext.setAttribute("validation-error", "A System Command is already in progress. Please retry!!");
					return;
				}
			}
			
			this.activateBlockingStatus(commandContext);
			
			Application.getApplication().invokeAndWait(new ExecuteOnEDT(commandContext));
		}
		catch(Exception e)
		{																		
			//report to ErrorHandling system
			ErrorHandler.getInstance().handle(new SystemException(this.getClass().getName(), "execute", new Object[]{
				"Message:"+e.getMessage(),
				"Exception:"+e.toString(),
				"Target Command:"+commandContext.getTarget()
			}));
			
			Application.getApplication().invokeAndWait(new ShowGenericError(commandContext));
		}
		finally
		{
			this.deactivateBlockingStatus(commandContext);
		}
	}
	
	private synchronized boolean validate(CommandContext commandContext)
	{		
		if(this.isAsyncRequest(commandContext))
		{
			if(this.isAsyncCommandActive)
			{
				//only one AsyncCommand at a time
				return false;
			}
			else
			{
				//ok to execute this command
				this.isAsyncCommandActive = true;
				return true;
			}
		}
		else if(this.isSystemRequest(commandContext))
		{
			if(this.isSystemCommandActive)
			{
				//only one system command at a time
				return false;
			}
			else
			{
				//ok to execute a system command
				this.isSystemCommandActive = true;
				return true;
			}
		}
		
		//No need to check for a blocking request...there can only be one active at a time enforced by the OS
		
		return true;
	}
	
	private synchronized void activateBlockingStatus(CommandContext commandContext)
	{		
		if(this.isBlockingRequest(commandContext))
		{
			this.isBlockingCommandActive = true;
		}
	}
	
	private synchronized void deactivateBlockingStatus(CommandContext commandContext)
	{
		if(this.isBlockingRequest(commandContext))
		{
			this.isBlockingCommandActive = false;
		}
	}
			
	private boolean isBlockingRequest(CommandContext commandContext)
	{
		UIInitiatedCommand command = findUICommand(commandContext.getTarget());
		if(command instanceof LocalCommand || command instanceof RemoteCommand)
		{
			return true;
		}
		return false;
	}
			
	private boolean isSystemRequest(CommandContext commandContext)
	{
		if(this.findSystemCommand(commandContext.getTarget())!=null)
		{
			return true;
		}
		return false;
	}
	
	private boolean isAsyncRequest(CommandContext commandContext)
	{
		UIInitiatedCommand command = findUICommand(commandContext.getTarget());
		if(command instanceof AsyncCommand)
		{
			return true;
		}
		return false;
	}
	
	private void executeOnEDT(CommandContext commandContext) throws Exception
	{
		UIInitiatedCommand command = findUICommand(commandContext.getTarget());
		if(command instanceof LocalCommand)
		{
			this.executeLocalOnEDT(commandContext);
		}
		else if(command instanceof RemoteCommand)
		{
			this.executeRemoteOnEDT(commandContext);
		}
		else if(command instanceof AsyncCommand)
		{
			this.executeAsync(commandContext);
		}
		else
		{
			SystemInitiatedCommand sysCommand = findSystemCommand(commandContext.getTarget());
			if(sysCommand instanceof PushCommand)
			{
				this.executePush(commandContext);
			}
		}
	}
	
	private void executeLocalOnEDT(CommandContext commandContext) throws Exception
	{
		UIInitiatedCommand command = findUICommand(commandContext.getTarget());
		
		//View Before
		command.doViewBefore(commandContext);				
		
		//Perform action
		try
		{
			command.doAction(commandContext);
		}
		catch(AppException ape)
		{
			this.reportAppException(commandContext, ape);
			command.doViewError(commandContext);
			return;
		}
										
		//View After
		command.doViewAfter(commandContext);
	}
	
	private void executeRemoteOnEDT(CommandContext commandContext) throws Exception
	{
		UIInitiatedCommand command = findUICommand(commandContext.getTarget());
		
		//View Before
		command.doViewBefore(commandContext);		
		
		//Show a Waiting Dialog and execute Command on another thread
		Dialog dialog = new Dialog("Processing....",
				null,null,
				Dialog.NO,
				null
		);
		dialog.setEditable(false);
		dialog.setEscapeEnabled(false);
				
				
		//Call the Wait Command
		Thread waitCommandThread = new Thread(new ExecuteRemoteCommand(commandContext, dialog));
		waitCommandThread.start();		
						
		//Block the EDT here via the waiting dialog
		dialog.doModal();
	}
	
	private void executeAsync(CommandContext commandContext) throws Exception
	{
		UIInitiatedCommand command = findUICommand(commandContext.getTarget());
		
		//View Before
		command.doViewBefore(commandContext);
		
		//Spawn a background thread to execute the action asynchronously
		//When the background thread is done, it will make a UI callback via the
		//respective command's doViewAfter (if success), or doViewError(if failure) occurs
		Thread asyncCommandThread = new Thread(new ExecuteAsyncCommand(commandContext));
		asyncCommandThread.start();
	}
	
	private void executePush(CommandContext commandContext)
	{
		try
		{
			PushCommand pushCommand = (PushCommand)findSystemCommand(commandContext.getTarget());
									
			//Perform action
			pushCommand.doAction(commandContext);
											
			//View After
			pushCommand.doViewAfter(commandContext);
		}
		catch(Throwable t)
		{
			//since this is a non-ui initiated event, just handle it in the log
			ErrorHandler.getInstance().handle(new SystemException(this.getClass().getName(), "executePush", new Object[]{
				"Message:"+t.getMessage(),
				"Exception:"+t.toString(),
				"Target Command:"+commandContext.getTarget()
			}));
		}
		finally
		{
			this.isSystemCommandActive = false;
		}
	}
	//---------------------------------------------------------------------------------------------------------				
	//These *must* execute on EDT	
	private class ExecuteOnEDT implements Runnable
	{
		private CommandContext commandContext;
		
		private ExecuteOnEDT(CommandContext commandContext)
		{
			this.commandContext = commandContext;
		}
		
		public void run()
		{
			try
			{
				NativeCommandService.this.executeOnEDT(commandContext);
			}
			catch(Exception e)
			{
				//report to ErrorHandling system
				ErrorHandler.getInstance().handle(new SystemException(this.getClass().getName(), "run", new Object[]{
					"Message:"+e.getMessage(),
					"Exception:"+e.toString(),
					"Target Command:"+commandContext.getTarget()
				}));
				
				Application.getApplication().invokeAndWait(new ShowGenericError(commandContext));
			}
		}
	}
	private class CallViewAfter implements Runnable
	{
		private CommandContext commandContext;
		private CallViewAfter(CommandContext commandContext)
		{
			this.commandContext = commandContext;
		}
		
		public void run()
		{
			try
			{
				UIInitiatedCommand command = findUICommand(commandContext.getTarget());
				command.doViewAfter(commandContext);
			}
			catch(Exception e)
			{
				//handle this error
				SystemException syse = new SystemException(this.getClass().getName(), "run", new Object[]{
					"Exception: "+e.toString(),
					"Message: "+e.getMessage(),
					"Target Command: "+commandContext.getTarget()
				});
				throw syse;
			}
		}
	}
	
	private class CallViewError implements Runnable
	{
		private CommandContext commandContext;
		private CallViewError(CommandContext commandContext)
		{
			this.commandContext = commandContext;
		}
		
		public void run()
		{
			try
			{
				UIInitiatedCommand command = findUICommand(commandContext.getTarget());
				command.doViewError(commandContext);
			}
			catch(Exception e)
			{
				//handle this error
				SystemException syse = new SystemException(this.getClass().getName(), "run", new Object[]{
					"Exception: "+e.toString(),
					"Message: "+e.getMessage(),
					"Target Command: "+commandContext.getTarget()
				});
				throw syse;
			}
		}
	}
	
	private class ShowGenericError implements Runnable
	{
		private CommandContext commandContext;
		
		private ShowGenericError(CommandContext commandContext)
		{			
			this.commandContext = commandContext;
		}
		
		public void run()
		{
			//Display a Generic Error Message
			AppResources appResources = Services.getInstance().getResources();
			Dialog.alert(appResources.localize(SystemLocaleKeys.unknown_system_error, "unknown_system_error"));
			
			//Exit the App if error occurred at startup
			if(commandContext.getTarget().indexOf("startup")!=-1)
			{
				System.exit(1);
			}
		}
	}	
			
	private class WaitCommandUICallback implements Runnable
	{
		private Dialog dialog;
		
		private WaitCommandUICallback(Dialog dialog)
		{
			this.dialog = dialog;
		}
		
		public void run()
		{
			this.dialog.close();			
		}
	}
	
	private class ShowTimerExpired implements Runnable
	{
		private Dialog dialog;
		
		private ShowTimerExpired(Dialog dialog)
		{
			this.dialog = dialog;
		}
		
		public void run()
		{
			this.dialog.close();
			Status.show("RemoteCommand is taking longer to execute than expected!!");			
		}
	}
	//---Not Necessarily on EDT-----------------------------------------------------------------------------------------------------------------------
	private class ExecuteRemoteCommand implements Runnable
	{
		private CommandContext commandContext;
		private Dialog dialog;
		
		private ExecuteRemoteCommand(CommandContext commandContext, Dialog dialog)
		{
			this.commandContext = commandContext;
			this.dialog = dialog;
		}
		
		public void run()
		{
			try
			{								
				UIInitiatedCommand command = findUICommand(commandContext.getTarget());	
				
				try
				{
					//Start a timer after which the remote command should not be allowed to execute
					if(commandContext.isTimeoutActivated())
					{
						Timer timer = new Timer();
						timer.schedule(new RemoteCommandExpiry(this.dialog,commandContext), 
						15000);
					}
					
					command.doAction(commandContext);
					
					commandContext.setAttribute("action-finished", "");
				}
				catch(AppException ape)
				{
					commandContext.setAttribute("action-finished", "");
					
					if(commandContext.getAttribute("timer-expired")==null)
					{
						NativeCommandService.this.reportAppException(commandContext, ape);
						Application.getApplication().invokeAndWait(new WaitCommandUICallback(this.dialog));
						Application.getApplication().invokeAndWait(new CallViewError(commandContext));
						return;
					}
				}									
				
				if(commandContext.getAttribute("timer-expired")==null)
				{
					Application.getApplication().invokeAndWait(new WaitCommandUICallback(this.dialog));	
					Application.getApplication().invokeAndWait(new CallViewAfter(commandContext));
				}
			}
			catch(Exception e)
			{		
				commandContext.setAttribute("action-finished", "");
				
				//report to ErrorHandling system
				ErrorHandler.getInstance().handle(new SystemException(this.getClass().getName(), "run", new Object[]{
					"Message:"+e.getMessage(),
					"Exception:"+e.toString(),
					"Target Command:"+this.commandContext.getTarget()
				}));
				
				if(commandContext.getAttribute("timer-expired")==null)
				{
					Application.getApplication().invokeAndWait(new WaitCommandUICallback(this.dialog));
					Application.getApplication().invokeAndWait(new ShowGenericError(commandContext));
				}
			}
		}
	}
	
	private class RemoteCommandExpiry extends TimerTask
	{
		private CommandContext commandContext;
		private Dialog dialog;
		
		private RemoteCommandExpiry(Dialog dialog,CommandContext commandContext)
		{
			this.commandContext = commandContext;
			this.dialog = dialog;
		}
		
		public void run()
		{
			try
			{
				if(commandContext.getAttribute("action-finished") == null)
				{
					//remote command is still busy
					commandContext.setAttribute("timer-expired", "");	
					Application.getApplication().invokeAndWait(new ShowTimerExpired(this.dialog));		
				}
			}
			finally
			{
				//makes sure this is cleaned up
				this.cancel();
			}
		}
	}
	
	private class ExecuteAsyncCommand implements Runnable
	{
		private CommandContext commandContext;
		
		private ExecuteAsyncCommand(CommandContext commandContext)
		{
			this.commandContext = commandContext;
		}
		
		public void run()
		{
			try
			{								
				UIInitiatedCommand command = findUICommand(commandContext.getTarget());	
				
				try
				{
					command.doAction(commandContext);
				}
				catch(AppException ape)
				{
					NativeCommandService.this.reportAppException(commandContext, ape);	
					
					//Execute this on by placing this on a queue
					this.startViewQueue(commandContext,new CallViewError(commandContext));
					return;
				}									
				
				//Execute this on by placing this on a queue
				this.startViewQueue(commandContext,new CallViewAfter(commandContext));								
			}
			catch(Exception e)
			{																				
				//report to ErrorHandling system
				ErrorHandler.getInstance().handle(new SystemException(this.getClass().getName(), "run", new Object[]{
					"Message:"+e.getMessage(),
					"Exception:"+e.toString(),
					"Target Command:"+this.commandContext.getTarget()
				}));
				
				//Execute this on by placing this on a queue
				this.startViewQueue(commandContext,new ShowGenericError(commandContext));
			}			
		}
		
		private void startViewQueue(CommandContext commandContext,Runnable runnable)
		{
			Thread t = new Thread(new AsyncQueueProcessor(commandContext,runnable));
			t.start();
		}
	}
	
	private class AsyncQueueProcessor implements Runnable
	{
		private CommandContext commandContext;
		private Runnable viewProcessor;
		
		private AsyncQueueProcessor(CommandContext commandContext,Runnable viewProcessor)
		{
			this.viewProcessor = viewProcessor;
			this.commandContext = commandContext;
		}
		
		public void run()
		{
			try
			{
				while(NativeCommandService.this.isBlockingCommandActive)
				{
					Thread.currentThread().sleep(5000); //wait for 5 seconds and try again, till a blocking 
					//UI thread is finished processing
				}
				
				if(Application.getApplication().isAlive() && Application.getApplication().isForeground())
				{
					Application.getApplication().invokeLater(viewProcessor);
				}
			}
			catch(Throwable t)
			{
				//report to ErrorHandling system
				ErrorHandler.getInstance().handle(new SystemException(this.getClass().getName(), "run", new Object[]{
					"Message:"+t.getMessage(),
					"Exception:"+t.toString(),
					"Target Command:"+this.commandContext.getTarget()
				}));
				
				if(Application.getApplication().isAlive() && Application.getApplication().isForeground())
				{
					Application.getApplication().invokeLater(new ShowGenericError(commandContext)); //so there is no UI freezing in the 
					//background
				}
			}
			finally
			{
				NativeCommandService.this.isAsyncCommandActive = false;
			}
		}
	}
}
