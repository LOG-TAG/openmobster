/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.app;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

/**
 *
 * @author openmobster@gmail.com
 */
public class DBHelper extends SQLiteOpenHelper 
{
	public DBHelper(Context context, String name, CursorFactory factory, int version) 
	{
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) 
	{
		try
		{
			db.beginTransaction();
			
			//Name of the table to be created
			String tableName = "content_provider_tutorial";
			
			// Create a table
			String tableSql = "CREATE TABLE IF NOT EXISTS " + tableName + " ("
					+ "id INTEGER PRIMARY KEY AUTOINCREMENT," + "name TEXT," + "value TEXT"
					+ ");";
			db.execSQL(tableSql);
			
			//Pre-Populate the table if it is empty
			this.prePopulate(db, tableName);
			
			
			//this makes sure transaction is committed
			db.setTransactionSuccessful();
		} 
		finally
		{
			db.endTransaction();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{	
	}
	
	
	public void prePopulate(SQLiteDatabase db, String table)
	{
		Cursor cursor = null;
		try
		{
			cursor = db.rawQuery("SELECT count(*) FROM "+table, null);
			
			//Check if the Table is Empty
			int countIndex = cursor.getColumnIndex("count(*)");
			cursor.moveToFirst();
			int rowCount = cursor.getInt(countIndex);
			if(rowCount > 0)
			{
				return;
			}
			
			//Table is empty need to pre-populate
			for(int i=0; i<5; i++)
			{
				ContentValues values = new ContentValues();
				values.put("name", "NAME://"+i);
				values.put("value", "VALUE://"+i);
				
				db.insert(table, null, values);
			}
		}
		finally
		{
			if(cursor != null)
			{
				cursor.close();
			}
		}
	}
}
