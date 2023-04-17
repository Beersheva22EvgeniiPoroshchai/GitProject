package telran.git.blocks;

import java.io.Serializable;

public class Head implements Serializable {

	private static final long serialVersionUID = 1L;
	private Branch currentBranch;
	private Commit currentCommit;
	
	
	public Branch getCurrentBranch() {
		return currentBranch;
	}
	public void setCurrentBranch(Branch currentBranch) {
		this.currentBranch = currentBranch;
	}
	public Commit getCurrentCommit() {
		return currentCommit;
	}
	public void setCurrentCommit(Commit currentCommit) {
		this.currentCommit = currentCommit;
	}
	
	
	
	
	
}
