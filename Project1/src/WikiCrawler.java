/**
*
* @author Zhen Zhao, Jichuan Zhang
*/

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.ArrayDeque;;


public class WikiCrawler {

	static final String BASE_URL = " https://en.wikipedia.org";
	String Seed;
	int Max;

	String[] Topics;
	String Output;

	Graph WebGraph;
	
	public WikiCrawler(String seed, int max, String[] topics, String output)
	{
		Seed = seed;
		Max = max;
		Topics = topics;
		Output = output;

		WebGraph = new Graph();
		
	}
	
	public ArrayList<String> extractLinks(String document)
	{
		ArrayList<String> result = new ArrayList<String>();
		
		int linkIndex = document.indexOf("<P>");
		if(linkIndex == -1)
			linkIndex = document.indexOf("<p>");
				
		if(linkIndex == -1)
			return result;
		
		int linkEndIndex;
		String possibleLink;

		while(true)
		{
			linkIndex = document.indexOf("href=\"/wiki/", linkIndex);
			if(linkIndex == -1)
				break;
			linkIndex +=6;
			linkEndIndex = document.indexOf("\"", linkIndex);
			possibleLink = document.substring(linkIndex, linkEndIndex);


			if(!possibleLink.contains("#") && !possibleLink.contains(":"))
			{
				result.add(possibleLink);
			}
		}
		
		HashSet<String> temp = new HashSet<String>();
		for(int i=0; i<result.size(); i++)
		{
			if(temp.contains(result.get(i)))
			{
				result.remove(i);
				i--;
			
			}
			else {
				temp.add(result.get(i));
			}
				
		}
	
		return result;
	}
	
	public void crawl(boolean focused)
	{
		if(Max == 0)
		{
			outputResult();
			return;
		}
		
		if(focused && Topics.length != 0)
			focusedCrawl();
		else
			unfocusedCrawl();
		
		outputResult();
	}
	

	
	private void focusedCrawl()
	{
		PriorityQ queue = new PriorityQ();
		int count = 0;
		
		HtmlParser parser = new HtmlParser("", Topics);
		parser.Link = BASE_URL + Seed;
		
		if(!parser.getHtmlContent())
			return;
		
		count = checkLoadLimit(count);
		parser.CaulateRelevance();
		
		if(!parser.ContainAll)
			return;
		
		WebGraph.root = WebGraph.AddNode(Seed, parser.Relevancy);
		ArrayList<String> links = extractLinks(parser.Content);

		queue.add(Seed + " " + String.join(" ", links), parser.Relevancy);
		
		while(WebGraph.Nodes.size()<Max && !queue.isEmpty())
		{
			String info[] = queue.extractMax().split(" ");
			String current = info[0];
			
			Node currentNode = WebGraph.Nodes.get(current);
			
			for(int i=1; i<info.length && WebGraph.Nodes.size()<Max; i++)
			{
				String child = info[i];
				Node childNode = WebGraph.Nodes.get(child);
				
				
				if(childNode == null)
				{
					parser.Link = BASE_URL + child;
					
					count = checkLoadLimit(count);
					if(!parser.getHtmlContent())
						continue;
					parser.CaulateRelevance();
					if(!parser.ContainAll)
						continue;
					
					links = extractLinks(parser.Content);
					queue.add(child  + " " + String.join(" ", links), parser.Relevancy);
					childNode = WebGraph.AddNode(child, parser.Relevancy);
				}
				
				currentNode.AddEdge(childNode);
			}
		}
		int amount = WebGraph.Nodes.size();
		System.out.println(amount);
	}
	
	
	private int checkLoadLimit(int count)
	{
		if(count == 20)
		{
			try {
				Thread.sleep(3000);
			} catch (Exception ignore) {}
			
			return 0;
		}
		
		return count + 1;
	}

