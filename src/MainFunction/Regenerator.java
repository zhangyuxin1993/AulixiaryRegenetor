package MainFunction;

import network.Node;

public class Regenerator {
	private Node node = new Node(null, 0, null, null, 0, 0);
	private int index = 0;
	private int propathNum = 0;
	private int RegType = 2;//代表属性 =0表示pyhsical layer 纯光层再生器 ；=1表示在IP层再生器
	
	public Regenerator(Node node) {
		super();
		this.node = node;
	}
	
	public void setNature(int nature) {
		 this.RegType=nature;
	}
	public int getNature() {
		return RegType;
	}
	
	public void setPropathNum(int propathNum) {
		 this.propathNum=propathNum;
	}
	public int getPropathNum() {
		return propathNum;
	}
	
	
	public void setnode(Node node) {
		 this.node=node;
	}
	public Node getnode() {
		return node;
	}
	
	public void setindex(int index) {
		 this.index=index;
	}
	public int getindex() {
		return index;
	}
	


}
