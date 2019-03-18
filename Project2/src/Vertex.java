import java.util.LinkedHashMap;

/**
*
* @author Zhen Zhao, Jichuan Zhang
*/
public class Vertex
{
	public int[] data;
	int dist;
	Vertex prev;
	int x;
	int y;

	LinkedHashMap<Vertex, Integer> IncomingEdges;
	LinkedHashMap<Vertex, Integer> OutgoingEdges;
	public Vertex()
	{
		dist = Integer.MAX_VALUE;
		data = new int[3];
		IncomingEdges = new LinkedHashMap<Vertex, Integer>();
		OutgoingEdges = new LinkedHashMap<Vertex, Integer>();
	}
	
	public void addEdge(Vertex des, int weight)
	{
		if (OutgoingEdges.containsKey(des))
		{
			return;
		}
		OutgoingEdges.put(des, weight);
		des.IncomingEdges.put(this, weight);
	}
	
	public int getdist()
	{
		return dist;
	}
}
