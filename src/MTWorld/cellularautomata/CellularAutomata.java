// created by nicolas.bredeche(at)upmc.fr
// date of creation: 2013-1-12

package MTWorld.cellularautomata;

public abstract class CellularAutomata {

	protected int dx;
	protected int dy;

	boolean buffering;

	int activeIndex;

	public CellularAutomata(int _dx, int _dy, boolean __buffering) {
		dx = _dx;
		dy = _dy;

		buffering = __buffering;

		activeIndex = 0;
	}

	public void checkBounds(int _x, int _y) {
		if (_x < 0 || _x > dx || _y < 0 || _y > dy) {
			System.err.println("[error] out of bounds (" + _x + "," + _y + ")\n-> dx = "+dx+" ; dy = "+dy);
			System.exit(-1);
		}
	}

	public int getWidth() {
		return dx;
	}

	public int getHeight() {
		return dy;
	}

	public void init() {
		// ...
	}

	public void step() {
		if (buffering) {
			swapBuffer();
		}
	}

	public void swapBuffer() // should be used carefully (except for initial step)
	{
		activeIndex = (activeIndex + 1) % 2;
	}

}
