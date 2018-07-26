
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

/**
 * The class for managing the files of our program. Allows for the writing, reading, and compilation
 * of files as well as the creation of jar files
 * 
 * Andrew Zeitler
 * June 21st, 2018
 *
 */
public class FileManager {
	
	/**
	 * Writes to a given file with the given information
	 * @param information the information to write to the file
	 * @param fileName the file to write to
	 * @throws IOException
	 */
	public static void writeToFile(String information, File file) throws IOException{
		//Each piece of information we will be writing to our file
		String[] lines;
		
		//Split the information
		lines = information.split("~");
		//Create our file writer object and attach it to our file
		PrintWriter fileOut = new PrintWriter(new FileWriter(file, false));
		//Add each line of information to the file
		for(int i = 0; i < lines.length; i++){
			fileOut.println(lines[i]);
		}
		//Close the file now
		fileOut.close();
		
	}
	
	/**
	 * Reads all the file and returns the quotes within
	 * @param fileName the file the user entered and we will be reading
	 * @return returns a filled ArrayList with all our quotes
	 * @throws IOException if there is an issue reading files
	 */
	public static String readFile(File file) throws IOException{
		//File reader to attach to the entered file
		BufferedReader reader;
		//Quotes to be loaded into
		String lines = "";
		
		//Open the file for the reader to attach to
		reader = new BufferedReader(new FileReader(file));
		//Add our quotes to the quotes ArrayList
		while(true){
			String line = reader.readLine();
			if(line == null){
				break;
			}
			lines += line + "\n";
		}
		//Close the file now
		reader.close();
		
		//Return the filled ArrayList
		return lines;
	}
	
	/**
	 * Creates a jar file that will contain all of the user's class files for use at runtime
	 * @param projectDir the directory of the user's project
	 * @param project the name of the project
	 * @param classes the classes to save into the jar
	 * @return returns the file location of the jar file
	 * @throws Exception
	 */
	public static File createExecutableJar(String projectDir, String project, ArrayList<File> classes) throws Exception{
		//Manifest of the jar file containing information for about how the files are packaged
		Manifest manifest = new Manifest();
		//Create the basic attributes such as the version number for the manifest
		manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
		manifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, "com." + project + "." + ExecutableMain.class.getName());
		manifest.getMainAttributes().put(Attributes.Name.CLASS_PATH, "zeity.jar");
		//The directory of the package
		String dir;
		//The input stream for reading the classes to output for the jar
		BufferedInputStream in = null;
		//The output stream that would be used by the jar output stream to write the classes to
		FileOutputStream fout = new FileOutputStream(projectDir + File.separator + project + ".jar");
		JarOutputStream jarOut = new JarOutputStream(fout, manifest);
		//Loop through each class to save it to the jar
		for(int i = 0; i < classes.size(); i++){
			//If it contains the .class extension, add it to the project directory
			if(classes.get(i).getName().contains(".class")){
				//The project directory
				dir = "com/" + project + "/";
			} else {
				//Otherwise add it to the resources directory
				dir = "resources/";
			}
			//Start the next class entry of the jar
			jarOut.putNextEntry(new ZipEntry(dir + classes.get(i).getName()));
			//Create an input stream for the class file to read from
			in = new BufferedInputStream(new FileInputStream(classes.get(i)));
			//Create the buffer for the bites to be read in each line
		    byte[] buffer = new byte[1024];
		    //Loop until there is nothing left to write
		    while (true)
		    {
		    	//Read the bytes in the next line
		    	int count = in.read(buffer);
		    	//If there is no next line, leave the loop
		    	if (count == -1){
		    		break;
		    	}
		    	//Write the information from the line to the jar file
		    	jarOut.write(buffer, 0, count);
		    }
		    //Close the class file to get ready to begin the next
		    jarOut.closeEntry();
		}
		//Close all the files
		jarOut.closeEntry();
		jarOut.close();
		fout.close();
		//Return the newly created jar file object
		return new File(projectDir + File.separator + project + ".jar"); 
	}
	
	/**
	 * Compile the files within the directories given
	 * @param dirs An array containing the directories containing the files that need to be compiled
	 * @return returns an ArrayList of the newly compiledd files
	 * @throws Exception
	 */
	public static ArrayList<File> compileFiles(File[] dirs) throws Exception{
		//Will contain our compiled files
		ArrayList<File> compiledFiles = new ArrayList<File>();
		//The combined paths from all directories of the scripts and scenes
		ArrayList<String> files = new ArrayList<String>();
		//Loop through all directories given
		for(int i = 0; i < dirs.length; i++){
			//Loop through all the files in each directory
			for(int j = 0; j < dirs[i].listFiles().length; j++){
				//Make sure it is a java file we are getting is a java file
				if(dirs[i].listFiles()[j].getName().contains(".java")){
					//Add the file to our gathered files list
					compiledFiles.add(new File(dirs[i].listFiles()[j]
							.getAbsolutePath().replaceAll(".java", ".class")));
					files.add(dirs[i].listFiles()[j].getPath());
				}
			}
		}
		//The paths of all the classes in the proper array to write later
		String[] paths = new String[files.size()];
		//Loop through each file and add it to the corresponding array location
		for(int i = 0; i < paths.length; i++){
			//Add to paths array from file ArrayList
			paths[i] = files.get(i);
		}
		//Create a crash log so that if an error occurs, we can show that message to the user
		File crashLog = new File(dirs[0].getParentFile() + File.separator + "crash.txt");
		//Output stream to write all the classes to
		OutputStream output = new FileOutputStream(crashLog);
		//Compiler to compile our classes
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		if(compiler != null){
			//Compiles all our files. Paths needs to be an array because if we compile individually,
			//errors can occur as one file may be compiled first over the other
			compiler.run(null, null, output, paths);
		}
		//Return the compiled files
		return compiledFiles;
	}
	
	/**
	 * Compiles the given file
	 * @param file the file to compile
	 * @return returns the newly compiled file
	 */
	public static File compileFile(File file){
		//Get the java compiler
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		//Check to see if we have successfully gotten it
		if(compiler != null){
			//Compiles all our files. Paths needs to be an array because if we compile individually,
			//errors can occur as one file may be compiled first over the other
			compiler.run(null, null, null, file.getPath());
			//Return the compiled file
			return new File(file.toString().replace(".java", ".class"));
		}
		//Return null if it was not successfully compiled
		return null;
	}
	
}
