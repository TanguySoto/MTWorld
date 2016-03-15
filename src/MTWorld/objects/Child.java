/**
 * -----------------
 * Projet 2I013 : Simulation de vie artificielle UPMC
 * 2014/2015
 * 
 * @author Tanguy SOTO
 *  ----------------
 * Agent se deplacant en suivant les traces laissées par sa mère
 */

package MTWorld.objects;

import static javax.media.opengl.GL2.GL_QUADS;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import MTWorld.graphics.ColorPalette;
import MTWorld.worlds.MTWorld;
import MTWorld.worlds.World;

public class Child extends Agent {

	protected static int evolutionSpeed = 37;
	protected static int starvation = 3450;

	protected static float pMove = 0.8f;
	protected static float pDecompose = 0.0018f;

	protected int age;
	protected int lastTimeEat;

	protected boolean isFollowing = false;

	public Child(int __x, int __y, World __world) {
		super(__x, __y, __world);
		isAlive = true;
		age = 0;
		lastTimeEat = 0;
	}

	@Override
	public void step(int index) {
		age++;

		// Mort
		if ((age - lastTimeEat > starvation || world.getCellsTempValues(x, y) > 60) && isAlive) {
			isAlive = false;
			//System.out.println("M " + index + " famine");
		}

		if (world.getIteration() % evolutionSpeed != 0 || !isAlive) {
			if (Math.random() < pDecompose && !isAlive) {
				world.removeAgent(this);
			}
			return;
		}

		int dx = world.getWidth();
		int dy = world.getHeight();

		int newX = -1, newY = -1, state = -1;

		boolean traceFound = false;

		// recherche de trace 
		for (int i = -1; i <= 1 && !traceFound; i++) {
			for (int j = -1; j <= 1 && !traceFound; j++) {
				for (UniqueObject o : world.objectMap[(x + i + dx) % dx][(y + j + dy) % dy]) {
					if (o instanceof Fruits) { // fruit atteint  -> mange
						world.removeObject(o);
						lastTimeEat = age;
						break;
					} else if (o instanceof MotherTrace) { // trace trouvee -> suit la trace
						traceFound = true;
						newX = (x + i + dx) % dx;
						newY = (y + j + dy) % dy;
						state = world.getCellValue(newX, newY);
						world.removeObject(o);
						break;
					}
				}

				if (!traceFound) { // si rien trouve cherche mere
					for (Agent a : world.agentsMap[(x + i + dx) % dx][(y + j + dy) % dy]) {
						if (a instanceof Mother) {
							traceFound = true;
							newX = (x + i + dx) % dx;
							newY = (y + j + dy) % dy;
							state = world.getCellValue(newX, newY);
							break;
						}
					}
				}
			}
		}

		if (!traceFound) {

			orient = (int) (Math.random() * (4)); // aleatoire

			// Calcule de la case destination
			switch (orient) {
			case 0: // nord
				newX = x;
				newY = (y - 1 + dy) % dy;
				state = world.getCellValue(newX, newY);
				break;
			case 1: // est
				newX = (x + 1 + dx) % dx;
				newY = y;
				state = world.getCellValue(newX, newY);
				break;
			case 2: // sud
				newX = x;
				newY = (y + 1 + dy) % dy;

				break;
			case 3: // ouest
				newX = (x - 1 + dx) % dx;
				newY = y;
				state = world.getCellValue(newX, newY);
				break;
			}
		}

		// Verifie que c'est possible
		if (state == 0 || state == 5 || state == 9 || state == 8) { // peut aller sur vide, herbe et sable ou neige
			((MTWorld) world).agentsMap[x][y].remove(this);
			x = newX;
			y = newY;
			((MTWorld) world).agentsMap[x][y].add(this);
		}

	}

	public void displayObjectAt(World myWorld, GL2 gl, GLU glu, double height, double width) {

		// Evite la repetition des calculs pour chaque vertex
		double e = x * width + width / 4;
		double f = x * width + width * 0.5;

		double i = y * height + height / 4;
		double j = y * height + height * 0.5;

		double floor = myWorld.getCellHeight(x, y);//Math.min(Math.min(a, b), Math.min(c, d));
		double middle = floor * myWorld.getLandscape().getHeightFactor() * height + height;

		floor = floor * myWorld.getLandscape().getHeightFactor() * height;

		float[] color;
		if (isAlive) {
			color = ColorPalette.mother();
			gl.glColor3f(color[0], color[1], color[2]);

			gl.glBegin(GL_QUADS);

			if (myWorld.getLandscape().DISPLAY_OBJECTS) {
				gl.glVertex3d(e, i, floor);
				gl.glVertex3d(e, i, middle);
				gl.glVertex3d(e, j, middle);
				gl.glVertex3d(e, j, floor);

				color = ColorPalette.motherPath();
				gl.glColor3f(color[0], color[1], color[2]);
				gl.glVertex3d(e, j, floor);
				gl.glVertex3d(e, j, middle);
				gl.glVertex3d(f, j, middle);
				gl.glVertex3d(f, j, floor);

				color = ColorPalette.mother();
				gl.glColor3f(color[0], color[1], color[2]);
				gl.glVertex3d(f, i, floor);
				gl.glVertex3d(f, i, middle);
				gl.glVertex3d(f, j, middle);
				gl.glVertex3d(f, j, floor);

				color = ColorPalette.motherPath();
				gl.glColor3f(color[0], color[1], color[2]);
				gl.glVertex3d(e, i, floor);
				gl.glVertex3d(e, i, middle);
				gl.glVertex3d(f, i, middle);
				gl.glVertex3d(f, i, floor);

				color = ColorPalette.mother();
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
		} else {
			color = ColorPalette.deadMother();
			gl.glColor3f(color[0], color[1], color[2]);

			gl.glBegin(GL_QUADS);

			if (myWorld.getLandscape().DISPLAY_OBJECTS) {

				gl.glVertex3d(e, i, floor);
				gl.glVertex3d(e, i, middle);
				gl.glVertex3d(e, j, middle);
				gl.glVertex3d(e, j, floor);

				color = ColorPalette.deadMother2();
				gl.glColor3f(color[0], color[1], color[2]);
				gl.glVertex3d(e, j, floor);
				gl.glVertex3d(e, j, middle);
				gl.glVertex3d(f, j, middle);
				gl.glVertex3d(f, j, floor);

				color = ColorPalette.deadMother();
				gl.glColor3f(color[0], color[1], color[2]);
				gl.glVertex3d(f, i, floor);
				gl.glVertex3d(f, i, middle);
				gl.glVertex3d(f, j, middle);
				gl.glVertex3d(f, j, floor);

				color = ColorPalette.deadMother2();
				gl.glColor3f(color[0], color[1], color[2]);
				gl.glVertex3d(e, i, floor);
				gl.glVertex3d(e, i, middle);
				gl.glVertex3d(f, i, middle);
				gl.glVertex3d(f, i, floor);

				color = ColorPalette.deadMother();
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
		}
	}

	public void startFollowing() {
		isFollowing = true;
	}

	public void stopFollowing() {
		isFollowing = false;
	}
}
