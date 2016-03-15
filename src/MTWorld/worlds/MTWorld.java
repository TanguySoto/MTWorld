package MTWorld.worlds;

import javax.media.opengl.GL2;

import MTWorld.cellularautomata.CellularAutomataDouble;
import MTWorld.cellularautomata.CellularAutomataInteger;
import MTWorld.cellularautomata.MTWorldCA;
import MTWorld.graphics.ColorPalette;
import MTWorld.graphics.Landscape;
import MTWorld.objects.Agent;
import MTWorld.objects.Bird;
import MTWorld.objects.BirdsFly;
import MTWorld.objects.Child;
import MTWorld.objects.Fish;
import MTWorld.objects.Fruits;
import MTWorld.objects.GrammarTree;
import MTWorld.objects.Moon;
import MTWorld.objects.Mother;
import MTWorld.objects.Player;
import MTWorld.objects.Predator;
import MTWorld.objects.Prey;
import MTWorld.objects.Sun;
import MTWorld.objects.Tree;
import MTWorld.objects.UniqueObject;

public class MTWorld extends World {

	protected MTWorldCA automate;

	protected CellularAutomataInteger cellsWaterValues;
	protected CellularAutomataDouble cellsTempValues;

	protected final int dayDuration = 4000; // -> '12H'
	protected float dayMoment; // 0->1, 1=fin de la journee

	public boolean verifyAgents = true;

	/* ======= DONNEES STATISTIQUES ======= */
	public int nbPrey = 0;
	public int nbPredator = 0;

	public int nbBirdsGroupe = 0;
	public int nbBirds = 0;

	public int nbMother = 0;
	public int nbChild = 0;

	public int nbFish = 0;

	public int nbFruit = 0;

