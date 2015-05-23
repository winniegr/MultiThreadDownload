package com.example.downloadtest.service;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.http.HttpStatus;

import com.example.downloadtest.MainActivity;
import com.example.downloadtest.entity.FileInfo;
import com.example.downloadtest.utils.FileUtils;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.text.InputFilter.LengthFilter;
import android.util.Log;

public class DownloadService extends Service {
	
	public static final String ACTION_START = "com.example.downloadtest.action_start";
	public static final String ACTION_STOP = "com.example.downloadtest.action_stop";
	public static final String ACTION_FINISHED= "com.example.downloadtest.action_finished";
	public static final String ACTION_UPDATE_PROGRESS = "com.example.downloadtest.action_update_progress";
	public static final String ACTION_SET_FILE_LENGTH = "com.example.downloadtest.action_set_file_length";
	public static final int MSG_INIT = 1;
	public static ExecutorService sThreadPool = Executors.newCachedThreadPool();
	private LinkedHashMap<Integer, DownloadTask> taskMaps = new LinkedHashMap<Integer, DownloadTask>();
	
	private static final String TAG = DownloadService.class.getSimpleName();

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public void onCreate() {
		super.onCreate();
	}
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == MSG_INIT){
				FileInfo fileInfo = (FileInfo)msg.obj;
				DownloadTask task = new DownloadTask(DownloadService.this, fileInfo, 3);
				task.download();
				taskMaps.put(fileInfo.getId(), task);
			}
		};
	};
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			if (DownloadService.ACTION_START.equals(intent.getAction())) {
				FileInfo fileInfo = intent.getParcelableExtra("fileInfo");
				if (taskMaps.containsKey(fileInfo.getId())) {
					taskMaps.get(fileInfo.getId()).setPaused(false);
				} 
				if (fileInfo.getLength() == 0) {
					sThreadPool.execute(new InitThread(fileInfo));
				} else {
					Message msg = new Message();
					msg.what = MSG_INIT;
					msg.obj = fileInfo;
					handler.sendMessage(msg);
				}
			} else if (DownloadService.ACTION_STOP.equals(intent.getAction())) {
				int id = intent.getIntExtra("id", -1);
				if (taskMaps.containsKey(id)) {
					taskMaps.get(id).setPaused(true);
				}
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
	class InitThread extends Thread {
		
		private FileInfo mFileInfo;
		
		public InitThread(FileInfo fileInfo) {
			mFileInfo = fileInfo;
		}
		@Override
		public void run() {
			if (mFileInfo == null) {
				return;
			}
			String urlString = mFileInfo.getUrl();
			if (TextUtils.isEmpty(urlString)){
				return;
			}
			HttpURLConnection connection = null;
			//RandomAccessFile raf = null;
			try {
				URL url = new URL(urlString);
				connection = (HttpURLConnection) url.openConnection();
				if (connection.getResponseCode() == HttpStatus.SC_OK) {
					int length = connection.getContentLength();
//					raf = FileUtils.getSDCardFile(mFileInfo.getFileName(), "rwd");
//					if (raf == null) {
//						return;
//					}
//					raf.setLength(length);
					if (length > 0) {
						Intent intent = new Intent(DownloadService.this, MainActivity.class);
						intent.setAction(ACTION_SET_FILE_LENGTH);
						intent.putExtra("length", length);
						intent.putExtra("id", mFileInfo.getId());
						sendBroadcast(intent);
						mFileInfo.setLength(length);
						Message msg = new Message();
						msg.what = MSG_INIT;
						msg.obj = mFileInfo;
						handler.sendMessage(msg);
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}finally {
				if (connection != null) {
					connection.disconnect();
				}
//				if (raf != null) {
//					try {
//						raf.close();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
			}
			
		}
	}

}
