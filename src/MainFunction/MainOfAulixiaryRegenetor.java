package MainFunction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import general.Constant;
import general.file_out_put;
import network.Layer;
import network.Link;
import network.NodePair;
import subgraph.LinearRoute;

public class MainOfAulixiaryRegenetor {
	public static String FinalResultFile = "D:\\zyx\\programFile\\RegwithProandTrgro\\6_FinalResult.dat";
	public static String OutFileName = "D:\\zyx\\programFile\\RegwithProandTrgro\\6.dat";

	public static void main(String[] args) throws IOException {
		String TopologyName = "D:/zyx/Topology/6.csv";
		// String TopologyName = "F:/zyx/Topology/cost239.csv";
		int DemandNum = 1;
		ParameterTransfer pt = new ParameterTransfer();
		file_out_put file_io = new file_out_put();
		MainOfAulixiaryRegenetor mm = new MainOfAulixiaryRegenetor();
		ArrayList<NodePair> RadomNodepairlist = new ArrayList<NodePair>();

		Layer MixLayer_base = new Layer(null, 0, null, null);
		MixLayer_base.readTopology(TopologyName);
		MixLayer_base.generateNodepairs();

		DemandRadom dr = new DemandRadom();
		RadomNodepairlist = dr.NodePairRadom(DemandNum, MixLayer_base);// 随机产生指定个数的节点对
		dr.TrafficNumRadom(RadomNodepairlist); // 随机产生节点对业务

		// 以下可以读取表格中的业务
		// RadomNodepairlist=rd.readDemand(iplayer_base,
		// "f:\\zyx\\USNETTraffic.csv");
		// ReadDemand rd=new ReadDemand();
		// RadomNodepairlist=rd.readDemand(iplayer_base,
		// "D:\\ZYX\\cost239Traffic.csv");

		/*
		 * 设置threshold循环
		 */
		for (float threshold = (float) 0; threshold <= 1.05; threshold = (float) (threshold + 0.1)) {
			double bestResult = 100000;
			int bestshuffle = 1000, NumOfIPreg = 0, NumofOEOreg = 0;
			int bestSingleshuffle = 0, bestAllshuffle = 0;
			int MinSlotofAllShuffleOnSingleLink = 10000;
			int MinSlotofAllShuffleofAllLink = 10000;

			for (int shuffle = 0; shuffle < 50; shuffle++) {// 打乱次序100次
				ArrayList<WorkandProtectRoute> wprlist = new ArrayList<>();
				double TotalWorkCost = 0, TotalProCost = 0;
				pt.setNumOfTransponder(0);
				pt.setcost_of_tranp(0);
				
				file_io.filewrite2(OutFileName, " ");
				file_io.filewrite2(FinalResultFile, " ");
				file_io.filewrite2(OutFileName, "threshold=" + threshold);
				file_io.filewrite2(FinalResultFile, "threshold=" + threshold);
				file_io.filewrite2(OutFileName, "shuffle=" + shuffle);
				file_io.filewrite2(FinalResultFile, "shuffle=" + shuffle);
				

				Collections.shuffle(RadomNodepairlist);// 打乱产生的业务100次
				for (NodePair nodepair : RadomNodepairlist) {
					System.out.println("节点对  " + nodepair.getName() + "  流量：" + nodepair.getTrafficdemand());
					file_io.filewrite2(FinalResultFile,"节点对  " + nodepair.getName() + "  流量：" + nodepair.getTrafficdemand());
				}

				// 产生的节点对之间的容量(int)(Math.random()*(2*Constant.AVER_DEMAND-20));
				// ArrayList<WorkandProtectRoute> wprlist = new ArrayList<>();
				ArrayList<NodePair> SmallNodePairList = new ArrayList<NodePair>();

				Layer MixLayer = new Layer("mixlayer", 0, null, null);
				MixLayer.readTopology(TopologyName);
				MixLayer.generateNodepairs();
				mm.NodepairListset(MixLayer, RadomNodepairlist);// 在IP层设置nodepairList
				ArrayList<NodePair> demandlist = mm.getDemandList(MixLayer, RadomNodepairlist);// 使随机节点对位于IP层
				mm.init(MixLayer,pt);

				for (int n = 0; n < demandlist.size(); n++) {
					NodePair nodepair = demandlist.get(n);
					file_io.filewrite2(OutFileName, "");
					file_io.filewrite2(OutFileName, "");
					if (nodepair.getTrafficdemand() < 50) {// 业务量低于50G的业务不建立通道
						SmallNodePairList.add(nodepair);
						continue;
					}
					file_io.filewrite2(OutFileName,"正在操作的节点对： " + nodepair.getName() + "  他的流量需求是： " + nodepair.getTrafficdemand());
					file_io.filewrite2(OutFileName, "Total numberof transponder " + pt.getNumOfTransponder());
				
					mm.mainMethod(nodepair, MixLayer, pt, threshold, wprlist);
				}
				if (SmallNodePairList != null && SmallNodePairList.size() != 0) {
					for (NodePair smallnodepair : SmallNodePairList) {
						file_io.filewrite2(OutFileName, "");
						file_io.filewrite2(OutFileName, "");
						file_io.filewrite2(OutFileName, "正在操作的节点对： " + smallnodepair.getName() + "  他的流量需求是： "
								+ smallnodepair.getTrafficdemand());
						file_io.filewrite2(OutFileName, "Total numberof transponder " + pt.getNumOfTransponder());
						mm.mainMethod(smallnodepair, MixLayer, pt, threshold,wprlist);
					}
				}
			}
		}
		// 输出结果

	}

