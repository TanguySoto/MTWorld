/**
 * -----------------
 * Projet 2I013 : Simulation de vie artificielle UPMC
 * 2014/2015
 * 
 * @author Tanguy SOTO
 *  ----------------
 * Agent evoluant dans l'eau (independemment du reste du monde) afin de simuler l'evolution d'une population.
 */

package MTWorld.objects;

import static javax.media.opengl.GL2.GL_QUADS;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import MTWorld.graphics.ColorPalette;
import MTWorld.worlds.MTWorld;
import MTWorld.worlds.World;

public class Fish extends Agent {

	protected static int evolutionSpeed = 25;
	protected static int lifeTime=497;
	protected static int reproductionSpeed = 249;
	
	protected double pMutation = 0.05;	

	protected int age;
	protected double fitness;
	
	/* ==== Caracteristiques principaux ==== */
	protected int vision;
	protected int branchies;
	protected int tailleNageoir;
	protected int taille;

	public Fish(int __x, int __y, World __world) {
		super(__x, __y, __world);
		isAlive = true;
		age = 0;
		
		// Phenotype aleatoire
		vision=(int) (Math.random() * (100));
		branchies=(int) (Math.random() * (100));
		tailleNageoir=(int) (Math.random() * (100));
		taille=(int) (Math.random() * (100));
		
		fitness = calculFitness();
	}

	@Override
	public void step(int index) {
		age++;

		// Reproduction
		if (world.getIteration() % reproductionSpeed == 0 && isAlive && age != 1) {
			if(Math.random()<fitness) {
				boolean found=false;
				for(Agent a : world.getAgents()) {
					if(a instanceof Fish && Math.random()<0.1) {
						Fish f = reproduct((Fish)a);
						world.agentsMap[x][y].add(f);
						world.addAgent(f);
						found=true;
						break;
					}
				}
				if(!found) {
					Fish f = this.clone();
					world.agentsMap[x][y].add(f);
					world.addAgent(f);
				}
			}
		}
		
		// Mort
		if (age > lifeTime) {
			isAlive=false;
		}

		if (world.getIteration() % evolutionSpeed != 0 || !isAlive) {
			if(!isAlive)
			world.removeAgent(this);
			return;
		}

		// Deplacement aleatoire
		orient = (int) (Math.random() * (4));

		// Calcule de la case destination
		int newX = -1, newY = -1, state = -1;
		switch (orient) {
		case 0: // nord
			newX = x;
			newY = (y - 1 + world.getHeight()) % world.getHeight();
			state = world.getCellValue(newX, newY);
			break;
		case 1: // est
			newX = (x + 1 + world.getWidth()) % world.getWidth();
			newY = y;
			state = world.getCellValue(newX, newY);
			break;
		case 2: // sud
			newX = x;
			newY = (y + 1 + world.getHeight()) % world.getHeight();
			state = world.getCellValue(newX, newY);
			break;
		case 3: // ouest
			newX = (x - 1 + world.getWidth()) % world.getWidth();
			newY = y;
			state = world.getCellValue(newX, newY);
			break;
		}

		// Mise a jours selon regles de vie de l'agents
		if (state == 4) { // peut aller dans l'eau
			((MTWorld) world).agentsMap[x][y].remove(this);
			x = newX;
			y = newY;
			((MTWorld) world).agentsMap[x][y].add(this);
		}
	}

	public void displayObjectAt(World myWorld, GL2 gl, GLU glu, double height, double width) {

		switch (state) {

		case 0:

			// Evite la repetition des calculs pour chaque vertex
			double e = x * width + width / 4;
			double f = x * width + 3 * width / 4;

			double i = y * height + height / 4;
			double j = y * height + 3 * height / 4;

			double floor = myWorld.getCellHeight(x, y);
			
			floor = floor * myWorld.getLandscape().getHeightFactor() * height-height*0.01f;

			if (isAlive) {
				gl.glColor3d(1, 1-0.95*fitness, 1-0.95*fitness);
			} else { // cadavre
				float[] color = ColorPalette.deadFish();
				gl.glColor3f(color[0], color[1], color[2]);
			}

			gl.glBegin(GL_QUADS);

			gl.glVertex3d(e, i, floor);
			gl.glVertex3d(f, i, floor);
			gl.glVertex3d(f, j, floor);
			gl.glVertex3d(e, j, floor);

			gl.glEnd();

			break;

		default:
			break;
		}
	}
	
	// produit un poisson fils issu du croisement des 2 parents
	private Fish reproduct(Fish f) {
		Fish fils = new Fish(x, y, world);
		fils.branchies=(int) (Math.max(branchies,f.branchies)*0.85+Math.min(branchies,f.branchies)*0.15);
		fils.taille=(int) (Math.max(taille,f.taille)*0.85+Math.min(taille,f.taille)*0.15);
		fils.tailleNageoir=(int) (Math.max(tailleNageoir,f.tailleNageoir)*0.85+Math.min(tailleNageoir,f.tailleNageoir)*0.15);
		fils.vision=(int) (Math.max(vision,f.vision)*0.85+Math.min(vision,f.vision)*0.15);
		
		fils.fitness=fils.calculFitness();
		
		return fils;
	}
	
	public Fish clone(){
		Fish copy = new Fish(x, y, world);
		copy.branchies=branchies;
		copy.taille=taille;
		copy.tailleNageoir=tailleNageoir;
		copy.vision=vision;
		copy.fitness=copy.calculFitness();
		mutate(copy);
		
		return copy;
	}
	
	private void mutate(Fish s) {
		if(Math.random() <pMutation) {
			s.branchies+=(int)(Math.random()*20-10);
			if(s.branchies>100) {
				s.branchies=100;
			}
			if(s.branchies<0) {
				s.branchies=0;
			}
			
			s.vision+=(int)(Math.random()*20-10);
			if(s.vision>100) {
				s.vision=100;
			}
			if(s.vision<0) {
				s.vision=0;
			}
			
			s.taille+=(int)(Math.random()*20-10);
			if(s.taille>100) {
				s.taille=100;
			}
			if(s.taille<0) {
				s.taille=0;
			}
			
			s.tailleNageoir+=(int)(Math.random()*20-10);
			if(s.tailleNageoir>100) {
				s.tailleNageoir=100;
			}
			if(s.tailleNageoir<0) {
				s.tailleNageoir=0;
			}
		}
	}
	
	// renvoie un nombre entre 0 et 1
	private double calculFitness()  {
		int total = 6*vision+2*branchies+3*tailleNageoir-3*taille + 300;
		double fit = total/(1400.0);
		return fit;
	}
	public double getFitness() {
		return fitness;
	}

}
