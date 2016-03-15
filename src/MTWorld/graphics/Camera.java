/**
 * -----------------
 * Projet 2I013 : Simulation de vie artificielle UPMC
 * 2014/2015
 * 
 * @author Tanguy SOTO
 *  ----------------
 * Implemente une 'FreeFlight' camera with JOGL, realisee avec l'aide du site du zeros
 */

package MTWorld.graphics;

import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_PROJECTION;

import java.awt.Canvas;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import MTWorld.objects.Player;

public class Camera implements KeyListener, MouseMotionListener, MouseListener, MouseWheelListener {

	private final static float DEFAULT_VITESSE = 0.6f;

	private float sensibilite; // sensibilite de la souris
	private float vitesse;	   // vitesse de deplacement
	private boolean isEnable = true;
	private boolean firstPerson = false;
	private boolean playerMove = false;
	private boolean playerAction = false;

	// Attributs d'orientation
	private float phi; 	// angle d'orientation selon l'axe y
	private float theta;// angle d'orientation selon l'axe x

	private float orientationX; //
	private float orientationY; // Vecteur orientation
	private float orientationZ; //

	// Attributs de deplacement
	private float axeVerticalX; //
	private float axeVerticalY; // Vecteur axe vertical (ici Y)
	private float axeVerticalZ; //

	private float deplacementLateralX; //
	private float deplacementLateralY; // Vecteur deplacement lateral
	private float deplacementLateralZ; //

	// Autres attributs
	private float positionX; //
	private float positionY; // Position de la camera
	private float positionZ; //

	private float pointCibleX; //
	private float pointCibleY; // Point cible de la camera
	private float pointCibleZ; //

	// Position actuelles de la souris
	private int x;
	private int y;
	// Position precedente de la souris
	private int xprec;
	private int yprec;

	private Landscape landscape;
	private Player player; // optional

	public Camera(Canvas c) {
		this(1, 1, 1, 0, 0, 0, 0.0f, 1.0f, 0.0f, c, null);
	}

	public Camera(int posX, int posY, int posZ, int cibleX, int cibleY, int cibleZ, float axeX,
			float axeY, float axeZ, Canvas c, Landscape landscape) {

		this.landscape = landscape;

		phi = 0;
		theta = 0;

		sensibilite = 0.7f;
		vitesse = DEFAULT_VITESSE;

		axeVerticalX = axeX;
		axeVerticalY = axeY;
		axeVerticalZ = axeZ;

		setPosition(posX, posY, posZ);
		setPointCible(cibleX, cibleY, cibleZ);

		if (c != null) {
			c.addMouseListener(this);
			c.addMouseMotionListener(this);
			c.addMouseWheelListener(this);
			c.addKeyListener(this);
		} else {
			System.err.println("Camera.Camera : canvas object is null\n");
		}
	}

	// Position la camera au point donne
	public void setPosition(float _x, float _y, float _z) {

		// Mise e jour de la position
		positionX = _x;
		positionY = _y;
		positionZ = _z;

		// Actualisation du point cible
		pointCibleX = positionX + orientationX;
		pointCibleY = positionY + orientationY;
		pointCibleZ = positionZ + orientationZ;
	}

