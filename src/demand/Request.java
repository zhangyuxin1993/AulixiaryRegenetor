package demand;
import java.util.ArrayList;
import resource.ResourceOnLink;
import general.Constant;
import network.Link;
import network.NodePair;
import subgraph.LinearRoute;

public class Request {
	private NodePair nodepair;
	private double bandwidth;
	private int slots;
	//private double time;
	//private int requesttype;
	private ArrayList<Link> route;
	private ArrayList<ResourceOnLink> rollist;
	private ArrayList<ResourceOnLink> rollist_w;
	//private ArrayList<ResourceOnLink> rollist_p;
	private int protection_index;
	//private int spectrumassigment=1;//频谱分配方式；若为1，则为首次命中，否则为随机选择
    //private Route route;
	
	/*public Request(NodePair nodepair, double bandwidth, double time) {
		// TODO Auto-generated constructor stub
		
		this.setNodepair(nodepair);
		this.setRoute(nodepair.getLinearroutelist().get(0).getRoute_linklist());
//		this.setRoute(route);
		this.setBandwidth(bandwidth);
		this.setTime(time);
		this.rollist = new ArrayList<ResourceOnLink>();
	}*/
	
	//public Request(NodePair nodepair, int slots, double time,int requesttype){ 
		// TODO Auto-generated constructor stub
		public Request(NodePair nodepair, int slots){ 
		this.setNodepair(nodepair);
		nodepair.setSlotsnum(slots);
		this.setRoute(nodepair.getLinearroutelist().get(0).getLinklist());
		this.setSlots(slots);
		//this.setTime(time);
		//this.setRequesttype(requesttype);
		this.rollist = new ArrayList<ResourceOnLink>();
		this.rollist_w = new ArrayList<ResourceOnLink>();
		//this.rollist_p= new ArrayList<ResourceOnLink>();
	}
		public Request(NodePair nodepair){ 
		this.setNodepair(nodepair);
	}
		
	public void spectrumrelease_working(){
		for(ResourceOnLink rol : this.rollist_w){
			rol.resoucerelease();
		}
	}
	//保护路径的波长分配
	//public void spectrumrelease_protection(){
	//	for(ResourceOnLink rol : this.rollist_p){
	//		rol.resoucerelease();
	//	}
	//}
	

	public ArrayList<Integer> spectrumallocationOneRoute(LinearRoute route){
		ArrayList<Link> routelink = route.getLinklist();
			for(Link link : routelink){
				if(route.getSlotsnum() == 0){
					System.out.println("noslots");
					break;
				}
				link.getSlotsindex().clear();

//				System.out.println(link.getName() + "\t" + link.getSlotsindex().size());
				for(int i = 0; i <= link.getSlotsarray().size() - route.getSlotsnum(); i++){
//					System.out.println(link.getName() + "\t" + link.getSlotsindex().size());
					if(link.getSlotsarray().get(i) == null){
						int s = 1;
						for(int k = i; k < route.getSlotsnum() + i; k ++){
							
							if(link.getSlotsarray().get(k) != null){
								s = 0;
								break;
							}
							
						}
						if(s != 0){
							link.getSlotsindex().add(i);
							
						}
					}
				}	
			}
			Link link = routelink.get(0);
			ArrayList<Integer> sameindex = new ArrayList<Integer>();
			sameindex.clear();
			for(int i = 0; i < link.getSlotsindex().size(); i ++){
				int index = link.getSlotsindex().get(i);
				int flag = 1;
				for(Link link2 : routelink){
					if(!link2.getSlotsindex().contains(index)){
						flag = 0;
						break;
					}
				}
				if(flag != 0){
					sameindex.add(link.getSlotsindex().get(i));
				}
			}
			return sameindex;		
		}
	
	
	
	public ArrayList<Integer> spectrumallocationOneRoute_index(LinearRoute route, int original_index){
		ArrayList<Link> routelink = route.getLinklist();
			for(Link link : routelink){
				if(route.getSlotsnum() == 0){
					System.out.println("noslots");
					break;
				}
				link.getSlotsindex().clear();

//				System.out.println(link.getName() + "\t" + link.getSlotsindex().size());
				for(int i = 0; i <=original_index; i++){
//					System.out.println(link.getName() + "\t" + link.getSlotsindex().size());
					if(link.getSlotsarray().get(i) == null){
						int s = 1;
						for(int k = i; k < route.getSlotsnum() + i; k ++){
							
							if(link.getSlotsarray().get(k) != null){
								s = 0;
								break;
							}
							
						}
						if(s != 0){
							link.getSlotsindex().add(i);
							
						}
					}
				}	
			}
			Link link = routelink.get(0);
			ArrayList<Integer> sameindex = new ArrayList<Integer>();
			sameindex.clear();
			for(int i = 0; i < link.getSlotsindex().size(); i ++){
				int index = link.getSlotsindex().get(i);
				int flag = 1;
				for(Link link2 : routelink){
					if(!link2.getSlotsindex().contains(index)){
						flag = 0;
						break;
					}
				}
				if(flag != 0){
					sameindex.add(link.getSlotsindex().get(i));
				}
			}
			return sameindex;
			
		}
	
	public void setNodepair(NodePair nodepair) {
		this.nodepair = nodepair;
	}

	public NodePair getNodepair() {
		return nodepair;
	}

	public void setBandwidth(double bandwith) {
		this.bandwidth = bandwith;
	}

	public double getBandwidth() {
		return bandwidth;
	}

	public void setRoute(ArrayList<Link> route) {
		this.route = route;
	}

	public ArrayList<Link> getRoute() {
		return route;
	}

	public void setRollist(ArrayList<ResourceOnLink> rollist) {
		this.rollist = rollist;
	}
	
	public void setRollist_w(ArrayList<ResourceOnLink> rollist) {
		this.rollist_w = rollist;
	}
	
	public ArrayList<ResourceOnLink> getRollist() {
		return rollist;
	}

	public void setSlots(int slots) {
		this.slots = slots;
	}

	public int getSlots() {
		return slots;
	}
	
	public ArrayList<ResourceOnLink> getRollist_w() {
		return rollist_w;
	}
	
	public void setprotection_index(int firstindex)
	{
		this.protection_index=firstindex;
	}
	public int getprotection_index()
	{
		return protection_index;
	}

}
