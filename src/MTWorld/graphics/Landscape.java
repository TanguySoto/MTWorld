/**
 * -----------------
 * Projet 2I013 : Simulation de vie artificielle UPMC
 * 2014/2015
 * 
 * @author Tanguy SOTO
 *  ----------------
 * Gere la partie graphique de la simulation
 */

package MTWorld.graphics;

import MTWorld.landscapegenerator.*;
import MTWorld.objects.*;
import MTWorld.worlds.*;
import MTWorld.interfaces.OptionWindow;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLException;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

import static javax.media.opengl.GL.*; // GL constants
import static javax.media.opengl.GL2.*; // GL2 constants

public class Landscape implements GLEventListener, KeyListener {

	private MTWorld myWorld;

	public static final int WIDTH = 1280;
	public static final int HEIGHT = 720;

	/* =============== FONCTION ACTIVEE OU NON =============== */
	public boolean DISPLAY_OBJECTS = true;
	public boolean MAP_MODE = false;
	public boolean PAUSE = false;

	/* ====================== JOGL / AWT ===================== */
	private GLU glu; // GL Utilities
	private GLProfile glp;
	private Camera camera; // Permet de se deplacer librement
	@SuppressWarnings("unused")
	private GLCanvas canvas; // On dessine dessus
	private FPSAnimator animator; // Appelle les fonctions GLEvent

	private Frame frame; // Fenetre
	private OptionWindow optionWindow; // Fenetre de reglage

	/* ====================== DIMENSIONS ====================== */
	private int dx; // Dimensions du paysage en nombre de case
	private int dy; //

	private float size = 0.5f; // Largeur d'une case
	private final float HEIGHT_FACTOR_VALUE = 128f;
	private float heightFactor = HEIGHT_FACTOR_VALUE;

	private double[][] landscape; // Contient l'altitude

	// Permet le calcul des FPS
	private int it = 0;
	private int lastItStamp = 0;
	private long lastTimeStamp = 0;
	private int wait = 1;

	/* ====================== TEXTURES ====================== */
	public int moonTextureNb;
	public int sunTextureNb;
	public int waterTextureNb;

	public Texture moonTexture;
	public Texture sunTexture;

	public Texture waterTexture;
	public Texture grassTexture;

	/**
	 * Creer un landscape a partir de bruit de perlin
	 */
	public Landscape(World _myWorld, int _dx, int _dy, double scaling, double landscapeAltitudeRatio) {
		myWorld = (MTWorld) _myWorld;

		landscape = PerlinNoiseLandscapeGenerator.generatePerlinNoiseLandscape(_dx, _dy, scaling,
				landscapeAltitudeRatio, 9);
		// landscape = new double[5][5];

		initLandscape();
	}

	/**
	 * Creer un landscape a partir d'une HeigthMap
	 */
	public Landscape(World _myWorld, String _filename, double scaling, double landscapeAltitudeRatio) {
		myWorld = (MTWorld) _myWorld;

		landscape = LoadFromFileLandscape.load(_filename, scaling, landscapeAltitudeRatio);

		initLandscape();
	}

	/**
	 * Initialise le landscape (dim, world ...)
	 */
	public void initLandscape() {
		dx = landscape.length;
		dy = landscape[0].length;

		myWorld.init(dx - 1, dy - 1, landscape, this);
	}

