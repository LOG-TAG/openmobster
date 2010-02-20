/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.test.mock;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author openmobster@gmail.com
 * 
 */
public class DatabaseHelper extends SQLiteOpenHelper
{
	public DatabaseHelper(Context context, String name, int version)
	{
		super(context, name, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase database)
	{
		try
		{
			database.beginTransaction();
			// Create a table
			String tableName = "channels";
			String tableSql = "CREATE TABLE IF NOT EXISTS " + tableName + " ("
					+ "id INTEGER PRIMARY KEY," + "path TEXT," + "name TEXT"
					+ ");";
			database.execSQL(tableSql);

			this.loadData(database);
			
			//this makes sure transaction is committed
			database.setTransactionSuccessful();
		} 
		finally
		{
			database.endTransaction();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion)
	{
	}

	// --------------------------------------------------------------------------------------
	private void loadData(SQLiteDatabase database)
	{
		// insert a couple of rows
		for (int i = 0; i < 2; i++)
		{
			String path = "/channels/email/" + i;
			String name = "email/" + i;
			String insert = "INSERT INTO channels (path,name) VALUES ('"
					+ path + "','" + name + "');";
			database.execSQL(insert);
		}
	}
}
