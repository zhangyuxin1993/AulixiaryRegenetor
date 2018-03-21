package network;

import java.util.ArrayList;

import network.NodePair;
import subgraph.Cycle;
import subgraph.LinearRoute;
import general.CommonObject;
import general.Constant;
import general.Slot;

public class Link extends CommonObject{
	
	private Layer associatedLayer = null; //the layer that the link belongs to
	private Node nodeA = null; //node A
	private Node nodeB = null; //node B
	private double length = 0; //physical distance of the link
	private double cost = 0;// the cost of the link
	private int status = Constant.UNVISITED;//the visited status	 	 
	private int[] wavestatus = new int[Constant.WAVE_PRE_FIBER]; //链路的波长占用状态
	private double flow=0;//经过链路的网络流量
	private double sumflow=0;//建立的光通道所含有的总共的流量
	private double ipremainflow=0;
    private int nature_IPorOP;//链路是IP链路还是OP链路
    private int nature_WorkOrPro;//链路是IP链路还是OP链路
	private double fullcapacity = 0;
	private double restcapacity = 0;
	private double UsedCapacity = 0;
	private int maxslot;//最大使用slot
	private int indexInRoute;//最大使用slot
	
	public int getindexInRoute() {
		return indexInRoute;
	}
	public void setindexInRoute(int indexInRoute) {
		this.indexInRoute = indexInRoute;
	}
	
	public double getUsedCapacity() {
		return UsedCapacity;
	}
	
	public void setUsedCapacity(double UsedCapacity) {
		this.UsedCapacity = UsedCapacity;
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
	
 
	public int getMaxslot() {
		return maxslot;
	}
	public void setMaxslot(int maxslot) {
		this.maxslot = maxslot;
	}
	public int getnature_IPorOP() {
		return nature_IPorOP;
	}
	public void setnature_IPorOP(int nature_IPorOP) {
		this.nature_IPorOP = nature_IPorOP;
	}
	
	public int getnature_WorkOrPro() {
		return nature_WorkOrPro;
	}
	public void setnature_WorkOrPro(int nature_WorkOrPro) {
		this.nature_WorkOrPro = nature_WorkOrPro;
	}
	public double getIpremainflow() {
		return ipremainflow;
	}
	public void setIpremainflow(double ipremainflow) {
		this.ipremainflow = ipremainflow;
	}

	private int wavenum=0; //链路所承载的波长数，用于计算该链路所需的光纤数
	private ArrayList<Slot> slotsarray;	
	private ArrayList<Integer> slotsindex;
	private ArrayList<Link> physicallink=new ArrayList<>();//在物理层所经过的link
	
	public ArrayList<Link> getPhysicallink() {
		return physicallink;
	}
	public void setPhysicallink(ArrayList<Link> physicallink) {
		this.physicallink.addAll(physicallink);
	}
	public Layer getAssociatedLayer() {
		return associatedLayer;
	}
	public void setAssociatedLayer(Layer associatedLayer) {
		this.associatedLayer = associatedLayer;
	}
	public Node getNodeA() {
		return nodeA;
	}
	public void setNodeA(Node nodeA) {
		this.nodeA = nodeA;
	}
	public Node getNodeB() {
		return nodeB;
	}
	public void setNodeB(Node nodeB) {
		this.nodeB = nodeB;
	}
	public double getLength() {
		return length;
	}
	public void setLength(double length) {
		this.length = length;
	}
	public double getCost() {
		return cost;
	}
	public void setCost(double cost) {
		this.cost = cost;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int[] getWavestatus() {
		return wavestatus;
	}
	public void setWavestatus(int[] wavestatus) {
		this.wavestatus = wavestatus;
	}
	public double getFlow() {
		return flow;
	}
	public void setFlow(double flow) {
		this.flow = flow;
	}
	public double getSumflow() {
		return sumflow;
	}
	public void setSumflow(double sumflow) {
		this.sumflow = sumflow;
	}

	
	public void setWavenum(int wavenum) {
		this.wavenum = wavenum;
	}
	public int getWavenum() {
		return wavenum;
	}
	public void setSlotsarray(ArrayList<Slot> slotsarray) {
		this.slotsarray = slotsarray;
	}

	public ArrayList<Slot> getSlotsarray() {
		return slotsarray;
	}
	public void setSlotsindex(ArrayList<Integer> randomnum) {
		this.slotsindex = randomnum;
	}

	public ArrayList<Integer> getSlotsindex() {
		return slotsindex;
	}
	
	public Link(String name, int index, String comments, Layer associatedLayer,
			Node nodeA, Node nodeB, double length, double cost) {
		super(name, index, comments);
		this.associatedLayer = associatedLayer;
		this.nodeA = nodeA;
		this.nodeB = nodeB;
		this.length = length;
		this.cost = cost;	 	 
		status = Constant.UNVISITED;
		this.slotsindex=new ArrayList();
		
		this.slotsarray=new ArrayList<Slot>();
		this.nature_WorkOrPro=nature_WorkOrPro;
		this.nature_IPorOP=nature_IPorOP;
		//System.out.println("size="+slotsarray.size());
		for(int i = 0; i < Constant.F; i ++){
			Slot slot=new Slot();
			this.slotsarray.add(slot);
		}
		for(int k=0;k<Constant.WAVE_PRE_FIBER;k++)
			this.wavestatus[k]= Constant.FREE;
	}
}

