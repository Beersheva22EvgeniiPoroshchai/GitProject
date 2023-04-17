package telran.git;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import telran.git.blocks.Branch;
import telran.git.blocks.Commit;
import telran.git.blocks.CommitMessage;
import telran.git.blocks.FileState;
import telran.git.blocks.Head;

import telran.git.blocks.Status;

public class GitRepositoryImpl implements GitRepository {
	private static final long serialVersionUID = 1L;

	private Head head;

	Map<Branch, LinkedList<Commit>> commits;

	private static String gitPath;

	public GitRepositoryImpl(String git) {
		gitPath = git;
		head = new Head();
		commits = new HashMap<>();

	}

	public static GitRepositoryImpl init() throws IOException {
		GitRepositoryImpl gitRepos = new GitRepositoryImpl(".");
		File fileGit = new File(GIT_FILE);

		if (!fileGit.createNewFile()) {
			try (ObjectInputStream input = new ObjectInputStream(Files.newInputStream(Path.of(fileGit.getPath())))) {
				gitRepos = (GitRepositoryImpl) input.readObject();
			} catch (Exception e) {
				e.printStackTrace();

			}

		}
		return gitRepos;
	}

	@Override
	public String commit(String commitMessage) {
		String res = null;
		if (head.getCurrentBranch() == null) {
			Branch branch = new Branch("master", null);
			commits.put(branch, new LinkedList<>());
			head.setCurrentBranch(branch);
		}

		if (isHeadinLastCommit()) {
			List<FileState> states = checkInfoStateFiles();

			if (isUncommitted(states)) {
				String name = createComName();
				Commit commit = new Commit(name, new CommitMessage(commitMessage, name), Instant.now(),
						createMapFileCond(states));
				commits.get(head.getCurrentBranch()).add(commit);
				head.setCurrentCommit(commit);
				res = "Commit has been done";

			} else {
				res = "Nothing to commit";
			}

		} else {
			res = "Commit is not last";
		}

		return res;
	}

	private boolean isUncommitted(List<FileState> actualStates) {
		return actualStates.stream().anyMatch(
				fileState -> fileState.getStatus() == Status.UNTRACKED || fileState.getStatus() == Status.MODIFIED);
	}

	private boolean isHeadinLastCommit() {
		boolean res = true;
		if (head.getCurrentCommit() != null && head.getCurrentBranch() != null && !commits.isEmpty()
				&& head.getCurrentCommit() != null) {
			res = commits.get(head.getCurrentBranch()).getLast() == head.getCurrentCommit();
		}
		return res;
	}

	private String createComName() {

		Random rand = new Random();
		String str = rand.ints(48, 123).filter(num -> (num < 58 || num > 64) && (num < 91 || num > 96)).limit(7)
				.mapToObj(c -> (char) c).collect(StringBuffer::new, StringBuffer::append, StringBuffer::append)
				.toString();
		return str;
	}

	private List<FileState> checkInfoStateFiles() {
		List<FileState> states = new ArrayList<>();
		File directory = new File(gitPath);
		if (directory.exists() && directory.isDirectory()) {
			File[] files = directory.listFiles(file -> file.getName().matches("(\\w|\\d)+[.]txt"));
			if (files != null) {
				Arrays.stream(files).forEach(file -> states.add(new FileState(file)));
				if (head.getCurrentCommit() == null) {
					states.forEach(state -> state.setStatus(Status.UNTRACKED));
				} else {
					states.forEach(state -> {
						FileState headState = head.getCurrentCommit().getFileCond().keySet()
								.stream().filter(s -> s.getFile().equals(state.getFile())).findFirst().get(); // not
																												// null
							if (headState != null) {
							if (state.getFile().lastModified() != headState.getTimeModif()) {
								state.setStatus(Status.MODIFIED);
							} else {
								state.setStatus(Status.COMMITTED);
							}
						} else {
							state.setStatus(Status.UNTRACKED);
						}
					});
				}
			}
		}
		return states;

	}

	private Map<FileState, String[]> createMapFileCond(List<FileState> states) {
		Map<String, String[]> contentMap = getContentFromFiles(states);
		LinkedHashMap<FileState, String[]> res = new LinkedHashMap<>();

		for (FileState fileState : states) {
			res.put(fileState, contentMap.get(fileState.getFile().getName()));

		}

		return res;

	}

