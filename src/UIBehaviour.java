
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Handles the behaviour of the UI elements
 * 
 * Andrew Zeitler
 * June 21st, 2018
 *
 */
public class UIBehaviour {
	//The main object
	private Main main;
	
	//Creates the UIBehaviour object to handle the main UI
	public UIBehaviour(Main main){
		//Set the main object
		this.main = main;
	}
	
	/**
	 * Closes the tab the user is currently looking at
	 */
	public void closeTab(){
		//Make sure they are looking at a page
		if(main.getCurrentPage() != null){
			//Check if it has been saved and if it hasn't confirm if they want to close without saving
			if(!main.getCurrentPage().isSaved()){
				//Ask if they would like to close without saving
				int input = JOptionPane.showConfirmDialog(null, "There are some unsaved files. Would you like to continue?",
						"Are You Sure?", JOptionPane.YES_NO_OPTION);
				//If they say yes, close tab
				if(input == 0){
					//Gets the current page and removes from the arraylist and the window
					Page page = main.getCurrentPage();
					main.getTabbedPane().remove(page);
					main.getPages().remove(page);
				}
			//Close tab right away if it was saved already
			} else {
				//Gets the current page and removes from the arraylist and the window
				Page page = main.getCurrentPage();
				main.getTabbedPane().remove(page);
				main.getPages().remove(page);
			}
		}
	}
	
