package telran.git.blocks;

import java.io.Serializable;
import java.time.Instant;

import java.util.Map;

public class Commit implements Serializable {
	
	private static final long serialVersionUID = 1L;
	String commitName;
	CommitMessage commitMessage;
	Instant time;
	Map<FileState, String[]> conditionsOfFiles; 
	
	
	
	public Commit(String commitName, CommitMessage commitMessage, Instant time, Map<FileState, String[]> fileCond) {
		super();
		this.commitName = commitName;
		this.commitMessage = commitMessage;
		this.time = time;
		this.conditionsOfFiles = fileCond;
	}


	public String getCommitName() {
		return commitName;
	}


	public void setCommitName(String commitName) {
		this.commitName = commitName;
	}


	public CommitMessage getCommitMessage() {
		return commitMessage;
	}


	public void setCommitMessage(CommitMessage commitMessage) {
		this.commitMessage = commitMessage;
	}


	public Instant getTime() {
		return time;
	}


	public void setTime(Instant time) {
		this.time = time;
	}


	public Map<FileState, String[]> getFileCond() {
		return conditionsOfFiles;
	}


	public void setFileCond(Map<FileState, String[]> fileCond) {
		this.conditionsOfFiles = fileCond;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
//	List<CommitFile> comFiles;
	
	
}
