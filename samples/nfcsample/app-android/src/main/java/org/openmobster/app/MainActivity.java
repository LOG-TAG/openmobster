/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.app;

import java.util.List;

import android.app.ListActivity;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.os.Parcelable;
import android.util.Log;

/**
 * @author openmobster@gmail.com
 * 
 */
public class MainActivity extends ListActivity
{
	private ArrayAdapter<String> adapter;
	
	public MainActivity()
	{
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		//Create the ArrayAdapter for the List Activity
        this.adapter = new ArrayAdapter<String>(
         this, android.R.layout.simple_list_item_1, android.R.id.text1);
        
        setListAdapter(adapter);
        
        //Process the intent that started this activity. It should be a Tag Discovery related intent
        this.resolveIntent(this.getIntent());
	}
	
	@Override
    public void onNewIntent(Intent intent) 
	{
        setIntent(intent);
        resolveIntent(intent);
    }
	
	private void resolveIntent(Intent intent)
	{
		//Get the Action
		String action = intent.getAction();
		
		if(action.equals(NfcAdapter.ACTION_TAG_DISCOVERED))
		{
			Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs;
            
            if (rawMsgs != null) 
            {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) 
                {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            } 
            else 
            {
                // Unknown tag type
                byte[] empty = new byte[] {};
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
                NdefMessage msg = new NdefMessage(new NdefRecord[] {record});
                msgs = new NdefMessage[] {msg};
            }
            
            //Display the received messages
            List<TextRecord> records = NdefMessageParser.parse(msgs[0]);
            this.adapter.clear();
            
            for(TextRecord local:records)
            {
            	String text = local.getText();
            	this.adapter.add(text);
            }
		}
		else
		{
			Log.e("NFC/MainActivity", "Unknown intent " + intent);
            finish();
            return;
		}
	}
}
