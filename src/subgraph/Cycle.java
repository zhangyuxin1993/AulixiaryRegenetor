package subgraph;

public class Cycle extends Subgraph {

	public Cycle(String name, int index, String comments) {
		super(name, index, comments);
		// TODO Auto-generated constructor stub
	}
	
	
	public Boolean isEqual(Cycle acycle) {
		int thisnum = this.getNodelist().size();
		int asum = acycle.getNodelist().size();
		if(thisnum != asum)
			return false;
		else{
			Boolean forward = true;
			Boolean backward = true;
			for(int i=0;i<asum;i++){
				if(this.getNodelist().get(i)!=acycle.getNodelist().get(i)){
					forward = false;
					break;
				}				
			}
			
			for(int i=0;i<asum;i++){
				if(this.getNodelist().get(i)!=acycle.getNodelist().get(asum-1-i)){
					backward = false;
					break;
				}				
			}
			
			if(!forward && !backward)
				return false;
		}
		return true;
	}

}

