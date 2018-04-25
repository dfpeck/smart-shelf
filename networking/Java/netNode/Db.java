package netNode;

import java.io.File;
import java.io.Serializable;
import java.net.URL;

public class Db implements Serializable{
    private File file;

    public Db () {
        URL url = Db.class.getResource("SendDatabase.db");
		File file = new File(url.getPath());
    }

	public File getFile() {
		return file;
	}
	
	public void setFile(File file) {
		this.file = file;
	}
}