	/**
	 * Prompts the user with the option to open a script
	 */
	public void openScript(){
		//Filters to only include .java files for them to pick from
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
		        ".java Files", "java");
        File selectedFile = getFile("Open Scene", filter, JFileChooser.FILES_ONLY, new File(main.getProjectDirectory() + File.separator + "Scripts"), null);
        //Show the open file and then check if they chose to open the file
        if (selectedFile != null) {
        	try{
        		//If the file was already open, don't allow it to be opened again
        		if(main.isPage(selectedFile.getAbsolutePath())){
        			//Tell the user the file was already open and return from the method
					JOptionPane.showMessageDialog(null, "Sorry that file is already open right now. Close it to continue.");
					return;
				}
        		//Create the tab with the selected file
        		Page page = main.createTab(selectedFile.getName(), selectedFile);
        		//Set the tab's text to be what is contained within the file
        		String fileText = FileManager.readFile(selectedFile);
        		page.setText(fileText);
        	} catch (IOException error){
        		JOptionPane.showMessageDialog(null, "There was an error reading that file.");
        	}
        }        
	}
	
	/**
	 * Prompts the user with the option to open a script
	 */
	public void openScene(){
		//Filters to only include .java files for them to pick from
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
		        ".java Files", "java");
        File selectedFile = getFile("Open Scene", filter, JFileChooser.FILES_ONLY, new File(main.getProjectDirectory() + File.separator + "Scenes"), null);
        //Show the open file and then check if they chose to open the file
        if (selectedFile != null) {
        	try{
        		//If the file was already open, don't allow it to be opened again
        		if(main.isPage(selectedFile.getAbsolutePath())){
        			//Tell the user the file was already open and return from the method
					JOptionPane.showMessageDialog(null, "Sorry that file is already open right now. Close it to continue.");
					return;
				}
        		//Create the tab with the selected file
        		Page page = main.createTab(selectedFile.getName(), selectedFile);
        		//Set the tab's text to be what is contained within the file
        		String fileText = FileManager.readFile(selectedFile);
        		page.setText(fileText);
        	} catch (IOException error){
        		JOptionPane.showMessageDialog(null, "There was an error reading that file.");
        	}
        }        
	}
	
	/**
	 * Saves the script to file
	 */
	public void save(){
		//Get the current page that the user is on
		Page page = main.getCurrentPage();
		
		//If there is no page, leave the method
		if(page == null){
			return;
		}
		
		try {
			//Write to the page's file the text in the page
			FileManager.writeToFile(page.getText(), page.getDirectory());
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "There was an error saving your file.");
		}
		//Set the file as saved so the user can see it was successfully saved
		page.setSaved(true);
	}
	
	/**
	 * Prompts the user with the option to save their current script
	 */
	public void saveAs(){
		//Gets the current page the user is on
		Page page = main.getCurrentPage();
		//Exit if there was no page there
		if(page == null){
			return;
		}
		
		//Allow them to only choose .java files
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
		        ".java Files", "java");
        File selectedFile = getFile("Save As", filter, JFileChooser.FILES_ONLY, page.getDirectory(), page.getDirectory());
        //Show the save file and then check if they chose to save the file
        if (selectedFile != null) {
        	try{
        		//Check if the file was already open in another tab then don't allow it to be overwritten
        		if(main.isPage(selectedFile.getAbsolutePath()) && !selectedFile.getName().equals(page.getName())){
        			//Let them know the file is already open and return from the method
					JOptionPane.showMessageDialog(null, "Sorry that file is already open right now. Close it to continue.");
					return;
				}
        		//If the file already exists, warn them about overwriting it
        		if(selectedFile.exists()){
        			//Ask if they would like to overwrite the file
    				int choice = JOptionPane.showConfirmDialog(null, "This file already exists. Are you sure you want to overwrite it?",
    						"Overwrite", JOptionPane.YES_NO_OPTION);
    				//If they choose no, exit the method
    				if(choice == 1){
    					return;
    				}
        		}
        		//Get the name they chose
        		String fileName = selectedFile.getName();
        		//Add a .java extension if they didn't do it themselves
        		if(!fileName.contains(".java")){
        			fileName += ".java";
        		}
        		//Write the text in the file to the file location
        		FileManager.writeToFile(page.getText(), new File(main.getProjectDirectory() + File.separator + "Scripts" + File.separator + fileName));
        		//Set the file to be saved so that the user can see the change
        		page.setSaved(true);
        		//Set the page's name to the new updated name
        		page.setName(fileName);
        	} catch (IOException error){
        		JOptionPane.showMessageDialog(null, "There was an error saving that file.");
        	}
        }
        
	}
	
	/**
	 * Save all open files
	 */
	public void saveAll(){
		//Get all the pages
		ArrayList<Page> pages = main.getPages();
		
		//Make sure there is an open file
		if(pages == null){
			return;
		}
		
		//Loop through each page and save them
		for(int i = 0; i < pages.size(); i++){
			//Get the page
			Page page = pages.get(i);
			
			try {
				//Write to file the text within the page
				FileManager.writeToFile(page.getText(), page.getDirectory());
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "There was an error saving your file.");
			}
			//Set the page to be saved so the user may see
			page.setSaved(true);
		}
	}
	
	/**
	 * Prompts user with message input and returns their response
	 * @param text the message we want to show up on the input dialog
	 * @return returns the user's input into the text field
	 */
	public static String getInput(String text){
		//String input that they enter
		String input = "";
			
		//Prompt user with input dialog message and keep looping if they enter nothing or press cancel		
		input = JOptionPane.showInputDialog(text);
		
		//Return what they type into the box
		return input;
	}
		
	/**
	 * Creates a new script for the user to edit
	 */
	public void newScript(){
		//Gets the script name
		String input = getInput("Enter the script name.");
		//Make sure they input something
		if(input == null){
			return;
		}
		//Make sure the file have a .java extension on it
		if(!input.contains(".java")){
			input += ".java";
		}
		try {
			//Create the file directory for the scripts folder
			File dir = new File(main.getProjectDirectory().getAbsolutePath() + File.separatorChar  + "Scripts" + File.separatorChar + input);
			//Make sure that the file isn't already open/exists
			if(main.isPage(dir.getAbsolutePath())){
				JOptionPane.showMessageDialog(null, "Sorry that file is already open right now. Close it to continue.");
				return;
			}
			//If it exists, let them know they will be overwriting their file if they do
			if(dir.exists()){
				int choice = JOptionPane.showConfirmDialog(null, "This file already exists. Are you sure you want to overwrite it?",
						"Overwrite", JOptionPane.YES_NO_OPTION);
				//If they say no, leave the method
				if(choice == 1){
					return;
				}
			}
			//Create the initial script for the user already filled in
			String initScriptText = "package com." + main.getProjectDirectory().getName() 
					+ ";\nimport zeity.*;\n\npublic class " + input.substring(0, input.length() - 5) 
					+ " extends ScriptBehavior {\n\n\tpublic void start(){\n\t\t\n\t}\n\n\tpublic "
					+ "void update(){\n\t\t\n\t}\n\n}";
			//Save the file so it exists in the file system
			FileManager.writeToFile(initScriptText, dir);
			//Create the page for the user to see
			Page page = main.createTab(input, dir);
			//Set the text to the initial script
			page.setText(initScriptText);
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "There was an error creating that file.");
		}
	}
	
	/**
	 * Creates a new script for the user to edit
	 */
	public void newScene(){
		//Get the scene name
		String input = getInput("Enter the scene name.");
		//Check to make sure they didn't input nothing
		if(input == null){
			return;
		}
		//Make sure it contains .java extension
		if(!input.contains(".java")){
			input += ".java";
		}
		try {
			//Create file directory within the scenes folder
			File dir = new File(main.getProjectDirectory().getAbsolutePath() + File.separatorChar  + "Scenes" + File.separatorChar + input);
			//Make sure the page isn't already open, otherwise let them know and return
			if(main.isPage(dir.getAbsolutePath())){
				JOptionPane.showMessageDialog(null, "Sorry that file is already open right now. Close it to continue.");
				return;
			}
			//If the file already exists warn them that they will overwrite it
			if(dir.exists()){
				int choice = JOptionPane.showConfirmDialog(null, "This file already exists. Are you sure you want to overwrite it?",
						"Overwrite", JOptionPane.YES_NO_OPTION);
				//If they say no, leave the method
				if(choice == 1){
					return;
				}
			}
			//Create the initial text for the scene
			String initScriptText = "package com." + main.getProjectDirectory().getName() 
					+ ";\nimport zeity.*;\n\npublic class " + input.substring(0, input.length() - 5) 
					+ " extends SceneBehavior {\n\n\tpublic void start(){\n\t\t\n\t}\n\n\tpublic "
					+ "void update(){\n\t\t\n\t}\n\n}";
			//Write the scene to file so that it already exists in the system
			FileManager.writeToFile(initScriptText, dir);
			//Create the page tab
			Page page = main.createTab(input, dir);
			//Set the text on the page
			page.setText(initScriptText);
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "There was an error creating that file.");
		}
	}
	
	/**
	 * Runs the scripts and begins the runtime handler
	 * @return returns the RunTimeHandler object for the instance
	 */
	public void run(){
		//Get the directory of the main file
		File dir = main.getProjectDirectory();
		File jar = compileJAR(dir);
		//If they haven't chosen a main scene to run already, prompt them
		if(main.getMainScene() == null){
			mainScene();
		}
		//Create the runtime object and return it
		String cmd = "java -jar " + jar;
	    try {
			Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public File compileJAR(File exportFile){
		//Get the directory of the main file
		File dir = main.getProjectDirectory();
		//The created jar file
		File jar = null;
		//Contains the combined files of scenes and scripts
		ArrayList<File> combinedFiles;
		//If they haven't chosen a main scene to run already, prompt them
		if(main.getMainScene() == null){
			mainScene();
		}
		
		try{
			//The executable main class file
			File execClass = new File(main.getProjectDirectory() + File.separator + "ExecutableMain.class");
			//The executable main private class file
			File execPrivClass = new File(main.getProjectDirectory() + File.separator + "ExecutableMain$1.class");
			//The properties text file
			File properties = new File(main.getProjectDirectory() + File.separator + "properties.txt");
		    //The resources directory
			File resources = new File(main.getProjectDirectory() + File.separator + "Resources");

			//Combine all the files for the scenes and scripts after compiling and add them to the combined files
			combinedFiles = FileManager.compileFiles(new File[] {new File(dir + File.separator + "Scenes"),
					new File(dir + File.separator + "Scripts")});
			//Add the executable main class file
			combinedFiles.add(execClass);
			//Add the private executable main class file
			combinedFiles.add(execPrivClass);
			//Add the properties text file
			combinedFiles.add(properties);
			//Get all files in the resource directory
			File[] resourceFiles = resources.listFiles();
			//Loop through all the resource and add them to the ArrayList
			for(int i = 0; i < resourceFiles.length; i++){
				combinedFiles.add(resourceFiles[i]);
			}
			//Read the output file to see if any error occurred
			String output = FileManager.readFile(new File(dir + File.separator + "crash.txt"));
			//If there is an output, then there was an error, so handle that
			if(output != ""){
				//If an error occurred, show the error message and leave method
				JOptionPane.showMessageDialog(null, output);
				return null;
			}
			//If the export destination does not exist, make it
			if(!exportFile.exists()){
				exportFile.mkdirs();
			}
			//Create the jar file
			jar = FileManager.createExecutableJar(exportFile.toString(), dir.getName(), combinedFiles);
			//The input stream for reading the jar file
			InputStream is = null;
			//The output stream for writing the jar file
			OutputStream os = null;
			try {
				//Create the file input stream for the jar file
				is = new FileInputStream("Zeity.jar");
				//Create the file output location for the jar file in the export location
				os = new FileOutputStream(exportFile + File.separator + "Zeity.jar");
				//The buffer for reading the file
				byte[] buffer = new byte[1024];
				//The byte length of the line
				int length;
				//Continually loop to read the file
				while (true) {
					//Read the bytes in the next line
					length = is.read(buffer);
					//If there is no next line, leave the loop
					if (length == -1){
						break;
					}
					//Write the line to the jar location
				    os.write(buffer, 0, length);
				}
			} finally {
				//Close the files
				is.close();
				os.close();
			}
		} catch(Exception e){
			e.printStackTrace();
			return null;
		}
		//Return the created jar
		return jar;
	}
	
	/**
	 * Prompts the user with the choice to select a main scene to run
	 */
	public void mainScene(){
		//Allow them to only choose .java files
		FileNameExtensionFilter filter = new FileNameExtensionFilter(".java Files", "java");
		File selectedFile = getFile("Main Scene", filter, JFileChooser.FILES_ONLY, new File(main.getProjectDirectory() + File.separator + "Scenes"),
				null);
        //Check to see if they chose a file
	    if (selectedFile != null) {
	        //Set the main scene to what the user chose
	        main.setMainScene(selectedFile);
	        String data = "Name=" + main.getProjectDirectory().getName() + "\n" 
	        		+ "Main=" + selectedFile.getName().replace(".java", "");
	        try {
				FileManager.writeToFile(data, new File(main.getProjectDirectory() + File.separator + "properties.txt"));
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	}
	
	public File getFile(String title, FileNameExtensionFilter filter, int mode, File currDir, File selFile){
		//Create the file chooser object
		JFileChooser chooser = new JFileChooser();
		//Set the title and file selection mode for the chooser
		chooser.setDialogTitle(title);
		//Set filter
		if(filter != null){
			chooser.setFileFilter(filter);
		}
		//Set file selection
		chooser.setFileSelectionMode(mode);
		//Set the directory to be the current page's directory
		chooser.setCurrentDirectory(currDir);
		//Set the selected file to be the current page's directory
		chooser.setSelectedFile(selFile);
		//If they successfully opened a file then format the project file
		if (chooser.showOpenDialog(main) == JFileChooser.APPROVE_OPTION) {
			//Return the file they chose
		    return chooser.getSelectedFile();
		}
		//Return null if they clicked cancel
		return null;
	}
	
	public void export(){
		File export = getFile("Export", null, JFileChooser.DIRECTORIES_ONLY, null, null);
		File exportFile = new File(export + File.separator + main.getProjectDirectory().getName());

		compileJAR(exportFile);
	}
}
