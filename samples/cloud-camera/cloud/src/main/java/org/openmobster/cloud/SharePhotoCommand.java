/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.cloud;

import org.apache.log4j.Logger;
import java.io.IOException;

import org.openmobster.server.api.camera.CameraCommandContext;
import org.openmobster.server.api.camera.CameraCommandResponse;
import org.openmobster.server.api.camera.CloudCameraCommand;
import org.openmobster.server.api.camera.CloudCameraURI;

/**
 * A Cloud Command which receives Photo storage invocations from the device
 * 
 * @author openmobster@gmail.com
 */
@CloudCameraURI(uri="/share/photo")
public final class SharePhotoCommand implements CloudCameraCommand
{
	private static Logger log = Logger.getLogger(SharePhotoCommand.class);
	
	public void start()
	{
		log.info("***********************************************");
		log.info("Share Photo Command successfully started.......");
		log.info("***********************************************");
	}
	
	@Override
	public CameraCommandResponse invoke(CameraCommandContext context) 
	{
		try
		{
			//Gather the incoming data
			String fileName = context.getFullName();
			String mimeType = context.getMimeType();
			byte[] photo = context.getPhoto();
			
			//Log it...You can do anything from here like store in a CMS.
			//store it in a database, etc
			log.info("*********************************");
			log.info("FileName: "+fileName);
			log.info("MimeType: "+mimeType);
			log.info("Photo: "+photo);
			log.info("*********************************");
		
			//Nothing to send back...otherwise use a CameraCommandResponse to send somthing back
			return null;
		}
		catch(IOException ioe)
		{
			throw new RuntimeException(ioe);
		}
	}
}
