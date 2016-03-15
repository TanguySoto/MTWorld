/**
 * -----------------
 * Projet 2I013 : Simulation de vie artificielle UPMC
 * 2014/2015
 * 
 * @author Tanguy SOTO
 *  ----------------
 * - Oiseau se deplacement en vol groupe.
 * 	 La mise en oeuvre des 3 regles (cohesion,orientation,separation) est basee sur celle
 *   de Daniel Shiffman (http://natureofcode.com)
 * - Ajout de fonctions de deplacement d'un point A vers un point B
 */

package MTWorld.objects;

import java.util.ArrayList;

import javax.media.opengl.GL2;

import MTWorld.extern.PVector;
import MTWorld.worlds.World;

public class Bird {

	// Poids des regles
	public static float swt = 7.8f; // separation
	public static float awt = 4.0f; // orientation
	public static float cwt = 6.9f; // cohesion

	public static float maxspeed = 0.35f;
	private static float maxforce = 0.0125f;
	private int neighBordist = 4;

	private PVector loc;
	private PVector vel;
	private PVector acc;

	private int isEating = 0; // 0 -> pas de cible en vue, 1-> se dirige vers la
								// cible, 2-> reviens dans le ciel
	private float xC, yC, zC; // coordonnees de la cible
	private Agent cadavre; 	 // Cible
	private PVector dir; // vecteur directeur de la trajectoire
	private float k = 0; // position sur la trajectoire vers la cible
	private float speed = 0.0055f;
	
	private int timeArrived; // Moment d'arrivee des oiseau sur le cadavre
	private int timeToStay = 100; // Duree du repas
	
	

	private float limiteZ;

	private World world;

	public Bird(float x, float y, float z, World world) {
		this.world = world;

		acc = new PVector(0, 0, 0);
		vel = new PVector((float) Math.random() * (3) - 1,
				(float) Math.random() * (3) - 1,
				(float) Math.random() * (3) - 1);
		loc = new PVector(x, y, z);
		limiteZ = z;
		;
	}

	// Fait bouge l'oiseau en fonction des autres ou de son action
	public void run(ArrayList<Bird> boids) {
		moveToAction(isEating, boids); // Plonge vers cible
	}

	// Se deplace en fonction d'une action
	public void moveToAction(int action, ArrayList<Bird> boids) {
		switch (action) {
		case 0:
			flock(boids); // mise en oeuvre des 3 regles
			update();
			borders();
			break;
		case 1:
			loc.x = k * dir.x + (dir.x + xC);
			loc.y = k * dir.y + (dir.y + yC);
			loc.z = k * dir.z + (dir.z + zC);
			k -= speed;
			if (k < -1) {
				k = -1;
				timeArrived = world.getIteration();
				isEating = 2; // revient
			}
			break;
		case 2:
			dir.normalize();
			dir.mult(maxspeed/10);
			dir.mult(awt);
			applyForce(dir);
			flock(boids); // mise en oeuvre des 3 regles
			update();
			
			if ((world.getIteration() - timeArrived) == timeToStay) { // "mange"
				world.removeAgent(cadavre);
			}

			k += speed*0.7f;

			if (k > 0) {
				k = 0;
				isEating = 0;
			}
			break;
		default:
			break;
		}
	}

	public int getIsEating() {
		return isEating;
	}

	// L'oiseau a repere un cadavre en (x,y)
	public void setEatingCible(int x, int y,Agent cible) {
		isEating = 1;
		cadavre=cible;

		// Point cible
		xC = x * world.getLandscape().getSize();
		yC = y * world.getLandscape().getSize();
		zC = (float) world.getCellHeight(x, y)
				* world.getLandscape().getHeightFactor()
				* world.getLandscape().getSize();

		// Vecteur directeur de la trajectoire
		dir = new PVector(loc.x - xC, loc.y - yC, loc.z - zC);

	}

	// Augmente l'acceleration de la 'force' donnee
	private void applyForce(PVector force) {
		acc.add(force);
	}

	// L'acceleration est calculee selon les 3 regles
	private void flock(ArrayList<Bird> boids) {
		PVector sep = separate(boids); // s�paration
		PVector ali = align(boids); // orientation
		PVector coh = cohesion(boids); // coh�sion

		// Mutliplication par des valeurs arbitraires (permet de jouer sur
		// l'importance de chaque r�gle)
		sep.mult(swt);
		ali.mult(awt);
		coh.mult(cwt);

		// Application des 3 vecteurs d�coulant des r�gles �
		// l'acceleration
		applyForce(sep);
		applyForce(ali);
		applyForce(coh);
	}

	// Mise a jours de la position
	private void update() {
		// Vitesse = vitesse + acceleration
		vel.add(acc);
		vel.limit(maxspeed);

		if ((world.getIteration() - timeArrived) < timeToStay && isEating == 2) { // "mange"
																				
			// ralentit
			vel.div(10);
		}

		// Position = position + vitesse
		loc.add(vel);

		// Acceleration remise a zero
		acc.mult(0);
	}

