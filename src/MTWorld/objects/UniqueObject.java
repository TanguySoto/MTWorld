// created by nicolas.bredeche(at)upmc.fr
// date of creation: 2013-1-12

package MTWorld.objects;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import MTWorld.worlds.World;

abstract public class UniqueObject // UniqueObject are object defined with particular, unique, properties (ex.: particular location)
{
	protected int x,y;
	protected World world;
	
	public UniqueObject(int __x, int __y, World __world) 
	{
		this.world = __world;
		this.x = __x;
		this.y = __y;
	}
	
	public int[] getCoordinate()
	{
		int coordinate[] = new int[2];
		coordinate[0] = this.x;
		coordinate[1] = this.y;
		return coordinate;
	}
	
	abstract public void displayUniqueObject(World myWorld,GLU glu, GL2 gl,
			double height, double width);

}
