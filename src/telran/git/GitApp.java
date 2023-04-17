package telran.git;
import telran.git.view.*;

import telran.git.view.InputOutput;
import telran.git.view.Item;
import telran.git.view.StandardInputOutput;



public class GitApp {
	

		public static void main(String args[])  {
		InputOutput io = new StandardInputOutput();
		
		try {
			Item[] gitItems = GitItems.getGitItems(GitRepositoryImpl.init());
			Menu menu = new Menu("Main git menu", gitItems);
			menu.perform(io);
			
			
			
		} catch (Exception e) {
			io.writeLine("Error" + e.getMessage());
		}
		
	}
}
	


