/**
 * Copyright (c) {2003,2009} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.rim_native.framework;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.system.Application;

import org.openmobster.core.mobileCloud.rimos.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.rimos.errors.SystemException;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.AppException;
import org.openmobster.core.mobileCloud.api.ui.framework.command.Command;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandService;
import org.openmobster.core.mobileCloud.api.ui.framework.command.LocalCommand;
import org.openmobster.core.mobileCloud.api.ui.framework.resources.AppResources;


/**
 * @author openmobster@gmail.com
 */
final class NativeCommandService extends CommandService
{			
	public void execute(CommandContext commandContext) 
	{
		try
		{
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
	}
	
	private void executeOnEDT(CommandContext commandContext) throws Exception
	{
		Command command = find(commandContext.getTarget());
		if(command instanceof LocalCommand)
		{
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
		else 
		{
			this.executeRemoteOnEDT(commandContext);
		}
	}
	
	private void executeRemoteOnEDT(CommandContext commandContext) throws Exception
	{
		Command command = find(commandContext.getTarget());
		
		//View Before
		command.doViewBefore(commandContext);		
		
		//Show a Waiting Dialog and execute Command on another thread
		Dialog dialog = new Dialog("Waiting....",
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
				Command command = find(commandContext.getTarget());
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
				Command command = find(commandContext.getTarget());
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
				Command command = find(commandContext.getTarget());	
				
				try
				{
					command.doAction(commandContext);
				}
				catch(AppException ape)
				{
					NativeCommandService.this.reportAppException(commandContext, ape);
					Application.getApplication().invokeAndWait(new WaitCommandUICallback(this.dialog));
					Application.getApplication().invokeAndWait(new CallViewError(commandContext));
					return;
				}									
				
				Application.getApplication().invokeAndWait(new WaitCommandUICallback(this.dialog));	
				Application.getApplication().invokeAndWait(new CallViewAfter(commandContext));								
			}
			catch(Exception e)
			{																				
				//report to ErrorHandling system
				ErrorHandler.getInstance().handle(new SystemException(this.getClass().getName(), "run", new Object[]{
					"Message:"+e.getMessage(),
					"Exception:"+e.toString(),
					"Target Command:"+this.commandContext.getTarget()
				}));
				
				Application.getApplication().invokeAndWait(new WaitCommandUICallback(this.dialog));
				Application.getApplication().invokeAndWait(new ShowGenericError(commandContext));
			}			
		}
	}
	//-------------------------------------------------------------------------------------------------------------------------------------------
	/*private void executeAndPause(CommandContext commandContext) 
	{		
	}
	//--------------------------------------------------------------------------------------------------------------------------------------------
	private void executeWithPauseAndResume(CommandContext commandContext) 
	{
		Resources resources = NavigationContext.getInstance().getResources();
		Image progressImage = resources.getAnimation("loading.gif");
		Button progressButton = new Button(progressImage);
		progressButton.setAlignment(Button.CENTER);
		
		Dialog dialog = new Dialog(UIManager.getInstance().localize(LocaleKeys.waiting, "Waiting")+"...");
		dialog.setLayout(new BorderLayout());
		dialog.addComponent(BorderLayout.CENTER, progressButton);
		
		Thread pauseAndResumeThread = new Thread(new PauseAndResumeCommand(dialog, commandContext));
		pauseAndResumeThread.start();
		
		App.midlet.notifyPaused();	
		
		//Block the EDT here via the waiting dialog			
		dialog.show();
	}
	
	private class PauseAndResumeCommand implements Runnable
	{
		private CommandContext commandContext;
		private Dialog waitDialog;
		
		private PauseAndResumeCommand(Dialog waitDialog, CommandContext commandContext)
		{
			this.commandContext = commandContext;
			this.waitDialog = waitDialog;
		}
		
		public void run()
		{
			try
			{								
				Command command = (Command)registry.get(commandContext.getTarget());												
				command.execute(this.commandContext);
				
				Display.getInstance().callSerially(new PauseAndResumeCommandUICallback(this.waitDialog));
			}
			catch(Exception e)
			{
				CommandError error = new CommandError();
				error.setType(CommandKeys.system_error);
				error.setMessage(e.getMessage());
				error.setMessageKey(LocaleKeys.unknown_system_error);
				this.commandContext.setError(error);
				
				//report to ErrorHandling system
				ErrorHandler.getInstance().handle(new SystemException(this.getClass().getName(), "run", new Object[]{
					"Message:"+e.getMessage(),
					"Exception:"+e.toString(),
					"Target Command:"+this.commandContext.getTarget()
				}));
			}
		}
	}
	
	private class PauseAndResumeCommandUICallback implements Runnable
	{
		private Dialog waitDialog;
		
		private PauseAndResumeCommandUICallback(Dialog waitDialog)
		{
			this.waitDialog = waitDialog;
		}
		
		public void run()
		{
			App.midlet.resumeRequest();
			this.waitDialog.dispose();
		}
	}*/	
}
