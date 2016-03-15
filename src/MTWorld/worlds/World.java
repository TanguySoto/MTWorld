// created by nicolas.bredeche(at)upmc.fr
// date of creation: 2013-1-12

package MTWorld.worlds;

import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import MTWorld.objects.*;
import MTWorld.cellularautomata.*;
import MTWorld.graphics.Landscape;

public abstract class World {

	public Player player;

	protected int iteration = 0;

	protected ArrayList<UniqueObject> uniqueObjects = new ArrayList<UniqueObject>();
	protected ArrayList<Agent> agents = new ArrayList<Agent>();
	public ListeAgents[][] agentsMap;
	public ListeUniqueObject[][] objectMap;

	protected int dxCA;
	protected int dyCA;

	protected int indexCA;

	public float waterVolume;

	protected Landscape landscape;

	//protected CellularAutomataInteger cellularAutomata; // TO BE DEFINED IN CHILDREN CLASSES

	protected CellularAutomataDouble cellsHeightValuesCA;
	protected CellularAutomataDouble cellsHeightAmplitudeCA;

	public CellularAutomataColor cellsColorValues;

	// Altitudes extremes du landscape correspondant
	protected double maxEverHeightValue = Double.NEGATIVE_INFINITY;
	protected double minEverHeightValue = Double.POSITIVE_INFINITY;

	public World() {
		// ... cf. init() for initialization
	}

	public void init(int _dxCA, int _dyCA, double[][] heightMap,
			Landscape landscape) {
		dxCA = _dxCA;
		dyCA = _dyCA;

		this.landscape = landscape;

		iteration = 0;

		// Initialise la carte des Agents
		agentsMap = new ListeAgents[dxCA][dyCA];
		for (int x = 0; x < dxCA; x++) {
			for (int y = 0; y < dyCA; y++) {
				agentsMap[x][y] = new ListeAgents();
			}
		}

		// Initialise la carte des Objects
		objectMap = new ListeUniqueObject[dxCA][dyCA];
		for (int x = 0; x < dxCA; x++) {
			for (int y = 0; y < dyCA; y++) {
				objectMap[x][y] = new ListeUniqueObject();
			}
		}

		this.cellsHeightValuesCA = new CellularAutomataDouble(_dxCA, _dyCA,
				false);
		this.cellsHeightAmplitudeCA = new CellularAutomataDouble(_dxCA, _dyCA,
				false);

		this.cellsColorValues = new CellularAutomataColor(_dxCA, _dyCA, false);

		int xMaxHeight = 0, yMaxHeight = 0;
		// init altitude and color related information
		for (int x = 0; x != dxCA; x++)
			for (int y = 0; y != dyCA; y++) {
				// compute height values (and amplitude) from the landscape for this CA cell 
				double minHeightValue = Math.min(
						Math.min(heightMap[x][y], heightMap[x + 1][y]),
						Math.min(heightMap[x][y + 1], heightMap[x + 1][y + 1]));
				double maxHeightValue = Math.max(
						Math.max(heightMap[x][y], heightMap[x + 1][y]),
						Math.max(heightMap[x][y + 1], heightMap[x + 1][y + 1]));

				if (this.maxEverHeightValue < maxHeightValue) {
					this.maxEverHeightValue = maxHeightValue;
					xMaxHeight = x;
					yMaxHeight = y;
				}
				if (this.minEverHeightValue > minHeightValue)
					this.minEverHeightValue = minHeightValue;

				cellsHeightAmplitudeCA.setCellState(x, y, maxHeightValue
						- minHeightValue);
				cellsHeightValuesCA.setCellState(x, y,
						(minHeightValue + maxHeightValue) / 2.0);

			}

		initCellularAutomata(_dxCA, _dyCA, heightMap);
	}

	public void step() {
		stepAgents();
		stepCellularAutomata();
		iteration++;
	}

	public int getIteration() {
		return this.iteration;
	}

