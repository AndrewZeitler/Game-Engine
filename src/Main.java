
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.*;

/**
 * The main program
 * This program will allow a user to design their own games using the engine I designed
 * 
 * Andrew Zeitler
 * June 21st, 2018
 *
 */
public class Main extends JFrame implements ActionListener{
	//The default serial ID for the JFrame
	private static final long serialVersionUID = 1L;
	//The UI behaviour object to handle all the UI interations
	private UIBehaviour uib;
	//The pane for holding all the script/scene tabs
	private JTabbedPane pane;
	//An array of all the pages being held by the JTabbedPane
	private ArrayList<Page> pages;
	//The file project location
	private File project;
	//The file location of the main scene to be run
	private File mainScene;
	
	/**
	 * The main class for the program to run from
	 */
	public Main(){
		//Super the constructor for the JFrame
		super("Culminating");
		//Set the size to be the monitors screen size
		setSize(Toolkit.getDefaultToolkit().getScreenSize());
		//Sets the layout to contain the UI nicely
		setLayout(new BorderLayout());
		//Sets the default close operation so that the whole program ends when you close it
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		///Sets the menu bar on the JFrame
		setJMenuBar(createMenuBar());
		//Create the tab pane object and add it to the JFrame
		pane = new JTabbedPane();
		add(pane);
		
		//Create the UIBehaviour object
		uib = new UIBehaviour(this);
		//Create the arraylist of pages
		pages = new ArrayList<Page>();
		
		//Get the project file location
		JOptionPane.showMessageDialog(null, "Please select or create your project file.");
		//Set the project file
		project = uib.getFile("Project", null, JFileChooser.DIRECTORIES_ONLY, null, null);
		//If they clicked cancel, exit the program
		if(project == null){
			System.exit(0);
		}
		//Create the project files necessary for the program
		createFiles(project);
		
		//Sets the JFrame to be visible
		setVisible(true);
	}
	
