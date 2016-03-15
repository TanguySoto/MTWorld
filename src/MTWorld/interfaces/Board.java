package MTWorld.interfaces;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import MTWorld.cellularautomata.MTWorldCA;
import MTWorld.objects.Bird;

@SuppressWarnings("serial")
public class Board extends JPanel{
	
	private OptionWindow ow;
	private MTWorldCA mtworldca;
	
	private GridBagConstraints c = new GridBagConstraints();
	
	// ========== General ======== //
	private JSlider slPFire;
	private JLabel lPFire;
	
	private JSlider slPFire2;
	private JLabel lPFire2;
	
	private JSlider slPGrow;
	private JLabel lPGrow;
	
	private JSlider slPGrow2;
	private JLabel lPGrow2;
	
	private JSlider slPFlow;
	private JLabel lPFlow;
	
	private JSlider slPEvapor;
	private JLabel lPEvapor;
	
	private JSlider slPLavaFLow;
	private JLabel lPLavaFLow;
	
	// ========== Temperatures ========= //
	private JSlider slCooling;
	private JLabel lCooling;
	
	private JSlider slLavaCooling;
	private JLabel lLavaCooling;
	
	private JSlider slTempSnow;
	private JLabel lTempSnow;
	
	private JSlider slTempTreeFire;
	private JLabel lTempTreeFire;
	
	private JSlider slTempFire;
	private JLabel lTempFire;
	
	private JSlider slTempLava;
	private JLabel lTempLava;
	
	private JSlider slTempVariation;
	private JLabel lTempVariation;
	
	// ========== Geologie ====== //
	private JSlider slPEruption;
	private JLabel lPEruption;
	
	private JSlider slAltitudeVariation;
	private JLabel lAltitudeVariation;
	
	private JSlider slErosion;
	private JLabel lErosion;
	
	// ========== Agents ======== //
	private JTextArea agentsInfos;
	
	private JSpinner nbSpinner; // Selection du nombre d'agents a modifier
	
	// Selection des agents a modifier
	private String[] agentsNames = { "Bird","Child", "Fish", "Mother", "Predator", "Prey" };
	private JComboBox<String> agentsList;
	
	private JButton add; // Ajout / suppression d'agents
	private JButton del; //
	
	private JCheckBox verification;
	
	// Forces des oiseaux
	private JSlider slOrientation;
	private JLabel lOrientation;
	
	private JSlider slCohesion;
	private JLabel lCohesion;
	
	private JSlider slSeparation;
	private JLabel lSeparation;
	
	private JSlider slSpeed;
	private JLabel lSpeed;
	