	private Map<String, String[]> getContentFromFiles(List<FileState> states) {
		Map<String, String[]> filesContent = new HashMap<>();
		for (FileState fileState : states) {
			try (BufferedReader reader = Files.newBufferedReader(Paths.get(fileState.getFile().getPath()))) {
				filesContent.put(fileState.getFile().getName(), reader.lines().toArray(String[]::new));
			} catch (IOException e) {
				throw new RuntimeException(e.getMessage());
			}
		}
		return filesContent;
	}

	@Override
	public List<FileState> info() {
		List<FileState> listFileStates = checkInfoStateFiles();
		return listFileStates;
	}

	@Override
	public String createBranch(String branchName) {
		String res = null;
		if (commits.values().isEmpty()) {
			res = "Branch or commit does not exist";
		} else if (commits.values().contains(branchName)) {
			res = "Branch or commit with this name is already exist";
		} else {
			Branch branch = new Branch(branchName, head.getCurrentCommit());
			commits.put(branch, new LinkedList<>());
			 head.setCurrentBranch(branch);
			head.setCurrentCommit(null);
			res = "Branch " + branchName + " has been created";
		}

		return res;

	}

	@Override
	public String renameBranch(String branchName, String newName) {
		String res = "Branch " + branchName + " does not exist";
		Set<Branch> branch = commits.keySet();
		Iterator<Branch> iterBr = branch.iterator();
		boolean flag = true;
		while (iterBr.hasNext()) {
			Branch curBr = iterBr.next();
			LinkedList<Commit> commit = commits.get(curBr);
			if (curBr.getBranchName().equals(branchName)) {
				flag = false;
				curBr.setBranchName(newName);
				res = "Branch: " + branchName + " has been renamed to " + newName;  
		}
			
		}
			return res;
	}

	@Override
	public String deleteBranch(String branchName) {
		String res = "Branch " + branchName + " does not exist";
		Set<Branch> branch = commits.keySet();
		Iterator<Branch> iter = branch.iterator();
		boolean flag = true;
		while (iter.hasNext() && flag) {
			Branch curBranch = iter.next();
			if (curBranch.getBranchName().equals(branchName)) {
				flag = false;
				if (!head.getCurrentBranch().getBranchName().equals(branchName)) {
					commits.remove(curBranch);
					res = "Branch " + branchName + " has been removed";
				} else {
					res = "Current branch is impossible to delete";
				}

			}
		}
		return res;
	}

	@Override
	public List<CommitMessage> log() {
		List<CommitMessage> res = new ArrayList<>();
		commits.get(head.getCurrentBranch()).forEach(c -> res.add(c.getCommitMessage()));
		return res;

	}

	@Override
	public List<String> branches() {
		List<String> branches = new ArrayList<>();
		Set<Branch> setBr = commits.keySet();
		for (Branch branch : setBr) {
			String br = branch.getBranchName();
			if (br.equals(head.getCurrentBranch().getBranchName())) {
				br = "*" + br;
			}
			branches.add(br);
		}
		return branches;

	}

	@Override
	public List<Path> commitContent(String commitName) {
		List<Path> listOfPathes = new LinkedList<>();
		Commit commit = null;
		if (!commits.isEmpty()) {
			commit = getCommitByName(commitName);
		}
		if (commit != null)
			commit.getFileCond().keySet().forEach(state -> listOfPathes.add(Paths.get(state.getFile().getPath())));
		return listOfPathes;
	}



	private Branch getBranchByName(String nameBranch) {
		Branch branchByName = null;
		Set<Branch> setBranches = commits.keySet();
		Iterator<Branch> iterBranch = setBranches.iterator();

		while (iterBranch.hasNext() && branchByName == null) {

			Branch curBranch = iterBranch.next();
			if (curBranch.getBranchName().equals(nameBranch)) {
				branchByName = curBranch;
			}
		}
		return branchByName;
	}

	private Commit getCommitByName(String name) {
		Commit commit = null;
		boolean flag = true;
		Iterator<Branch> iter = commits.keySet().iterator();
		while (iter.hasNext() && flag) {
			Iterator<Commit> iterCom = commits.get(iter.next()).iterator();
			while (iterCom.hasNext() && flag) {
				Commit current = iterCom.next();
				if (current.getCommitName().equals(name)) {
					commit = current;
					flag = false;
				}
			}
		}
		return commit;
	}

