/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.android.crypto;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android.util.Base64;

/**
 *
 * @author openmobster@gmail.com
 */
public final class CryptoManager
{
	private static CryptoManager singleton;
	
	private CryptoManager()
	{
		
	}
	
	public static CryptoManager getInstance()
	{
		if(CryptoManager.singleton == null)
		{
			synchronized(CryptoManager.class)
			{
				if(CryptoManager.singleton == null)
				{
					CryptoManager.singleton = new CryptoManager();
				}
			}
		}
		return CryptoManager.singleton;
	}
	
	public void start()
	{
		try
		{
			Uri uri = Uri.parse("content://org.openmobster.core.mobileCloud.android.provider.crypto.secret.key");
			
			//Read the secret key from the provider
			ContentResolver resolver = Registry.getActiveInstance().getContext().getContentResolver();
			Cursor cursor = resolver.query(uri, 
					null, 
					null, 
					null, 
					null);
			
			if(cursor == null || cursor.getCount()==0)
			{
				this.stop();
				return;
			}
			
			cursor.moveToFirst();
			int index = cursor.getColumnIndex("secret-key");
			String secretKey = cursor.getString(index);
			
			//set the secret key for the Cryptographer
			byte[] secretKeyBytes = Base64.decode(secretKey);
			Cryptographer.getInstance().setSecretKey(secretKeyBytes);
		}
		catch(Throwable t)
		{
			t.printStackTrace(System.out);
			this.stop();
		}
	}
	
	public void stop()
	{
		Cryptographer.stop();
		CryptoManager.singleton = null;
	}
}
