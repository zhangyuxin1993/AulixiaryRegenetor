package resource;
import network.Link;
import network.VirtualLink;
import demand.Request;

public class ResourceOnLink {
	private Request request;
	private Link link;
	private int startindex;
	private int slots;
	
	public ResourceOnLink(Request request, Link link, int startindex, int slots) {
		// TODO Auto-generated constructor stub
		this.setRequest(request);
		this.setLink(link);
		this.setStartindex(startindex);
		this.setSlots(slots);
		this.resouceocuppied(request, link, startindex, slots);
	}
	public ResourceOnLink(Request request, VirtualLink Vlink, int startindex, int slots) {
		this.setRequest(request);
		this.setLink(link);
		this.setStartindex(startindex);
		this.setSlots(slots);
		this.resouceocuppied(request, link, startindex, slots);
	}
	
	//分配资源时按离开时间进行排序;
	public void resouceocuppied(Request request, Link link, int startindex, int slots){
		for(int i = startindex; i < startindex + slots; i ++){
				link.getSlotsarray().get(i).getoccupiedreqlist().add(request);
			
		}
		/*for(int i=startindex;i<startindex+slots;i++)
		{
			System.out.println("第"+i+"个slot");
			for(Request request1:link.getSlotsarray().get(i).getoccupiedreqlist())
				System.out.println("request="+request1.getNodepair().getName()+'\t'+"departtime 0f request is:"+request1.getDepartTime());
		}*/
	}
	
	
	public void resoucerelease(){
		//System.out.println("释放资源前，该链路状态:");
	    /*for(int j=startindex;j<startindex+slots;j++)
	    {
	    		if(link.getSlotsarray().get(j).getoccupiedreqlist().size()!=0)
	    		{
	    			System.out.print("占用链路"+link.getName()+"的第"+j+"个资源的业务有：");
	    			for(Request request:link.getSlotsarray().get(j).getoccupiedreqlist())
	    				System.out.print(request.getNodepair().getName()+'\t');
	    			System.out.println();
	    		}
	    }*/
		for(int i = startindex; i < startindex + slots; i ++){
			link.getSlotsarray().get(i).getoccupiedreqlist().remove(request);
		}
		/*System.out.println("释放资源后，该链路状态:");
	    for(int j=startindex;j<startindex+slots;j++)
	    {
	    		if(link.getSlotsarray().get(j).getoccupiedreqlist().size()!=0)
	    		{
	    			System.out.print("占用链路"+link.getName()+"的第"+j+"个资源的业务有：");
	    			for(Request request:link.getSlotsarray().get(j).getoccupiedreqlist())
	    				System.out.print(request.getNodepair().getName()+'\t');
	    			System.out.println();
	    		}
	    }*/
	}

	public void setLink(Link link) {
		this.link = link;
	}

	public Link getLink() {
		return link;
	}

	public void setStartindex(int startindex) {
		this.startindex = startindex;
	}

	public int getStartindex() {
		return startindex;
	}

	public void setSlots(int widthnum) {
		this.slots = widthnum;
	}

	public int getSlots() {
		return slots;
	}

	public void setRequest(Request request) {
		this.request = request;
	}

	public Request getNodepair() {
		return request;
	}
}
