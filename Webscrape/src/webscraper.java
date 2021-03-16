/*
 * @Author: Steve Pham
 * A class that webscrapes a table from jlptsensei.com, that allows Kanji search and displays it in a user friendly format.
 * Used to help me study, memorize, and lookup definitions and Kanji for my Japanese courses in college.
 * Mainly used to learn introductory Websraping through JSoup library
 */
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

public class webscraper {
	
	//field to hold the url, changes based on the new page or level of Kanji I want to use
	private static String url;
	//frame that holds the overall content
	private static JFrame frame = new JFrame();
	//panel holds the content that is then placed onto the frame
	private static JPanel panel = new JPanel();
	//Textarea that the text is printed onto that is webscraped
	private static JTextArea display = new JTextArea();
	//add the textarea onto the scrollpane so it can be scrolled
	private static JScrollPane scroll = new JScrollPane(display);
	//highlighter that keeps track of the searches
	private static Highlighter.HighlightPainter myHighlighter = new MyHighlighter(Color.yellow);
	
	/*
	 * Main method that launches the program
	 * @param String[] takes the program name to run, also the identifier for running the program
	 */
	public static void main(String[] args) {
		createWindow();
		scrapePage();
	}
	
	//getter and setter for url
	private static void setUrl(String link) {
		url = link;
	}

	private static String getUrl() {
		return url;
	}
	
	/*
	 * Method to create the overall window with content, instantiating the JFrame, JPanel, Search Button, and Dropdown Box to select level/difficulty
	 */
	private static void createWindow() {
		//instantiating the scrollpane and initially giving value of the n5 level
		setUrl("https://jlptsensei.com/jlpt-n5-kanji-list/");

		//decorations of the window
		ImageIcon img = new ImageIcon(webscraper.class.getResource("japanese-icon (transparent).png"));
		frame.setIconImage(img.getImage());
		frame.setTitle("JLPT Kanji Dictionary");
		
		//dropdown box to select which page
		String s[] = {"JLPT N5", "JLPT N4", "JLPT N3", "JLPT N2", "JLPT N1"};
		@SuppressWarnings({ "rawtypes", "unchecked" })
		JComboBox dropdown = new JComboBox(s);
		dropdown.setBounds(0, 1, 100, 20);
		dropdown.setSelectedIndex(0);
		dropdown.addActionListener(new ActionListener(){
		      public void actionPerformed(ActionEvent e){
		          if (e.getSource() == dropdown) {
					@SuppressWarnings("rawtypes")
					JComboBox copy = (JComboBox)e.getSource();
		              String msg = (String)copy.getSelectedItem();
		              switch(msg) {
		              case "JLPT N5": setUrl("https://jlptsensei.com/jlpt-n5-kanji-list/"); scrapePage(); break;
		              case "JLPT N4": setUrl("https://jlptsensei.com/jlpt-n4-kanji-list/"); scrapePage(); break;
		              case "JLPT N3": setUrl("https://jlptsensei.com/jlpt-n3-kanji-list/"); scrapePage(); break;
		              case "JLPT N2": setUrl("https://jlptsensei.com/jlpt-n2-kanji-list/"); scrapePage(); break;
		              case "JLPT N1": setUrl("https://jlptsensei.com/jlpt-n1-kanji-list/"); scrapePage(); break;
		              }
		        }
		      }
		      });
		
		
		//implement a search bar feature
		JTextField search = new JTextField();
		search.setUI(new HintTextFieldUI("Search...", true));
		search.setBounds(100, 1, 221, 21);
		display.setBounds(10, 10, 800, 800);
		display.setEditable(false);
		display.setFont(new Font ("Roboto", Font.BOLD, 12));
		scroll.setBounds(0, 20, 400, 400);
		
		//search button to search for words and highlight them
		JButton searchButton = new JButton("Search");
		searchButton.setBounds(319, 1, 80, 20);
		searchButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
			    try {
			    	highlight(display, search.getText());
			    }
			    catch(Exception exception) {
			    		System.out.println(exception.getMessage());
			    }
			}
		});

		//adding everything to frame
		frame.add(search);
		frame.add(searchButton);
		frame.add(panel);
		frame.add(dropdown);
		frame.getContentPane().add(scroll);

		//frame properties
		frame.setLayout(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setSize(415,455);
		frame.setVisible(true);
	}
	
	/*
	 * Updates the current window, helper function to be used in scrapePage()
	 * @param String takes a body of text, in this case the StringBuilder from scrapePage() to add to content panel
	 */
	private static void updateWindow(String txt) {
		display.setText(txt);
		//invoke later runs on the AWT thread
		SwingUtilities.invokeLater(new Runnable(){
		    public void run(){
		        scroll.getViewport().setViewPosition(new Point(0, 0));
		    }
		});
	}
	
	
	/*
	 * Scrapes the webpage, allowing for new information to be taken to the program
	 */
	private static void scrapePage() {
		try {
			//establishes connection
			final Document document = Jsoup.connect(getUrl()).timeout(6000).get();
		
			Elements body = document.select("div.table-responsive.mt-3.mt-lg-5");
			//System.out.println("Current size of list = " + body.select("tr").size());
			//StringBuilder holds all the current text scraped so it can be edited
			StringBuilder sb = new StringBuilder();
			for(Element e : body.select("tr")) {
				
				String number = e.select("td.jl-td-num.align-middle.text-center").text();
				String kanji = e.select("td.jl-td-k.align-middle.text-center").text();
				String meaning = e.select("td.jl-td-m.align-middle").text();
			
				if (e.select("td.jl-td-num.align-middle.text-center").text().equals("")) {
					continue;		
				}
				
				else 
					sb.append(number + ". " + kanji + "  " + meaning + "\n");
			}
			updateWindow(sb.toString());
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}	
	}
	

	/*
	 * Creates a highlighter in the document using the searchfield,
	 * to create an instance you need highlight(JTextArea name, JTextField name.getText());
	 * @param JTextComponent takes in the TextField to search among the possible listed words in the field
	 * @param String searches up the pattern or word that was input into the search field to find in document
	 */
	public static void highlight(JTextComponent text, String pattern) {
		removeHighlights(text);
		try {
			
			Highlighter painter = text.getHighlighter();
			javax.swing.text.Document doc = text.getDocument();
			String word = doc.getText(0, doc.getLength());
			int pos = 0;
			boolean firstFound = false;
			while((pos = word.toUpperCase().indexOf(pattern.toUpperCase(), pos)) > 0){
				if (firstFound == false) {
					painter.addHighlight(pos, pos+pattern.length(), myHighlighter);
					display.setCaretPosition(pos);
					firstFound = true;
				}
				else {
					painter.addHighlight(pos, pos+pattern.length(), myHighlighter);
					pos += pattern.length();
				}
			}

		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	/*
	 * Helper function to the highlighting so it can remove all previous highlights once it moves onto the next search
	 * @param JTextComponent takes in the TextField to search among the possible listed words in the field
	 */
	public static void removeHighlights(JTextComponent text) {
		Highlighter painter = text.getHighlighter();
		Highlighter.Highlight[] painted = painter.getHighlights();
		for (int i = 0; i < painted.length; i++) {
			if (painted[i].getPainter() instanceof MyHighlighter) {
				painter.removeHighlight(painted[i]);
			}
		}
	}
}
