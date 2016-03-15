/**
 * -----------------
 * Projet 2I013 : Simulation de vie artificielle UPMC
 * 2014/2015
 * 
 * @author Tanguy SOTO
 *  ----------------
 * Gere la partie "vie artificielle" de la simulation
 */

package MTWorld.cellularautomata;

import MTWorld.graphics.ColorPalette;
import MTWorld.worlds.MTWorld;

public class MTWorldCA extends CellularAutomataInteger {

	private MTWorld world;

	private double density = 0.12; // Densite des arbres ; 0.55 -> seuil de percolation
	private double density2 = 0.45; // Densite de l'herbe ; 0.55 -> seuil de // percolation

	private double pFire = 0.0005; // Prob de prendre feu arbre
	private double pFire2 = 0.0001; // Prob de prendre feu herbe

	private double pGrow = 0.0001; // Prob d'apparition d'arbre
	private double pGrow2 = 0.0003; // Prob d'apparition d'herbe

	private double pFlow = 0.95; // Prob de mettre l'eau a jour
	private double waves = 0.05; // quantite de remous dans l'eau
	private double pEvapor = 0.001; // Prob que l'eau s'evapore
	
	private double pLavaFLow = 0.65; // Prob de mettre a jours la lave
	
	private int evolutionSpeed = 20; // Vitesse d'evolution de l'automate

	
	// TEMPERATURES
	private int cooling = 5; // Vitesse de refroidissement des cendres en degree
	private int lavaCooling = 8; // Vitesse de refroidissement de la lave en degree
	
	private float tempSnow = -1; // Temperature d'apparition de la neige
	private int tempCender = 15; // Temperature de disparition des cendres
	
	private int tempTreeFire = 100; // Temperature du feu des arbres
	private int tempFire = 50; // Temperature du feu dans l'herbe
	private int tempLava = 200; // Temperature de la lave
	
	private int hoting = 13; // Temperature additionnee au voisin d'un feu //
	private int hotingLava = 250; // Temperature additionnee au voisin d'un volcan //

	private int tempVariation = 1; // Influence du moment de la journee sur la température

	private int minTemp = -15; // Temperature minimale
	private int baseTemp = 15; // Temperature moyenne
	
	
	// GEOLOGIE
	private double pEruption = 0.001f; // Prob d'apparition d'eruption volcanique
	private int volcanoWakeUp = 1500; // Frequence de reveil du volcan
	private float altitudeVariation = 0.003f; // influence de la fonte de la lave sur l'atltitude
	private float erosion = 0.00005f; // Erosion naturelle des hautes montagnes

	
	
	private double maxVolcanoHeight;
	private double heightToErode;


	private int xLava, yLava; // Coordonnees du volcan
	private int xWater, yWater; // Coordonnee d'une source d'eau

	public MTWorldCA(MTWorld _world, int _dx, int _dy, boolean _buffering) {
		super(_dx, _dy, _buffering);

		world = _world;
	}