	public String switchTo(String name) {
		String res = "Branch has been switched to " + name;
		boolean flag = false;
		List<FileState> actualState = checkInfoStateFiles();
		if (!isUncommitted(actualState)) {
			Branch foundBranch = getBranchByName(name);
			if (foundBranch != null) {
				head.setCurrentBranch(foundBranch);
				head.setCurrentCommit(commits.get(foundBranch).getLast());
				flag = true;
				
			} else {
				Commit commit = null;
				Branch branch = null;
				Iterator<Branch> iter = commits.keySet().iterator();
				while (iter.hasNext() && branch == null) {
					Branch temp = iter.next();
					List<Commit> listCom = commits.get(temp);
					if (listCom!=null && !listCom.isEmpty()) {
					Iterator<Commit> iterCom = commits.get(temp).iterator();
					while (iterCom.hasNext() && commit == null) {
						Commit current = iterCom.next();
						if (current.getCommitName().equals(name)) {
							commit = current;
							branch = temp;
							flag = true;
						}
					}
				}
				}

				if (commit != null) {
					head.setCurrentBranch(branch);
					head.setCurrentCommit(commit);
				} 
			}

			if (flag) {
				res = replaceFiles(head.getCurrentCommit(), actualState);
				
				//????????????
			//	head.getCurrentCommit().getFileCond().keySet().forEach(k -> k.setStatus(Status.COMMITTED));
			} else {
				res = "There are not branch or commit with such name!";
			}

		} else {
			res = "There are uncommitted files!";
		}
		return res;
	}
	
	private String replaceFiles (Commit commit, List<FileState> actualState){
        String res = "All files from commit "+ commit.getCommitName() + "on branch " + head.getCurrentBranch().getBranchName() + " have been recovered.";
        Map<FileState, String[]> filesForRecovery = new HashMap<>(commit.getFileCond());
        for (FileState state: actualState) {
            File file = state.getFile();
            FileState curFileState = null;
           try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
               curFileState = filesForRecovery.keySet()
            		   .stream()
            		   .filter(s->s.getFile().equals(file))
            		   .findFirst()
            		   .get();
               
               if (curFileState != null) {

                String[] strings = filesForRecovery.get(curFileState);
               
                  filesForRecovery.remove(curFileState);
                   for (String s : strings) {
                       writer.write(s);
                   }
                   writer.flush();
      
              
               } else {
                   writer.close();
                   file.delete();
               }
           } catch (IOException e) {
               res = "File no found!";
           }
           file.setLastModified(curFileState.getTimeModif());
       }
        if (!filesForRecovery.isEmpty()){
                filesForRecovery.entrySet().forEach(entry -> {
                    try {
                        File file = entry.getKey().getFile();
                        file.createNewFile();
                        
                        
                        try (PrintStream writer = new PrintStream(file)) {
                            for (String s : entry.getValue()) {
                                writer.println(s);
                            }
                            writer.flush();
                        }
                        
                        
                        
//                        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
//                                for (String s : entry.getValue()) {
//                                    writer.write(s);
//                                }
//                                writer.flush();
//                            }
                        file.setLastModified(entry.getKey().getFile().lastModified());             
                        } catch(IOException e){
                        	e.getMessage();
                         
                        }
                });
            }
        return res;
    }

	@Override
	public String getHead() {
		String res = "The head does not exist";
		if (head != null) {
			String curBrName = head.getCurrentBranch().getBranchName();
			Commit comName = head.getCurrentCommit();
			res = "Current branch " + curBrName + " Current commit " + comName.getCommitName();
		}
		return res;
	}

	@Override
	public void save() {
		File fileGit = new File(GIT_FILE);
		try (ObjectOutputStream output = new ObjectOutputStream(Files.newOutputStream(Path.of(fileGit.getPath())))) {
			output.writeObject(this);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public String addIgnoredFileNameExp(String regex) {
		try {
			".mygit".matches(regex);
		} catch (Exception e) {
			throw new IllegalArgumentException("Incorrect expression: " + regex);
		}
		return regex + " is ignored for files";

	}

}
