/**
 * -----------------
 * Projet 2I013 : Simulation de vie artificielle UPMC
 * 2014/2015
 * 
 * @author Tanguy SOTO
 *  ----------------
 * Affiche un arbre en 3D selon son etat
 */

package MTWorld.objects;

import MTWorld.graphics.ColorPalette;
import MTWorld.worlds.*;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

public class GrammarTree extends Agent {

	private int dx;
	private int dy;
	private float[] color;

	private double a;
	private double b;
	private double c;
	private double d;

	private double floor;

	// Anciennes versions
	private double stepx = 0.25;
	private double stepy = 0.35;

	// Version 2-3
	public double angle = 35;
	private double delta = 0.42;
	public double ratio = 0.66;

	private int maxLevel = 6;
	private int nbBranch;
	private int num;

	private int[] fruits; // emplacement des fruits dans l'arbre
	private int[] random; // emplacement des variations d'angle
	
	private int nbFruitsMax=10; // Nombre maximal de fruit déposé par l'arbre

	public GrammarTree(int __x, int __y, World __world) {
		super(__x, __y, __world);

		for (int i = 0; i < maxLevel; i++) {
			nbBranch += Math.pow(3, i);
		}

		fruits = new int[nbBranch + 1];
		for (int i = 0; i < nbBranch; i++) {
			if (Math.random() < 0.07) {
				fruits[i] = 1;
			}
		}

		random = new int[nbBranch + 1];
		for (int i = 2; i < nbBranch; i++) {
			if (Math.random() < 0.4) {
				if (Math.random() < 0.3) {
					random[i] = -1;
				} else {
					random[i] = 1;
				}
			}
		}
	}

	@Override
	public void step(int index) {
		if(((MTWorld)world).nbFruit<nbFruitsMax) { // ajoute un fruit
			for (int i = 0; i < 1; i++) {
				int x = (int) (Math.random() * (world.getWidth()));
				int y = (int) (Math.random() * (world.getHeight()));
				
				int state= world.getCellValue(x, y);
				if (state!=5 && state!=0 && state !=9 &&state !=8) { // sur vide, herbe, sable , neige
					i--;
				} else {
					world.addObject(new Fruits(x,y,world));
				}
			}			
		}

	}

	@Override
	public void displayObjectAt(World myWorld, GL2 gl, GLU glu, double height,
			double width) {
		dx = myWorld.getWidth() + 1;
		dy = myWorld.getHeight() + 1;

		a = myWorld.getLandscape().getLandscape()[x][y];
		b = myWorld.getLandscape().getLandscape()[x][(y + 1) % dy];
		c = myWorld.getLandscape().getLandscape()[(x + 1) % dx][y];
		d = myWorld.getLandscape().getLandscape()[(x + 1) % dx][(y + 1) % dy];
	
		num = 0;
		if (myWorld.getLandscape().DISPLAY_OBJECTS) { // grammar tree
			floor = Math.min(Math.min(a, b), Math.min(c, d));
			floor = floor * myWorld.getLandscape().getHeightFactor() * height;
			
			gl.glTranslated(x * width + width / 2, y * height + height / 2, floor);
			
			drawBranch3(gl, glu, height * 5, 1);
		} else { // juste une cercle sur le sol
			floor = Math.max(Math.max(a, b), Math.max(c, d));
			floor = floor * myWorld.getLandscape().getHeightFactor() * height;
			
			gl.glTranslated(x * width + width / 2, y * height + height / 2, floor);
			
			color = ColorPalette.wood();
			gl.glColor3f(color[0], color[1], color[2]);	
			
			GLUquadric quad = glu.gluNewQuadric();
			double radius = height * 5 / 15;
			glu.gluDisk(quad, 0, radius, 8, 1); 
		}
		gl.glLoadIdentity();
	}

	// Dessin un arbre recursivement (version 1 : bricolage)
	private void drawBranch(GL2 gl, double x, double y, double z, double x1,
			double y1, double z1, double length, int level) {

		// Arrete la recursion
		if (level > 5) {
			return;
		}

		if (level < 2) {
			color = ColorPalette.wood();
			gl.glColor3f(color[0], color[1], color[2]);
		} else {
			color = ColorPalette.tree2();
			gl.glColor3f(color[0], color[1], color[2]);
		}

		// Dessin de la branche courante
		gl.glLineWidth(6f - level);
		gl.glBegin(GL.GL_LINES);
		gl.glVertex3d(x, y, z);
		gl.glVertex3d(x1, y1, z1);
		gl.glEnd();

		// Dessin des autres
		drawBranch(gl, x1, y1, z1, x1 + (level + 1) * length * stepx, y1, z1
				+ length * stepy, length * stepy, level + 1);

		drawBranch(gl, x1, y1, z1, x1 - (level + 1) * length * stepx, y1
				- (level + 1) * length * stepx, z1 + length * stepy, length
				* stepy, level + 1);

		drawBranch(gl, x1, y1, z1, x1 - (level + 1) * length * stepx, y1
				+ (level + 1) * length * stepx, z1 + length * stepy, length
				* stepy, level + 1);
	}

