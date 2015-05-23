package com.example.downloadtest.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;

import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.downloadtest.db.ThreadDAO;
import com.example.downloadtest.entity.FileInfo;
import com.example.downloadtest.entity.ThreadInfo;
import com.example.downloadtest.utils.FileUtils;

public class DownloadTask {

	private FileInfo mFileInfo;
	private ThreadDAO threadDAO;
	private Context mContext;
	private int mFinished;
	private int mThreadCount;
	private boolean mPaused;
	private List<ThreadInfo> mThreadInfos = new ArrayList<ThreadInfo>();
	private static final String TAG = DownloadTask.class.getSimpleName();

	public DownloadTask(Context context, FileInfo fileInfo, int count) {
		mContext = context;
		this.mFileInfo = fileInfo;
		this.mThreadCount = count;
		this.mFinished = 0;
		threadDAO = new ThreadDAO(context);
	}

	public void download() {
		List<ThreadInfo> list = threadDAO.getThreadInfos(mFileInfo.getUrl());
		if (list == null || list.size() == 0) {
			int len = mFileInfo.getLength() / mThreadCount;
			for (int i = 0; i < mThreadCount; i++) {
				ThreadInfo threadInfo = new ThreadInfo(0, mFileInfo.getUrl(),
						len * i, (i + 1) * len - 1, 0);
				if (i == mThreadCount - 1) {
					threadInfo.setEnd(mFileInfo.getLength());
				}
				list.add(threadInfo);
				threadDAO.insert(threadInfo);
			}
		}
		for (ThreadInfo threadInfo : list) {
			mFinished += threadInfo.getFinished();
			mThreadInfos.add(threadInfo);
			DownloadService.sThreadPool.execute(new DownloadThread(mContext,
					threadInfo));
		}
	}

	class DownloadThread extends Thread {

		ThreadInfo mThreadInfo = null;
		Context mContext = null;

		public DownloadThread(Context context, ThreadInfo threadInfo) {
			mContext = context;
			this.mThreadInfo = threadInfo;
		}

		@Override
		public void run() {
			HttpURLConnection connection = null;
			InputStream inputStream = null;
			RandomAccessFile file = null;
			int start = mThreadInfo.getStart() + mThreadInfo.getFinished();
			int finished = mThreadInfo.getFinished();

			try{
				URL url = new URL(mThreadInfo.getUrl());
				connection = (HttpURLConnection) url.openConnection();
				connection.setConnectTimeout(5000);
				connection.setRequestMethod("GET");
				connection.setRequestProperty("Range", "bytes=" + start + "-"
						+ mThreadInfo.getEnd());

				if (connection.getResponseCode() == HttpStatus.SC_OK
						|| connection.getResponseCode() == HttpStatus.SC_PARTIAL_CONTENT) {
					inputStream = connection.getInputStream();
					file = FileUtils.getSDCardFile(mFileInfo.getFileName(),
							"rwd");
					file.seek(start);
					byte[] buffer = new byte[4096];
					long currentTime = System.currentTimeMillis();
					int len = inputStream.read(buffer);
					while (len != -1) {
						file.write(buffer, 0, len);
						finished += len;
						synchronized (this) {
							mFinished += len;
						}
						if (!mPaused) {
							if (System.currentTimeMillis() - currentTime > 500) {
								currentTime = System.currentTimeMillis();
								//这里会溢出
								// int progress = (int) (((long)mFinished * 100)
								// / mFileInfo.getLength());
								int progress = (int) (((float) mFinished / mFileInfo
										.getLength()) * 100);
								if (progress > 100) {
									progress = 100;
								}
								Log.v(TAG,
										"mFinished = " + mFinished
												+ ", fileLength = "
												+ mFileInfo.getLength()
												+ ", progress = " + progress);
								Intent intent = new Intent(
										DownloadService.ACTION_UPDATE_PROGRESS);
								intent.putExtra("id", mFileInfo.getId());
								intent.putExtra("progress", progress);
								mContext.sendBroadcast(intent);
							}
						} else {
							threadDAO.update(mThreadInfo.getUrl(),
									mThreadInfo.getId(), finished);
							break;
						}
						len = inputStream.read(buffer);
					}
					if (checkIfAllFinished()) {
						threadDAO.delete(mThreadInfo.getUrl());
						Intent intent = new Intent(
								DownloadService.ACTION_FINISHED);
						intent.putExtra("id", mFileInfo.getId());
						mContext.sendBroadcast(intent);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				// TODO
				// 保存下载状态信息
				threadDAO.update(mThreadInfo.getUrl(), mThreadInfo.getId(),
						finished);
			} finally {
				if (connection != null) {
					connection.disconnect();
				}
				if (file != null) {
					try {
						file.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	private synchronized boolean checkIfAllFinished() {
		return mFinished == mFileInfo.getLength();
	}

	public void setPaused(boolean paused) {
		mPaused = paused;
	}
}
