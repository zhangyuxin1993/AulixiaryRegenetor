package MainFunction;

import java.util.ArrayList;

import network.Link;
import subgraph.LinearRoute;

public class RouteAndRegPlace {//一条业务的路由 以及路由上reg个数 新使用的FS数 再生器的位置 以及工作还是保护
	private LinearRoute route = new LinearRoute(null, 0, null);
	private int regnum = 0;
	private int newFSnum=0;
	private ArrayList<Integer> regnode=new ArrayList<Integer>();
	private int nature=0;  //属性工作是0保护是1
	private int constant=0;  //属性工作是0保护是1
	private ArrayList<Integer> IPRegnode=new ArrayList<Integer>();
	private float NumRemainFlow=0;
	private ArrayList<Regenerator> UsedShareReg=new ArrayList<Regenerator>();
	private ArrayList<Integer> NewRegList=new ArrayList<Integer>();
	private ArrayList<Link> LinklistOnRoute=new ArrayList<Link>();
	
	public RouteAndRegPlace(LinearRoute route, int nature) {
		super();
		this.route = route;
		this.nature = nature;
	}

	public RouteAndRegPlace(ArrayList<Link> LinklistOnRoute, int nature,int constant) {
		super();
		this.LinklistOnRoute = LinklistOnRoute;
		this.nature = nature;
		this.constant=constant;
	}
	public void setLinklistOnRoute(ArrayList<Link> LinklistOnRoute) {
		this.LinklistOnRoute.addAll(LinklistOnRoute);
	}
	public ArrayList<Link> getLinklistOnRoute() {
		return LinklistOnRoute;
	}
	
	public void setNewRegList(ArrayList<Integer> NewRegList) {
		this.NewRegList.addAll(NewRegList);
	}
	public ArrayList<Integer> getNewRegList() {
		return NewRegList;
	}
	
	public void setUsedShareReg(ArrayList<Regenerator> UsedShareReg) {
		this.UsedShareReg.addAll(UsedShareReg);
	}
	public ArrayList<Regenerator> getUsedShareReg() {
		return UsedShareReg;
	}
	
	public void setNumRemainFlow(float  NumRemainFlow) {
		this.NumRemainFlow=NumRemainFlow;
	}
	public float getNumRemainFlow() {
		return NumRemainFlow;
	}
	
	
	public void setIPRegnode(ArrayList<Integer> IPRegnode) {
		this.IPRegnode.addAll(IPRegnode);
	}
	public ArrayList<Integer> getIPRegnode() {
		return IPRegnode;
	}
	
	

	public LinearRoute getRoute(){
		return route;
	}
	public void setregnum(int  regnum) {
		this.regnum=regnum;
	}
	public int getregnum() {
		return regnum;
	}
	public void setnewFSnum(int  newFSnum) {
		this.newFSnum=newFSnum;
	}
	public int getnewFSnum() {
		return newFSnum;
	}
	public void setregnode(ArrayList<Integer> regnode) {
		this.regnode.addAll(regnode);
	}
	public ArrayList<Integer> getregnode() {
		return regnode;
	}
	public void setnature(int  nature) {
		this.nature=nature;
	}
	public int getnature() {
		return nature;
	}

	}
	
	
 
