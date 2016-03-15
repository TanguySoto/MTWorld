/**
 * -----------------
 * Projet 2I013 : Simulation de vie artificielle UPMC
 * 2014/2015
 * 
 * @author Tanguy SOTO
 *  ----------------
 * Agent proie
 */

package MTWorld.objects;

import static javax.media.opengl.GL2.GL_QUADS;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import MTWorld.graphics.ColorPalette;
import MTWorld.worlds.MTWorld;
import MTWorld.worlds.World;

public class Prey extends Agent {

	protected static int evolutionSpeed = 35;
	protected static int reproductionSpeed = 1800;
	protected static int starvation = 900;
	protected static int decomposition = 1400;

	protected static float pMove = 0.8f;
	protected static float pComestible = 0.8f;

	protected int age;
	protected int lastTimeEat;
	protected int deathTime;

	public Prey(int __x, int __y, World __world) {
		super(__x, __y, __world);
		isAlive = true;
		age = 0;
		lastTimeEat = 0;
	}

	@Override
	public void step(int index) {
		age++;

		// Reproduction
		if (world.getIteration() % reproductionSpeed == 0 && isAlive && age != 1) {
			Prey p = new Prey(x, y, world);
			world.agentsMap[x][y].add(p);
			world.addAgent(p);
		}

		// Mort
		if ((age - lastTimeEat > starvation || world.getCellsTempValues(x, y) > 60) && isAlive) {
			isAlive = false;
			deathTime = age;
			for (int i = 0; i < 5; i++) {
				if (((MTWorld) world).nbBirdsGroupe > 0) {
					int randGrp = (int) (Math.random() * ((MTWorld) world).nbBirdsGroupe);
					BirdsFly boid = (BirdsFly) world.getAgents().get(randGrp);
					int randBird = (int) (Math.random() * boid.getBirds().size());
					if (randBird>=0 && randBird<boid.getBirds().size() && boid.getBirds().get(randBird).getIsEating() == 0
							&& Math.random() < pComestible) {
						boid.getBirds().get(randBird).setEatingCible(x, y, this);
					}
				}
			}

			//System.out.println("P " + index + " famine"); TODO
		}

		// cadavre disparait
		if (!isAlive && age - deathTime > decomposition) {
			world.removeAgent(this);
			return;
		}

		if (world.getIteration() % evolutionSpeed != 0 || !isAlive) {
			return;
		}

		int stateCourant = world.getCellValue(x, y);
		switch (stateCourant) {
		case 5: // mange herbe
			lastTimeEat = age;
			world.setCellOldValue(x, y, 0);
			// System.out.println("P " + index + " mange"); TODO
			break;
		default:
			break;
		}

		/* ========= Deplacement en fonction du voisinage ========= */
		boolean continuer = true;

		// Regarde en bas
		for (Agent a : world.agentsMap[x][(y - 1 + world.getHeight()) % world.getHeight()]) {
			if (a instanceof Predator) {
				continuer = false;
				orient = 2;
				break;
			}
		}

		// Regarde a droite
		if (continuer) {
			for (Agent a : world.agentsMap[(x + 1 + world.getWidth()) % world.getWidth()][y]) {
				if (a instanceof Predator) {
					continuer = false;
					orient = 3;
					break;
				}
			}
		}

		// Regarde en haut
		if (continuer) {
			for (Agent a : world.agentsMap[x][(y + 1 + world.getHeight()) % world.getHeight()]) {
				if (a instanceof Predator) {
					continuer = false;
					orient = 0;
					break;
				}
			}
		}

		// Regarde a droite
		if (continuer) {
			for (Agent a : world.agentsMap[(x - 1 + world.getWidth()) % world.getWidth()][y]) {
				if (a instanceof Predator) {
					continuer = false;
					orient = 1;
					break;
				}
			}
		}

		// Si rien trouve, aleatoire
		if (continuer && Math.random() < pMove) {
			orient = (int) (Math.random() * (4));
		}

		// Calcule de la case destination
		int newX = -1, newY = -1, state = -1;
		switch (orient) {
		case 0: // nord
			newX = x;
			newY = (y - 1 + world.getHeight()) % world.getHeight();
			state = world.getCellValue(newX, newY);
			break;
		case 1: // est
			newX = (x + 1 + world.getWidth()) % world.getWidth();
			newY = y;
			state = world.getCellValue(newX, newY);
			break;
		case 2: // sud
			newX = x;
			newY = (y + 1 + world.getHeight()) % world.getHeight();
			state = world.getCellValue(newX, newY);
			break;
		case 3: // ouest
			newX = (x - 1 + world.getWidth()) % world.getWidth();
			newY = y;
			state = world.getCellValue(newX, newY);
			break;
		}

		// Mise a jours selon regles de vie de l'agents
		if (state == 0 || state == 5 || state == 9) { // peut aller sur vide,
														// herbe et sable
			((MTWorld) world).agentsMap[x][y].remove(this);
			x = newX;
			y = newY;
			((MTWorld) world).agentsMap[x][y].add(this);
		}

	}

