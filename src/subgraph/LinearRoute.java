package subgraph;

import java.io.IOException;
import java.util.ArrayList;

 
import general.file_out_put;
import network.Link;
import network.Node;


public class LinearRoute extends Subgraph {
	private int slotsnum;
	  private ArrayList<Link>linklist=null;
	  
	public LinearRoute(String name, int index, String comments) {
		super(name, index, comments);
		// TODO Auto-generated constructor stub
	       
	}
	
	public int Equal_link(LinearRoute newroute,Link link){//返回是否有相同link
		     ArrayList<Link>linklist=newroute.getLinklist();
		     for(Link link1:linklist){
		    	 if(link.getName().endsWith(link1.getName()))return 1;
		     }
		     return 0;
	}
	public void OutputRoute_link(LinearRoute newroute){//输出link
		     ArrayList<Link>linklist=newroute.getLinklist();
		     for(Link link:linklist){
		    	 if(link.getNodeA().getIndex()<link.getNodeB().getIndex()){
		    	 System.out.println(link.getNodeA().getName()+"------"+link.getNodeB().getName()+link.getCost());
		    	 }
		     }
	}
	public void OutputRoute_node(LinearRoute newroute,String write_name) throws IOException{//输出节点
		file_out_put file=new file_out_put();
		if(newroute.getNodelist().size()==0){
//			System.out.println("no path to the desnode");
			file.filewrite(write_name,"no path to the desnode");
		}
		else{ 
		      for(Node node:newroute.getNodelist()){		    	       
//			           System.out.println(node.getName()+"  ");			        
			           file.filewrite(write_name,node.getName()+"   ");			          			           
		      }		     
		}		
		 
	}
	
	public void OutputRoute_node(LinearRoute newroute){//输出路径的节点信息
		if(newroute.getNodelist().size()==0){
			System.out.println("no path to the desnode");
		}
		else{	
			for(int n=0;n<newroute.getNodelist().size()-1;n++){
				Node node=newroute.getNodelist().get(n);
				System.out.print(node.getName()+"-");
			}	    	   
		    	System.out.println(newroute.getNodelist().get(newroute.getNodelist().size()-1).getName());
		     	      
		}
	}
	//public ArrayList<Link> getRoute_linklist() {可删，返回linklist
	//	return linklist;
	//}
	public void setSlotsnum(int slotsnum) {
	
		this.slotsnum = slotsnum;
	}

	public int getSlotsnum() {
		return this.slotsnum;
	}
}

	
 
