import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JFrame;

import zeity.*;

public class ExecutableMain extends Canvas implements Runnable{
	private static final long serialVersionUID = -81695814973579500L;
	JFrame window;
	Thread thread;
	String projectName;
	String mainScene;
	boolean isRunning;
	
	public ExecutableMain(){
		try {
			//The file information from the properties.txt file
			String file = "";
			//Read the properties file
			file = readFile(getClass().getResourceAsStream("/resources/properties.txt"));
			//Split the file by it's lines
			String[] properties = file.split("\n");
			//Get the name of the project
			projectName = properties[0].replace("Name=", "");
			//Create the window with the project name as the title
	        createWindow(projectName);
	        //Set the size of the canvas to be the size of the computer
	        setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
	      	setSize(Toolkit.getDefaultToolkit().getScreenSize());
	      	//Sets the canvas so it won't be focused on so that the focus stays on the scene's JPanel
	      	setFocusable(false);
	      	//Add the canvas
	      	window.add(this);
	      	//Create our buffer strategy with a triple screen buffer
	        createBufferStrategy(3);
	        
	        //Get the main scene to load from the file
	        mainScene = properties[1].replace("Main=", "");
	      		
	        //Set running to be true
	        isRunning = true;
	        //Create and start the runtime thread for the user's program
	      	thread = new Thread(this);
	      	thread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Creates a window for the user with the name of the project
	 * @param name the name of the user's project for the window
	 */
	public void createWindow(String name){
		//Create the JFrame
		window = new JFrame(name);
		//Set the frame to be visible
		window.setVisible(true);
		//Sets the size to the user's screen size
		window.setSize(Toolkit.getDefaultToolkit().getScreenSize());
		window.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
		//Set the window to be focusable -> can be focused on
		window.setFocusable(true);
		//Sets the default close operation so that the program does not close the whole program when you exit
		window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		//Sets the window to be centered
		window.setLocationRelativeTo(null);
		//Don't allow for the window to be resized
		window.setResizable(false);
		//Bring the window to the front
		window.toFront();
	}
	
	public void beginExecution(){
		//Get the class loader
		ClassLoader classLoader = ExecutableMain.class.getClassLoader();
		
		try {
			//The main class for the program to begin from
			Class<?> mainClass;
			//Load the main scene class using the class loader object so that we may 
			//run the start method for it and later the update method
			mainClass = classLoader.loadClass("com." + projectName + "." + mainScene);
		
			//Create an instance of the main scene's class
			Object instance = mainClass.newInstance();
			
			//Check to make sure that the loaded class is a scene
			if(instance instanceof SceneBehavior){
				//Change the current scene of nothing to be the main scene
				SceneManager.changeScene((SceneBehavior) instance);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Reads the file and returns the string filled
	 * @param fileName the file to be read from
	 * @return returns a string with the file's information
	 * @throws IOException if there is an issue reading files
	 */
	public static String readFile(InputStream file) throws IOException{
		//File reader to attach to the entered file
		BufferedReader reader;
		//Lines to be read from the file
		String lines = "";
		
		//Open the file for the reader to attach to
		reader = new BufferedReader(new InputStreamReader(file));
		//Read all the lines and add them to the string
		while(true){
			//Read the line
			String line = reader.readLine();
			//If the file has finished being read, leave the loop
			if(line == null){
				break;
			}
			//Add a line break between the lines
			lines += line + "\n";
		}
		//Close the file now
		reader.close();
		
		//Return the filled string
		return lines;
	}
	
	/**
	 * The main method to begin from
	 * @param args
	 */
	public static void main(String[] args) {
		//Begin execution
		new ExecutableMain();
	}
	
	/**
	 * Main thread's runtime method
	 */
	public void run() {
		//Begin the execution of the main scene
        beginExecution();
        //Set the SceneManager to contain the bufferstategy of the canvas for graphic drawing
        SceneManager.setBufferStrategy(this.getBufferStrategy());
        //Add the panel from the scene to the window
        window.add(SceneManager.getScene().getPanel());
        //Add the mouse and keyboard listeners to the window
        SceneManager.getScene().addListeners();
        
        //Add a listener to the window so that if the user exits the program, the program will close properly
        window.addWindowListener(new WindowAdapter(){
        	@Override
        	public void windowClosed(WindowEvent e){
        		//Closes the window
        		window.dispose();
        		//Clear the static references to the current objects and scenes
        		ObjectManager.clearObjects();
        		SceneManager.clearScenes();
        		//End thread
        		isRunning = false;
        	}
        });
        //Pack the window
        window.pack();
        //The start time to ensure the main thread always runs at 60 frames or less
        long startTime;
        //Main loop of the user's program
        while(isRunning){
        	//Begin the loop time counter
        	startTime = System.currentTimeMillis();
        	//Get the graphics of the window
        	Graphics g = getBufferStrategy().getDrawGraphics();
        	//Get the main scene
        	SceneBehavior currScene = SceneManager.getScene();
        	//Clear the screen
        	g.clearRect(0, 0, window.getWidth(), window.getHeight());
        	//If the current scene does not equal the current scene in the SceneManager(the scene changed)
        	//Then remove the current window and add the switched one in SceneManager
    		if(!currScene.getPanel().equals(SceneManager.getScene().getPanel())){
    			//Remove the current scene
        		window.remove(currScene.getPanel());
        		//Add the changed scene to the window
        		window.add(SceneManager.getScene().getPanel());
        	}
        	//Call the current scene's update method
        	currScene.update();
        	//Loop through all the created objects and call their update methods
        	for(int i = 0; i < ObjectManager.getObjectSize(); i++){
        		//Get one of the objects
        		GameObject obj = ObjectManager.getObject(i);
        		//Loop through the object's scripts and call their update methods
        		for(int j = 0; j < obj.getScriptSize(); j++){
        			//Update the script if it has already been initialized
        			if(obj.getScript(j).isInitialized()){
        				obj.getScript(j).update();
        			} else {
        				//Initialize if it hadn't been initialized before
        				obj.getScript(j).start();
        				obj.getScript(j).setInitialized(true);
        			}
        		}
        	}
        	//Dispose of the graphics object
        	g.dispose();
        	//Show the graphics
        	getBufferStrategy().show();
        	
        	//If the loop ran faster than 60fps, sleep until it is at 60fps
        	if(System.currentTimeMillis() - startTime < 1000/60){
        		try {
        			//Make the thread sleep until it is 60 fps
					Thread.sleep(16 - (System.currentTimeMillis() - startTime));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        	}
        }
	}

}
