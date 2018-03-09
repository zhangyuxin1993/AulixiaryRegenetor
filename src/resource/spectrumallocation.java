package resource;

import java.util.ArrayList;

import network.Link;
import subgraph.LinearRoute;

public class spectrumallocation {
	private ArrayList<Integer> sameindex=new ArrayList<Integer>();
	

	public ArrayList<Integer> getSameindex() {
		return sameindex;
	}
	public void setSameindex(ArrayList<Integer> sameindex) {
		this.sameindex = sameindex;
	}
	public ArrayList<Integer> spectrumallocationOneRoute(LinearRoute route){
		
		ArrayList<Link> routelink = route.getLinklist();
			for(Link link : routelink){
				if(route.getSlotsnum() == 0){
					System.out.println("noslots");
					break;
				}
				link.getSlotsindex().clear();	
				for(int i = 0; i <= link.getSlotsarray().size() - route.getSlotsnum(); i++){
						int s = 1;
						for(int k = i; k < route.getSlotsnum() + i; k ++){
							
							if(link.getSlotsarray().get(k).getoccupiedreqlist().size()!= 0){
								s = 0;
								break;
							}
							
						}
						if(s != 0){
							link.getSlotsindex().add(i);
							
						}
					}
				}	
		if(routelink.size()!=0){
			Link link = routelink.get(0);
		//	ArrayList<Integer> sameindex = new ArrayList<Integer>();
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
	}
			return sameindex;		
		}
	
}
