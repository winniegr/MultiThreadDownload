package com.example.downloadtest.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import android.os.Environment;

public class FileUtils {
	public static File getSDCardDir() {
		File dir = null;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			dir = Environment.getExternalStorageDirectory();
		}
		return dir;
	}
	public static RandomAccessFile getSDCardFile(String fileName, String mode) {
		File dir = getSDCardDir();
		if (dir == null) {
			return null;
		}
		if (!dir.exists()){
			dir.mkdir();
		}
		File file = new File (dir, fileName);
		try {
			RandomAccessFile raf = new RandomAccessFile(file, mode);
			return raf;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	public static File getSDFile(String fileName) {
		File dir = getSDCardDir();
		if (dir == null) {
			return null;
		}
		if (!dir.exists()){
			dir.mkdir();
		}
		File file = new File (dir, fileName);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		return file;
	}
}