	// Oriente la camera vers le point cible donne
	public void setPointCible(float _x, float _y, float _z) {

		// Mise e jour du point cible
		pointCibleX = _x;
		pointCibleY = _y;
		pointCibleZ = _z;

		// calcul du vecteur orientation ...
		orientationX = _x - positionX;
		orientationY = _y - positionY;
		orientationZ = _z - positionZ;

		// ... normalisation
		float norme = (float) Math.sqrt(orientationX * orientationX + orientationY * orientationY
				+ orientationZ * orientationZ);
		orientationX = orientationX / norme;
		orientationY = orientationY / norme;
		orientationZ = orientationZ / norme;

		// calcul du vecteur deplacement lateral (vecteur normal au plan
		// (O,Vertical,Orientation)) ...
		deplacementLateralX = axeVerticalY * orientationZ - axeVerticalZ * orientationY;
		deplacementLateralY = axeVerticalZ * orientationX - axeVerticalX * orientationZ;
		deplacementLateralZ = axeVerticalX * orientationY - axeVerticalY * orientationX;

		// ... normalisation
		norme = (float) Math.sqrt(deplacementLateralX * deplacementLateralX + deplacementLateralY
				* deplacementLateralY + deplacementLateralZ * deplacementLateralZ);
		deplacementLateralX = deplacementLateralX / norme;
		deplacementLateralY = deplacementLateralY / norme;
		deplacementLateralZ = deplacementLateralZ / norme;

		// Calcul des angles
		if (axeVerticalX == 1) { // avec axe vertical X
			phi = (float) Math.asin(orientationX);
			theta = (float) Math.acos(orientationY / Math.cos(phi));
			if (orientationY < 0) {
				theta *= -1;
			}
		} else {
			if (axeVerticalY == 1) { // avec axe vertical Y
				phi = (float) Math.asin(orientationY);
				theta = (float) Math.acos(orientationZ / Math.cos(phi));
				if (orientationZ < 0) {
					theta *= -1;
				}
			} else { // avec axe vertical Z
				phi = (float) Math.asin(orientationX);
				theta = (float) Math.acos(orientationZ / Math.cos(phi));
				if (orientationZ < 0) {
					theta *= -1;
				}
				//System.out.println("*");
			}
		}

		// Conversion en degres
		phi = (float) (phi * 180 / Math.PI);
		theta = (float) (theta * 180 / Math.PI);

		//System.out.println(phi+" | "+theta);
	}

	// Regle la sensibilite de la souris
	public void setSensibilite(float sensibilite) {
		this.sensibilite = sensibilite;
	}

	// Regle la vitesse de deplacement
	public void setVitesse(float vitesse) {
		this.vitesse = vitesse;
	}

	// Renvoie la sensibilite de la souris
	public float getSensibilite() {
		return sensibilite;
	}

	// Renvoie la vitesse de deplacement
	public float getVitesse() {
		return sensibilite;
	}

	// Associe p � la camera
	public void setPlayer(Player p) {
		player = p;
	}

	public Player getPlayer() {
		return player;
	}

	// Fixe le position reference de la souris pour le calcul des angles
	protected void setMouseReference(int _x, int _y) {
		x = _x;
		y = _y;
	}

	// Oriente la camera en fonction de la position de la souris
	protected void orienter(int _x, int _y) {

		// Mise e jour des coordonnees de la souris
		xprec = x;
		yprec = y;
		x = _x;
		y = _y;

		// chgt des angles
		theta = (theta + (x - xprec) * sensibilite * 90f / 200);
		phi = (phi + (y - yprec) * sensibilite * 90f / 200);

		// limitation de l'angle vertical
		if (phi > 89.0) {
			phi = 89.0f;
		} else {
			if (phi < -89.0) {
				phi = -89.0f;
			}
		}

		// Passage en radian
		float phiRadian = (float) (phi * Math.PI / 180);
		float thetaRadian = (float) (theta * Math.PI / 180);

		// Mise e jour du vecteur orientation
		if (axeVerticalX == 1) { // avec axe vertical X
			orientationX = (float) (Math.sin(phiRadian));
			orientationY = (float) (Math.cos(phiRadian) * Math.cos(thetaRadian));
			orientationZ = (float) (Math.cos(phiRadian) * Math.sin(thetaRadian));
		} else {
			if (axeVerticalY == 1) { // avec axe vertical Y
				orientationX = (float) (Math.cos(phiRadian) * Math.sin(thetaRadian));
				orientationY = (float) Math.sin(phiRadian);
				orientationZ = (float) (Math.cos(phiRadian) * Math.cos(thetaRadian));
			} else { // avec axe vertical Z
				orientationX = (float) (Math.cos(phiRadian) * Math.cos(thetaRadian));
				orientationY = (float) (Math.cos(phiRadian) * Math.sin(thetaRadian));
				orientationZ = (float) (Math.sin(phiRadian));
			}
		}

		// Mise e jour du vecteur deplacement lateral (vecteur normal au plan
		// (O,Vertical,Orientation)) ...
		deplacementLateralX = axeVerticalY * orientationZ - axeVerticalZ * orientationY;
		deplacementLateralY = axeVerticalZ * orientationX - axeVerticalX * orientationZ;
		deplacementLateralZ = axeVerticalX * orientationY - axeVerticalY * orientationX;

		// ... normalisation
		float norme = (float) Math.sqrt(deplacementLateralX * deplacementLateralX
				+ deplacementLateralY * deplacementLateralY + deplacementLateralZ
				* deplacementLateralZ);
		deplacementLateralX = deplacementLateralX / norme;
		deplacementLateralY = deplacementLateralY / norme;
		deplacementLateralZ = deplacementLateralZ / norme;

		// Mise e jour du point cible
		pointCibleX = positionX + orientationX;
		pointCibleY = positionY + orientationY;
		pointCibleZ = positionZ + orientationZ;

	}

