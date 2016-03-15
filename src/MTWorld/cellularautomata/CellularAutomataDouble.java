// created by nicolas.bredeche(at)upmc.fr
// date of creation: 2013-1-12

package MTWorld.cellularautomata;

public class CellularAutomataDouble extends CellularAutomata {

	protected double Buffer0[][];
	protected double Buffer1[][];

	public CellularAutomataDouble(int _dx, int _dy, boolean _buffering) {
		super(_dx, _dy, _buffering);

		Buffer0 = new double[_dx][_dy];
		Buffer1 = new double[_dx][_dy];

		for (int x = 0; x != dx; x++) {
			for (int y = 0; y != dy; y++) {
				Buffer0[x][y] = 0;
				Buffer1[x][y] = 0;
			}
		}
	}

	public double getCellState(int _x, int _y) {
		checkBounds(_x, _y);

		double value;

		if (buffering == false) {
			value = Buffer0[_x][_y];
		} else {
			if (activeIndex == 1) // read old buffer
			{
				value = Buffer0[_x][_y];
			} else {
				value = Buffer1[_x][_y];
			}
		}

		return value;
	}

	public void setCellState(int _x, int _y, double _value) {
		checkBounds(_x, _y);

		if (buffering == false) {
			Buffer0[_x][_y] = _value;
		} else {
			if (activeIndex == 0) // write new buffer
			{
				Buffer0[_x][_y] = _value;
			} else {
				Buffer1[_x][_y] = _value;
			}
		}
	}

	public double[][] getCurrentBuffer() {
		if (activeIndex == 0 || buffering == false) {
			return Buffer0;
		} else {
			return Buffer1;
		}
	}

}
