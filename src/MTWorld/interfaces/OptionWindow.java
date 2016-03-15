package MTWorld.interfaces;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import MTWorld.cellularautomata.MTWorldCA;
import MTWorld.graphics.Landscape;

public class OptionWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	public static final int WIDTH = 280;
	public static final int HEIGHT = Landscape.HEIGHT;

	private MTWorldCA mtworldCA;

	private JPanel panel;
	public Landscape landscape;
	private MainMenu mainMenu;

	private String[] tabO = { "General", "Temperature", "Geology", "Agents" };
	public Board[] panneaux = new Board[4];
	private JTabbedPane onglet;
	
	private JFrame helpFrame;

	public OptionWindow(MTWorldCA mtworldCA) {
		this.mtworldCA = mtworldCA;
		panneaux[0] = new Board(0, this);
		panneaux[1] = new Board(1, this);
		panneaux[2] = new Board(2, this);
		panneaux[3] = new Board(3, this);
		setTitle("Setting");
		setSize(WIDTH, HEIGHT);
		//setLocation(Landscape.WIDTH,0);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (helpFrame != null) {
					helpFrame.setVisible(false);
					helpFrame.dispose();
				}
				closeWindows();
			}
		});

		panel = new JPanel(new GridBagLayout());

		onglet = new JTabbedPane();

		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(10, 5, 5, 10);
		c.gridheight = 1;
		c.gridwidth = 1;

		c.gridy = 0;
		panel.add(onglet, c);

		int i = -1;
		for (Board pan : panneaux) {
			onglet.add(tabO[++i].toString(), pan);
		}

		// bouton back
		JButton back = new JButton("Back");
		back.setPreferredSize(new Dimension(90, 30));
		back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (helpFrame != null) {
					helpFrame.setVisible(false);
					helpFrame.dispose();
				}
				closeWindows();
			}
		});
		c.gridy = 1;
		panel.add(back, c);
		
		// bouton help
		JButton help = new JButton("Help");
		help.setPreferredSize(new Dimension(90, 20));
		help.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (helpFrame == null) {
					helpFrame = new JFrame();
					helpFrame.setTitle("Help");
					helpFrame.setSize(600, 500);
					Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
					helpFrame.setLocation(dim.width / 2 - helpFrame.getSize().width / 2,
							dim.height / 2 - helpFrame.getSize().height / 2);
					helpFrame.getContentPane().setLayout(new GridBagLayout());
					JTextArea text = new JTextArea();
					text.setBackground(new Color(1, 0, 0, 0));
					text.setEditable(false);
					text.setText(helpText);

					helpFrame.getContentPane().add(text);
					helpFrame.setVisible(true);
				} else if (!helpFrame.isVisible()) {
					helpFrame.setVisible(true);
				}
			}
		});
		c.gridy = 2;
		panel.add(help, c);
		
		
		this.getContentPane().add(new JScrollPane(panel), BorderLayout.CENTER);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2
				- this.getSize().height / 2);
		this.setResizable(true);
		this.setVisible(true);

	}

	public Landscape getLandscape() {
		return landscape;
	}

	public void setLandscape(Landscape landscape) {
		this.landscape = landscape;
	}

	public MainMenu getMainMenu() {
		return mainMenu;
	}

	public void setMainMenu(MainMenu mainMenu) {
		this.mainMenu = mainMenu;
	}

	public MTWorldCA getMtworldCA() {
		return mtworldCA;
	}

	public void setMtworldCA(MTWorldCA mtworldCA) {
		this.mtworldCA = mtworldCA;
	}

	public void closeWindows() {
		if (helpFrame != null) {
			helpFrame.setVisible(false);
			helpFrame.dispose();
		}
		landscape.stopSimulation();
		dispose();
		mainMenu.setVisible(true);
	}
	
	private String helpText="Welcome, Here are some usefull commands :\n\n"+
							"General :\n"+
							"   m : show/hide map\n"+
							"   o : enable/disable 3D objects\n"+
							"   p : pause/run the simulation\n"+
							"   UP/DOWN : change the player object (displayed in the terminal)\n"+
							"   ESCAPE : stop the simulation\n\n"+
							"Player :\n"+
							"   a + RIGHT BUTTON PRESSED : move up\n"+
							"   d + RIGHT BUTTON PRESSED : move right\n"+
							"   q + RIGHT BUTTON PRESSED : move left\n"+
							"   s + RIGHT BUTTON PRESSED : move backward\n"+
							"   RIGHT + LEFT BUTTON PRESSED : put the current object at the player ball's location when moving\n"+
							"   x : put the current object at the player ball's location\n\n"+
							"Camera :\n"+
							"   a : move up\n"+
							"   d : move right\n"+
							"   q : move left\n"+
							"   s : move backward\n"+
							"   w : move down\n"+
							"   z : move forward\n"+
							"   j : move the camera to the player ball\n"+
							"   MOUSE DRAG : change orientation\n"+
							"   MOUSE WHEEL : move forward/backward\n";

}