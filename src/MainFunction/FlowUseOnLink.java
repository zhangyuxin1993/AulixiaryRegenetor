package MainFunction;

import network.Link;

public class FlowUseOnLink {
	private Link link;
	private double FlowUseOnLink=0;
 
	public FlowUseOnLink(Link link, double flowUseOnLink) {
		super();
		this.link = link;
		FlowUseOnLink = flowUseOnLink;
	}
	
	public void setvlink(Link link) {
		this.link=link;
	}
	public Link getVlink() {
		return link;
	}
	
	public void setFlowUseOnLink(double FlowUseOnLink) {
		this.FlowUseOnLink=FlowUseOnLink;
	}
	public double getFlowUseOnLink() {
		return FlowUseOnLink;
	}
	

}