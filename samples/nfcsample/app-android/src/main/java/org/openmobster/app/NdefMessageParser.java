/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.app;

import java.util.List;
import java.util.ArrayList;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;

/**
 * A utility class for creating TextRecords for a give NdefMessage
 * 
 * @author openmobster@gmail.com
 */
public class NdefMessageParser 
{
	private NdefMessageParser()
	{
		
	}
	
	public static List<TextRecord> parse(NdefMessage message)
	{
		List<TextRecord> textRecords = new ArrayList<TextRecord>();
		
		//Get the Records inside the message
		NdefRecord[] records = message.getRecords();
		
		//Iterate through and generate a list of text records
		if(records != null && records.length>0)
		{
			for(NdefRecord local: records)
			{
				TextRecord textRecord = TextRecord.parse(local);
				textRecords.add(textRecord);
			}
		}
		
		return textRecords;
	}
}
