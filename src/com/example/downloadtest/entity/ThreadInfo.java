package com.example.downloadtest.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class ThreadInfo implements Parcelable {
	private int id;
	private String url;
	private int start;
	private int end;
	private int finished;

	public ThreadInfo() {
		
	}
	
	public ThreadInfo(int id, String url, int start, int end, int finished) {
		super();
		this.id = id;
		this.url = url;
		this.start = start;
		this.end = end;
		this.finished = finished;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public int getFinished() {
		return finished;
	}

	public void setFinished(int finished) {
		this.finished = finished;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(url);
		dest.writeInt(start);
		dest.writeInt(end);
		dest.writeInt(finished);
	}

	public static final Parcelable.Creator<ThreadInfo> CREATOR = new Parcelable.Creator<ThreadInfo>() {

		@Override
		public ThreadInfo createFromParcel(Parcel source) {
			return new ThreadInfo(source.readInt(), source.readString(),
					source.readInt(), source.readInt(), source.readInt());
		}

		@Override
		public ThreadInfo[] newArray(int size) {
			return new ThreadInfo[size];
		}
	};

	@Override
	public String toString() {
		return "ThreadInfo [id=" + id + ", url=" + url + ", start=" + start
				+ ", end=" + end + ", finished=" + finished + "]";
	}

}
