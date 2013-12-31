/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.showcase.cloud.camera;

import org.apache.log4j.Logger;

import org.openmobster.cloud.api.camera.CameraCommandContext;
import org.openmobster.cloud.api.camera.CameraCommandResponse;
import org.openmobster.cloud.api.camera.CloudCameraCommand;
import org.openmobster.cloud.api.camera.CloudCameraURI;

/**
 * 
 * @author openmobster@gmail.com
 */
@CloudCameraURI(uri="/upload/picture")
public class UploadPictureCommand implements CloudCameraCommand
{
	private static Logger log = Logger.getLogger(UploadPictureCommand.class);
	
	public CameraCommandResponse invoke(CameraCommandContext context)
	{
		CameraCommandResponse response = new CameraCommandResponse();
		
		log.info("***********************************************");
		log.info("UploadPictureCommand successfully invoked");
		String[] names = context.getNames();
		for(String name:names)
		{
			String value = context.getAttribute(name);
			log.info("Name: "+name+", Value: "+value);
		}
		
		for(int i=0; i<5; i++)
		{
			response.setAttribute("name:"+i, "value:"+i);
		}
		log.info("***********************************************");
		
		return response;
	}
}
