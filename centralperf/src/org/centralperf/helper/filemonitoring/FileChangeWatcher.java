package org.centralperf.helper.filemonitoring;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.TimerTask;

public abstract class FileChangeWatcher extends TimerTask {

	private long timeStamp;
	private File file;
	static String s;
    private long addFileLen;

	public FileChangeWatcher(File file) {
		addFileLen = file.length();
		this.file = file;
		this.timeStamp = file.lastModified();
	}

	public final void run() {
		long timeStamp = file.lastModified();
		if (this.timeStamp != timeStamp) {
			this.timeStamp = timeStamp;
			onChange(file);
		}
	}

	/**
	 * Triggered when the monitored file has changed
	 * Can be overrided if necessary
	 */
	protected void onChange(File file) {
		RandomAccessFile access = null;
		try {
			access = new RandomAccessFile(file, "r");
			if (file.length() < addFileLen) {
				access.seek(file.length());
			} else {
				access.seek(addFileLen);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		String line = null;
		try {

			while ((line = access.readLine()) != null) {
				processLine(line);
			}

			addFileLen = file.length();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		try {
			access.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Triggered when a new line is read in the monitored file
	 * Can be overrided if necessary
	 */
	protected abstract void processLine(String line);
}
