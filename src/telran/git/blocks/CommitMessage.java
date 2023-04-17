package telran.git.blocks;

import java.io.Serializable;

public class CommitMessage implements Serializable{

	private static final long serialVersionUID = 1L;
	String message;
	
	String commitName;

	public CommitMessage(String message, String commitName) {
		this.message = message;
		this.commitName = commitName;
	}

	@Override
	public String toString() {
		return "CommitMessage [message=" + message + ", commitName=" + commitName + "]";
	}

	

	
	
	
}
