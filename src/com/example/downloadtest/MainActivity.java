package com.example.downloadtest;

import java.util.ArrayList;
import java.util.List;

import com.example.downloadtest.entity.FileInfo;
import com.example.downloadtest.service.DownloadService;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	List<FileInfo> mFileInfos = new ArrayList<FileInfo>();
	FileInfoAdapter mAdapter = null;
	Context context;
	private static final String TAG = MainActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ListView listView = (ListView) findViewById(R.id.list);
		initData();
		mAdapter = new FileInfoAdapter(this);
		mAdapter.setData(mFileInfos);
		listView.setAdapter(mAdapter);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(DownloadService.ACTION_UPDATE_PROGRESS);
		intentFilter.addAction(DownloadService.ACTION_FINISHED);
		//intentFilter.addAction(DownloadService.ACTION_SET_FILE_LENGTH);
		registerReceiver(receiver, intentFilter);
		context = this;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(receiver);
	}

	public void initData() {

		FileInfo file1 = new FileInfo(
				0,
				"百度糯米.apk",
				"http://gdown.baidu.com/data/wisegame/03c669a6317624c7/baidunuomi_129.apk",
				0, 0);
		FileInfo file2 = new FileInfo(
				1,
				"手机百度.apk",
				"http://gdown.baidu.com/data/wisegame/6fe7456c3e3c7afb/shoujibaidu_16787209.apk",
				0, 0);
		FileInfo file3 = new FileInfo(
				2,
				"百度视频.apk",
				"http://gdown.baidu.com/data/wisegame/177ed2b7908c6dd1/baidushipin_1071102822.apk",
				0, 0);
		FileInfo file4 = new FileInfo(
				3,
				"百度手机助手.apk",
				"http://gdown.baidu.com/data/wisegame/01b8d9054ff3210b/baidushoujizhushou_16785259.apk",
				0, 0);
		FileInfo file5 = new FileInfo(
				4,
				"百度手机卫士.apk",
				"http://gdown.baidu.com/data/wisegame/5394dc7cabf25e8e/baidushoujiweishi_1988.apk",
				0, 0);
		mFileInfos.add(file1);
		mFileInfos.add(file2);
		mFileInfos.add(file3);
		mFileInfos.add(file4);
		mFileInfos.add(file5);
	}

	BroadcastReceiver receiver = new BroadcastReceiver() {
		public void onReceive(android.content.Context context, Intent intent) {
			if (DownloadService.ACTION_UPDATE_PROGRESS.equals(intent
					.getAction())) {
				int progress = intent.getIntExtra("progress", 0);
				int id = intent.getIntExtra("id", -1);
				if (id >= 0) {
					mAdapter.updateProgress(id, progress);
				}
			} else if (DownloadService.ACTION_FINISHED.equals(intent
					.getAction())) {
				int id = intent.getIntExtra("id", -1);
				if (id >=0) {
					mAdapter.updateProgress(id, 100);
					Toast.makeText(
							context,
							mFileInfos.get(id).getFileName()
									+ " finished downloading", Toast.LENGTH_SHORT)
							.show();
				}

//			} else if (DownloadService.ACTION_SET_FILE_LENGTH.endsWith(intent.getAction())){
//				int length = intent.getIntExtra("length", 0);
//				int id = intent.getIntExtra("id", 0);
//				if (id >= 0 && length > 0) {
//					mAdapter.setFileLength(id, length);
//				}
			}
		};
	};
}