	public void init(int dxCA, int dyCA, double[][] heightMap, Landscape landscape) {
		super.init(dxCA, dyCA, heightMap, landscape);

		// Initialise les couleurs
		for (int x = 0; x < dxCA; x++) {
			for (int y = 0; y < dyCA; y++) {
				float color[] = new float[3];

				float height = (float) getCellHeight(x, y);
				switch (this.getCellValue(x, y)) {
				case 0: // vide : green moutains
					color[0] = 0.855f - 0.5f * height / ((float) getMaxEverHeight());
					color[1] = 0.647f - 0.5f * height / ((float) getMaxEverHeight());
					color[2] = 0.125f;
					break;
				case 1: // arbre
					color = ColorPalette.tree();
					break;
				case 2: // feu
					color = ColorPalette.fire();
					break;
				case 3: // cendre
					color[0] = (float) (0.8f - 0.6f * getCellsTempValues(x, y) / 100.0f);
					color[1] = (float) (0.8f - 0.6f * getCellsTempValues(x, y) / 100.0f);
					color[2] = (float) (0.8f - 0.6f * getCellsTempValues(x, y) / 100.0f);
					break;
				case 4: // water
					color[0] = 0;
					color[1] = 0.3f;
					color[2] = 1.0f - 0.9f * getCellsWaterValues(x, y) / 10f;
					if (color[2] < 0.1f)
						color[2] = 0.1f;
					break;

				case 5: // grass
					color = ColorPalette.grass();
					color[0] = color[0] * height / ((float) getMaxEverHeight());
					color[1] = color[1] * height / ((float) getMaxEverHeight());
					break;
				case 8: // neige
					color = ColorPalette.snow1();
					break;
				case 9: // sable
					color = ColorPalette.sand();
					break;
				case 10: // grammar tree -> sol vide
					color[0] = 0.855f - 0.5f * height / ((float) getMaxEverHeight());
					color[1] = 0.647f - 0.5f * height / ((float) getMaxEverHeight());
					color[2] = 0.125f;
					if (color[0] >= 1) {
						color[0] = 0.99f;
					}
					if (color[1] >= 1) {
						color[1] = 0.99f;
					}
					break;
				case 11: // lave
					color = ColorPalette.lava();
					break;
				case 12: // roche volcanique
					color = ColorPalette.lava();
					break;
				case 20: // trace de la mere
					color = ColorPalette.motherPath();
					break;
				default:
					color[0] = 0.5f;
					color[1] = 0.5f;
					color[2] = 0.5f;
					System.out.print("cannot interpret CA state: " + this.getCellValue(x, y));
					System.out.println(" (at: " + x + "," + y + " -- height: "
							+ this.getCellHeight(x, y) + " )");
					break;
				}
				this.cellsColorValues.setCellState(x, y, color);
			}
		}

		// Initialise les objets : (Agents (uniques et dynamiques), uniques)

		// Agents
		// Oiseaux (independants), toujours ajout� en premiers
		BirdsFly bF = new BirdsFly(this);
		for (int i = 0; i < 50; i++) {
			bF.addBoid(new Bird(
					dxCA / 2 * landscape.getSize(),
					dyCA / 2 * landscape.getSize(),
					(float) (maxEverHeightValue * landscape.getHeightFactor() * landscape.getSize() * 4.5),
					this));
		}
		addAgent(bF);

		BirdsFly bF2 = new BirdsFly(this);
		for (int i = 0; i < 20; i++) {
			bF2.addBoid(new Bird(
					dxCA / 6 * landscape.getSize(),
					dyCA / 6 * landscape.getSize(),
					(float) (maxEverHeightValue * landscape.getHeightFactor() * landscape.getSize() * 4.5),
					this));
		}
		addAgent(bF2);

		// Proie
		int x, y;
		for (int i = 0; i < (dxCA + dyCA) / 2; i++) {
			x = (int) (Math.random() * (dxCA));
			y = (int) (Math.random() * (dyCA));

			if (getCellValue(x, y) != 0 && getCellValue(x, y) != 5 && getCellValue(x, y) != 8
					&& getCellValue(x, y) != 9) {
				i--;
			} else {
				Prey p = new Prey(x, y, this);
				addAgent(p);
			}
		}

		// Predateur
		for (int i = 0; i < (dxCA+dyCA) * 5.0 / 12; i++) {
			x = (int) (Math.random() * (dxCA));
			y = (int) (Math.random() * (dyCA));

			if (getCellValue(x, y) != 0 && getCellValue(x, y) != 5 && getCellValue(x, y) != 8
					&& getCellValue(x, y) != 9) {
				i--;
			} else {
				Predator pr = new Predator(x, y, this);
				addAgent(pr);
			}
		}

		// mother and child
		for (int i = 0; i < (dxCA+dyCA) / 40 + 1; i++) {
			x = (int) (Math.random() * (dxCA));
			y = (int) (Math.random() * (dyCA));

			if (getCellValue(x, y) != 0 && getCellValue(x, y) != 5 && getCellValue(x, y) != 8
					&& getCellValue(x, y) != 9) {
				i--;
			} else {
				Mother mo = new Mother(x, y, this);
				addAgent(mo);
				Child c = new Child(x, y, this);
				addAgent(c);
			}
		}

		// fish
		for (int i = 0; i < (dxCA+dyCA) * 0.3; i++) {
			x = (int) (Math.random() * (dxCA));
			y = (int) (Math.random() * (dyCA));

			if (getCellValue(x, y) != 4 && getCellHeight(x, y) != 0) {
				i--;
			} else {
				Fish f = new Fish(x, y, this);
				addAgent(f);
			}
		}

		// Grammar Tree
		for (int i = 0; i < 1; i++) {
			x = (int) (Math.random() * (dxCA));
			y = (int) (Math.random() * (dyCA));
			if (getCellValue(x, y) == 4) {
				i--;
			} else {
				GrammarTree g = new GrammarTree(x, y, this);
				addAgent(g);
			}
		}

		// Unique
		// Sun toujours en premier dans uniqueObjects
		Sun s = new Sun(0, (int) (dyCA / 2 * landscape.getSize()), this);
		uniqueObjects.add(s);
		Moon m = new Moon(0, (int) (dyCA / 2 * landscape.getSize()), this);
		uniqueObjects.add(m);
		player = new Player(dxCA / 2, dyCA / 2, this);
		uniqueObjects.add(player);
	}