	/**
	 * Lance la simulation du landscape
	 */
	public void run() {

		/*
		 * Highly recommended by JOGL developer when working on LINUX
		 * "Linux specific locking optimization" GLProfile.initSingleton();
		 */

		glp = GLProfile.getDefault();
		GLCapabilities caps = new GLCapabilities(glp);
		GLCanvas canvas = new GLCanvas(caps);

		frame = new Frame("MTWorld");
		frame.setSize(WIDTH, HEIGHT);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(dim.width / 2 - frame.getSize().width / 2,
				dim.height / 2 - frame.getSize().height / 2);
		frame.add(canvas);
		frame.setResizable(true);
		frame.setVisible(true);

		animator = new FPSAnimator(canvas, 60);
		animator.add(canvas);
		animator.start();

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				optionWindow.closeWindows();
			}
		});

		canvas.addGLEventListener(this);
		canvas.addKeyListener(this);

		camera = new Camera(1, 1, 15, 2, 0, 0, 0.0f, 0.0f, 1.0f, canvas, this);
		camera.setPosition(15,15,15);
		camera.setPointCible(0, 0, 0);
		camera.setVitesse(0.6f);
		camera.setPlayer(((MTWorld) myWorld).player);

		System.out
				.println(".======= MTWorld =======.\n| By : Tanguy SOTO      |\n|      Malcolm AUFFRAY  |\n'======================='");
		System.out.println("=> Simulation on " + dx * dy + " cells. (" + dx + "x" + dy + ")");
	}

	/** ====================== JOGL EVENTS ====================== */
	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2(); // get the OpenGL graphics context
		glu = new GLU();

		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // set background (clear) color
		gl.glClearDepth(1.0f); // set clear depth value to farthest
		gl.glEnable(GL.GL_DEPTH_TEST); // enables depth testing
		gl.glDepthFunc(GL.GL_LEQUAL); // the type of depth test to do
		gl.glHint(GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST); // best
																		// perspective
																		// correction
		gl.glShadeModel(GL_SMOOTH); // blends colors nicely, and smoothes out

		// Transparence
		gl.glEnable(GL_BLEND);
		gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		// Double buffer
		gl.glEnable(GL2.GL_DOUBLEBUFFER);
		drawable.setAutoSwapBufferMode(true);

		// Culling - display only triangles facing the screen
		//gl.glCullFace(GL.GL_FRONT);
		//gl.glEnable(GL.GL_CULL_FACE);

		// Textures	
		gl.glEnable(GL_TEXTURE_2D);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

		try {
			moonTextureNb = genTexture(gl);
			InputStream stream = getClass().getResourceAsStream("Textures/moon.jpg");
			TextureData data = TextureIO.newTextureData(glp, stream, false, ".jpg");
			moonTexture = TextureIO.newTexture(data);

			sunTextureNb = genTexture(gl);
			stream = getClass().getResourceAsStream("Textures/sun.jpg");
			data = TextureIO.newTextureData(glp, stream, false, ".jpg");
			sunTexture = TextureIO.newTexture(data);
			
			waterTextureNb = genTexture(gl);
			stream = getClass().getResourceAsStream("Textures/water_102.jpg");
			data = TextureIO.newTextureData(glp, stream, false, ".jpg");
			waterTexture = TextureIO.newTexture(data);
			

		} catch (GLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void display(GLAutoDrawable drawable) {

		if (PAUSE) {
			return;
		}

		// Get the OpenGL 2 graphics context
		GL2 gl = drawable.getGL().getGL2();

		// Efface l'ecran avec le bon fond
		Sun s = (Sun) myWorld.getUniqueObjects().get(0);
		float r = (float) (0.529f * s.getCurrentHeight() / s.getMaxHeight());
		if (r < 0) {
			r = 0;
		}
		float g = (float) (0.808f * s.getCurrentHeight() / s.getMaxHeight());
		if (g < 0) {
			g = 0;
		}
		float b = (float) (0.980f * s.getCurrentHeight() / s.getMaxHeight());
		if (b < 0) {
			b = 0;
		}
		gl.glClearColor(r, g, b, 1f);

		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		gl.glClear(GL.GL_DEPTH_BUFFER_BIT);

		// Mise a jours de la camera
		camera.lookAt(gl, glu);

		// R�initialise la matrice de "dessin"
		gl.glMatrixMode(GL_MODELVIEW);
		gl.glLoadIdentity();

		// Dessin du landscape
		gl.glBegin(GL_QUADS);

		// Base
		for (int x = 0; x < dx - 1; x++) {
			for (int y = 0; y < dy - 1; y++) {
				gl.glColor3d(0.855, 0.647, 0.125);
				gl.glVertex3f(x * size, y * size, 0);
				gl.glVertex3f(x * size + size, y * size, 0);
				gl.glVertex3f(x * size + size, y * size + size, 0);
				gl.glVertex3f(x * size, y * size + size, 0);
			}
		}

		// Sol
		for (int x = 0; x < dx - 1; x++) {
			for (int y = 0; y < dy - 1; y++) {

				int cell1 = myWorld.getCellValue(x % dx, y % dy);
				int cell2 = myWorld.getCellValue((x + 1) % dx, y % dy);
				int cell3 = myWorld.getCellValue((x + 1) % dx, (y + 1) % dy);
				int cell4 = myWorld.getCellValue(x % dx, (y + 1) % dy);

				int cell = cell1;

				if (cell1 == cell2 && cell2 == cell3 && cell3 == cell4 && cell1 == 4) { // eau avec interpolation

					float color[] = this.myWorld.getCellColorValue(x % dx, y % dy);
					gl.glColor3d(color[0], color[1], color[2]);
					gl.glVertex3f(x * size, y * size, (float) landscape[x % dx][y % dy]
							* heightFactor * size);

					color = this.myWorld.getCellColorValue((x + 1) % dx, y % dy);
					gl.glColor3d(color[0], color[1], color[2]);
					gl.glVertex3f(x * size + size, y * size,
							(float) landscape[(x + 1) % dx][y % dy] * heightFactor * size);

					color = this.myWorld.getCellColorValue((x + 1) % dx, (y + 1) % dy);
					gl.glColor3d(color[0], color[1], color[2]);
					gl.glVertex3f(x * size + size, y * size + size,
							(float) landscape[(x + 1) % dx][(y + 1) % dy] * heightFactor * size);

					color = this.myWorld.getCellColorValue(x % dx, (y + 1) % dy);
					gl.glColor3d(color[0], color[1], color[2]);
					gl.glVertex3f(x * size, y * size + size,
							(float) landscape[x % dx][(y + 1) % dy] * heightFactor * size);
				} else { // Sol sans interpolation

					float color[] = this.myWorld.getCellColorValue(x % dx, y % dy);
					gl.glColor4d(color[0], color[1], color[2], 1);

					gl.glVertex3f(x * size, y * size, (float) landscape[x % dx][y % dy]
							* heightFactor * size);

					gl.glVertex3f(x * size + size, y * size,
							(float) landscape[(x + 1) % dx][y % dy] * heightFactor * size);

					gl.glVertex3f(x * size + size, y * size + size,
							(float) landscape[(x + 1) % dx][(y + 1) % dy] * heightFactor * size);

					gl.glVertex3f(x * size, y * size + size,
							(float) landscape[x % dx][(y + 1) % dy] * heightFactor * size);

				}

				if (DISPLAY_OBJECTS) {
					// CommonObject (sans etats internes)
					myWorld.displayObjectAt(myWorld, gl, cell, x, y, size, size);
				}

			}
		} // fin For: sol

		gl.glEnd();

		myWorld.displayUniqueObjects(myWorld, gl, glu, size, size);

		it++;

		myWorld.step();

		// Calcul et affichage des FPS
		if (System.currentTimeMillis() - lastTimeStamp >= 1000) {
			int fps = (it - lastItStamp) / 1;

			if (Math.random() < 0.2) { // Evite de flood
				System.out.print("Frames per seconds  : " + fps + " ; ");
				System.out.println();
			}

			lastItStamp = it;
			lastTimeStamp = System.currentTimeMillis();
		}

		try {
			Thread.sleep(wait);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL2 gl = drawable.getGL().getGL2();

		if (height == 0) { // evite la division par 0
			height = 1;
		}
		float aspect = (float) width / height;

		// Set the view port (display area) to cover the entire window
		gl.glViewport(0, 0, width, height);

		// Passe la matrice en mode projection et l'initialise
		gl.glMatrixMode(GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(70.0, aspect, 0.1, 100.0); // angle de vue, ratio
														// fenetre, distance de
														// projection, distance
														// de vue max

		// Repasse la matrice en mode dessin
		gl.glMatrixMode(GL_MODELVIEW);
		gl.glLoadIdentity();

	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// Non utilisee (??)
	}

	/** ====================== KEYBOARD EVENTS ====================== */
	@Override
	public void keyTyped(KeyEvent e) {
		// Non utilisee
	}

	@Override
	public void keyPressed(KeyEvent key) {

		switch (key.getKeyCode()) {
		case KeyEvent.VK_ESCAPE:
			animator.stop();
			System.exit(0);
			break;
		case KeyEvent.VK_O: // Desactive le dessins des objets -> gain de performance
			DISPLAY_OBJECTS = !DISPLAY_OBJECTS;
			break;
		case KeyEvent.VK_M: // Affiche la carte
			MAP_MODE = !MAP_MODE;
			this.enableMapMode(MAP_MODE);
			break;
		case KeyEvent.VK_P: // Pause
			PAUSE = !PAUSE;
			break;
		case KeyEvent.VK_J:
			camera.moveToPlayer();
			break;
		case KeyEvent.VK_X:
			myWorld.player.action();
			break;
		case KeyEvent.VK_UP:
			if (myWorld.player.action <= 13) {
				myWorld.player.action++;
				myWorld.player.displayCurrentOject();
			}
			break;
		case KeyEvent.VK_DOWN:
			if (myWorld.player.action >= 1) {
				myWorld.player.action--;
				myWorld.player.displayCurrentOject();
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// Non utilisee
	}

	/** ========= **/

	public float getHeightFactor() {
		return heightFactor;
	}

	public double[][] getLandscape() {
		return landscape;
	}

	public float getSize() {
		return size;
	}

	public Camera getCamera() {
		return camera;
	}

	public GLU getGLU() {
		return glu;
	}

	private int genTexture(GL gl) {
		final int[] tmp = new int[1];
		gl.glGenTextures(1, tmp, 0);
		return tmp[0];
	}

	/** ========= **/

	// Affiche la carte du monde
	private void enableMapMode(boolean enable) {
		if (enable) {
			heightFactor = 0;
			DISPLAY_OBJECTS = false;
			camera.setPointCible(dx * size / 2.0f, dy * size / 2.0f, 0);
			camera.setPosition(dx * size / 2.0f, dy * size / 2.0f, (float) (Math.tan(Math.PI / 6.5)
					* Math.sqrt((dx) * (dx) + (dy) * (dy)) * size));

		} else {
			heightFactor = HEIGHT_FACTOR_VALUE;
			DISPLAY_OBJECTS = true;
			camera.setPosition(15, 15, 15);
			camera.setPointCible(0, 0, 0);
		}
		camera.setEnable(!enable);
	}

	public void stopSimulation() {
		System.out.println("=> Simulation stopped.\n");
		animator.stop();
		for(Agent a : myWorld.getAgents()) {
			if(a instanceof Mother) {
				Mother m = (Mother)a;
				if(m.findingThread!= null) {
					try {
						m.findingThread.interrupt();
					}
					catch(Exception e) {
						
					}
				}
			}
		}
		frame.dispose();
	}

	public OptionWindow getSettingFrame() {
		return optionWindow;
	}

	public void setSettingFrame(OptionWindow optionWindow) {
		this.optionWindow = optionWindow;
	}

	public MTWorld getMyWorld() {
		return myWorld;
	}

	public void setMyWorld(MTWorld myWorld) {
		this.myWorld = myWorld;
	}

}
