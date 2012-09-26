/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.dataService.server;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

import java.io.FileInputStream;

import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.KeyManager;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.SSLContext;

import org.apache.log4j.Logger;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.SimpleByteBufferAllocator;
import org.apache.mina.common.IoAcceptor;
import org.apache.mina.common.ThreadModel;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.SSLFilter;

import org.apache.mina.transport.socket.nio.SocketAcceptorConfig;
import org.apache.mina.transport.socket.nio.SocketAcceptor;


/**
 * 
 * @author openmobster@gmail.com
 */
public class Server
{	
	private static Logger log = Logger.getLogger(Server.class);
		
	private IoAcceptor acceptor = null;
		
	private ExecutorService executor = null;
		
	private ServerHandler handler = null;
		
	protected int port = 1500; //default port if not overriden from configuration
		
	private String keyStoreLocation = null;
		
	private String keyStorePassword = null;
	
	private TransactionFilter transactionFilter;
	private AuthenticationFilter authenticationFilter;
	private PayloadFilter payloadFilter;
	private RequestConstructionFilter requestFilter;
		
	public Server()
	{
		
	}
		
	public void start() throws RuntimeException
	{
		this.startListening(this.isSecure());        
	}
		
	public void stop() throws RuntimeException
	{
		this.acceptor.unbindAll();
		this.executor.shutdown();
		this.acceptor = null;
		this.executor = null;
		this.handler = null;
	}
	
	public int getPort()
	{
		return port;
	}
	
	public void setPort(int port)
	{
		this.port = port;
	}
	
	public ServerHandler getHandler()
	{
		return handler;
	}
	
	public void setHandler(ServerHandler handler)
	{
		this.handler = handler;
	}
		
	public String getKeyStoreLocation() 
	{
		return keyStoreLocation;
	}

	
	public void setKeyStoreLocation(String keyStoreLocation) 
	{
		this.keyStoreLocation = keyStoreLocation;
	}
	
	
	public String getKeyStorePassword() 
	{
		return keyStorePassword;
	}

	
	public void setKeyStorePassword(String keyStorePassword) 
	{
		this.keyStorePassword = keyStorePassword;
	}
	
	
	public AuthenticationFilter getAuthenticationFilter() 
	{
		return authenticationFilter;
	}

	public void setAuthenticationFilter(AuthenticationFilter authenticationFilter) 
	{
		this.authenticationFilter = authenticationFilter;
	}
	
	
	public TransactionFilter getTransactionFilter() 
	{
		return transactionFilter;
	}

	public void setTransactionFilter(TransactionFilter transactionFilter) 
	{
		this.transactionFilter = transactionFilter;
	}
		
	public PayloadFilter getPayloadFilter() 
	{
		return payloadFilter;
	}

	public void setPayloadFilter(PayloadFilter payloadFilter) 
	{
		this.payloadFilter = payloadFilter;
	}
	
	public RequestConstructionFilter getRequestFilter()
	{
		return requestFilter;
	}

	public void setRequestFilter(RequestConstructionFilter requestFilter)
	{
		this.requestFilter = requestFilter;
	}
	//---------------------------------------------------------------------------------------------------
	public boolean isSecure()
	{
		return (this.keyStoreLocation != null && this.keyStoreLocation.trim().length()>0);
	}
	protected void startListening(boolean activateSSL)
	{
		try
		{
			//The following two lines change the default buffer type to 'heap',
	        // which yields better performance.c
	        ByteBuffer.setUseDirectBuffers(false);
	        ByteBuffer.setAllocator(new SimpleByteBufferAllocator());
	        
	        this.acceptor = new SocketAcceptor();
	        this.executor = Executors.newCachedThreadPool();
	        
	        SocketAcceptorConfig cfg = new SocketAcceptorConfig();
	        cfg.setThreadModel(ThreadModel.MANUAL);
	       
	        if(activateSSL)
	        {
	        	//Makes the tcp connection protected with SSL
	        	SSLFilter sslFilter = new SSLFilter(this.getSSLContext());	        
	        	cfg.getFilterChain().addLast("ssl", sslFilter);
	        }
	        
	        //
	        TextLineCodecFactory textLine = new TextLineCodecFactory(Charset.forName("UTF-8"));	
	        textLine.setDecoderMaxLineLength(Integer.MAX_VALUE);
	        textLine.setEncoderMaxLineLength(Integer.MAX_VALUE);
	        ProtocolCodecFilter codecFilter = new ProtocolCodecFilter(textLine);
	        cfg.getFilterChain().addLast( "codec", codecFilter);
	        
	        //
	        cfg.getFilterChain().addLast("threadPool", new ExecutorFilter(this.executor));
	        
	        //Add Custom filters here
	        if(this.payloadFilter != null)
	        {
	        	cfg.getFilterChain().addLast("payloadFilter", this.payloadFilter);
	        }
	        
	        if(this.requestFilter != null)
	        {
	        	cfg.getFilterChain().addLast("requestFilter", this.requestFilter);
	        }
	        
	        if(this.transactionFilter != null)
	        {
	        	cfg.getFilterChain().addLast("transactionFilter", this.transactionFilter);	   
	        }
	        
	        if(this.authenticationFilter != null)
	        {
	        	cfg.getFilterChain().addLast("authenticationFilter", this.authenticationFilter);
	        }
	        
	        this.acceptor.bind(new InetSocketAddress(this.port), this.handler, cfg);
	        
	        log.info("--------------------------------------------");
	        log.info("Mobile Data Server successfully loaded on port ("+this.port+").....");
	        log.info("--------------------------------------------");
		}
		catch(Exception e)
		{
			log.error(this, e);
			this.stop();
			throw new RuntimeException(e);
		}
	}
	
	
	private SSLContext getSSLContext() throws Exception
	{
		SSLContext sslContext = null;
		
		//Setup the KeyStore
		String keyStorePass = this.keyStorePassword;
		if(keyStorePass == null)
		{
			keyStorePass = "";
		}
		
		KeyStore keyStore = KeyStore.getInstance("JKS");
		FileInputStream fis = new FileInputStream(keyStoreLocation);
		keyStore.load(fis, keyStorePass.toCharArray());
				
		//Setup the KeyManagers for private key management
		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(
		KeyManagerFactory.getDefaultAlgorithm());
		keyManagerFactory.init(keyStore, keyStorePass.toCharArray());
		KeyManager[] keyManager = keyManagerFactory.getKeyManagers();
		
		//Setup TrustManagers for handling trusts with other peers
		TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
		TrustManagerFactory.getDefaultAlgorithm());
		trustManagerFactory.init(keyStore);
		TrustManager[] trustManager = trustManagerFactory.getTrustManagers();
		
		//Create an SSLContext
		sslContext = SSLContext.getInstance("SSL");
		SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
		sslContext.init(keyManager, trustManager, secureRandom);
		
		return sslContext;
	}
}
