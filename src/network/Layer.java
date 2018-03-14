package network;

import general.CommonObject;
import general.file_out_put;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import MainFunction.MainOfAulixiaryRegenetor;
import demand.TrafficDemand;

import subgraph.Cycle;

public class Layer extends CommonObject{
	
	private Network associateNetwork = null;//the network that the layer associated to
	private ArrayList<Topology> topolist = null;//list of topology; the first topology is the current topology by default
	private HashMap<String, Node> nodelist = null; //list of nodes within the layer
	private HashMap<String, Link> linklist =null;//list of links within the layer
	private HashMap<String, NodePair> nodepairlist =null;//list of node pairs within the layer
	private Layer serverlayer = null; //the server layer of the current layer
	private Layer clientlayer = null; //the client layer of the current layer
	private ArrayList<Link> ArrLinklist =null;
	
	public ArrayList<Topology> getTopolist() {
		return topolist;
	}
	public void setTopolist(ArrayList<Topology> topolist) {
		this.topolist = topolist;
	}		
	
	public Layer getServerlayer() {
		return serverlayer;
	}
	public void setServerlayer(Layer serverlayer) {
		this.serverlayer = serverlayer;
	}
	public Layer getClientlayer() {
		return clientlayer;
	}
	public void setClientlayer(Layer clientlayer) {
		this.clientlayer = clientlayer;
	}
	public Network getAssociateNetwork() {
		return associateNetwork;
	}
	public void setAssociateNetwork(Network associateNetwork) {
		this.associateNetwork = associateNetwork;
	}
	public HashMap<String, Node> getNodelist() {
		return nodelist;
	}
	public void setNodelist(HashMap<String, Node> nodelist) {
		this.nodelist = nodelist;
	}
	public HashMap<String, Link> getLinklist() {
		return linklist;
	}
	public void setLinklist(HashMap<String, Link> linklist) {
		this.linklist = linklist;
	}
	public HashMap<String, NodePair> getNodepairlist() {
		return nodepairlist;
	}
	public void setNodepairlist(HashMap<String, NodePair> nodepairlist) {
		this.nodepairlist = nodepairlist;
	}
	public ArrayList<Link> getArrLinklist() {
		return ArrLinklist;
	}
	public void setArrLinklist(ArrayList<Link> ArrLinklist) {
		this.ArrLinklist = ArrLinklist;
	}
	
	
	public Layer(String name, int index, String comments,Network associateNetwork) {
		super(name, index, comments);
		this.associateNetwork = associateNetwork;
		this.topolist = new ArrayList<Topology>();
		this.nodelist = new HashMap<String, Node>(40);
		this.linklist = new HashMap<String, Link>(100);
		this.nodepairlist = new HashMap<String, NodePair>(800);
		this.ArrLinklist=new ArrayList<>();
	}
	
	
	public Layer(String name, int index, String comments,
			Network associateNetwork, Layer serverlayer, Layer clientlayer) {
		super(name, index, comments);
		this.associateNetwork = associateNetwork;
		this.topolist = new ArrayList<Topology>();
		this.nodelist = new HashMap<String, Node>(40);
		this.linklist = new HashMap<String, Link>(100);
		this.nodepairlist = new HashMap<String, NodePair>(800);
		this.ArrLinklist = new ArrayList<Link>();
		this.serverlayer = serverlayer;
		this.clientlayer = clientlayer;
	}
	
	
	/**
	 * add a node to the layer
	 * @param node
	 */
	public void addNode(Node node){
		this.nodelist.put(node.getName(), node);	
		node.setAssociatedLayer(this);
	}
	
	/**
	 * remove node from the layer
	 * when remove the node, we need to remove the links that source from it as well
	 */
	public void removeNode(String nodename){
		Node node = this.nodelist.get(nodename);
		node.setAssociatedLayer(null);
		//find the link list that is incident to the node and remove them
		String linkname = "";
		for(int i=0; i<node.getNeinodelist().size();i++){
			Node node2 = node.getNeinodelist().get(i);
			if(node.getIndex()<node2.getIndex()){
				linkname = node.getName()+"-"+node2.getName();
				this.linklist.remove(linkname); //remove the link from the layer
			}
			else{
				linkname = node2.getName()+"-"+node.getName();
				this.linklist.remove(linkname);
			}			
		}
		
		//remove the node itself
		this.nodelist.remove(nodename);
	}
	public int getNodepair_num(){
		 return this.nodepairlist.size();
	 }
	/**
	 * add link to the layer
	 */
	String OutFileName = MainOfAulixiaryRegenetor.OutFileName;
	public void addLink(Link link){
		this.linklist.put(link.getName(),link);
		link.setAssociatedLayer(this);
		Node nodeA=link.getNodeA();
		Node nodeB=link.getNodeB();
		nodeA.addNeiNode(nodeB);
		nodeB.addNeiNode(nodeA);
	}
	
