package telran.git;

import java.nio.file.Path;
import java.util.List;

import telran.git.blocks.*;

import telran.git.view.*;


public class GitItems {
	
	static GitRepositoryImpl gitReposImpl;
	
//	private static GitRepository gitRepository;
	
	InputOutput io;
	
	public static Item[] getGitItems (GitRepositoryImpl gitRepository) {
		 
		gitReposImpl = gitRepository;
			
		Item[] listItems = {
				Item.of("Commit", GitItems:: doingCommit),
				Item.of("Info", GitItems:: getInfo),
				Item.of("Create Branch", GitItems:: createBranchItem),
				Item.of("Rename Branch", GitItems:: renameBranchItem),
				Item.of("Delete Branch", GitItems:: deleteBranchItem),
				Item.of("Logs", GitItems:: getLogs),
				Item.of("List of branches", GitItems:: getBranches),
				Item.of("List of pathes", GitItems:: getPathes),
				Item.of("Switch to", GitItems:: switchToItem),
				Item.of("Get Head", GitItems:: getHeadItem),
				Item.of("Add ignored file", GitItems:: addIgnFileRegExItem),
				Item.of("Exit", s -> gitReposImpl.save(), true),
		};		
				
				return listItems;
		 }
	
	
	public static void doingCommit (InputOutput io) {
		String commitMsg = io.readString("Enter name of commit: ");
		io.writeLine(gitReposImpl.commit(commitMsg));
		
	}
	
	
	public static void getInfo (InputOutput io) {
		List<FileState> list = gitReposImpl.info();
		list.forEach(io:: writeLine);
	}
	
	
	public static void createBranchItem (InputOutput io) {
		String brName = io.readStringPredicate("Enter a branch name", "Wrong branch name",
				ios -> ios.matches("\\p{Alpha}{4,10}"));
		io.writeLine(gitReposImpl.createBranch(brName));
		}
	
	public static void renameBranchItem (InputOutput io) {
		String brName = io.readString("Enter a branch name");
//		if (!gitReposImpl.branches().contains(brName)) {
//			io.writeLine("Branch with this name doesn't exist");
//		} else {
		String newBrName = io.readString("Enter a new branch name");
			io.writeLine(gitReposImpl.renameBranch(brName, newBrName));
		}
	
		
		
		public static void deleteBranchItem (InputOutput io) {
			String brName = io.readString("Enter a branch name");
			if (!gitReposImpl.branches().contains(brName)) {
				io.writeLine("Branch with this name doesn't exist");
			} else {
				io.writeLine(gitReposImpl.deleteBranch(brName));
			
			}
		}
		
		
			public static void getLogs (InputOutput io) {
				List<CommitMessage> list = gitReposImpl.log();
				list.forEach(io:: writeLine);
				
		}
			
			public static void getBranches (InputOutput io) {
				List<String> list = gitReposImpl.branches();
				list.forEach(io:: writeLine);
			}
			
			
			public static void getPathes (InputOutput io) {
				String commitName = io.readString("Enter a commit name");
				List<Path> list = gitReposImpl.commitContent(commitName);
				list.forEach(io:: writeLine);
				
				}
			
			
			public static void switchToItem (InputOutput io) {
				String nameBranch = io.readString("Enter name of commit or branch for switching");
				io.writeLine(gitReposImpl.switchTo(nameBranch));
				
			}
			
			
			public static void getHeadItem(InputOutput io) {
				io.writeLine(gitReposImpl.getHead());
			}
			
			
			public static void addIgnFileRegExItem (InputOutput io) {
				String regularExp = io.readStringPredicate("Enter a regular expression for being ignored file name",
						"Wrong regular expression", ios -> {
							try {
								"{\\w.txt}".matches(ios);
							} catch (Exception e) {
								return false;
							}
							return true;
						});
				
				
				io.writeLine(gitReposImpl.addIgnoredFileNameExp(regularExp));
			}
			
			
}
	
	
	
	
	
	
	
	
	
	
	