	@Override
	protected void stepAgents() {
		for (int i = 0; i < this.agents.size(); i++) {
			this.agents.get(i).step(i);
		}

		if (iteration % 20 == 0) {
			/*
			System.out.println("NB Agents = " + agents.size());
			System.out.println("  NB Prey = " + nbPrey);
			System.out.println("  NB Predator = " + nbPredator);
			System.out.println("  NB Mother = " + nbMother);
			System.out.println("  NB Child = " + nbChild);
			System.out.println("  NB Birds = " + nbBirds);
			System.out.println("  NB Fishs = " + nbFish);
			System.out.println("  NB Fruits = " + nbFruit);
			System.out.println("Average fitness :"+fit/nbFish);
			*/

			landscape.getSettingFrame().panneaux[3].updateAgentsData(nbBirds, nbChild, nbFish,
					nbMother, nbPredator, nbPrey);
		}

		if (iteration % 150 == 0 && verifyAgents) {
			verifyAgent();
		}

	}

	public void addAgent(Agent a) {

		if (a instanceof BirdsFly) {
			agents.add(0, a);
		} else {
			agents.add(a);
		}

		if (a instanceof Prey) {
			agentsMap[a.getCoordinate()[0]][a.getCoordinate()[1]].add(a);
			nbPrey++;
		} else if (a instanceof Predator) {
			agentsMap[a.getCoordinate()[0]][a.getCoordinate()[1]].add(a);
			nbPredator++;
		} else if (a instanceof BirdsFly) { // oiseaux ne sont pas dans la carte d'agent
			nbBirds += ((BirdsFly) a).getBirds().size();
			nbBirdsGroupe++;
		} else if (a instanceof Mother) {
			agentsMap[a.getCoordinate()[0]][a.getCoordinate()[1]].add(a);
			nbMother++;
		} else if (a instanceof Child) {
			agentsMap[a.getCoordinate()[0]][a.getCoordinate()[1]].add(a);
			nbChild++;
		} else if (a instanceof Fish) {
			agentsMap[a.getCoordinate()[0]][a.getCoordinate()[1]].add(a);
			nbFish++;
		}
	}

	@Override
	public void removeAgent(Agent a) {

		boolean wasIn = agents.remove(a);

		if (!wasIn) {
			return;
		}

		if (a instanceof Prey) {
			agentsMap[a.getCoordinate()[0]][a.getCoordinate()[1]].remove(a);
			nbPrey--;
		} else if (a instanceof Predator) {
			agentsMap[a.getCoordinate()[0]][a.getCoordinate()[1]].remove(a);
			nbPredator--;
		} else if (a instanceof BirdsFly) {
			nbBirds -= ((BirdsFly) a).getBirds().size();
			nbBirdsGroupe--;
		} else if (a instanceof Mother) {
			agentsMap[a.getCoordinate()[0]][a.getCoordinate()[1]].remove(a);
			nbMother--;
		} else if (a instanceof Child) {
			agentsMap[a.getCoordinate()[0]][a.getCoordinate()[1]].remove(a);
			nbChild--;
		} else if (a instanceof Fish) {
			agentsMap[a.getCoordinate()[0]][a.getCoordinate()[1]].remove(a);
			nbFish--;
		}
	}

	@Override
	public void addObject(UniqueObject o) {
		uniqueObjects.add(o);
		objectMap[o.getCoordinate()[0]][o.getCoordinate()[1]].add(o);

		// update stats
		if (o instanceof Fruits) {
			nbFruit++;
		}
	}

	@Override
	public void removeObject(UniqueObject o) {
		objectMap[o.getCoordinate()[0]][o.getCoordinate()[1]].remove(o);
		boolean wasIn = uniqueObjects.remove(o);

		if (!wasIn) {
			return;
		}

		// update stats
		if (o instanceof Fruits) {
			nbFruit--;
		}
	}

	@Override
	protected void initCellularAutomata(int _dxCA, int _dyCA, double[][] heightMap) {
		cellsTempValues = new CellularAutomataDouble(_dxCA, _dyCA, false);
		cellsWaterValues = new CellularAutomataInteger(_dxCA, _dyCA, false);

		automate = new MTWorldCA(this, _dxCA, _dyCA, true);
		automate.init();
	}