	// A method that calculates and applies a steering force towards a target :
	// STEER = DESIRED MINUS VELOCITY
	private PVector seek(PVector target) {
		// A vector pointing from the location to the target
		PVector desired = PVector.sub(target, loc);

		// Normalize desired and scale to maximum speed
		desired.normalize();
		desired.mult(maxspeed);

		// Steering = Desired minus Velocity
		PVector steer = PVector.sub(desired, vel);

		// Limit to maximum steering force
		steer.limit(maxforce);

		return steer;
	}

	// Affiche un simple "V", TODO -> orientation correcte
	public void displayObjectAt(World myWorld, GL2 gl, double height,
			double width) {

		gl.glVertex3d(loc.x, loc.y, loc.z);
		gl.glVertex3d(loc.x + width, loc.y, loc.z);
		gl.glVertex3d(loc.x + width, loc.y + height, loc.z + height * 0.6f);
		gl.glVertex3d(loc.x, loc.y + height, loc.z + height * 0.6f);

		gl.glVertex3d(loc.x, loc.y, loc.z);
		gl.glVertex3d(loc.x + width, loc.y, loc.z);
		gl.glVertex3d(loc.x + width, loc.y - height, loc.z + height * 0.6f);
		gl.glVertex3d(loc.x, loc.y - height, loc.z + height * 0.6f);

	}

	// Limite le vol dans la map et alentours
	private void borders() {
		if (loc.z > limiteZ) {
			loc.z = limiteZ;
		}
		if (loc.z < limiteZ * 0.8) {
			loc.z = limiteZ * 0.8f;
		}

		if (loc.x < -2)
			loc.x = world.getWidth() * world.getLandscape().getSize() + 2;
		if (loc.y < -2)
			loc.y = world.getHeight() * world.getLandscape().getSize() + 2;

		if (loc.y > world.getHeight() * world.getLandscape().getSize() + 2)
			loc.y = -2;

		if (loc.x > world.getWidth() * world.getLandscape().getSize() + 2)
			loc.x = -2;

	}

	// 1 - Calcule le vecteur direction moyen, oppose aux voisins
	// 2 - Calcule le vecteur force � appliquer pour fuir
	private PVector separate(ArrayList<Bird> boids) {
		float desiredseparation = world.getLandscape().getSize() * neighBordist;

		PVector steer = new PVector(0, 0);
		int count = 0;

		// Si voisin trop proche
		for (Bird other : boids) {
			float d = PVector.dist(loc, other.loc);
			if ((d > 0) && (d < desiredseparation)) {
				PVector diff = PVector.sub(loc, other.loc);
				diff.normalize();
				diff.div(d); // plus voisin est loin, moins cela influence
				steer.add(diff);
				count++;
			}
		}
		// Average -- divide by how many
		if (count > 0) {
			steer.div((float) count);
			// Implement Reynolds: Steering = Desired - Velocity
			steer.normalize();
			steer.mult(maxspeed);
			steer.sub(vel);
			steer.limit(maxforce);
		}
		return steer;
	}

	// 1 - Calcule du vecteur vitesse moyen des voisins
	// 2 - Calcule le vecteur force � appliquer pour s'orienter de m�me
	private PVector align(ArrayList<Bird> boids) {
		float neighbordist = world.getLandscape().getSize() * neighBordist * 2f;

		PVector steer = new PVector();
		int count = 0;

		// Moyenne des vitesse des voisins
		for (Bird other : boids) {
			float d = PVector.dist(loc, other.loc);
			if ((d > 0) && (d < neighbordist)) {
				steer.add(other.vel);
				count++;
			}
		}
		if (count > 0) {
			steer.div((float) count);
			// Implement Reynolds: force � appliqu� = orientation voulue -
			// vitesse
			steer.normalize();
			steer.mult(maxspeed);
			steer.sub(vel);
			steer.limit(maxforce);
		}
		return steer;
	}

	// 1 - Calcule de la 'postion moyenne' des voisins proches
	// 2 - Calcule le vecteur force � appliquer pour y parvenir
	private PVector cohesion(ArrayList<Bird> boids) {
		float neighborDist = world.getLandscape().getSize() * neighBordist * 2f;

		PVector sum = new PVector(0, 0, 0);
		int count = 0;

		// Moyenne des distance a l'oiseau courant
		for (Bird other : boids) {
			float d = PVector.dist(loc, other.loc);
			if ((d > 0) && (d < neighborDist)) {
				sum.add(other.loc);
				count++;
			}
		}

		// Renvoie le vecteur � appliquer
		if (count > 0) {
			sum.div((float) count);
			return seek(sum);
		}
		return sum;
	}

}
