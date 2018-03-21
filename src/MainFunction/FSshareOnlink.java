package MainFunction;

import java.util.ArrayList;

import network.Link;

public class FSshareOnlink {
	private Link link=new Link(null, 0, null, null, null, null, 0, 0); 
	private ArrayList<Integer> slotIndex=new ArrayList<Integer>();
	private WorkandProtectRoute wpr=new WorkandProtectRoute(null);
	
	public FSshareOnlink(Link link, ArrayList<Integer> slotIndex) {
		super();
		this.link = link;
	 
		this.slotIndex = slotIndex;
	}
 
	public void setlink(Link  link) {
		this.link=link;
	}
	public Link getlink() {
		return link;
	}

	public void setslotIndex(ArrayList<Integer> slotIndex) {
		this.slotIndex.addAll(slotIndex);
	}
	public ArrayList<Integer> getslotIndex() {
		return slotIndex;
	}
	
	public void setwpr(WorkandProtectRoute  wpr) {
		this.wpr=wpr;
	}
	public WorkandProtectRoute getwpr() {
		return wpr;
	}
	
}