	// Deplace la camera en fonction de la touche enfoncee
	protected void deplace(int keyCode) {
		switch (keyCode) {
		case KeyEvent.VK_Z: // on avance

			
			if (!playerMove) {
				positionX += (orientationX * vitesse);
				positionY += (orientationY * vitesse);
				positionZ += (orientationZ * vitesse);
			}

			if (playerMove) {
				player.xR += (orientationX * vitesse);
				player.yR += (orientationY * vitesse);
			}

			// Evite de traverser le sol en first personne
			if (landscape != null && firstPerson) {
				int x = (int) (positionX / landscape.getSize());
				int y = (int) (positionY / landscape.getSize());
				x = (x + landscape.getLandscape().length) % landscape.getLandscape().length;
				y = (y + landscape.getLandscape()[0].length) % landscape.getLandscape()[0].length;

				if ((landscape.getLandscape()[x][y] * landscape.getSize()
						* landscape.getHeightFactor() + landscape.getSize()) > positionZ) {
					positionZ = (float) (landscape.getLandscape()[x][y] * landscape.getSize()
							* landscape.getHeightFactor() + landscape.getSize());
				}
			}

			pointCibleX = positionX + orientationX;
			pointCibleY = positionY + orientationY;
			pointCibleZ = positionZ + orientationZ;

			break;
		case KeyEvent.VK_S: // on recule
			
			
			if (!playerMove) {
				positionX -= orientationX * vitesse;
				positionY -= orientationY * vitesse;
				positionZ -= orientationZ * vitesse;
			}
			
			if (playerMove) {
				player.xR -= (orientationX * vitesse);
				player.yR -= (orientationY * vitesse);
			}			

			pointCibleX = positionX + orientationX;
			pointCibleY = positionY + orientationY;
			pointCibleZ = positionZ + orientationZ;
			break;
		case KeyEvent.VK_Q: // on va a gauche
			
			if (!playerMove) {
				positionX += deplacementLateralX * vitesse;
				positionY += deplacementLateralY * vitesse;
				positionZ += deplacementLateralZ * vitesse;
			}
			
			if (playerMove) {
				player.xR += (deplacementLateralX * vitesse);
				player.yR += (deplacementLateralY * vitesse);
			}

			pointCibleX = positionX + orientationX;
			pointCibleY = positionY + orientationY;
			pointCibleZ = positionZ + orientationZ;
			break;
		case KeyEvent.VK_D: // on va a droite
			
			if (!playerMove) {
				positionX -= deplacementLateralX * vitesse;
				positionY -= deplacementLateralY * vitesse;
				positionZ -= deplacementLateralZ * vitesse;
			}
			
			if (playerMove) {
				player.xR -= (deplacementLateralX * vitesse);
				player.yR -= (deplacementLateralY * vitesse);
			}

			pointCibleX = positionX + orientationX;
			pointCibleY = positionY + orientationY;
			pointCibleZ = positionZ + orientationZ;
			break;

		case KeyEvent.VK_A: // on monte
			if (axeVerticalX == 1) { // avec axe vertical X
				positionX += 1.0f * vitesse;
				pointCibleX = positionX + orientationX;
			} else {
				if (axeVerticalY == 1) { // avec axe vertical Y
					positionY += 1.0f * vitesse;
					pointCibleY = positionY + orientationY;
				} else { // avec axe vertical Z
					positionZ += 1.0f * vitesse;
					pointCibleZ = positionZ + orientationZ;
				}
			}

			break;

		case KeyEvent.VK_W: // on descend
			if (axeVerticalX == 1) { // avec axe vertical X
				positionX -= 1.0f * vitesse;
				pointCibleX = positionX + orientationX;
			} else {
				if (axeVerticalY == 1) { // avec axe vertical Y
					positionY -= 1.0f * vitesse;
					pointCibleY = positionY + orientationY;
				} else { // avec axe vertical Z
					positionZ -= 1.0f * vitesse;
					pointCibleZ = positionZ + orientationZ;
				}
			}
			break;
		default:
			break;
		}
		if(playerMove&&playerAction) {
			player.action();
		}
	}

