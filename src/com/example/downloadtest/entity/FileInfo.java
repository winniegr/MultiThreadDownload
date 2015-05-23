package com.example.downloadtest.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class FileInfo implements Parcelable {
	private int id;
	private String fileName;
	private String url;
	private int length;
	private int finished;

	public FileInfo(int id, String fileName, String url, int length, int finished) {
		super();
		this.id = id;
		this.fileName = fileName;
		this.url = url;
		this.length = length;
		this.finished = finished;
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
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
		dest.writeString(fileName);
		dest.writeString(url);
		dest.writeInt(length);
		dest.writeInt(finished);
	}

	public static final Parcelable.Creator<FileInfo> CREATOR = new Parcelable.Creator<FileInfo>() {

		@Override
		public FileInfo createFromParcel(Parcel source) {
			return new FileInfo(source.readInt(), source.readString(), source.readString(),
					source.readInt(), source.readInt());
		}

		@Override
		public FileInfo[] newArray(int size) {
			return new FileInfo[size];
		}
	};

	@Override
	public String toString() {
		return "FileInfo [id=" + id + ", fileName=" + fileName + ", url=" + url
				+ ", length=" + length + ", finished=" + finished + "]";
	}

}
