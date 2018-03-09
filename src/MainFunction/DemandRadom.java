package MainFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import general.file_out_put;
import graphalgorithms.RouteSearching;
import network.Layer;
import network.NodePair;
import randomfunctions.randomfunction;
import subgraph.LinearRoute;

public class DemandRadom {
	
	public ArrayList<NodePair> NodePairRadom(int nodepairNum,Layer mylayer){//随机产生nodepair列表
		ArrayList<LinearRoute> routelist_once=new ArrayList<LinearRoute>();
		int serial=0;
		
		HashMap<String,Integer> nodepair_serial=new HashMap<String,Integer>();
		ArrayList<NodePair> nodepairlist= new ArrayList<NodePair>();
		HashMap<String, NodePair> Snodepair = mylayer.getNodepairlist();
		Iterator<String> iter1 = Snodepair.keySet().iterator();
		while (iter1.hasNext()) 
		{
			
			NodePair nodepairser=(NodePair) (Snodepair.get(iter1.next()));
			nodepair_serial.put(nodepairser.getName(),serial);
			serial++;
//			System.out.println(nodepairser.getName()+"  "+serial);
		}//为nodepair编号
		
		////产生nodepair
		randomfunction radom=new randomfunction();
		int[] nodepair_num=radom.Dif_random(nodepairNum, mylayer.getNodepairNum());
		int has=0;
		HashMap<String, NodePair> map = mylayer.getNodepairlist();
		Iterator<String> iter = map.keySet().iterator();
		while (iter.hasNext()) 
		{
			has=0;
			routelist_once.clear();
			NodePair nodepair=(NodePair) (map.get(iter.next()));

//			rs.findAllRoute(nodepair.getSrcNode(), nodepair.getDesNode(), mylayer, null, 100, routelist_once);
//			if(routelist_once.size()<3) continue;
			for(int a=0;a<nodepair_num.length;a++){
				if(nodepair_num[a]==nodepair_serial.get(nodepair.getName())){
					has=1;
					break;
				}
					
			}
			if(has==0) continue;//随机产生demand
			nodepairlist.add(nodepair);
		}
		return nodepairlist;
	}
	
	public void TrafficNumRadom(ArrayList<NodePair>nodepairlist ){
		randomfunction radom=new randomfunction();
		for(NodePair nodePair:nodepairlist){
				nodePair.setTrafficdemand(radom.Num_random(1,200)[0]+1);//产生200G的容量
		}
	}
}
