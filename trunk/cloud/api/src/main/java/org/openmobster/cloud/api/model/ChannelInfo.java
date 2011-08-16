/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.cloud.api.model;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * Annotation which carries configuration information about a Mobile Bean Channel
 * <p>
 * A Channel serves as a gateway for integrating on-device data objects with
 * server side backend storage systems such as relational databases, content repositories, or Enterprise systems like
 * CRMs, ERPs etc 
 *  
 * @author openmobster@gmail.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ChannelInfo 
{
	/**
	 * A unique identifier that allows the mobile system to route data object traffic to 
	 * the proper channel
	 * 
	 * @return a unique channel identifier
	 */
	String uri();
	
	/**
	 * The fully qualified class name of the Mobile Bean that will be processed by this Channel
	 * 
	 * @return fully qualified class name of the Mobile Bean
	 */
	String mobileBeanClass();
	
	/**
	 * Specifies how often this channel should be queries for updates to be pushed as notifications
	 * (comet, mobile push, etc). Default Value is : 20000ms
	 * 
	 * @return an updateCheckInterval
	 */
	long updateCheckInterval() default 20000;
}
