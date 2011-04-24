package org.openmobster.core.cloud.console.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmobster.core.console.server.pushapp.ManagePushApp;
import org.openmobster.core.console.server.pushapp.Certificate;

public class DownloadAPNCertificateServlet extends HttpServlet
{
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
	{
		try
		{
			this.process(req, resp);
		}
		catch(Exception e)
		{
			throw new ServletException(e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
	{
		try
		{
			this.process(req, resp);
		}
		catch(Exception e)
		{
			throw new ServletException(e);
		}
	}
	
	private void process(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String appId = request.getParameter("appid");
		
		ManagePushApp managePushApp = ManagePushApp.getInstance();
		
		Certificate cert = managePushApp.downloadCertificate(appId);
		
		if(cert != null)
		{
			byte[] certificate = cert.getCertificate();
			String filename = cert.getFileName();
			response.setHeader("Content-Type", "binary/octet-stream");
			response.setHeader("Content-Length", String.valueOf(certificate.length));
			response.setHeader("Content-disposition", "attachment;filename=\"" + filename + "\"");
			response.getOutputStream().write(certificate);
		}
	}
}
