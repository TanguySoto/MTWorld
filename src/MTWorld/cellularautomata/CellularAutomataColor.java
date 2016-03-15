// created by nicolas.bredeche(at)upmc.fr
// date of creation: 2013-1-12

package MTWorld.cellularautomata;

public class CellularAutomataColor extends CellularAutomata {

	protected float Buffer0[][][];
	protected float Buffer1[][][];

	public CellularAutomataColor(int _dx, int _dy, boolean _buffering) {
		super(_dx, _dy, _buffering);

		Buffer0 = new float[dx][dy][3];
		Buffer1 = new float[dx][dy][3];

		for (int x = 0; x != dx; x++) {
			for (int y = 0; y != dy; y++) {
				Buffer0[x][y][0] = 255;
				Buffer0[x][y][1] = 255;
				Buffer0[x][y][2] = 255;
				Buffer1[x][y][0] = 255;
				Buffer1[x][y][1] = 255;
				Buffer1[x][y][2] = 255;
			}
		}
	}

	public float[] getCellState(int _x, int _y) {
		checkBounds(_x, _y);

		float color[] = new float[3];

		if (buffering == false) {
			color[0] = Buffer0[_x][_y][0];
			color[1] = Buffer0[_x][_y][1];
			color[2] = Buffer0[_x][_y][2];
		} else {
			if (activeIndex == 1) // read old buffer
			{
				color[0] = Buffer0[_x][_y][0];
				color[1] = Buffer0[_x][_y][1];
				color[2] = Buffer0[_x][_y][2];
			} else {
				color[0] = Buffer1[_x][_y][0];
				color[1] = Buffer1[_x][_y][1];
				color[2] = Buffer1[_x][_y][2];
			}
		}

		return color;
	}

	public void setCellState(int _x, int _y, float _r, float _g, float _b) {
		checkBounds(_x, _y);

		if (_r > 1.0f || _g > 1.0f || _b > 1.0f) {
			System.err
					.println("[WARNING] CellularAutomataColor - value must be in [0.0,1.0[ ( was: "
							+ _r + "," + _g + "," + _b + " ) -- THRESHOLDING.");
			if (_r > 1.0f)
				_r = 1.0f;
			if (_g > 1.0f)
				_g = 1.0f;
			if (_b > 1.0f)
				_b = 1.0f;
		}

		if (buffering == false) {
			Buffer0[_x][_y][0] = _r;
			Buffer0[_x][_y][1] = _g;
			Buffer0[_x][_y][2] = _b;
		} else {
			if (activeIndex == 0) // write new buffer
			{
				Buffer0[_x][_y][0] = _r;
				Buffer0[_x][_y][1] = _g;
				Buffer0[_x][_y][2] = _b;
			} else {
				Buffer1[_x][_y][0] = _r;
				Buffer1[_x][_y][1] = _g;
				Buffer1[_x][_y][2] = _b;
			}
		}
	}

	public void setCellState(int _x, int _y, float _color[]) {
		checkBounds(_x, _y);

		if (_color[0] > 1.0 || _color[1] > 1.0 || _color[2] > 1.0) {
			System.err
					.println("[WARNING] CellularAutomataColor - value must be in [0.0,1.0[ ( was: "
							+ _color[0] + "," + _color[1] + "," + _color[2] + " ) -- THRESHOLDING.");
			if (_color[0] > 1.0f)
				_color[0] = 1.0f;
			if (_color[1] > 1.0f)
				_color[1] = 1.0f;
			if (_color[2] > 1.0f)
				_color[2] = 1.0f;
		}

		if (buffering == false) {
			Buffer0[_x][_y][0] = _color[0];
			Buffer0[_x][_y][1] = _color[1];
			Buffer0[_x][_y][2] = _color[2];
		} else {
			if (activeIndex == 0) {
				Buffer0[_x][_y][0] = _color[0];
				Buffer0[_x][_y][1] = _color[1];
				Buffer0[_x][_y][2] = _color[2];
			} else {
				Buffer1[_x][_y][0] = _color[0];
				Buffer1[_x][_y][1] = _color[1];
				Buffer1[_x][_y][2] = _color[2];
			}
		}
	}

	public float[][][] getCurrentBuffer() {
		if (activeIndex == 0 || buffering == false) {
			return Buffer0;
		} else {
			return Buffer1;
		}
	}

}
