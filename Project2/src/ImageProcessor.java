import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

/**
*
* @author Zhen Zhao, Jichuan Zhang
*/
public class ImageProcessor {

	ImageProcessor(String FName)
	{
		if(!readFile(FName))
		{
			System.out.println("Incorrect File Format");
			return;
		}
		constructEdges(graph);
	}
	
	WGraph graph;
	
	void constructEdges(WGraph graph)
	{

		ArrayList<ArrayList<Integer>> I = getImportance();
		
		for(int i=0; i<graph.map.length -1; i++)
		{
			for(int j=0; j<graph.map[0].size(); j++)
			{
				if(graph.map[0].size() == 1)
					break;
				int firstLineWeight = 0;
				if(i==0)
				{
					firstLineWeight += I.get(i).get(j).intValue();
				}
				
				if(j == 0)
				{
					graph.map[i].get(j).addEdge(graph.map[i+1].get(j+1), I.get(i+1).get(j+1).intValue()+ firstLineWeight);
					
				}
				else if(j == graph.map[i].size()-1)
				{
					graph.map[i].get(j).addEdge(graph.map[i+1].get(j-1), I.get(i+1).get(j-1).intValue()+ firstLineWeight);
				}
				else
				{
					graph.map[i].get(j).addEdge(graph.map[i+1].get(j+1), I.get(i+1).get(j+1).intValue()+ firstLineWeight);
					graph.map[i].get(j).addEdge(graph.map[i+1].get(j-1), I.get(i+1).get(j-1).intValue()+ firstLineWeight);
				}
				graph.map[i].get(j).addEdge(graph.map[i+1].get(j), I.get(i+1).get(j).intValue()+ firstLineWeight);
			}
		}
	}
	
	
	
	boolean readFile(String FName)
	{
		File file = new File(FName);
		Scanner scan;
		try {
			scan = new Scanner(file);
		} catch (FileNotFoundException e) {

			return false;
		}
		
		if(!scan.hasNext())
		{
			scan.close();
			return false;
		}

		int height = Integer.parseInt(scan.nextLine());
		if(!scan.hasNext())
		{
			scan.close();
			return false;
		}
		int width = Integer.parseInt(scan.nextLine());
		graph = new WGraph(width, height);
		Scanner lineScan;

		int y=0;
		while(scan.hasNextLine())
		{
			String line = scan.nextLine();
			lineScan = new Scanner(line);
			lineScan.useDelimiter(" ");
			int i=0;
			
			while(lineScan.hasNextInt())
			{
				graph.map[y].get(i/3).data[i%3] = lineScan.nextInt();
				i++;
			}
			y++;
		}
		
		scan.close();
		return true;
	}
	
	ArrayList<ArrayList<Integer>> getImportance()
	{
		return getIfromGraph(graph);
	}
	
	ArrayList<ArrayList<Integer>> getIfromGraph(WGraph graph)
	{
		ArrayList<ArrayList<Integer>> I = new ArrayList<ArrayList<Integer>>();
		for(int i=0; i<graph.map.length; i++)
		{
			I.add(new ArrayList<Integer>());
			for(int j=0; j<graph.map[i].size() ; j++)
			{
				Vertex yV1 = (i == 0) ? graph.map[graph.map.length-1].get(j) : graph.map[i-1].get(j); 
				Vertex yV2 = (i == graph.map.length-1) ? graph.map[0].get(j) : graph.map[i + 1].get(j); 
				
				Vertex xV1 = (j == 0) ? graph.map[i].get(graph.map[0].size() -1) : graph.map[i].get(j - 1); 
				Vertex xV2 = (j == graph.map[0].size()-1) ? graph.map[i].get(0) : graph.map[i].get(j+1); 
				I.get(I.size()-1).add(Integer.valueOf(PDist(xV1, xV2) + PDist(yV1, yV2)));
			}
		}
		
		return I;
	}
	
	int PDist(Vertex a, Vertex b)
	{
		int result = 0;
		for(int i=0; i<3; i++)
		{
			result += Math.pow(a.data[i] - b.data[i], 2);
		}
		return result;
	}
	
	void writeReduced(int k, String FName)
	{

		ArrayList<Integer> head = new ArrayList<Integer>();
		ArrayList<Integer> tail = new ArrayList<Integer>();
		for(int i=0; i<graph.map[0].size(); i++)
		{
			head.add(Integer.valueOf(i));
			head.add(Integer.valueOf(0));
			tail.add(Integer.valueOf(i));
			tail.add(Integer.valueOf(graph.map.length-1));
		}
		for(int i=0; i< k; i++)
		{
			ArrayList<Integer> cut = graph.S2S(head, tail);
			System.out.println(cut);
			head.remove(head.size()-1);
			head.remove(head.size()-1);
			tail.remove(tail.size()-1);
			tail.remove(tail.size()-1);
			
//			for(int j=0; j<cut.size(); j+=2)
//			{
//				for(Vertex src:graph.map[cut.get(j+1).intValue()].get(cut.get(j).intValue()).IncomingEdges.keySet())
//				{
//					src.OutgoingEdges.remove(graph.map[cut.get(j+1).intValue()].get(cut.get(j).intValue()));
//				}
//			
//			}
//			
	
			for(int j=0; j<cut.size(); j+=2)
			{
				graph.map[cut.get(j+1).intValue()].remove(cut.get(j).intValue());		
				
			}
			for(int j=0; j<graph.map.length; j++)
			{
				for(int z=0; z<graph.map[j].size(); z++)
				{
					graph.map[j].get(z).IncomingEdges.clear();
					graph.map[j].get(z).OutgoingEdges.clear();
				}
			}
			

			constructEdges(graph);
		}
		OutputGraph("output.txt");
		readFile(FName);
		constructEdges(graph);
	}
	
	void OutputGraph(String FName)
	{
		PrintWriter writer;
		try {
			writer = new PrintWriter(FName);
			writer.println(graph.map.length);
			writer.println(graph.map[0].size());
			for (int i = 0; i < graph.map.length; i++)
			{
				for (int j = 0; j < graph.map[i].size(); j++)
				{
					for (int k = 0; k < graph.map[i].get(j).data.length; k++)
					{
						writer.print(graph.map[i].get(j).data[k] + " ");
					}
				}
				writer.println();
			}
			writer.close();
		} catch (FileNotFoundException e) {
			System.out.println("File Error");
		}

	}
}
