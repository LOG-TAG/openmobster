/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.test.mock;

import java.io.File;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;

/**
 * @author openmobster@gmail.com
 *
 */
public class Database
{
	private DatabaseHelper helper;
	private Context context;
	private String dbName;
	private int version;
	private SQLiteDatabase db;
	
	public String getDbName()
	{
		return dbName;
	}

	public void setDbName(String dbName)
	{
		this.dbName = dbName;
	}

	public int getVersion()
	{
		return version;
	}

	public void setVersion(int version)
	{
		this.version = version;
	}

	public Database(Context context, String dbName, int version)
	{
		this.context = context;
		this.dbName = dbName;
		this.version = version;
		this.helper = new DatabaseHelper(this.context, this.dbName, this.version);
	}
	//-------------------------------------------------------------------------------------------
	public void open()
	{
		this.db = this.helper.getWritableDatabase();
		System.out.println("DB Opened At: "+this.db.getPath());
	}
	
	public void close()
	{
		this.db.close();
	}
	
	public void delete()
	{
		File file = new File(this.db.getPath());
		file.delete();
	}
	
	public void dump()
	{
		Cursor cursor = this.db.rawQuery("select * from channels", null);
		
		int idIndex = cursor.getColumnIndex("id");
		int pathIndex = cursor.getColumnIndex("path");
		int nameIndex = cursor.getColumnIndex("name");
		
		cursor.moveToFirst();
		do
		{
			String id = cursor.getString(idIndex);
			String path = cursor.getString(pathIndex);
			String name = cursor.getString(nameIndex);
			
			System.out.println("-------------------------------------");
			System.out.println("Id: "+id);
			System.out.println("Path: "+path);
			System.out.println("Name: "+name);
			
			cursor.moveToNext();
		}while(!cursor.isAfterLast());
	}
}
