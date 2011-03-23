/*
 * Copyright 2007 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.openmobster.core.cloud.console.client;

import com.google.gwt.core.client.EntryPoint;

import org.openmobster.core.cloud.console.client.state.ContextRegistry;
import org.openmobster.core.cloud.console.client.flow.FlowServiceRegistry;
import org.openmobster.core.cloud.console.client.flow.TransitionService;
import org.openmobster.core.cloud.console.client.ui.*;
import org.openmobster.core.cloud.console.client.ui.NavigationController;
import org.openmobster.core.cloud.console.client.ui.TabController;

public class App implements EntryPoint 
{
	public void onModuleLoad() 
	{
		this.initialize();
		String configurationXml = AppConfig.INSTANCE.configuration().getText();
		
		TransitionService transitionService = FlowServiceRegistry.getTransitionService();
		
        //Display Authentication Screen
		transitionService.transitionHost(new AuthenticateScreen());
		
		transitionService.transitionActiveWindow(new AuthenticationDialog());
	}
	
	private void initialize()
	{
		ContextRegistry.init();
		TabController.getInstance().start();
		NavigationController.getInstance().start();
		FlowServiceRegistry.init();
	}
}