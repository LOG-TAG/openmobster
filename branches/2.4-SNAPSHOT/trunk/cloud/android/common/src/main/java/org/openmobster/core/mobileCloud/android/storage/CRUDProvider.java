/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.android.storage;

import java.util.Set;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;

/**
 *
 * @author openmobster@gmail.com
 */
public interface CRUDProvider
{
	void init(SQLiteDatabase db);
	
	void cleanup();
	
	String insert(String table, Record record) throws DBException;
	
	Set<Record> selectAll(String from) throws DBException;
	
	long selectCount(String from) throws DBException;
	
	Record select(String from, String recordId) throws DBException;
	
	Set<Record> select(String from, String name, String value) throws DBException;
	
	Set<Record> selectByValue(String from, String value) throws DBException;
	
	Set<Record> selectByNotEquals(String from, String value) throws DBException;
	
	Set<Record> selectByContains(String from, String value) throws DBException;
	
	void update(String table, Record record) throws DBException;
	
	void delete(String table, Record record) throws DBException;
	
	void deleteAll(String table) throws DBException;
	
	Cursor testCursor(String table) throws DBException;
	
	Cursor readProxyCursor(String table) throws DBException;
}
