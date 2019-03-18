import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Scanner;

/**
*
* @author Zhen Zhao, Jichuan Zhang
*/
public class WGraph
{
	private String filename;
	public ArrayList<Vertex>[] map; 
	private int maprow = 0;
	private int mapcol = 0;
	
	public WGraph(String FName) 
	{
		filename = FName;
		mapbound();
		constructmap();
		addVertex();
	}
	
	public WGraph(int x, int y) 
	{
		maprow = y;
		mapcol = x;
		constructmap();
	}
	
	public ArrayList<Integer> V2V(int ux, int uy, int vx, int vy)
	{
		ArrayList<Integer> S1 = new ArrayList<Integer>();
		S1.add(Integer.valueOf(ux));
		S1.add(Integer.valueOf(uy));
		ArrayList<Integer> S2 = new ArrayList<Integer>();
		S2.add(Integer.valueOf(vx));
		S2.add(Integer.valueOf(vy));
		return S2S(S1, S2);
	}
	
	public ArrayList<Integer> V2S(int ux, int uy, ArrayList<Integer> S2)
	{
		ArrayList<Integer> S1 = new ArrayList<Integer>();
		S1.add(Integer.valueOf(ux));
		S1.add(Integer.valueOf(uy));
		
		return S2S(S1, S2);
	}
	
	public ArrayList<Integer> S2S(ArrayList<Integer> S1, ArrayList<Integer> S2)
	{
		for(int i=0; i<map.length; i++)
		{
			for(int j=0; j<map[i].size(); j++)
			{
				map[i].get(j).dist = Integer.MAX_VALUE;
				map[i].get(j).prev = null;
			}
		}
		
		for(int i=0; i<S1.size(); i+=2)
		{
			map[S1.get(i+1).intValue()].get(S1.get(i).intValue()).dist = 0;
		}
		
		PriorityQueue<Vertex> queue = new PriorityQueue<>(maprow * mapcol, new VertexComparator());
		
		for(int i=0; i<map.length; i++)
		{
			for(int j=0; j<map[i].size(); j++)
			{
				queue.add(map[i].get(j));
			}
		}
		
		for(int i=0; i<map.length; i++)
		{
			for(int j=0; j<map[i].size(); j++)
			{
				map[i].get(j).x = j;
				map[i].get(j).y = i;
			}
		}
		
		while(!queue.isEmpty()) 
		{
			Vertex current = queue.poll();

			for(Vertex des:current.OutgoingEdges.keySet())
			{
				if(!queue.contains(des))
					continue;
				
				int alt = current.dist + current.OutgoingEdges.get(des).intValue();
				if(alt < des.dist)
				{
					des.dist = alt;
					des.prev = current;
					queue.remove(des);
					queue.add(des);
				}
			}
		}
		
		Vertex des = new Vertex();
		for(int i=0; i<S2.size(); i+=2)
		{
			if(des.dist > map[S2.get(i+1).intValue()].get(S2.get(i)).dist)
			{
				des = map[S2.get(i+1).intValue()].get(S2.get(i));
			}
		}

		
		ArrayList<Integer> result = new ArrayList<Integer>();
		Vertex current = des;
		do {
			result.add(0, current.y);
			result.add(0, current.x);
			current = current.prev;
		}while(current != null);
		return result;
	}
		
	class VertexComparator implements Comparator<Vertex>
	{
		public int compare(Vertex s1, Vertex s2) 
		{ 
            if (s1.dist > s2.dist) 
                return 1; 
            else if (s1.dist < s2.dist) 
                return -1; 
            return 0; 
        } 
	}
	
	//construct a map
	@SuppressWarnings("unchecked")
	private void constructmap()
	{
		map = (ArrayList<Vertex>[]) new ArrayList<?>[maprow];
		for(int i = 0; i < map.length; i++)
		{
			map[i] = new ArrayList<Vertex>();
			for(int j = 0; j < mapcol; j++)
			{
				map[i].add(new Vertex());
			}
		}
	}
	
	//add vertex into map
	private void addVertex()
	{
		File file = new File(filename);
		try 
		{	
			Scanner sc = new Scanner(file);
			sc.nextLine();
			sc.nextLine();
			while(sc.hasNextLine())
			{
				int colS = sc.nextInt();
				int rowS = sc.nextInt();
				int colD = sc.nextInt();
				int rowD = sc.nextInt();
				int weight = sc.nextInt();
				map[rowS].get(colS).addEdge(map[rowD].get(colD), weight);
				//sc.nextLine();
			}			
			sc.close();
		}
		catch(FileNotFoundException e)
		{
			System.out.println("File Error");
		}
	}
	
	// get map's row and col
	private void mapbound()
	{
		File file = new File(filename);
		try 
		{	
			Scanner sc = new Scanner(file);
			sc.nextLine();
			sc.nextLine();
			while(sc.hasNextLine())
			{
				int i = 0;
				while (i < 2)
				{
					int tmpcol = sc.nextInt();
					int tmprow = sc.nextInt();
					if (maprow < tmprow)
					{
						maprow = tmprow;
					}
					if (mapcol < tmpcol)
					{
						mapcol = tmpcol;
					}
					i++;
				}
				sc.nextInt();
			}		
			maprow++;
			mapcol++;
			sc.close(); 
		}
		catch(FileNotFoundException e)
		{
			System.out.println("File Error");
		}
	}
}

