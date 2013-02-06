/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.io.StringWriter;
import java.io.BufferedInputStream;
import java.io.InputStreamReader;
import org.apache.http.util.ByteArrayBuffer;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.openmobster.core.mobileCloud.android.testsuite.Test;

import com.turbomanage.httpclient.*;

/**
 * @author openmobster@gmail.com
 *
 */
public final class TestBasicHttpClient extends Test
{
	public void runTest()
	{		
		try
		{
			BasicHttpClient httpClient = new BasicHttpClient("https://192.168.1.103:1504",new BasicRequestHandler(){
				@Override
			    public HttpURLConnection openConnection(String urlString) throws IOException 
			    {
					try
					{
						// First create a trust manager that won't care.
				        X509TrustManager trustManager = new X509TrustManager() 
				        {
				            public java.security.cert.X509Certificate[] getAcceptedIssuers() 
				            {
				                // Don't do anything.
				                return null;
				            }
	
				            @Override
				            public void checkClientTrusted(
				                    java.security.cert.X509Certificate[] chain,
				                    String authType)
				                    throws java.security.cert.CertificateException 
				           {
				                // TODO Auto-generated method stub
				           }
	
				            @Override
				            public void checkServerTrusted(
				                    java.security.cert.X509Certificate[] chain,
				                    String authType)
				                    throws java.security.cert.CertificateException 
				            {
				                // TODO Auto-generated method stub
	
				            }
				        };
				        
				        // Now put the trust manager into an SSLContext.
				        // Supported: SSL, SSLv2, SSLv3, TLS, TLSv1, TLSv1.1
				        SSLContext sslContext = SSLContext.getInstance("SSL");
				        sslContext.init(null, new TrustManager[] { trustManager },
				                new SecureRandom());
				        
				        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

				        // Create all-trusting host name verifier
				        HostnameVerifier allHostsValid = new HostnameVerifier() {
				            public boolean verify(String hostname, SSLSession session) 
				            {
				            	return true;
				            }
				        };
				        // Install the all-trusting host verifier
				        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
				        
				        URL url = new URL(urlString);
				        HttpURLConnection uc = (HttpURLConnection) url.openConnection();
				        return uc;
					}
					catch(Exception e)
					{
						throw new RuntimeException(e);
					}
			    }
				
				/*@Override
			    public void prepareConnection(HttpURLConnection urlConnection, HttpMethod httpMethod,
			            String contentType) throws IOException 
			    {
			        // Configure connection for request method
					urlConnection.setChunkedStreamingMode(100);
			        urlConnection.setRequestMethod(httpMethod.getMethodName());
			        urlConnection.setDoOutput(httpMethod.getDoOutput());
			        urlConnection.setDoInput(httpMethod.getDoInput());
			        if (contentType != null) {
			            urlConnection.setRequestProperty("Content-Type", contentType);
			        }
			        // Set additional properties
			        urlConnection.setRequestProperty("Accept-Charset", UTF8);
			    }*/
				
				/*@Override
			    public void writeStream(OutputStream out, byte[] content) throws IOException 
			    {
					System.out.println("*********Sending a streamed output****************");
					ByteArrayInputStream bis = null;
					try
					{
						bis = new ByteArrayInputStream(content);
						int bytesRead = -1;
						byte[] buffer = new byte[500];
						while((bytesRead = bis.read(buffer)) != -1)
						{
							out.write(buffer, 0, bytesRead);
						}
						out.flush();
					}
					finally
					{
						if(bis != null)
						{
							bis.close();
						}
					}
			    }*/
				
				@Override
			    public byte[] readStream(InputStream in) throws IOException 
			    {
			        /*int nRead;
			        byte[] data = new byte[500];
			        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

			        while ((nRead = in.read(data)) != -1) 
			        {
			            buffer.write(data, 0, nRead);
			        }
			        
			        buffer.flush();
			        
			        byte[] content = buffer.toByteArray();
			        
			        buffer.close();
			        
			        return content;*/
					
					ByteArrayBuffer buffer = new ByteArrayBuffer(1024);
					int byteRead = -1;
			        while ((byteRead = in.read()) != -1) 
			        {
			            buffer.append(byteRead);
			        }
			        
			        return buffer.toByteArray();
			    }
			});
			
			/*ParameterMap params = httpClient.newParams();
			params.add("id", "x&y&z");
			params.add("blah","blahblah2,12345");
			
			HttpResponse httpResponse = httpClient.get("/om/testservice", params);*/
			
			StringBuilder buffer = new StringBuilder();
	        for(int i=0; i<1024; i++)
	        {
	        	for(int j=0; j<2000; j++)
	        	{
	        		buffer.append("a");
	        	}
	        }
			
			HttpResponse httpResponse = httpClient.post("/om/testservice", "application/json", buffer.toString().getBytes());
			
			System.out.println("*********************************************");
			System.out.println(httpResponse.getBodyAsString());
			System.out.println("*********************************************");
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			throw new RuntimeException(e.toString());
		}
	}
}
