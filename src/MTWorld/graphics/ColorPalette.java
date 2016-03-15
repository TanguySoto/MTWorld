/**
 * -----------------
 * Projet 2I013 : Simulation de vie artificielle UPMC
 * 2014/2015
 * 
 * @author Tanguy SOTO
 *  ----------------
 * Palette des couleurs utilis�e, en RGB
 */

package MTWorld.graphics;

public abstract class ColorPalette {

	private static float[] color = new float[3];

	// Feuillage arbre clair
	public static float[] tree() {
		color[0] = 0.333f;
		color[1] = 0.9f;
		color[2] = 0.08f;
		return color;
	}

	// Feuillage arbre foncé
	public static float[] tree2() {
		color[0] = 0.253f;
		color[1] = 0.8f;
		color[2] = 0.08f;
		return color;
	}

	// Tron arbre foncé
	public static float[] wood() {
		color[0] = 0.545f;
		color[1] = 0.271f;
		color[2] = 0.075f;
		return color;
	}

	// Tron arbre clair
	public static float[] wood2() {
		color[0] = 0.605f;
		color[1] = 0.301f;
		color[2] = 0.105f;
		return color;
	}

	// Feu
	public static float[] fire() {
		color[0] = 0.9f;
		color[1] = 0.f;
		color[2] = 0.f;
		return color;
	}

	// Cendre foncée
	public static float[] cendre() {
		color[0] = 0.15f;
		color[1] = 0.15f;
		color[2] = 0.15f;
		return color;
	}

	// Cendre claire
	public static float[] cendre2() {
		color[0] = 0.25f;
		color[1] = 0.25f;
		color[2] = 0.25f;
		return color;
	}

	// Case eau
	public static float[] water() {
		color[0] = 0.f;
		color[1] = 0.3f;
		color[2] = 1f;
		return color;
	}

	// Case lava
	public static float[] lava() {
		color[0] = 1.f;
		color[1] = 0.449f;
		color[2] = 0f;
		return color;
	}

	// neige
	public static float[] snow1() {
		color[0] = 1.f;
		color[1] = 1.f;
		color[2] = 1.f;
		return color;
	}

	// herbe
	public static float[] grass() {
		color[0] = 0.f;
		color[1] = 0.8f;
		color[2] = 0.1f;
		return color;
	}

	// herbe
	public static float[] grass2() {
		color[0] = 0.f;
		color[1] = 0.7f;
		color[2] = 0.1f;
		return color;
	}

	// proie
	public static float[] prey() {
		color[0] = 1.f;
		color[1] = 0.412f;
		color[2] = 0.7f;
		return color;
	}

	// proie
	public static float[] prey2() {
		color[0] = 1.f;
		color[1] = 0.312f;
		color[2] = 0.61f;
		return color;
	}

	// proie morte
	public static float[] deadPrey() {
		color[0] = 1.f;
		color[1] = 0.842f;
		color[2] = 0.0f;
		return color;
	}

	// predateur
	public static float[] predator() {
		color[0] = 0.545f;
		color[1] = 0.f;
		color[2] = 0.545f;
		return color;
	}

	// predateur
	public static float[] predator2() {
		color[0] = 0.455f;
		color[1] = 0.1f;
		color[2] = 0.45f;
		return color;
	}

	// predateur mort
	public static float[] deadPredator() {
		color[0] = 1.f;
		color[1] = 0.622f;
		color[2] = 0.0f;
		return color;
	}

	// oiseaux
	public static float[] bird() {
		color[0] = 0.345f;
		color[1] = 0.345f;
		color[2] = 0.345f;
		return color;
	}

	// agent "mere"
	public static float[] mother() {
		color[0] = 0.801f;
		color[1] = 0.802f;
		color[2] = 0.982f;
		return color;
	}

	// agent "poisson"
	public static float[] fish() {
		color[0] = 0.0f;
		color[1] = 0.75f;
		color[2] = 1.0f;
		return color;
	}

	// agent "poisson" mort
	public static float[] deadFish() {
		color[0] = 0.098f;
		color[1] = 0.098f;
		color[2] = 0.43f;
		return color;
	}

	// agent "mere" morte
	public static float[] deadMother() {
		color[0] = 0.498f;
		color[1] = 1.000f;
		color[2] = 0.831f;
		return color;
	}

	// agent "mere" morte
	public static float[] deadMother2() {
		color[0] = 0.398f;
		color[1] = 0.900f;
		color[2] = 0.731f;
		return color;
	}

	// Trace laissée par la mère
	public static float[] motherPath() {
		color[0] = 0.601f;
		color[1] = 0.62f;
		color[2] = 0.782f;
		return color;
	}

	// Fruit déposé par l'arbre
	public static float[] fruits() {
		color[0] = 0.8f;
		color[1] = 0.f;
		color[2] = 0.f;
		return color;
	}

	// Fruit déposé par l'arbre
	public static float[] fruits2() {
		color[0] = 0.8f;
		color[1] = 0.25f;
		color[2] = 0.f;
		return color;
	}

	// sable
	public static float[] sand() {
		color[0] = 0.941f;
		color[1] = 0.902f;
		color[2] = 0.549f;
		return color;
	}

	// En cas d'erreur
	public static float[] error() {
		color[0] = 0.5f;
		color[1] = 0.5f;
		color[2] = 0.5f;
		return color;
	}

}
