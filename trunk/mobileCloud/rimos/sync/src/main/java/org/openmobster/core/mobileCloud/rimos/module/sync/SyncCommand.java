/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.rimos.module.sync;

import java.util.Vector;

import org.openmobster.core.mobileCloud.rimos.util.VectorUtil;

/**
 * 
 * @author openmobster@gmail.com
 *
 */
public final class SyncCommand
{	
	private String cmdId; //required
	
	private String target; //nullable
	private String source; //nullable 
	private String meta; //nullable
	private String numberOfChanges; //nullable
	
	private Vector addCommands; //zero or many
	private Vector replaceCommands; //zero or many
	private Vector deleteCommands; //zero or many
		
	
	/**
	 * 
	 *
	 */
	public SyncCommand()
	{
		this.addCommands = new Vector();
		this.replaceCommands = new Vector();
		this.deleteCommands = new Vector();
	}


	public String getCmdId()
	{
		return cmdId;
	}


	public void setCmdId(String cmdId)
	{
		this.cmdId = cmdId;
	}


	public String getMeta()
	{
		return meta;
	}


	public void setMeta(String meta)
	{
		this.meta = meta;
	}


	public String getNumberOfChanges()
	{
		return numberOfChanges;
	}


	public void setNumberOfChanges(String numberOfChanges)
	{
		this.numberOfChanges = numberOfChanges;
	}


	public String getSource()
	{
		return source;
	}


	public void setSource(String source)
	{
		this.source = source;
	}


	public String getTarget()
	{
		return target;
	}


	public void setTarget(String target)
	{
		this.target = target;
	}


	public Vector getAddCommands()
	{
		if(this.addCommands == null)
		{
			this.addCommands = new Vector();
		}
		return addCommands;
	}


	public void setAddCommands(Vector addCommands)
	{
		this.addCommands = addCommands;
	}
	
	public Vector getReplaceCommands()
	{
		if(this.replaceCommands == null)
		{
			this.replaceCommands = new Vector();
		}
		return this.replaceCommands;
	}
	
	public void setReplaceCommands(Vector replaceCommands)
	{
		this.replaceCommands = replaceCommands;
	}
	
	public Vector getDeleteCommands()
	{
		if(this.deleteCommands == null)
		{
			this.deleteCommands = new Vector();
		}
		return this.deleteCommands;
	}
	
	public void setDeleteCommands(Vector deleteCommands)
	{
		this.deleteCommands = deleteCommands;
	}
	
	/**
	 * 
	 * @param command
	 */
	public void addOperationCommand(AbstractOperation command)
	{
		if(command instanceof Add)
		{
			this.getAddCommands().addElement(command);
		}
		else if(command instanceof Replace)
		{
			this.getReplaceCommands().addElement(command);
		}
		else if(command instanceof Delete)
		{
			this.getDeleteCommands().addElement(command);
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public Vector getAllCommands()
	{
		Vector allCommands = new Vector();
	
		Vector commands = this.getAddCommands();
		if(commands != null)
		{
			for(int i=0,size=commands.size(); i<size; i++)
			{
				allCommands.addElement(commands.elementAt(i));
			}
		}
		
		commands = this.getReplaceCommands();
		if(commands != null)
		{
			for(int i=0,size=commands.size(); i<size; i++)
			{
				allCommands.addElement(commands.elementAt(i));
			}
		}
		
		commands = this.getDeleteCommands();
		if(commands != null)
		{
			for(int i=0,size=commands.size(); i<size; i++)
			{
				allCommands.addElement(commands.elementAt(i));
			}
		}
				
		return allCommands;
	}	
	//-----Long Object Support----------------------------------------------------------------------------------
	/**
	 * 
	 * @return
	 */
	public Vector filterChunkedCommands()
	{
		Vector chunkedCommands = new Vector();
		
		Vector chunkedAddCommands = new Vector();
		Vector commands = this.getAddCommands();
		for(int i=0,size=commands.size();i<size;i++)
		{
			Add cour = (Add)commands.elementAt(i);
			if(cour.isChunked())
			{
				chunkedAddCommands.addElement(cour);
			}
		}
		VectorUtil.removeElements(commands, chunkedAddCommands);
		VectorUtil.addAll(chunkedCommands, chunkedAddCommands);
		
		Vector chunkedReplaceCommands = new Vector();
		commands = this.getReplaceCommands();
		for(int i=0,size=commands.size();i<size;i++)
		{
			Replace cour = (Replace)commands.elementAt(i);
			if(cour.isChunked())
			{
				chunkedReplaceCommands.addElement(cour);
			}
		}
		VectorUtil.removeElements(commands, chunkedReplaceCommands);
		VectorUtil.addAll(chunkedCommands, chunkedReplaceCommands);
				
		return chunkedCommands;
	}
	
	/**
	 * 
	 * @return
	 */
	public Vector getChunkedCommands()
	{
		Vector chunkedCommands = new Vector();
		
		Vector chunkedAddCommands = new Vector();
		Vector commands = this.getAddCommands();
		for(int i=0,size=commands.size();i<size;i++)
		{
			Add cour = (Add)commands.elementAt(i);
			if(cour.isChunked())
			{
				chunkedAddCommands.addElement(cour);
			}
		}		
		VectorUtil.addAll(chunkedCommands, chunkedAddCommands);
		
		Vector chunkedReplaceCommands = new Vector();
		commands = this.getReplaceCommands();
		for(int i=0,size=commands.size();i<size;i++)
		{
			Replace cour = (Replace)commands.elementAt(i);
			if(cour.isChunked())
			{
				chunkedReplaceCommands.addElement(cour);
			}
		}
		VectorUtil.addAll(chunkedCommands, chunkedReplaceCommands);
		
		
		return chunkedCommands;
	}
		
	/**
	 * 
	 * @param command
	 */
	public void addChunkedCommand(AbstractOperation command)
	{
		if(command instanceof Add)
		{
			this.getAddCommands().addElement(command);
		}
		else if(command instanceof Replace)
		{
			this.getReplaceCommands().addElement(command);
		}
	}	
}
