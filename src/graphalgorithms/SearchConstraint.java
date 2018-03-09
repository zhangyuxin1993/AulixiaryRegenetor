package graphalgorithms;

import java.util.ArrayList;
import network.*;

public class SearchConstraint {
	private ArrayList<Node> excludedNodelist = null;//excluded node list
	private ArrayList<Link> excludedLinklist = null;//exclueded link list
	private double max_length = 10000000; //max-length 
	private int max_hop = 10000000;//max hops
	
	public ArrayList<Node> getExcludedNodelist() {
		return excludedNodelist;
	}
	public void setExcludedNodelist(ArrayList<Node> excludedNodelist) {
		this.excludedNodelist = excludedNodelist;
	}
	public ArrayList<Link> getExcludedLinklist() {
		return excludedLinklist;
	}
	public void setExcludedLinklist(ArrayList<Link> excludedLinklist) {
		this.excludedLinklist = excludedLinklist;
	}
	public double getMax_length() {
		return max_length;
	}
	public void setMax_length(double max_length) {
		this.max_length = max_length;
	}	
	public int getMax_hop() {
		return max_hop;
	}
	public void setMax_hop(int max_hop) {
		this.max_hop = max_hop;
	}
	
	public SearchConstraint(double max_length) {
		super();
		this.excludedNodelist = new ArrayList<Node>();
		this.excludedLinklist = new ArrayList<Link>();
		this.max_length = max_length;
	}
	public SearchConstraint(double max_length, int max_hop) {
		super();
		this.excludedNodelist = new ArrayList<Node>();
		this.excludedLinklist = new ArrayList<Link>();
		this.max_length = max_length;
		this.max_hop = max_hop;
	}
	
		
}


