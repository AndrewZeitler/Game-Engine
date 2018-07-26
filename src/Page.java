
import java.awt.BorderLayout;
import java.awt.Font;
import java.io.File;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Page for adding to the JTabbedPane. Extends JPanel so that a text area and text pane can be added
 * Will hold the text of the file
 * 
 * Andrew Zeitler
 * June 21st, 2018
 *
 */
public class Page extends JPanel implements DocumentListener{
	//Default ID for JPanel
	private static final long serialVersionUID = 1L;
	//The area in which our text will be held
	private JTextArea area;
	//The pane that will hold the area for our text
	private JTextPane textPane;
	//Name of the file
	private String name;
	//Directory of the file it was taken from
	private File dir;
	//Keeps track of if the file is saved or not
	private boolean isSaved;
	
	/**
	 * Creates a page object
	 * @param name the name of the file
	 * @param dir the file the text comes from
	 */
	public Page(String name, File dir){
		//Sets the layout to border layout for adding the text
		setLayout(new BorderLayout());
		//Sets the name and directory
		this.name = name;
		this.dir = dir;
		//Create the area for the text and the pane for the text
		area = new JTextArea(1, 1);
		textPane = new JTextPane();
		//Add a listener to react to changes in its text
		textPane.getDocument().addDocumentListener(this);
		//Set the font for both
		area.setFont(new Font("Monospace", Font.PLAIN, 16));
		textPane.setFont(new Font("Monospace", Font.PLAIN, 14));
		//Creates the line numbers for the text area
		TextLineNumber number = new TextLineNumber(textPane);
		JScrollPane scroll = new JScrollPane(textPane);
		scroll.setRowHeaderView(number);
		//Add the text to the panel
		add(scroll);
	}
	
	/**
	 * Gets the text in the pane
	 * @return returns the string in the text pane
	 */
	public String getText(){
		return textPane.getText();
	}
	
	/**
	 * Sets the text in the pane
	 * @param text the text to set to the pane
	 */
	public void setText(String text){
		//Sets the text
		textPane.setText(text);
		//Set the file to be saved since that is when the text is typically saved
		isSaved = true;
	}
	
	/**
	 * Sets the title of the pane to say it has been saved and changes the variable
	 * @param isSaved set the file to this saved state
	 */
	public void setSaved(boolean isSaved){
		//If it wasn't saved before but is trying to be now
		if(this.isSaved == false && isSaved == true){
			//Get the pane it belongs to
			JTabbedPane pane = (JTabbedPane)this.getParent().getParent().getComponent(0);
			if(pane != null){
				//Sets the title to what it is now but remove the star indicating it was unsaved
				pane.setTitleAt(pane.indexOfComponent(this), pane.getTitleAt(pane.indexOfComponent(this))
						.substring(0, pane.getTitleAt(pane.indexOfComponent(this)).length() - 1));
			}
		//If it was saved before but is trying not to be now
		} else if(this.isSaved == true && isSaved == false){
			//Get the pane
			JTabbedPane pane = (JTabbedPane)this.getParent().getParent().getComponent(0);
			if(pane != null){
				//Add a star to the end of the title to show it is not saved
				pane.setTitleAt(pane.indexOfComponent(this), pane.getTitleAt(
						pane.indexOfComponent(this)) + "*");
			}
		}
		
		this.isSaved = isSaved;
	}
	
	/**
	 * returns if the file is saved
	 * @return returns if the files has been saved
	 */
	public boolean isSaved(){
		return isSaved;
	}
	
	/**
	 * Sets the name of the text pane
	 * @param name the name to set the text pane to
	 */
	public void setName(String name){
		//Get the pane
		JTabbedPane pane = (JTabbedPane)this.getParent().getParent().getComponent(0);
		if(pane != null){
			//Set the title on the pane
			pane.setTitleAt(pane.getSelectedIndex(), name);
			//Change it to not being saved since a change occured
			isSaved = false;
		}
		
		this.name = name;
	}
	
	/**
	 * Gets the name of the pane
	 * @return returns the name of the page
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Gets the directory of the file this was read from
	 * @return returns the directory of the file in a File object
	 */
	public File getDirectory(){
		return dir;
	}

	@Override
	public void changedUpdate(DocumentEvent arg0) {
		//Sets the save to false as a change occurred
		setSaved(false);
	}

	@Override
	public void insertUpdate(DocumentEvent arg0) {
		//Sets the save to false as a change occurred
		setSaved(false);
	}

	@Override
	public void removeUpdate(DocumentEvent arg0) {
		//Sets the save to false as a change occurred
		setSaved(false);
	}

}
