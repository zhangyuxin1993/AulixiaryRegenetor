package network;

import java.util.ArrayList;

public class VirtualLink {
	private String srcnode = null;
	private String desnode = null;
	private double fullcapacity = 0;
	private double restcapacity = 0;
	private double usedcapacity = 0;
	private int nature = 2;
	private int maxslot=0;
	private double cost=0;
	private double length=0;
	private ArrayList<Link> physicallink=new ArrayList<Link>();
	private int MaybeUseFlow = 0;
	
	public VirtualLink(String srcnode,String desnode,int nature,int restcapacity) {
		this.restcapacity = restcapacity;
		this.srcnode = srcnode;
		this.desnode = desnode;
		this.nature = nature;
	}
	public void setMaybeUseFlow(int  MaybeUseFlow) {
		this.MaybeUseFlow=MaybeUseFlow;
	}
	public int getMaybeUseFlow() {
		return MaybeUseFlow;
	}
	
	public void setcost(double  cost) {
		this.cost=cost;
	}
	public double getcost() {
		return cost;
	}
	
	public void setlength(double  length) {
		this.length=length;
	}
	public double getlength() {
		return length;
	}
	public void setPhysicallink(ArrayList<Link> physicallink) {
		this.physicallink.addAll(physicallink);
	}
	public ArrayList<Link> getPhysicallink() {
		return physicallink;
	}
	public void setMaxslot(int maxslot) {
		this.maxslot = maxslot;
	}
	public int getMaxslot() {
		return maxslot;
	}
	public int getNature() {
		return nature;
	}
	public void setnature(int nature) {
		this.nature = nature;
	}
	public String getSrcnode() {
		return srcnode;
	}
	
	public void setSrcnode(String srcnode) {
		this.srcnode = srcnode;
	}
	
	public String getDesnode() {
		return desnode;
	}
	
	public void setDesnode(String desnode) {
		this.desnode = desnode;
	}
	
	public double getFullcapacity() {
		return fullcapacity;
	}
	
	public void setFullcapacity(double fullcapacity) {
		this.fullcapacity = fullcapacity;
	}
	
	public double getRestcapacity() {
		return restcapacity;
	}
	
	public void setRestcapacity(double restcapacity) {
		this.restcapacity = restcapacity;
	}
	public double getUsedcapacity() {
		return usedcapacity;
	}
	public void setUsedcapacity(double usedcapacity) {
		this.usedcapacity = usedcapacity;
	}

}
