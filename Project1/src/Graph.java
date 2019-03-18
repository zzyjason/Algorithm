/**
*
* @author Zhen Zhao, Jichuan Zhang
*/
import java.util.HashMap;

public class Graph {

	HashMap<String, Node> Nodes;
	Node root;
	
	Graph()
	{
		Nodes = new HashMap<String, Node>();
	}
	
	Node AddNode(String key, int value)
	{
		Node result = new Node(key, value);
		Nodes.put(key, result);
		
		return result;
	}
}


