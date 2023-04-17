package telran.git.blocks;

import java.io.File;
import java.io.Serializable;

public class FileState implements Serializable{
	
	private static final long serialVersionUID = 1L;
	public Status status;
	File file;
	long timeModif; // or LocalDate with method to ms
	
	
	
	public FileState(Status status, File file, long timeModif) {
		super();
		this.status = status;
		this.file = file;
		this.timeModif = timeModif;
	}

	@Override
	public String toString() {
		return "FileState [status=" + status + ", file=" + file + ", time modified=" + timeModif + "]";
	}

	public FileState(File file) {
		this.file = file;
		timeModif = file.lastModified();
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public long getTimeModif() {
		return timeModif;
	}

	public void setTimeModif(long timeModif) {
		this.timeModif = timeModif;
	}
	
	
	
	
	
}
