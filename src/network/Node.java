package network;

import java.util.ArrayList;

import general.CommonObject;
import general.Constant;
 

public class Node extends CommonObject{
	
	private Layer associatedLayer = null; //the layer that the node is associated with
	private ArrayList<Node> neinodelist = null; //list of neighbor nodes	 
	private int x = 0;//x-coordinate
	private int y = 0;//y-coordinate
	private double cost_from_src = 10000000;//used search algorithm to get he cost from the src node
	private int hop_from_src = 100000000; //used in search algorithm to get the number of hops from the src node
	private int status = Constant.UNVISITED; //not visited yet
	private Node parentNode = null;//parent node of the current node
	private int regnum=0; //表示节点上再生器的个数
	
	public Node(String name, int index, String comments, Layer associatedLayer,	int x, int y) {
		super(name, index, comments);
		this.associatedLayer = associatedLayer;
		this.neinodelist = new ArrayList<Node>();
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	
	public int getregnum() {
		return regnum;
	}
	public void setregnum(int regnum) {
		this.regnum = regnum;
	}
	
	public Node getParentNode() {
		return parentNode;
	}
	public void setParentNode(Node parentNode) {
		this.parentNode = parentNode;
	}
	public int getHop_from_src() {
		return hop_from_src;
	}
	public void setHop_from_src(int hop_from_src) {
		this.hop_from_src = hop_from_src;
	}
	public double getCost_from_src() {
		return cost_from_src;
	}
	public void setCost_from_src(double cost_from_src) {
		this.cost_from_src = cost_from_src;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public Layer getAssociatedLayer() {
		return associatedLayer;
	}
	public void setAssociatedLayer(Layer associatedLayer) {
		this.associatedLayer = associatedLayer;
	}
	public ArrayList<Node> getNeinodelist() {
		return neinodelist;
	}
	public void setNeinodelist(ArrayList<Node> neinodelist) {
		this.neinodelist = neinodelist;
	}
	
	/**
	 * add neighbor node
	 */
	public void addNeiNode(Node node){
		this.neinodelist.add(node);
	}
	/**
	 * remove neighbor node from the neinode list by index
	 */
	public void removeNeiNode(int index){
		for(int i=0;i<this.neinodelist.size();i++){
			if(this.neinodelist.get(i).getIndex() == index){
				this.neinodelist.remove(this.neinodelist.get(i));
				break;
			}
		}			
	}
	
	/**
	 * remove neighbor node from the neinode list by name
	 */
	public void removeNeiNode(String name){
		for(int i=0;i<this.neinodelist.size();i++){
			if(this.neinodelist.get(i).getName().equals(name)){
				this.neinodelist.remove(this.neinodelist.get(i));
				break;
			}
		}			
	}
	/**
	 * remove neighbor node from the neinode list
	 */
	public void removeNeiNode(Node node){
		this.getNeinodelist().remove(node);
	}		   
	/**
	 * get degree of the node
	 */
	public int getDegree(){
		return this.neinodelist.size();
	}
 		
}



