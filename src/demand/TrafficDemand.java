package demand;

import general.Constant;

public class TrafficDemand {	
	
	public int generateTrafficDemand(){
		int flow=0;
	    int randomnum=(int)(Math.random()*(2*Constant.AVER_DEMAND-20));
	    flow=randomnum+2;
//	    flow=randomnum+10;
	    return flow;
	}
	
}