	@Override
	protected void stepCellularAutomata() {
		if (iteration % automate.getEvolutionSpeed() == 0) {
			automate.step();
		}
	}

	@Override
	public int getCellValue(int x, int y) {
		return automate.getCellState((x + dxCA) % dxCA, (y + dyCA) % dyCA);
	}

	@Override
	public void setCellValue(int x, int y, int state) {
		automate.setCellState((x + dxCA) % dxCA, (y + dyCA) % dyCA, state);
	}

	// Utilisee pour l'eau (asynchrone) et agents
	public void setCellOldValue(int x, int y, int state) {
		automate.setCellOldState((x + dxCA) % dxCA, (y + dyCA) % dyCA, state);
	}

	public void displayObjectAt(World _myWorld, GL2 gl, int cellState, int x, int y, double height,
			double width) {
		switch (cellState) {
		case 0:
			break;
		case 1: // trees: green, fire, burnt
		case 2:
		case 3:
			Tree.displayObjectAt(_myWorld, gl, cellState, x, y, height, width);
			break;
		case 5:
			// Grass.displayObjectAt(_myWorld,gl,cellState, x, y, height,
			// width);
			break;
		default:
			// nothing to display at this location.
			break;
		}
	}

	public double getCellsTempValues(int x, int y) {
		return cellsTempValues.getCellState((x + dxCA) % dxCA, (y + dyCA) % dyCA);
	}

	public void setCellsTempValues(int x, int y, double value) {
		cellsTempValues.setCellState((x + dxCA) % dxCA, (y + dyCA) % dyCA, value);
	}

	public int getCellsWaterValues(int x, int y) {
		return cellsWaterValues.getCellState((x + dxCA) % dxCA, (y + dyCA) % dyCA);
	}

	public void setCellsWaterValues(int x, int y, int value) {
		cellsWaterValues.setCellState((x + dxCA) % dxCA, (y + dyCA) % dyCA, value);
	}

	// Modifie la heightmap (altitude) en fonction de 'action' NON UTILISEE
	public void modifyHeightMap(int x, int y, int action) {
		switch (action) {
		default:
			break;
		}
	}

	public int getDayDuration() {
		return dayDuration;
	}

	public void setDayMoment(float m) {
		dayMoment = m;
	}

	public float getDayMoment() {
		return dayMoment;
	}

	public void swapBuffer() {
		automate.swapBuffer();
	}

	// renvoie une % de la hauteur max
	public double getSunHeight() {
		return ((Sun) uniqueObjects.get(0)).getCurrentHeight()
				/ ((Sun) uniqueObjects.get(0)).getMaxHeight();
	}

	// Fonction d'ajustement de la quantité d'agent
	private void verifyAgent() {

		// Proie
		if (nbPrey < (dxCA+dyCA) / 5) {
			addAgent(dxCA / 8, "Prey");
		}

		// Predateur
		if (nbPredator < (dxCA+dyCA) / 5) {
			addAgent(dxCA / 8, "Predator");
		}

		// Mother and child
		if (nbMother < (dxCA+dyCA) / 40 + 1) {
			addAgent(2, "Mother");
		}

		// child
		if (nbChild < nbMother) {
			addAgent(1, "Child");
		}

		// fish
		if (nbFish < (dxCA+dyCA) * 0.1) {
			addAgent((int) ((dxCA+dyCA) * 0.3), "Fish");
		}

		// birds
		if (nbBirds < 10) {
			addAgent(20,"Bird");
			addAgent(40,"Bird");
		}
	}

	public MTWorldCA getMTWorldCA() {
		return automate;
	}

