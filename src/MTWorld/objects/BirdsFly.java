/**
 * -----------------
 * Projet 2I013 : Simulation de vie artificielle UPMC
 * 2014/2015
 * 
 * @author Tanguy SOTO
 *  ----------------
 * Vol d'oiseaux en 3 dimensions.
 */

package MTWorld.objects;

import static javax.media.opengl.GL2.GL_QUADS;

import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import MTWorld.graphics.ColorPalette;
import MTWorld.worlds.World;

public class BirdsFly extends Agent {

	// Contient tous les oiseaux
	private ArrayList<Bird> birds;

	public BirdsFly(World world) {
		// Position fictive
		super(-1,-1,world);
		birds = new ArrayList<Bird>();
	}

	public void run() {

	}

	// Ajoute l'oiseaux � l'ensemble
	public void addBoid(Bird b) {
		birds.add(b);
	}

	public void displayObjectAt(World myWorld, GL2 gl,GLU glu, double height, double width) {
		
		if(!myWorld.getLandscape().DISPLAY_OBJECTS) { return; }
		
		gl.glBegin(GL_QUADS);
		
		float[] color = ColorPalette.bird();
		gl.glColor3f(color[0], color[1], color[2]);
		
		Bird b;
		for (int i = 0; i < birds.size(); i++) {
			b = birds.get(i);
			b.displayObjectAt(myWorld, gl, height/2, width/2);
		}
		
		gl.glEnd();
	}

	public ArrayList<Bird> getBirds() {
		return birds;
	}

	@Override
	public void step(int index) {
		// Chaque oiseaux a besoin de connaitre les caract�ristique des autres
		Bird b;
		for (int i = 0; i < birds.size(); i++) {
			b = birds.get(i);
			b.run(birds);
		}
	}
}