	private void unfocusedCrawl()
	{
		ArrayDeque<String> queue = new ArrayDeque<String>();
		
		HtmlParser parser = new HtmlParser("", Topics);
		
		queue.add("");
		queue.add(Seed);
		int count = 0;
		
		while(!queue.isEmpty())
		{
			String from = queue.poll();
			String current = queue.poll();
						
			Node parent = WebGraph.Nodes.get(from);
			int value = 0;
			
			if(parent!=null)
				value = parent.OutgoingEdges.size();
			

			
			Node newNode = WebGraph.Nodes.get(current);
			if(newNode == null)
			{	
				if(WebGraph.Nodes.size() < Max)
					newNode = WebGraph.AddNode(current, value);
				else
					continue;

			}

			
			if(parent!=null)
				parent.AddEdge(newNode);
			else
				WebGraph.root = newNode;
			
			
			if(count++ == 20)
			{
				try {
					Thread.sleep(3000);
				} catch (Exception ignore) {}
				
				count = 0;
			}
			
			parser.Link = BASE_URL + current;
			if (!parser.getHtmlContent())
				continue;
			
			if(!parser.checkTopics())
				continue;
			

			
			ArrayList<String> links = extractLinks(parser.Content);
			for(int i=0; i<links.size(); i++)
			{

				if(!WebGraph.Nodes.containsKey(links.get(i)))
				{
					queue.add(current);
					queue.add(links.get(i));
				}
				else
				{
					Node currentNode = WebGraph.Nodes.get(current);
					Node oldNode = WebGraph.Nodes.get(links.get(i));
					currentNode.AddEdge(oldNode);
				}
			}
		}
	}
	
	
	private void outputResult()
	{
		try {
			ArrayDeque<String> result = BFS();
			File file = new File(Output);
			PrintWriter pw = new PrintWriter(file);
			pw.println(Max);
			while(!result.isEmpty())
			{
				pw.println(result.poll() + " " + result.poll());
			}
			pw.close();
		}
		catch (FileNotFoundException e) {
			System.out.println("The file " + Output + " was not found.");
		}
	}
	
	private ArrayDeque<String> BFS() 
	{
		ArrayDeque<String> result = new ArrayDeque<String>();
		ArrayDeque<String> wait = new ArrayDeque<String>();
		HashSet<String> Discovered = new HashSet<String>();
		
		wait.add(Seed);

		while(!wait.isEmpty())
		{
			Node node = WebGraph.Nodes.get(wait.poll());
			if (node == null) {
				return result;
			}
			if(Discovered.contains(node.Name))
				continue;
			
			Discovered.add(node.Name);

			for(String child: node.OutgoingEdges.keySet())
			{
				if(node.Name.equals(child))
					continue;
				
				result.add(node.Name);
				result.add(child);
				
				if(Discovered.contains(child))
					continue;

				wait.add(child);

			}
		}
		
		return result;
	}
	
	private class HtmlParser
	{
		String Link;
		String Content;
		String[] Topics;
		int Relevancy = 0;
		boolean ContainAll = true;
		
		HtmlParser(String link, String[] topics)
		{
			Link = link;
			Topics = topics;
			if (link.length() == 0 || !getHtmlContent())
			{
				Content = "";
				return;
			}
		}
		
		private boolean getHtmlContent()
		{
			try {
				URL url = new URL(Link);
				BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
				Content = reader.lines().collect(Collectors.joining());
			} catch (MalformedURLException e) {
				return false;
			} catch (IOException e) {
				return false;
			}
			return true;
		}
		
		private int CaulateRelevance() 
		{
			ContainAll = true;
			int WordIndex;
			String content = Content.toLowerCase();
			for(int i=0; i< Topics.length; i++)
			{
				WordIndex = 0;
				boolean found = false;
				while(true)
				{
					WordIndex = content.indexOf(Topics[i].toLowerCase(), WordIndex);
					if(WordIndex == -1)
					{
						if(!found)
							ContainAll = false;
						break;
					}
					found = true;
					WordIndex +=Topics[i].length();
					Relevancy++;
				}
			}
			
			return Relevancy;
		}
		
		private boolean checkTopics() {
			ContainAll = true;
			
			String content = Content.toLowerCase();
			for (int i = 0; i < Topics.length; i++) {
				
				if (!content.contains(Topics[i].toLowerCase())) {
					ContainAll = false;
					break;
				}
			}

			return ContainAll;
		}
	}
}
