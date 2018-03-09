package general;
import java.util.ArrayList;
import demand.Request;
public class Slot {
	private ArrayList<Request> occupiedreqlist;
	
	public Slot()
	{
		this.occupiedreqlist=new ArrayList<Request>();
	}
	public ArrayList<Request> getoccupiedreqlist()
	{
		return occupiedreqlist;
	}
	
	public void setoccupiedreqlist(ArrayList<Request> occupiedreqlist)
	{
		this.occupiedreqlist=occupiedreqlist;
	}
	
}

