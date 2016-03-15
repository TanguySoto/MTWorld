/**
 * -----------------
 * Projet 2I013 : Simulation de vie artificielle UPMC
 * 2014/2015
 * 
 * @author Tanguy SOTO, Malcolm Auffray
 * ----------------
 *  Sphere
 */

package MTWorld.objects;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import MTWorld.worlds.World;
import static javax.media.opengl.GL.*; // GL constants
import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_MODELVIEW;

public class Moon extends UniqueObject {

	private float radius = 2.5f; // Rayon
	private float rx = 0; // Angle de rotation

	private double angle = -Math.PI;

	public Moon(int x, int y, World world) {
		super(x, y, world);
	}

	@Override
	public void displayUniqueObject(World myWorld, GLU glu, GL2 gl,
			double height, double width) {
		if (gl == null || glu == null) {
			System.err.println("Moon.draw : GL2 or GLU object is null\n");
			return;
		}

		angle += Math.PI / world.getDayDuration();

		double tailleMap = world.getWidth() * width + 4 * radius;
		double r = tailleMap / 2;

		double xM = r * Math.cos(angle);
		double zM = r * Math.sin(angle);

		gl.glMatrixMode(GL_MODELVIEW);
		gl.glTranslated(xM + r - 2 * radius, y, zM);
		gl.glRotatef(rx, 0, 0, 1);

		world.getLandscape().moonTexture.bind(gl);

		if (myWorld.getLandscape().DISPLAY_OBJECTS) {
			gl.glEnable(GL_TEXTURE_2D);
			GLUquadric quad = glu.gluNewQuadric();
			gl.glColor3f(0.75f, 0.75f, 0.75f);
			glu.gluQuadricTexture(quad, true);
			glu.gluSphere(quad, radius * 1.0, 15, 15);
			gl.glDisable(GL_TEXTURE_2D);
		}

		rx += 0.3;

		gl.glLoadIdentity();

	}
}
