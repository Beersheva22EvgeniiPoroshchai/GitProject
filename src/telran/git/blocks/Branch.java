package telran.git.blocks;

import java.io.Serializable;
import java.util.Objects;

public class Branch implements Serializable {
	
	private static final long serialVersionUID = 1L;
	String branchName;
	Commit commitName;
	
	public Branch(String branchName, Commit commitName) {
		super();
		this.branchName = branchName;
		this.commitName = commitName;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	@Override
	public int hashCode() {
		return Objects.hash(branchName, commitName);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Branch other = (Branch) obj;
		return Objects.equals(branchName, other.branchName) && Objects.equals(commitName, other.commitName);
	}

	@Override
	public String toString() {
		return "Branch [branchName=" + branchName + ", commitName=" + commitName + "]";
	}
	
	
	
	
	

}
