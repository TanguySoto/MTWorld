/**
 * -----------------
 * Projet 2I013 : Simulation de vie artificielle UPMC
 * 2014/2015
 * 
 * @author Tanguy SOTO, Malcolm Auffray
 * ----------------
 *  Montre la localisation du joueur
 */

package MTWorld.objects;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import static javax.media.opengl.GL.GL_TEXTURE_2D;
import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_MODELVIEW;
import MTWorld.worlds.MTWorld;
import MTWorld.worlds.World;

public class Player extends UniqueObject {

	private float radius; // Rayon
	private float size;	  // Dimensions d'une case
	private float rx = 0; // Angle de rotation

	public float xR, yR, zR; // Vrai emplacement

	public int action=1; // Action sur l'environnement

	public Player(int x, int y, World world) {
		super(x, y, world);
		size = world.getLandscape().getSize();
		radius = size * 0.4f;
		xR = x * size;
		yR = y * size;
	}

	@Override
	public void displayUniqueObject(World myWorld, GLU glu, GL2 gl, double height, double width) {
		if (gl == null || glu == null) {
			System.err.println("Player.draw : GL2 or GLU object is null\n");
			return;
		}

		y = (int) (yR / size);
		x = (int) (xR / size);

		zR = (float) (world.getCellHeight(x, y) * size * world.getLandscape().getHeightFactor() + radius * 2);

		gl.glMatrixMode(GL_MODELVIEW);
		gl.glTranslated(xR, yR, zR);
		
		world.getLandscape().waterTexture.bind(gl);

		if (myWorld.getLandscape().DISPLAY_OBJECTS) { // Sph�re textur�e en rotation
			gl.glRotatef(rx, 0, 1, 1);
			
			gl.glEnable(GL_TEXTURE_2D);
			GLUquadric quad = glu.gluNewQuadric();
			glu.gluQuadricTexture(quad, true);
			gl.glColor3f(1, 0f,0);
			glu.gluQuadricTexture(quad, true);
			glu.gluSphere(quad, radius, 9, 9);
			gl.glDisable(GL_TEXTURE_2D);
			
			rx += 10f;
		}
		else { // Simple cercle
			GLUquadric quad = glu.gluNewQuadric();
			gl.glColor3f(1, 0.2f,0);
			glu.gluDisk(quad, 0, radius, 9, 1);
		}

		gl.glLoadIdentity();

	}

	public void action() {
		world.setCellsWaterValues(x, y, 0);
		world.setCellsTempValues(x, y, 0);
		switch (action) {
		case 0:
			world.setCellOldValue(x, y, 0);
			break;
		case 1:
			world.setCellOldValue(x, y, 1);
			break;
		case 2:
			world.setCellOldValue(x, y, 2);
			world.setCellsTempValues(x, y, 100);
			break;
		case 3:
			world.setCellOldValue(x, y, 3);
			world.setCellsTempValues(x, y, 100);
			break;
		case 4:
			world.setCellOldValue(x, y, 4);
			world.setCellsWaterValues(x, y, world.getCellsWaterValues(x, y) + 1);
			break;
		case 5:
			world.setCellOldValue(x, y, 5);
			break;
		case 6:
			world.setCellOldValue(x, y, 6);
			world.setCellsTempValues(x, y, 50);
			break;
		case 7:
			world.setCellOldValue(x, y, 7);
			world.setCellsTempValues(x, y, 50);;
			break;
		case 8:
			world.setCellOldValue(x, y, 8);
			world.setCellsTempValues(x, y, -15);
			break;
		case 9:
			world.setCellOldValue(x, y, 9);
			break;
		case 10:
			break;
		case 11:
			world.setCellOldValue(x, y, 11);
			world.setCellsTempValues(x, y, 200);
			break;
		case 12:
			world.setCellOldValue(x, y, 12);
			world.setCellsTempValues(x, y, 75);
			break;
		case 13:
			((MTWorld) world).getMTWorldCA().majHeight(x, y);
			break;
		case 14:
			((MTWorld) world).getMTWorldCA().majHeight2(x, y);
			break;
		default:
			break;
		}
	}
	
	// Affiche l'objet courrant dans le terminal
	public void displayCurrentOject() {
		switch (action) {
		case 0:
			System.out.println("Current player's object : empty ground");
			break;
		case 1:
			System.out.println("Current player's object : tree");
			break;
		case 2:
			System.out.println("Current player's object : burning tree");
			break;
		case 3:
			System.out.println("Current player's object : burnt tree");
			break;
		case 4:
			System.out.println("Current player's object : water");
			break;
		case 5:
			System.out.println("Current player's object : grass");
			break;
		case 6:
			System.out.println("Current player's object : burning grass");
			break;
		case 7:
			System.out.println("Current player's object : burnt grass");
			break;
		case 8:
			System.out.println("Current player's object : snow");
			break;
		case 9:
			System.out.println("Current player's object : sand");
			break;
		case 10:
			System.out.println("Current player's object : UNDEFINED");
			break;
		case 11:
			System.out.println("Current player's object : lava");
			break;
		case 12:
			System.out.println("Current player's object : volcanic rock");
			break;
		case 13:
			System.out.println("Current player's object : landscape up");
			break;
		case 14:
			System.out.println("Current player's object : landscape down");
			break;
		default:
			break;
		}
	}
}