	public void displayObjectAt(World myWorld, GL2 gl, GLU glu, double height, double width) {

		switch (state) {

		case 0:

			// Evite la repetition des calculs pour chaque vertex
			double e = x * width + width / 4;
			double f = x * width + 3 * width / 4;

			double i = y * height + height / 4;
			double j = y * height + 3 * height / 4;

			double floor = myWorld.getCellHeight(x, y);// Math.min(Math.min(a,
														// b), Math.min(c, d));
			double middle = floor * myWorld.getLandscape().getHeightFactor() * height + height
					* 0.3f;

			floor = floor * myWorld.getLandscape().getHeightFactor() * height;

			if (isAlive) {
				float[] color = ColorPalette.prey();
				gl.glColor3f(color[0], color[1], color[2]);
			} else { // cadavre
				float[] color = ColorPalette.deadPrey();
				gl.glColor3f(color[0], color[1], color[2]);
			}

			gl.glBegin(GL_QUADS);

			if (myWorld.getLandscape().DISPLAY_OBJECTS) {

				gl.glVertex3d(e, i, floor);
				gl.glVertex3d(e, i, middle);
				gl.glVertex3d(e, j, middle);
				gl.glVertex3d(e, j, floor);

				float[] color = ColorPalette.prey2();
				if (isAlive)
					gl.glColor3f(color[0], color[1], color[2]);

				gl.glVertex3d(e, j, floor);
				gl.glVertex3d(e, j, middle);
				gl.glVertex3d(f, j, middle);
				gl.glVertex3d(f, j, floor);

				color = ColorPalette.prey();
				if (isAlive)
					gl.glColor3f(color[0], color[1], color[2]);

				gl.glVertex3d(f, i, floor);
				gl.glVertex3d(f, i, middle);
				gl.glVertex3d(f, j, middle);
				gl.glVertex3d(f, j, floor);

				color = ColorPalette.prey2();
				if (isAlive)
					gl.glColor3f(color[0], color[1], color[2]);

				gl.glVertex3d(e, i, floor);
				gl.glVertex3d(e, i, middle);
				gl.glVertex3d(f, i, middle);
				gl.glVertex3d(f, i, floor);

				color = ColorPalette.prey();
				if (isAlive)
					gl.glColor3f(color[0], color[1], color[2]);

				gl.glVertex3d(e, i, middle);
				gl.glVertex3d(f, i, middle);
				gl.glVertex3d(f, j, middle);
				gl.glVertex3d(e, j, middle);
			} else {
				gl.glVertex3d(e, i, middle);
				gl.glVertex3d(f, i, middle);
				gl.glVertex3d(f, j, middle);
				gl.glVertex3d(e, j, middle);
			}

			gl.glEnd();

			break;

		default:
			break;
		}
	}

}