	/**
	 * remove link from the iplayer
	 */
	public void removeLink(Link link){
		file_out_put file_io = new file_out_put();
		Link delLink=new Link(null, 0, null, this, null, null, 0, 0);
		Iterator<String> linkitor = this.linklist.keySet().iterator();
		while (linkitor.hasNext()) {
			Link remlink = (Link) (this.linklist.get(linkitor.next()));
			if(link.equals(remlink)){//取出需要删除的链路
				delLink=remlink;
			}
		}
		Node nodeA=delLink.getNodeA();
		Node nodeB=delLink.getNodeB();
		nodeA.removeNeiNode(nodeB);
		nodeB.removeNeiNode(nodeA);
		delLink.setAssociatedLayer(null);
		this.linklist.remove(delLink.getName());	
	}
	
	public void removeLink(String linkname){
		Node nodeA=this.linklist.get(linkname).getNodeA();
		Node nodeB=this.linklist.get(linkname).getNodeB();
		nodeA.removeNeiNode(nodeB);
		nodeB.removeNeiNode(nodeA);
		this.linklist.get(linkname).setAssociatedLayer(null);
		this.linklist.remove(linkname);	
	}


	
	/**
	 * add node pair to the layer
	 */
	public void addNodepair(NodePair nodepair){
		this.nodepairlist.put(nodepair.getName(), nodepair);
		nodepair.setAssociateLayer(this);
	}
	
	/**
	 * remove node pair from the layer
	 */
	public void removeNodepair(String nodepairname){
		this.nodepairlist.get(nodepairname).setAssociateLayer(null);
		this.nodepairlist.remove(nodepairname);
	}
	/**
	 * get number of nodes
	 */
	public int getNodeNum(){
		return this.nodelist.size();
	}
	
	/**
	 * get number of links
	 */
	public int getLinkNum(){
		return this.linklist.size();
	}
	/**
	 * get number of node pairs
	 */
	public int getNodepairNum(){
		return this.nodepairlist.size();
	}
	/**
	 * get number of cycles
	 */
//	public int getCycleNum(){
//		return this.cyclelist.size();
//	}
	
