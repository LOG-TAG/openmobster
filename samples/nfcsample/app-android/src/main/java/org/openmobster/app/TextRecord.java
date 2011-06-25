/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.app;

import java.util.Arrays;

import android.nfc.NdefRecord;

import com.google.common.base.Preconditions;

/**
 * Creates a POJO TextRecord object from an NdefRecord object
 *
 * @author openmobster@gmail.com
 */
public class TextRecord 
{
	private String text;
	private String languageCode;
	
	public TextRecord(String text, String languageCode)
	{
		//Validate
		if(text == null || text.trim().length() == 0)
		{
			throw new IllegalArgumentException("Text is required!!");
		}
		
		//Validate
		if(languageCode == null || languageCode.trim().length() == 0)
		{
			throw new IllegalArgumentException("LanguageCode is required!!");
		}
		
		this.text = text;
		this.languageCode = languageCode;
	}
	
	public String getText()
	{
		return this.text;
	}
	
	public String getLanguageCode()
	{
		return this.languageCode;
	}
	
	public static TextRecord parse(NdefRecord record)
	{
		//Validation
		Preconditions.checkArgument(record.getTnf() == NdefRecord.TNF_WELL_KNOWN);
        Preconditions.checkArgument(Arrays.equals(record.getType(), NdefRecord.RTD_TEXT));
        try
        {
        	byte[] payload = record.getPayload();
        	
        	/*
             * payload[0] contains the "Status Byte Encodings" field, per the
             * NFC Forum "Text Record Type Definition" section 3.2.1.
             *
             * bit7 is the Text Encoding Field.
             *
             * if (Bit_7 == 0): The text is encoded in UTF-8 if (Bit_7 == 1):
             * The text is encoded in UTF16
             *
             * Bit_6 is reserved for future use and must be set to zero.
             *
             * Bits 5 to 0 are the length of the IANA language code.
             */

        	 //Get the Text Encoding
        	String textEncoding = ((payload[0] & 0200) == 0) ? "UTF-8" : "UTF-16";
        	
        	//Get the Language Code
        	int languageCodeLength = payload[0] & 0077;
        	String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
        	
        	//Get the Text
        	String text = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        	
            return new TextRecord(text, languageCode);
        }
        catch(Exception e)
        {
        	throw new RuntimeException("Record Parsing Failure!!");
        }
	}
}
