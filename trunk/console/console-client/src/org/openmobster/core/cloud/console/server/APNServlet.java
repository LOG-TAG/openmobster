package org.openmobster.core.cloud.console.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileItem;

import org.openmobster.core.console.server.pushapp.ManagePushApp;

public class APNServlet extends HttpServlet
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
			e.printStackTrace();
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
			e.printStackTrace();
			throw new ServletException(e);
		}
	}
	
	private void process(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		this.processCertificate(request);
		
		PrintWriter out;
	    out = response.getWriter();
	    response.setContentType("text/html");       
	    out.println("<html>");
	    out.println("<head>");
	    out.println("<script type=\"text/javascript\">");
	    out.println("function foo() { ");
	    out.println("window.top.uploadComplete('fileName');");
	    out.println("}");
	    out.println("</script>");
	    out.println("</head>");
	    out.println("<body onload=\"foo();\">");
	    out.println("</body>");
	    out.println("</html>");
	}
	
	private void processCertificate(HttpServletRequest request) throws Exception
	{
		// Check that we have a file upload request
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if(isMultipart)
		{
			// Create a factory for disk-based file items
			FileItemFactory factory = new DiskFileItemFactory();

			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload(factory);

			// Parse the request
			List /* FileItem */ items = upload.parseRequest(request);
			
			// Process the uploaded items
			Iterator iter = items.iterator();
			while (iter.hasNext()) 
			{
			    FileItem item = (FileItem) iter.next();
			    if (item.isFormField()) 
			    {
			        processFormField(request,item);
			    } 
			    else 
			    {
			        processUploadedFile(request,item);
			    }
			}
			
			//Store this
			String appId = (String)request.getAttribute("appId");
			byte[] certificate = (byte[])request.getAttribute("certificate");
			String password = (String)request.getAttribute("password");
			String certificateName = (String)request.getAttribute("certificateName");
			
			ManagePushApp managePushApp = ManagePushApp.getInstance();
			managePushApp.uploadCertificate(appId,certificateName, certificate, password);
		}
	}
	
	private void processUploadedFile(HttpServletRequest request,FileItem item)
	{
		byte[] certificate = item.get();
		String certificateName = item.getName();
		request.setAttribute("certificate", certificate);
		request.setAttribute("certificateName",certificateName);
	}
	
	private void processFormField(HttpServletRequest request,FileItem item)
	{
		String name = item.getFieldName();
		String value = item.getString();
		request.setAttribute(name,value);
	}
}
