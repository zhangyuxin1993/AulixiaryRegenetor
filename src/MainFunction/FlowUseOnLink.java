package MainFunction;

import network.Link;

public class FlowUseOnLink {
	private Link link;
	private double flowuseonlink=0;
 
	public FlowUseOnLink(Link link, double flowuseonlink) {
		super();
		this.link = link;
		this.flowuseonlink = flowuseonlink;
	}
	
	public void setlink(Link link) {
		this.link=link;
	}
	public Link getLink() {
		return link;
	}
	
	public void setFlowUseOnLink(double FlowUseOnLink) {
		this.flowuseonlink=FlowUseOnLink;
	}
	public double getFlowUseOnLink() {
		return flowuseonlink;
	}
	

}