	/**
	 * Appelee dans MTWorl.initCellularAutomata() Peuple l'automate.
	 */
	public void init() {

		boolean waterSource = false;

		// Initialisation des etats
		for (int x = 0; x != dx; x++) {
			for (int y = 0; y != dy; y++) {

				int t = (int) (world.getCellHeight(x, y) * (minTemp - baseTemp)
						/ world.getMaxEverHeight() + baseTemp);

				if (world.getCellHeight(x, y) > world.getCellHeight(xLava, yLava)) { // determination de l'emplacement du volcan
					xLava = x;
					yLava = y;
					maxVolcanoHeight = world.getCellHeight(x, y) * 1.8f;
				} else {
					if (!waterSource
							&& world.getCellHeight(x, y) >= world.getMaxEverHeight() * 1.0 / 2.0
							&& Math.random() < 0.005) {
						xWater = x;
						yWater = y;
						waterSource = true;
					}
				}

				world.setCellsTempValues(x, y, t); // temperature

				if (density2 >= Math.random() && world.getCellHeight(x, y) > 0) {
					this.setCellState(x, y, 5); // herbes
				}

				if (density >= Math.random() && world.getCellHeight(x, y) > 0
						&& world.getCellHeightAmpl(x, y) < 0.005) {
					this.setCellState(x, y, 1); // arbres
				} else {
					if (world.getCellHeight(x, y) <= 0) {
						if (Math.random() < waves) {
							world.setCellsWaterValues(x, y, 3);
						} else {
							world.setCellsWaterValues(x, y, 1);
						}
						this.setCellState(x, y, 4); // eau
					}
				}
			}
		}

		// Limite de l'erosion
		heightToErode = world.getCellHeight(xLava, yLava);

		// source d'eau
		world.setCellsWaterValues(xWater, yWater, 1000);
		this.setCellState(xWater, yWater, 4);

		// Place le volcan au point le plus haut
		setCellState(xLava, yLava, 11);
		world.setCellsTempValues(xLava, yLava, tempLava);

		this.swapBuffer();
	}

