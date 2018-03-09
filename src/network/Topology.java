package network;

import general.CommonObject;

import java.util.ArrayList;

public class Topology extends CommonObject{
	
	private ArrayList<Node> nodelist = null;//list of nodes
	private ArrayList<Link> linklist = null;//list of links
	private Layer  associatedLayer = null;//the layer that the topology created for
	
	public ArrayList<Node> getNodelist() {
		return nodelist;
	}
	public void setNodelist(ArrayList<Node> nodelist) {
		this.nodelist = nodelist;
	}
	public ArrayList<Link> getLinklist() {
		return linklist;
	}
	public void setLinklist(ArrayList<Link> linklist) {
		this.linklist = linklist;
	}
	public Layer getAssociatedLayer() {
		return associatedLayer;
	}
	public void setAssociatedLayer(Layer associatedLayer) {
		this.associatedLayer = associatedLayer;
	}
	public Topology(String name, int index, String comments,
			Layer associatedLayer) {
		super(name, index, comments);
		this.nodelist = new ArrayList<Node>();
		this.linklist = new ArrayList<Link>();
		this.associatedLayer = associatedLayer;
	}
	
	

}

