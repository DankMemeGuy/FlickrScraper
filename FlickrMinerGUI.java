import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * FlickrMiner GUI
 * 
 * @author Jayden Weaver
 *
 */
public class FlickrMinerGUI {

	final static double VERSION = 1.0;

	public static void main(String[] args) throws IOException {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		JFrame frame = new JFrame("FlickrMiner " + VERSION);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JTextField profilePath = new JTextField(50);
		JButton scrapeButton = new JButton("Scrape User");

		ActionListener scrapeMonitor = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				scrapeButton.setText("SCRAPING...");
				scrapeButton.setForeground(Color.RED);

				if (profilePath.getText().equals("")) {
					System.err.println("Cannot leave username blank!");
					scrapeButton.setText("Scrape User");
					scrapeButton.setForeground(Color.BLACK);
					return;
				}

				System.out.println("WILL ATTEMPT TO PARSE: " + profilePath.getText());
				FlickrProfile profile = new FlickrProfile(profilePath.getText().substring(
						profilePath.getText().indexOf("photos/") + 7, profilePath.getText().length() - 1), "");
				profile.parseProfile();
				profile.savePhotos();
			}
		};

		scrapeButton.addActionListener(scrapeMonitor);
		scrapeButton.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));

		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setPreferredSize(new Dimension(700, 300));
		tabbedPane.setFocusable(false);

		JPanel panel1 = new JPanel();
		JLabel pathLabel = new JLabel("Profile Path: ");

		panel1.add(pathLabel);
		panel1.add(profilePath);
		panel1.add(scrapeButton);

		JPanel aboutPanel = new JPanel();
		JLabel author = new JLabel("@author: Jayden Weaver");
		JLabel github = new JLabel("github.com/jayden2013");
		JLabel year = new JLabel("2021");
		JLabel versionLabel = new JLabel("Version: " + VERSION);
		github.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				try {
					Desktop.getDesktop().browse(new URI("http://www.github.com/jayden2013")); // github account
				} catch (Exception ex) {
					System.err.println(ex);
				}
			}
		});
		github.setCursor(new Cursor(Cursor.HAND_CURSOR));

		aboutPanel.setLayout(new BoxLayout(aboutPanel, BoxLayout.Y_AXIS));
		aboutPanel.add(author);
		aboutPanel.add(github);
		aboutPanel.add(year);
		aboutPanel.add(versionLabel);

		tabbedPane.addTab("Z-750 Binary Rifle", panel1);
		tabbedPane.addTab("About", aboutPanel);

		frame.setLayout(new FlowLayout());
		frame.add(tabbedPane);
		frame.pack();
		frame.setVisible(true);

	}

}
