/**
 * -----------------
 * Projet 2I013 : Simulation de vie artificielle UPMC
 * 2014/2015
 * 
 * @author Tanguy SOTO
 *  ----------------
 * Trace laissée par la mère lorsqu'elle cherche un chemin vers un fruit
 */

package MTWorld.objects;

import static javax.media.opengl.GL2.GL_QUADS;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import MTWorld.graphics.ColorPalette;
import MTWorld.worlds.World;

public class MotherTrace extends UniqueObject{
	
	private int age=0;
	private int lifeTime = 1500;

	public MotherTrace(int __x, int __y, World __world) {
		super(__x, __y, __world);
		
	}

	@Override
	public void displayUniqueObject(World myWorld, GLU glu, GL2 gl,
			double height, double width) {
		age++;
		
		
		if(age > lifeTime) {
			world.removeObject(this);
			return;
		}
		
		// Evite la repetition des calculs pour chaque vertex
					double e = x * width + 2*width / 5;
					double f = x * width + 3 * width / 5;

					double i = y * height + 2*height / 5;
					double j = y * height + 3 * height / 5;

					double floor = myWorld.getCellHeight(x, y);// Math.min(Math.min(a,
																// b), Math.min(c, d));
					double middle = floor * myWorld.getLandscape().getHeightFactor()
							* height + height * 0.2f;

					floor = floor * myWorld.getLandscape().getHeightFactor() * height;
					
					float[] color = ColorPalette.mother();
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
		
	}

}
