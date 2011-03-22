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

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 
 * @author openmobster@gmail.com
 */
public final class TicketDAO 
{
	private static SQLiteDatabase db;
	
	public static void initialize(Context context)
	{
		//Open a SQLite Database
		db = context.openOrCreateDatabase("crm.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
		
		//Check for the 'tickets' table and create it if it does not exist
		createTable(db, "tickets");
		
		//Load mock data into 'tickets' table if it is empty
		if(isTableEmpty("tickets"))
		{
			for(int i=0; i<3; i++)
			{
				Ticket local = new Ticket();
				switch(i)
				{
					case 0:
						local.setTitle("Search is down");
						local.setCustomer("Google");
						local.setSpecialist("Larry Page");
						local.setComments("Seach Index Error!!!");
					break;
					
					case 1:
						local.setTitle("Windows is down");
						local.setCustomer("Microsoft");
						local.setSpecialist("Steve Ballmer");
						local.setComments("Blue Screen of Death!!!");
					break;
					
					case 2:
						local.setTitle("MobileMe is down");
						local.setCustomer("Apple");
						local.setSpecialist("Steve Jobs");
						local.setComments("Cannot synchronize data!!!");
					break;
					
					default:
					break;
				}
				insert(local);
			}
		}
	}
	
	private static void createTable(SQLiteDatabase database, String tableName)
	{
		try
		{
			//begin the transaction
			database.beginTransaction();
			
			// Create a table
			String tableSql = "CREATE TABLE IF NOT EXISTS " + tableName + " ("
					+ "id INTEGER PRIMARY KEY AUTOINCREMENT," + "title TEXT," + "customer TEXT," + "specialist TEXT," +"comments TEXT"
					+ ");";
			database.execSQL(tableSql);
			
			//this makes sure transaction is committed
			database.setTransactionSuccessful();
		} 
		finally
		{
			database.endTransaction();
		}
	}
	
	private static boolean isTableEmpty(String table)
	{
		Cursor cursor = null;
		try
		{
			cursor = db.rawQuery("SELECT count(*) FROM "+table, null);
			
			int countIndex = cursor.getColumnIndex("count(*)");
			cursor.moveToFirst();
			int rowCount = cursor.getInt(countIndex);
			if(rowCount > 0)
			{
				return false;
			}
			
			return true;
		}
		finally
		{
			if(cursor != null)
			{
				cursor.close();
			}
		}
	}
	//--------------------------------------------------------------------------------------------------
	public static void insert(Ticket ticket)
	{
		try
		{
			db.beginTransaction();
			
			//insert this row
			String title = ticket.getTitle();
			String customer = ticket.getCustomer();
			String specialist = ticket.getSpecialist();
			String comments = ticket.getComments();
			String insert = "INSERT INTO tickets "+" (title,customer,specialist,comments) VALUES (?,?,?,?);";
			db.execSQL(insert,new Object[]{title,customer,specialist,comments});
			
			db.setTransactionSuccessful();
		}
		finally
		{
			db.endTransaction();
		}
	}
	
	public static List<Ticket> readAll()
	{
		Cursor cursor = null;
		try
		{
			List<Ticket> all = new ArrayList<Ticket>();
			
			cursor = db.rawQuery("SELECT * FROM tickets", null);
			
			if(cursor.getCount() > 0)
			{
				int idIndex = cursor.getColumnIndex("id");
				int titleIndex = cursor.getColumnIndex("title");
				int customerIndex = cursor.getColumnIndex("customer");
				int specialistIndex = cursor.getColumnIndex("specialist");
				int commentsIndex = cursor.getColumnIndex("comments");
				cursor.moveToFirst();
				do
				{
					int id = cursor.getInt(idIndex);
					String title = cursor.getString(titleIndex);
					String customer = cursor.getString(customerIndex);
					String specialist = cursor.getString(specialistIndex);
					String comments = cursor.getString(commentsIndex);
					
					Ticket ticket = new Ticket();
					ticket.setId(id);
					ticket.setTitle(title);
					ticket.setCustomer(customer);
					ticket.setSpecialist(specialist);
					ticket.setComments(comments);
					
					all.add(ticket);
					
					cursor.moveToNext();
				}while(!cursor.isAfterLast());
			}
			
			return all;
		}
		finally
		{
			if(cursor != null)
			{
				cursor.close();
			}
		}
	}
	
	public static void delete(Ticket ticket)
	{
		try
		{
			db.beginTransaction();
			
			//delete this record
			String delete = "DELETE FROM tickets WHERE id='"+ticket.getId()+"'";
			db.execSQL(delete);
			
			db.setTransactionSuccessful();
		}
		finally
		{
			db.endTransaction();
		}
	}
}