	abstract protected void stepAgents();

	public ArrayList<Agent> getAgents() {
		return agents;
	}

	// ----

	protected abstract void initCellularAutomata(int _dxCA, int _dyCA,
			double[][] heightMap);

	protected abstract void stepCellularAutomata();

	// ---

	abstract public int getCellValue(int x, int y); // used by the visualization code to call specific object display.

	abstract public void setCellValue(int x, int y, int state);

	abstract public void setCellOldValue(int x, int y, int state);

	// ---- 

	/*
	 * Renvoie la valeur moyenne de la case Doit etre dynamique (recalcul�) car
	 * la hauteur est modifi� par eau etc ...
	 */
	public double getCellHeight(int x, int y) {
		return cellsHeightValuesCA.getCellState(x % dxCA, y % dyCA);
	}

	public double getCellHeightAmpl(int x, int y) // used by the visualization code to set correct height values
	{
		return cellsHeightAmplitudeCA.getCellState(x % dxCA, y % dyCA);
	}

	// ---- 

	public float[] getCellColorValue(int x, int y) // used to display cell color
	{
		float[] cellColor = this.cellsColorValues.getCellState(x % this.dxCA, y
				% this.dyCA);

		float[] color = { cellColor[0], cellColor[1], cellColor[2], 1.0f };

		return color;
	}

	abstract public void displayObjectAt(World _myWorld, GL2 gl, int cellState,
			int x, int y, double height, double width);

	public void displayUniqueObjects(World _myWorld, GL2 gl, GLU glu,
			double height, double width) {

		for (int i = 0; i < agents.size(); i++) {
			agents.get(i).displayObjectAt(_myWorld, gl, glu, height, width);
		}

		for (int i = 0; i < uniqueObjects.size(); i++) {
			uniqueObjects.get(i).displayUniqueObject(_myWorld,
					landscape.getGLU(), gl, height, width);
		}
	}

	public void addAgent(Agent a) {
		agents.add(a);
	}

	public void removeAgent(Agent a) {
		agents.remove(a);
	}
	
	public void addObject(UniqueObject o) {
		uniqueObjects.add(o);
		objectMap[o.getCoordinate()[0]][o.getCoordinate()[1]].add(o);
	}

	public void removeObject(UniqueObject o) {
		objectMap[o.getCoordinate()[0]][o.getCoordinate()[1]].remove(o);
		boolean wasIn  = uniqueObjects.remove(o);
		
		if(!wasIn) { return; }
		
		// update stats		
	}

	public int getWidth() {
		return dxCA;
	}

	public int getHeight() {
		return dyCA;
	}

	public double getMaxEverHeight() {
		return this.maxEverHeightValue;
	}

	public ArrayList<UniqueObject> getUniqueObjects() {
		return uniqueObjects;
	}

	public double getMinEverHeight() {
		return this.minEverHeightValue;
	}

	public Landscape getLandscape() {
		return landscape;
	}

	public double getCellsTempValues(int x, int y) {
		// To be impletemented in children
		return 0;
	}

	public void setCellsTempValues(int x, int y, double value) {
		// To be impletemented in children
	}

	public void setCellHeight(int x, int y, double value) {
		cellsHeightValuesCA.setCellState(x, y, value);
	}

	public void setCellHeightAmpl(int x, int y, double value) {
		cellsHeightAmplitudeCA.setCellState(x, y, value);
	}

	public int getCellsWaterValues(int x, int y) {
		// To be impletemented in children
		return 0;
	}

	public void setCellsWaterValues(int x, int y, int value) {
		// To be impletemented in children
	}

	// A implementer dans la sous classe si utilis�e
	public int getDayDuration() {
		return 0;
	}

	// A implementer dans la sous classe si utilis�e
	public float getDayMoment() {
		return 0;
	}

	// A implementer dans la sous classe si utilis�e
	public void setDayMoment(float m) {
		;
	}
}
