package MainFunction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import demand.Request;
import general.Constant;
import general.file_out_put;
import graphalgorithms.RouteSearching;
import network.Layer;
import network.Link;
import network.Node;
import network.NodePair;
import resource.ResourceOnLink;
import subgraph.LinearRoute;

public class PurePhyWorkRouteSta {
	String OutFileName = MainOfAulixiaryRegenetor.OutFileName;

	public LinearRoute purephyworkroutesta(NodePair nodepair, Layer MixLayer,ArrayList<WorkandProtectRoute> wprlist, float threshold,
			ParameterTransfer ptoftransp,ArrayList<Double> RegLengthList,ArrayList<Link> totallink) throws IOException {
		RouteSearching Dijkstra = new RouteSearching();
		boolean opworkflag = false;
		Node srcnode = nodepair.getSrcNode();
		Node desnode = nodepair.getDesNode();
		double routelength = 0;
		LinearRoute route_out = new LinearRoute(null, 0, null);
		file_out_put file_io = new file_out_put();
	 
		file_io.filewrite2(OutFileName, " ");
		ArrayList<LinearRoute> routeList = new ArrayList<>();
		file_io.filewrite2(OutFileName, "纯物理链路工作路由尝试建立");

		//需要使用纯物理路由 则此时应当删除属性为虚拟
		int index= ptoftransp.getNumOfLink() + 1;
		ptoftransp.setNumOfLink(index);
		String index_inName=String.valueOf(index);
		ArrayList<Link> DelAllIPLink=new ArrayList<>();
		HashMap<String,Link> linklist=MixLayer.getLinklist();
	     Iterator<String> iter1=linklist.keySet().iterator();
	     while(iter1.hasNext()){
	    	 Link link=(Link)(linklist.get(iter1.next()));
	    	 if(link.getnature_IPorOP()==Constant.NATURE_BOUND) continue;
	    	 if(link.getnature_IPorOP()==Constant.NATURE_IP){
	    		 DelAllIPLink.add(link);
	    	 }
	     }
			for (Link delLink : DelAllIPLink) {
				MixLayer.removeLink(delLink);
			}
	     
			
		// 在光层新建光路的时候不需要考虑容量的问题
		LinearRoute opnewRoute=new LinearRoute(null, 0, null);
		Dijkstra.Kshortest(srcnode, desnode, MixLayer, 10, routeList);

		for (int count = 0; count < routeList.size(); count++) {
			opnewRoute = routeList.get(count);
			file_io.filewrite_without(OutFileName, "count=" + count + "  工作路径路由：");
			opnewRoute.OutputRoute_node(opnewRoute, OutFileName);
			file_io.filewrite2(OutFileName, "");

			if (opnewRoute.getLinklist().size() == 0) {
				file_io.filewrite2(OutFileName, "工作无路径");
			} else {
				file_io.filewrite_without(OutFileName, "纯物理链路路由为：");
				route_out.OutputRoute_node(opnewRoute, OutFileName);

				int slotnum = 0;
				int IPflow = nodepair.getTrafficdemand();
				double X = 1;// 2000-4000 BPSK,1000-2000
								// QBSK,500-1000，8QAM,0-500 16QAM

				for (Link link : opnewRoute.getLinklist()) {
					routelength = routelength + link.getLength();
				}
				// 通过路径的长度来变化调制格式 并且判断再生器 的使用

				if (routelength < 4000) {// 找到的路径不需要再生器就可以直接使用
					double costOftransp=0;
					if (routelength > 2000 && routelength <= 4000) {
						costOftransp=Constant.Cost_IP_reg_BPSK;
						X = 12.5;
					} else if (routelength > 1000 && routelength <= 2000) {
						costOftransp=Constant.Cost_IP_reg_QPSK;
						X = 25.0;
					} else if (routelength > 500 && routelength <= 1000) {
						costOftransp=Constant.Cost_IP_reg_8QAM;
						X = 37.5;
					} else if (routelength > 0 && routelength <= 500) {
						costOftransp=Constant.Cost_IP_reg_16QAM;
						X = 50.0;
					}
					slotnum = (int) Math.ceil(IPflow / X);// 向上取整
					ptoftransp.setcost_of_tranp(ptoftransp.getcost_of_tranp()+costOftransp*2);
					file_io.filewrite2(OutFileName, "");
					file_io.filewrite2(OutFileName, "纯物理链路工作路径不需要再生器时 cost of transponder" + costOftransp*2
							+"   total transponder cost="+ ptoftransp.getcost_of_tranp());
					
					if (slotnum < Constant.MinSlotinLightpath) {
						slotnum = Constant.MinSlotinLightpath;
					}
					opnewRoute.setSlotsnum(slotnum);
					// System.out.println("不需要再生器 该链路所需slot数： " + slotnum);
					file_io.filewrite2(OutFileName, "不需要再生器 该链路所需slot数： " + slotnum);
					ArrayList<Integer> index_wave = new ArrayList<Integer>();
					MainOfAulixiaryRegenetor spa = new MainOfAulixiaryRegenetor();
					index_wave = spa.spectrumallocationOneRoute(true, opnewRoute, null, slotnum);
					if (index_wave.size() == 0) {
						file_io.filewrite2(OutFileName, "路径堵塞 ，不分配频谱资源");
					} else {
						file_io.filewrite2(OutFileName, "");
						file_io.filewrite2(OutFileName, "工作链路不需要再生器时在光层分配频谱：");
						file_io.filewrite2(OutFileName, "FS起始值：" + index_wave.get(0) + "  长度" + slotnum);
						opworkflag = true;
						double length1 = 0;
						double cost = 0;

						for (Link link : opnewRoute.getLinklist()) {// 物理层的link
							length1 = length1 + link.getLength();
							cost = cost + link.getCost();
							Request request = null;
							ResourceOnLink ro = new ResourceOnLink(request, link, index_wave.get(0), slotnum);
							// 之后在工作路径需要再生器的地方也加入这段
							link.setMaxslot(slotnum + link.getMaxslot());
						} // 改变物理层上的链路容量 以便于下一次新建时分配slot

						Node helpNode=new Node(null, index, null, MixLayer, 0, 0); // 这里将helpNode设置为中间辅助节点
						helpNode.setName(srcnode.getName()+"("+index_inName+")");
						MixLayer.addNode(helpNode);
						length1=length1/1000;
						cost=cost/1000;
						
						String name = null;
						Link createlink=new Link(null, 0, null, null, null, null, 0, 0);
						if (desnode.getIndex() < helpNode.getIndex()){
							// 确定添加的虚拟路径的名字
							name = desnode.getName() +"-"+ helpNode.getName();
							createlink = new Link(name, index, null, MixLayer, desnode, helpNode, length1, cost);
						}
						else{
							name = helpNode.getName() +"-"+ desnode.getName() ;
							createlink = new Link(name, index, null, MixLayer,helpNode, desnode,  length1, cost);
						}
						
						createlink.setnature_IPorOP(Constant.NATURE_IP);
						createlink.setnature_WorkOrPro(Constant.NATURE_WORK);
						createlink.setFullcapacity(slotnum * X);// 多出来的flow是从这里产生的
						createlink.setRestcapacity(createlink.getFullcapacity() - IPflow );
						createlink.setPhysicallink(opnewRoute.getLinklist());
						MixLayer.addLink(createlink);
						file_io.filewrite2(OutFileName, "建立虚拟链路" +createlink.getName()+" 剩余容量"+ createlink.getRestcapacity());
						
						String boundLink_name = null;
						Link boundlink=new Link(null, 0, null, null, null, null, 0, 0);
						if (srcnode.getIndex() < helpNode.getIndex()){
							// 确定添加的虚拟路径的名字
							boundLink_name = srcnode.getName() +"-"+ helpNode.getName();
							boundlink = new Link(boundLink_name, index, null, MixLayer, srcnode, helpNode, 0, 0);
						}
						else{
							boundLink_name = helpNode.getName() +"-"+ srcnode.getName() ;
							boundlink = new Link(boundLink_name, index, null, MixLayer,helpNode, srcnode, 0, 0);
						}
						
						boundlink.setnature_IPorOP(Constant.NATURE_BOUND);
						boundlink.setnature_WorkOrPro(Constant.NATURE_BOUND);
						boundlink.setRestcapacity(0);
						MixLayer.addLink(boundlink);
					}
				}
				if (routelength > 4000) {
					RegeneratorPlace regplace = new RegeneratorPlace();
					opworkflag = regplace.regeneratorplace(IPflow, routelength, false,opnewRoute, null,MixLayer, wprlist,
							nodepair, RegLengthList,threshold,ptoftransp);
				}
			}
			
			if (opworkflag) 
				break;
		}
		if (opworkflag) {
			file_io.filewrite2(OutFileName, "工作路径纯物理路径路由成功并且RSA");
			
		}
		if (!opworkflag) {
			file_io.filewrite2(OutFileName, "工作路径纯物理链路路由失败");
		}
		for(Link link:DelAllIPLink){//恢复所有的虚拟链路
			MixLayer.addLink(link);
		}
		return opnewRoute;
	}
}