	/**
	 * read in physical topology from a data file csv file
	 * and meanwhile generate node pairs for each layer
	 */
	public void readTopology(String filename){
		
		String[] data = new String[10];
		File file = new File(filename);
		BufferedReader bufRdr = null;
		try {
			bufRdr = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String line = null;
		int col = 0;
		try {
			line = bufRdr.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//read the first title line
		//read each line of text file
		try {
			boolean link = false;
			while((line = bufRdr.readLine()) != null){
				StringTokenizer st = new StringTokenizer(line,",");
				while (st.hasMoreTokens()){
					data[col] = st.nextToken();
					col++;
				}
				col=0;
				String name = data[0];
				if(name.equals("Link")){
					link=true;						
				}
				if(!link)//node operation
				{
					int x = Integer.parseInt(data[1]);
					int y = Integer.parseInt(data[2]);					
					int index = this.getNodelist().size();
					Node newnode = new Node(name, index, "", this, x, y);
					this.addNode(newnode);
				}
				else{ //link operation
					if(!(name.equals("Link"))){
						Node nodeA = this.getNodelist().get(data[1]);						
						Node nodeB = this.getNodelist().get(data[2]);
						int length = (int) Double.parseDouble(data[3]);
						double cost = Double.parseDouble(data[4]);
						int index = this.getLinklist().size();
						if(nodeA.getIndex()<nodeB.getIndex()){
							name = nodeA.getName()+"-"+nodeB.getName();
						}
						else{
							name = nodeB.getName()+"-"+nodeA.getName();
						}
							
						
						Link newlink = new Link(name,index,"",this,nodeA,nodeB,length,cost);
						this.addLink(newlink);
						//update the neighbor node list
						//nodeA.addNeiNode(nodeB);
						//nodeB.addNeiNode(nodeA);
					}					
				}				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		try {
			bufRdr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
 
	/**
	 * copy nodes from physical layers
	 */
	public void copyNodes(Layer layer){
		HashMap<String, Node> map = layer.getNodelist();
		Iterator<String> iter1 = map.keySet().iterator();
		while (iter1.hasNext()) {
		    Node node1 = (Node)(map.get(iter1.next()));
		    Node node2 = new Node(node1.getName(), node1.getIndex(), "", this, node1.getX(), node1.getY());
		    this.addNode(node2);		    	    
		} 	
	}
	/**
	 * copy links from physical layers
	 */
	public void copyLinks(Layer layer){
		HashMap<String, Link> map = layer.getLinklist();
		Iterator<String> iter1 = map.keySet().iterator();
		while (iter1.hasNext()){
			Link link1=(Link)(map.get(iter1.next()));
			Link link2=new Link(link1.getName(),link1.getIndex(),"",this,link1.getNodeA(),link1.getNodeB(),link1.getLength(),link1.getCost());
			this.addLink(link2);
		}
	}
	/**
	 * Create node pair based on the existing node list
	 */
	public void generateNodepairs(){
		HashMap<String, Node> map = this.getNodelist();
		HashMap<String, Node> map2 = this.getNodelist();
		Iterator<String> iter1 = map.keySet().iterator();
		
		while (iter1.hasNext()) {
		    Node node1 = (Node)(map.get(iter1.next()));
		    Iterator<String> iter2 = map2.keySet().iterator();
		    while(iter2.hasNext()){
		    	Node node2= (Node)(map2.get(iter2.next()));
				if (!node1.equals(node2)) {
					if (node1.getIndex() < node2.getIndex()) {
						String name = node1.getName() + "-" + node2.getName();
						int index = this.getNodepairlist().size();
						NodePair nodepair = new NodePair(name, index, "", this,
								node1, node2);
						TrafficDemand trfdem=new TrafficDemand();  //节点对之间产生流量
						nodepair.setTrafficdemand(trfdem.generateTrafficDemand());
						this.addNodepair(nodepair);
					}
		    	}
		    	
		    }		    
		} 		
	}
	//可以随机产生n个节点对
	public void generateNodepairs(int n){
		HashMap<String, Node> map = this.getNodelist();
		HashMap<String, Node> map2 = this.getNodelist();
		Iterator<String> iter1 = map.keySet().iterator();
		
		while (iter1.hasNext()) {
		    Node node1 = (Node)(map.get(iter1.next()));
		    Iterator<String> iter2 = map2.keySet().iterator();
		    while(iter2.hasNext()){
		    	Node node2= (Node)(map2.get(iter2.next()));
		    	if (!node1.equals(node2)) {
		    		if (node1.getIndex() < node2.getIndex()) {
		    			String name = node1.getName() + "-" + node2.getName();
		    			int index = this.getNodepairlist().size();
		    			NodePair nodepair = new NodePair(name, index, "", this,
		    					node1, node2);
		    			TrafficDemand trfdem=new TrafficDemand();  //节点对之间产生流量
		    			nodepair.setTrafficdemand(trfdem.generateTrafficDemand());
		    			this.addNodepair(nodepair);
		    		}
		    	
		    }		    
		    	}
		} 		
	}
	/**
	 * find a link based on two nodes
	 */
	public Link findLink(Node nodeA, Node nodeB){
		String name;
		if(nodeA.getIndex()<=nodeB.getIndex())
			name = nodeA.getName()+"-"+nodeB.getName();
		else
			name = nodeB.getName()+"-"+nodeA.getName();
		return this.getLinklist().get(name);
	}
	
	/**
	 * 清空链路负载流量
	 */
	public void ClearLinkFlow(Layer layer){
		HashMap<String, Link> map = layer.getLinklist();
		Iterator<String> iter = map.keySet().iterator();
		while(iter.hasNext()){
			Link link=(Link)(map.get(iter.next()));
			link.setFlow(0);
		}
	}


}