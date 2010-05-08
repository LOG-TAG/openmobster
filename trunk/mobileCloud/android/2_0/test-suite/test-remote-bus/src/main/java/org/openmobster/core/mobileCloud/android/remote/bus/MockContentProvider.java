/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.android.remote.bus;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.database.MatrixCursor;

/**
 * @author openmobster@gmail.com
 *
 */
public class MockContentProvider extends ContentProvider
{
	@Override
	public boolean onCreate()
	{
		return true;
	}

	@Override
	public String getType(Uri uri)
	{
		return "vnd.android.cursor.dir/vnd.openmobster.mock";
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, 
	String[] selectionArgs,
	String sortOrder)
	{
		String path = uri.getPath();
		
		System.out.println("------------------------------------------");
		System.out.println("Path: "+path);
		System.out.println("------------------------------------------");
		
		MatrixCursor cursor = new MatrixCursor(new String[]{"mock1","mock2"});		
		cursor.addRow(new String[]{"mock1://blah1","mock2://blah2"});
		cursor.addRow(new String[]{"mock1://blah3","mock2://blah4"});
		cursor.addRow(new String[]{"mock1://blah5","mock2://blah6"});
		
		return cursor;
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs)
	{
		return 0;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values)
	{
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
	{
		return 0;
	}
}
