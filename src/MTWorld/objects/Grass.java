/**
 * -----------------
 * Projet 2I013 : Simulation de vie artificielle UPMC
 * 2014/2015
 * 
 * @author Tanguy SOTO
 *  ----------------
 * Texture herbe
 */

package MTWorld.objects;

import MTWorld.graphics.ColorPalette;
import MTWorld.worlds.*;

import javax.media.opengl.GL2;

public class Grass extends CommonObject {

	// Evite la redeclaration de variables pour chaque arbres
	private static float[] color;

	private static double e;
	private static double f;

	private static double i;
	private static double j;

	private static double floor;
	private static double middle;

	public static void displayObjectAt(World myWorld, GL2 gl, int cellState, int x, int y,
			double height, double width) {

		switch (cellState) {

		case 5: 

			// Evite la repetition des calculs pour chaque vertex
			e = x * width + width / 4;
			f = x * width + 3 * width / 4;

			i = y * height + height / 4;
			j = y * height + 3 * height / 4;

			floor = myWorld.getCellHeight(x, y);//Math.min(Math.min(a, b), Math.min(c, d));
			middle = floor * myWorld.getLandscape().getHeightFactor() * height + height*0.1f;

			floor = floor * myWorld.getLandscape().getHeightFactor() * height;

			color = ColorPalette.grass();
			gl.glColor3f(color[0], color[1], color[2]);

			// Cendre face 1
			gl.glVertex3d(e, i, floor);
			gl.glVertex3d(e, i, middle);
			gl.glVertex3d(e, j, middle);
			gl.glVertex3d(e, j, floor);
			
			color = ColorPalette.grass2();
			gl.glColor3f(color[0], color[1], color[2]);
			
			// Cendre face 2
			gl.glVertex3d(e, j, floor);
			gl.glVertex3d(e, j, middle);
			gl.glVertex3d(f, j, middle);
			gl.glVertex3d(f, j, floor);
			
			color = ColorPalette.grass();
			gl.glColor3f(color[0], color[1], color[2]);

			// Cendre face 3
			gl.glVertex3d(f, i, floor);
			gl.glVertex3d(f, i, middle);
			gl.glVertex3d(f, j, middle);
			gl.glVertex3d(f, j, floor);
			
			color = ColorPalette.grass2();
			gl.glColor3f(color[0], color[1], color[2]);

			// Cendre face 4
			gl.glVertex3d(e, i, floor);
			gl.glVertex3d(e, i, middle);
			gl.glVertex3d(f, i, middle);
			gl.glVertex3d(f, i, floor);

			// Dessus grass
			gl.glVertex3d(e, i, middle);
			gl.glVertex3d(f, i, middle);
			gl.glVertex3d(f, j, middle);
			gl.glVertex3d(e, j, middle);

			break;

		default:
			break;
		}

	}
}
