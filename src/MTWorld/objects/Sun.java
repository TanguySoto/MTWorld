/**
 * -----------------
 * Projet 2I013 : Simulation de vie artificielle UPMC
 * 2014/2015
 * 
 * @author Tanguy SOTO, Malcolm Auffray
 * ----------------
 *  Soleil : Sphere avec rayons sortant
 */

package MTWorld.objects;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import MTWorld.worlds.World;
import static javax.media.opengl.GL.*; // GL constants
import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_MODELVIEW;

public class Sun extends UniqueObject {

	private int n = 0; // Nb de rayons
	private Ray[] liste;
	private float radius = 2.5f; // Rayon de la sph�re

	private double maxHeight; // Hauteur maximale en plein jours
	private double currentHeight = 0;

	private double angle = 0;

	public Sun(int x, int y, World world) {
		super(x, y, world);
		liste = new Ray[n];
		for (int i = 0; i < liste.length; i++) {
			liste[i] = new Ray();
		}
		double tailleMap = world.getWidth() * world.getLandscape().getSize()
				+ 4 * radius;
		double r = tailleMap / 2;
		maxHeight = r;
	}

	// Affiche un rayon sortant de la sph�re
	private class Ray {

		private float z;
		private float phi;
		private float length;
		private float theta;

		public Ray() {
			z = (float) (Math.random() * (2 * radius) - radius);
			phi = (float) (Math.random() * (Math.PI * 2));
			length = (float) (Math.random() * (1.2 - 1.15) + 1.15);
			theta = (float) Math.asin(z / radius);
		}

		public void draw(GL2 gl) {
			if (gl == null) {
				System.err.println("Poil.draw : GL2 object is null\n");
				return;
			}

			//float off = (noise(System.currentTimeMillis() * 0.0005, Math.sin(phi)) - 0.5) * 0.3;
			//float offb = (noise(System.currentTimeMillis() * 0.0007, Math.sin(z) * 0.01) - 0.5) * 0.3;

			float thetaff = theta;// + off;
			float phff = phi;// + offb;
			float x = (float) (radius * Math.cos(theta) * Math.cos(phi));
			float y = (float) (radius * Math.cos(theta) * Math.sin(phi));
			float z = (float) (radius * Math.sin(theta));

			float xo = (float) (radius * Math.cos(thetaff) * Math.cos(phff));
			float yo = (float) (radius * Math.cos(thetaff) * Math.sin(phff));
			float zo = (float) (radius * Math.sin(thetaff));

			float xb = xo * length;
			float yb = yo * length;
			float zb = zo * length;

			gl.glBegin(GL_LINES);
			gl.glColor3f(1f, 0.7f, 0f);
			gl.glVertex3f(x, y, z);
			gl.glColor3f(1f, 0.7f, 0.4f);
			gl.glVertex3f(xb, yb, zb);
			gl.glEnd();
		}
	}

	@Override
	public void displayUniqueObject(World myWorld, GLU glu, GL2 gl,
			double height, double width) {
		if (gl == null || glu == null) {
			System.err.println("Sun.draw : GL2 or GLU object is null\n");
			return;
		}

		// Fait bouger le soleil
		angle += Math.PI / world.getDayDuration();

		float dayMoment = (float) ((angle % (2 * Math.PI)) / (2 * Math.PI));
		dayMoment = (float) Math.round(dayMoment * 100) / 100; // Aroundit � 2 d�cimale
		world.setDayMoment(dayMoment);

		// Rayon de l'orbite
		double tailleMap = world.getWidth() * width + 4 * radius;
		double r = tailleMap / 2;

		// Nouvelle coordonn�es
		double xM = r * Math.cos(angle);
		double zM = r * Math.sin(angle);

		currentHeight = zM;

		gl.glMatrixMode(GL_MODELVIEW);
		gl.glTranslated(xM + r - 2 * radius, y, zM);

		world.getLandscape().sunTexture.bind(gl);

		if (myWorld.getLandscape().DISPLAY_OBJECTS) {
			gl.glEnable(GL_TEXTURE_2D);
			GLUquadric quad = glu.gluNewQuadric();
			glu.gluQuadricTexture(quad, true);
			gl.glColor3f(1f, 0.8f, 0f);
			glu.gluSphere(quad, radius * 1.0, 15, 15);
			gl.glDisable(GL_TEXTURE_2D);

			for (int i = 0; i < liste.length; i++) {
				liste[i].draw(gl);
			}
		}
		gl.glLoadIdentity();

	}

	public double getMaxHeight() {
		return maxHeight;
	}

	public double getCurrentHeight() {
		return currentHeight;
	}
}