	/*
	 * Dessin un arbre recursivement (version 2) R�gle de la grammaire : | ->
	 * \|/ selon un angle donne
	 */
	private void drawBranch2(GL2 gl, double length, int level) {

		// Arrete la recursion
		if (level > 6) {
			return;
		}

		// Choix couleur

		color = ColorPalette.wood();
		gl.glColor3f(color[0], color[1], color[2]);

		// Epaisseur
		gl.glLineWidth(8 - level);
		if (level == 6) {
			gl.glLineWidth(5);
			color = ColorPalette.tree2();
			gl.glColor3f(color[0], color[1], color[2]);
		}

		/* ========== Dessine la branch courante =========== */
		gl.glBegin(GL.GL_LINES);
		gl.glVertex3d(0, 0, 0);
		gl.glVertex3d(0, 0, length);
		gl.glEnd();

		/* ========== Dessine les sous branches =========== */
		gl.glTranslated(0, 0, length);
		length *= ratio;

		// 1 -
		gl.glPushMatrix(); // Conserve l'�tat courant
		gl.glRotated(angle, 0, 1, 0); // Tourne
		drawBranch2(gl, length, level + 1); // Dessine
		gl.glPopMatrix(); // Remet � l'�tat pr�cedent

		// 2 -
		gl.glPushMatrix(); // Conserve l'�tat courant
		gl.glRotated(120, 0, 0, 1);
		gl.glRotated(angle, 0, 1, 0); // Tourne
		drawBranch2(gl, length, level + 1); // Dessine
		gl.glPopMatrix(); // Remet � l'�tat pr�cedent

		// 3 -
		gl.glPushMatrix(); // Conserve l'�tat courant
		gl.glRotated(240, 0, 0, 1);
		gl.glRotated(angle, 0, 1, 0); // Tourne
		drawBranch2(gl, length, level + 1); // Dessine
		gl.glPopMatrix(); // Remet � l'�tat pr�cedent
	}

	/*
	 * Dessin un arbre recursivement avec des cylindres (version 3) Regle de la
	 * grammaire : | -> \|/ selon un angle donne
	 */
	private void drawBranch3(GL2 gl, GLU glu, double length, int level) {

		// Arrete la recursion
		if (level > maxLevel) {
			return;
		}

		// Choix couleur
		color = ColorPalette.wood();
		gl.glColor3f(color[0], color[1], color[2]);

		// Epaisseur
		double radius1 = length / (15 + level);
		double radius2 = (length * ratio) / (15 + level);

		// Feuilles
		if (level == maxLevel) {
			color = ColorPalette.tree2();
			gl.glColor3f(color[0], color[1], color[2]);
			radius1 = world.getLandscape().getSize() / 17;
			radius2 = radius1;
		}

		/* ========== Dessine la branch courante =========== */
		num++;

		// Variations	
		if (random[num] != 0 && level != maxLevel) {
			length += delta * length * random[num];
		}

		GLUquadric quad = glu.gluNewQuadric();
		glu.gluCylinder(quad, radius1, radius2, length, 6, 1);

		// Fruits	
		if (fruits[num] == 1 && level > maxLevel / 4) {
			color = ColorPalette.fruits();
			gl.glColor3f(color[0], color[1], color[2]);
			glu.gluSphere(quad, world.getLandscape().getSize() / 8, 3, 3);
		}

		/* ========== Dessine les sous branches =========== */
		gl.glTranslated(0, 0, length);

		// Annule la variation
		if (random[num] != 0 && level != maxLevel) {
			length -= delta * length * random[num];
		}

		length *= ratio;

		// 1 -
		gl.glPushMatrix(); // Conserve l'etat courant
		gl.glRotated(angle, 0, 1, 0); // Tourne
		drawBranch3(gl, glu, length, level + 1); // Dessine
		gl.glPopMatrix(); // Remet a l'etat precedent

		// 2 -
		gl.glPushMatrix(); // Conserve l'etat courant
		gl.glRotated(120, 0, 0, 1);
		gl.glRotated(angle, 0, 1, 0); // Tourne
		drawBranch3(gl, glu, length, level + 1); // Dessine
		gl.glPopMatrix(); // Remet a l'etat precedent

		// 3 -
		gl.glPushMatrix(); // Conserve l'etat courant
		gl.glRotated(240, 0, 0, 1);
		gl.glRotated(angle, 0, 1, 0); // Tourne
		drawBranch3(gl, glu, length, level + 1); // Dessine
		gl.glPopMatrix(); // Remet a l'etat precedent
	}
}
