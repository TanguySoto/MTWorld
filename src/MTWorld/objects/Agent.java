// created by nicolas.bredeche(at)upmc.fr
// date of creation: 2013-1-12

package MTWorld.objects;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import MTWorld.worlds.*;

abstract public class Agent
{
	protected int x, y;
	
	protected int orient;
	protected int state;
	
	protected boolean isAlive;
	
	protected World world;

	public Agent(int __x, int __y, World __world) {
		this.world = __world;
		this.x = __x;
		this.y = __y;
		state=0;
		orient=0;
	}

	abstract public void step(int index);

	public int[] getCoordinate() {
		int coordinate[] = new int[2];
		coordinate[0] = this.x;
		coordinate[1] = this.y;
		return coordinate;
	}
	
	public int getState() {
		return state;
	}
	
	public int getOrient() {
		return orient;
	}

	abstract public void displayObjectAt(World myWorld, GL2 gl,GLU glu,
			double height, double width);

}
