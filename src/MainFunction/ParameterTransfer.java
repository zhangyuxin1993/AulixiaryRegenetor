package MainFunction;

import java.util.ArrayList;

import network.Link;
import network.Node;

/*
 * 为了实现方法中值的传递
 */
public class ParameterTransfer {

	float remainFlowRatio=100;
	float NumremainFlow=0;
	Node StartNode=new Node(null, 0, null, null, 0, 0);
	Node EndNode=new Node(null, 0, null, null, 0, 0);
	float MinRemainFlowRSA=0;  //在每条链路RSA时 记录每条链路上剩余的最小容量
	ArrayList<Double> ResFlowOnlinks= new ArrayList<>();
	int numOfTransponder=0;
	double cost_of_tranp=0;
	int NumOfLink=0;
	ArrayList<Link> IPlinkStaInWork=new ArrayList<>();
	
	public int getNumOfLink() {
		return NumOfLink;
	}
	public void setNumOfLink(int  NumOfLink) {
		 this.NumOfLink=NumOfLink;
	}
	public double getcost_of_tranp() {
		return cost_of_tranp;
	}
	public void setcost_of_tranp(double  cost_of_tranp) {
		 this.cost_of_tranp=cost_of_tranp;
	}
	
	
	public int getNumOfTransponder() {
		return numOfTransponder;
	}
	public void setNumOfTransponder(int  numOfTransponder) {
		 this.numOfTransponder=numOfTransponder;
	}
	public void setIPlinkStaInWork(ArrayList<Link> IPlinkStaInWork) {
		this.IPlinkStaInWork.addAll(IPlinkStaInWork);
	}
	public ArrayList<Link> getIPlinkStaInWork() {
		return IPlinkStaInWork;
	}
	
	
	public void setResFlowOnlinks(ArrayList<Double> ResFlowOnlinks) {
		this.ResFlowOnlinks.addAll(ResFlowOnlinks);
	}
	public ArrayList<Double> getResFlowOnlinks() {
		return ResFlowOnlinks;
	}
	
	public Node getEndNode() {
		return EndNode;
	}
	public void setEndNode(Node EndNode) {
		 this.EndNode=EndNode;
	}
	
	public Node getStartNode() {
		return StartNode;
	}
	public void setStartNode(Node StartNode) {
		 this.StartNode=StartNode;
	}

	public float getMinRemainFlowRSA() {
		return MinRemainFlowRSA;
	}
	public void setMinRemainFlowRSA(float MinRemainFlowRSA) {
		 this.MinRemainFlowRSA=MinRemainFlowRSA;
	}
	
	
	public float getRemainFlowRatio() {
		return remainFlowRatio;
	}
	public void setRemainFlowRatio(float remainFlow) {
		 this.remainFlowRatio=remainFlow;
	}
	
	public float getNumremainFlow() {
		return NumremainFlow;
	}
	public void setNumremainFlow(float NumremainFlow) {
		 this.NumremainFlow=NumremainFlow;
	}
}
