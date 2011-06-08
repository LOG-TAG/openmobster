/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.app;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.content.UriMatcher;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteDatabase;
import android.database.SQLException;

/**
 *
 * @author openmobster@gmail.com
 */
public class TutorialContentProvider extends ContentProvider
{
	public static final String CONTENT_URI = "content://org.openmobster.app.tutorialcontentprovider";
	
	public static final String ID = "id";
	public static final String NAME = "name";
	public static final String VALUE = "value";
	public static final String TABLE = "content_provider_tutorial";
	
	public static final int MATCH_ALL = 1;
	public static final int MATCH_ID = 2;
	public static final String AUTHORITY = "org.openmobster.app.tutorialcontentprovider";
	
	
	private SQLiteOpenHelper sqliteOpenHelper;
	private UriMatcher uriMatcher;
	
	@Override
	public boolean onCreate() 
	{
		//Open the database
		this.sqliteOpenHelper = new DBHelper(this.getContext(),"tutorialdb",null,1);
		((DBHelper)this.sqliteOpenHelper).prePopulate(this.sqliteOpenHelper.getWritableDatabase(), TABLE);
		
		//Setup the UriMatcher
		this.uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		this.uriMatcher.addURI(AUTHORITY, "objects", MATCH_ALL);
		this.uriMatcher.addURI(AUTHORITY, "object/#", MATCH_ID);
		
		return true;
	}
	
	@Override
	public String getType(Uri uri) 
	{
		return "tutorial/content/provider";
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,String sortOrder) 
	{
		//Query building helper class
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		
		//Set the table to query
		builder.setTables(TABLE);
		
		//See if this is a request to get all the objects or just the one with the specified oid
		if(uriMatcher.match(uri) == MATCH_ID)
		{
			builder.appendWhere("id="+uri.getLastPathSegment());
		}
		
		//execute the query
		Cursor cursor = builder.query(this.sqliteOpenHelper.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder,null);
		
		//sets up data watch so that the cursor is notified if data changes after it is returned
		cursor.setNotificationUri(this.getContext().getContentResolver(), uri);
		
		return cursor;
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues values) 
	{
		//Get an instance of writable database
		SQLiteDatabase db = this.sqliteOpenHelper.getWritableDatabase();
		
		//Insert the data into the database
		long newId = db.insert(TABLE, null, values);
		
		//Make sure data is successfully added
		if(newId < 0)
		{
			throw new SQLException("Insert Failure on: "+uri);
		}
		
		//Setup the newUri to identify this newly created object
		Uri newUri = ContentUris.withAppendedId(uri, newId);
		
		//notify the system that a change has been made to the underlying data
		this.getContext().getContentResolver().notifyChange(newUri, null);
		
		return newUri;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) 
	{
		if(this.uriMatcher.match(uri) != MATCH_ID)
		{
			//no object designated for update
			return 0;
		}
		
		//Get an instance of writable database
		SQLiteDatabase db = this.sqliteOpenHelper.getWritableDatabase();
		
		//Find the id of the object being updated
		String id = uri.getLastPathSegment();
		int rowsUpdated = 0;
		if(selection == null || selection.trim().length()==0)
		{
			rowsUpdated = db.update(TABLE, values, "id="+id, null);
		}
		else
		{
			String whereClause = selection + "and" + "id=" + id;
			rowsUpdated = db.update(TABLE, values, whereClause, selectionArgs);
		}
		
		return rowsUpdated;
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) 
	{
		int match = this.uriMatcher.match(uri);
		
		//Get the database
		SQLiteDatabase db = this.sqliteOpenHelper.getWritableDatabase();
		
		int rowsDeleted = 0;
		switch(match)
		{
			case MATCH_ALL:
				rowsDeleted = db.delete(TABLE, selection, selectionArgs);
			break;
			
			case MATCH_ID:
				String id = uri.getLastPathSegment();
				if(selection == null || selection.trim().length()==0)
				{
					String whereClause = "id="+id;
					rowsDeleted = db.delete(TABLE, whereClause, null);
				}
				else
				{
					String whereClause = selection + "and" + "id=" + id;
					rowsDeleted = db.delete(TABLE, whereClause, selectionArgs);
				}
			break;
		}
		
		return rowsDeleted;
	}	
}
