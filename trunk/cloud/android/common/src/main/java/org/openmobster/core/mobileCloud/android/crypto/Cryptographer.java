/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.android.crypto;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

import org.openmobster.core.mobileCloud.android.service.Service;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.util.Base64;

/**
 *
 * @author openmobster@gmail.com
 */
public class Cryptographer extends Service
{
	private byte[] secretKey;
	
	public Cryptographer()
	{
		
	}

	@Override
	public void start()
	{	
		try
		{
			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			keyGenerator.init(256);
			this.secretKey = keyGenerator.generateKey().getEncoded();
		}
		catch(Throwable t)
		{
			SystemException sys = new SystemException(Cryptographer.class.getName(), "start", new String[]{
				"Throwable: "+t.toString(),
				"Message: "+t.getMessage()
			});
			throw sys;
		}
	}

	@Override
	public void stop()
	{
		this.secretKey = null;
	}
	
	public static Cryptographer getInstance()
	{
		return (Cryptographer)Registry.getActiveInstance().lookup(Cryptographer.class);
	}
	//----------------------------------------------------------------------------------------
	public String encrypt(byte[] data)
	{
		try
		{
			//AES encription
		    Cipher c = Cipher.getInstance("AES");
		    SecretKeySpec k = new SecretKeySpec(this.secretKey, "AES");
		    c.init(Cipher.ENCRYPT_MODE, k);
		    byte[] encryptedBytes = c.doFinal(data);
		    return Base64.encodeBytes(encryptedBytes);
		}
		catch(Throwable t)
		{
			SystemException sys = new SystemException(Cryptographer.class.getName(), "encrypt", new String[]{
				"Throwable: "+t.toString(),
				"Message: "+t.getMessage()
			});
			throw sys;
		}
	}
	
	public String decrypt(String encoded)
	{
		try
		{
			Cipher c = Cipher.getInstance("AES");
			SecretKeySpec k = new SecretKeySpec(this.secretKey, "AES");
			c.init(Cipher.DECRYPT_MODE, k);
			
			byte[] encryptedBytes = Base64.decode(encoded);
			
			byte[] decryptedBytes = c.doFinal(encryptedBytes);
			
			return new String(decryptedBytes);
		}
		catch(Throwable t)
		{
			SystemException sys = new SystemException(Cryptographer.class.getName(), "decrypt", new String[]{
				"Throwable: "+t.toString(),
				"Message: "+t.getMessage()
			});
			throw sys;
		}
	}
}
