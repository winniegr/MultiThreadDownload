package com.example.downloadtest.db;

import java.util.ArrayList;
import java.util.List;

import com.example.downloadtest.entity.ThreadInfo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ThreadDAO {
	private DBHelper mDBHelper;

	public ThreadDAO(Context context) {
		// TODO Auto-generated constructor stub
		mDBHelper = DBHelper.newInstance(context);
	}

	public synchronized void  insert(ThreadInfo threadInfo) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		db.execSQL(
				"insert into "
						+ mDBHelper.TABLE_NAME
						+ "(thread_id, url, start, end, finished) values(?,?,?,?,?)",
				new Object[] { threadInfo.getId(), threadInfo.getUrl(),
						threadInfo.getStart(), threadInfo.getEnd(),
						threadInfo.getFinished() });
		db.close();
	}

	public synchronized void delete(String url) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		db.execSQL("delete from " + mDBHelper.TABLE_NAME
				+ " where url = ?", new Object[] { url});
		db.close();
	}

	public synchronized void update(String url, int threadId, int finished) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		db.execSQL("update " + mDBHelper.TABLE_NAME
				+ " set finished = ?  where url = ? and thread_id = ?",
				new Object[] { finished, url, threadId });
		db.close();
	}

	public synchronized List<ThreadInfo> getThreadInfos(String url) {
		List<ThreadInfo> threadInfos = new ArrayList<ThreadInfo>();
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from " + mDBHelper.TABLE_NAME + " where url = ?",
				new String[] {url});
		while(cursor.moveToNext()) {
			ThreadInfo threadInfo = new ThreadInfo();
			threadInfo.setId(cursor.getInt(cursor.getColumnIndex("thread_id")));
			threadInfo.setUrl(cursor.getString(cursor.getColumnIndex("url")));
			threadInfo.setStart(cursor.getInt(cursor.getColumnIndex("start")));
			threadInfo.setEnd(cursor.getInt(cursor.getColumnIndex("end")));
			threadInfo.setFinished(cursor.getInt(cursor.getColumnIndex("finished")));
			threadInfos.add(threadInfo);
		}
		cursor.close();
		db.close();
		return threadInfos;
	}

	public synchronized boolean exists(String url, int threadId) {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from " + mDBHelper.TABLE_NAME + " where url = ? and thread_id = ?",
				new String[] {url, threadId + ""});
		boolean isExists = cursor.moveToNext();
		cursor.close();
		db.close();
		return isExists;
	}
}
