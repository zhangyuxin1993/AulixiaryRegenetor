package general;

public class Constant {
	public final static int UNVISITED=0;
	public final static int VISITEDONCE=1;
	public final static int VISITEDTWICE=2;
	public final static int UNORDER=0;          //节点对还未按顺序排列
	public final static int ORDERED=1;          //节点对已经按顺序排列
	public final static int WAVE_PRE_FIBER=40;  //WDM中每条光纤传输的最大波长数
	public final static int F=320;
	public final static int FREE=0;
	public final static int BUSY=1;
	public final static int WAVE_UNASSIGNMENT=0;
	public final static int WAVE_ASSIGNMENTED=1;
	public final static int AVER_DEMAND=100;   //节点对之间的平均流量
	public final static int NATURE_IP=0;   
	public final static int NATURE_OP=1;   
	public final static int NATURE_WORK=3; 
	public final static int NATURE_PRO=4; 
	public final static int NATURE_PHY=5;
	public final static int NATURE_BOUND=9;
	public final static double MaxNum=10000;
	
	public final static int Cost_OEO_reg_BPSK=1;  //OEO再生 不同调制格式下的cost
	public final static double Cost_OEO_reg_QPSK=1.3;  
	public final static double Cost_OEO_reg_8QAM=1.5;   
	public final static double Cost_OEO_reg_16QAM=1.7;   
	
	public final static double Cost_IP_reg_BPSK=1.3;  //IP再生 不同调制格式下的cost
	public final static double Cost_IP_reg_QPSK=1.69;  
	public final static double Cost_IP_reg_8QAM=1.95;   
	public final static double Cost_IP_reg_16QAM=2.21;   
	public final static int MinSlotinLightpath=12;		
}

