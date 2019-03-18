/**
*
* @author Zhen Zhao, Jichuan Zhang
*/
import java.util.LinkedHashMap;

public class Node {

	
	String Name;
	int Value;
	LinkedHashMap<String,Node> IncomingEdges;
	LinkedHashMap<String,Node> OutgoingEdges;
	
	public Node(String name, int value)
	{
		Name = name;
		Value = value;
		IncomingEdges = new LinkedHashMap<String,Node>();
		OutgoingEdges = new LinkedHashMap<String,Node>();
	}
	
	public void AddEdge(Node node)
	{
		if(node.Name.equals(Name))
			return;
		
		OutgoingEdges.put(node.Name, node);
		node.IncomingEdges.put(Name, this);
	}
}
