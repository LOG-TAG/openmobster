/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.console.server.admin;

import java.util.Map;

/**
 * 
 * @author openmobster@gmail.com
 */
public class AdminAccountException extends Exception
{	
	
	private static final long serialVersionUID = -2294095894100486056L;
	
	public static final int VALIDATION_ERROR = 1;	
	public static final int ACCOUNT_ALREADY_EXISTS = 2;
	public static final int ACCOUNT_INACTIVE = 3;
	public static final int LAST_ACCOUNT_CANNOT_BE_DEACTIVATED = 4;
	public static final int EMAIL_INVALID = 5;
	
	private Map<String, String[]> validationErrors;
	private int type;
	
	public AdminAccountException()
	{
		this("");
	}

	
	public AdminAccountException(String msg)
	{
		super(msg);
	}

	
	public AdminAccountException(Throwable t)
	{
		super(t.getMessage(), t);
	}
	
	public AdminAccountException(int type)
	{
		this("");
		this.type = type;
	}
	
	public AdminAccountException(String msg, int type)
	{
		this(msg);
		this.type = type;
	}
	
	public AdminAccountException(Throwable t, int type)
	{
		this(t);
		this.type = type;
	}
	//-------------------------------------------------------------------------------------------------------------------------------------------------------
	public Map<String, String[]> getValidationErrors() 
	{
		return validationErrors;
	}


	public void setValidationErrors(Map<String, String[]> validationErrors) 
	{
		this.validationErrors = validationErrors;
	}
	
	public int getType()
	{
		return this.type;
	}
	//-------------------------------------------------------------------------------------------------------------------------------------------------------
	@Override
	public String getMessage() 
	{	
		String message = super.getMessage();
		
		if((message == null || message.trim().length() == 0) && this.type > 0)
		{
			switch(this.type)
			{
				case AdminAccountException.VALIDATION_ERROR:
					message = "validation_error";
				break;
				
				case AdminAccountException.ACCOUNT_ALREADY_EXISTS:
					message = "identity_already_registered";
				break;
				
				case AdminAccountException.ACCOUNT_INACTIVE:
					message = "account_inactive";
				break;
				
				case AdminAccountException.LAST_ACCOUNT_CANNOT_BE_DEACTIVATED:
					message = "last_account_cannot_be_deactivated";
				break;
				
				case AdminAccountException.EMAIL_INVALID:
					message = "email_invalid";
				break;
				
				default:
					message = "";
				break;
			}
		}
		
		return message;
	}	
}
