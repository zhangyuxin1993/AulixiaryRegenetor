package MainFunction;

import demand.Request;
import network.Link;

public class SlotUseOnWorkPhyLink {
	
	private Link WorkLink;
	private Request WorkRequest;
	private int StartFS;
	private int SlotNum;
 

	public SlotUseOnWorkPhyLink(Link workLink, Request workRequest, int startFS, int slotNum) {
		super();
		WorkLink = workLink;
		WorkRequest = workRequest;
		StartFS = startFS;
		SlotNum = slotNum;
	}

	public void setWorkLink(Link WorkLink) {
		this.WorkLink = WorkLink;
	}

	public Link getWorkLink() {
		return WorkLink;
	}
	public void setStartFS(int StartFS) {
		this.StartFS = StartFS;
	}

	public int getStartFS() {
		return StartFS;
	}

	public void setSlotNum(int SlotNum) {
		this.SlotNum = SlotNum;
	}

	public int getSlotNum() {
		return SlotNum;
	}
	public void setWorkRequest(Request  WorkRequest) {
		this.WorkRequest=WorkRequest;
	}
	public Request getWorkRequest() {
		return WorkRequest;
	}

	
}