	/**
	 * Create the files necessary for the project to run if it hasn't been created yet
	 * @param project the project file location
	 */
	public void createFiles(File project){
		//The scenes folder
		File scenes = new File(project.toString() + File.separator + "Scenes");
		//The scripts folder
		File scripts = new File(project.toString() + File.separator + "Scripts");
		//The resources folder
		File resources = new File(project.toString() + File.separator + "Resources");
		//The executable main class
		File execMain = new File(project.toString() + File.separator + "ExecutableMain.class");
		
		//If the scenes folder doesn't exist, make it
		if(!scenes.exists()){
			scenes.mkdir();
		}
		//If the scripts folder doesn't exist, make it
		if(!scripts.exists()){
			scripts.mkdir();
		}
		//If the resources folder doesn't exist, make it
		if(!resources.exists()){
			resources.mkdir();
		}
		
		//Create and compile the executable main file for the user
		try {
			//Create the temporary file of executable main as a .java for compilation
			File tempFile = new File(execMain.getPath().replace(".class", ".java"));
			//Read the data from the .java version of executable main
			String data = FileManager.readFile(new File("src" + File.separator + "ExecutableMain.java"));
			//Write the executable main class with the correct package line to join the other files
			data = "package com." + project.getName() + ";\n\n" + data;
			//Write the executable main file to the project directory
			FileManager.writeToFile(data, tempFile);
			//Compile the file
			FileManager.compileFile(tempFile);
			//Delete the temporary file
			tempFile.delete();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Entry point for program
	 * @param args
	 */
	public static void main(String[] args) {
		new Main();
	}
	
	/**
	 * Creates a tab to hold the script/scene in
	 * @param title the title of the script/scene
	 * @param dir the directory of the script/scene
	 * @return returns the tab after being created
	 */
	public Page createTab(String title, File dir){
		//Create the page to be added to the pane
		Page page = new Page(title, dir);
		//Add the page to the array
		pages.add(page);
		//Add the tab and switch to that tab
		pane.addTab(title, page);
		pane.setSelectedIndex(pane.getTabCount() - 1);
		//Return the newly created page
		return page;
	}
	
	/**
	 * Creates a UI menu bar for the user
	 * @return returns the created menu bar for the UI
	 */
	private JMenuBar createMenuBar(){
		//Create menu bar
		JMenuBar mB = new JMenuBar();
		//Create the menus
		JMenu file = new JMenu("File");
		JMenu tools = new JMenu("Run");
		//Sets the font on each menu
		file.setFont(new Font("Monospace", Font.PLAIN, 15));
		tools.setFont(new Font("Monospace", Font.PLAIN, 15));
		//Create all the menu items
		JMenuItem[] fileItems = {new JMenuItem("Create Scene"), new JMenuItem("Open Scene"),
				new JMenuItem("New"), new JMenuItem("Open"), new JMenuItem("Save"), 
				new JMenuItem("Save As"), new JMenuItem("Save All"), new JMenuItem("Close Tab"), new JMenuItem("Exit")};
		JMenuItem[] toolItems = {new JMenuItem("Run"), new JMenuItem("Main Scene..."),
				new JMenuItem("Export")};
		//Add listeners to each of the menu items so we can respond to their actions
		//Also add the item to their menu
		for(JMenuItem item : fileItems){
			item.addActionListener(this);
			file.add(item);
		}
		for(JMenuItem item : toolItems){
			item.addActionListener(this);
			tools.add(item);
		}
		//Add separators to make the menu look nice
		file.insertSeparator(2);
		file.insertSeparator(8);
		tools.insertSeparator(1);
		//Add the menus to the menu bar
		mB.add(file);
		mB.add(tools);
		//Set the menu bar to be white
		mB.setBackground(Color.WHITE);
		//Return the newly created menu bar
		return mB;
	}
	
	/**
	 * Gets the page at the given index
	 * @param index the index to get the page from
	 * @return returns the page at the given index
	 */
	public Page getText(int index){
		return pages.get(index);
	}
	
	/**
	 * Gets the page the user is currently looking at
	 * @return returns the page the user is on
	 */
	public Page getCurrentPage(){
		//Make sure there are pages open and then return the current open one
		if(pages.size() > 0){
			return pages.get(pane.getSelectedIndex());
		}
		//If nothing is open, return null
		return null;
	}
	
	/**
	 * Returns true if the page with the given directory is already open
	 * @param dir the directory of the page
	 * @return returns true if the page is found, false otherwise
	 */
	public boolean isPage(String dir){
		//Check to see if the page is already open
		for(Page page : pages){
			if(page.getDirectory().getAbsolutePath().equals(dir)){
				//Return true if it is open
				return true;
			}
		}
		//Return false if it is not open
		return false;
	}
	
	/**
	 * Gets the whole project's directory
	 * @return returns the File object of the directory
	 */
	public File getProjectDirectory(){
		return project;
	}
	
	/**
	 * Overrides actions with desired effect
	 */
	public void actionPerformed(ActionEvent e) {
		//If they click exit
		if(e.getActionCommand().equals("Exit")){
			//Keeps track of any unsaved items
			int unsaved = 0;
			//Loop through and find all unsaved pages
			for(Page page : pages){
				if(!page.isSaved()){
					unsaved++;
				}
			}
			//If there are some unsaved, let the user know
			if(unsaved > 0){
				int input = JOptionPane.showConfirmDialog(null, "There are some unsaved files. Would you like to continue?",
						"Are You Sure?", JOptionPane.YES_NO_OPTION);
				//If they say yes, exit
				if(input == 0){
					System.exit(DISPOSE_ON_CLOSE);
				}
			} else {
				//Exit if there is nothing unsaved
				System.exit(DISPOSE_ON_CLOSE);
			}
			
		//Close tab
		} else if(e.getActionCommand().equals("Close Tab")){
			uib.closeTab();
		//Open scene
		} else if(e.getActionCommand().equals("Open Scene")){
			uib.openScene();
		//Create scene
		} else if(e.getActionCommand().equals("Create Scene")){
			uib.newScene();
		//Open script
		} else if(e.getActionCommand().equals("Open")){
			uib.openScript();
		//Save file
		}else if(e.getActionCommand().equals("Save")){
			uib.save();
		//Save the file as
		} else if(e.getActionCommand().equals("Save As")){
			uib.saveAs();
		//Save all files
		} else if(e.getActionCommand().equals("Save All")){
			uib.saveAll();
		//Create new script
		} else if(e.getActionCommand().equals("New")){
			uib.newScript();
		//Run the program
		} else if(e.getActionCommand().equals("Run")){	
			uib.run();
		//Choose main scene for running
		} else if(e.getActionCommand().equals("Main Scene...")){
			uib.mainScene();
		//Export the project
		} else if(e.getActionCommand().equals("Export")){
			uib.export();
		}
	}
	
	/**
	 * Gets the main scene the user wants to begin their program from
	 * @return returns the file object of the scene
	 */
	public File getMainScene(){
		return mainScene;
	}
	
	/**
	 * Sets the main scene the user wants to begin their program from
	 * @param mainScene the File object of the scene to start from
	 */
	public void setMainScene(File mainScene){
		this.mainScene = mainScene;
	}
	
	/**
	 * Gets the pages open currently
	 * @return returns an ArrayList of the pages currently open
	 */
	public ArrayList<Page> getPages(){
		return pages;
	}
	
	/**
	 * Get the JTabbedPane object
	 * @return returns the JTabbedPane for main
	 */
	public JTabbedPane getTabbedPane(){
		return pane;
	}

}