	/**
	 * Appelee dans MTWorl.stepCellularAutomata() Passage t -> t+1
	 */
	public void step() {

		// Influence de la journee sur la temperature.
		if (world.getDayMoment() == 0.01f) { // La soleil rechauffe
			tempSnow -= tempVariation;
		} else {
			if (world.getDayMoment() == 0.26f) { // La soleil rechauffe encore
				tempSnow -= tempVariation;
			} else {
				if (world.getDayMoment() == 0.51f) { // La nuit refroidit
					tempSnow += tempVariation;
				} else {
					if (world.getDayMoment() == 0.76f) { // La nuit refroidit encore
						tempSnow += tempVariation;
					}
				}
			}
		}

		// Source d'eau infinie
		int sourceValue = world.getCellsWaterValues(xWater, yWater);
		if (sourceValue < 100) {
			world.setCellsWaterValues(xWater, yWater, 1000);
			this.setCellOldState(xWater, yWater, 4);
		}

		for (int x = 0; x != dx; x++) {
			for (int y = 0; y != dy; y++) {

				/* ============ Actualisation des etats ============= */
				if (this.getCellState(x, y) != 99) {
					this.setCellState(x, y, 0);
				}
				double temp = world.getCellsTempValues(x, y);

				switch (this.getCellState(x, y)) {
				case 0: // vide
					if (temp <= tempSnow) {
						this.setCellState(x, y, 8); // neige apparait
					} else {
						if (Math.random() < pGrow2) {
							this.setCellState(x, y, 5); // herbe pousse
						} else {
							if (Math.random() < pGrow && world.getCellHeightAmpl(x, y) < 0.005) { // arbre pousse
								this.setCellState(x, y, 1);
							} else {

								// Si voisin eau -> sable
								int nbWater = 0;
								for (int i = -1; i < 2 && nbWater < 1; i++) {
									for (int j = -1; j < 2 && nbWater < 1; j++) {
										if (this.getCellState((x + i + dx) % dx, (y + j + dy) % dy) == 4
												&& world.getCellHeight((x + i + dx) % dx,
														(y + j + dy) % dy) == 0) {
											nbWater++;
										}
									}
								}
								if (nbWater >= 1) {
									this.setCellState(x, y, 9);
								}
							}
						}
					}
					break;

				case 1: // arbre
					if (this.getCellState((x - 1 + dx) % dx, y) == 2
							|| this.getCellState((x + 1 + dx) % dx, y) == 2
							|| this.getCellState(x, (y - 1 + dy) % dy) == 2
							|| this.getCellState(x, (y + 1 + dy) % dy) == 2
							|| this.getCellState((x - 1 + dx) % dx, y) == 2
							|| this.getCellState((x + 1 + dx) % dx, y) == 11
							|| this.getCellState(x, (y - 1 + dy) % dy) == 11
							|| this.getCellState(x, (y + 1 + dy) % dy) == 11) {
						this.setCellState(x, y, 2); // prend feu car voisin
						world.setCellsTempValues(x, y, tempTreeFire);
					} else {
						if (Math.random() < pFire) { // prend spontanement feu a 100
							this.setCellState(x, y, 2);
							world.setCellsTempValues(x, y, tempTreeFire);
						} else {
							this.setCellState(x, y, 1);
						}
					}
					break;

				case 2: // feu
					for (int i = -1; i < 2; i++) { // rechauffe le voisinage
						for (int j = -1; j < 2; j++) {
							world.setCellsTempValues(x + i, y + j,
									world.getCellsTempValues(x + i, y + j) + hoting);
						}
					}
					this.setCellState(x, y, 3);
					break;
				case 3: // cendre
					if (temp > tempCender) { // refroidit
						world.setCellsTempValues(x, y, temp - cooling);
						this.setCellState(x, y, 3);
					} else { // disparait
						this.setCellState(x, y, 0);
						world.setCellsTempValues(x, y, baseTemp);
					}
					break;
				case 4: // eau

					if (Math.random() < pFlow) { // eau
						int i, j;
						for (int n = 0; n < 8 && world.getCellsWaterValues(x, y) > 0; n++) {
							i = (int) (Math.random() * (4) - 2);
							j = (int) (Math.random() * (4) - 2);

							// si plus bas + vide ou herbe ou eau ou neige ou sable + moins eau -> eau coule
							if (world.getCellHeight(x, y) >= world.getCellHeight((x + i + dx) % dx,
									(y + j + dy) % dy)
									&& (this.getCellState((x + i + dx) % dx, (y + j + dy) % dy) == 9
											|| this.getCellState((x + i + dx) % dx, (y + j + dy)
													% dy) == 0
											|| this.getCellState((x + i + dx) % dx, (y + j + dy)
													% dy) == 4
											|| this.getCellState((x + i + dx) % dx, (y + j + dy)
													% dy) == 5 || this.getCellState((x + i + dx)
											% dx, (y + j + dy) % dy) == 8)
									&& world.getCellsWaterValues((x + i + dx) % dx, (y + j + dy)
											% dy) < world.getCellsWaterValues(x, y)) {

								// ajout d'eau
								world.setCellsWaterValues(
										(x + i + dx) % dx,
										(y + j + dy) % dy,
										world.getCellsWaterValues((x + i + dx) % dx, (y + j + dy)
												% dy) + 1);

								// suppression d'eau
								world.setCellsWaterValues(x, y, world.getCellsWaterValues(x, y) - 1);

								this.setCellState((x + i + dx) % dx, (y + j + dy) % dy, 4);
								this.setCellOldState((x + i + dx) % dx, (y + j + dy) % dy, 99);

								if (world.getCellsWaterValues(x, y) <= 0) { // si plus d'eau -> vide
									this.setCellOldState(x, y, 0);
									this.setCellState(x, y, 0);
									world.setCellsTempValues(x, y, baseTemp);
								} else {
									this.setCellOldState(x, y, 4);
									this.setCellState(x, y, 4); // sinon encore eau
								}
							} else {
								if (world.getCellsWaterValues(x, y) > 0) { // rien ne bouge (mauvaises conditions)
									if (Math.random() < pEvapor && world.getCellHeight(x, y) > 0) { // évaporation
										this.setCellState(x, y, 0);
										world.setCellsWaterValues(x, y, 0);
									} else { // l'eau ne change pas
										this.setCellState(x, y, 4);
									}

								}
							}

						}
					} else {
						this.setCellState(x, y, 4); // rien ne bouge (pas update)
					}

					break;

				case 5: // herbe
					if (Math.random() < pGrow && world.getCellHeightAmpl(x, y) < 0.005) { // arbre pousse
						this.setCellState(x, y, 1);
					} else {

						if (this.getCellState((x - 1 + dx) % dx, y) == 6
								|| this.getCellState((x + 1 + dx) % dx, y) == 6
								|| this.getCellState(x, (y - 1 + dy) % dy) == 6
								|| this.getCellState(x, (y + 1 + dy) % dy) == 6
								|| this.getCellState((x - 1 + dx) % dx, y) == 11
								|| this.getCellState((x + 1 + dx) % dx, y) == 11
								|| this.getCellState(x, (y - 1 + dy) % dy) == 11
								|| this.getCellState(x, (y + 1 + dy) % dy) == 11) {
							this.setCellState(x, y, 6); // prend feu car voisin
						} else {
							if (Math.random() < pFire2) { // prend spontanement feu a 50
								this.setCellState(x, y, 6);
								world.setCellsTempValues(x, y, tempFire);
							} else {
								this.setCellState(x, y, 5); // reste herbe
							}
						}
					}
					break;
				case 6: // feu herbe
					for (int i = -1; i < 2; i++) { // rechauffe le voisinage
						for (int j = -1; j < 2; j++) {
							world.setCellsTempValues(x + i, y + j,
									world.getCellsTempValues(x + i, y + j) + hoting);
						}
					}
					this.setCellState(x, y, 7);
					break;
				case 7: // cendre herbe
					if (temp > tempCender) { // refroidit
						world.setCellsTempValues(x, y, temp - cooling);
						this.setCellState(x, y, 7);
					} else { // disparait
						this.setCellState(x, y, 0);
						world.setCellsTempValues(x, y, baseTemp);
					}
					break;
				case 8: // neige

					if (temp > tempSnow) {
						this.setCellState(x, y, 0); // fond
						//world.setCellsWaterValues(x, y, 1);
					} else {
						this.setCellState(x, y, 8); // reste
					}
					break;
				case 9: // sable
					boolean stop = false;
					for (int i = -1; i < 2 && !stop; i++) {
						for (int j = -1; j < 2 && !stop; j++) {
							if (this.getCellState((x + i + dx) % dx, (y + j + dy) % dy) == 4) {
								stop = true;
							}
						}
					}
					if (stop) {
						this.setCellState(x, y, 9); // eau trouvee -> reste sable
					} else {
						this.setCellState(x, y, 0); // plus de plage
					}

					break;
				case 10: // grammar tree
					this.setCellState(x, y, 10); // reste grammar tree
					break;
				case 11: // Lava				
					if (temp < baseTemp) { // devient roche volcanique -> augmente altitude
						this.setCellState(x, y, 12);

					} else {
						int i, j;
						boolean done = false;
						int stateToGo;

						if (Math.random() < pLavaFLow) {
							for (int n = 0; n < 8 && !done; n++) {
								i = (int) (Math.random() * (4) - 2);
								j = (int) (Math.random() * (4) - 2);

								stateToGo = this.getCellState((x + i + dx) % dx, (y + j + dy) % dy);
								if (world.getCellHeight(x, y) > world.getCellHeight((x + i + dx)
										% dx, (y + j + dy) % dy)
										&& (stateToGo == 9 || stateToGo == 0 || stateToGo == 4
												|| stateToGo == 5 || stateToGo == 8 || stateToGo == 12)) { // si plus bas + (vide ou herbe ou eau ou neige ou sable)

									this.setCellOldState((x + i + dx) % dx, (y + j + dy) % dy, 99);
									this.setCellState((x + i + dx) % dx, (y + j + dy) % dy, 11);
									if (stateToGo == 4) { // eau -> refroidit d'un coup
										if ((temp - lavaCooling * 8) < baseTemp) { // borne inférieure
											world.setCellsTempValues((x + i + dx) % dx,
													(y + j + dy) % dy, baseTemp - 1);
										} else {
											world.setCellsTempValues((x + i + dx) % dx,
													(y + j + dy) % dy, temp - lavaCooling * 8);
										}
									} else { // refroidit normalement
										world.setCellsTempValues((x + i + dx) % dx, (y + j + dy)
												% dy, temp - lavaCooling);
									}

									if (!(x == xLava && y == yLava)) {
										this.setCellState(x, y, 12);
										majHeight(x, y);
										world.setCellsTempValues(x, y, temp - lavaCooling);
									}

									done = true;
								}
							}
						}
						if (!done) { // rien ne bouge (mauvaises conditions)
							this.setCellState(x, y, 11);
							world.setCellsTempValues(x, y, temp - lavaCooling);
						}
					}

					break;
				case 12: // roche volcanique
					if (temp < baseTemp) { // redevient vide
						this.setCellState(x, y, 0);
					} else {
						this.setCellState(x, y, 12);
						world.setCellsTempValues(x, y, temp - lavaCooling);
					}
					break;
				case 20: // trace de la mere
					this.setCellState(x, y, 20); // reste trace de la mere
					break;
				case 99: // Case ignore en asynchrone
					break;
				default: // inconnu -> vide
					this.setCellState(x, y, 0);
					System.out.print("cannot interpret CA state: " + this.getCellState(x, y));
					System.out.println(" (at: " + x + "," + y + " -- height: "
							+ this.world.getCellHeight(x, y) + " )");
					break;
				}

				if (x == xLava && y == yLava) { // volcan -> source infinie
					this.setCellState(x, y, 11);
					world.setCellsTempValues(x, y, tempLava);
					double p = Math.random();
					if (p < pEruption) {
						for (int nb = 0; nb < 1; nb++) {
							majHeight(x, y);
						}
						System.out.println("Evenement : Eruption !");
					}

					if (world.getIteration() % volcanoWakeUp == 0) {
						for (int i = -2; i < 3; i++) { // rechauffe le voisinage
							for (int j = -2; j < 3; j++) {
								world.setCellsTempValues(x + i, y + j, hotingLava);
							}
						}
					}
				}

				// tends vers sa temperature normale
				int tVisee = (int) (world.getCellHeight(x, y) * (minTemp - baseTemp)
						/ world.getMaxEverHeight() + baseTemp);
				temp = world.getCellsTempValues(x, y);
				if (temp < tVisee) {
					world.setCellsTempValues(x, y, temp + 1);
				} else if (temp > tVisee) {
					world.setCellsTempValues(x, y, temp - 1);
				}

				// erosion
				if (world.getCellHeight(x, y) > heightToErode) {
					erosion(x, y);
				}

			}
		}

		this.swapBuffer();

		for (int x = 0; x != dx; x++) {
			for (int y = 0; y != dy; y++) {
				/* ============ Actualisation des couleurs ============= */
				float color[] = new float[3];
				float height = (float) world.getCellHeight(x, y);
				switch (this.getCellState(x, y)) {
				case 0: // vide : brown moutains
					color[0] = 0.855f - 0.5f * height / ((float) world.getMaxEverHeight());
					color[1] = 0.647f - 0.5f * height / ((float) world.getMaxEverHeight());
					color[2] = 0.125f;

					break;
				case 1: // arbre
					color = ColorPalette.tree();
					break;
				case 2: // feu
					color = ColorPalette.fire();
					break;
				case 3: // cendre
					color[0] = (float) (0.6f - 0.5f * world.getCellsTempValues(x, y) / 100.0f);
					color[1] = (float) (0.6f - 0.5f * world.getCellsTempValues(x, y) / 100.0f);
					color[2] = (float) (0.6f - 0.5f * world.getCellsTempValues(x, y) / 100.0f);
					break;
				case 4: // water
					color[0] = 0;
					color[1] = 0.3f;
					color[2] = 0.999f - 0.9f * world.getCellsWaterValues(x, y) / 15f;
					if (color[2] < 0.1f) {
						color[2] = 0.1f;
					}
					if (color[2] >= 1.0f) {
						color[2] = 0.999f;
					}
					break;

				case 5: // herbe
					color = ColorPalette.grass();
					color[1] = color[1] - 0.6f * height / ((float) world.getMaxEverHeight());
					if (color[1] >= 1) {
						color[1] = 0.99f;
					}
					break;
				case 6: // feu herbe
					color = ColorPalette.fire();
					break;
				case 7: // cendre herbe 
					color[0] = (float) (0.6f - 0.5f * world.getCellsTempValues(x, y) / 100.0f);
					color[1] = (float) (0.6f - 0.5f * world.getCellsTempValues(x, y) / 100.0f);
					color[2] = (float) (0.6f - 0.5f * world.getCellsTempValues(x, y) / 100.0f);
					break;
				case 8: //neige
					color = ColorPalette.snow1();
					break;
				case 9: // sable
					color = ColorPalette.sand();
					break;
				case 10: // grammar tree -> sol vide
					color[0] = 0.855f - 0.5f * height / ((float) world.getMaxEverHeight());
					color[1] = 0.647f - 0.5f * height / ((float) world.getMaxEverHeight());
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
					color[0] = (float) (color[0] - color[0] * 0.35f
							* (tempLava - (world.getCellsTempValues(x, y))) / (tempLava * 1.0));
					color[1] = (float) (color[1] - color[1] * 0.35f
							* (tempLava - (world.getCellsTempValues(x, y))) / (tempLava * 1.0));
					if (color[0] >= 1) {
						color[0] = 0.99f;
					}
					if (color[1] >= 1) {
						color[1] = 0.99f;
					}
					break;
				case 20: // trace de la mere
					color = ColorPalette.motherPath();
					break;
				default:
					color[0] = 0.5f;
					color[1] = 0.5f;
					color[2] = 0.5f;
					System.out.print("cannot interpret CA state: " + this.getCellState(x, y));
					System.out.println(" (at: " + x + "," + y + " -- height: "
							+ this.world.getCellHeight(x, y) + " )");
					break;
				}
				this.world.cellsColorValues.setCellState(x, y, color);
			}
		}

	}