	public Board(int num, OptionWindow ow){
		super();	
		GridBagLayout g = new GridBagLayout();
		this.setLayout(g);
	
		this.ow=ow;
		
		switch(num){
			// General
			case 0:
				// Probabilite de prendre feu arbre
				slPFire = new JSlider(0,500);
			   	slPFire.setPaintTicks(true);
			    slPFire.setMajorTickSpacing(100);
			    slPFire.setMinorTickSpacing(50);
			    slPFire.setValue(((int)(ow.getMtworldCA().getpFire()*10000)));
			    slPFire.addChangeListener(new ChangeListener() {
			    	@Override
			    	public void stateChanged(ChangeEvent event) {				
			    		Board.this.ow.getMtworldCA().setpFire(slPFire.getValue()/10000.0);
			    		lPFire.setText("Fire probability (tree) : "+Board.this.ow.getMtworldCA().getpFire());
			    		
			   		}
			   	});
			    lPFire=new JLabel("Fire probability (tree) : "+slPFire.getValue()/10000.0);
			    addSlider(slPFire, lPFire);			    
			    
			    
			    // Probabilite de prendre feu herbe
			    slPFire2 = new JSlider(0,500);
			   	slPFire2.setPaintTicks(true);
			    slPFire2.setMajorTickSpacing(100);
			    slPFire2.setMinorTickSpacing(50);
			    slPFire2.setValue(((int)(ow.getMtworldCA().getpFire2()*10000)));
			    slPFire2.addChangeListener(new ChangeListener() {
			    	@Override
			    	public void stateChanged(ChangeEvent event) {
			    		Board.this.ow.getMtworldCA().setpFire2(slPFire2.getValue()/10000.0);
			    		lPFire2.setText("Fire probability (grass) : "+Board.this.ow.getMtworldCA().getpFire2());			
			   		}
			   	});
			    lPFire2=new JLabel("Fire probability (grass) : "+slPFire2.getValue()/10000.0);
			    addSlider(slPFire2, lPFire2);
			    
			    
			    // Probabilite d'apparition d'arbre
			    slPGrow = new JSlider(0,500);
			   	slPGrow.setPaintTicks(true);
			    slPGrow.setMajorTickSpacing(100);
			    slPGrow.setMinorTickSpacing(50);
			    slPGrow.setValue(((int)(ow.getMtworldCA().getpGrow()*10000)));
			    slPGrow.addChangeListener(new ChangeListener() {
			    	@Override
			    	public void stateChanged(ChangeEvent event) {	
			    		Board.this.ow.getMtworldCA().setpGrow(slPGrow.getValue()/10000.0);
			    		lPGrow.setText("Appearance probability (tree) : "+Board.this.ow.getMtworldCA().getpGrow());			
			   		}
			   	});
			    lPGrow=new JLabel("Appearance probability (tree) : "+slPGrow.getValue()/10000.0);
			    addSlider(slPGrow, lPGrow);
			    
			    
			    // Probabilite d'apparition d'herbe
			    slPGrow2 = new JSlider(0,500);
			   	slPGrow2.setPaintTicks(true);
			    slPGrow2.setMajorTickSpacing(100);
			    slPGrow2.setMinorTickSpacing(50);
			    slPGrow2.setValue(((int)(ow.getMtworldCA().getpGrow2()*10000)));
			    slPGrow2.addChangeListener(new ChangeListener() {
			    	@Override
			    	public void stateChanged(ChangeEvent event) {
			    		Board.this.ow.getMtworldCA().setpGrow2(slPGrow2.getValue()/10000.0);
			    		lPGrow2.setText("Appearance probability (grass) : "+Board.this.ow.getMtworldCA().getpGrow2());			
			   		}
			   	});
			    lPGrow2=new JLabel("Appearance probability (grass) : "+slPGrow2.getValue()/10000.0);
			    addSlider(slPGrow2, lPGrow2);
			    
			    
			    // Probabilite de mettre l'eau a jour
			    slPFlow = new JSlider();
			   	slPFlow.setPaintTicks(true);
			    slPFlow.setMajorTickSpacing(20);
			    slPFlow.setMinorTickSpacing(5);
			    slPFlow.setValue(((int)(ow.getMtworldCA().getpFlow()*100)));
			    slPFlow.addChangeListener(new ChangeListener() {
			    	@Override
			    	public void stateChanged(ChangeEvent event) {	
			    		Board.this.ow.getMtworldCA().setpFlow(slPFlow.getValue()/100.0);
			    		lPFlow.setText("Water viscosity : "+Board.this.ow.getMtworldCA().getpFlow());			
			   		}
			   	});
			    lPFlow=new JLabel("Water viscosity : "+slPFlow.getValue()/100.0);
			    addSlider(slPFlow, lPFlow);
			    
			    
			    // Probabilite que l'eau s'evapore
			    slPEvapor= new JSlider(0,500);
			    slPEvapor.setPaintTicks(true);
			    slPEvapor.setMajorTickSpacing(100);
			    slPEvapor.setMinorTickSpacing(50);
			    slPEvapor.setValue(((int)(ow.getMtworldCA().getpEvapor()*1000)));
			    slPEvapor.addChangeListener(new ChangeListener() {
			    	@Override
			    	public void stateChanged(ChangeEvent event) {	
			    		Board.this.ow.getMtworldCA().setpEvapor(slPEvapor.getValue()/1000.0);
			    		lPEvapor.setText("Water evaporation : "+Board.this.ow.getMtworldCA().getpEvapor());			
			   		}
			   	});
			    lPEvapor=new JLabel("Water evaporation : "+slPEvapor.getValue()/1000.0);
			    addSlider(slPEvapor, lPEvapor);
			    
			    
			    // Probabilite de mettre a jour la lave
			    slPLavaFLow = new JSlider();
			   	slPLavaFLow.setPaintTicks(true);
			    slPLavaFLow.setMajorTickSpacing(20);
			    slPLavaFLow.setMinorTickSpacing(5);
			    slPLavaFLow.setValue(((int)(ow.getMtworldCA().getpLavaFLow()*100)));
			    slPLavaFLow.addChangeListener(new ChangeListener() {
			    	@Override
			    	public void stateChanged(ChangeEvent event) {		
			    		Board.this.ow.getMtworldCA().setpLavaFLow(slPLavaFLow.getValue()/100.0);
			    		lPLavaFLow.setText("Lava viscosity : "+Board.this.ow.getMtworldCA().getpLavaFLow());			
			   		}
			   	});
			    lPLavaFLow=new JLabel("Lava viscosity : "+slPLavaFLow.getValue()/100.0);
			    addSlider(slPLavaFLow, lPLavaFLow);
			    
			    
			    
			    break;
			case 1:
				// Vitesse de refroidissement des cendres en degree
				slCooling = new JSlider(0,100);
				slCooling.setPaintTicks(true);
				slCooling.setMajorTickSpacing(20);
				slCooling.setMinorTickSpacing(5);
				slCooling.setValue(ow.getMtworldCA().getCooling());
				slCooling.addChangeListener(new ChangeListener() {
			    	@Override
			    	public void stateChanged(ChangeEvent event) {				
			    		Board.this.ow.getMtworldCA().setCooling(slCooling.getValue());
			    		lCooling.setText("Cender cooling speed: "+Board.this.ow.getMtworldCA().getCooling());
			    		
			   		}
			   	});
				lCooling=new JLabel("Cender cooling speed : "+slCooling.getValue());
			    addSlider(slCooling, lCooling);		    
			    
			    // Vitesse de refroidissement de la lave en degree
			    slLavaCooling = new JSlider(0,100);
			   	slLavaCooling.setPaintTicks(true);
			    slLavaCooling.setMajorTickSpacing(20);
			    slLavaCooling.setMinorTickSpacing(5);
			    slLavaCooling.setValue(ow.getMtworldCA().getLavaCooling());
			    slLavaCooling.addChangeListener(new ChangeListener() {
			    	@Override
			    	public void stateChanged(ChangeEvent event) {				
			    		Board.this.ow.getMtworldCA().setLavaCooling(slLavaCooling.getValue());
			    		lLavaCooling.setText("Lava cooling speed : "+Board.this.ow.getMtworldCA().getLavaCooling());
			    		
			   		}
			   	});
			    lLavaCooling=new JLabel("Lava cooling speed : "+slLavaCooling.getValue());
			    addSlider(slLavaCooling, lLavaCooling);			    
			    
			    // Temperature d'apparition de la neige
			    slTempSnow = new JSlider(0,20);
			   	slTempSnow.setPaintTicks(true);
			    slTempSnow.setMajorTickSpacing(5);
			    slTempSnow.setMinorTickSpacing(1);
			    slTempSnow.setValue((int)(ow.getMtworldCA().getTempSnow()+10));
			    slTempSnow.addChangeListener(new ChangeListener() {
			    	@Override
			    	public void stateChanged(ChangeEvent event) {				
			    		Board.this.ow.getMtworldCA().setTempSnow(slTempSnow.getValue()-10);
			    		lTempSnow.setText("Snow limit temperature : "+Board.this.ow.getMtworldCA().getTempSnow());
			    		
			   		}
			   	});
			    lTempSnow=new JLabel("Snow limit temperature : "+(slTempSnow.getValue()-10));
			    addSlider(slTempSnow, lTempSnow);			    	    
			    
			    // Temperature du feu des arbres
			    slTempTreeFire = new JSlider(0,500);
			   	slTempTreeFire.setPaintTicks(true);
			    slTempTreeFire.setMajorTickSpacing(100);
			    slTempTreeFire.setMinorTickSpacing(50);
			    slTempTreeFire.setValue(ow.getMtworldCA().getTempTreeFire());
			    slTempTreeFire.addChangeListener(new ChangeListener() {
			    	@Override
			    	public void stateChanged(ChangeEvent event) {				
			    		Board.this.ow.getMtworldCA().setTempTreeFire(slTempTreeFire.getValue());
			    		lTempTreeFire.setText("Fire temparature (tree) : "+Board.this.ow.getMtworldCA().getTempTreeFire());
			    		
			   		}
			   	});
			    lTempTreeFire=new JLabel("Fire temparature (tree) : "+slTempTreeFire.getValue());
			    addSlider(slTempTreeFire, lTempTreeFire);			    
			    
				// Temperature du feu dans l'herbe
			    slTempFire = new JSlider(0,500);
			   	slTempFire.setPaintTicks(true);
			    slTempFire.setMajorTickSpacing(100);
			    slTempFire.setMinorTickSpacing(50);
			    slTempFire.setValue(ow.getMtworldCA().getTempTreeFire());
			    slTempFire.addChangeListener(new ChangeListener() {
			    	@Override
			    	public void stateChanged(ChangeEvent event) {				
			    		Board.this.ow.getMtworldCA().setTempFire(slTempFire.getValue());
			    		lTempFire.setText("Fire temparature (grass) : "+Board.this.ow.getMtworldCA().getTempFire());
			    		
			   		}
			   	});
			    lTempFire=new JLabel("Fire temparature (grass) : "+slTempFire.getValue());
			    addSlider(slTempFire, lTempFire);
			    
			    // Temperature de la lave
			    slTempLava = new JSlider(0,500);
			   	slTempLava.setPaintTicks(true);
			    slTempLava.setMajorTickSpacing(100);
			    slTempLava.setMinorTickSpacing(50);
			    slTempLava.setValue(ow.getMtworldCA().getTempLava());
			    slTempLava.addChangeListener(new ChangeListener() {
			    	@Override
			    	public void stateChanged(ChangeEvent event) {				
			    		Board.this.ow.getMtworldCA().setTempLava(slTempLava.getValue());
			    		lTempLava.setText("Lava temperature : "+Board.this.ow.getMtworldCA().getTempLava());
			    		
			   		}
			   	});
			    lTempLava=new JLabel("Lava temperature : "+slTempLava.getValue());
			    addSlider(slTempLava, lTempLava);			    
			    		    
			    // Influence du moment de la journee sur la température
			    slTempVariation = new JSlider(0,10);
			   	slTempVariation.setPaintTicks(true);
			    slTempVariation.setMajorTickSpacing(5);
			    slTempVariation.setMinorTickSpacing(1);
			    slTempVariation.setValue(ow.getMtworldCA().getTempVariation());
			    slTempVariation.addChangeListener(new ChangeListener() {
			    	@Override
			    	public void stateChanged(ChangeEvent event) {				
			    		Board.this.ow.getMtworldCA().setTempVariation(slTempVariation.getValue());
			    		lTempVariation.setText("Day/night temperature gap : "+Board.this.ow.getMtworldCA().getTempVariation());
			    		
			   		}
			   	});
			    lTempVariation=new JLabel("Day/night temperature gap : "+slTempVariation.getValue());
			    addSlider(slTempVariation, lTempVariation);			    
			    
				break;
			case 2:
				// Probabilite d'apparition d'eruption volcanique
			    slPEruption = new JSlider(0,500);
			   	slPEruption.setPaintTicks(true);
			    slPEruption.setMajorTickSpacing(100);
			    slPEruption.setMinorTickSpacing(50);
			    slPEruption.setValue(((int)(ow.getMtworldCA().getpEruption()*1000)));
			    slPEruption.addChangeListener(new ChangeListener() {
			    	@Override
			    	public void stateChanged(ChangeEvent event) {		
			    		Board.this.ow.getMtworldCA().setpEruption(slPEruption.getValue()/1000.0);
			    		lPEruption.setText("Eruption probability : "+Board.this.ow.getMtworldCA().getpEruption());			
			   		}
			   	});
			    lPEruption=new JLabel("Eruption probability : "+slPEruption.getValue()/1000.0);
			    addSlider(slPEruption, lPEruption);	
			    
			    // influence de la fonte de la lave sur l'atltitude
			    slAltitudeVariation = new JSlider(0,500);
			   	slAltitudeVariation.setPaintTicks(true);
			    slAltitudeVariation.setMajorTickSpacing(100);
			    slAltitudeVariation.setMinorTickSpacing(50);
			    slAltitudeVariation.setValue(((int)(ow.getMtworldCA().getAltitudeVariation()*10000)));
			    slAltitudeVariation.addChangeListener(new ChangeListener() {
			    	@Override
			    	public void stateChanged(ChangeEvent event) {		
			    		Board.this.ow.getMtworldCA().setAltitudeVariation((float)(slAltitudeVariation.getValue()/10000.0));
			    		lAltitudeVariation.setText("Altitude variation : "+Board.this.ow.getMtworldCA().getAltitudeVariation());			
			   		}
			   	});
			    lAltitudeVariation=new JLabel("Altitude variation : "+slAltitudeVariation.getValue()/10000.0);
			    addSlider(slAltitudeVariation, lAltitudeVariation);
			    
			    // Erosion naturelle des hautes montagnes
			    slErosion = new JSlider(0,500);
			   	slErosion.setPaintTicks(true);
			    slErosion.setMajorTickSpacing(100);
			    slErosion.setMinorTickSpacing(50);
			    slErosion.setValue(((int)(ow.getMtworldCA().getErosion()*100000)));
			    slErosion.addChangeListener(new ChangeListener() {
			    	@Override
			    	public void stateChanged(ChangeEvent event) {		
			    		Board.this.ow.getMtworldCA().setErosion((float)(slErosion.getValue()/100000.0));
			    		lErosion.setText("Erosion : "+Board.this.ow.getMtworldCA().getErosion());			
			   		}
			   	});
			    lErosion=new JLabel("Erosion : "+slErosion.getValue()/100000.0);
			    addSlider(slErosion, lErosion);
			    break;
				
				
				
			// Agents
			case 3:
				c.insets=new Insets(5,5,15,5);
				
				agentsInfos = new JTextArea();
				agentsInfos.setBackground(new Color(1, 0, 0, 0));
				agentsInfos.setEditable(false);
				c.gridwidth=0;
				c.gridx=0;
				this.add(agentsInfos,c);
				
				c.insets=new Insets(5,5,5,5);
				
				// Selecteur du nombre d'agents
				SpinnerNumberModel monthModel =  new SpinnerNumberModel(1, 1, 100, 1);
				nbSpinner = new JSpinner(monthModel);
				c.gridwidth=1;
				c.gridy=1;
				c.gridx=0;
				this.add(nbSpinner,c);
				
				// Selecteur du type d'agents
				agentsList = new JComboBox<String>(agentsNames);
				agentsList.setSelectedIndex(0);
				c.gridy=1;
				c.gridx=1;
				this.add(agentsList,c);
				
				// Boutons ajout/suppression
				add= new JButton("Add");
				add.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						Board.this.ow.landscape.getMyWorld().addAgent((int)nbSpinner.getValue(), (String) agentsList.getSelectedItem());
					}
				} );
				del= new JButton("Remove");
				del.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						Board.this.ow.landscape.getMyWorld().removeAgent((int)nbSpinner.getValue(), (String) agentsList.getSelectedItem());
					}
				} );
				c.gridwidth=1;
				c.gridy=2;
				c.gridx=0;
				this.add(add,c);
				c.gridx=1;
				this.add(del,c);
				
				verification= new JCheckBox("Population control");
				verification.setSelected(true);
				verification.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						Board.this.ow.landscape.getMyWorld().verifyAgents=verification.isSelected();				
					}
				});
				c.gridwidth=0;
				c.gridy=3;
				c.gridx=0;
				this.add(verification,c);
				
				JLabel l = new JLabel("Bird's behavior :");
			    c.gridy=4;
			    c.gridx=0;
			    c.insets=new Insets(10,5,5,25);
			    this.add(l,c);
				
				// Forces des oiseaux
				slOrientation = new JSlider(0,20);
				slOrientation.setPaintTicks(true);
			    slOrientation.setMajorTickSpacing(2);
			    slOrientation.setMinorTickSpacing(1);
			    slOrientation.setValue((int) Bird.awt);
			    slOrientation.addChangeListener(new ChangeListener() {
			    	@Override
			    	public void stateChanged(ChangeEvent event) {				
			    		Bird.awt=slOrientation.getValue();
			    		lOrientation.setText("Orientation : "+Bird.awt);
			   		}
			   	});
			    lOrientation=new JLabel("Orientation : "+slOrientation.getValue());
			    c.gridy=5;
			    c.insets=new Insets(5,5,0,2);
			    this.add(slOrientation,c);
			    c.gridy=6;
			    c.insets=new Insets(2,5,5,5);
			    this.add(lOrientation,c);
			    
			    slCohesion = new JSlider(0,20);
				slCohesion.setPaintTicks(true);
			    slCohesion.setMajorTickSpacing(2);
			    slCohesion.setMinorTickSpacing(1);
			    slCohesion.setValue((int) Bird.cwt);
			    slCohesion.addChangeListener(new ChangeListener() {
			    	@Override
			    	public void stateChanged(ChangeEvent event) {				
			    		Bird.cwt=slCohesion.getValue();
			    		lCohesion.setText("Cohesion : "+Bird.cwt);
			   		}
			   	});
			    lCohesion=new JLabel("Cohesion : "+slCohesion.getValue());
			    c.gridy=7;
			    c.insets=new Insets(5,5,0,2);
			    this.add(slCohesion,c);
			    c.gridy=8;
			    c.insets=new Insets(2,5,5,5);
			    this.add(lCohesion,c);
			    
			    slSeparation = new JSlider(0,20);
				slSeparation.setPaintTicks(true);
			    slSeparation.setMajorTickSpacing(2);
			    slSeparation.setMinorTickSpacing(1);
			    slSeparation.setValue((int) Bird.swt);
			    slSeparation.addChangeListener(new ChangeListener() {
			    	@Override
			    	public void stateChanged(ChangeEvent event) {				
			    		Bird.swt=slSeparation.getValue();
			    		lSeparation.setText("Separation : "+Bird.swt);
			   		}
			   	});
			    lSeparation=new JLabel("Separation : "+slSeparation.getValue());
			    c.gridy=9;
			    c.insets=new Insets(5,5,0,2);
			    this.add(slSeparation,c);
			    c.gridy=10;
			    c.insets=new Insets(2,5,5,5);
			    this.add(lSeparation,c);
			    
			    slSpeed = new JSlider(0,100);
				slSpeed.setPaintTicks(true);
			    slSpeed.setMajorTickSpacing(20);
			    slSpeed.setMinorTickSpacing(10);
			    slSpeed.setValue((int) (Bird.maxspeed*100));
			    slSpeed.addChangeListener(new ChangeListener() {
			    	@Override
			    	public void stateChanged(ChangeEvent event) {				
			    		Bird.maxspeed=slSpeed.getValue()/100.0f;
			    		lSpeed.setText("Speed : "+(int) (Bird.maxspeed*100));
			   		}
			   	});
			    lSpeed=new JLabel("Speed : "+(int) (Bird.maxspeed*100));
			    c.gridy=12;
			    c.insets=new Insets(5,5,0,2);
			    this.add(slSpeed,c);
			    c.gridy=13;
			    c.insets=new Insets(2,5,5,5);
			    this.add(lSpeed,c);
				
				break;
			default: break;
		}
		
	}
		
	public void addSlider(JSlider s, JLabel label) {
		c.gridwidth=0;
		c.gridx=0;
		c.insets=new Insets(10,5,0,2);
		
		this.add(s,c);
		
		c.insets=new Insets(2,5,5,5);
		this.add(label,c);
	}
	
	public MTWorldCA getMTWorldCA(){
		return mtworldca;
	}
	
	public void setMTWorldCA(MTWorldCA mtworldca){
		this.mtworldca = mtworldca;
	}
	
	public void updateAgentsData(int nbBirds, int nbChild, int nbFish, int nbMother, int nbPredator, int nbPrey) {
		String text = "Birds :\t"+nbBirds+
					  "\nChildren :\t"+nbChild+
					  "\nFishs :\t"+nbFish+
					  "\nMothers :\t"+nbMother+
					  "\nPredators :\t"+nbPredator+
					  "\nPreys :\t"+nbPrey+
					  "\n -->   Total :  "+(nbBirds+nbChild+nbFish+nbMother+nbPredator+nbPrey);
		agentsInfos.setText(text);
		this.repaint();
	}
}
