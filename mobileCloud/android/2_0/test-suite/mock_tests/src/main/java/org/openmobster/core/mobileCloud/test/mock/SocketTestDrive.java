/**
 * 
 */
package org.openmobster.core.mobileCloud.test.mock;

import java.net.Socket;
import java.security.KeyStore;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateException;

import org.openmobster.core.mobileCloud.android.testsuite.Test;

/**
 * @author openmobster
 *
 */
public class SocketTestDrive extends Test
{
	public void runTest()
	{
		try
		{
			this.testSimplePull();
			this.testSimplePush();
			
			this.testSecurePull();
			this.testSecurePush();
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
	}
	
	private void testSimplePull() throws Exception
	{
		InputStream is = null;
		OutputStream os = null;
		Socket socket = null;
		try
		{
			socket = this.getSocket(false);
			
			System.out.println("-----------------------------------");
			System.out.println("Socket IsConnected: "+socket.isConnected());
			
			
			is = socket.getInputStream();
			os = socket.getOutputStream();	
			
			String sessionInitPayload = "processorid=/testdrive/pull";
			
			IOUtilities.writePayLoad(sessionInitPayload, os);			
			
			String data = IOUtilities.readServerResponse(is);
			
			if(data.indexOf("status=200")!=-1)
			{
				String stream = "<pull><caller name='android'/></pull>";
				
				IOUtilities.writePayLoad(stream, os);
				
				String response = IOUtilities.readServerResponse(is);
				
				System.out.println("InvocationResponse........................");
				System.out.println("Response="+response);
			}
			else
			{
				System.out.println("Status="+data);
				throw new RuntimeException("Invocation Failed.........");
			}
		}
		finally
		{
			if(socket != null)
			{
				try{socket.close();}catch(IOException ioe){}
			}
		}
	}
	
	private void testSecurePull() throws Exception
	{
		InputStream is = null;
		OutputStream os = null;
		Socket socket = null;
		try
		{
			socket = this.getSocket(true);
			
			System.out.println("-----------------------------------");
			System.out.println("Socket IsConnected: "+socket.isConnected());
			
			
			is = socket.getInputStream();
			os = socket.getOutputStream();	
			
			String sessionInitPayload = "processorid=/testdrive/pull";
			
			IOUtilities.writePayLoad(sessionInitPayload, os);			
			
			String data = IOUtilities.readServerResponse(is);
			
			if(data.indexOf("status=200")!=-1)
			{
				String stream = "<pull><caller name='android'/></pull>";
				
				IOUtilities.writePayLoad(stream, os);
				
				String response = IOUtilities.readServerResponse(is);
				
				System.out.println("InvocationResponse........................");
				System.out.println("Response="+response);
			}
			else
			{
				System.out.println("Status="+data);
				throw new RuntimeException("Invocation Failed.........");
			}
		}
		finally
		{
			if(socket != null)
			{
				try{socket.close();}catch(IOException ioe){}
			}
		}
	}
	
	private void testSimplePush() throws Exception
	{
		InputStream is = null;
		OutputStream os = null;
		Socket socket = null;
		try
		{
			socket = this.getSocket(false);
			
			System.out.println("-----------------------------------");
			System.out.println("Socket IsConnected: "+socket.isConnected());
			
			
			is = socket.getInputStream();
			os = socket.getOutputStream();	
			
			String sessionInitPayload = "processorid=/testdrive/push";
			
			IOUtilities.writePayLoad(sessionInitPayload, os);			
			
			String data = IOUtilities.readServerResponse(is);
			
			if(data.indexOf("status=200")!=-1)
			{
				String stream = "<push><caller name='android'/></push>";					
				IOUtilities.writePayLoad(stream, os);
				
				String response = null;
				do
				{
					response = IOUtilities.readServerResponse(is);
					
					System.out.println(response);
				}while(!response.contains("close"));	
			}
			else
			{
				System.out.println("Status="+data);
				throw new RuntimeException("Invocation Failed.........");
			}
		}
		finally
		{
			if(socket != null)
			{
				try{socket.close();}catch(IOException ioe){}
			}
		}
	}
	
	private void testSecurePush() throws Exception
	{
		InputStream is = null;
		OutputStream os = null;
		Socket socket = null;
		try
		{
			socket = this.getSocket(true);
			
			System.out.println("-----------------------------------");
			System.out.println("Socket IsConnected: "+socket.isConnected());
			
			
			is = socket.getInputStream();
			os = socket.getOutputStream();	
			
			String sessionInitPayload = "processorid=/testdrive/push";
			
			IOUtilities.writePayLoad(sessionInitPayload, os);			
			
			String data = IOUtilities.readServerResponse(is);
			
			if(data.indexOf("status=200")!=-1)
			{
				String stream = "<push><caller name='android'/></push>";					
				IOUtilities.writePayLoad(stream, os);
				
				String response = null;
				do
				{
					response = IOUtilities.readServerResponse(is);
					
					System.out.println(response);
				}while(!response.contains("close"));	
			}
			else
			{
				System.out.println("Status="+data);
				throw new RuntimeException("Invocation Failed.........");
			}
		}
		finally
		{
			if(socket != null)
			{
				try{socket.close();}catch(IOException ioe){}
			}
		}
	}
	
	private static Socket getSocket(boolean isSecure) throws Exception
	{
		Socket socket = null;
		
		//Create a socket
		String serverIp = "192.168.1.107";
		
		if(isSecure)
		{
			int port = 1500;
			
			//SSL Socket
			SSLContext sslContext = getSSLContext();
			
			
			socket = sslContext.getSocketFactory().createSocket(serverIp, port);
			((SSLSocket)socket).setEnabledCipherSuites(((SSLSocket)socket).getSupportedCipherSuites());
		}
		else
		{
			int port = 1502;
			
			//Plain Socket
			socket = new Socket(serverIp,port);
		}
		
		return socket;
	}
	
	private static SSLContext getSSLContext() throws Exception
	{
		SSLContext sslContext = null;
		
		//Setup the KeyStore
		/*SecurityConfig securityConfig = Configuration.getInstance().getSecurityConfig();
		String keyStoreLocation = securityConfig.getKeyStoreLocation();
		String keyStorePass = securityConfig.getKeyStorePassword();
		
		KeyStore keyStore = KeyStore.getInstance("JKS");
		FileInputStream fis = new FileInputStream(keyStoreLocation);
		keyStore.load(fis, keyStorePass.toCharArray());*/
		
		//Setup TrustManagers for handling trusts with other peers
		//TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
		//TrustManagerFactory.getDefaultAlgorithm());
		//trustManagerFactory.init(keyStore);
		//TrustManager[] trustManager = trustManagerFactory.getTrustManagers();
		
		TrustManager[] trustManager = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[] {};
            }

            public void checkClientTrusted(X509Certificate[] chain,
                            String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain,
                            String authType) throws CertificateException {
            }
		} };

		
		//Create an SSLContext
		sslContext = SSLContext.getInstance("TLS");
		sslContext.init(null, trustManager, null);
		
		return sslContext;
	}
}