	public int getEvolutionSpeed() {
		return evolutionSpeed;
	}

	public void setEvolutionSpeed(int evolutionSpeed) {
		this.evolutionSpeed = evolutionSpeed;
	}

	// Met a jour l'altitude apres passage de la lave
	public void majHeight(int x, int y) {

		if (world.getLandscape().getLandscape()[x][y] + altitudeVariation <= maxVolcanoHeight) {
			world.getLandscape().getLandscape()[x][y] += (altitudeVariation);
		}
		if (world.getLandscape().getLandscape()[x + 1][y + 1] + altitudeVariation <= maxVolcanoHeight) {
			world.getLandscape().getLandscape()[x + 1][y + 1] += (altitudeVariation);
		}
		if (world.getLandscape().getLandscape()[x][y + 1] + altitudeVariation <= maxVolcanoHeight) {
			world.getLandscape().getLandscape()[x][y + 1] += (altitudeVariation);
		}
		if (world.getLandscape().getLandscape()[x + 1][y] + altitudeVariation <= maxVolcanoHeight) {
			world.getLandscape().getLandscape()[x + 1][y] += (altitudeVariation);
		}

		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				double minHeightValue = Math.min(Math.min(world.getLandscape().getLandscape()[(x
						+ i + dx)
						% dx][(y + j + dy) % dy],
						world.getLandscape().getLandscape()[(x + i + 1 + dx) % dx][(y + j + dy)
								% dy]), Math.min(world.getLandscape().getLandscape()[(x + i + dx)
						% dx][(y + j + 1 + dy) % dy], world.getLandscape().getLandscape()[(x + i
						+ 1 + dx)
						% dx][(y + j + 1 + dy) % dy]));
				double maxHeightValue = Math.max(Math.max(world.getLandscape().getLandscape()[(x
						+ i + dx)
						% dx][(y + j + dy) % dy],
						world.getLandscape().getLandscape()[(x + i + 1 + dx) % dx][(y + j + dy)
								% dy]), Math.max(world.getLandscape().getLandscape()[(x + i + dx)
						% dx][(y + j + 1 + dy) % dy], world.getLandscape().getLandscape()[(x + i
						+ 1 + dx)
						% dx][(y + j + 1 + dy) % dy]));
				world.setCellHeight((x + i + dx) % dx, (y + j + dy) % dy,
						(minHeightValue + maxHeightValue) / 2.0);
				world.setCellHeightAmpl(x, y, maxHeightValue - minHeightValue);
			}
		}
	}

	// Met a jour l'altitude apres passage de la lave
	public void majHeight2(int x, int y) {

		if (world.getLandscape().getLandscape()[x][y] - altitudeVariation > 0) {
			world.getLandscape().getLandscape()[x][y] -= (altitudeVariation);
		}
		if (world.getLandscape().getLandscape()[x + 1][y + 1] - altitudeVariation > 0) {
			world.getLandscape().getLandscape()[x + 1][y + 1] -= (altitudeVariation);
		}
		if (world.getLandscape().getLandscape()[x][y + 1] - altitudeVariation > 0) {
			world.getLandscape().getLandscape()[x][y + 1] -= (altitudeVariation);
		}
		if (world.getLandscape().getLandscape()[x + 1][y] - altitudeVariation > 0) {
			world.getLandscape().getLandscape()[x + 1][y] -= (altitudeVariation);
		}

		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				double minHeightValue = Math.min(Math.min(world.getLandscape().getLandscape()[(x
						+ i + dx)
						% dx][(y + j + dy) % dy],
						world.getLandscape().getLandscape()[(x + i + 1 + dx) % dx][(y + j + dy)
								% dy]), Math.min(world.getLandscape().getLandscape()[(x + i + dx)
						% dx][(y + j + 1 + dy) % dy], world.getLandscape().getLandscape()[(x + i
						+ 1 + dx)
						% dx][(y + j + 1 + dy) % dy]));
				double maxHeightValue = Math.max(Math.max(world.getLandscape().getLandscape()[(x
						+ i + dx)
						% dx][(y + j + dy) % dy],
						world.getLandscape().getLandscape()[(x + i + 1 + dx) % dx][(y + j + dy)
								% dy]), Math.max(world.getLandscape().getLandscape()[(x + i + dx)
						% dx][(y + j + 1 + dy) % dy], world.getLandscape().getLandscape()[(x + i
						+ 1 + dx)
						% dx][(y + j + 1 + dy) % dy]));
				world.setCellHeight((x + i + dx) % dx, (y + j + dy) % dy,
						(minHeightValue + maxHeightValue) / 2.0);
				world.setCellHeightAmpl(x, y, maxHeightValue - minHeightValue);
			}
		}
	}

	// Met a jour l'altitude pour l'erosion
	private void erosion(int x, int y) {

		world.getLandscape().getLandscape()[x][y] -= (erosion);
		world.getLandscape().getLandscape()[x + 1][y + 1] -= (erosion);
		world.getLandscape().getLandscape()[x][y + 1] -= (erosion);
		world.getLandscape().getLandscape()[x + 1][y] -= (erosion);

		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				double minHeightValue = Math.min(Math.min(world.getLandscape().getLandscape()[(x
						+ i + dx)
						% dx][(y + j + dy) % dy],
						world.getLandscape().getLandscape()[(x + i + 1 + dx) % dx][(y + j + dy)
								% dy]), Math.min(world.getLandscape().getLandscape()[(x + i + dx)
						% dx][(y + j + 1 + dy) % dy], world.getLandscape().getLandscape()[(x + i
						+ 1 + dx)
						% dx][(y + j + 1 + dy) % dy]));
				double maxHeightValue = Math.max(Math.max(world.getLandscape().getLandscape()[(x
						+ i + dx)
						% dx][(y + j + dy) % dy],
						world.getLandscape().getLandscape()[(x + i + 1 + dx) % dx][(y + j + dy)
								% dy]), Math.max(world.getLandscape().getLandscape()[(x + i + dx)
						% dx][(y + j + 1 + dy) % dy], world.getLandscape().getLandscape()[(x + i
						+ 1 + dx)
						% dx][(y + j + 1 + dy) % dy]));
				world.setCellHeight((x + i + dx) % dx, (y + j + dy) % dy,
						(minHeightValue + maxHeightValue) / 2.0);
				world.setCellHeightAmpl(x, y, maxHeightValue - minHeightValue);
			}
		}
	}

	public double getpFire() {

		return pFire;

	}

	public void setpFire(double pFire) {

		this.pFire = pFire;

	}

	public double getpFire2() {

		return pFire2;

	}

	public void setpFire2(double pFire2) {

		this.pFire2 = pFire2;

	}

	public double getpGrow() {

		return pGrow;

	}

	public void setpGrow(double pGrow) {

		this.pGrow = pGrow;

	}

	public double getpGrow2() {

		return pGrow2;

	}

	public void setpGrow2(double pGrow2) {

		this.pGrow2 = pGrow2;

	}

	public double getpFlow() {

		return pFlow;

	}

	public void setpFlow(double pFlow) {

		this.pFlow = pFlow;

	}

	public double getpEvapor() {

		return pEvapor;

	}

	public void setpEvapor(double pEvapor) {

		this.pEvapor = pEvapor;

	}

	public double getpLavaFLow() {
		return pLavaFLow;
	}

	public void setpLavaFLow(double pLavaFLow) {
		this.pLavaFLow = pLavaFLow;
	}

	public int getCooling() {
		return cooling;
	}

	public void setCooling(int cooling) {
		this.cooling = cooling;
	}

	public int getLavaCooling() {
		return lavaCooling;
	}

	public void setLavaCooling(int lavaCooling) {
		this.lavaCooling = lavaCooling;
	}

	public float getTempSnow() {
		return tempSnow;
	}

	public void setTempSnow(float tempSnow) {
		this.tempSnow = tempSnow;
	}

	public int getTempCender() {
		return tempCender;
	}

	public void setTempCender(int tempCender) {
		this.tempCender = tempCender;
	}

	public int getTempTreeFire() {
		return tempTreeFire;
	}

	public void setTempTreeFire(int tempTreeFire) {
		this.tempTreeFire = tempTreeFire;
	}

	public int getTempFire() {
		return tempFire;
	}

	public void setTempFire(int tempFire) {
		this.tempFire = tempFire;
	}

	public int getTempLava() {
		return tempLava;
	}

	public void setTempLava(int tempLava) {
		this.tempLava = tempLava;
	}

	public int getHoting() {
		return hoting;
	}

	public void setHoting(int hoting) {
		this.hoting = hoting;
	}

	public int getHotingLava() {
		return hotingLava;
	}

	public void setHotingLava(int hotingLava) {
		this.hotingLava = hotingLava;
	}

	public int getTempVariation() {
		return tempVariation;
	}

	public void setTempVariation(int tempVariation) {
		this.tempVariation = tempVariation;
	}

	public double getpEruption() {

		return pEruption;

	}

	public void setpEruption(double pEruption) {

		this.pEruption = pEruption;

	}
	public float getAltitudeVariation() {
		return altitudeVariation;
	}

	public void setAltitudeVariation(float altitudeVariation) {
		this.altitudeVariation = altitudeVariation;
	}

	public float getErosion() {
		return erosion;
	}

	public void setErosion(float erosion) {
		this.erosion = erosion;
	}

	public int getVolcanoWakeUp() {
		return volcanoWakeUp;
	}

	public void setVolcanoWakeUp(int volcanoWakeUp) {
		this.volcanoWakeUp = volcanoWakeUp;
	}
}





