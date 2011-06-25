/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.app.simulate;

import android.app.ListActivity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * A simulator activity that simulates generating a Tag process
 *
 * @author openmobster@gmail.com
 */
public class TagSimulatorActivity extends ListActivity
{
	ArrayAdapter<Tag> mAdapter;
	
	@Override
    public void onCreate(Bundle savedState) 
	{
        super.onCreate(savedState);
        
        //Create the ArrayAdapter for the List Activity
        final ArrayAdapter<Tag> adapter = new ArrayAdapter<Tag>(
            this, android.R.layout.simple_list_item_1, android.R.id.text1);
        
        //add entries
        adapter.add(
            new Tag("Broadcast NFC Text Tag", MockData.ENGLISH_PLAIN_TEXT_TAG));
        
        setListAdapter(adapter);
        mAdapter = adapter;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) 
    {
        final Tag description = mAdapter.getItem(position);
        
        //Create a Tag Discovery intent for broadcast
        final Intent intent = new Intent(NfcAdapter.ACTION_TAG_DISCOVERED);
        intent.putExtra(NfcAdapter.EXTRA_NDEF_MESSAGES, description.messages);
        
        //Send the intent
        startActivity(intent);
    }
    
    //Simple class representing a Tag
	private static class Tag
	{
		private String title;
		private NdefMessage[] messages;
		
		private Tag(String title, byte[] message)
		{
			try
			{
				this.title = title;
				this.messages = new NdefMessage[]{new NdefMessage(message)};
			}
			catch(Exception e)
			{
				throw new RuntimeException("Tag creation failed!!!");
			}
		}
		
		@Override
        public String toString() 
		{
            return this.title;
        }
	}
}
