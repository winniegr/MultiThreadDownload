package com.example.downloadtest;

import java.util.LinkedList;
import java.util.List;

import com.example.downloadtest.entity.FileInfo;
import com.example.downloadtest.service.DownloadService;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class FileInfoAdapter extends BaseAdapter{

	private List<FileInfo> mData = new LinkedList<FileInfo>();
	private Context mContext;
	
	public FileInfoAdapter(Context context) {
		mContext = context;
	}
	
	public void setData(List<FileInfo> list) {
		mData.clear();
		mData.addAll(list);
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mData.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		final FileInfo fileInfo = (FileInfo)getItem(position);
		
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);
			holder = new ViewHolder();
			holder.fileNameView = (TextView)convertView.findViewById(R.id.file_name);
			holder.progressbarView = (ProgressBar)convertView.findViewById(R.id.progressbar);
			holder.startButton= (Button)convertView.findViewById(R.id.start);
			holder.stopButton = (Button)convertView.findViewById(R.id.stop);
			holder.progressbarView.setMax(100);
			convertView.setTag(holder);
			
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		//holder.progressbarView.setTag(position);
		holder.fileNameView.setText(fileInfo.getFileName());
		holder.progressbarView.setProgress(fileInfo.getFinished());
		holder.startButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent startIntent = new Intent(mContext, DownloadService.class);
				startIntent.setAction(DownloadService.ACTION_START);
				startIntent.putExtra("fileInfo", fileInfo);
				mContext.startService(startIntent);
			}
		});
		holder.stopButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent stopIntent  = new Intent(mContext, DownloadService.class);
				stopIntent.setAction(DownloadService.ACTION_STOP);
				stopIntent.putExtra("id", fileInfo.getId());
				mContext.startService(stopIntent);
			}
		});
		return convertView;
	}
	
	class ViewHolder{
		public TextView fileNameView;
		public ProgressBar progressbarView;
		public Button startButton;
		public Button stopButton;
	}
	public void updateProgress(int id, int progress) {
		mData.get(id).setFinished(progress);
		notifyDataSetChanged();
	}
	public void setFileLength(int id, int length) {
		mData.get(id).setLength(length);
	}
}
