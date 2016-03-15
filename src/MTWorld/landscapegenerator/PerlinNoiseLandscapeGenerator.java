/**
 * -----------------
 * Projet 2I013 : Simulation de vie artificielle UPMC
 * 2014/2015
 * 
 * @author Tanguy SOTO
 *  ----------------
 * Genere un landscape en utilisant du bruit de Perlin
 */

package MTWorld.landscapegenerator;

public class PerlinNoiseLandscapeGenerator {
	
	private static int freqMod=3;
	
	private static int alea = 60;

	public static double[][] generatePerlinNoiseLandscape(int dxView, int dyView, double scaling,
			double landscapeAltitudeRatio, int perlinLayerCount) {
		double landscape[][] = new double[dxView][dyView];
		
		alea=(int)(Math.random()*(2000-50)+50);

		for (int x = 0; x < dxView; x++) {
			for (int y = 0; y < dyView; y++) {
				landscape[x][y] =perlinNoise2D(x, y, perlinLayerCount); // get2DPerlinNoiseValue(x, y, 30); 
			}
		}

		//http://freespace.virgin.net/hugo.elias/models/m_perlin.htm pour une explication

		// scaling and polishing
		landscape = LandscapeToolbox.scaleAndCenter(landscape, scaling, landscapeAltitudeRatio);
		landscape = LandscapeToolbox.smoothLandscape(landscape);
		landscape = LandscapeToolbox.avoidUnderZero(landscape);

		return landscape;
	}

	/**
	 * Generation de nombre aleatoire.
	 * Differe de Math.random() : renvoie toujours le meme nombre si on lui passe les memes parametres.
	 * => Besoin de nombre premiers differents pour obtenir des bruits differents 
	 */
	private static double noise(int x, int y) {
		int n = x + y * alea; // 57
		n = (n << 13) ^ n;
		
		return (1.0 - ((n * (n * n * 15731 + 789221) + 1376312589) & 0x7fffffff) / 1073741824.0);
	}

	/**
	 * Lisse les valeurs obtenues aleatoirement (meilleur rendu)
	 */
	private static double smoothNoise(int x, int y) {
		double corners = (noise(x - 1, y - 1) + noise(x + 1, y - 1) + noise(x - 1, y + 1) + noise(
				x + 1, y + 1)) / 16.0;
		double sides = (noise(x - 1, y) + noise(x + 1, y) + noise(x, y - 1) + noise(x, y + 1)) / 8.0;
		double center = noise(x, y) / 4.0;
		return corners + sides + center;
	}

	/**
	 * Permet l'interpolation de x entre a et b.
	 */
	private static double cosInterpolate(double a, double b, double x) {
		double ft = x * 3.1415927;
		double f = (1 - Math.cos(ft)) * 0.5;

		return a * (1 - f) + b * f;
	}

	/**
	 * Renvoie un nombre aleatoire interpolee et lissee
	 */
	private static double interpolatedNoise(double x, double y) {
		int integer_X = (int) x;
		double fractional_X = x - integer_X;

		int integer_Y = (int) y;
		double fractional_Y = y - integer_Y;

		double v1 = smoothNoise(integer_X, integer_Y);
		double v2 = smoothNoise(integer_X + 1, integer_Y);
		double v3 = smoothNoise(integer_X, integer_Y + 1);
		double v4 = smoothNoise(integer_X + 1, integer_Y + 1);

		double i1 = cosInterpolate(v1, v2, fractional_X);
		double i2 = cosInterpolate(v3, v4, fractional_X);

		return cosInterpolate(i1, i2, fractional_Y);
	}

	/**
	 * Renvoie le bruit de Perlin 2D pour (x,y) avec 'octaves' octaves;
	 */
	private static double perlinNoise2D(float x, float y, int octaves) {

		double total = 0;
		double p = 0.25f;

		for (int i = 0; i < octaves-(freqMod-1); i++) {
			double frequency = Math.pow(2, octaves-i);
			
			double amplitude =Math.pow(p, i);

			total = total + interpolatedNoise(x * 1/frequency, y * 1/frequency) * 1/amplitude;

			//System.out.println("f = "+frequency+" a = "+amplitude);
		};
		
		
		return total;
	}

	public static int getFreqMod() {
		return freqMod;
	}

	public static void setFreqMod(int n) {
		freqMod = n;
	}
}
