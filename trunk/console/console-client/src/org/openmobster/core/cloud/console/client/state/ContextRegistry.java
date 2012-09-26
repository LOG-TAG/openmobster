/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.cloud.console.client.state;

/**
 *
 * @author openmobster@gmail.com
 */
public final class ContextRegistry 
{
	private static AppContext appContext;
	private static ConversationContext conversationContext;
	private static InteractionContext interactionContext;
	
	public static void init()
	{
		appContext = new AppContext();
		conversationContext = new ConversationContext();
		interactionContext = new InteractionContext();
	}
	
	public static AppContext getAppContext()
	{
		if(appContext == null)
		{
			throw new IllegalStateException("ContextRegistry is uninitialized");
		}
		return appContext;
	}
	
	public static ConversationContext getConversationContext()
	{
		if(conversationContext == null)
		{
			throw new IllegalStateException("ContextRegistry is uninitialized");
		}
		return conversationContext;
	}
	
	public static InteractionContext getInteractionContext()
	{
		if(interactionContext == null)
		{
			throw new IllegalStateException("ContextRegistry is uninitialized");
		}
		return interactionContext;
	}
}
