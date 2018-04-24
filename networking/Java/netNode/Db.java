package db;

import java.io.File;

import java.io.FileNotFoundException;

public class Db {
    private File file;

    public Db () {
        URL url = TEST_NetMat.class.getResource("SendDatabase.db");
		File file = new File(url.getPath());
    }

	public File getFile() {
		return file;
	}
	
	public File setFile(File file) {
		this.file = file;
	}
}