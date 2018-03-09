package graphalgorithms;
import java.util.ArrayList;

import network.Node;


public class Message  {
	private ArrayList<Node> visitedNodelist = new ArrayList<Node>(); //record the node list that the current message wivist
	private Node currentNode = null; //the current node that the message is associated

	public Message() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public Message(Node currentNode) {
		super();
		this.currentNode = currentNode;
		this.visitedNodelist.add(currentNode);
	}

	
	public ArrayList<Node> getVisitedNodelist() {
		return visitedNodelist;
	}
	public void setVisitedNodelist(ArrayList<Node> visitedNodelist) {
		this.visitedNodelist = visitedNodelist;
	}
	public Node getCurrentNode() {
		return currentNode;
	}
	public void setCurrentNode(Node currentNode) {
		this.currentNode = currentNode;
	}

}
