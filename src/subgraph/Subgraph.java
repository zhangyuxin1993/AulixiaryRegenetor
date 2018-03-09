package subgraph;
import java.util.ArrayList;
import network.*;
import general.CommonObject;

public class Subgraph extends CommonObject{
    private ArrayList<Node>nodelist=null;
    private ArrayList<Link>linklist=null;
    private ArrayList<CommonObject>objetc=null;
    private ArrayList<Subgraph>routelist=null;
    
	public Subgraph(String name, int index, String comments) {
	   super(name, index, comments);
	   // TODO Auto-generated constructor stub
       this.nodelist=new ArrayList<Node>();
       this.linklist=new ArrayList<Link>();
       this.objetc=new ArrayList<CommonObject>();
       this.routelist=new ArrayList<Subgraph>();
	}
	
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
	public ArrayList<CommonObject> getObjetc() {
		return objetc;
	}
	public void setObjetc(ArrayList<CommonObject> objetc) {
		this.objetc = objetc;
	}
	public ArrayList<Subgraph> getRoutelist() {
		return routelist;
	}
	public void setRoutelist(ArrayList<Subgraph> routelist) {
		this.routelist = routelist;
	}
	
	
	/**
	 * convert a node list to link and common object list
	 */
	public void ConvertfromNodeListtoLinkList(){
		this.getLinklist().clear();
		for(Node node : this.getNodelist()){
			int next_node_index = this.getNodelist().indexOf(node)+1;
			if(next_node_index<this.getNodelist().size()){
				Node next_node = this.getNodelist().get(next_node_index);			
				if(next_node!=null){
					Link link = node.getAssociatedLayer().findLink(node, next_node);				
					this.getLinklist().add(link);
				}
			}
					
		}
	}
	
	public double getlength(){
		double sum=0;
		for(Link link:this.getLinklist()){
			sum+=link.getLength();
		}
		return sum;
	}
}
