/**
 * -----------------
 * Projet 2I013 : Simulation de vie artificielle UPMC
 * 2014/2015
 * 
 * @author Tanguy SOTO
 *  ----------------
 * Agent se deplacant d'un point A vers un point B en utilisant l'algorithme A*
 * Realise a l'aide du tutoriel http://khayyam.developpez.com/articles/algo/astar/
 */

package MTWorld.objects;

import static javax.media.opengl.GL2.GL_QUADS;

import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import MTWorld.graphics.ColorPalette;
import MTWorld.worlds.MTWorld;
import MTWorld.worlds.World;

public class Mother extends Agent {

	protected static int evolutionSpeed = 35;
	protected static int starvation = 8450;

	protected static float pMove = 0.8f;
	protected static float pDecompose = 0.00005f;

	protected int age;
	protected int lastTimeEat;
	
	protected ArrayList<Child> children;

	public Thread findingThread;
	private boolean isSearching = false;
	protected ArrayList<Node> path; // Chemin courant a suivre

	public Mother(int __x, int __y, World __world) {
		super(__x, __y, __world);
		isAlive = true;
		age = 0;
		lastTimeEat = 0;
	}

	@Override
	public void step(int index) {
		age++;

		if (age % 300==0 && path==null && (findingThread == null || !findingThread.isAlive())) { // Recherche un chemin
			beginSearch();
		}

		// Mort
		if ((age - lastTimeEat > starvation || world.getCellsTempValues(x, y)>60) && isAlive) {
			isAlive = false;
			isSearching = false;
			//System.out.println("M " + index + " meurt");
		}

		if (world.getIteration() % evolutionSpeed != 0 || !isAlive) {
			if(Math.random()<pDecompose && !isAlive) {
				world.removeAgent(this);
			}
			return;
		}

		if (path == null) { // si pas de chemin
			orient = (int) (Math.random() * (4)); // aleatoire

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

				break;
			case 3: // ouest
				newX = (x - 1 + world.getWidth()) % world.getWidth();
				newY = y;
				state = world.getCellValue(newX, newY);
				break;
			}

			// Vérifie que c'est possible
			if ((state == 0 || state == 5 || state == 9 || state == 8)
					&& !isSearching) { // peut aller sur vide, herbe et sable ou neige
				((MTWorld) world).agentsMap[x][y].remove(this);
				x = newX;
				y = newY;
				((MTWorld) world).agentsMap[x][y].add(this);
			}
		} else { // suit le chemin

			state = world.getCellValue(path.get(path.size() - 1).x,
					path.get(path.size() - 1).y);

			// Verifie que c'est possible
			if ((state == 0 || state == 5 || state == 9 || state == 8)) { // peut aller sur vide, herbe et sable ou neige
				((MTWorld) world).agentsMap[x][y].remove(this);
				x = path.get(path.size() - 1).x;
				y = path.get(path.size() - 1).y;
				((MTWorld) world).agentsMap[x][y].add(this);

				world.addObject(new MotherTrace(x, y, world));
				path.remove(path.size() - 1);
				if (path.isEmpty()) {
					lastTimeEat=age;
					path = null;
				}
			} else { // le chemin est obslete
				path=null;
			}
		}

	}

	public void displayObjectAt(World myWorld, GL2 gl, GLU glu, double height,
			double width) {

		// Evite la repetition des calculs pour chaque vertex
		double e = x * width + width / 4;
		double f = x * width + 3 * width / 4;

		double i = y * height + height / 4;
		double j = y * height + 3 * height / 4;

		double floor = myWorld.getCellHeight(x, y);//Math.min(Math.min(a, b), Math.min(c, d));
		double middle = floor * myWorld.getLandscape().getHeightFactor()
				* height + height * 3.2f;

		floor = floor * myWorld.getLandscape().getHeightFactor() * height;

		float[] color;
		if (isAlive) {
			color = ColorPalette.mother();
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
		} else {
			color = ColorPalette.deadMother();
			gl.glColor3f(color[0], color[1], color[2]);

			gl.glBegin(GL_QUADS);

			if (myWorld.getLandscape().DISPLAY_OBJECTS) {

				gl.glVertex3d(e, i, floor);
				gl.glVertex3d(e, i, middle);
				gl.glVertex3d(e, j, middle);
				gl.glVertex3d(e, j, floor);

				color = ColorPalette.deadMother2();
				gl.glColor3f(color[0], color[1], color[2]);
				gl.glVertex3d(e, j, floor);
				gl.glVertex3d(e, j, middle);
				gl.glVertex3d(f, j, middle);
				gl.glVertex3d(f, j, floor);

				color = ColorPalette.deadMother();
				gl.glColor3f(color[0], color[1], color[2]);
				gl.glVertex3d(f, i, floor);
				gl.glVertex3d(f, i, middle);
				gl.glVertex3d(f, j, middle);
				gl.glVertex3d(f, j, floor);

				color = ColorPalette.deadMother2();
				gl.glColor3f(color[0], color[1], color[2]);
				gl.glVertex3d(e, i, floor);
				gl.glVertex3d(e, i, middle);
				gl.glVertex3d(f, i, middle);
				gl.glVertex3d(f, i, floor);

				color = ColorPalette.deadMother();
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

	/* ========= ATTRIBUTS ET METHODES CONCERNANT A* ========== */

	// Un noeud de nos listes
	private class Node {
		public float coutG; // Cout du depart vers le noeud courant
		public float coutH; // Cout du noeud courant vers l'arrivee
		public float coutF; // F=G+H

		int x, y;

		public Node parent; // Noeud par lequel ont arrive sur celui-ci
	}

	private ArrayList<Node> openedList; // Liste des noeud etudies
	private ArrayList<Node> closedList; // Liste des noeuds du chemin

	private Node end;
	private Node start;

	// Commence la recherche d'un chemin vers un fruit dans un Thread séparé
	private void beginSearch() {
		findingThread = new Thread(new Runnable() {
			@Override
			public void run() {
				//System.out.println("Mother's looking for a fruit ...");
				boolean found = false;

				int xFruit = (int) (Math.random() * (world.getWidth())), yFruit = (int) (Math
						.random() * (world.getHeight()));
				for (UniqueObject o : world.getUniqueObjects()) {
					if (o instanceof Fruits && Math.random()<0.2) {
						xFruit = o.getCoordinate()[0];
						yFruit = o.getCoordinate()[1];
						found=true;
						break;
					}
				}
				if(found)
				path = findPath(x, y, xFruit, yFruit);
			}
		});
		findingThread.start();
	}

	// Renvoie le plus court chemin entre (xStart,yStart) et (xEnd,yEnd)
	private ArrayList<Node> findPath(int xStart, int yStart, int xEnd, int yEnd) {
		isSearching = true;

		openedList = new ArrayList<Node>();
		closedList = new ArrayList<Node>();

		end = new Node();
		end.x = xEnd;
		end.y = yEnd;

		start = new Node();
		start.x = xStart;
		start.y = yStart;

		// Noeud courant = depart
		Node actual = new Node();
		actual.x = start.x;
		actual.y = start.y;

		// Premiere recherche
		openedList.add(actual);
		openedList_to_closedList(actual);
		add_neighbors(actual);

		// Si on est ni arrive ni bloque
		int i = 0;
		while (!(actual.x == end.x && actual.y == end.y)
				&& !(openedList.isEmpty())) {
			if (i % 100 == 0) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					//e.printStackTrace();
				}
			}
			actual = bestNode(openedList);
			openedList_to_closedList(actual);
			add_neighbors(actual);
			i++;
		}

		ArrayList<Node> path = null;
		if (actual.x == end.x && actual.y == end.y) { // chemin trouve 
			path = reassemblePath(closedList);
			//System.out.println("Mother found a path");
		} else {
			//System.out.println("Mother didn't find a path");
		}

		isSearching = false;
		return path;
	}

	// Ajoute ou non les voisins de n dans la liste ouverte
	private void add_neighbors(Node n) {
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				int state = world.getCellValue(n.x + i, n.y + j);
				if ((i == 0 && j == 0) || (state == 4) || (state == 1)
						|| (state == 2) || (state == 3) || (state == 6)
						|| (state == 10) || (state == 11) || (state == 12)) { // evite le courant et obstacles
					continue;
				}
				Node tmp = new Node();
				tmp.x = (n.x + i + world.getWidth()) % world.getWidth();
				tmp.y = (n.y + j + world.getHeight()) % world.getHeight();

				// si le noeud n'est pas deja dans le liste fermee (le chemin)
				Node c = isIn(closedList, tmp.x, tmp.y);
				if (c == null) {
					tmp.coutG = n.coutG + distance(tmp.x, tmp.y, n.x, n.y); // ajout du cout depuis depart (parent + distance vers parent)		
					tmp.coutH = distance(tmp.x, tmp.y, end.x, end.y); // calcul du cout vers arrivee
					tmp.coutF = tmp.coutH + tmp.coutG;
					tmp.parent = n;

					// deja present dans la liste ouverte -> on compare les couts
					c = isIn(openedList, tmp.x, tmp.y);
					if (c != null) {
						if (tmp.coutF < c.coutF) { // meilleur chemin
							int index = openedList.indexOf(c);
							openedList.set(index, tmp);
						}
					} else { // on l'ajoute
						openedList.add(tmp);
					}
				}
			}
		}
	}

	// Trouve le meilleur noeud de la liste ouverte
	private Node bestNode(ArrayList<Node> openedList) {
		if (openedList == null || openedList.size() == 0) {
			return null;
		}
		Node bestNode = openedList.get(0);

		for (Node n : openedList) {
			if (n.coutF < bestNode.coutF) {
				bestNode = n;
			}
		}
		return bestNode;
	}

	// Supprime n de la liste ouverte et l'ajoute dans le liste fermée
	private void openedList_to_closedList(Node n) {
		openedList.remove(n);
		closedList.add(n);
	}

	// Remonte les noeuds en partant de la fin pour obtenir le chemin final
	private ArrayList<Node> reassemblePath(ArrayList<Node> closedList) {
		ArrayList<Node> path = new ArrayList<Node>();
		Node n = closedList.get(closedList.size() - 1); // on commence à l'arrivee
		path.add(n);

		// on remonte jusqu'au depart
		while (!(n.x == start.x && n.y == start.y) && n.parent != null) {
			path.add(n.parent);
			n = n.parent;
		}
		return path;
	}

	// Renvoie un node si il existe deja dans l (compare les coordonnees), null sinon
	private Node isIn(ArrayList<Node> l, int x, int y) {
		for (Node i : l) {
			if (i.x == x && i.y == y) {
				return i;
			}
		}

		return null;
	}

	private float distance(int x1, int y1, int x2, int y2) { // Utilise la distance euclidienne
		return (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	}

}
