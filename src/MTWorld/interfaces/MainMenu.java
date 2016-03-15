package MTWorld.interfaces;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import MTWorld.graphics.Landscape;
import MTWorld.landscapegenerator.PerlinNoiseLandscapeGenerator;
import MTWorld.worlds.MTWorld;

public class MainMenu extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int WIDTH = 350;
	public static final int HEIGHT = 500;

	private JButton start;
	private JButton exit;
	private JButton about;

	private JPanel pan;

	private JSlider slWidth;
	private JLabel lWidth;
	private JSlider slHeight;
	private JLabel lHeight;
	private JSlider slWater;
	private JLabel lWater;
	private JSlider slAltitude;
	private JLabel lAltitude;
	private JSlider slRelief;
	private JLabel lRelief;

	private JFrame aboutFrame;

	// Map predefinies
	private String[] mapNames = {"Default","Plains", "Mountains","High mountains", "Islands", "Islets","Swamps"};
	private JComboBox<String> mapList;

	int i;
	private GridBagConstraints c = new GridBagConstraints();

	// =====

	private int dx = 101;
	private int dy = 101;
	private int MAX_CASE = 160000;

	private double altitude = 0.25;
	private double waterQuantity = 0.4;

	public MainMenu() {
		this.setTitle("MTWorld");
		this.setSize(WIDTH, HEIGHT);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);

		pan = new JPanel(new GridBagLayout());

		i = 0;
		c.gridheight = 1;
		c.gridwidth = 3;
		c.anchor = GridBagConstraints.CENTER;

		JLabel title = new JLabel("=====  MTWorld  =====");
		c.insets = new Insets(10, 25, 0, 25);
		pan.add(title, c);

		i++;

		slWidth = new JSlider(11, 1001);
		slWidth.setPaintTicks(true);
		slWidth.setMajorTickSpacing(200);
		slWidth.setMinorTickSpacing(100);
		slWidth.setValue(dx);
		slWidth.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent event) {

				if (slWidth.getValue() * slHeight.getValue() >= MAX_CASE) {
					slWidth.setValue(MAX_CASE / slHeight.getValue());
				}

				lWidth.setText("Map width : " + (slWidth.getValue() - 1));
				dx = slWidth.getValue();
			}
		});
		lWidth = new JLabel("Map width : " + (slWidth.getValue() - 1));
		addSlider(slWidth, lWidth);

		slHeight = new JSlider(11, 1001);
		slHeight.setPaintTicks(true);
		slHeight.setMajorTickSpacing(200);
		slHeight.setMinorTickSpacing(100);
		slHeight.setValue(dy);
		slHeight.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent event) {

				if (slHeight.getValue() * slWidth.getValue() >= MAX_CASE) {
					slHeight.setValue(MAX_CASE / slWidth.getValue());
				}

				lHeight.setText("Map height : " + (slHeight.getValue() - 1));
				dy = slHeight.getValue();
			}
		});
		lHeight = new JLabel("Map height : " + (slHeight.getValue() - 1));
		addSlider(slHeight, lHeight);

		slWater = new JSlider(0, 100);
		slWater.setPaintTicks(true);
		slWater.setMajorTickSpacing(20);
		slWater.setMinorTickSpacing(10);
		slWater.setValue((int) (waterQuantity * 100));
		slWater.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent event) {
				lWater.setText("Water qantity : " + slWater.getValue() + " %");
				waterQuantity = slWater.getValue() / 100.0;
			}
		});
		lWater = new JLabel("Water qantity : " + slWater.getValue() + " %");
		addSlider(slWater, lWater);

		slAltitude = new JSlider(1, 250);
		slAltitude.setPaintTicks(true);
		slAltitude.setMajorTickSpacing(50);
		slAltitude.setMinorTickSpacing(25);
		slAltitude.setValue((int) (altitude * 100));
		slAltitude.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent event) {
				lAltitude.setText("Relief's amplitude: " + slAltitude.getValue() / 100.0);
				altitude = slAltitude.getValue() / 100.0;
			}
		});
		lAltitude = new JLabel("Relief's amplitude: " + slAltitude.getValue() / 100.0);
		addSlider(slAltitude, lAltitude);

		slRelief = new JSlider(2, 9);
		slRelief.setPaintTicks(true);
		slRelief.setSnapToTicks(true);
		slRelief.setMajorTickSpacing(1);
		slRelief.setValue(9 - PerlinNoiseLandscapeGenerator.getFreqMod());
		slRelief.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent event) {
				lRelief.setText("Relief's frequency : " + slRelief.getValue());
				PerlinNoiseLandscapeGenerator.setFreqMod((9 - slRelief.getValue()));
			}
		});
		lRelief = new JLabel("Relief's frequency : " + slRelief.getValue());
		addSlider(slRelief, lRelief);

		// bouton Start
		start = new JButton("Start");
		start.setPreferredSize(new Dimension(150, 50));
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				setVisible(false);
				if (aboutFrame != null) {
					aboutFrame.setVisible(false);
				}

				// creation du landscape
				Landscape l = new Landscape(new MTWorld(), dx, dy, altitude, waterQuantity);
				l.run();

				// mise en relation du landscape et de l'optionWindow
				OptionWindow ow = new OptionWindow(l.getMyWorld().getMTWorldCA());
				l.setSettingFrame(ow);
				ow.setMainMenu(MainMenu.this);
				ow.setLandscape(l);
			}
		});
		c.insets = new Insets(15, 5, 20, 10);
		c.gridy = i;
		c.weightx = 0.5;
		c.gridwidth = 1;
		c.gridx = 0;
		pan.add(start, c);
		
		// Selecteur du type d'agents
		mapList = new JComboBox<String>(mapNames);
		mapList.setSelectedIndex(0);
		mapList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				switch((String)mapList.getSelectedItem()) {
				case "Default":
					slWater.setValue(40);
					lWater.setText("Water qantity : " + slWater.getValue() + " %");
					waterQuantity = slWater.getValue() / 100.0;
					
					slAltitude.setValue(25);
					lAltitude.setText("Relief's amplitude: " + slAltitude.getValue() / 100.0);
					altitude = slAltitude.getValue() / 100.0;
					
					slRelief.setValue(6);
					lRelief.setText("Relief's frequency : " + slRelief.getValue());
					PerlinNoiseLandscapeGenerator.setFreqMod((9 - slRelief.getValue()));
					
					break;
				case "Plains":
					slWater.setValue(10);
					lWater.setText("Water qantity : " + slWater.getValue() + " %");
					waterQuantity = slWater.getValue() / 100.0;
					
					slAltitude.setValue(4);;
					lAltitude.setText("Relief's amplitude: " + slAltitude.getValue() / 100.0);
					altitude = slAltitude.getValue() / 100.0;
					
					slRelief.setValue(2);
					lRelief.setText("Relief's frequency : " + slRelief.getValue());
					PerlinNoiseLandscapeGenerator.setFreqMod((9 - slRelief.getValue()));
					break;
				case "Mountains":
					slWater.setValue(15);
					lWater.setText("Water qantity : " + slWater.getValue() + " %");
					waterQuantity = slWater.getValue() / 100.0;
					
					slAltitude.setValue(38);
					lAltitude.setText("Relief's amplitude: " + slAltitude.getValue() / 100.0);
					altitude = slAltitude.getValue() / 100.0;
					
					slRelief.setValue(5);
					lRelief.setText("Relief's frequency : " + slRelief.getValue());
					PerlinNoiseLandscapeGenerator.setFreqMod((9 - slRelief.getValue()));
					break;
				case "Islands":
					slWater.setValue(75);
					lWater.setText("Water qantity : " + slWater.getValue() + " %");
					waterQuantity = slWater.getValue() / 100.0;
					
					slAltitude.setValue(50);
					lAltitude.setText("Relief's amplitude: " + slAltitude.getValue() / 100.0);
					altitude = slAltitude.getValue() / 100.0;
					
					slRelief.setValue(4);
					lRelief.setText("Relief's frequency : " + slRelief.getValue());
					PerlinNoiseLandscapeGenerator.setFreqMod((9 - slRelief.getValue()));
					break;
				case "Islets":
					slWater.setValue(63);
					lWater.setText("Water qantity : " + slWater.getValue() + " %");
					waterQuantity = slWater.getValue() / 100.0;
					
					slAltitude.setValue(18);
					lAltitude.setText("Relief's amplitude: " + slAltitude.getValue() / 100.0);
					altitude = slAltitude.getValue() / 100.0;
					
					slRelief.setValue(7);
					lRelief.setText("Relief's frequency : " + slRelief.getValue());
					PerlinNoiseLandscapeGenerator.setFreqMod((9 - slRelief.getValue()));
					break;
				case "High mountains":
					slWater.setValue(0);
					lWater.setText("Water qantity : " + slWater.getValue() + " %");
					waterQuantity = slWater.getValue() / 100.0;
					
					slAltitude.setValue(55);
					lAltitude.setText("Relief's amplitude: " + slAltitude.getValue() / 100.0);
					altitude = slAltitude.getValue() / 100.0;
					
					slRelief.setValue(7);
					lRelief.setText("Relief's frequency : " + slRelief.getValue());
					PerlinNoiseLandscapeGenerator.setFreqMod((9 - slRelief.getValue()));
					break;
				case "Swamps":
					slWater.setValue(42);
					lWater.setText("Water qantity : " + slWater.getValue() + " %");
					waterQuantity = slWater.getValue() / 100.0;
					
					slAltitude.setValue(0);
					lAltitude.setText("Relief's amplitude: " + slAltitude.getValue() / 100.0);
					altitude = slAltitude.getValue() / 100.0;
					
					slRelief.setValue(8);
					lRelief.setText("Relief's frequency : " + slRelief.getValue());
					PerlinNoiseLandscapeGenerator.setFreqMod((9 - slRelief.getValue()));
					break;
					default:
						break;
				}
				
			}
		});
		c.gridy = i;
		c.weightx = 0.5;
		c.gridwidth = 1;
		c.gridx = 1;
		pan.add(mapList,c);
		i++;

		// bouton Exit
		exit = new JButton("Exit");
		exit.setPreferredSize(new Dimension(110, 35));
		exit.setAlignmentY(JButton.LEFT);
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		c.insets = new Insets(10, 0, 15, 0);
		c.gridy = i;
		c.gridwidth = 1;
		c.gridx = 0;
		c.weightx = 0.5;
		pan.add(exit, c);

		// bouton About
		about = new JButton("About");
		about.setPreferredSize(new Dimension(90, 20));
		about.setAlignmentY(JButton.LEFT);
		about.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (aboutFrame == null) {
					aboutFrame = new JFrame();
					aboutFrame.setTitle("About");
					aboutFrame.setSize(220, 150);
					Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
					aboutFrame.setLocation(dim.width / 2 - aboutFrame.getSize().width / 2,
							dim.height / 2 - aboutFrame.getSize().height / 2);
					aboutFrame.getContentPane().setLayout(new GridBagLayout());
					JTextArea text = new JTextArea();
					text.setBackground(new Color(1, 0, 0, 0));
					text.setEditable(false);
					text.setText(aboutText);

					aboutFrame.getContentPane().add(text);
					aboutFrame.setVisible(true);
				} else if (!aboutFrame.isVisible()) {
					aboutFrame.setVisible(true);
				}
			}
		});
		c.insets = new Insets(10, 0, 15, 0);
		c.gridy = c.gridheight - 2;
		c.gridwidth = 1;
		c.weightx = 0.5;
		c.gridx = 1;
		c.gridy = i;
		pan.add(about, c);

		this.getContentPane().add(pan);
		this.pack();
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2
				- this.getSize().height / 2);
		this.setVisible(true);
	}

	public void addSlider(JSlider s, JLabel label) {

		c.insets = new Insets(10, 25, 0, 25);
		if (i == 0) {
			c.insets = new Insets(25, 25, 0, 25);
		}
		c.gridy = i;
		pan.add(s, c);
		i++;

		c.insets = new Insets(2, 15, 5, 15);
		c.gridy = i;
		pan.add(label, c);
		i++;
	}

	private String aboutText = "                     Created by \nTanguy Soto and Malcolm Auffray \n\n"
			+ "                   Project 2I013\n" + "               UPMC 2014/2015";

}
