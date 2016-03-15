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

import javax.media.opengl.GL2;

public class Tree extends CommonObject {

	// Evite la redeclaration de variables pour chaque arbres
	private static int dx;
	private static int dy;
	private static float[] color;

	private static double a;
	private static double b;
	private static double c;
	private static double d;

	private static double e;
	private static double f;
	private static double g;
	private static double h;

	private static double i;
	private static double j;
	private static double k;
	private static double l;

	private static double floor;
	private static double middle;
	private static double top;

	public static void displayObjectAt(World myWorld, GL2 gl, int cellState, int x, int y,
			double height, double width) {		

		dx = myWorld.getWidth() + 1;
		dy = myWorld.getHeight() + 1;

		a = myWorld.getLandscape().getLandscape()[x][y];
		b = myWorld.getLandscape().getLandscape()[x][(y + 1) % dy];
		c = myWorld.getLandscape().getLandscape()[(x + 1) % dx][y];
		d = myWorld.getLandscape().getLandscape()[(x + 1) % dx][(y + 1) % dy];

		switch (cellState) {

		case 1: // Arbre vivant

			// Evite la repetition des calculs pour chaque vertex
			e = x * width + width / 4;
			f = x * width + 3 * width / 4;
			g = x * width + width / 9;
			h = x * width + 8 * width / 9;

			i = y * height + height / 4;
			j = y * height + 3 * height / 4;
			k = y * height + height / 9;
			l = y * height + 8 * height / 9;
			
			//gl.glMatrixMode(GL_MODELVIEW);
			floor = Math.min(Math.min(a, b), Math.min(c, d));
			middle = floor * myWorld.getLandscape().getHeightFactor() * height + height;
			top = middle + height;

			floor = floor * myWorld.getLandscape().getHeightFactor() * height;

			color = ColorPalette.wood();
			gl.glColor3f(color[0], color[1], color[2]);

			// Tron face 1
			gl.glVertex3d(e, i, floor);
			gl.glVertex3d(e, i, middle);
			gl.glVertex3d(e, j, middle);
			gl.glVertex3d(e, j, floor);

			color = ColorPalette.wood2();
			gl.glColor3f(color[0], color[1], color[2]);

			// Tron face 2
			gl.glVertex3d(e, j, floor);
			gl.glVertex3d(e, j, middle);
			gl.glVertex3d(f, j, middle);
			gl.glVertex3d(f, j, floor);

			color = ColorPalette.wood();
			gl.glColor3f(color[0], color[1], color[2]);

			// Tron face 3
			gl.glVertex3d(f, i, floor);
			gl.glVertex3d(f, i, middle);
			gl.glVertex3d(f, j, middle);
			gl.glVertex3d(f, j, floor);

			color = ColorPalette.wood2();
			gl.glColor3f(color[0], color[1], color[2]);

			// Tron face 4
			gl.glVertex3d(e, i, floor);
			gl.glVertex3d(e, i, middle);
			gl.glVertex3d(f, i, middle);
			gl.glVertex3d(f, i, floor);

			color = ColorPalette.tree2();
			gl.glColor3f(color[0], color[1], color[2]);

			// feuillage face 1
			gl.glVertex3d(g, k, middle);
			gl.glVertex3d(g, k, top);
			gl.glVertex3d(g, l, top);
			gl.glVertex3d(g, l, middle);

			color = ColorPalette.tree();
			gl.glColor3f(color[0], color[1], color[2]);

			// feuillage face 2
			gl.glVertex3d(g, l, top);
			gl.glVertex3d(g, l, middle);
			gl.glVertex3d(h, l, middle);
			gl.glVertex3d(h, l, top);

			color = ColorPalette.tree2();
			gl.glColor3f(color[0], color[1], color[2]);

			// feuillage face 3
			gl.glVertex3d(h, k, middle);
			gl.glVertex3d(h, k, top);
			gl.glVertex3d(h, l, top);
			gl.glVertex3d(h, l, middle);

			color = ColorPalette.tree();
			gl.glColor3f(color[0], color[1], color[2]);

			// feuillage face 4
			gl.glVertex3d(g, k, top);
			gl.glVertex3d(g, k, middle);
			gl.glVertex3d(h, k, middle);
			gl.glVertex3d(h, k, top);

			// Dessus feuillage
			gl.glVertex3d(h, k, top);
			gl.glVertex3d(g, k, top);
			gl.glVertex3d(g, l, top);
			gl.glVertex3d(h, l, top);

			color = ColorPalette.tree2();
			gl.glColor3f(color[0], color[1], color[2]);

			// Dessous feuillage
			gl.glVertex3d(h, k, middle);
			gl.glVertex3d(g, k, middle);
			gl.glVertex3d(g, l, middle);
			gl.glVertex3d(h, l, middle);

			break;

		case 2: // Arbre en feu

			// Evite la repetition des calculs pour chaque vertex
			e = x * width + width / 4;
			f = x * width + 3 * width / 4;
			g = x * width + width / 9;
			h = x * width + 8 * width / 9;

			i = y * height + height / 4;
			j = y * height + 3 * height / 4;
			k = y * height + height / 9;
			l = y * height + 8 * height / 9;

			floor = Math.min(Math.min(a, b), Math.min(c, d));
			middle = floor * myWorld.getLandscape().getHeightFactor() * height + height;
			top = middle + height;

			floor = floor * myWorld.getLandscape().getHeightFactor() * height;

			color = ColorPalette.fire();
			gl.glColor3f(color[0], color[1], color[2]);

			// Tron face 1
			gl.glVertex3d(e, i, floor);
			gl.glVertex3d(e, i, middle);
			gl.glVertex3d(e, j, middle);
			gl.glVertex3d(e, j, floor);

			color = ColorPalette.fire();
			gl.glColor3f(color[0], color[1], color[2]);

			// Tron face 2
			gl.glVertex3d(e, j, floor);
			gl.glVertex3d(e, j, middle);
			gl.glVertex3d(f, j, middle);
			gl.glVertex3d(f, j, floor);

			color = ColorPalette.fire();
			gl.glColor3f(color[0], color[1], color[2]);

			// Tron face 3
			gl.glVertex3d(f, i, floor);
			gl.glVertex3d(f, i, middle);
			gl.glVertex3d(f, j, middle);
			gl.glVertex3d(f, j, floor);

			color = ColorPalette.fire();
			gl.glColor3f(color[0], color[1], color[2]);

			// Tron face 4
			gl.glVertex3d(e, i, floor);
			gl.glVertex3d(e, i, middle);
			gl.glVertex3d(f, i, middle);
			gl.glVertex3d(f, i, floor);

			color = ColorPalette.fire();
			gl.glColor3f(color[0], color[1], color[2]);

			// feuillage face 1
			gl.glVertex3d(g, k, middle);
			gl.glVertex3d(g, k, top);
			gl.glVertex3d(g, l, top);
			gl.glVertex3d(g, l, middle);

			color = ColorPalette.fire();
			gl.glColor3f(color[0], color[1], color[2]);

			// feuillage face 2
			gl.glVertex3d(g, l, top);
			gl.glVertex3d(g, l, middle);
			gl.glVertex3d(h, l, middle);
			gl.glVertex3d(h, l, top);

			color = ColorPalette.fire();
			gl.glColor3f(color[0], color[1], color[2]);

			// feuillage face 3
			gl.glVertex3d(h, k, middle);
			gl.glVertex3d(h, k, top);
			gl.glVertex3d(h, l, top);
			gl.glVertex3d(h, l, middle);

			color = ColorPalette.fire();
			gl.glColor3f(color[0], color[1], color[2]);

			// feuillage face 4
			gl.glVertex3d(g, k, top);
			gl.glVertex3d(g, k, middle);
			gl.glVertex3d(h, k, middle);
			gl.glVertex3d(h, k, top);

			// Dessus feuillage
			gl.glVertex3d(h, k, top);
			gl.glVertex3d(g, k, top);
			gl.glVertex3d(g, l, top);
			gl.glVertex3d(h, l, top);

			color = ColorPalette.fire();
			gl.glColor3f(color[0], color[1], color[2]);

			// Dessous feuillage
			gl.glVertex3d(h, k, middle);
			gl.glVertex3d(g, k, middle);
			gl.glVertex3d(g, l, middle);
			gl.glVertex3d(h, l, middle);

			break;

		case 3: // Cendre

			// Evite la repetition des calculs pour chaque vertex
			e = x * width + width / 4;
			f = x * width + 3 * width / 4;
			g = x * width + width / 9;
			h = x * width + 8 * width / 9;

			i = y * height + height / 4;
			j = y * height + 3 * height / 4;
			k = y * height + height / 9;
			l = y * height + 8 * height / 9;

			floor = Math.min(Math.min(a, b), Math.min(c, d));
			middle = floor * myWorld.getLandscape().getHeightFactor() * height + height*1.2f;

			floor = floor * myWorld.getLandscape().getHeightFactor() * height;

			color = ColorPalette.cendre();
			gl.glColor3f(color[0], color[1], color[2]);

			// Cendre face 1
			gl.glVertex3d(e, i, floor);
			gl.glVertex3d(e, i, middle);
			gl.glVertex3d(e, j, middle);
			gl.glVertex3d(e, j, floor);
			
			color = ColorPalette.cendre2();
			gl.glColor3f(color[0], color[1], color[2]);
			
			// Cendre face 2
			gl.glVertex3d(e, j, floor);
			gl.glVertex3d(e, j, middle);
			gl.glVertex3d(f, j, middle);
			gl.glVertex3d(f, j, floor);
			
			color = ColorPalette.cendre();
			gl.glColor3f(color[0], color[1], color[2]);

			// Cendre face 3
			gl.glVertex3d(f, i, floor);
			gl.glVertex3d(f, i, middle);
			gl.glVertex3d(f, j, middle);
			gl.glVertex3d(f, j, floor);
			
			color = ColorPalette.cendre2();
			gl.glColor3f(color[0], color[1], color[2]);

			// Cendre face 4
			gl.glVertex3d(e, i, floor);
			gl.glVertex3d(e, i, middle);
			gl.glVertex3d(f, i, middle);
			gl.glVertex3d(f, i, floor);

			// Dessus cendre
			gl.glVertex3d(e, i, middle);
			gl.glVertex3d(f, i, middle);
			gl.glVertex3d(f, j, middle);
			gl.glVertex3d(e, j, middle);

			break;

		default:
			break;
		}
	 gl.glLoadIdentity();
	}
}
