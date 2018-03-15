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
		RadomNodepairlist = dr.NodePairRadom(DemandNum, MixLayer_base);// �������ָ�������Ľڵ��
		dr.TrafficNumRadom(RadomNodepairlist); // ��������ڵ��ҵ��

		// ���¿��Զ�ȡ����е�ҵ��
		// RadomNodepairlist=rd.readDemand(iplayer_base,
		// "f:\\zyx\\USNETTraffic.csv");
		// ReadDemand rd=new ReadDemand();
		// RadomNodepairlist=rd.readDemand(iplayer_base,
		// "D:\\ZYX\\cost239Traffic.csv");

		/*
		 * ����thresholdѭ��
		 */
		for (float threshold = (float) 0; threshold <= 1.05; threshold = (float) (threshold + 0.1)) {
			double bestResult = 100000;
			int bestshuffle = 1000, NumOfIPreg = 0, NumofOEOreg = 0;
			int bestSingleshuffle = 0, bestAllshuffle = 0;
			int MinSlotofAllShuffleOnSingleLink = 10000;
			int MinSlotofAllShuffleofAllLink = 10000;

			for (int shuffle = 0; shuffle < 50; shuffle++) {// ���Ҵ���100��
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
				

				Collections.shuffle(RadomNodepairlist);// ���Ҳ�����ҵ��100��
				for (NodePair nodepair : RadomNodepairlist) {
					System.out.println("�ڵ��  " + nodepair.getName() + "  ������" + nodepair.getTrafficdemand());
					file_io.filewrite2(FinalResultFile,"�ڵ��  " + nodepair.getName() + "  ������" + nodepair.getTrafficdemand());
				}

				// �����Ľڵ��֮�������(int)(Math.random()*(2*Constant.AVER_DEMAND-20));
				// ArrayList<WorkandProtectRoute> wprlist = new ArrayList<>();
				ArrayList<NodePair> SmallNodePairList = new ArrayList<NodePair>();

				Layer MixLayer = new Layer("mixlayer", 0, null, null);
				MixLayer.readTopology(TopologyName);
				MixLayer.generateNodepairs();
				mm.NodepairListset(MixLayer, RadomNodepairlist);// ��IP������nodepairList
				ArrayList<NodePair> demandlist = mm.getDemandList(MixLayer, RadomNodepairlist);// ʹ����ڵ��λ��IP��
				mm.init(MixLayer,pt);

				for (int n = 0; n < demandlist.size(); n++) {
					NodePair nodepair = demandlist.get(n);
					file_io.filewrite2(OutFileName, "");
					file_io.filewrite2(OutFileName, "");
					if (nodepair.getTrafficdemand() < 50) {// ҵ��������50G��ҵ�񲻽���ͨ��
						SmallNodePairList.add(nodepair);
						continue;
					}
					file_io.filewrite2(OutFileName,"���ڲ����Ľڵ�ԣ� " + nodepair.getName() + "  �������������ǣ� " + nodepair.getTrafficdemand());
					file_io.filewrite2(OutFileName, "Total numberof transponder " + pt.getNumOfTransponder());
				
					mm.mainMethod(nodepair, MixLayer, pt, threshold, wprlist);
				}
				if (SmallNodePairList != null && SmallNodePairList.size() != 0) {
					for (NodePair smallnodepair : SmallNodePairList) {
						file_io.filewrite2(OutFileName, "");
						file_io.filewrite2(OutFileName, "");
						file_io.filewrite2(OutFileName, "���ڲ����Ľڵ�ԣ� " + smallnodepair.getName() + "  �������������ǣ� "
								+ smallnodepair.getTrafficdemand());
						file_io.filewrite2(OutFileName, "Total numberof transponder " + pt.getNumOfTransponder());
						mm.mainMethod(smallnodepair, MixLayer, pt, threshold,wprlist);
					}
				}
			}
		}
		// ������

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
		// ����������Ľڵ�Ե��������õ�iplayer��Ӧ�Ľڵ������
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
		//true ��route false��linklist
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
				for (int num = start; num < slotnum + start; num++) {// �����FS������������
					if (link.getSlotsarray().get(num).getoccupiedreqlist().size() != 0) {// �ò����Ѿ���ռ��
						flag = 1;
						break;
					}
				}
				if (flag == 0) {
					link.getSlotsindex().add(start);// ���ҿ���slot�����
				}
			}
		} // �������е�link������

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
				sameindex.add(index); // ��ѡ����·��������link��ͬ��slot start��
			}
		}
		return sameindex;
	}
}