	// Apelle glu.glLookat() et glu.gluPerspective() en fonction de l'etat de la camera
	public void lookAt(GL2 gl, GLU glu) {
		if (gl == null || glu == null) {
			System.err.println("Camera.lookAt : GL2 or GLU object is null\n");
			return;
		}

		gl.glMatrixMode(GL_PROJECTION);
		gl.glLoadIdentity();

		float widthHeightRatio = (float) 1200 / 800;
		glu.gluPerspective(70.0, widthHeightRatio, 0.1, 300.0);

		glu.gluLookAt(positionX, positionY, positionZ, pointCibleX, pointCibleY, pointCibleZ,
				axeVerticalX, axeVerticalY, axeVerticalZ);
	}

	public void setEnable(boolean enable) {
		isEnable = enable;
	}

	// Active le mode first person : vitesse r�duite et ne traverse pas le sol
	public void firstPerson() {
		firstPerson = !firstPerson;
		if (firstPerson) {
			this.vitesse = 0.35f;
		} else {
			this.vitesse = DEFAULT_VITESSE;
		}
	}
	
	// Se place proche du joueur
	public void moveToPlayer() {
		setPosition(player.xR -7*landscape.getSize(), player.yR - 7 * landscape.getSize(),
				player.zR+landscape.getSize() * 10);
		setPointCible(player.xR, player.yR,player.zR);
	}

	// Affiche les parametres de la camera pour le debuggage
	protected void printProperties() {

		System.out.println("posX= " + positionX + " posY= " + positionY + " posZ= " + positionZ);
		System.out.println("cibleX= " + pointCibleX + " cibleY= " + pointCibleY + " cibleZ= "
				+ pointCibleZ);

		System.out.println("orientX= " + orientationX + " orientY= " + orientationY + " orientZ= "
				+ orientationZ);
		System.out.println("laterX= " + deplacementLateralX + " laterY= " + deplacementLateralY
				+ " laterZ= " + deplacementLateralZ);
		System.out.println("---------------------------------------------");
	}

	/* ============== Mouse EVENTS ============ */
	@Override
	public void mouseClicked(MouseEvent e) {
		// Non utilisee		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON3) {
			playerMove=true;
			firstPerson();
		}
		else if(e.getButton() == MouseEvent.BUTTON1) {
			playerAction=true;
		}
		this.setMouseReference(e.getX(), e.getY());
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON3) {
			playerMove=false;
			firstPerson();
		}
		else if(e.getButton() == MouseEvent.BUTTON1) {
			playerAction=false;
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// Non utilisee		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// Non utilisee		
	}

	/* ============== MouseMotion EVENTS ============ */
	@Override
	public void mouseDragged(MouseEvent e) {
		this.orienter(e.getX(), e.getY());
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// Non utilisee		
	}

	/* ============== Keyboard EVENTS ============ */
	@Override
	public void keyTyped(KeyEvent e) {
		// Non utilisee
	}

	@Override
	public void keyPressed(KeyEvent key) {
		if (!isEnable) {
			return;
		}
		this.deplace(key.getKeyCode());
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// Non utilisee		
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (!isEnable) {
			return;
		}
		if (e.getWheelRotation() >= 0) {
			deplace(KeyEvent.VK_S);
		} else {
			deplace(KeyEvent.VK_Z);
		}

	}

}
