/**
*
* @author Zhen Zhao, Jichuan Zhang
*/
import java.util.ArrayList;

public class PriorityQ {
	
	private class Pair {
		private String string;
		private int key;
		private Pair(String string, int key) {
			this.string = string;
			this.key = key;
		}
	}
	
	private ArrayList<Pair> data;
	
	// constructs an empty priority queue.
	public PriorityQ() {
		data = new ArrayList<Pair>();
	}
	
	// Adds a strings with priority p to the priority queue.
	public void add(String s, int p) {
		data.add(new Pair(s, p));
		shiftUp(data.size() - 1);
	}

	// returns a string whose priority is maximum.
	public String returnMax() {
		return data.get(0).string;
	}
	
	//returns a string whose priority is maximum and removes it from the priority queue.
	public String extractMax() {
		String max = data.get(0).string;
		remove(0);
		return max;
	}
	
	// removes the element from the priority queue whose array index is i.
	public void remove(int i) {
		data.set(i, data.get(data.size() - 1));
		data.remove(data.size() - 1);
		shiftDown(i);
	}
	
	// Decrements the priority of the ith element by k.
	public void decrementPriority(int i, int k) {
		data.get(i).key = data.get(i).key - k;
		shiftDown(i);
	}
	
	// returns an array B with the following property:B[i] = key(A[i]) for all i in the array A used to implement the priority queue.
	public ArrayList<Integer> priorityArray() {
		ArrayList<Integer> key = new ArrayList<Integer>();
		for (int i = 0; i < data.size(); i += 1) {
			key.add(data.get(i).key);
		}
		return key;
	}
	
	// Returns key(A[i]), where A is the array used to represent the priority queue
	public int getKey(int i) {
		return data.get(i).key;
	}
	
	// Returns value(A[i]), where A is the array used to represent the priority queue
	public String getValue(int i) {
		return data.get(i).string;
	}
	
	// Return true if and only if the queue is empty.
	public boolean isEmpty() {
		return data.size() == 0;
	}
	
	private void shiftUp(int nodeIndex) {
		int parentIndex;
		Pair tmp;
		if (nodeIndex != 0) {
			parentIndex = (nodeIndex - 1) / 2;
			if (data.get(nodeIndex).key > data.get(parentIndex).key) {
				tmp = data.get(parentIndex);
				data.set(parentIndex, data.get(nodeIndex));
				data.set(nodeIndex, tmp);
				shiftUp(parentIndex);
			}	
		}
	}
	
	private void shiftDown(int nodeIndex) {
		int child1, child2;
		Pair tmp;
		while (2 * nodeIndex + 1 < data.size()) {
			child1 = 2 * nodeIndex + 1;
			child2 = 2 * nodeIndex + 2;

			if (child2 < data.size()) {
				if (data.get(nodeIndex).key > data.get(child1).key && data.get(nodeIndex).key > data.get(child2).key) {
					break;
				}
				else {
					if (data.get(child1).key < data.get(child2).key) {
						tmp = data.get(child2);
						data.set(child2, data.get(nodeIndex));
						data.set(nodeIndex, tmp);
						nodeIndex = child2;
					}
					else {
						tmp = data.get(child1);
						data.set(child1, data.get(nodeIndex));
						data.set(nodeIndex, tmp);
						nodeIndex = child1;
					}
				}		
			}
			else {
				if (data.get(nodeIndex).key > data.get(child1).key) {
					break;
				}
				else {
					tmp = data.get(child1);
					data.set(child1, data.get(nodeIndex));
					data.set(nodeIndex, tmp);
					nodeIndex = child1;
				}
			}
		}
	}
}