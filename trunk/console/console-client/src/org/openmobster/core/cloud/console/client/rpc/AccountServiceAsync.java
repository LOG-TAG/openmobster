package org.openmobster.core.cloud.console.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface AccountServiceAsync 
{
	void invoke(String input, AsyncCallback<String> callback);
}