	private  void init(Layer MixLayer,ParameterTransfer ptoftransp) {
		HashMap<String,Link> LinkList=MixLayer.getLinklist();
	     Iterator<String> iter1=LinkList.keySet().iterator();
	     while(iter1.hasNext()){
	    	 Link link=(Link)(LinkList.get(iter1.next()));
	    	 link.setnature_IPorOP(Constant.NATURE_OP);
	    	 link.setRestcapacity(0);
	     }
	     ptoftransp.setNumOfLink(MixLayer.getLinklist().size());
	     
		
	}

	public void mainMethod(NodePair nodepair, Layer MixLayer, ParameterTransfer ptoftransp, float threshold,ArrayList<WorkandProtectRoute> wprlist) throws IOException {
		
		WorkRouteStab wrs=new WorkRouteStab();
		wrs.WorkRouteStab(nodepair, MixLayer, wprlist, ptoftransp, threshold);

	}

	public void NodepairListset(Layer ipLayer, ArrayList<NodePair> nodepairlist) {
		// 将随机产生的节点对的容量设置到iplayer对应的节点对里面
		HashMap<String, NodePair> IPnodePairList = new HashMap<String, NodePair>();
		HashMap<String, NodePair> map3 = ipLayer.getNodepairlist();
		Iterator<String> iter3 = map3.keySet().iterator();
		while (iter3.hasNext()) {
			NodePair NodePair = (NodePair) (map3.get(iter3.next()));
			for (int n = 0; n < nodepairlist.size(); n++) {
				NodePair nodePairinList = nodepairlist.get(n);
				if (nodePairinList.getName().equals(NodePair.getName())) {
					NodePair.setTrafficdemand(nodePairinList.getTrafficdemand());
					IPnodePairList.put(NodePair.getName(), NodePair);
					break;
				}
			}
		}
		ipLayer.setNodepairlist(IPnodePairList);
	}

	public ArrayList<NodePair> getDemandList(Layer ipLayer, ArrayList<NodePair> RadomNodepairlist) {
		ArrayList<NodePair> demandList = new ArrayList<NodePair>();

		ArrayList<NodePair> NewFormatnodePairList = new ArrayList<NodePair>();
		HashMap<String, NodePair> nodepairlist = ipLayer.getNodepairlist();
		Iterator<String> iter1 = nodepairlist.keySet().iterator();
		while (iter1.hasNext()) {
			NodePair nodepair = (NodePair) (nodepairlist.get(iter1.next()));
			NewFormatnodePairList.add(nodepair);
		}
		for (NodePair np : RadomNodepairlist) {
			for (NodePair nodepairInIP : NewFormatnodePairList) {
				if (np.getName().equals(nodepairInIP.getName())) {
					demandList.add(nodepairInIP);
					break;
				}
			}
		}
		return demandList;
	}

	public ArrayList<Integer> spectrumallocationOneRoute(Boolean routeflag, LinearRoute route, ArrayList<Link> linklist,int slotnum) {
		//true 是route false是linklist
		ArrayList<Link> linklistOnroute = new ArrayList<Link>();
		if (routeflag) {
			linklistOnroute = route.getLinklist();
		} else {
			linklistOnroute = linklist;
		}
		for (Link link : linklistOnroute) {
			link.getSlotsindex().clear();

			for (int start = 0; start < link.getSlotsarray().size() - slotnum; start++) {
				int flag = 0;
				for (int num = start; num < slotnum + start; num++) {// 分配的FS必须是连续的
					if (link.getSlotsarray().get(num).getoccupiedreqlist().size() != 0) {// 该波长已经被占用
						flag = 1;
						break;
					}
				}
				if (flag == 0) {
					link.getSlotsindex().add(start);// 查找可用slot的起点
				}
			}
		} // 以上所有的link分配完

		Link firstlink = linklistOnroute.get(0);
		ArrayList<Integer> sameindex = new ArrayList<Integer>();
		sameindex.clear();

		for (int s = 0; s < firstlink.getSlotsindex().size(); s++) {
			int index = firstlink.getSlotsindex().get(s);
			int flag = 1;

			for (Link otherlink : linklistOnroute) {
				if (otherlink.getName().equals(firstlink.getName()))
					continue;
				if (!otherlink.getSlotsindex().contains(index)) {
					flag = 0;
					break;
				}
			}
			if (flag == 1) {
				sameindex.add(index); // 挑选出该路径上所有link共同的slot start数
			}
		}
		return sameindex;
	}
}