	public void addAgent(int n, String name) {
		int j = 0;
		switch (name) {
		case "Bird":
			BirdsFly bF = new BirdsFly(this);
			for (int i = 0; i < n; i++) {
				bF.addBoid(new Bird(dxCA / 2 * landscape.getSize(), dyCA / 2 * landscape.getSize(),
						(float) (maxEverHeightValue * landscape.getHeightFactor()
								* landscape.getSize() * 4.5), this));
			}
			addAgent(bF);
			break;
		case "Child":
			for (int i = 0; i < n && j < n * n; i++) {
				j++;
				int x = (int) (Math.random() * (dxCA));
				int y = (int) (Math.random() * (dyCA));

				if (getCellValue(x, y) != 0 && getCellValue(x, y) != 5 && getCellValue(x, y) != 8
						&& getCellValue(x, y) != 9) {
					i--;
				} else {
					Child c = new Child(x, y, this);
					addAgent(c);
				}
			}
			break;
		case "Fish":
			for (int i = 0; i < n && j < n * n; i++) {
				j++;
				int x = (int) (Math.random() * (dxCA));
				int y = (int) (Math.random() * (dyCA));

				if (getCellValue(x, y) != 4 && getCellHeight(x, y) != 0) {
					i--;
				} else {
					Fish f = new Fish(x, y, this);
					addAgent(f);
				}
			}

			break;
		case "Mother":
			for (int i = 0; i < n && j < n * n; i++) {
				j++;
				int x = (int) (Math.random() * (dxCA));
				int y = (int) (Math.random() * (dyCA));

				if (getCellValue(x, y) != 0 && getCellValue(x, y) != 5 && getCellValue(x, y) != 8
						&& getCellValue(x, y) != 9) {
					i--;
				} else {
					Mother mo = new Mother(x, y, this);
					addAgent(mo);
				}
			}
			break;

		case "Prey":
			for (int i = 0; i < n && j < n * n; i++) {
				j++;
				int x = (int) (Math.random() * (dxCA));
				int y = (int) (Math.random() * (dyCA));

				if (getCellValue(x, y) != 0 && getCellValue(x, y) != 5 && getCellValue(x, y) != 8
						&& getCellValue(x, y) != 9) {
					i--;
				} else {
					Prey p = new Prey(x, y, this);
					addAgent(p);
				}
			}

			break;

		case "Predator":
			for (int i = 0; i < n && j < n * n; i++) {
				j++;
				int x = (int) (Math.random() * (dxCA));
				int y = (int) (Math.random() * (dyCA));

				if (getCellValue(x, y) != 0 && getCellValue(x, y) != 5 && getCellValue(x, y) != 8
						&& getCellValue(x, y) != 9) {
					i--;
				} else {
					Predator pr = new Predator(x, y, this);
					addAgent(pr);
				}
			}
			break;
		default:
			break;
		}
	}

	public void removeAgent(int n, String name) {
		int j = 0, i, k;
		switch (name) {
		case "Bird":
			i = agents.size() - 1;
			while (i >= 0 && j < n) {
				if (agents.get(i) instanceof BirdsFly) {
					BirdsFly fly = (BirdsFly) agents.get(i);
					k = fly.getBirds().size() - 1;
					while (k >= 0 && j < n) {
						fly.getBirds().remove(k);
						j++;
						nbBirds--;
						k--;
					}
					if (j == n) {
						break;
					} else {
						removeAgent(fly);
					}
				}
				i--;
			}
			break;
		case "Child":
			i = agents.size() - 1;
			while (i >= 0 && j < n) {
				if (agents.get(i) instanceof Child) {
					j++;
					removeAgent(agents.get(i));
				}
				i--;
			}

			break;
		case "Fish":
			i = agents.size() - 1;
			while (i >= 0 && j < n) {
				if (agents.get(i) instanceof Fish) {
					j++;
					removeAgent(agents.get(i));
				}
				i--;
			}
			break;
		case "Mother":
			i = agents.size() - 1;
			while (i >= 0 && j < n) {
				if (agents.get(i) instanceof Mother) {
					j++;
					removeAgent(agents.get(i));
				}
				i--;
			}
			break;

		case "Prey":
			i = agents.size() - 1;
			while (i >= 0 && j < n) {
				if (agents.get(i) instanceof Prey) {
					j++;
					removeAgent(agents.get(i));
				}
				i--;
			}
			break;

		case "Predator":
			i = agents.size() - 1;
			while (i >= 0 && j < n) {
				if (agents.get(i) instanceof Predator) {
					j++;
					removeAgent(agents.get(i));
				}
				i--;
			}
			break;
		default:
			break;
		}
	}

}
